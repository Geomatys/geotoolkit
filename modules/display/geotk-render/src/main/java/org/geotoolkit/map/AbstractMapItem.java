/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.swing.event.EventListenerList;

import org.geotoolkit.style.StyleConstants;
import org.geotoolkit.util.Utilities;
import org.geotoolkit.util.logging.Logging;

import org.opengis.style.Description;

/**
 * Abstract implementation of a MapItem.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class AbstractMapItem implements MapItem {

    protected static final Logger LOGGER = Logging.getLogger("org.geotoolkit.map");
    
    protected final EventListenerList listeners = new EventListenerList();

    private final Map<String,Object> parameters = new HashMap<String,Object>();

    protected String name = null;

    protected Description desc = null;

    /**
     * Constructor that can used by subclass only.
     */
    protected AbstractMapItem(){
        this.desc = StyleConstants.DEFAULT_DESCRIPTION;
    }
    
    /**
     * {@inheritDoc }
     * This method is thread safe.
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc }
     * This method is thread safe.
     */
    @Override
    public void setName(String name) {
        final String oldName;
        synchronized (this) {
            oldName = this.name;
            if (Utilities.equals(oldName, name)) {
                return;
            }
            this.name = name;
        }
        firePropertyChange(NAME_PROPERTY, oldName, this.name);
    }
    
    /**
     * {@inheritDoc }
     * This method is thread safe.
     */
    @Override
    public Description getDescription() {
        return desc;
    }

    /**
     * {@inheritDoc }
     * @param desc : Description can't be null
     */
    @Override
    public void setDescription(Description desc){
        if (desc == null) {
            throw new NullPointerException("description can't be null");
        }
        
        final Description oldDesc;
        synchronized (this) {
            oldDesc = this.desc;
            if(oldDesc.equals(desc)){
                return;
            }
            this.desc = desc;
        }
        firePropertyChange(DESCRIPTION_PROPERTY, oldDesc, this.desc);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setUserPropertie(String key,Object value){
        parameters.put(key, value);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Object getUserPropertie(String key){
        return parameters.get(key);
    }
    
    //--------------------------------------------------------------------------
    // listeners management ----------------------------------------------------
    //--------------------------------------------------------------------------

    /**
     * {@inheritDoc }
     */
    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener){
        listeners.add(PropertyChangeListener.class, listener);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener){
        listeners.remove(PropertyChangeListener.class, listener);
    }
    
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue){
        //TODO make fire property change thread safe, preserve fire order
        
        final PropertyChangeEvent event = new PropertyChangeEvent(this,propertyName,oldValue,newValue);
        final PropertyChangeListener[] lists = listeners.getListeners(PropertyChangeListener.class);
        
        for(PropertyChangeListener listener : lists){
            listener.propertyChange(event);
        }
    }
        
    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();
        buf.append("AbstractMapItem[ ");
        buf.append(name);
        buf.append(',');
        buf.append(desc);
        buf.append("]");
        return buf.toString();
    }

}
