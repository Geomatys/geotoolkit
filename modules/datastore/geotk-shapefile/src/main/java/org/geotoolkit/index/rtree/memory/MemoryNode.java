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

import org.geotoolkit.index.TreeException;
import org.geotoolkit.index.rtree.Entry;
import org.geotoolkit.index.rtree.Node;

/**
 * DOCUMENT ME!
 * 
 * @author Tommaso Nolli
 * @module pending
 */
public class MemoryNode extends Node {
    private Node parent;

    /**
     * DOCUMENT ME!
     * 
     * @param maxNodeEntries
     */
    public MemoryNode(int maxNodeEntries) {
        super(maxNodeEntries);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Node getParent() throws TreeException {
        return this.parent;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setParent(Node node) {
        this.parent = node;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected Entry getEntry(Node node) {
        Entry ret = null;
        Node n = null;

        for (int i = 0; i < this.entries.length; i++) {
            n = (Node) this.entries[i].getData();

            if (n == node) {
                ret = this.entries[i];

                break;
            }
        }

        return ret;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void doSave() throws TreeException {
        // does nothing....
    }
}
