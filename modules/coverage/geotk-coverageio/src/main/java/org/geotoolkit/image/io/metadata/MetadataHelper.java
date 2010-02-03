/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
import java.text.NumberFormat;
import java.text.FieldPosition;
import java.awt.Rectangle;
import java.awt.geom.Dimension2D;
import java.awt.geom.AffineTransform;
import javax.imageio.IIOParam;
import javax.measure.unit.Unit;
import javax.measure.unit.UnitFormat;

import org.opengis.geometry.DirectPosition;
import org.opengis.coverage.grid.RectifiedGrid;
import org.opengis.referencing.cs.CoordinateSystem;

import org.geotoolkit.math.XMath;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.util.Localized;
import org.geotoolkit.util.Utilities;
import org.geotoolkit.util.NumberRange;
import org.geotoolkit.util.MeasurementRange;
import org.geotoolkit.display.shape.DoubleDimension2D;
import org.geotoolkit.image.io.ImageMetadataException;


/**
 * Utility methods extracting commonly used informations from ISO 19115-2 or ISO 19123 objects.
 * Instances of ISO 19115-2 metadata are typically obtained from {@link SpatialMetadata} objects.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.09
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
    private final Localized owner;

    /**
     * Creates a new metadata helper for the given {@code ImageReader} or {@code ImageWriter}.
     *
     * @param owner The image reader or writer for which we are creating metadata,
     *        or {@code null} if none.
     */
    public MetadataHelper(final Localized owner) {
        this.owner = owner;
    }

    /**
     * Returns the error message from the given resource key and arguments.
     * The key shall be one of the {@link Errors.Key} constants. This is used
     * for formatting the message in {@link ImageMetadataException}.
     */
    private String error(final int key, final Object... arguments) {
        return Errors.getResources(getLocale()).getString(key, arguments);
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
            throw new ImageMetadataException(error(Errors.Keys.MISSING_PARAMETER_$1, name));
        }
    }

    /**
     * Ensures that the given {@code dimension} argument is equal to 2.
     *
     * @param  name      The name of the parameter being verified.
     * @param  index     The index to append to {@code name}, or -1 if none.
     * @param  dimension The dimension which shall be equals to 2.
     * @throws ImageMetadataException If the given dimension is not equals to 2.
     */
    private void ensureTwoDimensional(String name, int index, int dimension) throws ImageMetadataException {
        if (dimension != 2) {
            if (index >= 0) {
                name = name + '[' + index + ']';
            }
            throw new ImageMetadataException(error(Errors.Keys.MISMATCHED_DIMENSION_$3, name, dimension, 2));
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
        if (dimension == null) {
            return null;
        }
        return getValidSampleValues(dimension, dimension.getFillSampleValues());
    }

    /**
     * Returns the range of sample values defined in the given {@code SampleDimension} object.
     * This method performs the same work than {@link #getValidSampleValues(SampleDimension)},
     * except that the fill sample values are given explicitly. Note that the fill sample values
     * is not an ISO 19115-2 attribute.
     *
     * @param  dimension The object from which to extract the range.
     * @param  fillSampleValues The no-data values, or {@code null} if none.
     * @return The range of sample values, or {@code null}.
     */
    public NumberRange<?> getValidSampleValues(final SampleDimension dimension, final double[] fillSampleValues) {
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
        if (fillSampleValues != null) {
            isMinInclusive = inclusive(minimum, fillSampleValues);
            isMaxInclusive = inclusive(maximum, fillSampleValues);
        }
        if (geophysics) {
            Double n = dimension.getScaleFactor();
            final double scale = (n != null) ? n : 1;
            n = dimension.getOffset();
            final double offset = (n != null) ? n : 0;
            if (scale != 1 || offset != 0) {
                if (minimum != null) minimum = minimum * scale + offset;
                if (maximum != null) maximum = maximum * scale + offset;
            }
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
     * Creates an affine transform from the {@linkplain RectifiedGrid#getOrigin() origin} and
     * {@linkplain RectifiedGrid#getOffsetVectors() offset vectors} of the given domain. If
     * the {@code param} parameter is non-null, then the affine transform is scaled and
     * translated according the subsampling, source and destination regions specified.
     * <p>
     * Note that the returned transform may maps pixel corner or pixel center, depending on the
     * value returned by {@link org.opengis.metadata.spatial.Georectified#getPointInPixel()}.
     * It is caller responsability to make the necessary adjustements (tip:
     * {@link org.geotoolkit.metadata.iso.spatial.PixelTranslation} may be useful).
     *
     * @param  domain The domain from which to extract the origin and offset vectors.
     * @param  param Optional Image I/O parameters, or {@code null} if none.
     * @return The affine transform extracted from the given domain.
     * @throws ImageMetadataException If a mandatory attribute is missing from the given domain,
     *         or is not two dimensional.
     */
    public AffineTransform getAffineTransform(final RectifiedGrid domain, final IIOParam param)
            throws ImageMetadataException
    {
        final DirectPosition origin = domain.getOrigin();
        ensureMetadataExists("origin", -1, origin);
        ensureTwoDimensional("origin", -1, origin.getDimension());
        final List<double[]> vectors = domain.getOffsetVectors();
        ensureMetadataExists("OffsetVectors", -1, vectors);
        if (vectors.isEmpty()) {
            throw new ImageMetadataException(error(Errors.Keys.MISSING_PARAMETER_VALUE_$1, "OffsetVectors"));
        }
        ensureTwoDimensional("OffsetVectors", -1, vectors.size());
        final double matrix[] = new double[6];
        for (int i=0; i<=1; i++) {
            final double[] v = vectors.get(i);
            ensureMetadataExists("OffsetVector", i, v);
            ensureTwoDimensional("OffsetVector", i, v.length);
            matrix[i  ] = v[0]; // 0:ScaleX | 1:ShearY
            matrix[i+2] = v[1]; // 2:ShearX | 3:ScaleY
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
    public String getCellDimensionAsText(final RectifiedGrid domain, final CoordinateSystem cs) {
        final List<double[]> offsetVectors = domain.getOffsetVectors();
        if (offsetVectors == null) {
            return null;
        }
        /*
         * Get the pixel sizes, and verify if they are the same for all axes.
         */
        final double[] sizes = new double[offsetVectors.size()];
        for (int i=0; i<sizes.length; i++) {
            sizes[i] = adjustForRoundingError(XMath.magnitude(offsetVectors.get(i)));
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
                if (!Utilities.equals(units[i-1], units[i])) {
                    sameUnits = false;
                    break;
                }
            }
        }
        final Unit<?> commonUnit = (sameUnits && units != null && units.length != 0) ? units[0] : null;
        /*
         * Now format the string.
         */
        final StringBuffer  buffer = new StringBuffer();
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
        nf.setMaximumFractionDigits(6);
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
     *   <li>If the result of the above step is {@linkplain XMath#roundIfAlmostInteger almost
     *       an integer}, then round it, divide by 360 and return the result.</li>
     *   <li>Otherwise return the given value unchanged (we do not return the result of
     *       multiplication followed by a division, in order to avoid additional rounding
     *       error).</li>
     * </ul>
     *
     * @param  value The value that we want to adjust.
     * @return The adjusted value, or the given value unchanged if no adjustement were found.
     *
     * @see XMath#roundIfAlmostInteger(double, int)
     * @see org.geotoolkit.referencing.operation.matrix.XAffineTransform#round(AffineTransform, double)
     */
    public double adjustForRoundingError(final double value) {
        final double c1 = value * 360;
        final double c2 = XMath.roundIfAlmostInteger(c1, 16);
        // The above threshold (16) has been determined empirically from IFREMER data.
        return (c1 != c2) ? c2/360 : value;
    }

    /**
     * Returns the locale used by this helper, or {@code null} for the default locale. This is
     * used for text formatting in {@link #getCellDimensionAsText getCellDimensionAsText} and
     * for error messages.
     *
     * @since 3.09
     */
    @Override
    public Locale getLocale() {
        return (owner != null) ? owner.getLocale() : null;
    }
}
