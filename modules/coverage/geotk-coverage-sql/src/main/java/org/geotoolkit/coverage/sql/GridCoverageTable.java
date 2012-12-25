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

import java.util.*;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.awt.geom.Dimension2D;
import java.io.IOException;

import org.geotoolkit.util.NumberRange;
import org.geotoolkit.util.collection.RangeSet;
import org.geotoolkit.util.collection.XCollections;
import org.geotoolkit.internal.sql.table.Database;
import org.geotoolkit.internal.sql.table.SpatialDatabase;
import org.geotoolkit.internal.sql.table.BoundedSingletonTable;
import org.geotoolkit.internal.sql.table.CatalogException;
import org.geotoolkit.internal.sql.table.NoSuchTableException;
import org.geotoolkit.internal.sql.table.LocalCache;
import org.geotoolkit.internal.sql.table.Parameter;
import org.geotoolkit.internal.sql.table.QueryType;
import org.geotoolkit.internal.UnmodifiableArraySortedSet;
import org.geotoolkit.image.io.mosaic.TileManager;
import org.geotoolkit.resources.Errors;

import static org.apache.sis.util.ArgumentChecks.ensureNonNull;


/**
 * Connection to a table of grid coverages. This table builds references in the form of
 * {@link GridCoverageReference} objects, which will defer the image loading until first
 * needed. A {@code GridCoverageTable} can produce a list of available image intercepting
 * a given {@linkplain #setEnvelope2D horizontal area} and {@linkplain #setTimeRange time range}.
 *
 * {@section Implementation note}
 * For proper working of this class, the SQL query must sort entries by end time. If this
 * condition is changed, then {@link GridCoverageEntry#equalsAsSQL} must be updated accordingly.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Sam Hiatt
 * @version 3.15
 *
 * @since 3.10 (derived from Seagis)
 * @module
 */
class GridCoverageTable extends BoundedSingletonTable<GridCoverageEntry> {
    /**
     * Amount of milliseconds in a day.
     */
    static final long MILLIS_IN_DAY = 24*60*60*1000L;

    /**
     * The currently selected layer, or {@code null} if not yet set.
     */
    private LayerEntry layer;

    /**
     * The currently selected series in the current layer, or {@code null} for auto-detect.
     * This field is usually {@code null} since the public API works on layer as a whole.
     * It is used only in a few cases where a caller needs to work on a specific series,
     * and by {@link WritableGridCoverageTable} which need to associate new entries to a
     * specific series.
     */
    SeriesEntry specificSeries;

    /**
     * The table of grid geometries. Will be created only when first needed.
     */
    private transient GridGeometryTable gridGeometryTable;

    /**
     * The table of tiles. Will be created only when first needed.
     */
    private transient TileTable tileTable;

    /**
     * Comparator for selecting the "best" image when more than one is available in
     * the spatio-temporal area of interest. Will be created only when first needed.
     */
    private transient Comparator<GridCoverageReference> comparator;

    /**
     * Constructs a new {@code GridCoverageTable}.
     *
     * {@section Implementation note}
     * This constructor actually expects an instance of {@link SpatialDatabase},
     * but we have to keep {@link Database} in the method signature because this
     * constructor is fetched by reflection.
     *
     * @param database The connection to the database.
     */
    public GridCoverageTable(final Database database) {
        this(new GridCoverageQuery((SpatialDatabase) database));
    }

    /**
     * Constructs a new {@code GridCoverageTable} from the specified query.
     */
    GridCoverageTable(final GridCoverageQuery query) {
        // Method createIdentifier(...) expect the parameters to be in exactly that order.
        super(query, new Parameter[] {query.bySeries, query.byFilename, query.byIndex},
                query.byStartTime, query.byHorizontalExtent);
    }

    /**
     * Creates a new instance having the same configuration than the given table.
     * This is a copy constructor used for obtaining a new instance to be used
     * concurrently with the original instance.
     *
     * @param table The table to use as a template.
     */
    GridCoverageTable(final GridCoverageTable table) {
        super(table);
    }

    /**
     * Returns a copy of this table. This is a copy constructor used for obtaining
     * a new instance to be used concurrently with the original instance.
     */
    @Override
    protected GridCoverageTable clone() {
        return new GridCoverageTable(this);
    }

    /**
     * Invoked after constructor for initializing the {@link #envelope} field.
     */
    @Override
    protected CoverageEnvelope createEnvelope() {
        return new CoverageEnvelope((SpatialDatabase) getDatabase()) {
            @Override void fireStateChanged(final String property) {
                super.fireStateChanged(property);
                GridCoverageTable.this.fireStateChanged(property);
            }
        };
    }

