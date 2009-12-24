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
package org.geotoolkit.data.jdbc.datasource;

import java.sql.SQLException;

import org.apache.commons.dbcp.BasicDataSource;

/**
 * A closeable wrapper around {@link BasicDataSource}
 * @module pending
 */
public class DBCPDataSource extends AbstractManageableDataSource {

    public DBCPDataSource(BasicDataSource wrapped) {
        super(wrapped);
    }

    @Override
    public void close() throws SQLException {
        ((BasicDataSource) wrapped).close();
    }

    @Override
    public boolean isWrapperFor(Class type) throws SQLException {
        return false;
        //return this.wrapped.isWrapperFor(type);
    }

    @Override
    public Object unwrap(Class type) throws SQLException {
        return null;
        //return this.wrapped.unwrap(type);
    }
}
