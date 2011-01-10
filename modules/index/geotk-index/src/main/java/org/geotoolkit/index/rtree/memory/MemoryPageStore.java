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

    public MemoryPageStore(final DataDefinition def) throws TreeException {
        this(def, DEF_MAX, DEF_MIN, DEF_SPLIT);
    }

    public MemoryPageStore(final DataDefinition def, final int max, final int min, final short split)
            throws TreeException {
        super(def, max, min, split);

        this.root = new MemoryNode(max);
        this.root.setLeaf(true);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Node getRoot() {
        return this.root;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setRoot(final Node node) throws TreeException {
        this.root = node;
        this.root.setParent(null);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Node getEmptyNode(final boolean isLeaf) {
        MemoryNode ret = new MemoryNode(this.maxNodeEntries);
        ret.setLeaf(isLeaf);

        return ret;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Node getNode(final Entry parentEntry, final Node parent) throws TreeException {
        Node ret = (Node) parentEntry.getData();
        ret.setParent(parent);

        return ret;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Entry createEntryPointingNode(final Node node) {
        return new Entry(node.getBounds(), node);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void free(final Node node) {
        // Does nothing
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void close() throws TreeException {
        this.root = null;
    }
}
