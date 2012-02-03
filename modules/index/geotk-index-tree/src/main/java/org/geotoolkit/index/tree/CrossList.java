/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.index.tree;

import java.beans.PropertyChangeListener;
import java.util.Collection;
import org.geotoolkit.util.NumberRange;
import org.geotoolkit.util.collection.NotifiedCheckedList;

/**
 * A subclass of NotifiedCheckedList which listen to it's contained Node.
 * This allow to automatically forward events.
 *
 * @param <T> 
 * @author Johann Sorel (Geomatys)
 */
public abstract class CrossList<T extends Node> extends NotifiedCheckedList<T> implements PropertyChangeListener {

    public CrossList(final Class<T> type) {
        super(type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void notifyAdd(T e, int i) {
        e.addListener(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void notifyAdd(Collection<? extends T> clctn, NumberRange<Integer> nr) {
        for (T n : clctn) {
            n.addListener(this);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void notifyRemove(T e, int i) {
        e.removeListener(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void notifyRemove(Collection<? extends T> clctn, NumberRange<Integer> nr) {
        for (T n : clctn) {
            n.removeListener(this);
        }
    }
}
