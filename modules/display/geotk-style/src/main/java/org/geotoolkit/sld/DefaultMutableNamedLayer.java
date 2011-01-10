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
import java.util.Collection;
import java.util.EventObject;
import java.util.List;
import javax.swing.event.EventListenerList;

import org.geotoolkit.util.collection.CollectionChangeEvent;
import org.geotoolkit.util.collection.CollectionChangeListener;
import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.StyleConstants;
import org.geotoolkit.style.StyleListener;
import org.geotoolkit.util.NumberRange;
import org.geotoolkit.util.StringUtilities;
import org.geotoolkit.util.Utilities;
import org.geotoolkit.util.collection.NotifiedCheckedList;
import org.geotoolkit.util.converter.Classes;

import org.opengis.sld.Constraint;
import org.opengis.sld.FeatureTypeConstraint;
import org.opengis.sld.SLDVisitor;
import org.opengis.style.Description;

/**
 * Default mutable named layer, thread safe.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
class DefaultMutableNamedLayer implements MutableNamedLayer,StyleListener{

    private final List<MutableLayerStyle> styles = new NotifiedCheckedList<MutableLayerStyle>(MutableLayerStyle.class) {

            @Override
            protected Object getLock() {
                return DefaultMutableNamedLayer.this;
            }

            @Override
            protected void notifyAdd(final MutableLayerStyle item, final int index) {
                styleListener.registerSource(item);
                fireStyleChange(CollectionChangeEvent.ITEM_ADDED, item, NumberRange.create(index, index));
            }

            @Override
            protected void notifyAdd(final Collection<? extends MutableLayerStyle> items, final NumberRange<Integer> range) {
                for(MutableLayerStyle item : items){
                    styleListener.registerSource(item);
                }
                fireStyleChange(CollectionChangeEvent.ITEM_ADDED, items, range);
            }

            @Override
            protected void notifyRemove(final MutableLayerStyle item, final int index) {
                styleListener.unregisterSource(item);
                fireStyleChange(CollectionChangeEvent.ITEM_REMOVED, item, NumberRange.create(index, index));
            }
            
            @Override
            protected void notifyRemove(final Collection<? extends MutableLayerStyle> items, final NumberRange<Integer> range) {
                for(final MutableLayerStyle mls : items){
                    styleListener.unregisterSource(mls);
                }
                fireStyleChange(CollectionChangeEvent.ITEM_REMOVED, items, range);
            }

            
        };
        
    private final StyleListener.Weak styleListener = new StyleListener.Weak(null,this);
    
    private final EventListenerList listeners = new EventListenerList();
    
    private final MutableLayerFeatureConstraints constraints = new DefaultMutableLayerFeatureConstraints();
    
    private String name = null;
    
    private Description description = StyleConstants.DEFAULT_DESCRIPTION;
        
    /**
     * default constructor
     */
    DefaultMutableNamedLayer(){
        constraints.addListener(new CollectionChangeListener<FeatureTypeConstraint>() {

            @Override
            public void collectionChange(CollectionChangeEvent<FeatureTypeConstraint> event) {
                fireConstraintChange(event);
            }
        });
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
        return description;
    }
    
    /**
     * {@inheritDoc }
     * This method is thread safe.
     */
    @Override
    public void setDescription(final Description desc) {
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
     * This is the live list.
     */
    @Override
    public List<MutableLayerStyle> styles() {
        return styles;
    }

    /**
     * {@inheritDoc }
     * This method is thread safe.
     */
    @Override
    public MutableLayerFeatureConstraints getConstraints() {
        return constraints;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Object accept(final SLDVisitor visitor, final Object extraData) {
        return visitor.visit(this, extraData);
    }

    //--------------------------------------------------------------------------
    // listeners management ----------------------------------------------------
    //--------------------------------------------------------------------------
    
    protected void firePropertyChange(final String propertyName, final Object oldValue, final Object newValue){
        //TODO make fire property change thread safe, preserve fire order
        
        final PropertyChangeEvent event = new PropertyChangeEvent(this,propertyName,oldValue,newValue);
        final LayerListener[] lists = listeners.getListeners(LayerListener.class);
        
        for(LayerListener listener : lists){
            listener.propertyChange(event);
        }
        
    }
    
    protected void fireStyleChange(final int type, final MutableLayerStyle layer, final NumberRange<Integer> range) {
        //TODO make fire property change thread safe, preserve fire order

        final CollectionChangeEvent<MutableLayerStyle> event = new CollectionChangeEvent<MutableLayerStyle>(this, layer, type, range, null);
        final LayerListener[] lists = listeners.getListeners(LayerListener.class);
        
        for(LayerListener listener : lists){
            listener.styleChange(event);
        }

    }
    
    protected void fireStyleChange(final int type, final MutableLayerStyle layer, final NumberRange<Integer> range, final EventObject subEvent) {
        //TODO make fire property change thread safe, preserve fire order

        final CollectionChangeEvent<MutableLayerStyle> event = new CollectionChangeEvent<MutableLayerStyle>(this, layer, type, range,subEvent);
        final LayerListener[] lists = listeners.getListeners(LayerListener.class);
        
        for(LayerListener listener : lists){
            listener.styleChange(event);
        }

    }
    
    protected void fireStyleChange(final int type, final Collection<? extends MutableLayerStyle> layer, final NumberRange<Integer> range){
        //TODO make fire property change thread safe, preserve fire order
        
        final CollectionChangeEvent<MutableLayerStyle> event = new CollectionChangeEvent<MutableLayerStyle>(this,layer,type,range, null);
        final LayerListener[] lists = listeners.getListeners(LayerListener.class);
        
        for(LayerListener listener : lists){
            listener.styleChange(event);
        }
        
    }
    
    protected void fireConstraintChange(final CollectionChangeEvent<? extends Constraint> event){
        CollectionChangeEvent<Constraint> newEvent = new CollectionChangeEvent<Constraint>(this,event.getItems(),event.getType(),event.getRange(),null);
        
        final LayerListener[] lists = listeners.getListeners(LayerListener.class);
        
        for(LayerListener listener : lists){
            listener.constraintChange(newEvent);
        }
    }

    //--------------------------------------------------------------------------
    // style listener-----------------------------------------------------------
    //--------------------------------------------------------------------------

    @Override
    public void propertyChange(final PropertyChangeEvent event) {
        fireStyleChange(CollectionChangeEvent.ITEM_CHANGED, (MutableLayerStyle)event.getSource(), null, event);
    }
    @Override
    public void featureTypeStyleChange(final CollectionChangeEvent<MutableFeatureTypeStyle> event) {
        fireStyleChange(CollectionChangeEvent.ITEM_CHANGED, (MutableLayerStyle)event.getSource(), null, event);
    }


    /**
     * {@inheritDoc }
     */
    @Override
    public void addListener(final LayerListener listener) {
        listeners.add(LayerListener.class, listener);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void removeListener(final LayerListener listener) {
        listeners.remove(LayerListener.class, listener);
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public boolean equals(final Object obj) {

        if(this == obj){
            return true;
        }

        if(obj == null || !this.getClass().equals(obj.getClass()) ){
            return false;
        }

        DefaultMutableNamedLayer other = (DefaultMutableNamedLayer) obj;

        return Utilities.equals(this.name, other.name)
                && this.description.equals(other.description)
                && this.constraints.equals(other.constraints)
                && this.styles.equals(other.styles);

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        int hash = 2;
        if(name != null) hash *= name.hashCode();
        hash *= description.hashCode();
        hash *= styles.hashCode();
        hash *= constraints.hashCode();
        return hash;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("NamedLayer : ");
        builder.append(Classes.getShortClassName(this));
        builder.append(" [");

        if(name != null){
            builder.append(" Name=");
            builder.append(name);
        }
        builder.append(" Description=");
        builder.append(description);
        builder.append(" Constraints=");
        builder.append(constraints);
        builder.append(" StylesSize=");
        builder.append(styles.size());
        builder.append(']');

        if(!styles.isEmpty()){
            builder.append('\n');
            builder.append(StringUtilities.toStringTree(styles));
        }

        return builder.toString();
    }
    
}
