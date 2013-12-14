/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.image.io.metadata;

import java.awt.Point;
import java.util.List;
import java.util.Locale;
import java.util.ArrayList;
import java.util.Objects;
import java.text.NumberFormat;
import java.text.FieldPosition;
import java.awt.Rectangle;
import java.awt.geom.Dimension2D;
import java.awt.geom.AffineTransform;
import javax.imageio.IIOParam;
import javax.measure.unit.Unit;
import javax.measure.unit.UnitFormat;

import org.opengis.geometry.DirectPosition;
import org.opengis.util.FactoryException;
import org.opengis.util.InternationalString;
import org.opengis.coverage.grid.RectifiedGrid;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransform1D;
import org.opengis.referencing.operation.MathTransformFactory;
import org.opengis.metadata.content.TransferFunctionType;

import org.apache.sis.math.MathFunctions;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.Vocabulary;
import org.apache.sis.util.Localized;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.measure.MeasurementRange;
import org.apache.sis.internal.util.UnmodifiableArrayList;
import org.geotoolkit.internal.InternalUtilities;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.coverage.Category;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.display.shape.DoubleDimension2D;
import org.geotoolkit.image.io.ImageMetadataException;
import org.geotoolkit.referencing.operation.matrix.XMatrix;
import org.geotoolkit.referencing.operation.matrix.Matrix2;
import org.geotoolkit.referencing.operation.matrix.Matrices;
import org.geotoolkit.referencing.operation.matrix.XAffineTransform;

import static org.apache.sis.util.collection.Containers.isNullOrEmpty;


/**
 * Utility methods extracting commonly used informations from ISO 19115-2 or ISO 19123 objects.
 * Instances of ISO 19115-2 metadata are typically obtained from {@link SpatialMetadata} objects.
 * See the <a href="SpatialMetadataFormat.html#default-formats">format description</a> for a
 * description of the expected metadata tree.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
 *
 * @since 3.07
 * @module
 */
public class MetadataHelper implements Localized {
    /**
     * The default instance.
     */
    public static final MetadataHelper INSTANCE = new MetadataHelper(null);

    /**
     * Small tolerance factor for comparisons of floating point numbers.
     */
    private static final double EPS = 1E-10;

    /**
     * The image reader or writer for which we are creating metadata, or {@code null} if none.
     */
    private final org.apache.sis.util.Localized owner;

    /**
     * The math transform factory, fetched only if needed.
     */
    private transient MathTransformFactory mtFactory;

    /**
     * A math transform which, when used, is likely to be reused again.
     */
    private transient MathTransform exponential;

    /**
     * Creates a new metadata helper for the given {@code ImageReader} or {@code ImageWriter}.
     *
     * @param owner The image reader or writer for which we are creating metadata,
     *        or {@code null} if none.
     */
    public MetadataHelper(final org.apache.sis.util.Localized owner) {
        this.owner = owner;
    }

    /**
     * Returns the locale used by this helper, or {@code null} for the default locale. This is
     * used for formatting text in methods like {@link #formatCellDimension formatCellDimension},
     * and for localization of error messages when an exception is thrown.
     *
     * @return The locale, or {@code null} if unspecified.
     *
     * @since 3.09
     */
    @Override
    public Locale getLocale() {
        return (owner != null) ? owner.getLocale() : null;
    }

    /**
     * Returns the math transform factory.
     */
    private MathTransformFactory getMathTransformFactory() {
        if (mtFactory == null) {
            mtFactory = FactoryFinder.getMathTransformFactory(null);
        }
        return mtFactory;
    }

    /**
     * Returns the error message from the given resource key and arguments.
     * The key shall be one of the {@link Errors.Key} constants. This is used
     * for formatting the message in {@link ImageMetadataException}.
     */
    private String error(final short key, final Object... arguments) {
        return Errors.getResources(getLocale()).getString(key, arguments);
    }

