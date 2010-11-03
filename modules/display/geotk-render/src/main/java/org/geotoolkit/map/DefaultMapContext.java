/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003 - 2008, Open Source Geospatial Foundation (OSGeo)
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
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EventObject;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.util.collection.CollectionChangeEvent;
import org.geotoolkit.style.StyleConstants;
import org.geotoolkit.util.NumberRange;

import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;


/**
 * The default implementation of the {@linkplain org.geotoolkit.map.MapContext}
 * interface
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
final class DefaultMapContext extends DefaultMapItem implements MapContext, LayerListener {

    private final AdapterList layers = new AdapterList();

    private final LayerListener.Weak layerListener = new LayerListener.Weak(this);

    private CoordinateReferenceSystem crs = null;
    private Envelope area = null;

    public DefaultMapContext(CoordinateReferenceSystem crs) {
        if(crs == null){
            throw new NullPointerException("CRS can't be null");
        }

        this.crs = crs;
        desc = StyleConstants.DEFAULT_DESCRIPTION;
    }

    /**
     * {@inheritDoc }
     * This method is thread safe.
     */
    @Override
    public void setCoordinateReferenceSystem(final CoordinateReferenceSystem crs) {
        if (crs == null) {
            throw new NullPointerException("CRS can't be null");
        }

        synchronized (this) {
            if(CRS.equalsIgnoreMetadata(this.crs,crs)) return;

            if(this.area != null){
                try {
                    //update the area of interest
                    final Envelope newEnv = CRS.transform(area, crs);
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
     * @return The bounding box of the features or null if unknown and too
     *         expensive for the method to calculate. TODO: when coordinate
     *         system information will be added reproject the bounds according
     *         to the current coordinate system
     */
    @Override
    public Envelope getBounds() throws IOException {
        GeneralEnvelope result = null;

        GeneralEnvelope env;
        CoordinateReferenceSystem sourceCrs;
        for(final MapLayer layer : layers){
            env = new GeneralEnvelope(layer.getBounds());
            sourceCrs = env.getCoordinateReferenceSystem();

            if (env == null) {
                continue;
            } else {

                if ((sourceCrs != null) && (crs != null) && !CRS.equalsIgnoreMetadata(sourceCrs,crs)) {
                    try {
                        env = new GeneralEnvelope(CRS.transform(env, crs));
                    } catch (TransformException e) {
                        LOGGER.log(Level.WARNING,
                                "Data source and map context coordinate system differ, yet it was not possible to get a projected bounds estimate...",
                                e);
                    }
                }

                if (result == null) {
                    result = env;
                } else {
                    result.add(env);
                }
            }

        }

        if(result == null){
            //we could not find a valid envelope
            result = new GeneralEnvelope(crs);
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
        if (aoi == null) {
            throw new NullPointerException("Area of interest can't be null");
        }

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
    public void addContextListener(ContextListener listener){
        listeners.add(ContextListener.class, listener);
        addItemListener(listener);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void removeContextListener(ContextListener listener){
        listeners.remove(ContextListener.class, listener);
        removeItemListener(listener);
    }

    protected void fireLayerChange(int type, MapLayer layer, NumberRange<Integer> range, EventObject orig) {

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

        //fire the event
        final CollectionChangeEvent<MapLayer> event = new CollectionChangeEvent<MapLayer>(this, layer, type, range, orig);
        final ContextListener[] lists = listeners.getListeners(ContextListener.class);
        for (ContextListener listener : lists) {
            listener.layerChange(event);
        }

    }

    private void updateItemAdd(MapLayer layer, int index){
        if(index==0){
            items.add(0, layer);
        }else{
            final MapLayer beforeElement = layers.get(index-1);
            final MapItem parent = findParentForLayerNumber(this, index-1, new AtomicInteger(-1));
            final int beforeIndex = parent.items().indexOf(beforeElement);
            parent.items().add(beforeIndex+1, layer);
        }
    }

    private void updateItemRemove(int index){
        findAndRemoveForLayerNumber(this, index, new AtomicInteger(-1));
    }

    private MapItem findParentForLayerNumber(MapItem root, int wishedNumber, AtomicInteger inc){

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

    private boolean findAndRemoveForLayerNumber(MapItem root, int wishedNumber, AtomicInteger inc){

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
    
    @Override
    public void propertyChange(PropertyChangeEvent event) {
        final int number = layers.indexOf((MapLayer)event.getSource());
        fireLayerChange(CollectionChangeEvent.ITEM_CHANGED, (MapLayer)event.getSource(), NumberRange.create(number,number),event);
    }

    @Override
    public void styleChange(MapLayer source, EventObject event) {
        final int number = layers.indexOf(source);
        fireLayerChange(CollectionChangeEvent.ITEM_CHANGED, source, NumberRange.create(number,number),event);
    }

    //--------------------------------------------------------------------------
    // item changes, update layers list ----------------------------------------
    //--------------------------------------------------------------------------

    @Override
    protected void fireItemChange(int type, Collection<? extends MapItem> item, NumberRange<Integer> range) {
        super.fireItemChange(type, item, range);
        layers.clearCache();
    }

    @Override
    protected void fireItemChange(int type, MapItem item, NumberRange<Integer> range, EventObject orig) {
        super.fireItemChange(type, item, range, orig);
        layers.clearCache();
    }

    private List<MapLayer> createLayerList(MapItem item, List<MapLayer> buffer){
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
        public MapLayer get(int index) {
            return getCache()[index];
        }

        @Override
        public MapLayer set(int index, MapLayer element) {
            final MapLayer old = remove(index);
            add(index,element);
            return old;
        }

        @Override
        public void add(int index, MapLayer element) {
            layerListener.registerSource(element);
            fireLayerChange(CollectionChangeEvent.ITEM_ADDED, element, NumberRange.create(index, index), null);
        }

        @Override
        public MapLayer remove(int index) {
            final MapLayer[] array = getCache();
            fireLayerChange(CollectionChangeEvent.ITEM_REMOVED, array[index], NumberRange.create(index, index), null);
            final MapLayer removed = array[index];
            layerListener.unregisterSource(removed);
            return removed;
        }

        @Override
        public int size() {
            return getCache().length;
        }

    };

}
