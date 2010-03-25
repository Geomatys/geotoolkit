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
package org.geotoolkit.coverage.sql;

import java.util.Date;
import java.util.Set;
import java.util.SortedSet;
import java.util.Map;
import java.util.List;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.sql.SQLException;
import java.awt.geom.Dimension2D;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.InvalidObjectException;

import org.geotoolkit.util.Utilities;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.util.DateRange;
import org.geotoolkit.util.MeasurementRange;
import org.geotoolkit.util.collection.UnmodifiableArrayList;
import org.geotoolkit.internal.sql.table.Entry;
import org.geotoolkit.internal.sql.table.TablePool;
import org.geotoolkit.internal.sql.table.CatalogException;
import org.geotoolkit.internal.sql.table.NoSuchRecordException;
import org.geotoolkit.coverage.io.CoverageStoreException;


/**
 * A layer of {@linkplain GridCoverage grid coverages} sharing common properties.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.10
 *
 * @since 3.10 (derived from Seagis)
 * @module
 */
final class LayerEntry extends Entry implements Layer {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 5283559646740856038L;

    /**
     * Typical time interval (in days) between images, or {@link Double#NaN} if unknown.
     * For example a layer of weekly <cite>Sea Surface Temperature</cite> (SST) coverages
     * may set this field to 7, while a layer of mounthly SST coverage may set this field
     * to 30. The value is only approximative.
     */
    final double timeInterval;

    /**
     * The domain for this layer, or {@code null} if not yet computed.
     * Will be computed only when first needed, and is part of serialized data.
     */
    private volatile DomainOfLayerEntry domain;

    /**
     * The series associated with their identifiers.
     * This map will be created only when first needed, and is part of serialized data.
     */
    private Map<Integer,SeriesEntry> series;

    /**
     * A fallback layer to be used if no image can be found for a given date in this layer.
     * May be {@code null} if there is no fallback.
     * <p>
     * Upon construction, this field contains only the layer name as a {@link String}.
     * This is converted to {@link LayerEntry} only when first needed.
     */
    private volatile Object fallback;

    /**
     * The set of available dates. Will be computed by
     * {@link #getAvailableTimes()} when first needed.
     */
    private transient volatile SortedSet<Date> availableTimes;

    /**
     * The set of available altitudes. Will be computed by
     * {@link #getAvailableElevations()} when first needed.
     */
    private transient volatile SortedSet<Number> availableElevations;

    /**
     * Caches the value returned by {@link #getSampleValueRanges()}. Computed only when first
     * needed. This field doesn't need to be serialized since it can be recomputed from the
     * {@linkplain #series}.
     */
    private transient List<MeasurementRange<?>> sampleValueRanges;

    /**
     * The table which created this entry. This is used for fetching dependencies
     * like {@link DomainOfLayerTable} or {@link SeriesTable}.
     * <p>
     * This field is not serialized. It will be {@code null} on deserialization, which
     * imply that the various {@code getCoverageReference} methods will not be available.
     */
    private final transient LayerTable layerTable;

    /**
     * Creates a new layer.
     *
     * @param table        The table that created this entry.
     * @param name         The layer name.
     * @param timeInterval Typical time interval (in days) between images, or {@link Double#NaN} if unknown.
     * @param fallback     The layer on which to fallback, or {@code null} if none.
     * @param remarks      Optional remarks, or {@code null}.
     */
    LayerEntry(final LayerTable table, final Comparable<?> name, final double timeInterval,
            final String fallback, final String remarks)
    {
        super(name, remarks);
        this.timeInterval = timeInterval;
        this.layerTable = table;
    }

    /**
     * Returns the name of this layer.
     *
     * @return The name of this layer.
     */
    @Override
    public String getName() {
        return identifier.toString();
    }

    /**
     * Returns the domain of this layer.
     *
     * @throws SQLException If an error occured while fetching the domain.
     */
    private DomainOfLayerEntry getDomain() throws SQLException {
        DomainOfLayerEntry entry = domain;
        if (entry == null && layerTable != null) {
            final String name = getName();
            final DomainOfLayerTable domains = layerTable.getDomainOfLayerTable();
            try {
                synchronized (domains) {
                    entry = domains.getEntry(name);
                }
            } catch (NoSuchRecordException exception) {
                entry = DomainOfLayerEntry.NULL;
                Logging.recoverableException(LayerEntry.class, "getDomain", exception);
            }
            domain = entry;
        }
        return entry;
    }

    /**
     * Returns all series in this layer.
     *
     * @throws SQLException If an error occured while fetching the series.
     */
    final Collection<SeriesEntry> getSeries() throws SQLException {
        return getSeriesMap().values();
    }

