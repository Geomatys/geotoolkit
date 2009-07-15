/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2004-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.index.rtree.database;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * DOCUMENT ME!
 * 
 * @author Tommaso Nolli
 * @source $URL:
 *         http://svn.geotools.org/geotools/trunk/gt/modules/plugin/shapefile/src/main/java/org/geotools/index/rtree/database/Dialect.java $
 */
public interface Dialect {
    public String getCatalogQuery();

    public String getCatalogInsert();

    public String getCreateTable(String tableName);

    public int getNextPageId(Connection cnn, String tableName)
            throws SQLException;

    public String getSelectPage(String tableName);

    public String getInsertPage(String tableName);

    public String getUpdatePage(String tableName);
}
