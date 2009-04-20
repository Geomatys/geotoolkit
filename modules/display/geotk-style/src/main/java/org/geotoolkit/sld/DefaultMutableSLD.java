/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 * 
 *    (C) 2008, Open Source Geospatial Foundation (OSGeo)
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

import java.beans.PropertyChangeEvent;
import java.util.Collection;
import java.util.EventObject;
import java.util.List;
import javax.swing.event.EventListenerList;

import org.geotoolkit.style.CollectionChangeEvent;
import org.geotoolkit.style.StyleConstants;
import org.geotoolkit.util.NotifiedCheckedList;
import org.geotoolkit.util.NumberRange;

import org.geotoolkit.util.Utilities;
import org.opengis.sld.Constraint;
import org.opengis.sld.SLDLibrary;
import org.opengis.sld.SLDVisitor;
import org.opengis.style.Description;

/**
 * Default mutable Style Layer Descriptor, thread safe.
 * 
 * @author Johann Sorel (Geomatys)
 */
class DefaultMutableSLD implements MutableStyledLayerDescriptor{

    private final List<SLDLibrary> libraries = new NotifiedCheckedList<SLDLibrary>(SLDLibrary.class) {

            @Override
            protected Object getLock() {
                return DefaultMutableSLD.this;
            }

            @Override
            protected void notifyAdd(final SLDLibrary item, final int index) {
                fireLibraryChange(CollectionChangeEvent.ITEM_ADDED, item, NumberRange.create(index, index));
            }

            @Override
            protected void notifyAdd(final Collection<? extends SLDLibrary> items, final NumberRange<Integer> range) {
                fireLibraryChange(CollectionChangeEvent.ITEM_ADDED, items, range);
            }

            @Override
            protected void notifyRemove(final SLDLibrary item, final int index) {
                fireLibraryChange(CollectionChangeEvent.ITEM_REMOVED, item, NumberRange.create(index, index));
            }

            @Override
            protected void notifyRemove(Collection<? extends SLDLibrary> items, NumberRange<Integer> range) {
                fireLibraryChange(CollectionChangeEvent.ITEM_REMOVED, items, range);
            }
            
        };
    
    private final List<MutableLayer> layers = new NotifiedCheckedList<MutableLayer>(MutableLayer.class) {

            @Override
            protected Object getLock() {
                return DefaultMutableSLD.this;
            }

            @Override
            protected void notifyAdd(final MutableLayer item, final int index) {
                item.addListener(layerListener);
                fireLayerChange(CollectionChangeEvent.ITEM_ADDED, item, NumberRange.create(index, index));
            }

            @Override
            protected void notifyAdd(final Collection<? extends MutableLayer> items, final NumberRange<Integer> range) {
                for(MutableLayer item : items){
                    item.addListener(layerListener);
                }
                fireLayerChange(CollectionChangeEvent.ITEM_ADDED, items, range);
            }

            @Override
            protected void notifyRemove(final MutableLayer item, final int index) {
                item.removeListener(layerListener);
                fireLayerChange(CollectionChangeEvent.ITEM_REMOVED, item, NumberRange.create(index, index));
            }

            @Override
            protected void notifyRemove(Collection<? extends MutableLayer> items, NumberRange<Integer> range) {
                for(final MutableLayer ml : items){
                    ml.removeListener(layerListener);
                }
                fireLayerChange(CollectionChangeEvent.ITEM_REMOVED, items, range);
            }
            
        };
    
    private final LayerListener layerListener = new LayerListener() {

        @Override
        public void propertyChange(PropertyChangeEvent event) {
            fireLayerChange(CollectionChangeEvent.ITEM_CHANGED, (MutableLayer)event.getSource(), null, event);
        }

        @Override
        public void styleChange(CollectionChangeEvent<MutableLayerStyle> event) {
            fireLayerChange(CollectionChangeEvent.ITEM_CHANGED, (MutableLayer)event.getSource(), null, event);
        }

        @Override
        public void constraintChange(CollectionChangeEvent<Constraint> event) {
            fireLayerChange(CollectionChangeEvent.ITEM_CHANGED, (MutableLayer)event.getSource(), null, event);
        }
    };
        
    private final EventListenerList listeners = new EventListenerList();
    
    private String version = "1.1";
    
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
     * This method is thread safe.
     */
    @Override
    public String getVersion() {
        return version;
    }
    
    /**
     * {@inheritDoc }
     * This method is thread safe.
     */
    @Override
    public void setVersion(String version) {
        final String oldVersion;
        synchronized (this) {
            oldVersion = this.version;
            if (Utilities.equals(oldVersion, version)) {
                return;
            }
            this.version = version;
        }
        firePropertyChange(VERSION_PROPERTY, oldVersion, this.version);
    }
    
    /**
     * {@inheritDoc }
     * This method is thread safe.
     * This is the live list.
     */
    @Override
    public List<SLDLibrary> libraries() {
        return libraries;
    }

