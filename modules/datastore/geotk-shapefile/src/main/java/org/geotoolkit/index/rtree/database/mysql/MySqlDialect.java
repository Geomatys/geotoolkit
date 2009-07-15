/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
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
package org.geotoolkit.index.rtree.database.mysql;

import org.geotoolkit.index.rtree.database.AbstractDialect;

/**
 * DOCUMENT ME!
 * 
 * @author Tommaso Nolli
 * @source $URL:
 *         http://svn.geotools.org/geotools/trunk/gt/modules/plugin/shapefile/src/main/java/org/geotools/index/rtree/database/mysql/MySqlDialect.java $
 */
public class MySqlDialect extends AbstractDialect {
    /**
     * @see org.geotools.index.rtree.database.Dialect#getCreateTable(java.lang.String)
     */
    public String getCreateTable(String tableName) {
        return "create table " + tableName + "(" + "page_id int not null,"
                + "fl_leaf char(1) not null," + "blob_content blob";
    }
}
