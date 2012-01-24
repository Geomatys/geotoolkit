/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.index.tree;

import java.util.HashMap;
import java.util.Map;
import javax.swing.event.EventListenerList;
import org.geotoolkit.util.collection.CollectionChangeListener;

/**Create "generic" Node.
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class Node {

    private final EventListenerList listenerList = new EventListenerList();
    private Map<String,Object> userProperties ;

    /**
     * @param key
     * @return user properties stocked at key emplacement.
     */
    public Object getUserProperty(final String key) {
        return userProperties.get(key);
    }

    /**Add user property with key access.
     * 
     * @param key 
     * @param value Object will be stocked.
     */
    public void setUserProperty(final String key, final Object value) {
        if(userProperties == null){
            userProperties = new HashMap<String, Object>(); 
        }
        userProperties.put(key,value);
    }    
    
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