    /**
     * Ensures that the given vector is non-null and non-empty.
     */
    private void ensureVectorsExist(final List<?> vectors) throws ImageMetadataException {
        ensureMetadataExists("OffsetVectors", -1, vectors);
        if (vectors.isEmpty()) {
            throw new ImageMetadataException(error(Errors.Keys.NO_PARAMETER_VALUE_1, "OffsetVectors"));
        }
    }

    /**
     * Ensures that the given value is non-null. The value is presumed extracted
     * from a metadata attribute.
     *
     * @param  name  The name of the metadata attribute.
     * @param  index The index to append to {@code name}, or -1 if none.
     * @param  value The value extracted from the metadata.
     * @throws ImageMetadataException If the given value is null.
     */
    private void ensureMetadataExists(String name, int index, Object value) throws ImageMetadataException {
        if (value == null) {
            if (index >= 0) {
                name = name + '[' + index + ']';
            }
            throw new ImageMetadataException(error(Errors.Keys.NO_PARAMETER_1, name));
        }
    }

    /**
     * Ensures that the given {@code dimension} argument is equal to the expected value.
     *
     * @param  name      The name of the parameter being verified.
     * @param  index     The index to append to {@code name}, or -1 if none.
     * @param  dimension The dimension which shall be equals to {@code expected}.
     * @param  expected  The expected dimension value (often 2).
     * @throws ImageMetadataException If the given dimension is not equals to {@code expected}.
     */
    private void ensureDimensionMatch(String name, int index, int dimension, final int expected)
            throws ImageMetadataException
    {
        if (dimension != expected) {
            if (index >= 0) {
                name = name + '[' + index + ']';
            }
            throw new ImageMetadataException(error(Errors.Keys.MISMATCHED_DIMENSION_3,
                    name, dimension, expected));
        }
    }

    /**
     * Returns the range of geophysics values defined in the given {@code SampleDimension} object.
     * This method tries to build the range from the
     * {@linkplain SampleDimension#getMinValue() minimum value},
     * {@linkplain SampleDimension#getMaxValue() maximum value},
     * {@linkplain SampleDimension#getScaleFactor() scale factor},
     * {@linkplain SampleDimension#getOffset() offset} and the
     * {@linkplain SampleDimension#getFillSampleValues() fill sample values} metadata attributes.
     *
     * @param  dimension The object from which to extract the range.
     * @return The range of geophysics values, or {@code null}.
     *
     * @since 3.08
     */
    public NumberRange<?> getValidValues(final SampleDimension dimension) {
        if (dimension == null) {
            return null;
        }
        return getSampleValues(dimension, dimension.getFillSampleValues(), true);
    }

    /**
     * Returns the range of sample values defined in the given {@code SampleDimension} object. This
     * method first looks at the value returned by {@link SampleDimension#getValidSampleValues()}.
     * If the later returns {@code null}, then this method tries to build the range from the
     * {@linkplain SampleDimension#getMinValue() minimum value},
     * {@linkplain SampleDimension#getMaxValue() maximum value} and the
     * {@linkplain SampleDimension#getFillSampleValues() fill sample values} metadata attributes.
     * <p>
     * The fill sample values are used in order to determine if the minimum and maximum values
     * are inclusive or exclusive: if an extremum is equals to a fill sample value, then it is
     * considered exclusive. Otherwise it is considered inclusive.
     *
     * @param  dimension The object from which to extract the range.
     * @return The range of sample values, or {@code null}.
     */
    public NumberRange<?> getValidSampleValues(final SampleDimension dimension) {
        NumberRange<?> range = null;
        if (dimension != null) {
            range = dimension.getValidSampleValues();
            if (range == null) {
                range = getSampleValues(dimension, dimension.getFillSampleValues(), false);
            }
        }
        return range;
    }

