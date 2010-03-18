/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Geomatys
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

import java.lang.ref.WeakReference;
import java.util.EventListener;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class WeakListener<T extends EventListener>{

    protected final WeakReference<T> listenerRef;
    protected final Object src;

    public WeakListener(T listener, Object src) {
        listenerRef = new WeakReference(listener, ActiveReferenceQueue.getInstance());

        if(src == null){
            throw new NullPointerException("Weak listener source must be defined.");
        }

        this.src = src;
    }

    protected abstract void removeListener();

}
