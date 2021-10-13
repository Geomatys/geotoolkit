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
package org.geotoolkit.client.map;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.ArrayBlockingQueue;
import javax.swing.event.EventListenerList;
import org.geotoolkit.util.Cancellable;

/**
 * Cancellable queue.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class CancellableQueue<T> extends ArrayBlockingQueue<T> implements Cancellable{

    private final EventListenerList lst = new EventListenerList();

    private volatile boolean cancelled = false;

    public CancellableQueue(int capacity) {
        super(capacity);
    }

    @Override
    public void cancel() {
        cancelled = true;
        firePropertyChange();
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    private void firePropertyChange(){
        for(PropertyChangeListener l : lst.getListeners(PropertyChangeListener.class)){
            l.propertyChange(new PropertyChangeEvent(this, "cancel", false, true));
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener l){
        lst.add(PropertyChangeListener.class, l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l){
        lst.remove(PropertyChangeListener.class, l);
    }

}
