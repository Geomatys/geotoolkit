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

import java.io.IOException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EventObject;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.style.StyleConstants;
import static org.apache.sis.util.ArgumentChecks.*;
import org.apache.sis.measure.NumberRange;
import org.geotoolkit.util.collection.CollectionChangeEvent;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.referencing.CRS;
import org.apache.sis.util.Utilities;


/**
 * The default implementation of the {@linkplain org.geotoolkit.map.MapContext}
 * interface.
 * Mapcontext has a tree structure which can be viewed as a MapLayer list using
 * the 'layers' method.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
final class DefaultMapContext extends DefaultMapItem implements MapContext, LayerListener {

    private final AdapterList layers = new AdapterList();

    private final LayerListener.Weak layerListener = new LayerListener.Weak(this);

    private CoordinateReferenceSystem crs = null;
    private Envelope area = null;

    public DefaultMapContext(final CoordinateReferenceSystem crs) {
        ensureNonNull("crs", crs);
        this.crs = crs;
        desc = StyleConstants.DEFAULT_DESCRIPTION;
    }

    /**
     * {@inheritDoc }
     * This method is thread safe.
     */
    @Override
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
     * {@inheritDoc }
     * This method is thread safe.
     */
    @Override
    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        return crs;
    }

    /**
     * {@inheritDoc }
     */
    @Override
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
     */
    @Override
    public Envelope getBounds() throws IOException {
        return getBounds(false);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Envelope getBounds(boolean onlyVisible) throws IOException {
        GeneralEnvelope result = null;

        GeneralEnvelope env;
        CoordinateReferenceSystem sourceCrs;
        for(final MapLayer layer : layers){
            if(onlyVisible && !layer.isVisible()) continue;

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
     * {@inheritDoc }
     */
    @Override
    public Envelope getAreaOfInterest() {
        if(area != null){
            return new GeneralEnvelope(area);
        }else{
            return null;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
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

    /**
     * {@inheritDoc }
     */
    @Override
    public void addContextListener(final ContextListener listener){
        listeners.add(ContextListener.class, listener);
        addItemListener(listener);
    }

    /**
     * {@inheritDoc }
     */
    @Override
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
        if(index==0){
            items.add(0, layer);
        }else{
            final MapLayer beforeElement = layers.get(index-1);
            final MapItem parent = findParentForLayerNumber(this, index-1, new AtomicInteger(-1));
            final int beforeIndex = parent.items().indexOf(beforeElement);
            parent.items().add(beforeIndex+1, layer);
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


    private MapItem findParentForLayerNumber(final MapItem root, final int wishedNumber, final AtomicInteger inc){

        for(MapItem item : root.items()){
            if(item instanceof MapLayer){
                if(inc.incrementAndGet() == wishedNumber){
                    return root;
                }
            }else{
                final MapItem test = findParentForLayerNumber(item, wishedNumber, inc);
                if(test != null){
                    return test;
                }
            }
        }
        return null;
    }

    private boolean findAndRemoveForLayerNumber(final MapItem root, final int wishedNumber, final AtomicInteger inc){

        for(MapItem item : root.items()){
            if(item instanceof MapLayer){
                if(inc.incrementAndGet() == wishedNumber){
                    root.items().remove(item);
                    return true;
                }
            }else{
                final boolean found = findAndRemoveForLayerNumber(item, wishedNumber, inc);
                if(found){
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
    @Override
    protected void registerListenerSource(final MapItem item) {
        if(item instanceof MapLayer){
            layerListener.registerSource((MapLayer) item);
        }else{
            super.registerListenerSource(item);
        }
    }

    /**
     * In case item is a MapLayer we register it using the layer listener.
     */
    @Override
    protected void unregisterListenerSource(final MapItem item) {
        if(item instanceof MapLayer){
            layerListener.unregisterSource((MapLayer) item);
        }else{
            super.unregisterListenerSource(item);
        }
    }

    @Override
    public void styleChange(final MapLayer source, final EventObject event) {
    }

    //--------------------------------------------------------------------------
    // item changes, update layers list ----------------------------------------
    //--------------------------------------------------------------------------

    @Override
    protected void fireItemChange(final int type, final Collection<? extends MapItem> items, final NumberRange<Integer> range) {
        super.fireItemChange(type, items, range);

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

    @Override
    protected void fireItemChange(final int type, final MapItem item, final NumberRange<Integer> range, final EventObject orig) {
        super.fireItemChange(type, item, range, orig);

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
        if(item instanceof MapLayer){
            buffer.add((MapLayer) item);
        }else{
            for(MapItem child : item.items()){
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
                final List<MapLayer> layers = createLayerList(DefaultMapContext.this, new ArrayList<MapLayer>());
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
