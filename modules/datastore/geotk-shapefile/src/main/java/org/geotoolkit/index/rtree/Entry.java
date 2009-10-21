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

import com.vividsolutions.jts.geom.Envelope;

/**
 * DOCUMENT ME!
 * 
 * @author Tommaso Nolli
 * @source $URL:
 *         http://svn.geotools.org/geotools/trunk/gt/modules/plugin/shapefile/src/main/java/org/geotools/index/rtree/Entry.java $
 * @module pending
 */
public class Entry<T> implements Cloneable {
    private Envelope bounds;
    private T data;
    private EntryBoundsChangeListener listener;

    public Entry(Envelope e, T data) {
        this.bounds = e;
        this.data = data;
    }

    /**
     * DOCUMENT ME!
     * 
     */
    public Envelope getBounds() {
        return bounds;
    }

    /**
     * DOCUMENT ME!
     * 
     */
    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        Entry e = (Entry) obj;

        return this.bounds.equals(e.getBounds())
                && this.data.equals(e.getData());
    }

    /**
     * DOCUMENT ME!
     * 
     * @param envelope
     */
    void setBounds(Envelope envelope) {
        bounds = envelope;

        if (this.listener != null) {
            this.listener.boundsChanged(this);
        }
    }

    /**
     * @see java.lang.Object#clone()
     */
    protected Object clone() {
        Entry ret = new Entry(new Envelope(this.bounds), this.data);
        ret.setListener(this.listener);

        return ret;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "Entry --> " + this.bounds + " - key: " + this.data;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param listener
     */
    public void setListener(EntryBoundsChangeListener listener) {
        this.listener = listener;
    }
}
