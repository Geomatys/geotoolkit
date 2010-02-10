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
import java.util.EventObject;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class WeakLayerListener extends WeakListener<LayerListener> implements LayerListener {


    public WeakLayerListener(LayerListener listener, Object src) {
        super(listener,src);
    }

    @Override
    public void styleChange(MapLayer source, EventObject evt) {
        final LayerListener listener = listenerRef.get();
        if (listener != null) {
            listener.styleChange(source,evt);
        }else{
            removeListener();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        final LayerListener listener = listenerRef.get();
        if (listener != null) {
            listener.propertyChange(evt);
        }else{
            removeListener();
        }
    }

    @Override
    protected void removeListener() {
        try {
            Method method = src.getClass().getMethod("removeListener", LayerListener.class);
            method.invoke(src, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