    /**
     * {@inheritDoc }
     * This method is thread safe.
     * This is the live list.
     */
    @Override
    public List<MutableLayer> layers() {
        return layers;
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
        final SLDListener[] lists = listeners.getListeners(SLDListener.class);
        
        for(SLDListener listener : lists){
            listener.propertyChange(event);
        }
        
    }
    
    protected void fireLibraryChange(int type, SLDLibrary lib, NumberRange<Integer> range) {
        //TODO make fire property change thread safe, preserve fire order

        final CollectionChangeEvent<SLDLibrary> event = new CollectionChangeEvent<SLDLibrary>(this, lib, type, range, null);
        final SLDListener[] lists = listeners.getListeners(SLDListener.class);
        
        for(SLDListener listener : lists){
            listener.libraryChange(event);
        }

    }
    
    protected void fireLibraryChange(int type, SLDLibrary lib, NumberRange<Integer> range, EventObject subEvent) {
        //TODO make fire property change thread safe, preserve fire order

        final CollectionChangeEvent<SLDLibrary> event = new CollectionChangeEvent<SLDLibrary>(this, lib, type, range,subEvent);
        final SLDListener[] lists = listeners.getListeners(SLDListener.class);
        
        for(SLDListener listener : lists){
            listener.libraryChange(event);
        }

    }
    
    protected void fireLibraryChange(int type, Collection<? extends SLDLibrary> lib, NumberRange<Integer> range){
        //TODO make fire property change thread safe, preserve fire order
        
        final CollectionChangeEvent<SLDLibrary> event = new CollectionChangeEvent<SLDLibrary>(this,lib,type,range, null);
        final SLDListener[] lists = listeners.getListeners(SLDListener.class);
        
        for(SLDListener listener : lists){
            listener.libraryChange(event);
        }
        
    }
    
    protected void fireLayerChange(int type, MutableLayer layer, NumberRange<Integer> range) {
        //TODO make fire property change thread safe, preserve fire order

        final CollectionChangeEvent<MutableLayer> event = new CollectionChangeEvent<MutableLayer>(this, layer, type, range, null);
        final SLDListener[] lists = listeners.getListeners(SLDListener.class);
        
        for(SLDListener listener : lists){
            listener.layerChange(event);
        }

    }
    
    protected void fireLayerChange(int type, MutableLayer layer, NumberRange<Integer> range, EventObject subEvent) {
        //TODO make fire property change thread safe, preserve fire order

        final CollectionChangeEvent<MutableLayer> event = new CollectionChangeEvent<MutableLayer>(this, layer, type, range,subEvent);
        final SLDListener[] lists = listeners.getListeners(SLDListener.class);
        
        for(SLDListener listener : lists){
            listener.layerChange(event);
        }

    }
    
    protected void fireLayerChange(int type, Collection<? extends MutableLayer> layer, NumberRange<Integer> range){
        //TODO make fire property change thread safe, preserve fire order
        
        final CollectionChangeEvent<MutableLayer> event = new CollectionChangeEvent<MutableLayer>(this,layer,type,range, null);
        final SLDListener[] lists = listeners.getListeners(SLDListener.class);
        
        for(SLDListener listener : lists){
            listener.layerChange(event);
        }
        
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public void addListener(SLDListener listener) {
        listeners.add(SLDListener.class, listener);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void removeListener(SLDListener listener) {
        listeners.remove(SLDListener.class, listener);
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

        DefaultMutableSLD other = (DefaultMutableSLD) obj;

        return Utilities.equals(this.name, other.name)
                && Utilities.equals(this.version, other.version)
                && this.description.equals(other.description)
                && this.layers.equals(other.layers)
                && this.libraries.equals(other.libraries);

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        int hash = 3;
        if(name != null) hash *= name.hashCode();
        if(version != null) hash *= version.hashCode();
        hash *= description.hashCode();
        hash *= layers.hashCode();
        hash *= libraries.hashCode();
        return hash;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[MutableSLD : ");
        if(name != null){
            builder.append(" Name=");
            builder.append(name.toString());
        }
        builder.append(" Description=");
        builder.append(description.toString());
        if(version != null){
            builder.append(" Version=");
            builder.append(version.toString());
        }
        builder.append(" LayerSize=");
        builder.append(layers.size());
        builder.append(" LibrarySize=");
        builder.append(libraries.size());
        builder.append(']');
        return builder.toString();
    }
    
    /**
     * {@inheritDoc }
     * @TODO the complete clone must be applied on the attributes.
     */
    @Override
    public DefaultMutableSLD clone() {
        DefaultMutableSLD object = null;
        try {
            object =  (DefaultMutableSLD) super.clone();
        } catch(CloneNotSupportedException cnse) {
		cnse.printStackTrace(System.err);
        }
        return object;
    }
}
