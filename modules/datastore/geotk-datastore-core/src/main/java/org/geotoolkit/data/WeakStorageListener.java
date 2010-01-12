/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.data;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;

/**
 * Weak storage listener. Use it when you are not
 * sure that the listener will be correctly removed.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class WeakStorageListener implements StorageListener {

    private final WeakReference<StorageListener> ref;
    private final Object source;

    public WeakStorageListener(Object source, StorageListener ref) {
        this.ref = new WeakReference<StorageListener>(ref);
        this.source = source;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void structureChanged(StorageManagementEvent event) {
        final StorageListener session = ref.get();
        if (session == null) {
            removeListener();
        }
        session.structureChanged(event);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void contentChanged(StorageContentEvent event) {
        final StorageListener session = ref.get();
        if (session == null) {
            removeListener();
        }
        session.contentChanged(event);
    }

    private void removeListener() {
        try {
            Method method = source.getClass().getMethod("removeStorageListener", new Class[]{StorageListener.class});
            method.invoke(source, new Object[]{this});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
