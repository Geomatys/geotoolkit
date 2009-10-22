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
// TODO Store somewhere root pageId: add in rtrees_cat probably...
package org.geotoolkit.index.rtree.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.geotoolkit.index.DataDefinition;
import org.geotoolkit.index.Lock;
import org.geotoolkit.index.LockTimeoutException;
import org.geotoolkit.index.TreeException;
import org.geotoolkit.index.rtree.Entry;
import org.geotoolkit.index.rtree.Node;
import org.geotoolkit.index.rtree.PageStore;

/**
 * DOCUMENT ME!
 * 
 * @author Tommaso Nolli
 * @module pending
 */
public class DatabasePageStore extends PageStore {
    protected static final int DEF_MAX = 50;
    protected static final int DEF_MIN = 25;
    protected static final short DEF_SPLIT = SPLIT_QUADRATIC;
    private DataSource dataSource;
    private Dialect dialect;
    private String rtreeName;
    private DatabaseNode root;

    /**
     * Constructor
     * 
     * @param ds
     * @param dialect
     *                DOCUMENT ME!
     * @param rtreeName
     * 
     * @throws TreeException
     *                 DOCUMENT ME!
     */
    public DatabasePageStore(DataSource ds, Dialect dialect, String rtreeName)
            throws TreeException {
        super();

        this.dataSource = ds;
        this.dialect = dialect;
        this.rtreeName = rtreeName;

        this.createNew();
    }

    /**
     * Constructor
     * 
     * @param ds
     * @param dialect
     *                DOCUMENT ME!
     * @param rtreeName
     * @param def
     * 
     * @throws TreeException
     *                 DOCUMENT ME!
     */
    public DatabasePageStore(DataSource ds, Dialect dialect, String rtreeName,
            DataDefinition def) throws TreeException {
        this(ds, dialect, rtreeName, def, DEF_MAX, DEF_MIN, DEF_SPLIT);
    }

    /**
     * Constructor
     * 
     * @param ds
     * @param dialect
     *                DOCUMENT ME!
     * @param rtreeName
     * @param def
     * @param maxNodeEntries
     * @param minNodeEntries
     * @param splitAlg
     * 
     * @throws TreeException
     *                 DOCUMENT ME!
     */
    public DatabasePageStore(DataSource ds, Dialect dialect, String rtreeName,
            DataDefinition def, int maxNodeEntries, int minNodeEntries,
            short splitAlg) throws TreeException {
        super(def, maxNodeEntries, minNodeEntries, splitAlg);

        this.dataSource = ds;
        this.dialect = dialect;
        this.rtreeName = rtreeName;

        init();
    }

    /**
     * DOCUMENT ME!
     * 
     * @throws TreeException
     */
    private void createNew() throws TreeException {
        Connection cnn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            cnn = this.dataSource.getConnection();
            pst = cnn.prepareStatement(dialect.getCatalogQuery());
            pst.setString(1, this.rtreeName);
            rs = pst.executeQuery();

            if (rs.next()) {
                throw new TreeException(this.rtreeName + " already exists!");
            }

            rs.close();
            pst.close();

            int i = 1;
            pst = cnn.prepareStatement(dialect.getCatalogInsert());
            pst.setString(i++, this.rtreeName);
            pst.setInt(i++, this.minNodeEntries);
            pst.setInt(i++, this.maxNodeEntries);
            pst.setShort(i++, this.splitAlg);

            pst.executeUpdate();
            cnn.commit();
            pst.close();

            pst = cnn.prepareStatement(dialect.getCreateTable(this.rtreeName));
            pst.execute();

            this.root = new DatabaseNode(this.maxNodeEntries, this.dataSource,
                    this.dialect, this.rtreeName);

            this.root.setLeaf(true);
            this.root.save();
        } catch (SQLException e) {
            try {
                cnn.rollback();
            } catch (Exception ee) {
            }

            throw new TreeException(e);
        } finally {
            try {
                rs.close();
            } catch (Exception ee) {
            }

            try {
                pst.close();
            } catch (Exception ee) {
            }

            try {
                cnn.close();
            } catch (Exception ee) {
            }
        }
    }

    /**
     * Initializes the PageStore
     */
    private void init() {
    }

    /**
     * @see org.geotools.index.rtree.PageStore#getRoot()
     */
    public Node getRoot() {
        return this.root;
    }

    /**
     * @see org.geotools.index.rtree.PageStore#setRoot(org.geotools.index.rtree.Node)
     */
    public void setRoot(Node node) throws TreeException {
        DatabaseNode n = (DatabaseNode) node;
        n.setParent(null);

        this.root = n;
    }

    /**
     * @see org.geotools.index.rtree.PageStore#getEmptyNode(boolean)
     */
    public Node getEmptyNode(boolean isLeaf) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.geotools.index.rtree.PageStore#getNode(org.geotools.index.rtree.Entry,
     *      org.geotools.index.rtree.Node)
     */
    public Node getNode(Entry parentEntry, Node parent) throws TreeException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.geotools.index.rtree.PageStore#createEntryPointingNode(org.geotools.index.rtree.Node)
     */
    public Entry createEntryPointingNode(Node node) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.geotools.index.rtree.PageStore#getMaxNodeEntries()
     */
    public int getMaxNodeEntries() {
        // TODO Auto-generated method stub
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.geotools.index.rtree.PageStore#getMinNodeEntries()
     */
    public int getMinNodeEntries() {
        // TODO Auto-generated method stub
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.geotools.index.rtree.PageStore#getSplitAlgorithm()
     */
    public short getSplitAlgorithm() {
        // TODO Auto-generated method stub
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.geotools.index.rtree.PageStore#getDataDefinition()
     */
    public DataDefinition getDataDefinition() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.geotools.index.rtree.PageStore#free(org.geotools.index.rtree.Node)
     */
    public void free(Node node) {
        // TODO Auto-generated method stub
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.geotools.index.rtree.PageStore#getWriteLock()
     */
    public Lock getWriteLock() throws LockTimeoutException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.geotools.index.rtree.PageStore#getReadLock()
     */
    public Lock getReadLock() throws LockTimeoutException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.geotools.index.rtree.PageStore#releaseLock(org.geotools.index.rtree.Lock)
     */
    public void releaseLock(Lock lock) {
        // TODO Auto-generated method stub
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.geotools.index.rtree.PageStore#close()
     */
    public void close() throws TreeException {
        // TODO Auto-generated method stub
    }
}
