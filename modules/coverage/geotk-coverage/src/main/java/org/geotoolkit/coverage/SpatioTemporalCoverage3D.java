/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.coverage;

import java.util.Date;
import java.awt.geom.Point2D;
import java.awt.geom.Dimension2D;
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;

import org.opengis.coverage.Coverage;
import org.opengis.coverage.SampleDimension;
import org.opengis.coverage.CannotEvaluateException;
import org.opengis.coverage.PointOutsideCoverageException;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.metadata.extent.GeographicBoundingBox;
import org.opengis.util.InternationalString;

import org.geotoolkit.lang.Decorator;
import org.geotoolkit.util.DateRange;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageFactory;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.crs.DefaultGeographicCRS;
import org.apache.sis.referencing.crs.DefaultTemporalCRS;
import org.geotoolkit.referencing.operation.MathTransforms;
import org.apache.sis.metadata.iso.extent.DefaultGeographicBoundingBox;
import org.apache.sis.internal.referencing.AxisDirections;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.resources.Errors;


/**
 * Convenience view of an other coverage with <var>x</var>, <var>y</var> and <var>time</var> axis.
 * This class provides {@code evaluate} methods in two versions: the usual one expecting a complete
 * {@linkplain DirectPosition direct position}, and an other one expecting the {@linkplain Point2D
 * spatial position} and the {@linkplain Date date} as separated arguments. This class will detect
 * by itself which dimension is the time axis. It will also tries to uses the {@code Point2D}
 * {@linkplain java.awt.geom.Point2D.Double#x x} value for {@linkplain AxisDirection#EAST east}
 * or west direction, and the {@linkplain java.awt.geom.Point2D.Double#y y} value for
 * {@linkplain AxisDirection#NORTH north} or south direction. The dimension mapping can be
 * examined with the {@link #toSourceDimension} method.
 *
 * {@section Synchronization}
 * This class is not thread safe for performance reasons. If desired, users should create one
 * instance of {@code SpatioTemporalCoverage3D} for each thread.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.1
 * @module
 */
@Decorator(Coverage.class)
public class SpatioTemporalCoverage3D extends AbstractCoverage {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 2885506902340338431L;

    /**
     * The hints for fetching factories.
     */
    private static final Hints HINTS = null;

    /**
     * A set of usual axis directions for <var>x</var> and <var>y</var> values (opposite directions
     * not required). If an ordinate value is oriented toward one of those directions, it will be
     * interpreted as the {@link java.awt.geom.Point2D.Double#x} value if the direction was found at
     * an even index, or as the {@link java.awt.geom.Point2D.Double#y} value if the direction was
     * found at an odd index.
     */
    private static final AxisDirection[] DIRECTIONS = {
        AxisDirection.EAST,             AxisDirection.NORTH,
        AxisDirection.DISPLAY_RIGHT,    AxisDirection.DISPLAY_DOWN,
        AxisDirection.COLUMN_POSITIVE,  AxisDirection.ROW_POSITIVE
    };

    /**
     * The wrapped coverage.
     */
    protected final Coverage coverage;

    /**
     * The temporal coordinate system, as a Geotk implementation in order to gets the
     * {@link DefaultTemporalCRS#toDate} and {@link DefaultTemporalCRS#toValue} methods.
     */
    protected final DefaultTemporalCRS temporalCRS;

    /**
     * The dimension of the temporal coordinate system.
     * All other dimensions are expected to be the spatial ones.
     */
    protected final int temporalDimension;

    /**
     * The dimension for <var>x</var> and <var>y</var> coordinates.
     */
    protected final int xDimension, yDimension;

    /**
     * The geographic bounding box. Will be computed only when first needed.
     */
    private transient GeographicBoundingBox boundingBox;

    /**
     * The direct position to uses for {@code evaluate(...)} methods.
     * This object is cached and reused for performance purpose. However,
     * this caching sacrifies {@code SpatioTemporalCoverage3D} thread safety.
     */
    private final GeneralDirectPosition coordinate;

    /**
     * The grid coverage factory for {@link #getCoverage2D} method.
     * Will be created only when first needed.
     */
    private transient GridCoverageFactory factory;

