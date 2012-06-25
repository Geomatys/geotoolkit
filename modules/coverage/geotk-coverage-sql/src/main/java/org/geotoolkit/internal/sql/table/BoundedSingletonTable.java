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
package org.geotoolkit.internal.sql.table;

import java.util.Date;
import java.util.Calendar;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLDataException;
import java.sql.PreparedStatement;
import java.awt.geom.Rectangle2D;

import org.geotoolkit.util.DateRange;
import org.geotoolkit.geometry.Envelopes;
import org.geotoolkit.geometry.Envelope2D;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.display.shape.XRectangle2D;
import org.geotoolkit.coverage.sql.CoverageEnvelope;
import org.geotoolkit.referencing.datum.DefaultTemporalDatum;


/**
 * Base class for tables with a {@code getEntry(...)} method restricted to the elements contained in
 * some spatio-temporal bounding box. The bounding box is defined either by a {@link CoverageEnvelope}
 * expressed in the {@linkplain CoverageEnvelope#getSpatioTemporalCRS CRS of this table}, or by a
 * combination of {@linkplain CoverageEnvelope#getHorizontalRange() horizontal envelope},
 * {@link #getVerticalRange() vertical range} and {@linkplain CoverageEnvelope#getTimeRange()
 * time range} expressed in standard CRS.
 *
 * {@section Convention}
 * For every envelopes or ranges used by this class, the lower and upper bounds are both inclusive.
 * This is done that way for consistency with the envelope computed by {@link #trimEnvelope()}.
 *
 * @param <E> The kind of entries to be created by this table.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.15
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
    private static final Rectangle2D DEFAULT_LIMIT =
            XRectangle2D.createFromExtremums(-1E+12, -1E+12, 1E+12, 1E+12);

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
     * The spatio-temporal extent of the query. Getter and setter methods can be invoked
     * directly on this instance.
     */
    public final CoverageEnvelope envelope;

    /**
     * {@code true} if the {@link #trimEnvelope} method already shrinked the
     * {@linkplain #getEnvelope spatio-temporal envelope} for this table.
     */
    private boolean trimmed;

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
        envelope = createEnvelope();
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
        envelope = createEnvelope();
    }

    /**
     * Invoked after constructor for initializing the {@link #envelope} field.
     *
     * @return An initially infinite coverage extent suitable for this table.
     */
    protected abstract CoverageEnvelope createEnvelope();

    /**
     * Shrinks the {@linkplain #getEnvelope() spatio-temporal envelope} to a smaller envelope
     * containing all the elements to be returned by this table.  This method iterates over
     * the elements that intercept the envelope specified by {@code setXXX(...)} methods.
     * Then the envelope is altered in such a way that the {@code getXXX(...)} method returns
     * an identical or smaller envelope intercepting the same set of elements.
     *
     * @throws SQLException if an error occurred while reading the database.
     */
    public void trimEnvelope() throws SQLException {
        if (trimmed) {
            return;
        }
        final QueryType type = QueryType.BOUNDING_BOX;
        final int timeColumn = (byTimeRange     != null) ? byTimeRange    .column.indexOf(type) : 0;
        final int bboxColumn = (bySpatialExtent != null) ? bySpatialExtent.column.indexOf(type) : 0;
        if (timeColumn != 0 || bboxColumn != 0) {
            final LocalCache lc = getLocalCache();
            synchronized (lc) {
                final LocalCache.Stmt ce = getStatement(lc, type);
                final PreparedStatement statement = ce.statement;
                final ResultSet results = statement.executeQuery();
                while (results.next()) { // Should contains only one record.
                    if (timeColumn != 0) {
                        final Calendar calendar = getCalendar(lc);
                        final Date tMin = results.getTimestamp(timeColumn,   calendar); // NOSONAR: timeColumn can't be 0.
                        final Date tMax = results.getTimestamp(timeColumn+1, calendar);
                        // Computes the intersection with the time range that we found.
                        Date t;
                        final DateRange range = envelope.getTimeRange();
                        if ((t = range.getMinValue()) != null && t.after (tMin)) tMin.setTime(t.getTime());
                        if ((t = range.getMaxValue()) != null && t.before(tMax)) tMax.setTime(t.getTime());
                        envelope.setTimeRange(tMin, tMax);
                    }
                    if (bboxColumn != 0) {
                        final String bbox = results.getString(bboxColumn); // NOSONAR: bboxColumn can't be 0.
                        if (bbox == null) {
                            continue;
                        }
                        final GeneralEnvelope ge;
                        try {
                            ge = new GeneralEnvelope(bbox);
                        } catch (RuntimeException e) {
                            throw new IllegalRecordException(e, this, results, bboxColumn, null);
                        }
                        final XRectangle2D region = (XRectangle2D) ge.toRectangle2D();
                        region.intersect(envelope.getHorizontalRange());
                        envelope.setHorizontalRange(region);
                    }
                }
                results.close();
                release(lc, ce);
                fireStateChanged("Envelope");
            }
        }
        trimmed = true;
    }

    /**
     * Invoked automatically for a newly created statement or when this table
     * {@linkplain #fireStateChanged changed its state}. The default implementation
     * set the parameter values to the spatio-temporal bounding box.
     *
     * @param  lc The {@link #getLocalCache()} value.
     * @param  type The query type (mat be {@code null}).
     * @param  statement The statement to configure (never {@code null}).
     * @throws SQLDataException If the {@linkplain #envelope} is invalid.
     * @throws SQLException If an other kind of SQL error occurred while configuring the statement.
     */
    @Override
    protected void configure(final LocalCache lc, final QueryType type, final PreparedStatement statement)
            throws SQLDataException, SQLException
    {
        super.configure(lc, type, statement);
        if (byTimeRange != null) {
            final int index = byTimeRange.indexOf(type);
            if (index != 0) {
                final DateRange range = envelope.getTimeRange();
                Date tMin = range.getMinValue();
                Date tMax = range.getMaxValue();
                /*
                 * The default for minimum and maximum values are arbitrary, but we need to
                 * provide something. It seems that 'infinity' value doesn't work through JDBC.
                 *
                 * TODO: Revisit if we find some way to specify 'infinity' with future JDBC drivers.
                 */
                if (tMin == null) {
                    tMin = DefaultTemporalDatum.JULIAN.getOrigin();
                }
                if (tMax == null) {
                    tMax = new Date();
                }
                final Calendar calendar = getCalendar(lc);
                statement.setTimestamp(index,   new Timestamp(tMax.getTime()), calendar);
                statement.setTimestamp(index+1, new Timestamp(tMin.getTime()), calendar);
            }
        }
        if (bySpatialExtent != null) {
            final int index = bySpatialExtent.indexOf(type);
            if (index != 0) {
                final Envelope2D env = new Envelope2D();
                Rectangle2D.intersect(envelope.getHorizontalRange(), DEFAULT_LIMIT, env);
                final String wkt;
                try {
                    wkt = Envelopes.toPolygonWKT(env);
                } catch (IllegalArgumentException e) {
                    throw new SQLDataException(e.getLocalizedMessage(), e);
                }
                statement.setString(index, wkt);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void fireStateChanged(final String property) {
        if (!"PreferredResolution".equals(property)) {
            trimmed = false;
        }
        super.fireStateChanged(property);
    }
}