    /**
     * Returns the series for the given identifier, or {@code null} if none.
     *
     * @param  name The series identifier.
     * @return The series in this layer for the given identifier, or {@code null} if none.
     * @throws SQLException If an error occured while fetching the series.
     */
    final SeriesEntry getSeries(int identifier) throws SQLException {
        return getSeriesMap().get(identifier);
    }

    /**
     * Returns all series in this layer as (<var>identifier</var>, <var>series</var>) pairs.
     *
     * @throws SQLException If an error occured while fetching the series.
     */
    final synchronized Map<Integer,SeriesEntry> getSeriesMap() throws SQLException {
        if (series == null && layerTable != null) {
            final String name = getName();
            final SeriesTable st = layerTable.getSeriesTable();
            final Map<Integer,SeriesEntry> map;
            synchronized (st) {
                st.setLayer(name);
                map = st.getEntriesMap();
            }
            series = Collections.unmodifiableMap(map);
        }
        return series;
    }

    /**
     * A layer to use as a fallback if no data is available in this layer for a given position. For
     * example if no data is available in a weekly averaged <cite>Sea Surface Temperature</cite>
     * (SST) coverage because a location is masked by clouds, we may want to look in the mounthly
     * averaged SST coverage as a fallback.
     *
     * @return The fallback layer, or {@code null} if none.
     * @throws CoverageStoreException If an error occured while fetching the fallback.
     */
    @Override
    public LayerEntry getFallback() throws CoverageStoreException {
        Object fb = fallback;
        if (fb instanceof String && layerTable != null) {
            final String name = (String) fallback;
            final LayerTable table = layerTable.getLayerTable();
            try {
                synchronized (table) {
                    fb = table.getEntry(name);
                }
            } catch (SQLException e) {
                throw new CoverageStoreException(e);
            }
            fallback = fb;
        }
        return (LayerEntry) fb;
    }

    /**
     * Returns a time range encompassing all coverages in this layer, or {@code null} if none.
     *
     * @return The time range encompassing all coverages, or {@code null}.
     * @throws CoverageStoreException if an error occured while fetching the time range.
     */
    @Override
    public DateRange getTimeRange() throws CoverageStoreException {
        try {
            return getDomain().timeRange;
        } catch (SQLException e) {
            throw new CoverageStoreException(e);
        }
    }

    /**
     * Returns the set of dates when a coverage is available.
     */
    @Override
    public SortedSet<Date> getAvailableTimes() throws CoverageStoreException {
        SortedSet<Date> available = availableTimes;
        if (available == null && layerTable != null) try {
            final GridCoverageTable data = layerTable.getGridCoverageTable();
            synchronized (data) {
                data.setLayerEntry(this);
                available = data.getAvailableTimes();
            }
            availableTimes = available;
        } catch (SQLException e) {
            throw new CoverageStoreException(e);
        }
        return available;
    }

    /**
     * Returns the set of altitudes where a coverage is available.
     */
    @Override
    public SortedSet<Number> getAvailableElevations() throws CoverageStoreException {
        SortedSet<Number> available = availableElevations;
        if (available == null && layerTable != null) try {
            final GridCoverageTable data = layerTable.getGridCoverageTable();
            synchronized (data) {
                data.setLayerEntry(this);
                available = data.getAvailableElevations();
            }
            availableElevations = available;
        } catch (SQLException e) {
            throw new CoverageStoreException(e);
        }
        return available;
    }

    /**
     * Returns the ranges of valid <cite>geophysics</cite> values for each band.
     *
     * @return The range of valid sample values.
     * @throws CoverageStoreException If an error occured while computing the ranges.
     */
    @Override
    public synchronized List<MeasurementRange<?>> getSampleValueRanges() throws CoverageStoreException {
        List<MeasurementRange<?>> sampleValueRanges = this.sampleValueRanges;
        if (sampleValueRanges == null) try {
            MeasurementRange<?>[] ranges = null;
            for (final SeriesEntry series : getSeries()) {
                final FormatEntry format = series.format;
                if (format != null) {
                    final MeasurementRange<Double>[] candidates = format.getSampleValueRanges();
                    if (candidates != null) {
                        if (ranges == null) {
                            ranges = candidates;
                        } else {
                            final int length;
                            if (candidates.length <= ranges.length) {
                                length = candidates.length;
                            } else {
                                length = ranges.length;
                                ranges = Arrays.copyOf(ranges, candidates.length);
                                System.arraycopy(candidates, length, ranges, length, candidates.length - length);
                            }
                            for (int i=0; i<length; i++) {
                                ranges[i] = ranges[i].intersect(candidates[i]);
                            }
                        }
                    }
                }
            }
            if (ranges != null) {
                sampleValueRanges = UnmodifiableArrayList.wrap(ranges);
            } else {
                sampleValueRanges = Collections.emptyList();
            }
            this.sampleValueRanges = sampleValueRanges;
        } catch (SQLException e) {
            throw new CoverageStoreException(e);
        }
        return sampleValueRanges;
    }

