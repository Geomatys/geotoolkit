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

import java.io.IOException;
import java.util.Collection;
import java.util.EventObject;
import java.util.List;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
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
final class DefaultMapContext extends AbstractMapItem implements MapContext, LayerListener {

    private final List<MapLayer> layers = new CopyOnWriteArrayList<MapLayer>() {

        protected Object getLock() {
            return DefaultMapContext.this;
        }

        protected void notifyAdd(final MapLayer item, final int index) {
            fireLayerChange(CollectionChangeEvent.ITEM_ADDED, item, NumberRange.create(index, index),null);
            layerListener.registerSource(item);
        }

        protected void notifyAdd(final Collection<? extends MapLayer> items, final NumberRange<Integer> range) {
            fireLayerChange(CollectionChangeEvent.ITEM_ADDED, items, range);
            for(final MapLayer layer : items){
                layerListener.registerSource(layer);
            }
        }

        protected void notifyRemove(final MapLayer item, final int index) {
            fireLayerChange(CollectionChangeEvent.ITEM_REMOVED, item, NumberRange.create(index, index),null );
            layerListener.unregisterSource(item);
        }

        protected void notifyRemove(final Collection<? extends MapLayer> items, final NumberRange<Integer> range) {
            fireLayerChange(CollectionChangeEvent.ITEM_REMOVED, items, range );
            for(final MapLayer layer : items){
                layerListener.unregisterSource(layer);
            }
        }

        @Override
        public boolean add(final MapLayer element) throws IllegalArgumentException, UnsupportedOperationException {
            if(element == null) return false;
            final boolean added = super.add(element);
            if (added) {
                final int index = super.size() - 1;
                notifyAdd(element, index);
            }
            return added;
        }

        @Override
        public void add(final int index, final MapLayer element) throws IllegalArgumentException, UnsupportedOperationException {
            super.add(index, element);
            notifyAdd(element, index);
        }

        @Override
        public boolean addAll(final Collection<? extends MapLayer> collection) throws IllegalArgumentException, UnsupportedOperationException {
            final int startIndex = super.size();
            final boolean added = super.addAll(collection);
            if (added) {
                notifyAdd(collection, NumberRange.create(startIndex, super.size()-1) );
            }
            return added;
        }

        @Override
        public boolean addAll(final int index, final Collection<? extends MapLayer> collection) throws IllegalArgumentException, UnsupportedOperationException {
            final boolean added = super.addAll(index, collection);
            if (added) {
                notifyAdd(collection, NumberRange.create(index, index + collection.size()) );
            }
            return added;
        }

        @Override
        public boolean remove(final Object o) throws UnsupportedOperationException {
            final int index = super.indexOf(o);
            if (index >= 0) {
                super.remove(index);
                notifyRemove((MapLayer)o, index );
                return true;
            }
            return false;
        }

        @Override
        public MapLayer remove(final int index) throws UnsupportedOperationException {
            final MapLayer removed = super.remove(index);
            notifyRemove(removed, index );
            return removed;
        }

        @Override
        public boolean removeAll(final Collection<?> c) throws UnsupportedOperationException {
            //TODO handle remove by collection events if possible
            // to avoid several calls to remove
            boolean valid = false;
            for(final Object i : c){
                final boolean val = remove(i);
                if(val) valid = val;
            }
            return valid;
        }

        @Override
        public void clear() throws UnsupportedOperationException {
            if(!isEmpty()){
                final Collection<MapLayer> copy = new ArrayList<MapLayer>(this);
                final NumberRange<Integer> range = NumberRange.create(0, copy.size()-1);
                super.clear();
                notifyRemove(copy, range);
            }
        }

    };

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
     *
     *
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


    //--------------------------------------------------------------------------
    // listeners management ----------------------------------------------------
    //--------------------------------------------------------------------------

    protected void fireLayerChange(int type, MapLayer layer, NumberRange<Integer> range, EventObject orig) {
        //TODO make fire property change thread safe, preserve fire order

        final CollectionChangeEvent<MapLayer> event = new CollectionChangeEvent<MapLayer>(this, layer, type, range, orig);
        final ContextListener[] lists = listeners.getListeners(ContextListener.class);

        for (ContextListener listener : lists) {
            listener.layerChange(event);
        }

    }

    protected void fireLayerChange(int type, Collection<? extends MapLayer> layer, NumberRange<Integer> range){
        //TODO make fire property change thread safe, preserve fire order

        final CollectionChangeEvent<MapLayer> event = new CollectionChangeEvent<MapLayer>(this,layer,type,range, null);
        final ContextListener[] lists = listeners.getListeners(ContextListener.class);

        for(ContextListener listener : lists){
            listener.layerChange(event);
        }

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

    /**
     * {@inheritDoc }
     */
    @Override
    public void addContextListener(ContextListener listener){
        listeners.add(ContextListener.class, listener);
        addPropertyChangeListener(listener);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void removeContextListener(ContextListener listener){
        listeners.remove(ContextListener.class, listener);
        removePropertyChangeListener(listener);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void moveLayer(int sourcePosition, int destPosition) {

        if ((sourcePosition < 0) || (sourcePosition >= layers.size())) {
            throw new IndexOutOfBoundsException("Source position " + sourcePosition + " out of bounds");
        }

        if ((destPosition < 0) || (destPosition >= layers.size())) {
            throw new IndexOutOfBoundsException("Destination position " + destPosition + " out of bounds");
        }

        MapLayer removedLayer = (MapLayer) layers.remove(sourcePosition);
        layers.add(destPosition, removedLayer);
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

}
