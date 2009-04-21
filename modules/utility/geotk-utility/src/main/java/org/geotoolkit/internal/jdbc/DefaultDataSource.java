/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.internal.jdbc;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.sql.DataSource;


/**
 * A data source which get the connection from a {@link DriverManager}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.0
 *
 * @since 3.0
 * @module
 */
public final class DefaultDataSource implements DataSource {
    /**
     * The URL to use for connecting to the database.
     */
    private final String url;

    /**
     * Creates a data source for the given URL.
     *
     * @param url The URL to use for connecting to the database.
     */
    public DefaultDataSource(final String url) {
        this.url = url;
    }

    /**
     * Delegates to {@link DriverManager}.
     *
     * @throws SQLException If the connection can not be etablished.
     */
    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url);
    }

    /**
     * Delegates to {@link DriverManager}.
     *
     * @throws SQLException If the connection can not be etablished.
     */
    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return DriverManager.getConnection(url, username, password);
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
}
