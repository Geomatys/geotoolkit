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

import java.util.Map;
import java.util.List;
import java.util.Arrays;
import java.util.Collections;
import java.sql.SQLException;
import java.awt.geom.Dimension2D;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.InvalidObjectException;

import org.geotoolkit.util.Utilities;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.util.MeasurementRange;
import org.geotoolkit.util.collection.UnmodifiableArrayList;
import org.geotoolkit.internal.sql.table.Entry;
import org.geotoolkit.internal.sql.table.CatalogException;
import org.geotoolkit.internal.sql.table.NoSuchRecordException;


/**
 * A layer of {@linkplain GridCoverage grid coverages} sharing common properties.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.10
 *
 * @since 3.10 (derived from Seagis)
 * @module
 */
final class LayerEntry extends Entry {
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
    private final double timeInterval;

    /**
     * The domain for this layer, or {@code null} if not yet computed.
     * Will be computed only when first needed
     */
    private DomainOfLayerEntry domain;

    /**
     * The series associated with their identifiers.
     * This map will be created only when first needed.
     */
    private Map<Integer,SeriesEntry> series;

    /**
     * A fallback layer to be used if no image can be found for a given date in this layer.
     * May be {@code null} if there is no fallback.
     * <p>
     * Upon construction, this field contains only the layer name as a {@link String}.
     * This is converted to {@link LayerEntry} only when first needed.
     */
    private Object fallback;

    /**
     * Caches the value returned by {@link #getSampleValueRanges()}.
     * Computed only when first needed.
     */
    private transient List<MeasurementRange<?>> sampleValueRanges;

    /**
     * Provides indirectly a connection to the database.
     * This is set to {@code null} when no longer needed.
     */
    private transient LayerTable table;

    /**
     * Creates a new layer.
     *
     * @param table        The table that created this entry.
     * @param name         The layer name.
     * @param timeInterval Typical time interval (in days) between images, or {@link Double#NaN} if unknown.
     * @param fallback     The layer on which to fallback, or {@code null} if none.
     * @param remarks      Optional remarks, or {@code null}.
     */
    LayerEntry(final LayerTable table, final String name, final double timeInterval,
            final String fallback, final String remarks)
    {
        super(name, remarks);
        this.timeInterval = timeInterval;
        this.table = table;
    }

    /**
     * Returns the name of this layer.
     *
     * @return The name of this layer.
     */
    public String getName() {
        return (String) identifier;
    }

    /**
     * Forgets the reference to the layer table if we don't need it anymore.
     */
    private void conditionalRelease() {
        assert Thread.holdsLock(this);
        if ((domain != null) && (series != null) && !(fallback instanceof String)) {
            table = null;
        }
    }

    /**
     * Returns the domain of this layer.
     *
     * @throws CatalogException If an error occured while fetching the domain.
     */
    private synchronized DomainOfLayerEntry getDomain() throws CatalogException {
        if (domain == null) try {
            final DomainOfLayerTable domains = table.getDomainOfLayerTable();
            try {
                domain = domains.getEntry(getName());
            } catch (NoSuchRecordException exception) {
                domain = DomainOfLayerEntry.NULL;
                Logging.recoverableException(LayerEntry.class, "getDomain", exception);
            }
            conditionalRelease();
        } catch (SQLException e) {
            throw new CatalogException(e);
        }
        return domain;
    }

    /**
     * Returns all series in this layer as (<var>identifier</var>, <var>series</var>) pairs.
     *
     * @throws CatalogException If an error occured while fetching the series.
     */
    public synchronized Map<Integer,SeriesEntry> getSeries() throws CatalogException {
        if (series == null) try {
            final SeriesTable st = table.getSeriesTable();
            st.setLayer(getName());
            series = Collections.unmodifiableMap(st.getEntriesMap());
            conditionalRelease();
        } catch (SQLException e) {
            throw new CatalogException(e);
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
     * @throws CatalogException If an error occured while fetching the fallback.
     */
    public synchronized LayerEntry getFallback() throws CatalogException {
        if (fallback instanceof String) try {
            fallback = table.getEntry((String) fallback);
            conditionalRelease();
        } catch (SQLException e) {
            throw new CatalogException(e);
        }
        return (LayerEntry) fallback;
    }

    /**
     * Returns the ranges of valid sample values for each band.
     * The ranges are always expressed in <cite>geophysics</cite> values.
     *
     * @return The range of valid sample values.
     * @throws CatalogException If an error occured while computing the ranges.
     */
    public synchronized List<MeasurementRange<?>> getSampleValueRanges() throws CatalogException {
        List<MeasurementRange<?>> sampleValueRanges = this.sampleValueRanges;
        if (sampleValueRanges == null) try {
            MeasurementRange<?>[] ranges = null;
            for (final SeriesEntry series : getSeries().values()) {
                final FormatEntry format = series.format;
                if (format != null) {
                    final MeasurementRange<Double>[] candidates = format.getSampleValueRanges();
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
            if (ranges != null) {
                sampleValueRanges = UnmodifiableArrayList.wrap(ranges);
            } else {
                sampleValueRanges = Collections.emptyList();
            }
            this.sampleValueRanges = sampleValueRanges;
        } catch (SQLException e) {
            throw new CatalogException(e);
        }
        return sampleValueRanges;
    }

    /**
     * Returns the typical pixel resolution in this layer, or {@code null} if unknown.
     * Values are degrees of longitude and latitude.
     *
     * @return The typical pixel resolution.
     * @throws CatalogException if an error occured while fetching the resolution.
     */
    public Dimension2D getTypicalResolution() throws CatalogException {
        final DomainOfLayerEntry domain = getDomain();
        if (domain != null) {
            final Dimension2D resolution = domain.resolution;
            if (resolution != null) {
                return (Dimension2D) resolution.clone();
            }
        }
        return null;
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
            getSeries();
            getFallback();
        } catch (Exception e) {
            final InvalidObjectException ex = new InvalidObjectException(e.getLocalizedMessage());
            ex.initCause(e);
            throw ex;
        }
        out.defaultWriteObject();
    }
}
