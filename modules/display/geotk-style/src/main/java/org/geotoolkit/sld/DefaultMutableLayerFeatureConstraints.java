/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2004-2008, Open Source Geospatial Foundation (OSGeo)
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

package org.geotoolkit.sld;

import java.util.Collection;
import java.util.EventObject;
import java.util.List;
import javax.swing.event.EventListenerList;

import org.geotoolkit.style.CollectionChangeEvent;
import org.geotoolkit.style.CollectionChangeListener;
import org.geotoolkit.util.NotifiedCheckedList;
import org.geotoolkit.util.NumberRange;

import org.opengis.sld.Constraint;
import org.opengis.sld.FeatureTypeConstraint;
import org.opengis.sld.SLDVisitor;

/**
 * Default mutable feature constraints, thread safe.
 * 
 * @author Johann Sorel (Geomatys)
 */
class DefaultMutableLayerFeatureConstraints implements MutableLayerFeatureConstraints{

    private final List<FeatureTypeConstraint> constraints = new NotifiedCheckedList<FeatureTypeConstraint>(FeatureTypeConstraint.class) {

            @Override
            protected Object getLock() {
                return DefaultMutableLayerFeatureConstraints.this;
            }

            @Override
            protected void notifyAdd(final FeatureTypeConstraint item, final int index) {
                fireLibraryChange(CollectionChangeEvent.ITEM_ADDED, item, NumberRange.create(index, index));
            }

            @Override
            protected void notifyAdd(final Collection<? extends FeatureTypeConstraint> items, final NumberRange<Integer> range) {
                fireLibraryChange(CollectionChangeEvent.ITEM_ADDED, items, range);
            }

            @Override
            protected void notifyRemove(final FeatureTypeConstraint item, final int index) {
                fireLibraryChange(CollectionChangeEvent.ITEM_REMOVED, item, NumberRange.create(index, index) );
            }

            @Override
            protected void notifyRemove(final Collection<? extends FeatureTypeConstraint> items, final NumberRange<Integer> range) {
                fireLibraryChange(CollectionChangeEvent.ITEM_REMOVED, items, range );
            }
            
        };
    
    private final EventListenerList listeners = new EventListenerList();
    
    /**
     * default constructor
     */
    DefaultMutableLayerFeatureConstraints(){
    }
    
    /**
     * {@inheritDoc }
     * This method is thread safe.
     * This is the live list.
     */
    @Override
    public List<FeatureTypeConstraint> constraints() {
        return constraints;
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

    protected void fireLibraryChange(int type, FeatureTypeConstraint lib, NumberRange<Integer> range) {
        //TODO make fire property change thread safe, preserve fire order

        final CollectionChangeEvent<FeatureTypeConstraint> event = new CollectionChangeEvent<FeatureTypeConstraint>(this, lib, type, range, null);
        final CollectionChangeListener[] lists = listeners.getListeners(CollectionChangeListener.class);
        
        for(CollectionChangeListener listener : lists){
            listener.collectionChange(event);
        }

    }
    
    protected void fireLibraryChange(int type, FeatureTypeConstraint lib, NumberRange<Integer> range, EventObject subEvent) {
        //TODO make fire property change thread safe, preserve fire order

        final CollectionChangeEvent<FeatureTypeConstraint> event = new CollectionChangeEvent<FeatureTypeConstraint>(this, lib, type, range,subEvent);
        final CollectionChangeListener[] lists = listeners.getListeners(CollectionChangeListener.class);
        
        for(CollectionChangeListener listener : lists){
            listener.collectionChange(event);
        }

    }
    
    protected void fireLibraryChange(int type, Collection<? extends FeatureTypeConstraint> lib, NumberRange<Integer> range){
        //TODO make fire property change thread safe, preserve fire order
        
        final CollectionChangeEvent<FeatureTypeConstraint> event = new CollectionChangeEvent<FeatureTypeConstraint>(this,lib,type,range, null);
        final CollectionChangeListener[] lists = listeners.getListeners(CollectionChangeListener.class);
        
        for(CollectionChangeListener listener : lists){
            listener.collectionChange(event);
        }
        
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public void addListener(CollectionChangeListener<? extends Constraint> listener) {
        listeners.add(CollectionChangeListener.class, listener);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void removeListener(CollectionChangeListener<? extends Constraint> listener) {
        listeners.remove(CollectionChangeListener.class, listener);
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

        DefaultMutableLayerFeatureConstraints other = (DefaultMutableLayerFeatureConstraints) obj;

        return this.constraints.equals(other.constraints);

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        return constraints.hashCode() ;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[MutableLayerFeatureConstraints : ConstraintsSize=");
        builder.append(constraints.size());
        builder.append(']');
        return builder.toString();
    }
    
}
