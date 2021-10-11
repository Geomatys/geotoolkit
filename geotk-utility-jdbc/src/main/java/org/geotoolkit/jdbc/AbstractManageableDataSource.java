/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.jdbc;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;


/**
 * An abstract wrapper created to ease the setup of a
 * {@link ManageableDataSource}, you just have to subclass and create a close
 * method
 *
 * @author Andrea Aime - TOPP
 *
 * @module
 */
public abstract class AbstractManageableDataSource implements ManageableDataSource {

    protected DataSource wrapped;

    public AbstractManageableDataSource(final DataSource wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return wrapped.getConnection();
    }

    @Override
    public Connection getConnection(final String username, final String password)
            throws SQLException {
        return wrapped.getConnection(username, password);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return wrapped.getLoginTimeout();
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return wrapped.getLogWriter();
    }

    @Override
    public void setLoginTimeout(final int seconds) throws SQLException {
        wrapped.setLoginTimeout(seconds);
    }

    @Override
    public void setLogWriter(final PrintWriter out) throws SQLException {
        wrapped.setLogWriter(out);
    }

    @Override
    public boolean isWrapperFor(final Class c) throws SQLException {
        return false;
    }

    @Override
    public Object unwrap(final Class arg0) throws SQLException {
        throw new SQLException("This implementation cannot unwrap anything");
    }
}
