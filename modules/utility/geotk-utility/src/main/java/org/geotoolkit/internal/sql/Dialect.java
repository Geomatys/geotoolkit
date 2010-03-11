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

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.DatabaseMetaData;
import java.util.Enumeration;
import java.util.Locale;

import org.geotoolkit.util.XArrays;


/**
 * The SQL dialect used by a connection.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.10
 *
 * @since 3.00
 * @module
 */
public enum Dialect {
    /**
     * The database is presumed to use ANSI SQL syntax.
     */
    ANSI(null, null),

    /**
     * The database uses Derby syntax. This is ANSI, with some contraints that PostgreSQL
     * doesn't have (for example column with {@code UNIQUE} constraint must explicitly be
     * specified as {@code NOT NULL}).
     */
    DERBY("org.apache.derby.", "jdbc:derby:"),

    /**
     * The database uses HSQL syntax. This is ANSI, but does not allow {@code INSERT}
     * statements inserting many lines. It also have a {@code SHUTDOWN} command which
     * is specific to HSQLDB.
     *
     * @since 3.10
     */
    HSQL("org.hsqldb.", org.geotoolkit.internal.sql.HSQL.PROTOCOL),

    /**
     * The database uses PostgreSQL syntax. This is ANSI, but provided an a separated
     * enum because it allows a few additional commands like {@code VACUUM}.
     */
    POSTGRESQL("org.postgresql.", "jdbc:postgresql:"),

    /**
     * The database uses Access SQL syntax.
     */
    ACCESS(null, null);

    /**
     * The list of dialect to try to recognize. This is the list of all available dialects
     * minus the generic ones ({@link #ANSI}). The enum name must be part of the database
     * product name (ignoring case).
     */
    private static final Dialect[] SPECIFIC = XArrays.remove(values(), ANSI.ordinal(), 1);

    /**
     * The prefix of package name, or {@code null} if unknown.
     */
    private final String baseDriverName;

    /**
     * The base JDBC URL, or {@code null} if uknown.
     */
    private final String baseURL;

    /**
     * Creates a new dialect enum.
     */
    private Dialect(final String baseDriverName, final String baseURL) {
        this.baseDriverName = baseDriverName;
        this.baseURL = baseURL;
    }

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
            for (final Dialect candidate : SPECIFIC) {
                if (product.contains(candidate.name())) {
                    return candidate;
                }
            }
        }
        return ANSI;
    }

    /**
     * Returns the dialect for the given JDBC URL, or {@code null} if unknown.
     *
     * @param databaseURL The JDBC URL for which to get the dialect.
     * @return The dialect for the given JDBC URL.
     *
     * @since 3.10
     */
    public static Dialect forURL(final String databaseURL) {
        if (databaseURL != null) {
            for (final Dialect candidate : SPECIFIC) {
                final String baseURL = candidate.baseURL;
                if (baseURL != null && databaseURL.regionMatches(true, 0, baseURL, 0, baseURL.length())) {
                    return candidate;
                }
            }
        }
        return null;
    }

    /**
     * Returns {@code true} if the driver for this {@code Dialect} is found in the set
     * of {@linkplain DriverManager#getDrivers() registered drivers}. This method may
     * conservatively returns {@code false} if the registration state can not be determined.
     *
     * @return {@code true} if the driver for this {@code Dialect} is registered.
     *
     * @since 3.10
     */
    public boolean isDriverRegistered() {
        if (baseDriverName != null) {
            final Enumeration<Driver> drivers = DriverManager.getDrivers();
            while (drivers.hasMoreElements()) {
                final Driver drv = drivers.nextElement();
                if (drv.getClass().getName().startsWith(baseDriverName)) {
                    return true;
                }
            }
        }
        return false;
    }
}
