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
import java.util.Collection;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.swing.event.EventListenerList;

import org.geotoolkit.style.StyleConstants;
import org.geotoolkit.util.NumberRange;
import org.geotoolkit.util.StringUtilities;
import org.geotoolkit.util.Utilities;
import org.geotoolkit.util.collection.CollectionChangeEvent;
import org.geotoolkit.util.converter.Classes;
import org.geotoolkit.util.logging.Logging;

import org.opengis.style.Description;

import static org.geotoolkit.util.ArgumentChecks.*;

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
    
    protected boolean visible = true;

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
    public void setName(final String name) {
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
    public void setDescription(final Description desc){
        ensureNonNull("description", desc);
        
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
     * Getter for property visible.
     *
     * @return Value of property visible.
     */
    @Override
    public boolean isVisible() {
        return this.visible;
    }

    /**
     * Setter for property visible.
     *
     * @param visible : New value of property visible.
     */
    @Override
    public void setVisible(final boolean visible) {
        final boolean oldVisible;
        synchronized (this) {
            oldVisible = this.visible;
            if(oldVisible == visible){
                return;
            }
            this.visible = visible;
        }
        firePropertyChange(VISIBILITY_PROPERTY, oldVisible, this.visible);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setUserPropertie(final String key,final Object value){
        parameters.put(key, value);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Object getUserPropertie(final String key){
        return parameters.get(key);
    }
    
    //--------------------------------------------------------------------------
    // listeners management ----------------------------------------------------
    //--------------------------------------------------------------------------

    /**
     * {@inheritDoc }
     */
    @Override
    public void addItemListener(final ItemListener listener){
        listeners.add(ItemListener.class, listener);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void removeItemListener(final ItemListener listener){
        listeners.remove(ItemListener.class, listener);
    }

    protected void fireItemChange(final int type, final MapItem item, final NumberRange<Integer> range, final EventObject orig) {
        //TODO make fire property change thread safe, preserve fire order

        final CollectionChangeEvent<MapItem> event = new CollectionChangeEvent<MapItem>(this, item, type, range, orig);
        final ItemListener[] lists = listeners.getListeners(ItemListener.class);

        for (ItemListener listener : lists){
            listener.itemChange(event);
        }

    }

    protected void fireItemChange(final int type, final Collection<? extends MapItem> item, final NumberRange<Integer> range){
        //TODO make fire property change thread safe, preserve fire order

        final CollectionChangeEvent<MapItem> event = new CollectionChangeEvent<MapItem>(this,item,type,range, null);
        final ItemListener[] lists = listeners.getListeners(ItemListener.class);

        for(ItemListener listener : lists){
            listener.itemChange(event);
        }

    }

    protected void firePropertyChange(final String propertyName, final Object oldValue, final Object newValue){
        //TODO make fire property change thread safe, preserve fire order
        
        final PropertyChangeEvent event = new PropertyChangeEvent(this,propertyName,oldValue,newValue);
        final ItemListener[] lists = listeners.getListeners(ItemListener.class);
        
        for(PropertyChangeListener listener : lists){
            listener.propertyChange(event);
        }
    }
        
    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();
        buf.append(Classes.getShortClassName(this));
        if(name != null){
            buf.append(" (");
            buf.append(name);
            buf.append(") ");
        }        
        if(desc != null){
            buf.append(desc);
        }
        final List<MapItem> items = items();
        if(!items.isEmpty()){
            buf.append('\n');
            buf.append( StringUtilities.toStringTree(items) );
        }
        return buf.toString();
    }

}
