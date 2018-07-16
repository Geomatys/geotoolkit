/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.storage;

import java.util.HashSet;
import java.util.Set;
import org.apache.sis.storage.Resource;
import org.apache.sis.storage.event.ChangeEvent;
import org.apache.sis.storage.event.ChangeListener;

/**
 * Abstract storage class, adds convinient event methods.
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class AbstractStorage {

    protected final Set<ChangeListener> listeners = new HashSet<>();

    public <T extends ChangeEvent> void addListener(ChangeListener<? super T> listener, Class<T> eventType){
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    public <T extends ChangeEvent> void removeListener(ChangeListener<? super T> listener, Class<T> eventType) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    /**
     * Forward an event to all listeners.
     * @param event , event to send to listeners.
     */
    protected void sendEvent(final ChangeEvent event){
        final ChangeListener[] lst;
        synchronized (listeners) {
            lst = listeners.toArray(new ChangeListener[listeners.size()]);
        }
        for (final ChangeListener listener : lst) {
            listener.changeOccured(event);
        }
    }

    /**
     * Forward given event, changing the source by this object.
     * For implementation use only.
     * @param event
     */
    public void forwardEvent(StorageEvent event){
        sendEvent(event.copy((Resource) this));
    }

}
