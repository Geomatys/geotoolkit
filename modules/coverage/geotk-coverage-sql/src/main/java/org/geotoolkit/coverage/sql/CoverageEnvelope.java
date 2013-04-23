/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2007-2012, Geomatys
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
package org.geotoolkit.coverage.sql;

import java.util.Date;
import java.awt.Dimension;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import org.opengis.geometry.Envelope;
import org.opengis.util.FactoryException;
import org.opengis.referencing.operation.CoordinateOperationFactory;
import org.opengis.referencing.operation.TransformException;
import org.opengis.referencing.operation.CoordinateOperation;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.crs.SingleCRS;

import org.geotoolkit.util.Cloneable;
import org.geotoolkit.util.DateRange;
import org.geotoolkit.util.NumberRange;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.geometry.AbstractEnvelope;
import org.geotoolkit.display.shape.XRectangle2D;
import org.geotoolkit.display.shape.FloatDimension2D;
import org.geotoolkit.display.shape.DoubleDimension2D;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.internal.sql.table.SpatialDatabase;
import org.geotoolkit.referencing.crs.DefaultTemporalCRS;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.util.Utilities;

import static java.lang.Double.doubleToLongBits;
import static java.lang.Double.NEGATIVE_INFINITY;
import static java.lang.Double.POSITIVE_INFINITY;


/**
 * An envelope holding the spatio-temporal extent and preferred resolution of a coverage. The
 * envelope Coordinate Reference System (CRS) is determined by the {@link CoverageDatabase}
 * instance associated with this object and can not be changed. The {@code CoverageDatabase}
 * may define different CRS, but the following axes can be considered typical:
 * <p>
 * <ul>
 *   <li>The longitude in decimal degrees relative to Greenwich meridian.</li>
 *   <li>The latitude in decimal degrees.</li>
 *   <li>Altitude in metres above the WGS 84 ellipsoid.</li>
 *   <li>Time in fractional days since epoch.</li>
 * </ul>
 * <p>
 * This class provides convenience methods for fetching and setting the
 * {@linkplain #getHorizontalRange() horizontal rectangle},
 * {@linkplain #getVerticalRange() vertical range} and
 * {@linkplain #getTimeRange() temporal range} of the envelope.
 *
 * {@section Conventions}
 * In this class, the lower and upper bounds are both inclusive. This is consistent with OGC/ISO
 * conventions but different than typical Java conventions, where the upper bounds is often
 * exclusive.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.10
 *
 * @since 3.10 (derived from Seagis)
 * @module
 */
public class CoverageEnvelope extends AbstractEnvelope implements Cloneable {
    /**
     * The database for which this extent is defined. This is used in order
     * to get the horizontal, vertical and temporal components of the CRS.
     */
    final SpatialDatabase database;

    /**
     * The envelope time component, in milliseconds since January 1st, 1970.
     * May be {@link Long#MIN_VALUE} or {@link Long#MAX_VALUE} if unbounded.
     */
    private long tMin, tMax;

    /**
     * The envelope spatial component in {@link SpatialDatabase#horizontalCRS} coordinates. The
     * longitude range may be larger than needed (±360° instead of ±180°) because we don't know
     * in advance if the longitudes are inside the [-180 .. +180°] range or the [0 .. 360°] range.
     */
    private double xMin, xMax, yMin, yMax, zMin, zMax;

    /**
     * The preferred resolution along the <var>x</var> and <var>y</var> axis. Units shall be the
     * same than for the horizontal bounding box (determined by {@link SpatialDatabase#horizontalCRS}).
     * This information is only approximative; there is no guarantee that an image to be read will
     * have that resolution. A null value (zero) means that the best resolution should be used.
     */
    private float xResolution, yResolution;

    /**
     * The bounding box computed by {@link #getEnvelope()}, or {@code null}
     * if not yet computed. This is cached for performance reasons.
     */
    private transient GeneralEnvelope envelope;

