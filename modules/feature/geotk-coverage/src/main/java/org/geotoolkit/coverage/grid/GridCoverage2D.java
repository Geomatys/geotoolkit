/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.coverage.grid;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.measure.Unit;
import javax.media.jai.Interpolation;
import javax.media.jai.OperationNode;
import javax.media.jai.PlanarImage;
import javax.media.jai.remote.SerializableRenderedImage;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.geometry.Envelope2D;
import org.apache.sis.util.Classes;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.internal.coverage.CoverageUtilities;
import org.geotoolkit.lang.Debug;
import org.geotoolkit.resources.Errors;
import org.opengis.coverage.CannotEvaluateException;
import org.opengis.coverage.PointOutsideCoverageException;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.TransformException;
import org.apache.sis.internal.referencing.PositionTransformer;


/**
 * Basic access to grid data values backed by a two-dimensional
 * {@linkplain RenderedImage rendered image}. Each band in an image is represented as a
 * {@linkplain SampleDimension sample dimension}.
 *
 * {@section Two-dimensional slice in a <var>n</var>-dimensional space}
 * Grid coverages are usually two-dimensional. However, {@linkplain #getEnvelope their envelope}
 * may have more than two dimensions.
 *
 * <blockquote><font size="-1"><b>Example:</b> a remote sensing image may be valid only over
 * some time range (the time of satellite pass over the observed area). Envelopes for such grid
 * coverage can have three dimensions: the two usual ones (horizontal extent along <var>x</var>
 * and <var>y</var>), and a third one for start time and end time (time extent along <var>t</var>).
 * </font></blockquote>
 *
 * However, the {@linkplain GridExtent grid envelope} for all extra-dimension
 * <strong>must</strong> have a {@linkplain GridExtent#getSize span} not greater than 1.
 * In other words, a {@code GridCoverage2D} can be a slice in a 3 dimensional grid coverage. Each
 * slice can have an arbitrary width and height (like any two-dimensional images), but only 1
 * voxel depth (a "voxel" is a three-dimensional pixel).
 *
 * {@section Serialization}
 * Because it is serializable, {@code GridCoverage2D} can be included as method argument or as
 * return type in <cite>Remote Method Invocation</cite> (RMI). However, the pixel data are not
 * sent during serialization. Instead, the image data are transmitted "on-demand" using socket
 * communications. This mechanism is implemented using JAI {@link SerializableRenderedImage}
 * class.
 *
 * @author Martin Desruisseaux (IRD)
 *
 * @see GridGeometry2D
 * @see SampleDimension
 * @see GridCoverageBuilder
 */
public class GridCoverage2D extends GridCoverage {

    /**
     * Whatever default grid envelope computation should be performed on transform
     * relative to pixel center or relative to pixel corner.  The former is OGC
     * convention while the later is Java convention.
     */
    private static final PixelInCell PIXEL_IN_CELL = PixelInCell.CELL_CORNER;

    /**
     * The raster data.
     */
    protected final transient RenderedImage image;

    /**
     * The serialized image, as an instance of {@link SerializableRenderedImage}.
     * This image will be created only when first needed during serialization.
     */
    private RenderedImage serializedImage;

    /**
     * The views returned by {@link #views}. Constructed when first needed.
     * Note that some views may appear in the {@link #sources} list.
     */
    private transient ViewsManager views;

    /**
     * Used for transforming a direct position from arbitrary to internal CRS.
     * Will be created only when first needed. Note that the target CRS should
     * be two-dimensional, not the {@link #crs} value.
     */
    private transient PositionTransformer arbitraryToInternal;

    /**
     * The preferred encoding to use for serialization using the {@code writeObject} method,
     * or {@code null} for the default encoding. This value is set by {@link GridCoverageFactory}
     * according the hints provided to the factory.
     */
    transient String tileEncoding;

    /**
     * Constructs a new grid coverage with the same parameter than the specified
     * coverage. This constructor is useful when creating a coverage with
     * identical data, but in which some method has been overridden in order to
     * process data differently (e.g. interpolating them).
     *
     * @param name The name for this coverage, or {@code null} for the same than {@code coverage}.
     * @param coverage The source grid coverage.
     */
    GridCoverage2D(final CharSequence name, final GridCoverage2D coverage) {
        super(name, coverage);
        image            = coverage.image;
        tileEncoding     = coverage.tileEncoding;
        // Do not share the views, since subclasses will create different instances.
    }