    /**
     * Sets the layer as a string.
     *
     * @param  layer The layer name.
     * @throws SQLException if the layer can not be set to the given value.
     */
    public final void setLayer(final String layer) throws SQLException {
        ensureNonNull("layer", layer);
        if (!layer.equals(getLayer())) {
            final LayerTable table = getDatabase().getTable(LayerTable.class);
            this.layer = table.getEntry(layer);
            table.release();
            fireStateChanged("Layer");
        }
    }

    /**
     * Returns the name of the current layer, or {@code null} if none.
     */
    public final String getLayer() {
        final LayerEntry layer = this.layer;
        return (layer != null) ? layer.getName() : null;
    }

    /**
     * Sets the layer as an entry.
     */
    final void setLayerEntry(final LayerEntry layer) {
        ensureNonNull("layer", layer);
        if (!layer.equals(this.layer)) {
            this.layer = layer;
            fireStateChanged("Layer");
        }
    }

    /**
     * Returns the layer for the coverages in this table.
     *
     * @param  required If {@code true}, then the layer is required to be non-null.
     * @throws CatalogException if the layer is not set and {@code required} is {@code true}.
     */
    final LayerEntry getLayerEntry(final boolean required) throws CatalogException {
        final LayerEntry layer = this.layer;
        if (layer == null && required) {
            throw new CatalogException(errors().getString(Errors.Keys.NO_LAYER_SPECIFIED));
        }
        return layer;
    }

    /**
     * Returns the currently selected series. If {@link #setSeries(SeriesEntry)} has been invoked,
     * the given series is returned. Otherwise if the current layer contains exactly one series,
     * then that series is returned since there is no ambiguity. Otherwise an exception is thrown.
     *
     * @return The series for the {@linkplain #getLayerEntry() current layer}.
     * @throws SQLException if no series can be inferred from the current layer.
     */
    private SeriesEntry getSeries() throws SQLException {
        if (specificSeries != null) {
            return specificSeries;
        }
        final Iterator<SeriesEntry> iterator = getLayerEntry(true).getSeries().iterator();
        if (iterator.hasNext()) {
            final SeriesEntry series = iterator.next();
            if (!iterator.hasNext()) {
                return series;
            }
        }
        throw new CatalogException(errors().getString(Errors.Keys.NO_SERIES_SPECIFIED));
    }

    /**
     * Returns the {@link GridGeometryTable} instance, creating it if needed. This method is not
     * private because it is also used by {@link LayerEntry#getCountByExtent()}, but this is okay
     * if used in the same thread than this {@code GridCoverageTable} instance.
     */
    final GridGeometryTable getGridGeometryTable() throws NoSuchTableException {
        GridGeometryTable table = gridGeometryTable;
        if (table == null) {
            gridGeometryTable = table = getDatabase().getTable(GridGeometryTable.class);
        }
        return table;
    }

    /**
     * Returns the {@link TileTable} instance, creating it if needed.
     */
    private TileTable getTileTable() throws NoSuchTableException {
        TileTable table = tileTable;
        if (table == null) {
            tileTable = table = getDatabase().getTable(TileTable.class);
        }
        return table;
    }

    /**
     * Returns the two-dimensional coverages that intercept the
     * {@linkplain #getEnvelope current spatio-temporal envelope}.
     *
     * @return List of coverages in the current envelope of interest.
     * @throws SQLException if an error occurred while reading the database.
     */
    @Override
    public final Set<GridCoverageEntry> getEntries() throws SQLException {
        final Dimension2D resolution = envelope.getPreferredResolution();
        final  Set<GridCoverageEntry> entries  = super.getEntries();
        final List<GridCoverageEntry> filtered = new ArrayList<>(entries.size());
loop:   for (final GridCoverageEntry newEntry : entries) {
            /*
             * If there is many entries with the same spatio-temporal envelope but different
             * resolution, keep the one with a resolution close to the requested one and
             * remove the other entries.
             */
            for (int i=filtered.size(); --i>=0;) {
                final GridCoverageEntry oldEntry = filtered.get(i);
                if (!oldEntry.equalsAsSQL(newEntry)) {
                    // Entries not equal according the "ORDER BY" clause.
                    break;
                }
                final GridCoverageEntry coarseResolution = oldEntry.selectCoarseResolution(newEntry);
                if (coarseResolution != null) {
                    // Two entries has the same spatio-temporal coordinates.
                    if (coarseResolution.hasEnoughResolution(resolution)) {
                        // The entry with the lowest resolution is enough.
                        filtered.set(i, coarseResolution);
                    } else if (coarseResolution == oldEntry) {
                        // No entry has enough resolution;
                        // keep the one with the finest resolution.
                        filtered.set(i, newEntry);
                    }
                    continue loop;
                }
            }
            filtered.add(newEntry);
        }
        entries.retainAll(filtered);
        return entries;
    }