    /**
     * Returns the range of sample values defined in the given {@code SampleDimension} object.
     * This method performs the same work than {@link #getValidSampleValues(SampleDimension)},
     * except that the band index and fill sample values are given explicitly.
     * Note that the fill sample values is not an ISO 19115-2 attribute.
     * <p>
     * This method is invoked by {@link #getSampleValues(SampleDimension, double[], boolean)}.
     * Subclasses can override it for forcing the usage of a different range of sample values.
     *
     * @param  bandIndex Index of the band for which to get the valid sample values.
     * @param  dimension The object from which to extract the range.
     * @param  fillSampleValues The no-data values, or {@code null} if none.
     * @return The range of sample values, or {@code null}.
     */
    public NumberRange<?> getValidSampleValues(final int bandIndex,
            final SampleDimension dimension, final double[] fillSampleValues)
    {
        NumberRange<?> range = null;
        if (dimension != null) {
            range = dimension.getValidSampleValues();
            if (range == null) {
                range = getSampleValues(dimension, fillSampleValues, false);
            }
        }
        return range;
    }

    /**
     * Calculates the range of values. This is the range of geophysics values if
     * {@code geophysics} if {@code true}, or the range of sample values otherwise.
     */
    private NumberRange<?> getSampleValues(final SampleDimension dimension,
            final double[] fillSampleValues, final boolean geophysics)
    {
        Double minimum = dimension.getMinValue();
        Double maximum = dimension.getMaxValue();
        boolean isMinInclusive = true;
        boolean isMaxInclusive = true;
        if (!geophysics || fillSampleValues != null) {
            Double sampleMin = minimum;
            Double sampleMax = maximum;
            Double n;
            final double scale  = ((n = dimension.getScaleFactor()) != null) ? n : 1;
            final double offset = ((n = dimension.getOffset())      != null) ? n : 0;
            if (scale != 1 || offset != 0) {
                if (sampleMin != null) sampleMin = (sampleMin - offset) / scale;
                if (sampleMax != null) sampleMax = (sampleMax - offset) / scale;
            }
            if (fillSampleValues != null) {
                isMinInclusive = inclusive(sampleMin, fillSampleValues);
                isMaxInclusive = inclusive(sampleMax, fillSampleValues);
            }
            if (!geophysics) {
                minimum = sampleMin;
                maximum = sampleMax;
            }
        }
        if (geophysics) {
            final Unit<?> units = dimension.getUnits();
            if (units != null) {
                return MeasurementRange.createBestFit(minimum, isMinInclusive, maximum, isMaxInclusive, units);
            }
        }
        return NumberRange.createBestFit(minimum, isMinInclusive, maximum, isMaxInclusive);
    }

