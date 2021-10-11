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

import java.util.Set;
import java.util.HashSet;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.LogRecord;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.DatabaseMetaData;
import javax.sql.DataSource;

import org.geotoolkit.resources.Loggings;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.logging.Logging;
import org.apache.sis.util.Classes;


/**
 * A data source which get the connection from a {@link DriverManager}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @author Guilhem Legal (Geomatys)
 * @version 3.18
 *
 * @since 3.00
 * @module
 */
public class DefaultDataSource implements DataSource {
    /**
     * The logger where to report the JDBC driver version. Note that the logger
     * name intentionally hides the {@code "internal"} part of the package name.
     */
    public static final Logger LOGGER = Logging.getLogger("org.geotoolkit.sql");

    /**
     * The driver names of the connection returned by {@code DefaultDataSource}.
     * This is used for logging purpose only.
     */
    private static final Set<String> DRIVERS = new HashSet<>();

    /**
     * The URL to use for connecting to the database.
     * This field can not be {@code null}.
     */
    public final String url;

    /**
     * Creates a data source for the given URL.
     *
     * @param url The URL to use for connecting to the database.
     */
    public DefaultDataSource(final String url) {
        ArgumentChecks.ensureNonNull("url", url);
        this.url = url;
    }

    /**
     * Logs the driver version if this is the first time we get a connection for that driver.
     */
    static Connection log(final Connection connection, final Class<?> source) throws SQLException {
        if (LOGGER.isLoggable(Level.CONFIG)) {
            final DatabaseMetaData metadata = connection.getMetaData();
            final String name = metadata.getDriverName();
            final boolean log;
            synchronized (DRIVERS) {
                log = DRIVERS.add(name);
            }
            if (log) {
                final LogRecord record = Loggings.format(Level.CONFIG, Loggings.Keys.JdbcDriverVersion_3,
                        name, metadata.getDriverMajorVersion(), metadata.getDriverMinorVersion());
                record.setLoggerName(LOGGER.getName());
                record.setSourceClassName(source.getName());
                record.setSourceMethodName("getConnection");
                LOGGER.log(record);
            }
        }
        return connection;
    }

    /**
     * Delegates to {@link DriverManager}.
     *
     * @throws SQLException If the connection can not be established.
     */
    @Override
    public Connection getConnection() throws SQLException {
        return log(DriverManager.getConnection(url), DefaultDataSource.class);
    }

    /**
     * Delegates to {@link DriverManager}.
     *
     * @throws SQLException If the connection can not be established.
     */
    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return log(DriverManager.getConnection(url, username, password), DefaultDataSource.class);
    }

    /**
     * Delegates to {@link DriverManager}.
     */
    @Override
    public PrintWriter getLogWriter() {
        return DriverManager.getLogWriter();
    }

    /**
     * Delegates to {@link DriverManager}. It is better to avoid
     * calling this method since it has a system-wide effect.
     */
    @Override
    public void setLogWriter(final PrintWriter out) {
        DriverManager.setLogWriter(out);
    }

    /**
     * Delegates to {@link DriverManager}.
     */
    @Override
    public int getLoginTimeout() {
        return DriverManager.getLoginTimeout();
    }

    /**
     * Delegates to {@link DriverManager}. It is better to avoid
     * calling this method since it has a system-wide effect.
     */
    @Override
    public void setLoginTimeout(final int seconds) {
        DriverManager.setLoginTimeout(seconds);
    }

    /**
     * Returns (@code false} in all cases, since this class is not a wrapper
     * (omitting {@code DriverManager}).
     */
    @Override
    public boolean isWrapperFor(Class<?> iface) {
        return false;
    }

    /**
     * Throws an exception in all cases, since this class is not a wrapper
     * (omitting {@code DriverManager}).
     *
     * @param <T> Ignored.
     */
    @Override
    public <T> T unwrap(final Class<T> iface) throws SQLException {
        throw new SQLException();
    }

    /**
     * Shutdown the database represented by this data source.
     *
     * @since 3.03
     */
    public void shutdown() {
        final Dialect dialect = Dialect.forURL(url);
        if (dialect != null) try {
            dialect.shutdown(null, url, false);
        } catch (SQLException e) {
            Logging.unexpectedException(LOGGER, DefaultDataSource.class, "shutdown", e);
        }
    }

    /**
     * Compares this data source with the given object for equality.
     *
     * @param other The object to compare with this data source.
     * @return {@code true} if both objects are equal.
     *
     * @since 3.18
     */
    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (other != null && other.getClass() == getClass()) {
            final DefaultDataSource that = (DefaultDataSource) other;
            return Objects.equals(this.url, that.url);
        }
        return false;
    }

    /**
     * Returns a hash code value for this data source.
     *
     * @return A hash code value for this data source.
     *
     * @since 3.18
     */
    @Override
    public int hashCode() {
        return url.hashCode() ^ 335483867;
    }

    /**
     * Returns a string representation of this data source.
     */
    @Override
    public String toString() {
        return Classes.getShortClassName(this) + "[\"" + url + "\"]";
    }

    /**
     * Returns the parent logger for this data source.
     *
     * @return the parent Logger for this data source
     */
    @Override
    public Logger getParentLogger() {
        return LOGGER;
    }
}
