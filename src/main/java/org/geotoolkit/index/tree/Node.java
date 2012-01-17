/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.index.tree;

import javax.swing.event.EventListenerList;
import org.geotoolkit.util.collection.CollectionChangeListener;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class Node {

    private final EventListenerList listenerList = new EventListenerList();

    public void addListener(CollectionChangeListener l) {
        listenerList.add(CollectionChangeListener.class, l);
    }

    public void removeListener(CollectionChangeListener l) {
        listenerList.remove(CollectionChangeListener.class, l);
    }

    protected void fireCollectionEvent() {

        final CollectionChangeListener[] listeners = listenerList.getListeners(CollectionChangeListener.class);

        for (CollectionChangeListener l : listeners) {
            l.collectionChange(null);
        }
    }
}
