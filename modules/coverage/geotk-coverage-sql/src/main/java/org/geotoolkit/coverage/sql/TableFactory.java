/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010, Geomatys
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
import java.util.logging.Logger;
import javax.sql.DataSource;

import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.image.io.PaletteFactory;
import org.geotoolkit.internal.sql.table.TablePool;
import org.geotoolkit.internal.sql.table.SpatialDatabase;


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
            new TablePool<LayerTable>(this, LayerTable.class, new LayerTable[4]);

    /**
     * Pool of grid coverage tables.
     */
    final TablePool<GridCoverageTable> coverages =
            new TablePool<GridCoverageTable>(this, GridCoverageTable.class, new GridCoverageTable[4]);

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
     */
    public TableFactory(final TableFactory toCopy) {
        super(toCopy);
        paletteFactory = toCopy.paletteFactory;
    }

    /**
     * Creates a new instance using the provided data source and configuration properties.
     * A default Coordinate Reference System is used.
     *
     * @param  datasource The data source.
     * @param  properties The configuration properties, or {@code null} if none.
     */
    public TableFactory(final DataSource datasource, final Properties properties) {
        super(datasource, properties);
        paletteFactory = PaletteFactory.getDefault();
    }

    /**
     * Returns the logger to be used by every tables.
     */
    @Override
    protected Logger getLogger() {
        return LOGGER;
    }
}
