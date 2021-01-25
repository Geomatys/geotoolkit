/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2021, Geomatys
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
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import org.apache.sis.portrayal.MapItem;

/**
 * Weak MapItem listener implementation.
 * This class keeps weak references to the source and the listener.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class WeakMapItemListener implements PropertyChangeListener {

    private final WeakReference<MapItem> source;
    private final WeakReference<PropertyChangeListener> listener;
    private final String[] propertyNames;

    /**
     * @param source to listen to, not null
     * @param listener listener to receive event, not null
     * @param propertyNames properties to listen to
     */
    public WeakMapItemListener(MapItem source, PropertyChangeListener listener, String ... propertyNames) {
        this.source = new WeakReference<>(source);
        this.listener = new WeakReference<>(listener);
        this.propertyNames = propertyNames;
        for (String propertyName : propertyNames) {
            source.addPropertyChangeListener(propertyName, this);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        final PropertyChangeListener l = listener.get();
        if (l == null) {
            //unregister from source
            dispose();
        } else {
            l.propertyChange(evt);
        }
    }

    /**
     * Release all registered property listeners.
     */
    public void dispose() {
        final MapItem item = source.get();
        if (item != null) {
            for (String name : propertyNames) {
                item.removePropertyChangeListener(name, this);
            }
        }
    }

}
