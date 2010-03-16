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
import java.util.Collections;
import java.sql.SQLException;
import java.awt.geom.Dimension2D;

import org.geotoolkit.util.Utilities;
import org.geotoolkit.internal.sql.table.Entry;
import org.geotoolkit.internal.sql.table.CatalogException;


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
     */
    private final double timeInterval;

    /**
     * The domain for this layer, or {@code null} if not yet computed.
     * May be shared by many instances of {@code LayerEntry}.
     */
    private DomainOfLayerEntry domain;

    /**
     * The series associated with their identifiers.
     * This map will be created only when first needed.
     */
    private Map<Integer,SeriesEntry> series;

    /**
     * Provides indirectly a connection to the database.
     * This is set to {@code null} when no longer needed.
     */
    private transient SeriesTable table;

    /**
     * A fallback layer to be used if no image can be found for a given date in this layer.
     * May be {@code null} if there is no fallback.
     * <p>
     * Upon construction, this field contains only the layer name as a {@link String}.
     * This field is initialized only when first needed.
     */
    Object fallback;

    /**
     * Creates a new layer.
     *
     * @param name         The layer name.
     * @param timeInterval Typical time interval (in days) between images, or {@link Double#NaN} if unknown.
     * @param remarks      Optional remarks, or {@code null}.
     */
    protected LayerEntry(final String name, final double timeInterval, final String remarks) {
        super(name, remarks);
        this.timeInterval = timeInterval;
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
     * Returns all series in this layer as (<var>identifier</var>, <var>series</var>) pairs.
     */
    public synchronized Map<Integer,SeriesEntry> series() throws CatalogException {
        if (series == null) {
            if (table != null) {
                table.setLayer(getName());
                try {
                    series = Collections.unmodifiableMap(table.getEntriesMap());
                } catch (SQLException e) {
                    throw new CatalogException(e);
                }
                table = null;
            } else {
                series = Collections.emptyMap();
            }
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
     *
     * @todo Initialize when first needed.
     */
    public LayerEntry getFallback() {
        final Object fallback = this.fallback; // Protect from changes in concurrent threads.
        return (fallback instanceof LayerEntry) ? (LayerEntry) fallback : null;
    }

    /**
     * Returns the typical pixel resolution in this layer, or {@code null} if unknown.
     * Values are degrees of longitude and latitude.
     *
     * @return The typical pixel resolution.
     * @throws CatalogException if an error occured while fetching the resolution.
     */
    public Dimension2D getTypicalResolution() throws CatalogException {
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
}
