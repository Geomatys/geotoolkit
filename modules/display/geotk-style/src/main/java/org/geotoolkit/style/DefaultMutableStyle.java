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
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.EventObject;
import java.util.List;
import javax.swing.event.EventListenerList;

import org.geotoolkit.util.NumberRange;
import org.geotoolkit.util.StringUtilities;
import org.geotoolkit.util.Utilities;
import org.geotoolkit.util.collection.NotifiedCheckedList;
import org.geotoolkit.util.converter.Classes;

import org.opengis.feature.type.Name;
import org.opengis.style.Description;
import org.opengis.style.SemanticType;
import org.opengis.style.StyleVisitor;
import org.opengis.style.Symbolizer;

/**
 * Mutable implementation of GeoAPI style.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultMutableStyle implements MutableStyle,FeatureTypeStyleListener{
    
    private final List<MutableFeatureTypeStyle> fts = new NotifiedCheckedList<MutableFeatureTypeStyle>(MutableFeatureTypeStyle.class) {

            @Override
            protected Object getLock() {
                return DefaultMutableStyle.this;
            }

            @Override
            protected void notifyAdd(final MutableFeatureTypeStyle item, final int index) {
                ftsListener.registerSource(item);
                fireFeatureTypeStyleChange(CollectionChangeEvent.ITEM_ADDED, item, NumberRange.create(index, index) );
            }

            @Override
            protected void notifyAdd(final Collection<? extends MutableFeatureTypeStyle> items, final NumberRange<Integer> range) {
                for(MutableFeatureTypeStyle item : items){
                    ftsListener.registerSource(item);
                }
                fireFeatureTypeStyleChange(CollectionChangeEvent.ITEM_ADDED, items, range);
            }

            @Override
            protected void notifyRemove(final MutableFeatureTypeStyle item, final int index) {
                ftsListener.unregisterSource(item);
                fireFeatureTypeStyleChange(CollectionChangeEvent.ITEM_REMOVED, item, NumberRange.create(index, index) );
            }

            @Override
            protected void notifyRemove(final Collection<? extends MutableFeatureTypeStyle> items, final NumberRange<Integer> range) {
                for(final MutableFeatureTypeStyle fts : items){
                    ftsListener.unregisterSource(fts);
                }
                fireFeatureTypeStyleChange(CollectionChangeEvent.ITEM_REMOVED, items, range );
            }

        };
        
    private final EventListenerList listeners = new EventListenerList();
    
    private final FeatureTypeStyleListener.Weak ftsListener = new FeatureTypeStyleListener.Weak(this);
   
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

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Style : ");
        builder.append(Classes.getShortClassName(this));
        builder.append(" [");
        builder.append(description);
        builder.append(']');

        if(!fts.isEmpty()){
            builder.append('\n');
            builder.append(StringUtilities.toStringTree(fts));
        }

        return builder.toString();
    }
    
    //--------------------------------------------------------------------------
    // listeners management ----------------------------------------------------
    //--------------------------------------------------------------------------
    
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue){
        //TODO make fire property change thread safe, preserve fire order
        
        final PropertyChangeEvent event = new PropertyChangeEvent(this,propertyName,oldValue,newValue);
        final PropertyChangeListener[] lists = listeners.getListeners(PropertyChangeListener.class);
        
        for(PropertyChangeListener listener : lists){
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

    //--------------------------------------------------------------------------
    // fts listener ------------------------------------------------------------
    //--------------------------------------------------------------------------

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        final int index = fts.indexOf(event.getSource());
        fireFeatureTypeStyleChange(CollectionChangeEvent.ITEM_CHANGED,
                (MutableFeatureTypeStyle)event.getSource(), NumberRange.create(index, index), event);
    }

    @Override
    public void ruleChange(CollectionChangeEvent<MutableRule> event) {
        final int index = fts.indexOf(event.getSource());
        fireFeatureTypeStyleChange(CollectionChangeEvent.ITEM_CHANGED,
                (MutableFeatureTypeStyle)event.getSource(), NumberRange.create(index, index), event);
    }

    @Override
    public void featureTypeNameChange(CollectionChangeEvent<Name> event) {
        final int index = fts.indexOf(event.getSource());
        fireFeatureTypeStyleChange(CollectionChangeEvent.ITEM_CHANGED,
                (MutableFeatureTypeStyle)event.getSource(), NumberRange.create(index, index), event);
    }

    @Override
    public void semanticTypeChange(CollectionChangeEvent<SemanticType> event) {
        final int index = fts.indexOf(event.getSource());
        fireFeatureTypeStyleChange(CollectionChangeEvent.ITEM_CHANGED,
                (MutableFeatureTypeStyle)event.getSource(), NumberRange.create(index, index), event);
    }


    /**
     * {@inheritDoc }
     */
    @Override
    public void addListener(StyleListener listener){
        addListener((PropertyChangeListener)listener);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void addListener(PropertyChangeListener listener){
        listeners.add(PropertyChangeListener.class, listener);
        if(listener instanceof StyleListener){
            listeners.add(StyleListener.class, (StyleListener)listener);
        }
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public void removeListener(PropertyChangeListener listener){
        listeners.remove(PropertyChangeListener.class, listener);
        if(listener instanceof StyleListener){
            listeners.remove(StyleListener.class, (StyleListener)listener);
        }
    }
    
    
}
