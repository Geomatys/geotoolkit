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
    @Override
    protected Object clone() {
        Entry ret = new Entry(new Envelope(this.bounds), this.data);
        ret.setListener(this.listener);

        return ret;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Entry<T> other = (Entry<T>) obj;
        if (this.bounds != other.bounds && (this.bounds == null || !this.bounds.equals(other.bounds))) {
            return false;
        }
        if (this.data != other.data && (this.data == null || !this.data.equals(other.data))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 73 * hash + (this.bounds != null ? this.bounds.hashCode() : 0);
        hash = 73 * hash + (this.data != null ? this.data.hashCode() : 0);
        return hash;
    }

}