    /**
     * Returns one of the two-dimensional coverages that intercept the {@linkplain #getEnvelope()
     * current spatio-temporal envelope}. If more than one coverage intercept the envelope (i.e.
     * if {@link #getEntries()} returns a set containing at least two elements), then a coverage
     * will be selected using the default {@link GridCoverageComparator}.
     *
     * @return A coverage intercepting the given envelope, or {@code null} if none.
     * @throws SQLException if an error occurred while reading the database.
     */
    public final GridCoverageEntry getEntry() throws SQLException {
        final Iterator<GridCoverageEntry> entries = getEntries().iterator();
        GridCoverageEntry best = null;
        if (entries.hasNext()) {
            best = entries.next();
            if (entries.hasNext()) {
                Comparator<GridCoverageReference> comparator = this.comparator;
                if (comparator == null) {
                    comparator = new GridCoverageComparator(envelope);
                    this.comparator = comparator;
                }
                do {
                    final GridCoverageEntry entry = entries.next();
                    if (comparator.compare(entry, best) <= -1) {
                        best = entry;
                    }
                } while (entries.hasNext());
            }
        }
        return best;
    }

    /**
     * Returns an element for the given identifier.
     *
     * @param identifier The filename as a {@link String} (in which case the series is
     *        the {@linkplain #getSeries() current one} and the image index is 1) or a
     *        {@link GridCoverageIdentifier} with all primary key values.
     */
    @Override
    public GridCoverageEntry getEntry(Comparable<?> identifier) throws SQLException {
        return super.getEntry(toGridCoverageIdentifier(identifier));
    }

    /**
     * Tests if the given entry exists. This method does not attempt to create
     * the entry and doesn't check if the entry is valid.
     *
     * @param identifier The filename as a {@link String} (in which case the series is
     *        the {@linkplain #getSeries() current one} and the image index is 1) or a
     *        {@link GridCoverageIdentifier} with all primary key values.
     */
    @Override
    public boolean exists(Comparable<?> identifier) throws SQLException {
        return super.exists(toGridCoverageIdentifier(identifier));
    }

    /**
     * Deletes the given entry.
     *
     * @param identifier The filename as a {@link String} (in which case the series is
     *        the {@linkplain #getSeries() current one} and the image index is 1) or a
     *        {@link GridCoverageIdentifier} with all primary key values.
     */
    @Override
    public int delete(Comparable<?> identifier) throws SQLException {
        return super.delete(toGridCoverageIdentifier(identifier));
    }

