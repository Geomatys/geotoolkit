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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.geotoolkit.index.TreeException;

import com.vividsolutions.jts.geom.Envelope;

/**
 * DOCUMENT ME!
 * 
 * @author Tommaso Nolli
 * @module pending
 */
public abstract class Node implements EntryBoundsChangeListener {
    private boolean leaf;
    protected int entriesCount = 0;
    protected int maxNodeEntries;
    protected Envelope bounds;
    protected Entry[] entries;
    protected boolean isChanged;

    public Node(final int maxNodeEntries) {
        this.maxNodeEntries = maxNodeEntries;
        this.entries = new Entry[maxNodeEntries + 1];
        this.bounds = null;
    }

    /**
     * Adds an <code>Entry</code> to this <code>Node</code>
     * 
     * @param entry
     */
    public final void addEntry(final Entry entry) {
        this.entries[this.entriesCount++] = entry;
        entry.setListener(this);

        if (this.bounds == null) {
            this.bounds = new Envelope(entry.getBounds());
        } else {
            this.bounds.expandToInclude(entry.getBounds());
        }

        this.isChanged = true;
    }

    /**
     * Removes an <code>Entry</code> from this <code>Node</code>
     * 
     * @param entry
     *                The <code>Entry</code> to remove
     */
    public final void removeEntry(final Entry entry) {
        Entry[] newEntries = new Entry[this.entries.length];
        Envelope newBounds = null;
        int newSize = 0;

        for (int i = 0; i < this.entriesCount; i++) {
            if (!this.entries[i].equals(entry)) {
                newEntries[newSize++] = this.entries[i];

                if (newBounds == null) {
                    newBounds = new Envelope(this.entries[i].getBounds());
                } else {
                    newBounds.expandToInclude(this.entries[i].getBounds());
                }
            }
        }

        this.entries = newEntries;
        this.entriesCount = newSize;
        this.bounds = newBounds;
        this.isChanged = true;
    }

    /**
     * Removes all <code>Entry</code>s from this <code>Node</code>
     */
    public void clear() {
        Arrays.fill(this.entries, null);
        this.entriesCount = 0;
        this.bounds = null;
        this.isChanged = true;
    }

    /**
     * DOCUMENT ME!
     * 
     */
    public boolean isLeaf() {
        return leaf;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param b
     */
    public void setLeaf(final boolean b) {
        leaf = b;
    }

    /**
     * DOCUMENT ME!
     * 
     */
    public int getEntriesCount() {
        return this.entriesCount;
    }

    /**
     * Gets the n<i>th</i> Element
     * 
     * @param n
     * 
     */
    public Entry getEntry(final int n) {
        return this.entries[n];
    }

    public Collection getEntries() {
        ArrayList ret = new ArrayList(this.entriesCount);

        for (int i = 0; i < this.entriesCount; i++) {
            ret.add(this.entries[i].clone());
        }

        return ret;
    }

    /**
     * The bounds of this node.
     * <p>
     * You will need to look at the prj to produce a referneced envelope for
     * wider use.
     * 
     * @return The bounds
     */
    public Envelope getBounds() {
        return bounds;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void boundsChanged(final Entry e) {
        this.bounds = new Envelope(this.entries[0].getBounds());

        for (int i = 1; i < this.entriesCount; i++) {
            this.bounds.expandToInclude(this.entries[i].getBounds());
        }
    }

    /**
     * Saves this <code>Node</code>; this method calls doSave()
     * 
     * @throws TreeException
     */
    public final void save() throws TreeException {
        this.doSave();
        this.isChanged = false;
    }

    /**
     * DOCUMENT ME!
     * 
     * 
     * @throws TreeException
     *                 DOCUMENT ME!
     */
    public abstract Node getParent() throws TreeException;

    /**
     * Sets the parent of this <code>Node</code>
     * 
     * @param node
     *                The parent <code>Node</code>
     */
    public abstract void setParent(Node node);

    /**
     * Returns the Entry pointing the specified <code>Node</code>
     * 
     * @param node
     *                The <code>Node</code>
     * 
     * @return The <code>Entry</code>
     */
    protected abstract Entry getEntry(Node node);

    /**
     * Saves this <code>Node</code>; called from save()
     * 
     * @throws TreeException
     */
    protected abstract void doSave() throws TreeException;
}