    /**
     * Constructs a new coverage. The coordinate reference system will be the same than the
     * wrapped coverage, which must be three dimensional. This CRS must have a
     * {@linkplain DefaultTemporalCRS temporal} component.
     *
     * @param name The name for this coverage, or {@code null} for the same than {@code coverage}.
     * @param coverage The source coverage.
     * @throws IllegalArgumentException if the coverage CRS doesn't have a temporal component.
     */
    public SpatioTemporalCoverage3D(final CharSequence name, final Coverage coverage)
            throws IllegalArgumentException
    {
        super(name, coverage);
        final CoordinateSystem cs = crs.getCoordinateSystem();
        final int dimension = cs.getDimension();
        if (dimension != 3) {
            throw new MismatchedDimensionException(Errors.format(
                    Errors.Keys.MISMATCHED_DIMENSION_2, 3, dimension));
        }
        if (coverage instanceof SpatioTemporalCoverage3D) {
            final SpatioTemporalCoverage3D source = (SpatioTemporalCoverage3D) coverage;
            this.coverage          = source.coverage;
            this.temporalCRS       = source.temporalCRS;
            this.temporalDimension = source.temporalDimension;
            this.xDimension        = source.xDimension;
            this.yDimension        = source.yDimension;
            this.boundingBox       = source.boundingBox;
        } else {
            this.coverage = coverage;
            temporalCRS = DefaultTemporalCRS.castOrCopy(CRS.getTemporalComponent(crs));
            if (temporalCRS == null) {
                throw new IllegalArgumentException(Errors.format(
                        Errors.Keys.ILLEGAL_COORDINATE_REFERENCE_SYSTEM));
            }
            temporalDimension = CRSUtilities.getDimensionOf(crs, temporalCRS.getClass());
            final int  xDimension = (temporalDimension!=0) ? 0 : 1;
            final int  yDimension = (temporalDimension!=2) ? 2 : 1;
            Boolean swap = null; // 'null' if unknown, otherwise TRUE or FALSE.
control:    for (int p=0; p<=1; p++) {
                final AxisDirection direction;
                direction = AxisDirections.absolute(cs.getAxis(p==0 ? xDimension : yDimension).getDirection());
                for (int i=0; i<DIRECTIONS.length; i++) {
                    if (direction.equals(DIRECTIONS[i])) {
                        final boolean needSwap = (i & 1) != p;
                        if (swap == null) {
                            swap = Boolean.valueOf(needSwap);
                        } else if (swap.booleanValue() != needSwap) {
                            swap = null; // Found an ambiguity; stop the search.
                            break control;
                        }
                    }
                }
            }
            if (swap != null && swap.booleanValue()) {
                this.xDimension = yDimension;
                this.yDimension = xDimension;
            } else {
                this.xDimension = xDimension;
                this.yDimension = yDimension;
            }
        }
        assert temporalDimension>=0 && temporalDimension<dimension : temporalDimension;
        coordinate = new GeneralDirectPosition(dimension); // Each instance must have its own.
    }

    /**
     * Returns the coverage where the {@code evaluate} methods of this class delegate
     * their work. By default this is the coverage specified at construction time.
     *
     * @return The coverage where sample values are evaluated.
     *
     * @since 2.2
     */
    public Coverage getWrappedCoverage() {
        return coverage;
    }

    /**
     * The number of sample dimensions in the coverage.
     * For grid coverages, a sample dimension is a band.
     *
     * @return The number of sample dimensions in the coverage.
     */
    @Override
    public int getNumSampleDimensions() {
        return coverage.getNumSampleDimensions();
    }

    /**
     * Retrieve sample dimension information for the coverage.
     *
     * @param  index Index for sample dimension to retrieve. Indices are numbered 0 to
     *         (<var>{@linkplain #getNumSampleDimensions n}</var>-1).
     * @return Sample dimension information for the coverage.
     * @throws IndexOutOfBoundsException if {@code index} is out of bounds.
     */
    @Override
    public SampleDimension getSampleDimension(final int index) throws IndexOutOfBoundsException {
        return coverage.getSampleDimension(index);
    }

    /**
     * Returns the {@linkplain #getEnvelope envelope} geographic bounding box.
     * The bounding box coordinates uses the {@linkplain DefaultGeographicCRS#WGS84 WGS84} CRS.
     *
     * @return The geographic bounding box.
     * @throws TransformException if the envelope can't be transformed.
     */
    public GeographicBoundingBox getGeographicBoundingBox() throws TransformException {
        if (boundingBox == null) {
            final DefaultGeographicBoundingBox bbox = new DefaultGeographicBoundingBox();
            bbox.setBounds(getEnvelope());
            bbox.freeze();
            boundingBox = bbox;
        }
        return boundingBox;
    }

