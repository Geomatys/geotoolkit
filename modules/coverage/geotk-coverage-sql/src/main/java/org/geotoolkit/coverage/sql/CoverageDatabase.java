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
import javax.sql.DataSource;

import org.geotoolkit.lang.ThreadSafe;
import org.geotoolkit.internal.sql.table.SpatialDatabase;


/**
 * A connection to a collection of coverages declared in a SQL database.
 * The connection to the database is specified by a {@link DataSource}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.10
 *
 * @since 3.10
 * @module
 */
@ThreadSafe(concurrent = true)
public class CoverageDatabase {
    /**
     * The object which will manage the connections to the database.
     */
    private final SpatialDatabase database;

    /**
     * Creates a new instance using the given data source.
     *
     * @param datasource The data source.
     * @param properties The configuration properties, or {@code null} if none.
     */
    public CoverageDatabase(final DataSource datasource, final Properties properties) {
        database = new SpatialDatabase(datasource, properties);
    }
}
