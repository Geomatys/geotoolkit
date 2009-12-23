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
package org.geotoolkit.index.rtree.cachefs;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Iterator;
import java.util.Stack;

import org.geotoolkit.index.DataDefinition;
import org.geotoolkit.index.TreeException;

/**
 * DOCUMENT ME!
 * 
 * @author Tommaso Nolli
 * @module pending
 */
public class Parameters {
    private int maxNodeEntries;
    private int minNodeEntries;
    private short splitAlg;
    private DataDefinition dataDef;
    private FileChannel channel;
    private Stack freePages;
    private boolean forceChannel;
    private NodeCache cache;
    private long newNodeOffset;

    public Parameters() {
        this.freePages = new Stack();
        this.cache = new NodeCache();
    }

    /**
     * DOCUMENT ME!
     * 
     */
    public FileChannel getChannel() {
        return channel;
    }

    /**
     * DOCUMENT ME!
     * 
     */
    public DataDefinition getDataDef() {
        return dataDef;
    }

    /**
     * DOCUMENT ME!
     * 
     */
    public int getMaxNodeEntries() {
        return maxNodeEntries;
    }

    /**
     * DOCUMENT ME!
     * 
     */
    public int getMinNodeEntries() {
        return minNodeEntries;
    }

    /**
     * DOCUMENT ME!
     * 
     */
    public short getSplitAlg() {
        return splitAlg;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param channel
     */
    public void setChannel(FileChannel channel) {
        this.channel = channel;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param definition
     */
    public void setDataDef(DataDefinition definition) {
        dataDef = definition;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param i
     */
    public void setMaxNodeEntries(int i) {
        maxNodeEntries = i;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param i
     */
    public void setMinNodeEntries(int i) {
        minNodeEntries = i;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param s
     */
    public void setSplitAlg(short s) {
        splitAlg = s;
    }

    /**
     * DOCUMENT ME!
     * 
     */
    public boolean getForceChannel() {
        return forceChannel;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param b
     */
    public void setForceChannel(boolean b) {
        forceChannel = b;
    }

    /**
     * DOCUMENT ME!
     * 
     */
    public Stack getFreePages() {
        return freePages;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param stack
     */
    public void setFreePages(Stack stack) {
        freePages = stack;
    }

    public synchronized void setNodeCacheSize(int size) throws TreeException {
        if (this.cache != null) {
            this.flushCache();
        }

        if (size == 0) {
            this.cache = null;
        } else if (size < 0) {
            this.cache = new NodeCache();
        } else {
            this.cache = new NodeCache(size);
        }
    }

    /**
     * Gets a <code>FileSystemNode</code> from the cache, if the node is non
     * there, a new node will be created and added to the cache.
     * 
     * @param offset
     *                The node offset
     * 
     * @return a <code>FileSystemNode</code>
     * 
     * @throws IOException
     * @throws TreeException
     */
    public synchronized FileSystemNode getFromCache(long offset)
            throws IOException, TreeException {
        FileSystemNode node = null;

        if (this.cache != null) {
            node = (FileSystemNode) this.cache.get(new Long(offset));
        }

        if (node == null) {
            node = new FileSystemNode(this, offset);
            this.putToCache(node);
        }

        return node;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param len
     * 
     * 
     * @throws IOException
     *                 DOCUMENT ME!
     */
    public synchronized long getNewNodeOffset(int len) throws IOException {
        long offset = 0L;

        if (this.newNodeOffset == 0L) {
            offset = this.channel.size();
        } else {
            offset = this.newNodeOffset;
        }

        this.newNodeOffset = offset + len;

        return offset;
    }

    /**
     * Soters a <code>FileSystemNode</code> in the cache.
     * 
     * @param node
     *                the <code>FileSystemNode</code> to store
     * 
     * @throws TreeException
     */
    public synchronized void putToCache(FileSystemNode node)
            throws TreeException {
        if (this.cache != null) {
            // If we have a cache store the node, we'll flush it later
            this.cache.put(new Long(node.getOffset()), node);
        } else {
            // Else flush the node to disk
            node.flush();
        }
    }

    /**
     * Removes a node from the cache
     * 
     * @param node
     *                the node to remove
     */
    public synchronized void removeFromCache(FileSystemNode node) {
        if (this.cache != null) {
            this.cache.remove(node);
        }
    }

    /**
     * Flushes all nodes and clears the cache
     * 
     * @throws TreeException
     */
    public synchronized void flushCache() throws TreeException {
        if (this.cache == null) {
            return;
        }

        Iterator iter = this.cache.keySet().iterator();

        while (iter.hasNext()) {
            FileSystemNode element = (FileSystemNode) this.cache.get(iter
                    .next());
            element.flush();
        }

        this.cache.clear();
    }
}