    /**
     * Creates a new instance initialized to infinite bounds.
     *
     * @param database The database for which this extent is defined.
     */
    CoverageEnvelope(final SpatialDatabase database) {
        this.database = database;
        tMin = Long.MIN_VALUE;
        tMax = Long.MAX_VALUE;
        xMin = NEGATIVE_INFINITY;
        xMax = POSITIVE_INFINITY;
        yMin = NEGATIVE_INFINITY;
        yMax = POSITIVE_INFINITY;
        zMin = NEGATIVE_INFINITY;
        zMax = POSITIVE_INFINITY;
    }

    /**
     * Resets all envelope attributes to their initial state, which is an infinite envelope.
     *
     * @return {@code true} if this envelope changed as a result of this method call.
     */
    public boolean clear() {
        // Line below really requires | operator, not ||.
        return setHorizontalRange(null) | setVerticalRange(null) | setTimeRange(null) | setPreferredResolution(null);
    }

    /**
     * Sets the spatio-temporal envelope and the resolution from an other
     * {@code CoverageEnvelope} object. This method invokes the individual
     * {@link #setHorizontalRange}, {@link #setVerticalRange} and {@link #setTimeRange}
     * methods if possible, which are more efficient than {@link #setEnvelope}.
     */
    final void setAll(final CoverageEnvelope envelope) throws TransformException {
        if (envelope != this) {
            if (envelope == null || envelope.database != database) {
                setEnvelope(envelope);
            } else {
                setHorizontalRange(envelope.getHorizontalRange());
                setVerticalRange  (envelope.getVerticalRange());
                setTimeRange      (envelope.getTimeRange());
            }
            setPreferredResolution(envelope != null ? envelope.getPreferredResolution() : null);
        }
    }

    /**
     * Sets this envelope to the intersection of this envelope with the given one.
     */
    final void intersect(CoverageEnvelope envelope) throws TransformException {
        if (envelope.database != database) {
            // Paranoiac safety - should not happen.
            final CoverageEnvelope old = envelope;
            envelope = clone();
            envelope.setAll(old);
        }
        final Rectangle2D thisRect = getHorizontalRange();
        final Rectangle2D envRect = envelope.getHorizontalRange();
        if (thisRect instanceof XRectangle2D) {
            ((XRectangle2D) thisRect).intersect(envRect);
        } else {
            Rectangle2D.intersect(thisRect, envRect, thisRect);
        }
        setHorizontalRange(thisRect);
        setVerticalRange(Math.max(zMin, envelope.zMin), Math.min(zMax, envelope.zMax));
        setTimeRange(new Date(Math.max(tMin, envelope.tMin)), new Date(Math.min(tMax, envelope.tMax)));
    }