    /**
     * Returns the typical pixel resolution in this layer, or {@code null} if unknown.
     * Values are in the unit of the main CRS used by the database (typically degrees
     * of longitude and latitude).
     *
     * @return The typical pixel resolution.
     * @throws CatalogException if an error occured while fetching the resolution.
     */
    @Override
    public double[] getTypicalResolution() throws CoverageStoreException {
        final DomainOfLayerEntry domain;
        try {
            domain = getDomain();
        } catch (SQLException e) {
            throw new CoverageStoreException(e);
        }
        if (domain != null) {
            final Dimension2D resolution = domain.resolution;
            if (resolution != null) {
                return new double[] {resolution.getWidth(), resolution.getHeight()};
            }
        }
        return null;
    }

    /**
     * Returns a reference to a coverage for the given date and elevation.
     */
    @Override
    public GridCoverageReference getCoverageReference(final Date time, final Number elevation)
            throws CoverageStoreException
    {
        if (layerTable == null) {
            return null;
        }
        long delay = Math.round(timeInterval * (GridCoverageTable.MILLIS_IN_DAY / 2));
        if (delay <= 0) {
            delay = GridCoverageTable.MILLIS_IN_DAY / 2;
        }
        Date startTime, endTime;
        if (time != null) {
            final long t = time.getTime();
            startTime = new Date(t - delay);
            endTime   = new Date(t + delay);
        } else {
            startTime = null;
            endTime   = null;
        }
        final double zmin = (elevation != null) ? elevation.doubleValue() : 0;
        final double zmax = zmin; // TODO: choose a better range.
        final TablePool<GridCoverageTable> pool = layerTable.getTablePool();
        final GridCoverageEntry entry;
        try {
            final GridCoverageTable data = pool.acquire();
            data.setLayerEntry(this);
            data.setEnvelope2D(null);
            data.setVerticalRange(zmin, zmax);
            data.setTimeRange(startTime, endTime);
            entry = data.getEntry();
            pool.release(data);
        } catch (SQLException exception) {
            throw new CoverageStoreException(exception);
        }
        return entry;
    }

    /**
     * Returns a reference to every coverages available in this layer.
     * <p>
     * <b>Implementation note:</b> this method casts {@code Set<GridCoverageEntry>} to
     * {@code Set<GridCoverageReference>}. This is okay if the {@code Set} is a generic
     * implementation like {@link java.util.LinkedHashSet} and this class does not keep
     * any reference to the returned set (so no {@code Set<GridCoverageEntry>} view
     * exist anymore).
     */
    @Override
    @SuppressWarnings({"unchecked","rawtypes"})
    public Set<GridCoverageReference> getCoverageReferences() throws CoverageStoreException {
        if (layerTable != null) try {
            final GridCoverageTable data = layerTable.getGridCoverageTable();
            synchronized (data) {
                return (Set) data.getEntries(); // See implementation note above.
            }
        } catch (SQLException exception) {
            throw new CoverageStoreException(exception);
        } else {
            return Collections.emptySet();
        }
    }

    /**
     * Returns a reference to a single "typical" coverages available in this layer.
     */
    @Override
    public GridCoverageReference getCoverageReference() throws CoverageStoreException {
        if (layerTable != null) try {
            final GridCoverageTable data = layerTable.getGridCoverageTable();
            synchronized (data) {
                return data.getEntry();
            }
        } catch (SQLException exception) {
            throw new CoverageStoreException(exception);
        } else {
            return null;
        }
    }

    /**
     * Compares this layer with the specified object for equality.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (super.equals(object)) {
            final LayerEntry that = (LayerEntry) object;
            return Utilities.equals(this.timeInterval, that.timeInterval);
            /*
             * Do not test costly fields like 'fallback'.
             */
        }
        return false;
    }

    /**
     * Invoked before serialization in order to ensure that we will serialize the
     * series entries, not the database.
     */
    private void writeObject(final ObjectOutputStream out) throws IOException {
        try {
            getDomain();
            getSeriesMap();
            getFallback();
        } catch (Exception e) {
            final InvalidObjectException ex = new InvalidObjectException(e.getLocalizedMessage());
            ex.initCause(e);
            throw ex;
        }
        out.defaultWriteObject();
    }
}
