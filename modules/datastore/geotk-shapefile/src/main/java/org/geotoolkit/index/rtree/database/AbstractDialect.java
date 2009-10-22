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
package org.geotoolkit.index.rtree.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DOCUMENT ME!
 * 
 * @author Tommaso Nolli
 * @module pending
 */
public abstract class AbstractDialect implements Dialect {
    protected static final String CAT_TABLE = "rtrees_cat";
    private static final String QUERY_CAT = "select * from " + CAT_TABLE
            + " where rtree_name=?";
    private static final String INSERT_CAT = "insert into " + CAT_TABLE
            + " (rtree_name, min_entries, max_entries, split_alg)"
            + " values (?, ?, ?, ?)";
    private int current = -1;

    /**
     * @see org.geotools.index.rtree.database.Dialect#getCatalogQuery()
     */
    public String getCatalogQuery() {
        return QUERY_CAT;
    }

    /**
     * @see org.geotools.index.rtree.database.Dialect#getCatalogInsert()
     */
    public String getCatalogInsert() {
        return INSERT_CAT;
    }

    /**
     * This implementation works only in one JVM
     * 
     * @see org.geotools.index.rtree.database.Dialect#getNextPageId(java.sql.Connection,
     *      java.lang.String)
     */
    public synchronized int getNextPageId(Connection cnn, String tableName)
            throws SQLException {
        if (current == -1) {
            PreparedStatement pst = null;
            ResultSet rs = null;

            try {
                pst = cnn.prepareStatement("select max(page_id) from "
                        + tableName);
                rs = pst.executeQuery();
                rs.next();

                this.current = rs.getInt(1);
            } finally {
                try {
                    rs.close();
                } catch (Exception ee) {
                }

                try {
                    pst.close();
                } catch (Exception ee) {
                }
            }
        }

        return ++this.current;
    }

    /**
     * @see org.geotools.index.rtree.database.Dialect#getSelectPage(java.lang.String)
     */
    public String getSelectPage(String tableName) {
        return "select * from " + tableName + " where page_id=?";
    }

    /**
     * @see org.geotools.index.rtree.database.Dialect#getInsertPage(java.lang.String)
     */
    public String getInsertPage(String tableName) {
        return "insert into " + tableName + " (page_id, fl_leaf, blob_content)"
                + " values (?,?,?)";
    }

    /**
     * @see org.geotools.index.rtree.database.Dialect#getUpdatePage(java.lang.String)
     */
    public String getUpdatePage(String tableName) {
        return "update " + tableName
                + " set fl_leaf=?, blob_content=? where page_id=?";
    }
}
