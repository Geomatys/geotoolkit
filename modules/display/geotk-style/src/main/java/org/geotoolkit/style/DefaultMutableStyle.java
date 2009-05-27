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
package org.geotoolkit.style;

import java.beans.PropertyChangeEvent;
import java.util.Collection;
import java.util.EventObject;
import java.util.List;
import javax.swing.event.EventListenerList;

import org.geotoolkit.util.NotifiedCheckedList;
import org.geotoolkit.util.NumberRange;

import org.geotoolkit.util.Utilities;
import org.opengis.feature.type.Name;
import org.opengis.style.Description;
import org.opengis.style.SemanticType;
import org.opengis.style.StyleVisitor;
import org.opengis.style.Symbolizer;

/**
 * Mutable implementation of GeoAPI style.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class DefaultMutableStyle implements MutableStyle{
    
    private final List<MutableFeatureTypeStyle> fts = new NotifiedCheckedList<MutableFeatureTypeStyle>(MutableFeatureTypeStyle.class) {

            @Override
            protected Object getLock() {
                return DefaultMutableStyle.this;
            }

            @Override
            protected void notifyAdd(final MutableFeatureTypeStyle item, final int index) {
                item.addListener(ftsListener);
                fireFeatureTypeStyleChange(CollectionChangeEvent.ITEM_ADDED, item, NumberRange.create(index, index) );
            }

            @Override
            protected void notifyAdd(final Collection<? extends MutableFeatureTypeStyle> items, final NumberRange<Integer> range) {
                for(MutableFeatureTypeStyle item : items){
                    item.addListener(ftsListener);
                }
                fireFeatureTypeStyleChange(CollectionChangeEvent.ITEM_ADDED, items, range);
            }

            @Override
            protected void notifyRemove(final MutableFeatureTypeStyle item, final int index) {
                item.removeListener(ftsListener);
                fireFeatureTypeStyleChange(CollectionChangeEvent.ITEM_REMOVED, item, NumberRange.create(index, index) );
            }

            @Override
            protected void notifyRemove(final Collection<? extends MutableFeatureTypeStyle> items, final NumberRange<Integer> range) {
                for(final MutableFeatureTypeStyle fts : items){
                    fts.removeListener(ftsListener);
                }
                fireFeatureTypeStyleChange(CollectionChangeEvent.ITEM_REMOVED, items, range );
            }

        };
        
    private final EventListenerList listeners = new EventListenerList();
    
    private final FeatureTypeStyleListener ftsListener = new FeatureTypeStyleListener() {

        @Override
        public void propertyChange(PropertyChangeEvent event) {
            fireFeatureTypeStyleChange(CollectionChangeEvent.ITEM_CHANGED, (MutableFeatureTypeStyle)event.getSource(), null, event);
        }

        @Override
        public void ruleChange(CollectionChangeEvent<MutableRule> event) {
            fireFeatureTypeStyleChange(CollectionChangeEvent.ITEM_CHANGED, (MutableFeatureTypeStyle)event.getSource(), null, event);
        }

        @Override
        public void featureTypeNameChange(CollectionChangeEvent<Name> event) {
            fireFeatureTypeStyleChange(CollectionChangeEvent.ITEM_CHANGED, (MutableFeatureTypeStyle)event.getSource(), null, event);
        }

        @Override
        public void semanticTypeChange(CollectionChangeEvent<SemanticType> event) {
            fireFeatureTypeStyleChange(CollectionChangeEvent.ITEM_CHANGED, (MutableFeatureTypeStyle)event.getSource(), null, event);
        }
    };
   
    private Symbolizer symbol = null;
    
    private String name = null;
    
    private Description description = StyleConstants.DEFAULT_DESCRIPTION;
    
    private boolean isDefault = false;

    /**
     * Create a default mutable style.
     */
    public DefaultMutableStyle(){}
    
    /**
     * @return live list
     */
    @Override
    public List<MutableFeatureTypeStyle> featureTypeStyles() {
        return fts;
    }

    /**
     * {@inheritDoc }
     * This method is thread safe.
     */
    @Override
    public Symbolizer getDefaultSpecification() {
        return symbol;
    }

    @Override
    public void setDefaultSpecification(Symbolizer symbol){
        if(symbol == null){
            throw new NullPointerException("default symbolizer can't be null");
        }
        
        final Symbolizer oldSymbolizer;
        synchronized (this) {
            oldSymbolizer = this.symbol;
            if (Utilities.equals(oldSymbolizer, symbol)) {
                return;
            }
            this.symbol = symbol;
        }
        firePropertyChange(DEFAULT_SPECIFICATION_PROPERTY, oldSymbolizer, this.symbol);
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
     * Set the name of the style.
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
     * Set the Description of the style.
     * @param desc : Description can't be null
     */
    @Override
    public void setDescription(Description desc){
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
     * This method is thread safe.
     */
    @Override
    public boolean isDefault() {
        return isDefault;
    }

    /**
     * {@inheritDoc }
     * This method is thread safe.
     */
    @Override
    public void setDefault(boolean isDefault){
        final boolean oldIsDefault;
        synchronized (this) {
            oldIsDefault = this.isDefault;
            if(oldIsDefault == isDefault){
                return;
            }
            this.isDefault = isDefault;
        }
        firePropertyChange(ISDEFAULT_PROPERTY, oldIsDefault, this.isDefault);
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public Object accept(StyleVisitor visitor, Object extraData) {
        return visitor.visit(this,extraData);
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
    
    protected void fireFeatureTypeStyleChange(int type, MutableFeatureTypeStyle fts, NumberRange<Integer> range) {
        //TODO make fire property change thread safe, preserve fire order

        final CollectionChangeEvent<MutableFeatureTypeStyle> event = new CollectionChangeEvent<MutableFeatureTypeStyle>(this, fts, type, range, null);
        final StyleListener[] lists = listeners.getListeners(StyleListener.class);

        for (StyleListener listener : lists) {
            listener.featureTypeStyleChange(event);
        }

    }
    
    protected void fireFeatureTypeStyleChange(int type, MutableFeatureTypeStyle fts, NumberRange<Integer> range, EventObject subEvent) {
        //TODO make fire property change thread safe, preserve fire order

        final CollectionChangeEvent<MutableFeatureTypeStyle> event = new CollectionChangeEvent<MutableFeatureTypeStyle>(this, fts, type, range,subEvent);
        final StyleListener[] lists = listeners.getListeners(StyleListener.class);

        for (StyleListener listener : lists) {
            listener.featureTypeStyleChange(event);
        }

    }
    
    protected void fireFeatureTypeStyleChange(int type, Collection<? extends MutableFeatureTypeStyle> fts, NumberRange<Integer> range){
        //TODO make fire property change thread safe, preserve fire order
        
        final CollectionChangeEvent<MutableFeatureTypeStyle> event = new CollectionChangeEvent<MutableFeatureTypeStyle>(this,fts,type,range, null);
        final StyleListener[] lists = listeners.getListeners(StyleListener.class);
        
        for(StyleListener listener : lists){
            listener.featureTypeStyleChange(event);
        }
        
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public void addListener(StyleListener listener){
        listeners.add(StyleListener.class, listener);
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public void removeListener(StyleListener listener){
        listeners.remove(StyleListener.class, listener);
    }
    
    
}