    /**
     * Constructs a grid coverage with the specified {@linkplain GridGeometry2D grid geometry} and
     * {@linkplain SampleDimension sample dimensions}. The {@linkplain Envelope envelope}
     * (including the {@linkplain CoordinateReferenceSystem coordinate reference system}) is
     * inferred from the grid geometry.
     * <p>
     * This constructor accepts an optional map of user properties. This map is
     * useful for storing user-values like statistics. Keys shall be {@link String} or
     * {@link javax.media.jai.util.CaselessStringKey} instances, while values can be any
     * {@link Object}.
     * <p>
     * Note that {@link GridCoverageBuilder} provides more convenient ways to create
     * {@code GridCoverage2D} instances. But all those convenience methods will ultimately
     * delegate to this constructor.
     *
     * @param name
     *          The grid coverage name.
     * @param image
     *          The image.
     * @param gridGeometry
     *          The grid geometry (must contains an {@linkplain GridGeometry2D#getEnvelope envelope}
     *          with its {@linkplain GridGeometry2D#getCoordinateReferenceSystem coordinate reference
     *          system} and a "{@linkplain GridGeometry2D#getGridToCRS grid to CRS}" transform).
     * @param bands
     *          Sample dimensions for each image band, or {@code null} for default sample dimensions.
     *          If non-null, then this array length must matches the number of bands in {@code image}.
     * @param sources
     *          The sources for this grid coverage, or {@code null} if none.
     * @param properties
     *          The set of properties for this coverage, or {@code null} none.
     * @param hints
     *          An optional set of hints, or {@code null} if none.
     * @throws IllegalArgumentException
     *          If the number of bands differs from the number of sample dimensions.
     *
     * @since 2.5
     */
    public GridCoverage2D(final CharSequence             name,
                          final RenderedImage           image,
                                GridGeometry2D   gridGeometry,
                          final SampleDimension[]       bands,
                          final GridCoverage[]        sources,
                          final Map<?,?>           properties,
                          final Hints                   hints)
            throws IllegalArgumentException
    {
        super(name, fillGridGeometry(image, gridGeometry), fillSampleDimensions(name, image, bands), sources, properties);
        this.image = image;
        if (hints != null) {
            tileEncoding = (String) hints.get(Hints.TILE_ENCODING);
        }
    }

    /**
     * Wraps the user-supplied sample dimensions into instances of RenderedSampleDimension. This
     * process will creates default sample dimensions if the user supplied null values. Those
     * default will be inferred from image type (integers, floats...) and range of values. If
     * an inconsistency is found in user-supplied sample dimensions, an IllegalArgumentException
     * is thrown.
     */
    private static Collection<SampleDimension> fillSampleDimensions(final CharSequence name, final RenderedImage image, final SampleDimension[] bands) {
        RenderedSampleDimension[] sampleDimensions = new RenderedSampleDimension[image.getSampleModel().getNumBands()];
        RenderedSampleDimension.create(name, image, bands, sampleDimensions);
        final List<SampleDimension> dims = new ArrayList<>(sampleDimensions.length);
        for (RenderedSampleDimension rsd : sampleDimensions) {
            dims.add(rsd.dimension);
        }
        return dims;
    }

