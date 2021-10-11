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
import java.util.Objects;
import javax.swing.event.EventListenerList;

import org.geotoolkit.util.collection.CollectionChangeEvent;
import org.geotoolkit.util.collection.CollectionChangeListener;
import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.StyleConstants;
import org.geotoolkit.style.StyleListener;
import org.apache.sis.measure.NumberRange;
import org.geotoolkit.util.collection.NotifiedCheckedList;
import org.apache.sis.util.Classes;

import org.opengis.sld.Constraint;
import org.opengis.sld.FeatureTypeConstraint;
import org.opengis.sld.SLDVisitor;
import org.opengis.sld.Source;
import org.opengis.style.Description;

import static org.apache.sis.util.ArgumentChecks.*;
import org.geotoolkit.util.StringUtilities;

/**
 * Default mutable user layer, thread safe.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
class DefaultMutableUserLayer implements MutableUserLayer,StyleListener{

    private final List<MutableStyle> styles = new NotifiedCheckedList<MutableStyle>(MutableStyle.class) {

            @Override
            protected Object getLock() {
                return DefaultMutableUserLayer.this;
            }

            @Override
            protected void notifyAdd(final MutableStyle item, final int index) {
                styleListener.registerSource(item);
                fireStyleChange(CollectionChangeEvent.ITEM_ADDED, item, NumberRange.create(index, true, index, true));
            }

            @Override
            protected void notifyAdd(final Collection<? extends MutableStyle> items, final NumberRange<Integer> range) {
                for(MutableLayerStyle item : items){
                    styleListener.registerSource(item);
                }
                fireStyleChange(CollectionChangeEvent.ITEM_ADDED, items, range);
            }

            @Override
            protected void notifyRemove(final MutableStyle item, final int index) {
                styleListener.unregisterSource(item);
                fireStyleChange(CollectionChangeEvent.ITEM_REMOVED, item, NumberRange.create(index, true, index, true));
            }

            @Override
            protected void notifyRemove(final Collection<? extends MutableStyle> items, final NumberRange<Integer> range) {
                for(final MutableStyle item : items){
                    styleListener.unregisterSource(item);
                }
                fireStyleChange(CollectionChangeEvent.ITEM_REMOVED, items, range);
            }

            @Override
            protected void notifyChange(MutableStyle oldItem, MutableStyle newItem, int index) {
                if(oldItem != null){
                    styleListener.unregisterSource(oldItem);
                }
                if(newItem != null){
                    styleListener.registerSource(newItem);
                }
                fireStyleChange(CollectionChangeEvent.ITEM_CHANGED, oldItem, NumberRange.create(index, true, index, true));
            }

        };

    private final StyleListener.Weak styleListener = new StyleListener.Weak(null,this);

    private final CollectionChangeListener collectionListener = new CollectionChangeListener<FeatureTypeConstraint>(){

        @Override
        public void collectionChange(CollectionChangeEvent<FeatureTypeConstraint> event) {
            fireConstraintChange(event);
        }
    };

    private final EventListenerList listeners = new EventListenerList();

    private MutableConstraints constraints = new DefaultMutableLayerFeatureConstraints();

    private Source source = null;

    private String name = null;

    private Description description = StyleConstants.DEFAULT_DESCRIPTION;

    DefaultMutableUserLayer(){
        constraints.addListener(collectionListener);
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
            if (Objects.equals(oldName, name)) {
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
        ensureNonNull("description", desc);

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
    public Source getSource() {
        return source;
    }

    /**
     * {@inheritDoc }
     * This method is thread safe.
     */
    @Override
    public void setSource(final Source source) {
        final Source oldSource;
        synchronized (this) {
            oldSource = this.source;
            if(Objects.equals(oldSource, source)){
                return;
            }
            this.source = source;
        }
        firePropertyChange(SOURCE_PROPERTY, oldSource, this.source);


    }

    /**
     * {@inheritDoc }
     * This method is thread safe.
     */
    @Override
    public MutableConstraints getConstraints() {
        return constraints;
    }

    /**
     * {@inheritDoc }
     * This method is thread safe.
     */
    @Override
    public void setConstraints(final MutableConstraints constraints) {
        ensureNonNull("constraints", constraints);

        final MutableConstraints oldConstraints;
        synchronized (this) {
            oldConstraints = this.constraints;
            if(oldConstraints.equals(constraints)){
                return;
            }

            this.constraints.removeListener(collectionListener);
            this.constraints = constraints;
            this.constraints.addListener(collectionListener);
        }
        firePropertyChange(CONSTRAINTS_PROPERTY, oldConstraints, this.constraints);
    }

    /**
     * {@inheritDoc }
     * This method is thread safe.
     * This is the live list.
     */
    @Override
    public List<MutableStyle> styles() {
        return styles;
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

        DefaultMutableUserLayer other = (DefaultMutableUserLayer) obj;

        return Objects.equals(this.name, other.name)
                && Objects.equals(this.source, other.source)
                && this.description.equals(other.description)
                && Objects.equals(this.constraints, other.constraints)
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
        if(source != null) hash *= source.hashCode();
        if(constraints != null) hash *= constraints.hashCode();
        hash *= styles.hashCode();
        return hash;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("UserLayer : ");
        builder.append(Classes.getShortClassName(this));
        builder.append(" [");

        if(name != null){
            builder.append(" Name=");
            builder.append(name);
        }
        builder.append(" Description=");
        builder.append(description);
        if(source != null){
            builder.append(" Source=");
            builder.append(source);
        }
        if(constraints != null){
            builder.append(" Constraints=");
            builder.append(constraints);
        }
        builder.append(" StyleSize=");
        builder.append(styles.size());
        builder.append(']');

        if(!styles.isEmpty()){
            builder.append(StringUtilities.toStringTree("", styles));
        }

        return builder.toString();
    }

}
