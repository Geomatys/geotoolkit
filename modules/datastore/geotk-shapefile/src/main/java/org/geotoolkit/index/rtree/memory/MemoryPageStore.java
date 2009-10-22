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
package org.geotoolkit.index.rtree.memory;

import org.geotoolkit.index.DataDefinition;
import org.geotoolkit.index.LockManager;
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
public class MemoryPageStore extends PageStore {
    private static final int DEF_MAX = 50;
    private static final int DEF_MIN = 25;
    private static final short DEF_SPLIT = SPLIT_QUADRATIC;
    private LockManager lockManager = new LockManager();
    private Node root = null;

    public MemoryPageStore(DataDefinition def) throws TreeException {
        this(def, DEF_MAX, DEF_MIN, DEF_SPLIT);
    }

    public MemoryPageStore(DataDefinition def, int max, int min, short split)
            throws TreeException {
        super(def, max, min, split);

        this.root = new MemoryNode(max);
        this.root.setLeaf(true);
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
        this.root = node;
        this.root.setParent(null);
    }

    /**
     * @see org.geotools.index.rtree.PageStore#getEmptyNode(boolean)
     */
    public Node getEmptyNode(boolean isLeaf) {
        MemoryNode ret = new MemoryNode(this.maxNodeEntries);
        ret.setLeaf(isLeaf);

        return ret;
    }

    /**
     * @see org.geotools.index.rtree.PageStore#getNode(org.geotools.index.rtree.Entry,
     *      org.geotools.index.rtree.Node)
     */
    public Node getNode(Entry parentEntry, Node parent) throws TreeException {
        Node ret = (Node) parentEntry.getData();
        ret.setParent(parent);

        return ret;
    }

    /**
     * @see org.geotools.index.rtree.PageStore#createEntryPointingNode(org.geotools.index.rtree.Node)
     */
    public Entry createEntryPointingNode(Node node) {
        return new Entry(node.getBounds(), node);
    }

    /**
     * @see org.geotools.index.rtree.PageStore#free(org.geotools.index.rtree.Node)
     */
    public void free(Node node) {
        // Does nothing
    }

    /**
     * @see org.geotools.index.rtree.PageStore#close()
     */
    public void close() throws TreeException {
        this.root = null;
    }
}