    /**
     * Returns the {@linkplain #getEnvelope envelope} time range.
     *
     * @return The time range of the envelope.
     */
    public DateRange getTimeRange() {
        final Envelope envelope = getEnvelope();
        return new DateRange(temporalCRS.toDate(envelope.getMinimum(temporalDimension)),
                             temporalCRS.toDate(envelope.getMaximum(temporalDimension)));
    }

    /**
     * Returns the dimension in the wrapped coverage for the specified dimension in this coverage.
     * The {@code evaluate(Point2D, Date)} methods expect ordinates in the
     * (<var>x</var>,&nbsp;<var>y</var>,&nbsp;<var>t</var>) order.
     * The {@code evaluate(DirectPosition)} methods and the wrapped coverage way uses a different
     * order.
     *
     * @param  dimension A dimension in this coverage:
     *         0 for <var>x</var>,
     *         1 for <var>y</var> or
     *         2 for <var>t</var>.
     * @return The corresponding dimension in the wrapped coverage.
     *
     * @see #toDate
     * @see #toPoint2D
     * @see #toDirectPosition
     */
    public final int toSourceDimension(final int dimension) {
        switch (dimension) {
            case 0:  return xDimension;
            case 1:  return yDimension;
            case 2:  return temporalDimension;
            default: throw new IllegalArgumentException();
        }
    }

    /**
     * Returns a coordinate point for the given spatial position and date.
     *
     * @param  point The spatial position.
     * @param  date  The date.
     * @return The coordinate point.
     *
     * @see #toDate
     * @see #toPoint2D
     *
     * @since 2.2
     */
    public final DirectPosition toDirectPosition(final Point2D point, final Date date) {
        coordinate.ordinates[       xDimension] = point.getX();
        coordinate.ordinates[       yDimension] = point.getY();
        coordinate.ordinates[temporalDimension] = temporalCRS.toValue(date);
        return coordinate;
    }

    /**
     * Returns the date for the specified direct position. This method (together with
     * {@link #toPoint2D toPoint2D}) is the converse of {@link #toDirectPosition toDirectPosition}.
     *
     * @param  position The direct position, as computed by
     *                  {@link #toDirectPosition toDirectPosition}.
     * @return The date.
     *
     * @see #toPoint2D
     * @see #toDirectPosition
     *
     * @since 2.2
     */
    public final Date toDate(final DirectPosition position) {
        return temporalCRS.toDate(position.getOrdinate(temporalDimension));
    }

    /**
     * Returns the spatial coordinate for the specified direct position. This method (together with
     * {@link #toDate toDate}) is the converse of {@link #toDirectPosition toDirectPosition}.
     *
     * @param  position The direct position, as computed by
     *                  {@link #toDirectPosition toDirectPosition}.
     * @return The spatial coordinate.
     *
     * @see #toDate
     * @see #toDirectPosition
     *
     * @since 2.2
     */
    public final Point2D toPoint2D(final DirectPosition position) {
        return new Point2D.Double(position.getOrdinate(xDimension),
                                  position.getOrdinate(yDimension));
    }

    /**
     * Returns a sequence of boolean values for a given point in the coverage.
     *
     * @param  point The coordinate point where to evaluate.
     * @param  time  The date where to evaluate.
     * @param  dest  An array in which to store values, or {@code null} to create a new array.
     * @return The {@code dest} array, or a newly created array if {@code dest} was null.
     * @throws PointOutsideCoverageException if {@code point} or {@code time} is outside coverage.
     * @throws CannotEvaluateException if the computation failed for some other reason.
     */
    public final boolean[] evaluate(final Point2D point, final Date time, boolean[] dest)
            throws PointOutsideCoverageException, CannotEvaluateException
    {
        try {
            return evaluate(toDirectPosition(point, time), dest);
        } catch (OrdinateOutsideCoverageException exception) {
            if (exception.getOutOfBoundsDimension() == temporalDimension) {
                exception = new OrdinateOutsideCoverageException(exception, time);
            }
            throw exception;
        }
    }