    /**
     * Returns {@code true} if the given {@code nodataValues} array does <strong>not</strong>
     * contains the given value. In such case, the value can be considered inclusive.
     */
    private static boolean inclusive(final Number value, final double[] nodataValues) {
        if (value != null) {
            final double n = value.doubleValue();
            for (final double c : nodataValues) {
                if (c == n) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Creates the <cite>Grid to CRS</cite> conversion from the {@linkplain RectifiedGrid#getOrigin()
     * origin} and {@linkplain RectifiedGrid#getOffsetVectors() offset vectors} of the given domain.
     * <p>
     * This method is similar to {@link #getAffineTransform(RectifiedGrid, IIOParam)}, except that
     * it is not restricted to a two-dimensional conversion and does not take an {@link IIOParam}
     * object in account.
     *
     * @param  domain The domain from which to extract the origin and offset vectors.
     * @return The <cite>Grid to CRS</cite> conversion extracted from the given domain.
     * @throws ImageMetadataException If a mandatory attribute is missing from the given domain.
     *
     * @since 3.09
     */
    public MathTransform getGridToCRS(final RectifiedGrid domain) throws ImageMetadataException {
        final DirectPosition origin = domain.getOrigin();
        ensureMetadataExists("origin", -1, origin);
        final List<double[]> vectors = domain.getOffsetVectors();
        ensureVectorsExist(vectors);
        final int dimSource = vectors.size();        // Number of dimensions in the grid.
        final int dimTarget = origin.getDimension(); // Number of dimensions in the CRS.
        final XMatrix matrix = Matrices.create(dimTarget + 1, dimSource + 1);
        if (dimTarget < dimSource) {
            matrix.setElement(dimTarget, dimTarget, 0);
        }
        matrix.setElement(dimTarget, dimSource, 1);
        for (int i=0; i<dimSource; i++) {
            final double[] v = vectors.get(i);
            ensureMetadataExists("OffsetVector", i, v);
            ensureDimensionMatch("OffsetVector", i, v.length, dimTarget);
            for (int j=0; j<dimTarget; j++) {
                matrix.setElement(j, i, v[j]);
            }
        }
        for (int j=0; j<dimTarget; j++) {
            matrix.setElement(j, dimSource, origin.getOrdinate(j));
        }
        final MathTransformFactory mtFactory = getMathTransformFactory();
        try {
            return mtFactory.createAffineTransform(matrix);
        } catch (FactoryException e) {
            throw new ImageMetadataException(e);
        }
    }

    /**
     * Creates an affine transform from the {@linkplain RectifiedGrid#getOrigin() origin} and
     * {@linkplain RectifiedGrid#getOffsetVectors() offset vectors} of the given domain. If
     * the {@code param} parameter is non-null, then the affine transform is scaled and
     * translated according the subsampling, source and destination regions specified.
     * <p>
     * Note that the returned transform may maps pixel corner or pixel center, depending on the
     * value returned by {@link org.opengis.metadata.spatial.Georectified#getPointInPixel()}.
     * It is caller responsibility to make the necessary adjustments (tip:
     * {@link org.geotoolkit.metadata.iso.spatial.PixelTranslation} may be useful).
     *
     * @param  domain The domain from which to extract the origin and offset vectors.
     * @param  param Optional Image I/O parameters, or {@code null} if none.
     * @return The affine transform extracted from the given domain.
     * @throws ImageMetadataException If a mandatory attribute is missing from the given domain,
     *         or if this method can not extract the two first dimensions from the domain.
     */
    public AffineTransform getAffineTransform(final RectifiedGrid domain, final IIOParam param)
            throws ImageMetadataException
    {
        final List<double[]> vectors = domain.getOffsetVectors();
        ensureVectorsExist(vectors);
        final int dimSource = vectors.size();
        if (dimSource < 2) {
            ensureDimensionMatch("OffsetVectors", -1, dimSource, 2); // Exception always thrown.
        }
        final DirectPosition origin = domain.getOrigin();
        ensureMetadataExists("origin", -1, origin);
        final int dimTarget = origin.getDimension();
        if (dimTarget < 2 || !isSeparable(vectors)) {
            ensureDimensionMatch("origin", -1, dimTarget, 2);
        }
        final double[] matrix = new double[6];
        for (int i=0; i<=1; i++) {
            final double[] v = vectors.get(i);
            ensureMetadataExists("OffsetVector", i, v);
            ensureDimensionMatch("OffsetVector", i, v.length, dimTarget);
            System.arraycopy(v, 0, matrix, i*2, 2);
            matrix[i+4] = origin.getOrdinate(i);
        }
        for (int i=0; i<matrix.length; i++) {
            matrix[i] = adjustForRoundingError(matrix[i]);
        }
        final AffineTransform tr = new AffineTransform(matrix);
        if (param != null) {
            final Rectangle source = param.getSourceRegion();
            final Point     target = param.getDestinationOffset();
            if (target != null) {
                tr.translate(-target.x, -target.y);
            }
            tr.scale(param.getSourceXSubsampling(),
                     param.getSourceYSubsampling());
            if (source != null) {
                tr.translate(source.x + param.getSubsamplingXOffset(),
                             source.y + param.getSubsamplingYOffset());
            }
        }
        return tr;
    }

    /**
     * Returns {@code true} if the two first dimensions in the given array of vectors
     * are separable from all other dimensions. This is used in order to determine if
     * we can extract a two dimensional affine transform from the domain.
     */
    private static boolean isSeparable(final List<double[]> vectors) {
        for (int i=vectors.size(); --i>=0;) {
            final double[] vector = vectors.get(i);
            if (vector != null) {
                int lower, upper;
                if (i >= 2) {
                    lower = 0;
                    upper = 2;
                } else {
                    lower = 2;
                    upper = vector.length;
                }
                while (lower < upper) {
                    if (vector[lower++] != 0) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Returns the size of pixels, which must be square. The {@code gridToCRS} argument is
     * typically the output of {@link #getAffineTransform getAffineTransform}. This method
     * checks if the given transform complies with the following conditions:
     * <p>
     * <ul>
     *   <li>The {@link AffineTransform#getScaleX() scaleX} coefficient must be
     *       greater than zero.</li>
     *   <li>The {@link AffineTransform#getScaleY() scaleY} coefficient must be
     *       the negative value of {@code scaleX}, because the Y axis is assumed
     *       reversed.</li>
     *   <li>The {@link AffineTransform#getShearX() shearX} and {@link AffineTransform#getShearY()
     *       shearY} coefficients must be zero.</li>
     * </ul>
     * <p>
     * If all those conditions are meet, then {@code scaleX} is returned. Otherwise an
     * exception is thrown. This behavior is convenient for code like the
     * {@linkplain org.geotoolkit.image.io.plugin.AsciiGridWriter ASCII Grid writer},
     * which require square pixels as of format specification.
     *
     * @param  gridToCRS The affine transform from which to extract the cell size.
     * @return The cell size as a positive and non-null value.
     * @throws ImageMetadataException If the affine transform does not comply with the above cited conditions.
     */
    public double getCellSize(final AffineTransform gridToCRS) throws ImageMetadataException {
        final double size = gridToCRS.getScaleX();
        if (size > 0) {
            final double tol = size * EPS;
            if (Math.abs(gridToCRS.getScaleY() + size) <= tol &&
                Math.abs(gridToCRS.getShearX()) <= tol &&
                Math.abs(gridToCRS.getShearY()) <= tol)
            {
                return size;
            }
        }
        throw new ImageMetadataException(error(Errors.Keys.PIXELS_NOT_SQUARE_OR_ROTATED_IMAGE));
    }

    /**
     * Returns the dimension of pixels, or {@code null} if not applicable. The {@code gridToCRS}
     * argument is typically the output of {@link #getAffineTransform getAffineTransform}. This
     * method checks if the given transform complies with the following conditions:
     * <p>
     * <ul>
     *   <li>The {@link AffineTransform#getShearX() shearX}
     *       and {@link AffineTransform#getShearY() shearY} coefficients are zero.</li>
     * </ul>
     * <p>
     * If this condition is meet, then {@code scaleX} and <strong>the negative value</strong>
     * of {@code scaleY} (because the Y axis is assumed reversed) are returned in a new
     * {@link Dimension2D} object. Otherwise {@code null} is returned. This behavior is
     * convenient for code like the {@linkplain org.geotoolkit.image.io.plugin.AsciiGridWriter
     * ASCII Grid writer}, which require square pixels unless some extensions are enabled for
     * rectangular pixel.
     *
     * @param  gridToCRS The affine transform from which to extract the cell size.
     * @return The cell dimension, or {@code null} if the image is rotated.
     */
    public Dimension2D getCellDimension(final AffineTransform gridToCRS) {
        final double dx =  gridToCRS.getScaleX();
        final double dy = -gridToCRS.getScaleY();
        final double tol = Math.max(Math.abs(dx), Math.abs(dy)) * EPS;
        if (Math.abs(gridToCRS.getShearX()) <= tol &&
            Math.abs(gridToCRS.getShearY()) <= tol)
        {
            return new DoubleDimension2D(dx, dy);
        }
        return null;
    }

    /**
     * Returns the dimension of pixels as a text, or {@code null} if none. This method computes
     * the dimension from the {@linkplain RectifiedGrid#getOffsetVectors() offset vectors} and
     * appends the axis units, if any.
     *
     * @param  domain The domain from which to compute the cell dimensions.
     * @param  cs The "real world" coordinate system, or {@code null} if unknown.
     * @return A text representation of the cell dimension, or {@code null} if there is no
     *         offset vectors.
     *
     * @since 3.09
     */
    public String formatCellDimension(final RectifiedGrid domain, final CoordinateSystem cs) {
        final List<double[]> offsetVectors = domain.getOffsetVectors();
        if (offsetVectors == null) {
            return null;
        }
        /*
         * Get the pixel sizes, and verify if they are the same for all axes.
         */
        double minSize = Double.POSITIVE_INFINITY;
        final double[] sizes = new double[offsetVectors.size()];
        for (int i=0; i<sizes.length; i++) {
            sizes[i] = adjustForRoundingError(MathFunctions.magnitude(offsetVectors.get(i)));
            final double as = Math.abs(sizes[i]);
            if (as < minSize) {
                minSize = as;
            }
        }
        boolean sameSize = true;
        for (int i=1; i<sizes.length; i++) {
            if (sizes[i-1] != sizes[i]) {
                sameSize = false;
                break;
            }
        }
        /*
         * Get the units, and verify if they are the same for all axes.
         */
        boolean sameUnits = true;
        Unit<?>[] units = null;
        if (cs != null) {
            units = new Unit<?>[Math.min(sizes.length, cs.getDimension())];
            for (int i=0; i<units.length; i++) {
                units[i] = cs.getAxis(i).getUnit();
            }
            for (int i=1; i<units.length; i++) {
                if (!Objects.equals(units[i-1], units[i])) {
                    sameUnits = false;
                    break;
                }
            }
        }
        final Unit<?> commonUnit = (sameUnits && units != null && units.length != 0) ? units[0] : null;
        /*
         * Now format the string.
         */
        final StringBuffer  buffer = new StringBuffer(24);
        final Locale        locale = getLocale();
        final FieldPosition pos    = new FieldPosition(0);
        final NumberFormat  nf;
        final UnitFormat    uf;
        if (locale != null) {
            nf = NumberFormat.getInstance(locale);
            uf = UnitFormat  .getInstance(locale);
        } else {
            nf = NumberFormat.getInstance();
            uf = UnitFormat  .getInstance();
        }
        InternalUtilities.configure(nf, minSize, 9);
        if (sameSize && sameUnits) {
            if (sizes.length != 0) {
                nf.format(sizes[0], buffer, pos);
            }
        } else {
            if (commonUnit != null) {
                buffer.append('(');
            }
            boolean needsSeparator = false;
            for (int i=0; i<sizes.length; i++) {
                if (needsSeparator) {
                    buffer.append(" × ");
                }
                needsSeparator = true;
                nf.format(sizes[i], buffer, pos);
                if (!sameUnits && units != null && i<units.length) {
                    final Unit<?> unit = units[i];
                    if (unit != null) {
                        uf.format(unit, buffer.append(' '), pos);
                    }
                }
            }
        }
        if (commonUnit != null) {
            if (!sameSize) {
                buffer.append(')');
            }
            uf.format(commonUnit, buffer.append(' '), pos);
        }
        return buffer.toString();
    }

    /**
     * Converts the given {@link SampleDimension} instances to {@link GridSampleDimension} instances.
     * For each input sample dimension, this method creates a qualitative {@linkplain Category
     * category} for each {@linkplain SampleDimension#getFillSampleValues() fill values} (if any)
     * and a single quantitative category for the {@linkplain SampleDimension#getValidSampleValues()
     * range of sample values}.
     * <p>
     * The {@code sampleDimensions} argument is typically obtained by the following method call:
     *
     * {@preformat java
     *     SpatialMetadata metadata = ...
     *     sampleDimensions = metadata.getListForType(SampleDimension.class);
     * }
     *
     * @param  sampleDimensions The sample dimensions from Image I/O metadata, or {@code null}.
     * @return The {@link GridSampleDimension}s, or {@code null} if the given list was null or empty.
     * @throws ImageMetadataException If this method can not create the grid sample dimensions.
     *
     * @since 3.13
     */
    public List<GridSampleDimension> getGridSampleDimensions(
            final List<? extends SampleDimension> sampleDimensions) throws ImageMetadataException
    {
        if (isNullOrEmpty(sampleDimensions)) {
            return null;
        }
        /*
         * Now convert the SampleDimension instances to GridSampleDimension instances.
         * For each sample dimension, we create a qualitative category for each fill
         * values (if any) and a single quantitative category for the range of sample
         * values.
         */
        boolean allGeophysics = true;
        InternationalString untitled = null; // To be created only if needed.
        final List<Category> categories = new ArrayList<>();
        final GridSampleDimension[] bands = new GridSampleDimension[sampleDimensions.size()];
        boolean hasSampleDimensions = false;
        for (int i=0; i<bands.length; i++) {
            final SampleDimension sd = sampleDimensions.get(i);
            if (sd != null) {
                /*
                 * Get a name for the sample dimensions. This name will be given both to the
                 * GridSampleDimension object and to the single qualitative Category. If no
                 * name can be found, "Untitled" will be used.
                 */
                InternationalString dimensionName = sd.getDescriptor();
                if (dimensionName == null) {
                    if (untitled == null) {
                        untitled = Vocabulary.formatInternational(Vocabulary.Keys.UNTITLED);
                    }
                    dimensionName = untitled;
                } else {
                    hasSampleDimensions = true;
                }
                /*
                 * Create a qualitative category for each fill value (usually only one).
                 * Those categories need to be created before the quantitative category,
                 * because we will need this information in order to detect invalid range
                 * of geophysics values.
                 */
                final double[] fillValues = sd.getFillSampleValues();
                if (fillValues != null) {
                    final CharSequence name = Category.NODATA.getName();
                    for (final double fv : fillValues) {
                        final int ifv = (int) fv;
                        final Category c;
                        if (ifv == fv) {
                            c = new Category(name, null, ifv);
                        } else {
                            c = new Category(name, null, fv);
                        }
                        categories.add(c);
                    }
                }
                /*
                 * Create a quantitative category for the range of valid sample values.
                 * If there is no offset and scale factor, then the values are assumed
                 * geophysics values (the scale is set to 1 and the offset to 0).
                 */
                final NumberRange<?> range = getValidSampleValues(i, sd, fillValues);
                if (range != null) {
                    final Double scale  = sd.getScaleFactor();
                    final Double offset = sd.getOffset();
                    final boolean isGeophysics = (scale == null && offset == null);
                    if (!isGeophysics || !overlap(categories, range)) {
                        final TransferFunctionType type = sd.getTransferFunctionType();
                        final MathTransformFactory mtFactory = getMathTransformFactory();
                        MathTransform tr;
                        try {
                            /*
                             * NOTE: The formulas in this block must be consistent with the
                             *       formulas in CategoryTable.getCategories(String).
                             */
                            tr = mtFactory.createAffineTransform(new Matrix2(
                                    (scale  != null) ? adjustForRoundingError(scale)  : 1,
                                    (offset != null) ? adjustForRoundingError(offset) : 0, 0, 1));
                            if (type != null && !type.equals(TransferFunctionType.LINEAR)) {
                                if (type.equals(TransferFunctionType.EXPONENTIAL)) {
                                    if (exponential == null) {
                                        final ParameterValueGroup param = mtFactory.getDefaultParameters("Exponential");
                                        param.parameter("base").setValue(10d); // Must be a 'double'
                                        exponential = mtFactory.createParameterizedTransform(param);
                                    }
                                    tr = mtFactory.createConcatenatedTransform(tr, exponential);
                                } else {
                                    throw new ImageMetadataException(Errors.getResources(getLocale())
                                            .getString(Errors.Keys.UNSUPPORTED_OPERATION_1, type));
                                }
                            }
                        } catch (FactoryException e) {
                            throw new ImageMetadataException(e);
                        }
                        categories.add(new Category(dimensionName, null, range, (MathTransform1D) tr));
                    }
                    allGeophysics &= isGeophysics;
                }
                /*
                 * Create the GridSampleDimension instance.
                 */
                final Category[] array;
                if (categories.isEmpty()) {
                    array = null;
                } else {
                    array = categories.toArray(new Category[categories.size()]);
                    hasSampleDimensions = true;
                }
                final Unit<?> unit = sd.getUnits();
                if (unit != null) {
                    hasSampleDimensions = true;
                }
                final GridSampleDimension band;
                try {
                    band = new GridSampleDimension(dimensionName, array, unit);
                } catch (IllegalArgumentException e) {
                    throw new ImageMetadataException(e);
                }
                bands[i] = band;
                categories.clear();
            }
        }
        /*
         * At this point, we have all the sample dimensions. If the samples seem to be
         * already geophysics values, declare the bands as such.
         */
        if (hasSampleDimensions) {
            for (int i=0; i<bands.length; i++) {
                bands[i] = bands[i].geophysics(allGeophysics);
            }
            return UnmodifiableArrayList.wrap(bands);
        }
        return null;
    }

    /**
     * Returns {@code true} if the given range overlaps at least one category. This method is
     * invoked when the image metadata declares a range of sample value, but does not declare
     * any offset and scale factor. Sometime (e.g. in some NetCDF files encoded in a way not
     * compliant with CF-convention), the range is actually garbage data that we should ignore.
     * In some other cases (e.g. in ASCII-Grid), the range is still valid.
     * <p>
     * As an heuristic rule, we will consider the range as garbage data if it overlaps any
     * previously defined categories. Attempt to create a {@code GridSampleDimension} with
     * such range would thrown an exception anyway.
     */
    private static boolean overlap(final List<Category> categories, final NumberRange<?> range) {
        for (final Category category : categories) {
            if (range.intersectsAny(category.getRange())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Works around the rounding errors found in some metadata numbers. We usually don't try to
     * "fix" rounding errors, but {@linkplain AffineTransform affine transform} coefficients are
     * an exception because they have a very deep impact on performance, especially the scale
     * factors: integer scales are often processed by optimized loops much faster than the loops
     * for fractional scales, and operations like matrix multiplications are more likely to produce
     * special cases like the {@linkplain AffineTransform#isIdentity() identity transform} when the
     * initial matrix coefficients have an exact IEEE 754 representation.
     * <p>
     * This method processes as below:
     * <p>
     * <ul>
     *   <li>First, the given value is multiplied by 360. We choose the 360 value arbitrarily
     *       because it is a multiple of many commonly used factors: 2, 3, 4, 5, 6, 10, 60 and
     *       others.</li>
     *   <li>If the result of the above step is almost an integer,
     *       then round it, divide by 360 and return the result.</li>
     *   <li>Otherwise return the given value unchanged (we do not return the result of
     *       multiplication followed by a division, in order to avoid additional rounding
     *       error).</li>
     * </ul>
     *
     * @param  value The value that we want to adjust.
     * @return The adjusted value, or the given value unchanged if no adjustment were found.
     *
     * @see XAffineTransform#roundIfAlmostInteger(AffineTransform, double)
     */
    public double adjustForRoundingError(final double value) {
        return InternalUtilities.adjustForRoundingError(value, 360, 16);
        // The above threshold (16) has been determined empirically from IFREMER data.
    }
}
