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

import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.geometry.MismatchedReferenceSystemException;

import org.geotoolkit.util.Utilities;
import org.geotoolkit.util.DateRange;
import org.geotoolkit.util.MeasurementRange;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.util.collection.FrequencySortedSet;
import org.geotoolkit.util.collection.UnmodifiableArrayList;
import org.geotoolkit.internal.sql.table.Entry;
import org.geotoolkit.internal.sql.table.TablePool;
import org.geotoolkit.internal.sql.table.SpatialDatabase;
import org.geotoolkit.internal.sql.table.NoSuchRecordException;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.resources.Errors;


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
     * This map will be created only when first needed.
     */
    private volatile Map<Integer,SeriesEntry> series;

    /**
     * How many coverages are found in each series, sorted by decreasing frequency.
     * This is computed when first needed.
     */
    private volatile FrequencySortedSet<SeriesEntry> countBySeries;

    /**
     * A fallback layer to be used if no image can be found for a given date in this layer.
     * May be {@code null} if there is no fallback.
     * <p>
     * Upon construction, this field contains only the layer name as a {@link String}.
     * This is converted to {@link LayerEntry} only when first needed.
     */
    private volatile Object fallback;

    /**
     * The typical resolution along each axis of the database CRS.
     * This is computed only when first needed.
     */
    private volatile double[] resolution;

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
     */
    @Override
    public String getName() {
        return identifier.toString();
    }

    /**
     * Returns the ressources bundle for error messages.
     */
    private Errors errors() {
        return Errors.getResources(layerTable != null ? layerTable.getLocale() : null);
    }

    /**
     * Returns the layer table, or thrown an exception if there is none.
     * The exception may happen if this entry has been deserialized.
     *
     * @return The layer table.
     * @throws IllegalStateException If this entry is not connected to a database.
     */
    private LayerTable getLayerTable() throws IllegalStateException {
        final LayerTable table = layerTable;
        if (table == null) {
            throw new IllegalStateException(errors().getString(Errors.Keys.NO_DATA_SOURCE));
        }
        return table;
    }

    /**
     * Returns the domain of this layer, or {@code null} if none.
     *
     * @throws SQLException If an error occured while fetching the domain.
     */
    private DomainOfLayerEntry getDomain() throws SQLException {
        DomainOfLayerEntry entry = domain;
        if (entry == null) {
            final String name = getName();
            final DomainOfLayerTable domains = getLayerTable().getDomainOfLayerTable();
            try {
                synchronized (domains) {
                    entry = domain;
                    if (entry == null) {
                        entry = domains.getEntry(name);
                        domain = entry;
                    }
                }
            } catch (NoSuchRecordException exception) {
                entry = DomainOfLayerEntry.NULL;
                Logging.recoverableException(LayerEntry.class, "getDomain", exception);
            }
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
    private Map<Integer,SeriesEntry> getSeriesMap() throws SQLException {
        Map<Integer,SeriesEntry> map = series;
        if (map == null) {
            final String name = getName();
            final SeriesTable st = getLayerTable().getSeriesTable();
            synchronized (st) {
                map = series;
                if (map == null) {
                    st.setLayer(name);
                    map = Collections.unmodifiableMap(st.getEntriesMap());
                    series = map;
                }
            }
        }
        return map;
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
        if (fb instanceof String) {
            final String name = (String) fallback;
            final LayerTable table = getLayerTable().getLayerTable();
            try {
                synchronized (table) {
                    fb = fallback;
                    if (fb == null) {
                        fb = table.getEntry(name);
                        fallback = fb;
                    }
                }
            } catch (SQLException e) {
                throw new CoverageStoreException(e);
            }
        }
        return (LayerEntry) fb;
    }

    /**
     * Returns the number of coverages in this layer.
     */
    @Override
    public int getCoverageCount() throws CoverageStoreException {
        final int[] count;
        try {
            count = getCountBySeries().frequencies();
        } catch (SQLException e) {
            throw new CoverageStoreException(e);
        }
        int n = 0;
        for (int i=0; i<count.length; i++) {
            n += count[i];
        }
        return n;
    }

    /**
     * Returns the number of coverages in each series. This method returns a direct
     * reference to the internal set - <strong>do not modify!</strong>.
     */
    final FrequencySortedSet<SeriesEntry> getCountBySeries() throws SQLException {
        FrequencySortedSet<SeriesEntry> count = countBySeries;
        if (count == null) {
            final Collection<SeriesEntry> allSeries = getSeries();
            final GridCoverageTable data = getLayerTable().getGridCoverageTable();
            synchronized (data) {
                count = countBySeries;
                if (count == null) {
                    count = new FrequencySortedSet<SeriesEntry>(true);
                    data.setLayerEntry(this);
                    for (final SeriesEntry series : allSeries) {
                        final Integer identifier = series.getIdentifier();
                        final Map<Integer,Integer> countBySeries;
                        data.userSeries = series;
                        try {
                            countBySeries = data.count(false); // Should get a singleton.
                        } finally {
                            data.userSeries = null;
                        }
                        final Integer n = countBySeries.get(identifier);
                        if (n != null) {
                            count.add(series, n);
                        }
                    }
                    countBySeries = count;
                }
            }
        }
        return count;
    }

    /**
     * Returns a time range encompassing all coverages in this layer, or {@code null} if none.
     *
     * @return The time range encompassing all coverages, or {@code null}.
     * @throws CoverageStoreException if an error occured while fetching the time range.
     */
    @Override
    public DateRange getTimeRange() throws CoverageStoreException {
        final DomainOfLayerEntry domain;
        try {
            domain = getDomain();
        } catch (SQLException e) {
            throw new CoverageStoreException(e);
        }
        return (domain != null) ? domain.timeRange : null;
    }

    /**
     * Returns the set of dates when a coverage is available.
     */
    @Override
    public SortedSet<Date> getAvailableTimes() throws CoverageStoreException {
        SortedSet<Date> available = availableTimes;
        if (available == null) try {
            final GridCoverageTable data = getLayerTable().getGridCoverageTable();
            synchronized (data) {
                available = availableTimes;
                if (available == null) {
                    data.setLayerEntry(this);
                    available = data.getAvailableTimes();
                    availableTimes = available;
                }
            }
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
        if (available == null) try {
            final GridCoverageTable data = getLayerTable().getGridCoverageTable();
            synchronized (data) {
                available = availableElevations;
                if (available == null) {
                    data.setLayerEntry(this);
                    available = data.getAvailableElevations();
                    availableElevations = available;
                }
            }
        } catch (SQLException e) {
            throw new CoverageStoreException(e);
        }
        return available;
    }

    /**
     * Returns the ranges of valid <cite>geophysics</cite> values for each band. If some
     * coverages found in this layer have different range of values, then this method
     * returns the union of their ranges.
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
                        } else if (!Arrays.equals(ranges, candidates)) {
                            final int length;
                            if (candidates.length <= ranges.length) {
                                length = candidates.length;
                            } else {
                                length = ranges.length;
                                ranges = Arrays.copyOf(ranges, candidates.length);
                                System.arraycopy(candidates, length, ranges, length, candidates.length - length);
                            }
                            for (int i=0; i<length; i++) {
                                ranges[i] = ranges[i].union(candidates[i]);
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
     * Returns the typical pixel resolution in this layer.
     * Values are in the unit of the main CRS used by the database (typically degrees
     * of longitude and latitude for the horizontal part, and days for the temporal part).
     * Some elements of the returned array may be {@link Double#NaN NaN} if they are unnkown.
     */
    @Override
    public double[] getTypicalResolution() throws CoverageStoreException {
        double[] resolution = this.resolution;
        if (resolution == null) {
            final SpatialDatabase database = (SpatialDatabase) getLayerTable().getDatabase();
            final CoordinateSystem cs = database.spatioTemporalCRS.getCoordinateSystem();
            final int xPos = CRSUtilities.dimensionColinearWith(cs, database.horizontalCRS.getCoordinateSystem());
            final int tPos = CRSUtilities.dimensionColinearWith(cs, database.temporalCRS  .getCoordinateSystem());
            final int dim  = cs.getDimension();
            resolution = new double[dim];
            Arrays.fill(resolution, Double.NaN);
            if (xPos >= 0) {
                final DomainOfLayerEntry domain;
                try {
                    domain = getDomain();
                } catch (SQLException e) {
                    throw new CoverageStoreException(e);
                }
                if (domain == null) {
                    return null;
                }
                final Dimension2D xyRes = domain.resolution;
                if (xyRes != null) {
                    resolution[xPos]   = xyRes.getWidth();
                    resolution[xPos+1] = xyRes.getHeight();
                }
            }
            if (tPos >= 0) {
                resolution[tPos] = timeInterval;
            }
            this.resolution = resolution;
        }
        return resolution.clone();
    }

    /**
     * Returns the image format used by the coverages in this layer.
     */
    @Override
    public SortedSet<String> getImageFormats() throws CoverageStoreException {
        final FrequencySortedSet<SeriesEntry> series;
        try {
            series = getCountBySeries();
        } catch (SQLException e) {
            throw new CoverageStoreException(e);
        }
        final int[] count = series.frequencies();
        final FrequencySortedSet<String> names = new FrequencySortedSet<String>();
        int i = 0;
        for (final SeriesEntry entry : series) {
            names.add(entry.format.imageFormat, count[i++]);
        }
        return names;
    }

    /**
     * Returns a reference to every coverages available in this layer which intersect the
     * given envelope.
     * <p>
     * <b>Implementation note:</b> this method casts {@code Set<GridCoverageEntry>} to
     * {@code Set<GridCoverageReference>}. This is okay if the {@code Set} is a generic
     * implementation like {@link java.util.LinkedHashSet} and this class does not keep
     * any reference to the returned set (so no {@code Set<GridCoverageEntry>} view
     * exist anymore).
     */
    @Override
    @SuppressWarnings({"unchecked","rawtypes"})
    public Set<GridCoverageReference> getCoverageReferences(final CoverageEnvelope envelope)
            throws CoverageStoreException
    {
        final Set<GridCoverageEntry> entries;
        try {
            final TablePool<GridCoverageTable> pool = getLayerTable().getTablePool();
            final GridCoverageTable data = pool.acquire();
            data.envelope.setAll(envelope);
            entries = data.getEntries();
            pool.release(data);
        } catch (SQLException exception) {
            throw new CoverageStoreException(exception);
        } catch (TransformException exception) {
            throw new MismatchedReferenceSystemException(errors()
                    .getString(Errors.Keys.ILLEGAL_COORDINATE_REFERENCE_SYSTEM, exception));
        }
        return (Set) entries; // See implementation note above.
    }

    /**
     * Returns a reference to a coverage that intersect the given envelope. If more than one
     * coverage intersect the given envelope, then this method will select the one which seem
     * the most repesentative.
     */
    @Override
    public GridCoverageReference getCoverageReference(final CoverageEnvelope envelope)
            throws CoverageStoreException
    {
        final GridCoverageEntry entry;
        try {
            final TablePool<GridCoverageTable> pool = getLayerTable().getTablePool();
            final GridCoverageTable data = pool.acquire();
            data.envelope.setAll(envelope);
            entry = data.getEntry();
            pool.release(data);
        } catch (SQLException exception) {
            throw new CoverageStoreException(exception);
        } catch (TransformException exception) {
            throw new MismatchedReferenceSystemException(errors()
                    .getString(Errors.Keys.ILLEGAL_COORDINATE_REFERENCE_SYSTEM, exception));
        }
        return entry;
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
     * Invoked before serialization in order to ensure that the elements for which
     * the computation was deferred are now computed.
     */
    private void writeObject(final ObjectOutputStream out) throws IOException {
        try {
            getDomain();
            getSeriesMap();
            getFallback();
            getCountBySeries();
            getTypicalResolution();
        } catch (Exception e) {
            final InvalidObjectException ex = new InvalidObjectException(e.getLocalizedMessage());
            ex.initCause(e);
            throw ex;
        }
        out.defaultWriteObject();
    }
}
