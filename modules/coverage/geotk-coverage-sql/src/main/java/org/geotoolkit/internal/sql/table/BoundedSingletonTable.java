/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2007-2010, Geomatys
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
package org.geotoolkit.internal.sql.table;

import java.util.Date;
import java.util.Calendar;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import org.opengis.geometry.Envelope;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.CoordinateOperationFactory;
import org.opengis.referencing.operation.TransformException;
import org.opengis.referencing.operation.CoordinateOperation;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.crs.SingleCRS;

import org.geotoolkit.util.DateRange;
import org.geotoolkit.util.NumberRange;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.display.shape.XRectangle2D;
import org.geotoolkit.display.shape.FloatDimension2D;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.referencing.crs.DefaultTemporalCRS;

import static java.lang.Math.min;
import static java.lang.Math.max;
import static java.lang.Double.doubleToLongBits;
import static java.lang.Double.NEGATIVE_INFINITY;
import static java.lang.Double.POSITIVE_INFINITY;

import static org.geotoolkit.referencing.CRS.transform;
import static org.geotoolkit.referencing.CRS.equalsIgnoreMetadata;
import static org.geotoolkit.referencing.CRS.getCoordinateOperationFactory;


/**
 * Base class for tables with a {@code getEntry(...)} method restricted to the elements contained in
 * some spatio-temporal bounding box. The bounding box is defined either by an {@link #getEnvelope()
 * Envelope} expressed in the {@linkplain #getSpatioTemporalCRS CRS of this table}, or by a combinaison
 * of {@linkplain #getEnvelope2D() horizontal envelope}, {@link #getVerticalRange() vertical range}
 * and {@linkplain #getTimeRange() time range} expressed in standard CRS.
 *
 * {@section Convention}
 * For every envelopes or ranges in this class, the lower and upper bounds are both inclusive.
 * This is done that way for consistency with the envelope computed by {@link #trimEnvelope()}.
 *
 * @param <E> The kind of entries to be created by this table.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.10
 *
 * @since 3.10 (derived from Seagis)
 * @module
 */
public abstract class BoundedSingletonTable<E extends Entry> extends SingletonTable<E> {
    /**
     * If no minimal and maximal <var>x</var> or <var>y</var> value was supplied, the value
     * to use. This is used because the {@code 'infinity'} value doesn't seem to work.
     * <p>
     * Note that default values exist in the time dimension as well. Search for usage of
     * {@code DEFAULT_LIMIT} to find the method.
     */
    private static final double DEFAULT_LIMIT = 1E+12;

    /**
     * The parameter to use for looking an element by time range, or {@code null} if none.
     */
    private final Parameter byTimeRange;

    /**
     * The parameter to use for looking an element by horizontal spatial extent,
     * or {@code null} if none.
     */
    private final Parameter bySpatialExtent;

    /**
     * The bounding box computed by {@link #getEnvelope()}, or {@code null}
     * if not yet computed. Cached for performance reasons.
     */
    private GeneralEnvelope envelope;

    /**
     * {@code true} if the {@link #trimEnvelope} method already shrinked the
     * {@linkplain #getEnvelope spatio-temporal envelope} for this table.
     */
    private boolean trimmed;

    /**
     * The envelope time component, in milliseconds since January 1st, 1970.
     * May be {@link Long#MIN_VALUE} or {@link Long#MAX_VALUE} if unbounded.
     */
    private long tMin, tMax;

    /**
     * The envelope spatial component in {@link Database#horizontalCRS} coordinates. The longitude
     * range may be larger than needed (±360° instead of ±180°) because we don't know in advance if
     * the longitudes are inside the [-180 .. +180°] range or the [0 .. 360°] range.
     */
    private double xMin, xMax, yMin, yMax, zMin, zMax;

    /**
     * The preferred resolution along the <var>x</var> and <var>y</var> axis. Units shall be the
     * same than for the horizontal bounding box (determined by {@link Database#horizontalCRS}).
     * This information is only approximative; there is no garantee that an image to be read will
     * have that resolution. A null value (zero) means that the best resolution should be used.
     */
    private float xResolution, yResolution;