    /**
     * Computes the grid envelope if it was not explicitly provided. The range will be inferred
     * from the image size, if needed. The envelope computation (if needed) requires a valid
     * 'gridToCRS' transform in the GridGeometry object. In any case, the envelope must be
     * non-empty and its dimension must matches the coordinate reference system's dimension.
     */
    private static GridGeometry2D fillGridGeometry(final RenderedImage image, GridGeometry2D gridGeometry) {
        final CoordinateReferenceSystem crs = gridGeometry.getCoordinateReferenceSystem();
        final int dimension = crs.getCoordinateSystem().getDimension();
        if (!gridGeometry.isDefined(GridGeometry2D.EXTENT)) {
            final long[] low = new long[dimension];
            final long[] high = new long[dimension];
            Arrays.fill(low, 0);
            Arrays.fill(high, 1);
            low[0] = image.getMinX();
            low[1] = image.getMinY();
            high[0] = image.getWidth();
            high[1] = image.getHeight();

            final GridExtent r = new GridExtent(null, low, high, false);
            if (gridGeometry.isDefined(GridGeometry2D.GRID_TO_CRS)) {
                gridGeometry = new GridGeometry2D(r, PIXEL_IN_CELL,
                        gridGeometry.getGridToCRS(PIXEL_IN_CELL), crs);
            } else {
                /*
                 * If the math transform was not explicitly specified by the user, then it will be
                 * computed from the envelope. In this case, some heuristic rules are used in order
                 * to decide if we should reverse some axis directions or swap axis.
                 */
                gridGeometry = new GridGeometry2D(r, gridGeometry.getEnvelope());
            }
        } else {
            /*
             * Makes sure that the 'gridToCRS' transform is defined.
             * An exception will be thrown otherwise.
             */
            gridGeometry.getGridToCRS(PixelInCell.CELL_CENTER);
        }
        assert gridGeometry.isDefined(GridGeometry2D.CRS        |
                                      GridGeometry2D.ENVELOPE   |
                                      GridGeometry2D.EXTENT |
                                      GridGeometry2D.GRID_TO_CRS);
        /*
         * Last argument checks. The image size must be consistent with the grid envelope
         * and the georeferenced envelope must be non-empty.
         */
        final String error = checkConsistency(image, gridGeometry);
        if (error != null) {
            throw new IllegalArgumentException(error);
        }
        if (dimension <= Math.max(gridGeometry.axisDimensionX,  gridGeometry.axisDimensionY)
                             || !(gridGeometry.getEnvelope().getSpan(gridGeometry.axisDimensionX) > 0)
                             || !(gridGeometry.getEnvelope().getSpan(gridGeometry.axisDimensionY) > 0))
        {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.EmptyEnvelope2d));
        }
        return gridGeometry;
    }

    /**
     * Checks if the bounding box of the specified image is consistent with the specified
     * grid geometry. If an inconsistency has been found, then an error string is returned.
     * This string will be typically used as a message in an exception to be thrown.
     * <p>
     * Note that a successful check at construction time may fails later if the image is part
     * of a JAI chain (i.e. is a {@link javax.media.jai.RenderedOp}) and its bounds has been
     * edited (i.e the image node as been re-rendered). Since {@code GridCoverage2D} are immutable
     * by design, we are not allowed to propagate the image change here. The {@link #getGridGeometry}
     * method will thrown an {@link IllegalStateException} in this case.
     */
    private static String checkConsistency(final RenderedImage image, final GridGeometry2D grid) {
        final GridExtent range = grid.getExtent();
        final int dimension = range.getDimension();
        for (int i=0; i<dimension; i++) {
            final long min, span;
            final Object label;
            if (i == grid.gridDimensionX) {
                min   = image.getMinX();
                span  = image.getWidth();
                label = "\"X\"";
            } else if (i == grid.gridDimensionY) {
                min   = image.getMinY();
                span  = image.getHeight();
                label = "\"Y\"";
            } else {
                min   = range.getLow(i);
                span  = Math.min(Math.max(range.getSize(i), 0), 1);
                label = Integer.valueOf(i);
            }
            long n;
            if ((n = range.getLow(i)) != min) {
                return Errors.format(Errors.Keys.IllegalGridEnvelope_3, label, min, min + span)
                        + " The rendered image declares a low value of " + min + " while the grid extent declares " + n + ".";
            }
            if ((n = range.getSize(i)) != span) {
                return Errors.format(Errors.Keys.IllegalGridEnvelope_3, label, min, min + span)
                        + " The rendered image declares a span of " + span + " while the grid extent declares " + n + ".";
            }
        }
        return null;
    }

    @Override
    public GridGeometry2D getGridGeometry() {
        return (GridGeometry2D) super.getGridGeometry();
    }

    /**
     * Returns the two-dimensional bounding box for the coverage domain in coordinate reference
     * system coordinates. If the coverage envelope has more than two dimensions, only the
     * dimensions used in the underlying rendered image are returned.
     *
     * @return The two-dimensional bounding box.
     */
    public Envelope2D getEnvelope2D() {
        return getGridGeometry().getEnvelope2D();
    }

    /**
     * Returns the two-dimensional part of this grid coverage CRS. If the
     * {@linkplain #getCoordinateReferenceSystem complete CRS} is two-dimensional, then this
     * method returns the same CRS. Otherwise it returns a CRS for the two first axis having
     * a {@linkplain GridExtent#getSize span} greater than 1 in the grid envelope. Note that
     * those axis are guaranteed to appears in the same order than in the complete CRS.
     *
     * @return The two-dimensional part of the grid coverage CRS.
     *
     * @see #getCoordinateReferenceSystem
     */
    public CoordinateReferenceSystem getCoordinateReferenceSystem2D() {
        return getGridGeometry().getCoordinateReferenceSystem2D();
    }

    /**
     * Returns the interpolation used for all {@code evaluate(...)} methods.
     * The default implementation returns {@link javax.media.jai.InterpolationNearest}.
     *
     * @return The interpolation.
     */
    public Interpolation getInterpolation() {
        return Interpolation.getInstance(Interpolation.INTERP_NEAREST);
    }

    /**
     * Returns the value vector for a given point in the coverage.
     * A value for each sample dimension is included in the vector.
     */
    @Override
    public Object evaluate(final DirectPosition point) throws CannotEvaluateException {
        final int dataType = image.getSampleModel().getDataType();
        switch (dataType) {
            case DataBuffer.TYPE_BYTE:   return evaluate(point, (byte  []) null);
            case DataBuffer.TYPE_SHORT:  // Fall through
            case DataBuffer.TYPE_USHORT: // Fall through
            case DataBuffer.TYPE_INT:    return evaluate(point, (int   []) null);
            case DataBuffer.TYPE_FLOAT:  return evaluate(point, (float []) null);
            case DataBuffer.TYPE_DOUBLE: return evaluate(point, (double[]) null);
            default: throw new CannotEvaluateException();
        }
    }

    /**
     * Returns a sequence of byte values for a given point in the coverage.
     *
     * @param  coord The coordinate point where to evaluate.
     * @param  dest  An array in which to store values, or {@code null}.
     * @return An array containing values.
     * @throws CannotEvaluateException if the values can't be computed at the specified coordinate.
     *         More specifically, {@link PointOutsideCoverageException} is thrown if the evaluation
     *         failed because the input point has invalid coordinates.
     */
    @Override
    public byte[] evaluate(final DirectPosition coord, byte[] dest)
            throws CannotEvaluateException
    {
        final int[] array = evaluate(coord, (int[]) null);
        if (dest == null) {
            dest = new byte[array.length];
        }
        for (int i=0; i<array.length; i++) {
            dest[i] = (byte) array[i];
        }
        return dest;
    }

    /**
     * Returns a sequence of integer values for a given point in the coverage.
     *
     * @param  coord The coordinate point where to evaluate.
     * @param  dest  An array in which to store values, or {@code null}.
     * @return An array containing values.
     * @throws CannotEvaluateException if the values can't be computed at the specified coordinate.
     *         More specifically, {@link PointOutsideCoverageException} is thrown if the evaluation
     *         failed because the input point has invalid coordinates.
     */
    @Override
    public int[] evaluate(final DirectPosition coord, final int[] dest)
            throws CannotEvaluateException
    {
        return evaluate(toPoint2D(coord), dest);
    }

    /**
     * Returns a sequence of float values for a given point in the coverage.
     *
     * @param  coord The coordinate point where to evaluate.
     * @param  dest  An array in which to store values, or {@code null}.
     * @return An array containing values.
     * @throws CannotEvaluateException if the values can't be computed at the specified coordinate.
     *         More specifically, {@link PointOutsideCoverageException} is thrown if the evaluation
     *         failed because the input point has invalid coordinates.
     */
    @Override
    public float[] evaluate(final DirectPosition coord, final float[] dest)
            throws CannotEvaluateException
    {
        return evaluate(toPoint2D(coord), dest);
    }

    /**
     * Returns a sequence of double values for a given point in the coverage.
     *
     * @param  coord The coordinate point where to evaluate.
     * @param  dest  An array in which to store values, or {@code null}.
     * @return An array containing values.
     * @throws CannotEvaluateException if the values can't be computed at the specified coordinate.
     *         More specifically, {@link PointOutsideCoverageException} is thrown if the evaluation
     *         failed because the input point has invalid coordinates.
     */
    @Override
    public double[] evaluate(final DirectPosition coord, final double[] dest)
            throws CannotEvaluateException
    {
        return evaluate(toPoint2D(coord), dest);
    }

    /**
     * Converts the specified point into a two-dimensional one.
     *
     * @param  point The point to transform into a {@link Point2D} object.
     * @return The specified point as a {@link Point2D} object.
     * @throws CannotEvaluateException if a reprojection was required and failed.
     * @throws MismatchedDimensionException if the point doesn't have the expected dimension.
     */
    private Point2D toPoint2D(final DirectPosition point)
            throws CannotEvaluateException, MismatchedDimensionException
    {
        /*
         * If the point contains a CRS, transforms the point on the fly to this coverage CRS.
         * Note that we transform directly to the 2D CRS, so we don't need to look at the grid
         * geometry for interpreting the result.
         */
        final CoordinateReferenceSystem sourceCRS = point.getCoordinateReferenceSystem();
        if (sourceCRS != null) {
            synchronized (this) {
                if (arbitraryToInternal == null) {
                    final CoordinateReferenceSystem targetCRS = getCoordinateReferenceSystem2D();
                    arbitraryToInternal = new PositionTransformer(targetCRS, targetCRS, null);
                }
                final DirectPosition p;
                try {
                    p = arbitraryToInternal.transform(point);
                } catch (TransformException exception) {
                    throw new CannotEvaluateException(formatEvaluateError(point, false), exception);
                }
                return new Point2D.Double(p.getOrdinate(0), p.getOrdinate(1));
            }
        }
        /*
         * If the point did not contains any CRS, take only the axis specified by the grid
         * geometry and copy in a new Point2D instance.
         */
        final int actual   = point.getDimension();
        final int expected = getCoordinateReferenceSystem().getCoordinateSystem().getDimension();
        if (actual != expected) {
            throw new MismatchedDimensionException(Errors.format(
                    Errors.Keys.MismatchedDimension_2, actual, expected));
        }
        if (point instanceof Point2D) {
            return (Point2D) point;
        }
        final GridGeometry2D gridGeometry = getGridGeometry();
        assert gridGeometry.axisDimensionX < gridGeometry.axisDimensionY;
        return new Point2D.Double(point.getOrdinate(gridGeometry.axisDimensionX),
                                  point.getOrdinate(gridGeometry.axisDimensionY));
    }

    /**
     * Returns a sequence of integer values for a given two-dimensional point in the coverage.
     *
     * @param  coord The coordinate point where to evaluate.
     * @param  dest  An array in which to store values, or {@code null}.
     * @return An array containing values.
     * @throws CannotEvaluateException if the values can't be computed at the specified coordinate.
     *         More specifically, {@link PointOutsideCoverageException} is thrown if the evaluation
     *         failed because the input point has invalid coordinates.
     */
    public int[] evaluate(final Point2D coord, final int[] dest)
            throws CannotEvaluateException
    {
        final GridGeometry2D gridGeometry = getGridGeometry();
        final Point2D pixel = gridGeometry.inverseTransform(coord);
        final double fx = pixel.getX();
        final double fy = pixel.getY();
        if (!Double.isNaN(fx) && !Double.isNaN(fy)) {
            final int x = (int) Math.round(fx);
            final int y = (int) Math.round(fy);
            if (getBounds().contains(x,y)) { // getBounds() returns a cached instance.
                return image.getTile(XToTileX(x), YToTileY(y)).getPixel(x, y, dest);
            }
        }
        throw new PointOutsideCoverageException(formatEvaluateError(coord, true));
    }

    /**
     * Returns a sequence of float values for a given two-dimensional point in the coverage.
     *
     * @param  coord The coordinate point where to evaluate.
     * @param  dest  An array in which to store values, or {@code null}.
     * @return An array containing values.
     * @throws CannotEvaluateException if the values can't be computed at the specified coordinate.
     *         More specifically, {@link PointOutsideCoverageException} is thrown if the evaluation
     *         failed because the input point has invalid coordinates.
     */
    public float[] evaluate(final Point2D coord, final float[] dest)
            throws CannotEvaluateException
    {
        final GridGeometry2D gridGeometry = getGridGeometry();
        final Point2D pixel = gridGeometry.inverseTransform(coord);
        final double fx = pixel.getX();
        final double fy = pixel.getY();
        if (!Double.isNaN(fx) && !Double.isNaN(fy)) {
            final int x = (int) Math.round(fx);
            final int y = (int) Math.round(fy);
            if (getBounds().contains(x,y)) { // getBounds() returns a cached instance.
                return image.getTile(XToTileX(x), YToTileY(y)).getPixel(x, y, dest);
            }
        }
        throw new PointOutsideCoverageException(formatEvaluateError(coord, true));
    }

    /**
     * Returns a sequence of double values for a given two-dimensional point in the coverage.
     *
     * @param  coord The coordinate point where to evaluate.
     * @param  dest  An array in which to store values, or {@code null}.
     * @return An array containing values.
     * @throws CannotEvaluateException if the values can't be computed at the specified coordinate.
     *         More specifically, {@link PointOutsideCoverageException} is thrown if the evaluation
     *         failed because the input point has invalid coordinates.
     */
    public double[] evaluate(final Point2D coord, final double[] dest)
            throws CannotEvaluateException
    {
        final GridGeometry2D gridGeometry = getGridGeometry();
        final Point2D pixel = gridGeometry.inverseTransform(coord);
        final double fx = pixel.getX();
        final double fy = pixel.getY();
        if (!Double.isNaN(fx) && !Double.isNaN(fy)) {
            final int x = (int) Math.round(fx);
            final int y = (int) Math.round(fy);
            if (getBounds().contains(x,y)) { // getBounds() returns a cached instance.
                return image.getTile(XToTileX(x), YToTileY(y)).getPixel(x, y, dest);
            }
        }
        throw new PointOutsideCoverageException(formatEvaluateError(coord, true));
    }

    /**
     * Returns a debug string for the specified coordinate. This method produces a
     * string with pixel coordinates and pixel values for all bands (with geophysics
     * values or category name in parenthesis). Example for a 1-banded image:
     *
     * {@preformat text
     *     (1171,1566)=[196 (29.6 °C)]
     * }
     *
     * @param  coord The coordinate point where to evaluate.
     * @return A string with pixel coordinates and pixel values at the specified location,
     *         or {@code null} if {@code coord} is outside coverage.
     */
    @Debug
    public synchronized String getDebugString(final DirectPosition coord) {
        final GridGeometry2D gridGeometry = getGridGeometry();
        Point2D pixel = toPoint2D(coord);
        pixel         = gridGeometry.inverseTransform(pixel);
        final int   x = (int) Math.round(pixel.getX());
        final int   y = (int) Math.round(pixel.getY());
        if (getBounds().contains(x,y)) { // getBounds() returns a cached instance.
            final int  numBands = image.getSampleModel().getNumBands();
            final Raster raster = image.getTile(XToTileX(x), YToTileY(y));
            final int  datatype = image.getSampleModel().getDataType();
            final StringBuilder buffer = new StringBuilder();
            buffer.append('(').append(x).append(',').append(y).append(")=[");
            for (int band=0; band<numBands; band++) {
                if (band != 0) {
                    buffer.append(";\u00A0");
                }
                final double sample = raster.getSampleDouble(x, y, band);
                switch (datatype) {
                    case DataBuffer.TYPE_DOUBLE: buffer.append(        sample); break;
                    case DataBuffer.TYPE_FLOAT : buffer.append((float) sample); break;
                    default                    : buffer.append(  (int) sample); break;
                }
                final String formatted = null; // TODO sampleDimensions[band].getLabel(sample, null);
                if (formatted != null) {
                    buffer.append("\u00A0(").append(formatted).append(')');
                }
            }
            return buffer.append(']').toString();
        }
        return null;
    }

    /**
     * Returns grid data as a rendered image.
     *
     * @return The grid data as a rendered image.
     */
    public RenderedImage getRenderedImage() {
        return image;
    }

    @Override
    public RenderedImage render(GridExtent sliceExtent) throws CannotEvaluateException {
        if (sliceExtent != null) {
            final GridGeometry2D gridGeometry = getGridGeometry();
            int[] dims = sliceExtent.getSubspaceDimensions(2);
            if (dims[0] != gridGeometry.axisDimensionX && dims[1] != gridGeometry.axisDimensionY) {
                throw new CannotEvaluateException("Unsupported axis");
            }
            final GridExtent extent = getGridGeometry().getExtent();
            if (sliceExtent.getSize(dims[0]) != extent.getSize(dims[0]) || sliceExtent.getSize(dims[1]) != extent.getSize(dims[1])) {
                throw new CannotEvaluateException("Slice size must match coverage extent");
            }
        }
        return getRenderedImage();
    }

    /**
     * Returns 2D view of this grid coverage as a renderable image.
     * This method allows inter-operability with Java2D.
     *
     * @param  xAxis Dimension to use for <var>x</var> axis.
     * @param  yAxis Dimension to use for <var>y</var> axis.
     * @return A 2D view of this grid coverage as a renderable image.
     */
    @Override
    public RenderableImage getRenderableImage(final int xAxis, final int yAxis) {
        final GridGeometry2D gridGeometry = getGridGeometry();
        if (xAxis == gridGeometry.axisDimensionX  &&  yAxis == gridGeometry.axisDimensionY) {
            return new Renderable();
        } else {
            return super.getRenderableImage(xAxis, yAxis);
        }
    }

    /**
     * Utility method to convert image bounds as {@link java.awt.Rectangle}.
     * @return {@link java.awt.Rectangle} bounds.
     */
    private Rectangle getBounds() {
        return new Rectangle(image.getMinX(), image.getMinY(), image.getWidth(), image.getHeight());
    }

    /**
     * Converts a pixel's X coordinate into a horizontal tile index.
     * @param x pixel x coordinate
     * @return tile x coordinate
     */
    private int XToTileX(int x) {
        int tileWidth = image.getTileWidth();
        x -= image.getTileGridXOffset();
        if (x < 0) {
            x += 1 - tileWidth;
        }
        return x/tileWidth;
    }

    /**
     * Converts a pixel's Y coordinate into a vertical tile index.
     * @param y pixel x coordinate
     * @return tile y coordinate
     */
    private int YToTileY(int y) {
        int tileHeight = image.getTileHeight();
        y -= image.getTileGridYOffset();
        if (y < 0) {
            y += 1 - tileHeight;
        }
        return y/tileHeight;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void show(String title, final int xAxis, final int yAxis) {
        final GridCoverage2D displayable = view(ViewType.RENDERED);
        if (displayable != this) {
            displayable.show(title, xAxis, yAxis);
            return;
        }
        if (title == null || (title = title.trim()).isEmpty()) {
            final StringBuilder buffer = new StringBuilder(String.valueOf(getName()));
            final int visibleBandIndex = CoverageUtilities.getVisibleBand(this);
            final SampleDimension visibleBand = getSampleDimensions().get(visibleBandIndex);
            final Optional<Unit<?>> unit = visibleBand.getUnits();
            buffer.append(" - ").append(String.valueOf(visibleBand.getName()));
            if (unit.isPresent()) {
                buffer.append(" (").append(unit).append(')');
            }
            title = buffer.toString();
        }
        super.show(title, xAxis, yAxis);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void show(final String title) {
        final GridGeometry2D gridGeometry = getGridGeometry();
        show(title, gridGeometry.axisDimensionX, gridGeometry.axisDimensionY);
    }

    /**
     * A view of a {@linkplain GridCoverage2D grid coverage} as a renderable image. Renderable images
     * allow inter-operability with <A HREF="http://java.sun.com/products/java-media/2D/">Java2D</A>
     * for a two-dimensional slice of a grid coverage.
     *
     * @author Martin Desruisseaux (IRD)
     * @version 3.00
     *
     * @see AbstractCoverage#getRenderableImage
     *
     * @since 2.0
     * @module
     *
     * @todo Override {@link #createRendering} and use the affine transform operation.
     *       Also uses the JAI's "Transpose" operation is x and y axis are interchanged.
     */
    protected class Renderable extends GridCoverage.Renderable {
        /**
         * For compatibility during cross-version serialization.
         */
        private static final long serialVersionUID = 4544636336787905450L;

        /**
         * Constructs a renderable image.
         */
        public Renderable() {
            super(getGridGeometry().axisDimensionX, getGridGeometry().axisDimensionY);
        }

        /**
         * Returns a rendered image with a default width and height in pixels.
         *
         * @return A rendered image containing the rendered data
         */
        @Override
        public RenderedImage createDefaultRendering() {
            final GridGeometry2D gridGeometry = getGridGeometry();
            if (xAxis == gridGeometry.axisDimensionX &&
                yAxis == gridGeometry.axisDimensionY)
            {
                return getRenderedImage();
            }
            return super.createDefaultRendering();
        }
    }

    /**
     * Returns a view of the specified type. Valid types are:
     * <ul>
     *   <li><p>
     *     {@link ViewType#GEOPHYSICS GEOPHYSICS}: all sample values are equals to geophysics
     *     ("<cite>real world</cite>") values without the need for any transformation. The
     *     {@linkplain SampleDimension#getSampleToGeophysics sample to geophysics} transform
     *     {@linkplain org.opengis.referencing.operation.MathTransform1D#isIdentity is identity}
     *     for all sample dimensions. "<cite>No data</cite>" values (if any) are expressed as
     *     {@linkplain Float#NaN NaN} numbers. This view is suitable for computation, but usually
     *     not for rendering.
     *   </p></li>
     *   <li><p>
     *     {@link ViewType#PACKED PACKED}: sample values are typically integers. A
     *     {@linkplain SampleDimension#getSampleToGeophysics sample to geophysics} transform may
     *     exists for converting them to "<cite>real world</cite>" values.
     *   </p></li>
     *   <li><p>
     *     {@link ViewType#RENDERED RENDERED}: synonymous of {@code PACKED} for now. Will be
     *     improved in a future version.
     *   </p></li>
     *   <li><p>
     *     {@link ViewType#PHOTOGRAPHIC PHOTOGRAPHIC}: synonymous of {@code RENDERED} for now.
     *     Will be improved in a future version.
     *   </p></li>
     *   <li><p>
     *     {@link ViewType#SAME SAME}: returns {@code this} coverage unchanged.
     *   </p></li>
     * </ul>
     *
     * This method may be understood as applying the JAI's
     * {@linkplain javax.media.jai.operator.PiecewiseDescriptor piecewise} operation with
     * breakpoints specified by the {@link org.geotoolkit.coverage.Category} objects in each
     * sample dimension. However, it is more general in that the transformation specified
     * with each breakpoint doesn't need to be linear. On an implementation note, this method
     * tries to use the first of the following operations which is found applicable:
     * <cite>identity</cite>,
     * {@linkplain javax.media.jai.operator.LookupDescriptor lookup},
     * {@linkplain javax.media.jai.operator.RescaleDescriptor rescale},
     * {@linkplain javax.media.jai.operator.PiecewiseDescriptor piecewise} and in
     * last ressort a more general (but slower) <cite>sample transcoding</cite> algorithm.
     *
     * @param  type The kind of view wanted.
     * @return The grid coverage. Never {@code null}, but may be {@code this}.
     *
     * @see SampleDimension#geophysics
     * @see org.geotoolkit.coverage.Category#geophysics
     * @see javax.media.jai.operator.LookupDescriptor
     * @see javax.media.jai.operator.RescaleDescriptor
     * @see javax.media.jai.operator.PiecewiseDescriptor
     *
     * @since 2.5
     */
    public GridCoverage2D view(final ViewType type) {
        if (type == ViewType.SAME) {
            return this;
        }
        synchronized (this) {
            if (views == null) {
                views = ViewsManager.create(this);
            }
        }
        // Do not synchronize past this point, because ViewsManager.get is already
        // synchronized. We need to rely on ViewsManager locking because the views
        // are shared among many GridCoverage2D instances.
        final Hints hints = null; // We may revisit that later.
        return views.get(this, type, hints);
    }

    @Override
    public GridCoverage2D forConvertedValues(boolean converted) {
        if (converted) {
            return view(ViewType.GEOPHYSICS);
        } else {
            return getNativeView();
        }
    }

    /**
     * Returns the native view to be given to a newly created {@link ViewsManager}.  For
     * {@link GridCoverage2D}, this is always {@code this} because the first coverage to
     * instantiate a {@link ViewsManager} can not be anything else than native, since the
     * views do not exist yet. For {@link Calculator2D} (which is a decorator around an
     * other {@link GridCoverage2D}), we use the native view of its source.
     */
    GridCoverage2D getNativeView() {
        return this;
    }

    /**
     * Invoked (indirectly) by <code>{@linkplain #view view}(type)</code> when the
     * {@linkplain ViewType#PACKED packed}, {@linkplain ViewType#GEOPHYSICS geophysics} or
     * {@linkplain ViewType#PHOTOGRAPHIC photographic} view of this grid coverage needs to
     * be created.
     * <p>
     * This method is defined here for {@link ViewsManager} needs, which invokes it. But it
     * make sense only for {@link Calculator2D}, which override it with protected access.
     * For other subclasses, we do not allow overriding (i.e. we keep this method package-
     * privated) on purpose. See {@link #getViewClass} for the reason.
     */
    GridCoverage2D specialize(final GridCoverage2D view) {
        return view;
    }

    /**
     * Returns the base class of the view returned by {@link #specialize}, or {@code null} if
     * unknown. This method is invoked by {@link ViewsManager#create} in order to determine
     * if a given coverage can share its views with an other coverage. The condition tested
     * by {@link ViewsManager} (namely: coverages have the same image, same grid geometry and
     * same sample dimensions) are sufficient only if the coverages build the views in the same
     * way. The last condition can be guarantee only if we know how {@link #specialize} is
     * implemented. It is safe for non-{@link Calculator2D} classes (because users can not
     * override {@link #specialize} and for final classes like {@link Interpolator2D}, but
     * the later must returns a different class in order to tells {@link ViewsManager} that
     * it does not build the views in the same way.
     */
    Class<? extends GridCoverage2D> getViewClass() {
        return GridCoverage2D.class;
    }

    /**
     * Copies the views from this class into the specified coverage and returns them. The views
     * are actually shared, i.e. views created for one coverage can be used by the other. This
     * method is for internal use by {@link ViewsManager} only.
     */
    final synchronized ViewsManager copyViewsTo(final GridCoverage2D target) {
        if (views == null) {
            views = ViewsManager.create(this);
        }
        if (target.views == null) {
            target.views = views;
        } else if (target.views != views) {
            throw new IllegalStateException(); // As a safety, but should never happen.
        }
        return views;
    }

    /**
     * Returns the set of views that this coverage represents. The same coverage may be used for
     * more than one view. For example a coverage could be valid both as a {@link ViewType#PACKED
     * PACKED} and {@link ViewType#RENDERED RENDERED} view.
     *
     * @return The set of views that this coverage represents.
     *
     * @since 2.5
     */
    public synchronized Set<ViewType> getViewTypes() {
        synchronized (this) {
            if (views == null) {
                views = ViewsManager.create(this);
            }
        }
        return views.viewTypes;
    }

    /**
     * Provides a hint that a coverage will no longer be accessed from a reference in user space.
     * This method {@linkplain PlanarImage#dispose disposes} the {@linkplain #image} only if at
     * least one of the following conditions is true (otherwise this method do nothing):
     * <p>
     * <ul>
     *   <li>{@code force} is {@code true}, <strong>or</strong></li>
     *   <li>The underlying {@linkplain #image} has no {@linkplain PlanarImage#getSinks sinks}
     *       other than the views (geophysics, display, <i>etc.</i>).</li>
     * </ul>
     * <p>
     * This safety check helps to prevent the disposal of an {@linkplain #image} that still
     * used in a JAI operation chain. It doesn't prevent the disposal in every cases however.
     * When unsure about whatever a coverage is still in use or not, it is safer to not invoke
     * this method and rely on the garbage collector instead.
     *
     * @see PlanarImage#dispose
     *
     * @since 2.4
     */
    @Override
    public synchronized boolean dispose(final boolean force) {
        if (views != null) {
            if (views.dispose(force).contains(this)) {
                // The remaining GridCoverage2D include this one,
                // which means that this view has not been disposed.
                return false;
            }
            views = null;
        }
        return super.dispose(force);
    }

    /**
     * Returns a string representation of this grid coverage.
     * This is mostly for debugging purpose and may change in any future version.
     */
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder(super.toString());
        final String lineSeparator = System.lineSeparator();
        buffer.append("\u2514 Image=").append(Classes.getShortClassName(image)).append('[');
        if (image instanceof OperationNode) {
            buffer.append('"').append(((OperationNode) image).getOperationName()).append('"');
        }
        buffer.append(']');
        if (views == null || !Thread.holdsLock(views)) {
            /*
             * We use Thread.holdsLock(views) as a semaphore for avoiding never-ending loop if
             * toString() is invoked from ViewsManager (either by IDE debugger or by 'println'
             * statement). Because ViewsManager is not public, this trick doesn't impact users.
             */
            buffer.append(" as views ").append(getViewTypes());
        }
        return buffer.append(lineSeparator).toString();
    }
}
