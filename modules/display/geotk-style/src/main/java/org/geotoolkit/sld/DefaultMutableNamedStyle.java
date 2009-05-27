/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.sld;

import java.beans.PropertyChangeEvent;
import javax.swing.event.EventListenerList;

import org.geotoolkit.style.StyleConstants;
import org.geotoolkit.style.StyleListener;
import org.geotoolkit.util.Utilities;

import org.opengis.sld.SLDVisitor;
import org.opengis.style.Description;

/**
 * Default mutable named Style, thread safe.
 * 
 * @author Johann Sorel (Geomatys)
 */
class DefaultMutableNamedStyle implements MutableNamedStyle{

    private final EventListenerList listeners = new EventListenerList();
        
    private String name = null;
    
    private Description description = StyleConstants.DEFAULT_DESCRIPTION;
    
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
        return description;
    }
    
    /**
     * {@inheritDoc }
     * This method is thread safe.
     */
    @Override
    public void setDescription(Description desc) {
        if (desc == null) {
            throw new NullPointerException("description can't be null");
        }
        
        final Description oldDesc;
        synchronized (this) {
            oldDesc = this.description;
            if(oldDesc.equals(desc)){
                return;
            }
            this.description = desc;
        }
        firePropertyChange(DESCRIPTION_PROPERTY, oldDesc, this.description);
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public Object accept(SLDVisitor visitor, Object extraData) {
        return visitor.visit(this, extraData);
    }
    
    //--------------------------------------------------------------------------
    // listeners management ----------------------------------------------------
    //--------------------------------------------------------------------------
    
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue){
        //TODO make fire property change thread safe, preserve fire order
        
        final PropertyChangeEvent event = new PropertyChangeEvent(this,propertyName,oldValue,newValue);
        final StyleListener[] lists = listeners.getListeners(StyleListener.class);
        
        for(StyleListener listener : lists){
            listener.propertyChange(event);
        }
        
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public void addListener(StyleListener listener) {
        listeners.add(StyleListener.class, listener);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void removeListener(StyleListener listener) {
        listeners.remove(StyleListener.class, listener);
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public boolean equals(Object obj) {

        if(this == obj){
            return true;
        }

        if(obj == null || !this.getClass().equals(obj.getClass()) ){
            return false;
        }

        DefaultMutableNamedStyle other = (DefaultMutableNamedStyle) obj;

        return Utilities.equals(this.name, other.name)
                && this.description.equals(other.description);

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        int hash = 2;
        if(name != null) hash *= name.hashCode();
        hash *= description.hashCode();
        return hash;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[MutableNamedStyle : ");
        if(name != null){
            builder.append(" Name=");
            builder.append(name.toString());
        }
        builder.append(" Description=");
        builder.append(description.toString());
        builder.append(']');
        return builder.toString();
    }

}
