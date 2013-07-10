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
package org.geotoolkit.internal.sql;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import javax.sql.DataSource;
import java.util.logging.Logger;


/**
 * A {@link DataSource} which delegate the execution of every methods to a {@linkplain #wrapped}
 * data source. If non-null username and password have been specified to the constructor, then
 * those authentification info will be used by the {@link #getConnection()} method.
 * <p>
 * If addition, this class can also set the read-only state of the connection. This is done in this
 * class because write permissions are sometime determined from the user name (at caller choice).
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.09
 *
 * @since 3.09
 * @module
 */
public final class AuthenticatedDataSource implements DataSource {
    /**
     * The wrapped data source on which to delegate the method calls.
     */
    public final DataSource wrapped;

    /**
     * The authentification info.
     */
    private final String username, password;

    /**
     * Whatever the connection shall be set to read-only mode,
     * or {@code null} if the mode should be left unchanged.
     */
    private final Boolean isReadOnly;

    /**
     * Creates a data source.
     *
     * @param datasource The original datasource to wrap.
     * @param username   The user name, or {@code null}.
     * @param password   The password, or {@code null}.
     * @param isReadOnly Whatever the connection shall be set to read-only mode,
     *                   or {@code null} if the mode should be left unchanged.
     */
    public AuthenticatedDataSource(DataSource datasource, String username, String password, Boolean isReadOnly) {
        this.wrapped    = datasource;
        this.username   = username;
        this.password   = password;
        this.isReadOnly = isReadOnly;
    }

    /**
     * Delegates to the wrapped data source. If the username and password
     * given to the constructor are non-null, then they will be used.
     *
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public Connection getConnection() throws SQLException {
        final Connection c;
        if (username != null && password != null) {
            c = wrapped.getConnection(username, password);
        } else {
            c = wrapped.getConnection();
        }
        if (isReadOnly != null) {
            c.setReadOnly(isReadOnly);
        }
        return c;
    }

    /**
     * Delegates to the wrapped data source using the given username and password.
     *
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        final Connection c = wrapped.getConnection(username, password);
        if (isReadOnly != null) {
            c.setReadOnly(isReadOnly);
        }
        return c;
    }

    /**
     * Delegates to the wrapped data source.
     *
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return wrapped.getLogWriter();
    }

    /**
     * Delegates to the wrapped data source.
     *
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public void setLogWriter(final PrintWriter out) throws SQLException {
        wrapped.setLogWriter(out);
    }

    /**
     * Delegates to the wrapped data source.
     *
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public int getLoginTimeout() throws SQLException {
        return wrapped.getLoginTimeout();
    }

    /**
     * Delegates to the wrapped data source.
     *
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public void setLoginTimeout(final int seconds) throws SQLException {
        wrapped.setLoginTimeout(seconds);
    }

    /**
     * Delegates to the wrapped data source.
     *
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return wrapped.isWrapperFor(iface);
    }

    /**
     * Delegates to the wrapped data source.
     *
     * @param <T> The type of the wrapped object.
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public <T> T unwrap(final Class<T> iface) throws SQLException {
        return wrapped.unwrap(iface);
    }

    /**
     * Delegates to the wrapped data source.
     *
     * @return the parent Logger for this data source
     * @throws SQLFeatureNotSupportedException if the data source does not use {@code java.util.logging}.
     */
    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return wrapped.getParentLogger();
    }
}
