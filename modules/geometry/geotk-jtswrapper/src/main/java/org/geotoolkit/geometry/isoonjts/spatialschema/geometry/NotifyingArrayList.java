/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2006-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.geometry.isoonjts.spatialschema.geometry;


import java.util.ArrayList;
import java.util.Collection;

/**
 * Helper class that notifies the containing geometry when the list has changed
 * so that it can invalidate any cached JTS objects it had.
 */
public class NotifyingArrayList<T> extends ArrayList<T> {
    private static final long serialVersionUID = 4564579379192943141L;
    private JTSGeometry parent;

    public NotifyingArrayList() {
        this( null );
    }

    public NotifyingArrayList(JTSGeometry parent) {
        this.parent = parent;
    }

    public void setJTSParent( JTSGeometry parent ){
        this.parent = parent;
    }

    public JTSGeometry getJTSParent(){
        return parent;
    }

    public void invalidateCachedJTSPeer(){
        if (parent != null) parent.invalidateCachedJTSPeer();
    }

    @Override
    public void add(int index, T element) {
        super.add(index, element);
        if (parent != null) parent.invalidateCachedJTSPeer();
    }

    @Override
    public boolean add(T o) {
        boolean result = super.add(o);
        if (parent != null) parent.invalidateCachedJTSPeer();
        return result;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        boolean result = super.addAll(c);
        if (parent != null) parent.invalidateCachedJTSPeer();
        return result;
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        boolean result = super.addAll(index, c);
        if (parent != null) parent.invalidateCachedJTSPeer();
        return result;
    }

    @Override
    public void clear() {
        super.clear();
        if (parent != null) parent.invalidateCachedJTSPeer();
    }

    @Override
    public T remove(int index) {
        T result = super.remove(index);
        if (parent != null) parent.invalidateCachedJTSPeer();
        return result;
    }

    @Override
    public T set(int index, T element) {
        T result = super.set(index, element);
        if (parent != null) parent.invalidateCachedJTSPeer();
        return result;
    }
}
