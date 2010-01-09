/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
package org.geotoolkit.internal.sql;

import java.sql.SQLException;
import java.sql.DatabaseMetaData;
import java.util.Locale;


/**
 * The SQL dialect used by a connection.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 * @module
 */
public enum Dialect {
    /**
     * The database is presumed to use ANSI SQL syntax.
     */
    ANSI,

    /**
     * The database uses Derby syntax. This is ANSI, with some contraints that PostgreSQL
     * doesn't have (for example column with {@code UNIQUE} constraint must explicitly be
     * specified as {@code NOT NULL}).
     */
    DERBY,

    /**
     * The database uses PostgreSQL syntax. This is ANSI, but provided an a separated
     * enum because it allows a few additional commands like {@code VACUUM}.
     */
    POSTGRESQL,

    /**
     * The database uses Access SQL syntax.
     */
    ACCESS;

    /**
     * Returns the presumed SQL dialect. Current implementation is very primitive
     * and try to guess only a few cases.
     *
     * @param  metadata The database metadata.
     * @return The presumed SQL syntax.
     * @throws SQLException if an error occured while querying the metadata.
     */
    public static Dialect guess(final DatabaseMetaData metadata) throws SQLException {
        String product = metadata.getDatabaseProductName();
        if (product != null) {
            product = product.trim().toUpperCase(Locale.US);
            if (product.contains("ACCESS")) {
                return ACCESS;
            }
            if (product.contains("POSTGRESQL")) {
                return POSTGRESQL;
            }
            if (product.contains("DERBY")) {
                return DERBY;
            }
        }
        return ANSI;
    }
}
