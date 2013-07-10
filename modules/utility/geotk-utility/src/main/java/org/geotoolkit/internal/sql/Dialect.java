/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DatabaseMetaData;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.SQLNonTransientException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Locale;

import org.apache.sis.util.ArraysExt;
import org.apache.sis.util.ArgumentChecks;


/**
 * The SQL dialect used by a connection.
 * This class defines also a few driver-specific operations that can not (to our knowledge)
 * be inferred from the {@link DatabaseMetaData}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.00
 * @module
 */
public enum Dialect {
    /**
     * The database is presumed to use ANSI SQL syntax.
     */
    ANSI(null, null, null),

    /**
     * The database uses Derby syntax. This is ANSI, with some constraints that PostgreSQL
     * doesn't have (for example column with {@code UNIQUE} constraint must explicitly be
     * specified as {@code NOT NULL}).
     */
    DERBY("org.apache.derby.jdbc.EmbeddedDriver", "jdbc:derby:", new String[] {
            "directory", "memory", "classpath", "jar"})
    {
        /**
         * Shutdowns the Derby database using the given URL.
         */
        @Override
        public void shutdown(final Connection connection, String databaseURL, final boolean setReadOnly) throws SQLException {
            super.shutdown(connection, databaseURL, setReadOnly); // Close the connection.
            final int p = databaseURL.indexOf(';');
            if (p >= 0) {
                // Trim the parameters, especially ";create=true".
                databaseURL = databaseURL.substring(0, p);
            }
            databaseURL += ";shutdown=true";
            try {
                DriverManager.getConnection(databaseURL).close();
            } catch (SQLException e) {
                // This is the expected exception.
            }
        }

        /*
         * We do not implement supportsGrantStatement(DatabaseMetaData, CreateStatementType) for now.
         * However if we choose to do so in a future version, then this method should alway returns
         * 'false' for schemas, and 'true' for tables only if the "derby.database.sqlAuthorization"
         * property is set to 'TRUE'.
         */
    },

    /**
     * The database uses HSQL syntax. This is ANSI, but does not allow {@code INSERT}
     * statements inserting many lines. It also have a {@code SHUTDOWN} command which
     * is specific to HSQLDB.
     *
     * @since 3.10
     */
    HSQL("org.hsqldb.jdbcDriver", "jdbc:hsqldb:", new String[] {"file", "mem"}) {
        /**
         * HSQLDB accepts only one row per {@code INSERT} statement.
         */
        @Override
        public int maxRowsPerInsert(final DatabaseMetaData metadata) {
            return 1;
        }

        /**
         * The HSQL database supports the {@code "COMMIT"} statement.
         */
        @Override
        public boolean supportsCommitStatement(final DatabaseMetaData metadata) {
            return true;
        }

        /**
         * Shutdowns the HSQL database using the given connection if non-null, or using a new
         * connection created from the given URL otherwise. Note that is {@code setReadOnly}
         * is {@code true}, then {@code databaseURL} needs to be non-null.
         */
        @Override
        public void shutdown(Connection connection, final String databaseURL, final boolean setReadOnly) throws SQLException {
            if (connection == null) {
                ArgumentChecks.ensureNonNull("databaseURL", databaseURL);
                connection = DefaultDataSource.log(DriverManager.getConnection(databaseURL), Dialect.class);
            }
            try (Statement stmt = connection.createStatement()) {
                stmt.execute(setReadOnly ? "SHUTDOWN COMPACT" : "SHUTDOWN");
            } finally {
                connection.close();
            }
            if (setReadOnly) {
                final File path = getFile(databaseURL);
                if (path != null) try {
                    final File file = new File(path.getParentFile(), path.getName() + ".properties");
                    final Properties properties;
                    try (InputStream propertyIn = new FileInputStream(file)) {
                        properties = new Properties();
                        properties.load(propertyIn);
                    }
                    if (!"true".equals(properties.put("readonly", "true"))) {
                        try (OutputStream out = new FileOutputStream(file)) {
                            properties.store(out, "HSQL database configuration");
                        }
                    }
                } catch (IOException e) {
                    throw new SQLNonTransientException(e);
                }
            }
        }
    },