    /**
     * Creates a new table using the specified query. The query given in argument should be some
     * subclass with {@link Query#addColumn addColumn} and {@link Query#addParameter addParameter}
     * methods invoked in its constructor.
     *
     * @param  query The query to use for this table.
     * @param  pkParam The parameter for looking an element by name, or {@code null} if none.
     * @param  byTimeRange The parameter to use for looking an element by time range.
     * @param  bySpatialExtent The parameter to use for looking an element by horizontal spatial extent.
     * @throws IllegalArgumentException if the specified parameters are not one of those
     *         declared for {@link QueryType#SELECT}.
     */
    protected BoundedSingletonTable(final Query query, final Parameter[] pkParam,
            final Parameter byTimeRange, final Parameter bySpatialExtent)
    {
        super(query, pkParam);
        this.byTimeRange = byTimeRange;
        this.bySpatialExtent = bySpatialExtent;

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
     * Creates a new table connected to the same {@linkplain #getDatabase database} and using
     * the same {@linkplain #query query} than the specified table. Subclass constructors should
     * not modify the query, since it is shared.
     * <p>
     * In addition, the new table is initialized to the same spatio-temporal envelope and the
     * same {@linkplain #getSpatioTemporalCRS coordinate reference system} than the
     * specified table.
     *
     * @param table The table to use as a template.
     */
    protected BoundedSingletonTable(final BoundedSingletonTable<E> table) {
        super(table);
        byTimeRange     = table.byTimeRange;
        bySpatialExtent = table.bySpatialExtent;
        envelope        = table.envelope;
        trimmed         = table.trimmed;
        tMin            = table.tMin;
        tMax            = table.tMax;
        xMin            = table.xMin;
        xMax            = table.xMax;
        yMin            = table.yMin;
        yMax            = table.yMax;
        zMin            = table.zMin;
        zMax            = table.zMax;
        xResolution     = table.xResolution;
        yResolution     = table.yResolution;
    }

    /**
     * Returns the coordinate reference system used by {@code [get|set]Envelope} methods.
     * By default it contains the following dimensions, in this order:
     * <p>
     * <ul>
     *   <li>The longitude in decimal degrees relative to Greenwich meridian.</li>
     *   <li>The latitude in decimal degrees.</li>
     *   <li>Altitude in metres above the WGS 84 ellipsoid.</li>
     *   <li>Time in fractional days since epoch.</li>
     * </ul>
     *
     * @return The spatio-temporal CRS.
     */
    public final CoordinateReferenceSystem getSpatioTemporalCRS() {
        return ((SpatialDatabase) getDatabase()).spatioTemporalCRS;
    }

    /**
     * Returns the dimension in {@code spatioTemporalCS} of the first axis which is colinear
     * with the axes of {@code crs}. If none are found, return -1.
     */
    private static int dimensionColinearWith(final CoordinateSystem spatioTemporalCS, final SingleCRS crs) {
        return (crs != null) ? CRSUtilities.dimensionColinearWith(spatioTemporalCS, crs.getCoordinateSystem()) : -1;
    }

    /**
     * Returns the spatio-temporal envelope of the elements to be read by this table. The
     * {@linkplain Envelope#getCoordinateReferenceSystem() envelope CRS} is the one returned
     * by {@link #getSpatioTemporalCRS()}.
     * <p>
     * The default implementation creates an envelope from the informations returned by
     * {@link #getEnvelope2D()}, {@link #getVerticalRange()} and {@link #getTimeRange()}.
     *
     * @return The spatio-temporal envelope.
     * @throws SQLException if an error occured while reading the database.
     *
     * @see #getEnvelope2D()
     * @see #getVerticalRange()
     * @see #getTimeRange()
     * @see #trimEnvelope()
     */
    public synchronized GeneralEnvelope getEnvelope() throws SQLException {
        GeneralEnvelope envelope = this.envelope;
        if (envelope == null) {
            final SpatialDatabase database = (SpatialDatabase) getDatabase();
            final CoordinateReferenceSystem crs = getSpatioTemporalCRS();
            final CoordinateSystem cs = crs.getCoordinateSystem();
            envelope = new GeneralEnvelope(crs);
            int dim = dimensionColinearWith(cs, database.horizontalCRS);
            if (dim >= 0) {
                final Rectangle2D box = getEnvelope2D();
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
        return envelope.clone();
    }

    /**
     * Sets the spatio-temporal envelope of the elements to be read by this table.
     * Any element intercepting this envelope will be considered by next calls to
     * {@link #getEntries()}.
     * <p>
     * The default implementation delegates to {@link #setEnvelope2D}, {@link #setVerticalRange}
     * and {@link #setTimeRange}, applying a coordinate transformation if needed.
     *
     * @param  envelope The envelope, or {@code null} to reset full coverage.
     * @return {@code true} if the envelope changed as a result of this call, or
     *         {@code false} if the specified envelope is equals to the one already set.
     * @throws CatalogException if an error occured during the transformation or
     *         the envelope can not be set.
     */
    public synchronized boolean setEnvelope(Envelope envelope) throws CatalogException {
        if (envelope == null) {
            boolean changed;
            changed  = setEnvelope2D   (null);
            changed |= setVerticalRange(null);
            changed |= setTimeRange    (null);
            return changed;
        }
        final CoordinateReferenceSystem sourceCRS = envelope.getCoordinateReferenceSystem();
        final CoordinateReferenceSystem targetCRS = getSpatioTemporalCRS();
        if (sourceCRS != null && !equalsIgnoreMetadata(sourceCRS, targetCRS)) {
            final CoordinateOperationFactory factory = getCoordinateOperationFactory(true);
            final CoordinateOperation userToStandard;
            try {
                userToStandard = factory.createOperation(sourceCRS, targetCRS);
                envelope = transform(userToStandard, envelope);
            } catch (FactoryException exception) {
                throw new CatalogException(exception);
            } catch (TransformException exception) {
                throw new CatalogException(exception);
            }
        }
        boolean changed = false;
        final SpatialDatabase database = (SpatialDatabase) getDatabase();
        final CoordinateSystem cs = targetCRS.getCoordinateSystem();
        int dim = dimensionColinearWith(cs, database.horizontalCRS);
        if (dim >= 0) {
            changed |= setEnvelope2D(XRectangle2D.createFromExtremums(
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
     * Returns the horizontal bounding box of the elements to be read by this table. This bounding
     * box will not be greater than the box specified at the last call to {@link #setEnvelope2D
     * setEnvelope2D(...)}, but it may be smaller if {@link #trimEnvelope()} has been invoked.
     *
     * @return The bounding box of the elements to be read.
     *
     * @see #getVerticalRange()
     * @see #getTimeRange()
     * @see #getEnvelope()
     * @see #trimEnvelope()
     */
    public synchronized Rectangle2D getEnvelope2D() {
        return XRectangle2D.createFromExtremums(xMin, yMin, xMax, yMax);
    }

    /**
     * Sets the horizontal bounding box of the elements to be read by this table.
     *
     * @param  area The horizontal bounding box.
     * @return {@code true} if the bounding box changed as a result of this call, or
     *         {@code false} if the specified box is equals to the one already set.
     * @throws CatalogException If an error occured while setting the envelope.
     */
    public synchronized boolean setEnvelope2D(final Rectangle2D area) throws CatalogException {
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
     * Returns the vertical range of the elements to be read by this table.
     * This vertical range will not be greater than the box specified at the last call
     * to {@link #setVerticalRange setVerticalRange(...)}, but it may be smaller if
     * {@link #trimEnvelope()} has been invoked.
     *
     * @return The vertical range of the elements to be read.
     *
     * @see #getEnvelope2D()
     * @see #getTimeRange()
     * @see #getEnvelope()
     * @see #trimEnvelope()
     */
    public synchronized NumberRange<Double> getVerticalRange() {
        return NumberRange.create(zMin, zMax);
    }

    /**
     * Sets the vertical range of the elements to be read by this table.
     *
     * @param  range The vertical range, or {@code null} for full coverage.
     * @return {@code true} if the vertical range changed as a result of this call, or
     *         {@code false} if the specified range is equals to the one already set.
     * @throws CatalogException If an error occured while setting the envelope.
     */
    public final boolean setVerticalRange(final NumberRange<?> range) throws CatalogException {
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
     * Sets the vertical range of the elements to be read by this table.
     *
     * @param  minimum The minimal <var>z</var> value, inclusive.
     * @param  maximum The maximal <var>z</var> value, <strong>inclusive</strong>.
     * @return {@code true} if the vertical range changed as a result of this call, or
     *         {@code false} if the specified range is equals to the one already set.
     * @throws CatalogException If an error occured while setting the envelope.
     */
    public synchronized boolean setVerticalRange(final double minimum, final double maximum) throws CatalogException {
        boolean change;
        change  = (doubleToLongBits(zMin) != doubleToLongBits(zMin = minimum));
        change |= (doubleToLongBits(zMax) != doubleToLongBits(zMax = maximum));
        if (change) {
            fireStateChanged("VerticalRange");
        }
        return change;
    }

    /**
     * Returns the time range of the elements to be read by this table.
     * This time range will not be greater than the box specified at the last call
     * to {@link #setTimeRange setTimeRange(...)}, but it may be smaller if
     * {@link #trimEnvelope} has been invoked.
     *
     * @return The time range of the elements to be read.
     *
     * @see #getEnvelope2D()
     * @see #getVerticalRange()
     * @see #getEnvelope()
     * @see #trimEnvelope()
     */
    public synchronized DateRange getTimeRange() {
        return new DateRange((tMin != Long.MIN_VALUE) ? new Date(tMin) : null,
                             (tMax != Long.MAX_VALUE) ? new Date(tMax) : null);
    }

    /**
     * Sets the time range of the elements to be read by this table.
     *
     * @param  timeRange The time range.
     * @return {@code true} if the time range changed as a result of this call, or
     *         {@code false} if the specified range is equals to the one already set.
     * @throws CatalogException If an error occured while setting the envelope.
     */
    public final boolean setTimeRange(final DateRange timeRange) throws CatalogException {
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
     * @throws CatalogException If an error occured while setting the envelope.
     */
    public synchronized boolean setTimeRange(final Date startTime, final Date endTime) throws CatalogException {
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
     * The units are the same than for the {@linkplain #getEnvelope2D horizontal envelope}.
     *
     * @return The resolution, or {@code null} for the best resolution available.
     */
    public synchronized Dimension2D getPreferredResolution() {
        if (xResolution > 0 || yResolution > 0) {
            return new FloatDimension2D(xResolution, yResolution);
        } else {
            return null;
        }
    }

    /**
     * Sets the preferred in units of the {@linkplain #getEnvelope2D horizontal envelope}. This
     * is only an approximative hint, since there is no garantee that an image will be read with
     * that resolution. A null values means that the best available resolution should be used.
     *
     * @param  resolution The preferred geographic resolution, or {@code null} for best resolution.
     * @return {@code true} if the resolution changed as a result of this call, or
     *         {@code false} if the specified resolution is equals to the one already set.
     * @throws CatalogException If an error occured while setting the envelope.
     */
    public synchronized boolean setPreferredResolution(final Dimension2D resolution) throws CatalogException {
        float x,y;
        if (resolution != null) {
            x = (float) resolution.getWidth ();
            y = (float) resolution.getHeight();
            if (!(x >= 0)) x = 0; // '!' for catching NaN
            if (!(y >= 0)) y = 0;
        } else {
            x = 0;
            y = 0;
        }
        boolean change;
        change  = (xResolution != (xResolution = x));
        change |= (yResolution != (yResolution = y));
        if (change) {
            fireStateChanged("PreferredResolution");
        }
        return change;
    }

    /**
     * Shrinks the {@linkplain #getEnvelope() spatio-temporal envelope} to a smaller envelope
     * containing all the elements to be returned by this table.  This method iterates over
     * the elements that intercept the envelope specified by {@code setXXX(...)} methods.
     * Then the envelope is altered in such a way that the {@code getXXX(...)} method returns
     * an identical or smaller envelope intercepting the same set of elements.
     *
     * @throws SQLException if an error occured while reading the database.
     */
    public synchronized void trimEnvelope() throws SQLException {
        if (trimmed) {
            return;
        }
        final QueryType type = QueryType.BOUNDING_BOX;
        final int timeColumn = (byTimeRange     != null) ? byTimeRange    .column.indexOf(type) : 0;
        final int bboxColumn = (bySpatialExtent != null) ? bySpatialExtent.column.indexOf(type) : 0;
        if (timeColumn != 0 || bboxColumn != 0) synchronized (getLock()) {
            envelope = null; // Clears now in case of failure.
            final LocalCache.Stmt ce = getStatement(type);
            final PreparedStatement statement = ce.statement;
            final ResultSet results = statement.executeQuery();
            while (results.next()) { // Should contains only one record.
                if (timeColumn != 0) {
                    Date time;
                    final Calendar calendar = getCalendar();
                    time = results.getTimestamp(timeColumn, calendar);
                    if (time != null) {
                        tMin = max(tMin, time.getTime());
                    }
                    time = results.getTimestamp(timeColumn + 1, calendar);
                    if (time != null) {
                        tMax = min(tMax, time.getTime());
                    }
                }
                if (bboxColumn != 0) {
                    final String bbox = results.getString(bboxColumn);
                    if (bbox == null) {
                        continue;
                    }
                    final Envelope envelope;
                    try {
                        envelope = new GeneralEnvelope(bbox);
                    } catch (RuntimeException e) {
                        throw new IllegalRecordException(e, this, results, bboxColumn, null);
                    }
                    final int dimension = envelope.getDimension();
                    for (int i=0; i<dimension; i++) {
                        final double min = envelope.getMinimum(i);
                        final double max = envelope.getMaximum(i);
                        switch (i) {
                            case 0: if (min > xMin) xMin = min;
                                    if (max < xMax) xMax = max; break;
                            case 1: if (min > yMin) yMin = min;
                                    if (max < yMax) yMax = max; break;
                            case 2: if (min > zMin) zMin = min;
                                    if (max < zMax) zMax = max; break;
                            default: break; // Ignore extra dimensions, if any.
                        }
                    }
                }
            }
            results.close();
            ce.release();
            fireStateChanged("Envelope");
        }
        trimmed = true;
    }

    /**
     * Invoked automatically for a newly created statement or when this table
     * {@linkplain #fireStateChanged changed its state}. The default implementation
     * set the parameter values to the spatio-temporal bounding box.
     *
     * @param  type The query type (mat be {@code null}).
     * @param  statement The statement to configure (never {@code null}).
     * @throws SQLException if a SQL error occured while configuring the statement.
     */
    @Override
    protected void configure(final QueryType type, final PreparedStatement statement) throws SQLException {
        super.configure(type, statement);
        if (byTimeRange != null) {
            final int index = byTimeRange.indexOf(type);
            if (index != 0) {
                long min = tMin;
                long max = tMax;
                /*
                 * The default for minimum and maximum values are arbitrary, but we need to
                 * provide something. It seems that 'infinity' value doesn't work through JDBC.
                 *
                 * TODO: Revisit if we find some way to specify 'infinity' with future JDBC drivers.
                 */
                if (min == Long.MIN_VALUE) {
                    min = ((SpatialDatabase) getDatabase()).temporalCRS.getDatum().getOrigin().getTime();
                }
                if (max == Long.MAX_VALUE) {
                    max = System.currentTimeMillis();
                }
                final Calendar calendar = getCalendar();
                statement.setTimestamp(index,   new Timestamp(max), calendar);
                statement.setTimestamp(index+1, new Timestamp(min), calendar);
            }
        }
        if (bySpatialExtent != null) {
            final int index = bySpatialExtent.indexOf(type);
            if (index != 0) {
                final GeneralEnvelope envelope = new GeneralEnvelope(
                        new double[] {
                            xMin == NEGATIVE_INFINITY ? -DEFAULT_LIMIT : xMin,
                            yMin == NEGATIVE_INFINITY ? -DEFAULT_LIMIT : yMin, zMin},
                        new double[] {
                            xMax == POSITIVE_INFINITY ? +DEFAULT_LIMIT : xMax,
                            yMax == POSITIVE_INFINITY ? +DEFAULT_LIMIT : yMax, zMax});
                statement.setString(index, GeneralEnvelope.toPolygonString(envelope));
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void fireStateChanged(final String property) throws CatalogException {
        if (!property.equalsIgnoreCase("PreferredResolution")) {
            envelope = null;
            trimmed  = false;
        }
        super.fireStateChanged(property);
    }
}
