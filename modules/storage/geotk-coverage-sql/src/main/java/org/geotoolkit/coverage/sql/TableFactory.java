/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.LogRecord;
import javax.sql.DataSource;

import org.apache.sis.util.logging.Logging;
import org.geotoolkit.image.palette.PaletteFactory;
import org.geotoolkit.internal.sql.table.TablePool;
import org.geotoolkit.internal.sql.table.SpatialDatabase;
import org.geotoolkit.resources.Loggings;


/**
 * A {@link SpatialDatabase} with a few commonly-used table pooled.
 * Contains also a few additional factories, like {@link PaletteFactory}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.16
 *
 * @since 3.10 (derived from Seagis)
 * @module
 */
final class TableFactory extends SpatialDatabase {
    /**
     * The logger to be used by every tables.
     *
     * @since 3.16
     */
    private static final Logger LOGGER = Logging.getLogger(TableFactory.class);

    /**
     * Pool of layer tables.
     */
    final TablePool<LayerTable> layers =
            new TablePool<>(this, LayerTable.class, new LayerTable[4]);

    /**
     * Pool of grid coverage tables.
     */
    final TablePool<GridCoverageTable> coverages =
            new TablePool<>(this, GridCoverageTable.class, new GridCoverageTable[4]);

    /**
     * The factory for color palettes.
     *
     * @since 3.14
     */
    final PaletteFactory paletteFactory;

    /**
     * Creates a new instance using the same configuration than the given instance.
     * The new instance will have its own, initially empty, cache.
     *
     * @param toCopy The existing instance to copy.
     *
     * @see CoverageDatabase#flush()
     */
    public TableFactory(final TableFactory toCopy) {
        super(toCopy);
        paletteFactory = toCopy.paletteFactory;
        log("flush", Loggings.Keys.FlushCache_1);
    }

    /**
     * Creates a new instance using the provided data source and configuration properties.
     * A default Coordinate Reference System is used.
     * <p>
     * If the given properties contains only one entry, and the key for this entry is
     * {@value org.geotoolkit.internal.sql.table.ConfigurationKey#PARAMETERS}, then the
     * value will be used as {@link org.opengis.parameter.ParameterValueGroup}.
     *
     * @param  datasource The data source, or {@code null} for creating it from the URL.
     * @param  properties The configuration properties, or {@code null} if none.
     */
    public TableFactory(final DataSource datasource, final Properties properties) {
        super(datasource, properties);
        paletteFactory = PaletteFactory.getDefault();
        log("<init>", Loggings.Keys.CreatedObject_1);
    }

    /**
     * Logs this object construction. The creation of this {@code TableFactory} means either
     * that a {@link CoverageDatabase} has been instantiated, or its cache flushed.
     *
     * @param method The {@link CoverageDatabase} method which is causing (indirectly)
     *        this object construction.
     */
    private static void log(final String method, final short key) {
        if (LOGGER.isLoggable(Level.FINE)) {
            final LogRecord record = Loggings.format(Level.FINE, key, "CoverageDatabase");
            record.setSourceMethodName(method);
            record.setSourceClassName("org.geotoolkit.coverage.sql.CoverageDatabase");
            record.setLoggerName(LOGGER.getName());
            LOGGER.log(record);
        }
    }

    /**
     * Returns the logger to be used by every tables.
     */
    @Override
    protected Logger getLogger() {
        return LOGGER;
    }
}
