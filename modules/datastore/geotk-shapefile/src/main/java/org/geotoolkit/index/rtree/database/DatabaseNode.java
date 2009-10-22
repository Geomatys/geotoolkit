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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.geotoolkit.index.TreeException;
import org.geotoolkit.index.rtree.Entry;
import org.geotoolkit.index.rtree.Node;

/**
 * DOCUMENT ME!
 * 
 * @author Tommaso Nolli
 * @module pending
 */
public class DatabaseNode extends Node {
    private DataSource dataSource;
    private Dialect dialect;
    private String tableName;
    private Integer parentId;
    private Integer pageId;

    /**
     * Constructor
     * 
     * @param maxNodeEntries
     * @param ds
     * @param dialect
     * @param tableName
     */
    public DatabaseNode(int maxNodeEntries, DataSource ds, Dialect dialect,
            String tableName) {
        super(maxNodeEntries);
        this.dataSource = ds;
        this.dialect = dialect;
        this.tableName = tableName;
    }

    /**
     * Constructor that retrieves data from the db
     * 
     * @param maxNodeEntries
     * @param ds
     * @param dialect
     * @param tableName
     * @param pageId
     * 
     * @throws TreeException
     *                 DOCUMENT ME!
     */
    public DatabaseNode(int maxNodeEntries, DataSource ds, Dialect dialect,
            String tableName, Integer pageId) throws TreeException {
        this(maxNodeEntries, ds, dialect, tableName);

        // TODO Retrieve the data
    }

    /**
     * @see org.geotools.index.rtree.Node#doSave()
     */
    protected void doSave() throws TreeException {
        // TODO Fill this array
        byte[] bytes = null;

        Connection cnn = null;

        try {
            cnn = this.dataSource.getConnection();

            if (this.pageId == null) {
                this.doInsert(cnn, bytes);
            } else {
                this.doUpdate(cnn, bytes);
            }
        } catch (SQLException e) {
            throw new TreeException(e);
        } catch (IOException e) {
            throw new TreeException(e);
        } finally {
            try {
                cnn.close();
            } catch (Exception ee) {
            }
        }
    }

    /**
     * Inserts this Node into the database
     * 
     * @param cnn
     * @param bytes
     * 
     * @throws SQLException
     * @throws IOException
     */
    protected void doInsert(Connection cnn, byte[] bytes) throws SQLException,
            IOException {
        PreparedStatement pst = null;

        try {
            int i = 1;
            int id = dialect.getNextPageId(cnn, tableName);
            ByteArrayInputStream is = new ByteArrayInputStream(bytes);
            pst = cnn.prepareStatement(dialect.getInsertPage(tableName));
            pst.setInt(i++, id);
            pst.setString(i++, this.isLeaf() ? "Y" : "N");
            pst.setBinaryStream(i++, is, bytes.length);
            pst.executeUpdate();

            cnn.commit();
            is.close();

            this.pageId = new Integer(id);
        } catch (SQLException e) {
            try {
                cnn.rollback();
            } catch (Exception ee) {
            }
        } finally {
            try {
                pst.close();
            } catch (Exception ee) {
            }
        }
    }

    /**
     * Update the databse with this Node informations
     * 
     * @param cnn
     * @param bytes
     * 
     * @throws SQLException
     * @throws IOException
     */
    protected void doUpdate(Connection cnn, byte[] bytes) throws SQLException,
            IOException {
        PreparedStatement pst = null;

        try {
            int i = 1;
            ByteArrayInputStream is = new ByteArrayInputStream(bytes);
            pst = cnn.prepareStatement(dialect.getUpdatePage(tableName));
            pst.setString(i++, this.isLeaf() ? "Y" : "N");
            pst.setBinaryStream(i++, is, bytes.length);
            pst.setInt(i++, this.pageId.intValue());
            pst.executeUpdate();

            cnn.commit();
            is.close();
        } catch (SQLException e) {
            try {
                cnn.rollback();
            } catch (Exception ee) {
            }
        } finally {
            try {
                pst.close();
            } catch (Exception ee) {
            }
        }
    }

    /**
     * @see org.geotools.index.rtree.Node#getEntry(org.geotools.index.rtree.Node)
     */
    protected Entry getEntry(Node node) {
        DatabaseNode dn = (DatabaseNode) node;

        Entry ret = null;
        Integer id = null;

        for (int i = 0; i < this.getEntriesCount(); i++) {
            id = (Integer) this.entries[i].getData();

            if (id.equals(dn.getPageId())) {
                ret = this.entries[i];

                break;
            }
        }

        return ret;
    }

    /**
     * @see org.geotools.index.rtree.Node#getParent()
     */
    public Node getParent() throws TreeException {
        if (this.parentId == null) {
            return null;
        }

        return new DatabaseNode(this.maxNodeEntries, this.dataSource,
                this.dialect, this.tableName, this.parentId);
    }

    /**
     * @see org.geotools.index.rtree.Node#setParent(org.geotools.index.rtree.Node)
     */
    public void setParent(Node node) {
        this.parentId = ((DatabaseNode) node).getPageId();
    }

    /**
     * DOCUMENT ME!
     * 
     * @return Returns the pageId.
     */
    public Integer getPageId() {
        return this.pageId;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param pageId
     *                The pageId to set.
     */
    public void setPageId(Integer pageId) {
        this.pageId = pageId;
    }
}