    /**
     * The database uses PostgreSQL syntax. This is ANSI, but provided an a separated
     * enum because it allows a few additional commands like {@code VACUUM}.
     * <p>
     * While enums were introduced in PostgreSQL 8.3, we require PostgreSQL 8.4
     * because we need the {@code CAST ... WITH INOUT} feature.
     */
    POSTGRESQL("org.postgresql.Driver", "jdbc:postgresql:", null) {
        @Override
        public boolean supportsEnumType(final DatabaseMetaData metadata) throws SQLException {
            final int version = metadata.getDatabaseMajorVersion();
            return (version == 8) ? metadata.getDatabaseMinorVersion() >= 4 : version >= 8;
        }

        @Override
        public boolean needsCreateLanguage(final DatabaseMetaData metadata) throws SQLException {
            return metadata.getDatabaseMajorVersion() < 9;
        }

        @Override
        public boolean supportsGrantStatement(final DatabaseMetaData metadata, final CreateStatementType type) {
            return true;
        }
    },

    /**
     * The database uses Oracle syntax. This is ANSI, but without {@code "AS"} keyword.
     *
     * @since 3.18
     */
    ORACLE("oracle.jdbc.driver.OracleDriver", "jdbc:oracle:", null),

    /**
     * The database uses Access SQL syntax.
     */
    ACCESS(null, null, null);

    /**
     * The list of dialect to try to recognize. This is the list of all available dialects
     * minus the generic ones ({@link #ANSI}). The enum name must be part of the database
     * product name (ignoring case).
     */
    private static final Dialect[] SPECIFIC = ArraysExt.remove(values(), ANSI.ordinal(), 1);

    /**
     * The driver class name, or {@code null} if unknown.
     */
    public final String driverClass;

    /**
     * The prefix of the JDBC URL, or {@code null} if unknown.
     * If non-null, this string begins with {@code "jdbc:"} and ends with {@code ":"}.
     */
    public final String protocol;

    /**
     * The sub-protocols, or {@code null} if none. If non-null, then the first protocol
     * shall be the file protocol, and the second protocol (if any) the memory protocol.
     * The strings shall not have training {@code ':'}.
     */
    private final String[] subProtocols;

    /**
     * Creates a new dialect enum.
     */
    private Dialect(final String driverClass, final String protocol, final String[] subProtocols) {
        this.driverClass  = driverClass;
        this.protocol     = protocol;
        this.subProtocols = subProtocols;
    }

    /**
     * Returns the presumed SQL dialect. Current implementation is very primitive
     * and try to guess only a few cases.
     *
     * @param  metadata The database metadata.
     * @return The presumed SQL syntax.
     * @throws SQLException if an error occurred while querying the metadata.
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
                final String baseURL = candidate.protocol;
                if (baseURL != null && databaseURL.regionMatches(true, 0, baseURL, 0, baseURL.length())) {
                    return candidate;
                }
            }
        }
        return null;
    }

    /**
     * Constructs the full path to a database in the given directory.
     * The {@linkplain File#getName() name} of the given {@code path} shall be the
     * database name (without extension), and the {@linkplain File#getParentFile() parent}
     * of the {@code path} shall be the directory where the database is saved.
     *
     * @param  path The path (without extension) to the database.
     * @return The URL.
     * @throws SQLException If the database doesn't support local database.
     */
    public final String createURL(final File path) throws SQLException {
        if (subProtocols == null) {
            throw new SQLException();
        }
        // We do not use File.toURI() because HSQL doesn't seem to
        // expect an encoded URL (e.g. "%20" instead of spaces).}
        final StringBuilder url = new StringBuilder(protocol).append(subProtocols[0]).append(':');
        final String p = path.getAbsolutePath().replace(File.separatorChar, '/');
        if (!p.startsWith("/")) {
            url.append('/');
        }
        return url.append(p).toString();
    }

