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
import java.util.List;
import java.util.Map;
import javax.swing.event.EventListenerList;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.opengis.geometry.Envelope;

/**Create "generic" Node.
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class Node {

    protected GeneralEnvelope boundary;
    protected Node parent;
    protected Tree tree;
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
    
    /**
     * Affect a {@code Node} boundary.
     */
    public void setBound(Envelope bound){
        if(bound == null){
            boundary = null;
        }else{
            boundary = new GeneralEnvelope(bound);
        }
    }
    
    /**<blockquote><font size=-1>
     * <strong>NOTE: It is possible that return null.</strong> 
     * </font></blockquote>
     * 
     * @return {@code Node} boundary without re-computing subnode boundary.
     */
    public Envelope getBound(){
        return this.boundary;
    }
    
    /**Affect a new {@code Node} parent.
     * 
     * @param parent {@code Node} parent pointer.
     */
    public abstract void setParent(Node parent);
    
    /**
     * @return subNodes.
     */
    public abstract List<Node> getChildren();
    
    /**A leaf is a {@code Node} at extremity of {@code Tree} which contains only entries.
     * 
     * @return true if it is  a leaf else false (branch).
     */
    public abstract boolean isLeaf();
    
    /**
     * @return true if {@code Node} contains nothing else false.
     */
    public abstract boolean isEmpty();
    
    /**
     * @return true if node elements number equals or overflow max elements
     *         number autorized by {@code Tree} else false. 
     */
    public abstract boolean isFull();
    
    /**
     * <blockquote><font size=-1>
     * <strong>NOTE: if boundary is null, method re-compute all subnode boundary.</strong> 
     * </font></blockquote>
     * @return boundary.
     */
    public abstract Envelope getBoundary();
    
    /**
     * @return entries.
     */
    public abstract List<Envelope> getEntries();

    /**
     * @return {@code AbstractNode} parent pointer.
     */
    public abstract Node getParent();

    /**
     * @return {@code Tree} pointer.
     */
    public abstract Tree getTree();
    
}
