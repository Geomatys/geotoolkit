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
import java.util.HashMap;
import java.util.Map;
import javax.swing.event.EventListenerList;

/**Create "generic" Node.
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class AbstractNode<N extends Node<N,B>, B> implements Node<N, B>{

    private final EventListenerList listenerList = new EventListenerList();
    private Map<String, Object> userProperties;
    
    /**
     * @param key
     * @return user property for given key
     */
    public Object getUserProperty(final String key) {
        if (userProperties == null) {
            return null;
        }
        return userProperties.get(key);
    }

    /**Add user property with key access.
     * 
     * @param key 
     * @param value Object will be stocked.
     */
    public void setUserProperty(final String key, final Object value) {
        if (userProperties == null) {
            userProperties = new HashMap<String, Object>();
        }
        userProperties.put(key, value);
    }

    public void addListener(PropertyChangeListener l) {
        listenerList.add(PropertyChangeListener.class, l);
    }

    public void removeListener(PropertyChangeListener l) {
        listenerList.remove(PropertyChangeListener.class, l);
    }
    

    protected void fireCollectionEvent() {

        final PropertyChangeListener[] listeners = listenerList.getListeners(PropertyChangeListener.class);

        for (PropertyChangeListener l : listeners) {
            l.propertyChange(null);
        }
    }
}