    /**
     * Given a database URL, gets the path to the database.
     * This is the converse of {@link #createURL(File)}.
     *
     * @param  databaseURL The database URL.
     * @return The path, or {@code null} if the given URL is not recognized.
     */
    public final File getFile(final String databaseURL) {
        int offset = protocol.length();
        if (databaseURL != null && databaseURL.regionMatches(true, 0, protocol, 0, offset)) {
            if (subProtocols != null) {
                final int s = databaseURL.indexOf(':', offset);
                if (s >= 0) {
                    final String p = databaseURL.substring(offset, s);
                    for (int i=0; i<subProtocols.length; i++) {
                        if (p.equalsIgnoreCase(subProtocols[i])) {
                            if (i != 0) {
                                // As per 'subProtocols' javadoc, only the first sub-protocol
                                // is the file protocol. All other sub-protocols are ignored.
                                // In particular, the second sub-protocol is the "memory" one,
                                // which can not be mapped to a file.
                                return null;
                            }
                            offset = s + 1;
                        }
                    }
                }
            }
            return new File(databaseURL.substring(offset));
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
    public final boolean isDriverRegistered() {
        if (driverClass != null) {
            final int stop = driverClass.lastIndexOf('.')+1;
            final Enumeration<Driver> drivers = DriverManager.getDrivers();
            while (drivers.hasMoreElements()) {
                final Driver drv = drivers.nextElement();
                if (drv.getClass().getName().regionMatches(0, driverClass, 0, stop)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns {@code true} if the database supports enums. The default implementation
     * returns {@code false}, because enums are not a standard feature.
     *
     * @param  metadata The database metadata
     * @return {@code true} if the database supports enums.
     * @throws SQLException If an error occurred while querying the metadata.
     *
     * @since 3.14
     */
    public boolean supportsEnumType(final DatabaseMetaData metadata) throws SQLException {
        return false;
    }

    /**
     * Returns {@code true} if the database supports "{@code GRANT}" statements.
     *
     * @param  metadata The database metadata
     * @param  type The type of object for which to verify {@code GRANT} support.
     * @return {@code true} if the database supports {@code "GRANT"} statements.
     * @throws SQLException If an error occurred while querying the metadata.
     *
     * @since 3.20
     */
    public boolean supportsGrantStatement(final DatabaseMetaData metadata, final CreateStatementType type)
            throws SQLException
    {
        return false;
    }

    /**
     * Returns {@code true} if the database supports the {@code "COMMIT"} statements.
     *
     * @param  metadata The database metadata
     * @return {@code true} if the database supports {@code "COMMIT"} statement.
     * @throws SQLException If an error occurred while querying the metadata.
     *
     * @since 3.20
     */
    public boolean supportsCommitStatement(final DatabaseMetaData metadata) throws SQLException {
        return false;
    }

    /**
     * Returns the maximum number of rows allowed per {@code "INSERTS"} statement.
     * This method returns 1 if the database does not support multi-rows insertion.
     * For other database, this method returns an arbitrary "reasonable" value, since
     * attempts to insert too many rows with a single statement on Derby database cause
     * a {@link StackOverflowError}.
     *
     * @param  metadata The database metadata
     * @return Maximal number of rows per {@code "INSERT"} statements.
     * @throws SQLException If an error occurred while querying the metadata.
     *
     * @since 3.20
     */
    public int maxRowsPerInsert(final DatabaseMetaData metadata) throws SQLException {
        return 100; // Arbitrary value choosen from empirical trials.
    }

    /**
     * Returns {@code true} if the following instruction shall be executed (assuming that
     * the PostgreSQL {@code "plpgsql"} language is desired):
     *
     * {@code sql
     *   CREATE TRUSTED PROCEDURAL LANGUAGE 'plpgsql'
     *     HANDLER plpgsql_call_handler
     *     VALIDATOR plpgsql_validator;
     * }
     *
     * This method returns {@code true} only for PostgreSQL dialect on database prior
     * to version 9. Starting at version 9, the language is installed by default.
     *
     * @param  metadata The database metadata
     * @return {@code true} if the language shall be created explicitly.
     * @throws SQLException If an error occurred while querying the metadata.
     *
     * @since 3.16
     */
    public boolean needsCreateLanguage(final DatabaseMetaData metadata) throws SQLException {
        return false;
    }

    /**
     * Closes the given connection (if non-null) and shutdowns the database.
     * The boolean arguments specify some optional operations that can be applied before or after
     * the shutdown, and will be ignored by database that do not support those operations.
     *
     * @param  connection  The connection to use for shutting down the database, or {@code null} if unavailable.
     * @param  databaseURL The URL to the database, or {@code null} if unavailable.
     * @param  setReadOnly {@code true} for setting the database in read-only mode after shutdown.
     * @throws SQLException
     */
    public void shutdown(Connection connection, String databaseURL, boolean setReadOnly) throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }
}