    /**
     * Returns a sequence of byte values for a given point in the coverage.
     *
     * @param  point The coordinate point where to evaluate.
     * @param  time  The date where to evaluate.
     * @param  dest  An array in which to store values, or {@code null} to create a new array.
     * @return The {@code dest} array, or a newly created array if {@code dest} was null.
     * @throws PointOutsideCoverageException if {@code point} or {@code time} is outside coverage.
     * @throws CannotEvaluateException if the computation failed for some other reason.
     */
    public final byte[] evaluate(final Point2D point, final Date time, byte[] dest)
            throws PointOutsideCoverageException, CannotEvaluateException
    {
        try {
            return evaluate(toDirectPosition(point, time), dest);
        } catch (OrdinateOutsideCoverageException exception) {
            if (exception.getOutOfBoundsDimension() == temporalDimension) {
                exception = new OrdinateOutsideCoverageException(exception, time);
            }
            throw exception;
        }
    }

    /**
     * Returns a sequence of integer values for a given point in the coverage.
     *
     * @param  point The coordinate point where to evaluate.
     * @param  time  The date where to evaluate.
     * @param  dest  An array in which to store values, or {@code null} to create a new array.
     * @return The {@code dest} array, or a newly created array if {@code dest} was null.
     * @throws PointOutsideCoverageException if {@code point} or {@code time} is outside coverage.
     * @throws CannotEvaluateException if the computation failed for some other reason.
     */
    public final int[] evaluate(final Point2D point, final Date time, int[] dest)
            throws PointOutsideCoverageException, CannotEvaluateException
    {
        try {
            return evaluate(toDirectPosition(point, time), dest);
        } catch (OrdinateOutsideCoverageException exception) {
            if (exception.getOutOfBoundsDimension() == temporalDimension) {
                exception = new OrdinateOutsideCoverageException(exception, time);
            }
            throw exception;
        }
    }

    /**
     * Returns a sequence of float values for a given point in the coverage.
     *
     * @param  point The coordinate point where to evaluate.
     * @param  time  The date where to evaluate.
     * @param  dest  An array in which to store values, or {@code null} to create a new array.
     * @return The {@code dest} array, or a newly created array if {@code dest} was null.
     * @throws PointOutsideCoverageException if {@code point} or {@code time} is outside coverage.
     * @throws CannotEvaluateException if the computation failed for some other reason.
     */
    public final float[] evaluate(final Point2D point, final Date time, float[] dest)
            throws PointOutsideCoverageException, CannotEvaluateException
    {
        try {
            return evaluate(toDirectPosition(point, time), dest);
        } catch (OrdinateOutsideCoverageException exception) {
            if (exception.getOutOfBoundsDimension() == temporalDimension) {
                exception = new OrdinateOutsideCoverageException(exception, time);
            }
            throw exception;
        }
    }

    /**
     * Returns a sequence of double values for a given point in the coverage.
     *
     * @param  point The coordinate point where to evaluate.
     * @param  time  The date where to evaluate.
     * @param  dest  An array in which to store values, or {@code null} to create a new array.
     * @return The {@code dest} array, or a newly created array if {@code dest} was null.
     * @throws PointOutsideCoverageException if {@code point} or {@code time} is outside coverage.
     * @throws CannotEvaluateException if the computation failed for some other reason.
     */
    public final double[] evaluate(final Point2D point, final Date time, double[] dest)
            throws PointOutsideCoverageException, CannotEvaluateException
    {
        try {
            return evaluate(toDirectPosition(point, time), dest);
        } catch (OrdinateOutsideCoverageException exception) {
            if (exception.getOutOfBoundsDimension() == temporalDimension) {
                exception = new OrdinateOutsideCoverageException(exception, time);
            }
            throw exception;
        }
    }

    /**
     * Returns the value vector for a given point in the coverage.
     *
     * @param  coord The coordinate point where to evaluate.
     * @throws PointOutsideCoverageException if {@code coord} is outside coverage.
     * @throws CannotEvaluateException if the computation failed for some other reason.
     */
    @Override
    public final Object evaluate(final DirectPosition coord)
            throws PointOutsideCoverageException, CannotEvaluateException
    {
        return coverage.evaluate(coord);
    }

    /**
     * Returns a sequence of boolean values for a given point in the coverage.
     */
    @Override
    public final boolean[] evaluate(final DirectPosition coord, boolean[] dest)
            throws PointOutsideCoverageException, CannotEvaluateException
    {
        return coverage.evaluate(coord, dest);
    }

    /**
     * Returns a sequence of byte values for a given point in the coverage.
     */
    @Override
    public final byte[] evaluate(final DirectPosition coord, byte[] dest)
            throws PointOutsideCoverageException, CannotEvaluateException
    {
        return coverage.evaluate(coord, dest);
    }

