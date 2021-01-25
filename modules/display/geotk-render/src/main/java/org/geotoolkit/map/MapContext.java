/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2010, Geomatys
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
import java.io.IOException;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EventObject;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.referencing.CRS;
import static org.apache.sis.util.ArgumentChecks.*;
import org.apache.sis.util.Utilities;
import org.geotoolkit.style.StyleConstants;
import org.geotoolkit.util.collection.CollectionChangeEvent;
import org.geotoolkit.util.collection.NotifiedCopyOnWriteArrayList;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;


/**
 * Store context information about a map display.
 * This class does not store information about the map view.
 * Internal structure is a Tree which can be viewed as a list using method 'layers'.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public final class MapContext extends MapItem implements ItemListener, Serializable, LayerListener {

    public static final String AREA_OF_INTEREST_PROPERTY = "areaOfInterest";
    public static final String BOUNDS_PROPERTY = "bounds";

    protected final List<MapItem> items = new NotifiedCopyOnWriteArrayList<MapItem>() {

        @Override
        protected void notifyAdd(MapItem item, int index) {
            registerListenerSource(item);
            fireItemChange(CollectionChangeEvent.ITEM_ADDED,
                    Collections.singleton(item), NumberRange.create(index, true, index, true));
        }

        @Override
        protected void notifyAdd(Collection<? extends MapItem> items, NumberRange<Integer> range) {
            for(MapItem item : items){
                registerListenerSource(item);
            }
            fireItemChange(CollectionChangeEvent.ITEM_ADDED,items, range);
        }

        @Override
        protected void notifyRemove(MapItem item, int index) {
            unregisterListenerSource(item);
            fireItemChange(CollectionChangeEvent.ITEM_REMOVED,
                    Collections.singleton(item), NumberRange.create(index, true, index, true));
        }

        @Override
        protected void notifyRemove(Collection<? extends MapItem> items, NumberRange<Integer> range) {
            for(MapItem item : items){
                unregisterListenerSource(item);
            }
            fireItemChange(CollectionChangeEvent.ITEM_REMOVED,items, range);
        }
    };

    private final ItemListener.Weak itemListener = new ItemListener.Weak(this);

    private final AdapterList layers = new AdapterList();

    private final LayerListener.Weak layerListener = new LayerListener.Weak(this);

    private CoordinateReferenceSystem crs = null;
    private Envelope area = null;

    public MapContext(final CoordinateReferenceSystem crs) {
        ensureNonNull("crs", crs);
        this.crs = crs;
        desc = StyleConstants.DEFAULT_DESCRIPTION;
    }

    /**
     * Set the context crs. This is used when asking for the bounds property.
     * This reproject the area of interest to the new crs.
     */
    public void setCoordinateReferenceSystem(final CoordinateReferenceSystem crs) {
        ensureNonNull("crs", crs);

        synchronized (this) {
            if(Utilities.equalsIgnoreMetadata(this.crs,crs)) return;

            if(this.area != null){
                try {
                    //update the area of interest
                    final Envelope newEnv = Envelopes.transform(area, crs);
                    setAreaOfInterest(newEnv);
                } catch (TransformException ex) {
                    LOGGER.log(Level.WARNING, null, ex);
                }
            }
        }
    }

    /**
     * Get the context crs. This is used when asking for the bounds property.
     */
    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        return crs;
    }

    /**
     * Returns the living list of all items. You may add, remove or change items
     * of this list. In case this object is a map layer object, this list will be empty
     * and immutable.
     * This method is deprecated, use getComponents instead.
     * @return the live list
     */
    @Deprecated
    public List<MapItem> items() {
        return getComponents();
    }

    /**
     * Returns the living list of all items. You may add, remove or change items
     * of this list. In case this object is a map layer object, this list will be empty
     * and immutable.
     * @return the live list
     */
    public List<MapItem> getComponents() {
        return items;
    }

    /**
     * Returns the living list of all layers. You may add, remove or change layers
     * of this list. modifying this list automaticaly updates the tree structure of
     * the parent classe : MapItem.
     * @return the live list
     * @deprecated method will be removed, class replaced by MapLayers from SIS
     */
    @Deprecated
    public List<MapLayer> layers() {
        return layers;
    }

    /**
     * Get the bounding box of all the layers in this MapContext. If all the
     * layers cannot determine the bounding box in the speed required for each
     * layer, then null is returned. The bounds will be expressed in the
     * MapContext coordinate system.
     *
     * @return The bounding box of all layers.
     * @deprecated method will be removed, class replaced by MapLayers from SIS
     */
    @Deprecated
    public Envelope getBounds() throws IOException {
        return getEnvelope();
    }

    /**
     * Return the envelope of all layers.
     */
    public Envelope getEnvelope() throws IOException {
        GeneralEnvelope result = null;

        GeneralEnvelope env;
        CoordinateReferenceSystem sourceCrs;
        for (final MapLayer layer : layers) {

            env = new GeneralEnvelope(layer.getBounds());
            sourceCrs = env.getCoordinateReferenceSystem();

            if (!env.isAllNaN()) {

                boolean addToResult = false;
                if ((sourceCrs != null) && (crs != null) && !Utilities.equalsIgnoreMetadata(sourceCrs,crs)) {
                    try {
                        env = new GeneralEnvelope(Envelopes.transform(env, crs));
                        addToResult = true;
                    } catch (TransformException e) {
                        LOGGER.log(Level.WARNING,
                                "Data source and map context coordinate system differ, yet it was not possible to get a projected bounds estimate...",
                                e);
                    }
                } else {
                    addToResult = true;
                }

                if (addToResult) {
                    if (result == null) {
                        result = env;
                    } else {
                        result.add(env);
                    }
                }
            }
        }

        if (result == null|| result.isEmpty()) {
            //we could not find a valid envelope
            result = new GeneralEnvelope(CRS.getDomainOfValidity(crs));
        }
        return result;
    }

    /**
     * Get the favorite visible area.
     */
    public Envelope getAreaOfInterest() {
        if(area != null){
            return new GeneralEnvelope(area);
        }else{
            return null;
        }
    }

    /**
     * Set the favorite visible area. AreaOfInterest CRS and current MapContext CRS could be different.
     */
    public void setAreaOfInterest(final Envelope aoi) {
        ensureNonNull("area of interest", aoi);

        final Envelope oldEnv;
        synchronized (this) {
            oldEnv = this.area;
            if(this.area != null && oldEnv.equals(aoi)){
                return;
            }
            this.area = aoi;
        }
        firePropertyChange(AREA_OF_INTEREST_PROPERTY, oldEnv, this.area);
    }

    //--------------------------------------------------------------------------
    // listeners management ----------------------------------------------------
    //--------------------------------------------------------------------------

    @Override
    public void itemChange(final CollectionChangeEvent<MapItem> evt) {
        final MapItem source = (MapItem) evt.getSource();
        final int index = items.indexOf(source);
        fireItemChange(CollectionChangeEvent.ITEM_CHANGED,source,
                       NumberRange.create(index, true, index, true), evt);
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        final MapItem source = (MapItem) evt.getSource();
        final int index = items.indexOf(source);
        fireItemChange(CollectionChangeEvent.ITEM_CHANGED,source,
                       NumberRange.create(index, true, index, true), evt);
    }

    /**
     * Register an item listener.
     * @param listener item listener to register
     */
    public void addItemListener(final ItemListener listener){
        listeners.add(ItemListener.class, listener);
    }

    /**
     * Unregister an item listener.
     * @param listener item listener to unregister.
     */
    public void removeItemListener(final ItemListener listener){
        listeners.remove(ItemListener.class, listener);
    }

    /**
     * Register a context listener, this listener will be registered
     * also as an item listener.
     * @param listener Context listener to register
     */
    public void addContextListener(final ContextListener listener){
        listeners.add(ContextListener.class, listener);
        addItemListener(listener);
    }

    /**
     * Unregister a context listener, this listener will be registered
     * also as an item listener.
     * @param listener Context listener to unregister
     */
    public void removeContextListener(final ContextListener listener){
        listeners.remove(ContextListener.class, listener);
        removeItemListener(listener);
    }

    protected void updateTree(final int type, final MapLayer layer, final NumberRange<Integer> range, final EventObject orig) {

        //update the tree
        final List<MapLayer> list = Collections.singletonList(layer);
        final int startIndex = range.getMinValue();
        for(int i=list.size()-1;i>=0;i--){
            if(type == CollectionChangeEvent.ITEM_ADDED){
                updateItemAdd(list.get(i), startIndex+i);
            }else if(type == CollectionChangeEvent.ITEM_REMOVED){
                updateItemRemove(startIndex+i);
            }
        }

    }

    private void updateItemAdd(final MapLayer layer, final int index){
        if (index == 0) {
            items.add(0, layer);
        } else {
            final MapLayer beforeElement = layers.get(index-1);
            final MapContext parent = findParentForLayerNumber(this, index-1, new AtomicInteger(-1));
            final int beforeIndex = parent.getComponents().indexOf(beforeElement);
            parent.getComponents().add(beforeIndex+1, layer);
        }
    }

    private void updateItemRemove(final int index){
        findAndRemoveForLayerNumber(this, index, new AtomicInteger(-1));
    }

    protected void fireLayerChange(final int type, final MapLayer layer, final NumberRange<Integer> range, final EventObject orig) {

        //fire the event
        final CollectionChangeEvent<MapLayer> event = new CollectionChangeEvent<MapLayer>(this, layer, type, range, orig);
        final ContextListener[] lists = listeners.getListeners(ContextListener.class);
        for (ContextListener listener : lists) {
            listener.layerChange(event);
        }
    }

    protected void fireLayerChange(final int type, final Collection<MapLayer> layers, final NumberRange<Integer> range, final EventObject orig) {

        //fire the event
        final CollectionChangeEvent<MapLayer> event = new CollectionChangeEvent<MapLayer>(this, layers, type, range, orig);
        final ContextListener[] lists = listeners.getListeners(ContextListener.class);
        for (ContextListener listener : lists) {
            listener.layerChange(event);
        }
    }

    private MapContext findParentForLayerNumber(final MapContext root, final int wishedNumber, final AtomicInteger inc){

        for (MapItem item : root.getComponents()) {
            if (item instanceof MapLayer) {
                if (inc.incrementAndGet() == wishedNumber) {
                    return root;
                }
            } else if (item instanceof MapContext) {
                final MapContext test = findParentForLayerNumber((MapContext) item, wishedNumber, inc);
                if (test != null) {
                    return test;
                }
            }
        }
        return null;
    }

    private boolean findAndRemoveForLayerNumber(final MapContext root, final int wishedNumber, final AtomicInteger inc){

        for (MapItem item : root.getComponents()) {
            if (item instanceof MapLayer) {
                if (inc.incrementAndGet() == wishedNumber) {
                    root.getComponents().remove(item);
                    return true;
                }
            } else if (item instanceof MapContext) {
                final boolean found = findAndRemoveForLayerNumber((MapContext) item, wishedNumber, inc);
                if (found) {
                    return found;
                }
            }
        }
        return false;
    }


    //--------------------------------------------------------------------------
    // layer listener ----------------------------------------------------------
    //--------------------------------------------------------------------------

    /**
     * In case item is a MapLayer we register it using the layer listener.
     */
    protected void registerListenerSource(final MapItem item) {
        if (item instanceof MapLayer) {
            layerListener.registerSource((MapLayer) item);
        } else if (item instanceof MapContext) {
            itemListener.registerSource((MapContext) item);
        }
    }

    /**
     * In case item is a MapLayer we register it using the layer listener.
     */
    protected void unregisterListenerSource(final MapItem item) {
        if (item instanceof MapLayer) {
            layerListener.unregisterSource((MapLayer) item);
        } else if (item instanceof MapContext) {
            itemListener.unregisterSource((MapContext) item);
        }
    }

    @Override
    public void styleChange(final MapLayer source, final EventObject event) {
    }

    //--------------------------------------------------------------------------
    // item changes, update layers list ----------------------------------------
    //--------------------------------------------------------------------------

    protected void fireItemChange(final int type, final Collection<? extends MapItem> items, final NumberRange<Integer> range) {
        final CollectionChangeEvent<MapItem> event = new CollectionChangeEvent<MapItem>(this,items,type,range, null);
        final ItemListener[] lists = listeners.getListeners(ItemListener.class);

        for (ItemListener listener : lists) {
            listener.itemChange(event);
        }

        final Collection<MapLayer> candidates = new ArrayList<MapLayer>();
        final MapItem[] array = items.toArray(new MapItem[items.size()]);
        int from = -1;
        int to = -1;
        for(MapItem it : array){
            if(it instanceof MapLayer){
                candidates.add((MapLayer) it);
                final int index = layers.indexOf(it);
                if(from == -1){
                    from = index;
                    to = index;
                }else{
                    from = Math.min(index, from);
                    to = Math.max(index, to);
                }
            }
        }

        layers.clearCache();

        if(!candidates.isEmpty()){
            fireLayerChange(type, candidates, NumberRange.create(from, true, to, true), null);
        }
    }

    protected void fireItemChange(final int type, final MapItem item, final NumberRange<Integer> range, final EventObject orig) {
        final CollectionChangeEvent<MapItem> event = new CollectionChangeEvent<MapItem>(this, item, type, range, orig);
        final ItemListener[] lists = listeners.getListeners(ItemListener.class);

        for (ItemListener listener : lists) {
            listener.itemChange(event);
        }

        //forward event a MapLayer event if necessary

        int index =-1;
        if(item instanceof MapLayer){
            index = layers.indexOf(item);
        }

        layers.clearCache();

        if(item instanceof MapLayer){
            fireLayerChange(type, (MapLayer) item, NumberRange.create(index, true, index, true), orig);
        }
    }

    private List<MapLayer> createLayerList(final MapItem item, final List<MapLayer> buffer){
        if (item instanceof MapLayer) {
            buffer.add((MapLayer) item);
        } else if (item instanceof MapContext) {
            for (MapItem child : ((MapContext) item).getComponents()) {
                createLayerList(child, buffer);
            }
        }
        return buffer;
    }

    /**
     * Special list wish only raise events on add/remove calls.
     */
    private final class AdapterList extends AbstractList<MapLayer> {

        private MapLayer[] cache = null;

        private synchronized MapLayer[] getCache(){
            if(cache == null){
                final List<MapLayer> layers = createLayerList(MapContext.this, new ArrayList<MapLayer>());
                cache = layers.toArray(new MapLayer[layers.size()]);
            }
            return cache;
        }

        private synchronized void clearCache(){
            cache = null;
        }

        @Override
        public MapLayer get(final int index) {
            return getCache()[index];
        }

        @Override
        public MapLayer set(final int index, final MapLayer element) {
            final MapLayer old = remove(index);
            add(index,element);
            return old;
        }

        @Override
        public void add(final int index, final MapLayer element) {
            updateTree(CollectionChangeEvent.ITEM_ADDED, element, NumberRange.create(index, true, index, true), null);
        }

        @Override
        public MapLayer remove(final int index) {
            final MapLayer[] array = getCache();
            updateTree(CollectionChangeEvent.ITEM_REMOVED, array[index], NumberRange.create(index, true, index, true), null);
            final MapLayer removed = array[index];
            return removed;
        }

        @Override
        public int size() {
            return getCache().length;
        }

    };

}