    /**
     * Returns the spatio-temporal CRS of this envelope.
     */
    @Override
    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        return database.spatioTemporalCRS;
    }

    /**
     * Returns the CRS of this envelope containing only the requested dimensions. If an argument
     * is {@code true}, then the returned CRS will contain the corresponding dimension if the
     * database CRS has such dimension. If an argument is {@code false}, then the returned CRS
     * is guaranteed to not have the corresponding dimension. If all arguments are {@code false},
     * then this method returns {@code null}.
     *
     * @param  horizontal {@code false} for excluding the horizontal component in the returned CRS.
     * @param  vertical   {@code false} for excluding the vertical   component in the returned CRS.
     * @param  temporal   {@code false} for excluding the temporal   component in the returned CRS.
     * @return The envelope CRS excluding the components specified by {@code false} argument value,
     *         or {@code null} if there is no CRS for the remaining components.
     */
    public CoordinateReferenceSystem getCoordinateReferenceSystem(
            final boolean horizontal, final boolean vertical, final boolean temporal)
    {
        final CoordinateReferenceSystem crs;
        if (horizontal) {
            if (vertical) {
                crs = temporal ? database.spatioTemporalCRS : database.spatialCRS;
            } else {
                crs = temporal ? database.horizTemporalCRS : database.horizontalCRS;
            }
        } else {
            if (vertical) {
                crs = temporal ? database.vertTemporalCRS : database.verticalCRS;
            } else {
                crs = temporal ? database.temporalCRS : null;
            }
        }
        return crs;
    }

    /**
     * Returns the number of dimension in this envelope, which is usually 4.
     */
    @Override
    public int getDimension() {
        return database.spatioTemporalCRS.getCoordinateSystem().getDimension();
    }

    /**
     * Returns the dimension in {@code spatioTemporalCS} of the first axis which is colinear
     * with the axes of the given {@code crs}. If none are found, return -1.
     */
    private static int dimensionColinearWith(final CoordinateSystem spatioTemporalCS, final SingleCRS crs) {
        return (crs != null) ? CRSUtilities.dimensionColinearWith(spatioTemporalCS, crs.getCoordinateSystem()) : -1;
    }

    /**
     * Returns the spatio-temporal envelope. This implementation computes the envelope from
     * the informations returned by {@link #getHorizontalRange()}, {@link #getVerticalRange()}
     * and {@link #getTimeRange()}.
     *
     * @return The spatio-temporal envelope. This is a direct reference to the instance holds
     *         by this class - <strong>do not modify!</strong>.
     *
     * @see #getHorizontalRange()
     * @see #getVerticalRange()
     * @see #getTimeRange()
     */
    private GeneralEnvelope getEnvelope() {
        GeneralEnvelope envelope = this.envelope;
        if (envelope == null) {
            final CoordinateReferenceSystem crs = database.spatioTemporalCRS;
            final CoordinateSystem cs = crs.getCoordinateSystem();
            envelope = new GeneralEnvelope(crs);
            int dim = dimensionColinearWith(cs, database.horizontalCRS);
            if (dim >= 0) {
                final Rectangle2D box = getHorizontalRange();
                envelope.setRange(dim,   box.getMinX(), box.getMaxX());
                envelope.setRange(dim+1, box.getMinY(), box.getMaxY());
            }
            dim = dimensionColinearWith(cs, database.verticalCRS);
            if (dim >= 0) {
                final NumberRange<?> altitude = getVerticalRange();
                envelope.setRange(dim, altitude.getMinimum(), altitude.getMaximum());
            }
            final DefaultTemporalCRS temporalCRS = database.temporalCRS;
            dim = dimensionColinearWith(cs, temporalCRS);
            if (dim >= 0) {
                final DateRange time = getTimeRange();
                final Date startTime = time.getMinValue();
                final Date   endTime = time.getMaxValue();
                envelope.setRange(dim,
                        (startTime != null) ? temporalCRS.toValue(startTime) : NEGATIVE_INFINITY,
                          (endTime != null) ? temporalCRS.toValue(  endTime) : POSITIVE_INFINITY);
            }
            this.envelope = envelope;
        }
        return envelope;
    }

    /**
     * Sets the spatio-temporal envelope. The default implementation delegates to
     * {@link #setHorizontalRange(Rectangle2D)}, {@link #setVerticalRange(NumberRange)}
     * and {@link #setTimeRange(DateRange)}, applying a coordinate transformation if needed.
     * <p>
     * If the given envelope has more dimensions than this
     * {@linkplain #getDimension() envelope dimension}, then the extra dimensions will be ignored.
     * If the given envelope has less dimensions, then the {@code CoverageEnvelope} dimensions not
     * present in the given envelope will be left unchanged.
     * <p>
     * <b>Example:</b> If the given envelope is two-dimensional and its CRS is horizontal,
     * then only the {@link #setHorizontalRange(Rectangle2D)} method will be invoked on this
     * {@code CoverageEnvelope} - the vertical and temporal ordinate values will be unchanged.
     *
     * @param  envelope The envelope, or {@code null} to reset to full coverage.
     * @return {@code true} if the envelope changed as a result of this call, or
     *         {@code false} if the specified envelope is equals to the one already set.
     * @throws TransformException if an error occurred during coordinate transformation.
     */
    public boolean setEnvelope(Envelope envelope) throws TransformException {
        if (envelope == null) {
            // Like clear() except that we don't touch to the resolution.
            // Note: line below really requires the | operator, not ||.
            return setHorizontalRange(null) | setVerticalRange(null) | setTimeRange(null);
        }
        CoordinateReferenceSystem sourceCRS = envelope.getCoordinateReferenceSystem();
        CoordinateReferenceSystem targetCRS = getCoordinateReferenceSystem(
                CRS.getHorizontalCRS(sourceCRS) != null,
                CRS.getVerticalCRS  (sourceCRS) != null,
                CRS.getTemporalCRS  (sourceCRS) != null);
        if (targetCRS == null) {
            return false;
        }
        final CoordinateOperationFactory factory = CRS.getCoordinateOperationFactory(true);
        try {
            final CoordinateOperation userToStandard = factory.createOperation(sourceCRS, targetCRS);
            if (!userToStandard.getMathTransform().isIdentity()) {
                envelope = CRS.transform(userToStandard, envelope);
            }
        } catch (FactoryException e) {
            throw new TransformException(Errors.format(Errors.Keys.CANT_TRANSFORM_ENVELOPE), e);
        }
        boolean changed = false;
        sourceCRS = envelope.getCoordinateReferenceSystem();
        final CoordinateSystem cs = sourceCRS.getCoordinateSystem();
        int dim = dimensionColinearWith(cs, database.horizontalCRS);
        if (dim >= 0) {
            changed |= setHorizontalRange(XRectangle2D.createFromExtremums(
                    envelope.getMinimum(dim), envelope.getMinimum(dim+1),
                    envelope.getMaximum(dim), envelope.getMaximum(dim+1)));
        }
        dim = dimensionColinearWith(cs, database.verticalCRS);
        if (dim >= 0) {
            changed |= setVerticalRange(envelope.getMinimum(dim), envelope.getMaximum(dim));
        }
        final DefaultTemporalCRS temporalCRS = database.temporalCRS;
        dim = dimensionColinearWith(cs, temporalCRS);
        if (dim >= 0) {
            final Date minimum = temporalCRS.toDate(envelope.getMinimum(dim));
            final Date maximum = temporalCRS.toDate(envelope.getMaximum(dim));
            changed |= setTimeRange(minimum, maximum);
        }
        return changed;
    }

    /**
     * Returns the horizontal bounding box of the elements to be read.
     * The returned rectangle may contains infinite values.
     *
     * @return The horizontal bounding box of the elements to be read.
     *
     * @see #getVerticalRange()
     * @see #getTimeRange()
     */
    public Rectangle2D getHorizontalRange() {
        return XRectangle2D.createFromExtremums(xMin, yMin, xMax, yMax);
    }

    /**
     * Sets the horizontal bounding box of the elements to be read.
     *
     * @param  area The horizontal bounding box of the elements to be read.
     * @return {@code true} if the bounding box changed as a result of this call, or
     *         {@code false} if the specified box is equals to the one already set.
     */
    public boolean setHorizontalRange(final Rectangle2D area) {
        boolean change;
        change  = (xMin != (xMin = (area != null) ? area.getMinX() : NEGATIVE_INFINITY));
        change |= (xMax != (xMax = (area != null) ? area.getMaxX() : POSITIVE_INFINITY));
        change |= (yMin != (yMin = (area != null) ? area.getMinY() : NEGATIVE_INFINITY));
        change |= (yMax != (yMax = (area != null) ? area.getMaxY() : POSITIVE_INFINITY));
        if (change) {
            fireStateChanged("Envelope2D");
        }
        return change;
    }

    /**
     * Returns the vertical range of the elements to be read.
     *
     * @return The vertical range of the elements to be read.
     *
     * @see #getHorizontalRange()
     * @see #getTimeRange()
     */
    public NumberRange<Double> getVerticalRange() {
        return NumberRange.create(zMin, zMax);
    }

    /**
     * Sets the vertical range of the elements to be read. This convenience
     * method delegates to {@link #setVerticalRange(double, double)}.
     *
     * @param  range The vertical range, or {@code null} for full coverage.
     * @return {@code true} if the vertical range changed as a result of this call, or
     *         {@code false} if the specified range is equals to the one already set.
     */
    public final boolean setVerticalRange(final NumberRange<?> range) {
        final double minimum, maximum;
        if (range != null) {
            minimum = range.getMinimum(true);
            maximum = range.getMaximum(true);
        } else {
            minimum = NEGATIVE_INFINITY;
            maximum = POSITIVE_INFINITY;
        }
        return setVerticalRange(minimum, maximum);
    }

    /**
     * Sets the vertical range of the elements to be read.
     *
     * @param  minimum The minimal <var>z</var> value, inclusive.
     * @param  maximum The maximal <var>z</var> value, <strong>inclusive</strong>.
     * @return {@code true} if the vertical range changed as a result of this call, or
     *         {@code false} if the specified range is equals to the one already set.
     */
    public boolean setVerticalRange(final double minimum, final double maximum) {
        boolean change;
        change  = (doubleToLongBits(zMin) != doubleToLongBits(zMin = minimum));
        change |= (doubleToLongBits(zMax) != doubleToLongBits(zMax = maximum));
        if (change) {
            fireStateChanged("VerticalRange");
        }
        return change;
    }

    /**
     * Returns the time range of the elements to be read.
     *
     * @return The time range of the elements to be read.
     *
     * @see #getHorizontalRange()
     * @see #getVerticalRange()
     */
    public DateRange getTimeRange() {
        return new DateRange((tMin != Long.MIN_VALUE) ? new Date(tMin) : null,
                             (tMax != Long.MAX_VALUE) ? new Date(tMax) : null);
    }

    /**
     * Sets the time range of the elements to be read. This convenience method
     * delegates to {@link #setTimeRange(Date, Date)}.
     *
     * @param  timeRange The time range.
     * @return {@code true} if the time range changed as a result of this call, or
     *         {@code false} if the specified range is equals to the one already set.
     */
    public final boolean setTimeRange(final DateRange timeRange) {
        Date startTime, endTime;
        if (timeRange != null) {
            startTime = timeRange.getMinValue();
            endTime   = timeRange.getMaxValue();
        } else {
            startTime = null;
            endTime   = null;
        }
        if (startTime != null && !timeRange.isMinIncluded()) {
            startTime = new Date(startTime.getTime() + 1);
        }
        if (endTime != null && !timeRange.isMaxIncluded()) {
            endTime = new Date(endTime.getTime() - 1);
        }
        return setTimeRange(startTime, endTime);
    }

    /**
     * Sets the time range of the elements to be read by this table.
     *
     * @param  startTime The start time, inclusive.
     * @param  endTime The end time, <strong>inclusive</strong>.
     * @return {@code true} if the time range changed as a result of this call, or
     *         {@code false} if the specified range is equals to the one already set.
     */
    public boolean setTimeRange(final Date startTime, final Date endTime) {
        boolean change;
        change  = (tMin != (tMin = (startTime != null) ? startTime.getTime() : Long.MIN_VALUE));
        change |= (tMax != (tMax = (  endTime != null) ?   endTime.getTime() : Long.MAX_VALUE));
        if (change) {
            fireStateChanged("TimeRange");
        }
        return change;
    }

    /**
     * Returns the approximative resolution desired, or {@code null} for best resolution.
     * The units are the same than for the {@linkplain #getHorizontalRange horizontal envelope}.
     *
     * @return The resolution, or {@code null} for the best resolution available.
     */
    public Dimension2D getPreferredResolution() {
        if (xResolution > 0 || yResolution > 0) {
            return new FloatDimension2D(xResolution, yResolution);
        } else {
            return null;
        }
    }

    /**
     * Sets the preferred resolution in units of the {@linkplain #getHorizontalRange horizontal
     * envelope}. This is only an approximative hint, since there is no guarantee that an image
     * will be read with that resolution. A null values means that the best available resolution
     * should be used.
     *
     * @param  resolution The preferred geographic resolution, or {@code null} for best resolution.
     * @return {@code true} if the resolution changed as a result of this call, or
     *         {@code false} if the specified resolution is equals to the one already set.
     */
    public boolean setPreferredResolution(final Dimension2D resolution) {
        float dx, dy;
        if (resolution != null) {
            dx = (float) resolution.getWidth ();
            dy = (float) resolution.getHeight();
            if (!(dx >= 0)) dx = 0; // '!' for catching NaN
            if (!(dy >= 0)) dy = 0;
        } else {
            dx = 0;
            dy = 0;
        }
        boolean change;
        change  = (xResolution != (xResolution = dx));
        change |= (yResolution != (yResolution = dy));
        if (change) {
            fireStateChanged("PreferredResolution");
        }
        return change;
    }

    /**
     * Returns the approximative size of the desired image, or {@code null} if unknown.
     * This is computed from the {@linkplain #getHorizontalRange() horizontal range} and
     * the {@linkplain #getPreferredResolution() preferred resolution}.
     *
     * @return The image size computed from the horizontal range and the resolution,
     *         or {@code null} if the size can not be computed.
     */
    public Dimension getPreferredImageSize() {
        final double width  = (xMax - xMin) / xResolution;
        if (width >= 1 && width <= Integer.MAX_VALUE) {
            final double height = (yMax - yMin) / yResolution;
            if (height >= 1 && height <= Integer.MAX_VALUE) {
                return new Dimension((int) Math.round(width), (int) Math.round(height));
            }
        }
        return null;
    }

    /**
     * Sets the approximative size of the desired image. This is a convenience method which
     * {@linkplain #setPreferredResolution set the preferred resolution} to a value computed
     * from the {@linkplain #getHorizontalRange() horizontal range} and the given size.
     * <p>
     * The {@link #setHorizontalRange(Rectangle2D)} or {@link #setEnvelope(Envelope)} method
     * must have been invoked with a finite envelope before this {@code setPreferredImageSize}
     * method. The previous preferred resolution, if any, is discarded.
     *
     * @param  size The new preferred image size, or {@code null}.
     * @return {@code true} if the resolution changed as a result of this call, or
     *         {@code false} if the specified resolution is equals to the one computed
     *         by this method.
     * @throws IllegalStateException If the current {@linkplain #getHorizontalRange()
     *         horizontal range} is not finite.
     */
    public boolean setPreferredImageSize(final Dimension size) throws IllegalStateException {
        Dimension2D resolution = null;
        if (size != null) {
            double dx = size.width;
            double dy = size.height;
            if (!(dx > 0 && dy > 0)) {
                throw new IllegalArgumentException(errors()
                        .getString(Errors.Keys.ILLEGAL_ARGUMENT_2, "size", size));
            }
            dx = (xMax - xMin) / dx;
            dy = (yMax - yMin) / dy;
            if (Double.isInfinite(dx) || Double.isInfinite(dy)) {
                throw new IllegalStateException(errors()
                        .getString(Errors.Keys.UNDEFINED_PROPERTY_1, "envelope"));
            }
            resolution = new DoubleDimension2D(dx, dy);
        }
        return setPreferredResolution(resolution);
    }

    /**
     * Returns the resource bundled for error messages.
     */
    final Errors errors() {
        return Errors.getResources(database.getLocale());
    }

    /**
     * Notifies that the state of this extent changed. Subclasses can
     * override this method if they need to be notified about any change.
     *
     * @param property The name of the property that changed.
     */
    void fireStateChanged(final String property) {
        if (!"PreferredResolution".equals(property)) {
            envelope = null;
        }
    }

    /**
     * Returns the minimal ordinate along the specified dimension.
     *
     * @param  dimension The dimension for which to obtain the ordinate value.
     * @return The minimal ordinate at the given dimension.
     * @throws IndexOutOfBoundsException If the given index is negative or is equals or greater
     *         than the {@linkplain #getDimension envelope dimension}.
     */
    @Override
    public double getLower(final int dimension) throws IndexOutOfBoundsException {
        return getEnvelope().getLower(dimension);
    }

    /**
     * Returns the maximal ordinate along the specified dimension.
     *
     * @param  dimension The dimension for which to obtain the ordinate value.
     * @return The maximal ordinate at the given dimension.
     * @throws IndexOutOfBoundsException If the given index is negative or is equals or greater
     *         than the {@linkplain #getDimension envelope dimension}.
     */
    @Override
    public double getUpper(final int dimension) throws IndexOutOfBoundsException {
        return getEnvelope().getUpper(dimension);
    }

    /**
     * Returns the median ordinate along the specified dimension.
     *
     * @param  dimension The dimension for which to obtain the ordinate value.
     * @return The median ordinate at the given dimension.
     * @throws IndexOutOfBoundsException If the given index is negative or is equals or greater
     *         than the {@linkplain #getDimension envelope dimension}.
     */
    @Override
    public double getMedian(final int dimension) throws IndexOutOfBoundsException {
        return getEnvelope().getMedian(dimension);
    }

    /**
     * Returns the envelope span along the specified dimension.
     *
     * @param  dimension The dimension for which to obtain the ordinate value.
     * @return The envelope span along the given dimension.
     * @throws IndexOutOfBoundsException If the given index is negative or is equals or greater
     *         than the {@linkplain #getDimension envelope dimension}.
     */
    @Override
    public double getSpan(final int dimension) throws IndexOutOfBoundsException {
        return getEnvelope().getSpan(dimension);
    }

    /**
     * Returns a clone of this coverage envelope.
     */
    @Override
    public CoverageEnvelope clone() {
        try {
            return (CoverageEnvelope) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e); // Should never happen since we are Cloneable.
        }
    }

    /**
     * Returns a hash code value for this extent.
     */
    @Override
    public int hashCode() {
        long code = tMin + 31 *
                   (tMax + 31 *
                   (Double.doubleToLongBits(xMin) + 31 *
                   (Double.doubleToLongBits(yMin) + 31 *
                   (Double.doubleToLongBits(zMin) + 31 *
                   (Double.doubleToLongBits(xMax) + 31 *
                   (Double.doubleToLongBits(yMax) + 31 *
                   (Double.doubleToLongBits(zMax))))))));
        return (int) code ^ (int) (code >>> 32) +
                31 * (Float.floatToIntBits(xResolution) +
                31 * Float.floatToIntBits(yResolution));
    }

    /**
     * Compares this extent with the given object for equality.
     *
     * @param other The object to compare with this one.
     */
    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (other != null && other.getClass() == getClass()) {
            final CoverageEnvelope that = (CoverageEnvelope) other;
            return tMin == that.tMin && tMax == that.tMax &&
                   Utilities.equals(xMin, that.xMin) &&
                   Utilities.equals(xMax, that.xMax) &&
                   Utilities.equals(yMin, that.yMin) &&
                   Utilities.equals(yMax, that.yMax) &&
                   Utilities.equals(zMin, that.zMin) &&
                   Utilities.equals(zMax, that.zMax) &&
                   Utilities.equals(xResolution, that.xResolution) &&
                   Utilities.equals(yResolution, that.yResolution) &&
                   Utilities.equals(database.spatioTemporalCRS, that.database.spatioTemporalCRS);
        }
        return false;
    }
}