    /**
     * Returns a sequence of integer values for a given point in the coverage.
     */
    @Override
    public final int[] evaluate(final DirectPosition coord, int[] dest)
            throws PointOutsideCoverageException, CannotEvaluateException
    {
        return coverage.evaluate(coord, dest);
    }

    /**
     * Returns a sequence of float values for a given point in the coverage.
     */
    @Override
    public final float[] evaluate(final DirectPosition coord, float[] dest)
            throws PointOutsideCoverageException, CannotEvaluateException
    {
        return coverage.evaluate(coord, dest);
    }

    /**
     * Returns a sequence of double values for a given point in the coverage.
     */
    @Override
    public final double[] evaluate(final DirectPosition coord, final double[] dest)
            throws PointOutsideCoverageException, CannotEvaluateException
    {
        return coverage.evaluate(coord, dest);
    }

    /**
     * Returns a 2 dimensional grid coverage for the given date. The grid geometry will be computed
     * in order to produce image with the {@linkplain #getDefaultPixelSize() default pixel size},
     * if any.
     *
     * @param  time The date where to evaluate.
     * @return The grid coverage at the specified time, or {@code null}
     *         if the requested date fall in a hole in the data.
     * @throws PointOutsideCoverageException if {@code time} is outside coverage.
     * @throws CannotEvaluateException if the computation failed for some other reason.
     *
     * @see #getRenderableImage(Date)
     * @see RenderableImage#createDefaultRendering()
     */
    public GridCoverage2D getGridCoverage2D(final Date time) throws CannotEvaluateException {
        final InternationalString name = getName();
        final CoordinateReferenceSystem crs = CRS.getHorizontalComponent(this.crs);
        if (crs == null) {
            throw new CannotEvaluateException(
                    Errors.format(Errors.Keys.CANT_SEPARATE_CRS_1, this.crs.getName()));
        }
        final RenderedImage           image = getRenderableImage(time).createDefaultRendering();
        final GridSampleDimension[]   bands = new GridSampleDimension[getNumSampleDimensions()];
        for (int i=0; i<getNumSampleDimensions(); i++){
            bands[i] = GridSampleDimension.castOrCopy(getSampleDimension(i));
        }
        final MathTransform gridToCRS;
        gridToCRS = MathTransforms.linear((AffineTransform) image.getProperty("gridToCRS"));
        if (factory == null) {
            factory = CoverageFactoryFinder.getGridCoverageFactory(HINTS);
        }
        return factory.create(name, image, crs, gridToCRS, bands, null, null);
    }

    /**
     * Returns 2D view of this grid coverage at the given date. For images produced by the
     * {@linkplain RenderableImage#createDefaultRendering() default rendering}, the size
     * will be computed from the {@linkplain #getDefaultPixelSize() default pixel size},
     * if any.
     *
     * @param  date The date where to evaluate the images.
     * @return The renderable image.
     */
    public RenderableImage getRenderableImage(final Date date) {
        return new Renderable(date);
    }

    /**
     * Constructs rendered images on demand.
     *
     * @author Martin Desruisseaux (IRD)
     * @version 3.00
     *
     * @since 2.1
     * @module
     */
    private final class Renderable extends AbstractCoverage.Renderable {
        /**
         * For cross-version compatibility.
         */
        private static final long serialVersionUID = 2541951932891099034L;

        /**
         * Construct a {@code Renderable} object for the supplied date.
         */
        public Renderable(final Date date) {
            super(xDimension, yDimension);
            coordinate.ordinates[temporalDimension] = temporalCRS.toValue(date);
        }

        /**
         * Returns a rendered image with width and height computed from
         * {@link Coverage3D#getDefaultPixelSize()}.
         */
        @Override
        public RenderedImage createDefaultRendering() {
            final Dimension2D pixelSize = getDefaultPixelSize();
            if (pixelSize == null) {
                return super.createDefaultRendering();
            }
            return createScaledRendering((int) Math.round(getWidth()  / pixelSize.getWidth()),
                                         (int) Math.round(getHeight() / pixelSize.getHeight()), null);
        }
    }

    /**
     * Returns the default pixel size for images to be produced by {@link #getRenderableImage(Date)}.
     * This method is invoked by {@link RenderableImage#createDefaultRendering()} for computing a
     * default image size. The default implementation for this method always returns {@code null}.
     * Subclasses should overrides this method in order to provides a pixel size better suited to
     * their data.
     *
     * @return The default pixel size, or {@code null} if no default is provided.
     */
    protected Dimension2D getDefaultPixelSize() {
        return null;
    }
}
