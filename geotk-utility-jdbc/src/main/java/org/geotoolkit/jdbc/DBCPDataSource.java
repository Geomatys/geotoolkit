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

import java.sql.SQLException;

import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;
import org.apache.commons.dbcp.BasicDataSource;

/**
 * A closeable wrapper around {@link BasicDataSource}
 * @module
 */
public class DBCPDataSource extends AbstractManageableDataSource {

    public DBCPDataSource(final BasicDataSource wrapped) {
        super(wrapped);
    }

    @Override
    public void close() throws SQLException {
        ((BasicDataSource) wrapped).close();
    }

    @Override
    public boolean isWrapperFor(final Class type) throws SQLException {
        return false;
        //return this.wrapped.isWrapperFor(type);
    }

    @Override
    public Object unwrap(final Class type) throws SQLException {
        return null;
        //return this.wrapped.unwrap(type);
    }

    /**
     * Do not declare as @Override .
     * this method is only in JDK 7, declaring the override will break JDK 6 build.
     */
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException("Method getParentLogger will only be available when geotk will move on jdk 7.");
    }

}
