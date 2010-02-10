/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Johann Sorel
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
package org.geotoolkit.map;

import java.beans.PropertyChangeEvent;
import java.lang.reflect.Method;

import org.geotoolkit.style.CollectionChangeEvent;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class WeakContextListener extends WeakListener<ContextListener> implements ContextListener {


    public WeakContextListener(ContextListener listener, Object src) {
        super(listener,src);
    }

    @Override
    public void layerChange(CollectionChangeEvent<MapLayer> evt) {
        final ContextListener listener = listenerRef.get();
        if (listener != null) {
            listener.layerChange(evt);
        }else{
            removeListener();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        final ContextListener listener = listenerRef.get();
        if (listener != null) {
            listener.propertyChange(evt);
        }else{
            removeListener();
        }
    }

    @Override
    protected void removeListener() {
        try {
            Method method = src.getClass().getMethod("removeListener", ContextListener.class);
            method.invoke(src, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
