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
package org.geotoolkit.index.rtree;

import org.geotoolkit.index.DataDefinition;
import org.geotoolkit.index.Lock;
import org.geotoolkit.index.LockManager;
import org.geotoolkit.index.LockTimeoutException;
import org.geotoolkit.index.TreeException;

/**
 * RTree data structure.
 * 
 * @author Tommaso Nolli
 * @source $URL:
 *         http://svn.geotools.org/geotools/trunk/gt/modules/plugin/shapefile/src/main/java/org/geotools/index/rtree/PageStore.java $
 */
public abstract class PageStore {
    public static final short SPLIT_QUADRATIC = 1;
    public static final short SPLIT_LINEAR = 2;
    protected DataDefinition def;
    protected int maxNodeEntries;
    protected int minNodeEntries;
    protected short splitAlg;
    private LockManager lockManager;

    /**
     * 
     */
    public PageStore() {
        this.lockManager = new LockManager();
    }

    /**
     * DOCUMENT ME!
     * 
     * @param def
     * @param maxNodeEntries
     * @param minNodeEntries
     * @param splitAlg
     * 
     * @throws TreeException
     * @throws UnsupportedOperationException
     *                 DOCUMENT ME!
     */
    public PageStore(DataDefinition def, int maxNodeEntries,
            int minNodeEntries, short splitAlg) throws TreeException {
        this();

        if (minNodeEntries > (maxNodeEntries / 2)) {
            throw new TreeException("minNodeEntries shoud be <= "
                    + "maxNodeEntries / 2");
        }

        if ((splitAlg != SPLIT_LINEAR) && (splitAlg != SPLIT_QUADRATIC)) {
            throw new TreeException("Split algorithm shoud be "
                    + "SPLIT_LINEAR or SPLIT_QUADRATIC");
        }

        if (!def.isValid()) {
            throw new TreeException("Invalid DataDefinition");
        }

        // TODO: Remove when SPLIT_LINEAR is implemented
        if (splitAlg != SPLIT_QUADRATIC) {
            throw new UnsupportedOperationException(
                    "Only SPLIT_QUARDATIC is allowed by now...");
        }

        this.def = def;
        this.maxNodeEntries = maxNodeEntries;
        this.minNodeEntries = minNodeEntries;
        this.splitAlg = splitAlg;
    }

    /**
     * DOCUMENT ME!
     * 
     */
    public abstract Node getRoot();

    /**
     * DOCUMENT ME!
     * 
     * @param node
     * 
     * @throws TreeException
     *                 DOCUMENT ME!
     */
    public abstract void setRoot(Node node) throws TreeException;

    /**
     * DOCUMENT ME!
     * 
     * @param isLeaf
     * 
     */
    public abstract Node getEmptyNode(boolean isLeaf);

    /**
     * Returns the Node pointed by this entry and having this Node as parent
     * 
     * @param parentEntry
     * @param parent
     * 
     * 
     * @throws TreeException
     *                 DOCUMENT ME!
     */
    public abstract Node getNode(Entry parentEntry, Node parent)
            throws TreeException;

    /**
     * DOCUMENT ME!
     * 
     * @param node
     * 
     */
    public abstract Entry createEntryPointingNode(Node node);

    /**
     * DOCUMENT ME!
     * 
     * @return The maximum number of <code>Entry</code>s per page
     */
    public int getMaxNodeEntries() {
        return this.maxNodeEntries;
    }

    /**
     * DOCUMENT ME!
     * 
     * @return The minimum number of <code>Entry</code>s per page
     */
    public int getMinNodeEntries() {
        return this.minNodeEntries;
    }

    /**
     * DOCUMENT ME!
     * 
     * @return The split algorithm to use
     */
    public short getSplitAlgorithm() {
        return this.splitAlg;
    }

    /**
     * DOCUMENT ME!
     * 
     */
    public DataDefinition getDataDefinition() {
        return this.def;
    }

    /**
     * Frees resources used by this <code>Node</code>
     * 
     * @param node
     *                The <code>Node</code> to free
     */
    public abstract void free(Node node);

    /**
     * Aquires a write lock to the store
     * 
     * @return an Object rapresenting the lock
     * 
     * @throws LockTimeoutException
     */
    public Lock getWriteLock() throws LockTimeoutException {
        return this.lockManager.aquireExclusive();
    }

    /**
     * Aquires a read lock to the store
     * 
     * @return an Object rapresenting the lock
     * 
     * @throws LockTimeoutException
     */
    public Lock getReadLock() throws LockTimeoutException {
        return this.lockManager.aquireShared();
    }

    /**
     * DOCUMENT ME!
     * 
     * @param lock
     */
    public void releaseLock(Lock lock) {
        this.lockManager.release(lock);
    }

    /**
     * DOCUMENT ME!
     * 
     * @throws TreeException
     */
    public abstract void close() throws TreeException;
}