    /**
     * Returns the set of dates when a coverage is available. Only the images in
     * the currently {@linkplain #getEnvelope selected envelope} are considered.
     *
     * @return The set of dates.
     * @throws SQLException if an error occurred while reading the database.
     */
    public final SortedSet<Date> getAvailableTimes() throws SQLException {
        final Set<Long> dates = new HashSet<>();
        final GridCoverageQuery query = (GridCoverageQuery) super.query;
        final LocalCache lc = getLocalCache();
        synchronized (lc) {
            final LocalCache.Stmt ce = getStatement(lc, QueryType.AVAILABLE_DATA);
            final int startTimeIndex = indexOf(query.startTime);
            final int endTimeIndex   = indexOf(query.endTime);
            final Calendar calendar  = getCalendar(lc);
            final PreparedStatement statement = ce.statement;
            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    final Date startTime = results.getTimestamp(startTimeIndex, calendar);
                    final Date   endTime = results.getTimestamp(  endTimeIndex, calendar);
                    final long      time;
                    if (startTime != null) {
                        if (endTime != null) {
                            time = (startTime.getTime() + endTime.getTime()) >>> 1;
                        } else {
                            time = startTime.getTime();
                        }
                    } else if (endTime != null) {
                        time = endTime.getTime();
                    } else {
                        continue;
                    }
                    dates.add(time);
                }
            }
            release(lc, ce);
        }
        if (dates.isEmpty()) {
            return XCollections.emptySortedSet();
        }
        return new UnmodifiableArraySortedSet.Date(dates);
    }

    /**
     * Returns the range of date for available images.
     *
     * @param  addTo If non-null, the set where to add the time range of available coverages.
     * @return The time range of available coverages. This method returns {@code addTo} if it
     *         was non-null or a new object otherwise.
     * @throws SQLException if an error occurred while reading the database.
     */
    public final RangeSet<Date> getAvailableTimeRanges(RangeSet<Date> addTo) throws SQLException {
        final GridCoverageQuery query = (GridCoverageQuery) super.query;
        final int startTimeIndex = indexOf(query.startTime);
        final int   endTimeIndex = indexOf(query.endTime);
        long  lastEndTime = Long.MIN_VALUE;
        final LocalCache lc = getLocalCache();
        synchronized (lc) {
            final Calendar calendar = getCalendar(lc);
            final LocalCache.Stmt ce = getStatement(lc, QueryType.AVAILABLE_DATA);
            final PreparedStatement statement = ce.statement;
            try (ResultSet results = statement.executeQuery()) {
                final LayerEntry layer  = this.layer; // Don't use getLayerEntry() because we want to accept null.
                final long timeInterval = (layer != null) ? Math.round(layer.timeInterval * MILLIS_IN_DAY) : 0;
                if (addTo == null) {
                    addTo = new RangeSet<>(Date.class);
                }
                while (results.next()) {
                    final Date startTime = results.getTimestamp(startTimeIndex, calendar);
                    final Date   endTime = results.getTimestamp(  endTimeIndex, calendar);
                    if (startTime != null && endTime != null) {
                        final long lgEndTime = endTime.getTime();
                        final long checkTime = lgEndTime - timeInterval;
                        if (checkTime <= lastEndTime  &&  checkTime < startTime.getTime()) {
                            /*
                             * Use case: some layer may produce images every 24 hours, but declare
                             * a time range spaning only 12 hours for each image. We don't want to
                             * consider the 12 remaining hours as a hole in data availability. If
                             * the 'timeInterval' is set to "1 day", then we merge the time range
                             * of consecutive images.
                             */
                            startTime.setTime(checkTime);
                        }
                        lastEndTime = lgEndTime;
                        addTo.add(startTime, endTime);
                    }
                }
            }
            release(lc, ce);
        }
        return addTo;
    }

    /**
     * Configures the specified query. This method is invoked automatically after this table
     * {@linkplain #fireStateChanged changed its state}.
     *
     * @throws SQLException if a SQL error occurred while configuring the statement.
     */
    @Override
    protected final void configure(final LocalCache lc, final QueryType type, final PreparedStatement statement)
            throws SQLException
    {
        super.configure(lc, type, statement);
        final GridCoverageQuery query = (GridCoverageQuery) super.query;
        int index = query.byLayer.indexOf(type);
        if (index != 0) {
            statement.setString(index, getLayerEntry(true).getName());
        }
        index = query.bySeries.indexOf(type);
        if (index != 0) {
            final SeriesEntry series = getSeries();
            assert getLayerEntry(true).getSeries().contains(series) : series;
            statement.setInt(index, series.getIdentifier());
        }
    }

    /**
     * Creates an identifier for the current row in the given result set. This method expects
     * that {@code pkIndices} are for the "series", "filename" and "index" columns, in that
     * order. This order is determined by the constructor.
     */
    @Override
    protected final Comparable<?> createIdentifier(final ResultSet results, final int[] pkIndices)
            throws SQLException
    {
        if (pkIndices.length != 3) {
            /*
             * pkIndices.length should always be 3. If not, we have a bug. Invoking
             * the super-class method will intentionally throw an exception (unless
             * the length is 1, in which case the super-class can manage).
             */
            return super.createIdentifier(results, pkIndices);
        }
        final int    seriesID = results.getInt   (pkIndices[0]);
        final String filename = results.getString(pkIndices[1]);
        final short  index    = results.getShort (pkIndices[2]); // We expect 0 if null.
        /*
         * Gets the SeriesEntry in which this coverage is declared. The entry should be available
         * from the layer HashMap. If not, we will query the SeriesTable as a fallback, but there
         * is probably a bug (unless the table is queried immediately after the insertion of new
         * entries, and the LayerEntry has not been recreated from a refreshen LayerTable).
         */
        final LayerEntry layer = getLayerEntry(true);
        SeriesEntry series = layer.getSeries(seriesID);
        if (series == null) { // Should not happen, but be lenient if it happen anyway.
            final SeriesTable table = getDatabase().getTable(SeriesTable.class);
            table.setLayer(getLayer());
            series = table.getEntry(seriesID);
            table.release();
        }
        /*
         * We need to include the altitude in the identifier (since requests for different
         * altitude result in different coverages), but altitude is not an explicit column
         * in the 'GridCoverages' table. We need to compute it from the list of vertical
         * ordinate values.
         */
        final GridCoverageQuery query = (GridCoverageQuery) super.query;
        final int extent = results.getInt(indexOf(query.spatialExtent));
        final GridGeometryEntry geometry = getGridGeometryTable().getEntry(extent);
        final NumberRange<?> verticalRange = envelope.getVerticalRange();
        final double z = 0.5*(verticalRange.getMinimum() + verticalRange.getMaximum());
        return new GridCoverageIdentifier(series, filename, index,
                geometry.indexOfNearestAltitude(z), geometry);
    }

    /**
     * Creates an entry from the current row in the specified result set.
     *
     * @throws SQLException if an error occurred while reading the database.
     */
    @Override
    protected final GridCoverageEntry createEntry(final LocalCache lc, final ResultSet results, final Comparable<?> identifier)
            throws SQLException
    {
        final GridCoverageIdentifier id = (GridCoverageIdentifier) identifier;
        final Calendar calendar = getCalendar(lc);
        final GridCoverageQuery query = (GridCoverageQuery) super.query;
        final Timestamp startTime = results.getTimestamp(indexOf(query.startTime), calendar);
        final Timestamp endTime   = results.getTimestamp(indexOf(query.endTime),   calendar);
        /*
         * Complete the geometry if it was null. This may happen when toGridCoverageIdentifier
         * (below) is invoked, which usually don't happen in typical GridCoverageTable usage.
         */
        if (id.geometry == null) {
            id.geometry = getGridGeometryTable().getEntry(results.getInt(indexOf(query.spatialExtent)));
        }
        /*
         * If the layer is tiled, read the tiles.
         */
        final LayerEntry layer = getLayerEntry(true);
        Boolean isTiled = layer.isTiled;
        if (isTiled == null) {
            layer.isTiled = isTiled = getTileTable().exists(layer);
        }
        TileManager[] managers = null;
        if (isTiled) try {
            managers = getTileTable().getTiles(layer, startTime, endTime, id.geometry.getHorizontalSRID());
        } catch (IOException e) {
            throw new CatalogException(e);
        }
        return new GridCoverageEntry(id, startTime, endTime, managers, null);
    }

    /**
     * Converts the given identifier into an instance of {@link GridCoverageIdentifier}
     * if possible. Arbitrary values are used for unspecified parameters like image and
     * <var>z</var> index.
     * <p>
     * This method is invoked indirectly mostly for testing purpose. It is not expected
     * to be invoked in typical {@code GridCoverageTable} usage.
     *
     * @param  identifier The identifier, or {@code null}.
     * @return The identifier to use, or {@code null} if the given identifier was null.
     */
    private Comparable<?> toGridCoverageIdentifier(Comparable<?> identifier) throws SQLException {
        if (identifier instanceof CharSequence) {
            identifier = new GridCoverageIdentifier(getSeries(), identifier.toString(), (short) 1);
        }
        return identifier;
    }

    /**
     * Returns the number of coverages for the given series. The keys of the returned map are
     * the identifiers found for the current series. The values are the number of occurrences.
     *
     * @param  series The series for which to count
     * @param  groupByExtent {@code true} for grouping by extents.
     * @return The number of records by identifier.
     */
    final Map<Integer,Integer> count(final SeriesEntry series, final boolean groupByExtent) throws SQLException {
        final GridCoverageQuery query = (GridCoverageQuery) this.query;
        final Map<Integer,Integer> count = new HashMap<>();
        final LocalCache lc = getLocalCache();
        synchronized (lc) {
            final SeriesEntry oldSeries = specificSeries;
            specificSeries = series;
            try {
                final LocalCache.Stmt ce = getStatement(lc, QueryType.COUNT,
                        groupByExtent ? query.spatialExtent : query.series);
                final PreparedStatement stmt = ce.statement;
                try (ResultSet results = stmt.executeQuery()) {
                    while (results.next()) {
                        final Integer k = results.getInt(1);
                        final int     c = results.getInt(2);
                        final Integer p = count.put(k, c);
                        if (p != null) {
                            count.put(k, p+c); // Should not happen, but let be paranoiac.
                        }
                    }
                }
                release(lc, ce);
            } finally {
                specificSeries = oldSeries;
            }
        }
        return count;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void fireStateChanged(final String property) {
        if (!"PreferredResolution".equals(property)) {
            comparator = null;
        }
        super.fireStateChanged(property);
    }
}
