/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Geomatys
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
import java.util.logging.Logger;
import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import javax.swing.event.EventListenerList;

import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.style.CollectionChangeEvent;
import org.geotoolkit.style.StyleConstants;
import org.geotoolkit.util.NumberRange;
import org.geotoolkit.util.Utilities;
import org.geotoolkit.util.collection.NotifiedCheckedList;

import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.style.Description;


/**
 * The default implementation of the {@linkplain org.geotoolkit.map.MapContext}
 * interface
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
final class DefaultMapContext implements MapContext {

    private static final Logger LOGGER = org.geotoolkit.util.logging.Logging.getLogger("org.geotoolkit.map");

    private final List<MapLayer> layers = new NotifiedCheckedList<MapLayer>(MapLayer.class) {

        @Override
        protected Object getLock() {
            return DefaultMapContext.this;
        }

        @Override
        protected void notifyAdd(final MapLayer item, final int index) {
            fireLayerChange(CollectionChangeEvent.ITEM_ADDED, item, NumberRange.create(index, index),null );
            item.addLayerListener(layerListener);
        }

        @Override
        protected void notifyAdd(final Collection<? extends MapLayer> items, final NumberRange<Integer> range) {
            fireLayerChange(CollectionChangeEvent.ITEM_ADDED, items, range);
            for(final MapLayer layer : items){
                layer.addLayerListener(layerListener);
            }
        }

        @Override
        protected void notifyRemove(final MapLayer item, final int index) {
            fireLayerChange(CollectionChangeEvent.ITEM_REMOVED, item, NumberRange.create(index, index),null );
            item.removeLayerListener(layerListener);
        }

        @Override
        protected void notifyRemove(final Collection<? extends MapLayer> items, final NumberRange<Integer> range) {
            fireLayerChange(CollectionChangeEvent.ITEM_REMOVED, items, range );
            for(final MapLayer layer : items){
                layer.removeLayerListener(layerListener);
            }
        }

    };

    private final LayerListener layerListener = new LayerListener() {

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
    };

    private final Map<String,Object> parameters = new HashMap<String,Object>();

    private final EventListenerList listeners = new EventListenerList();

    private String name = null;

    private Description desc = null;

    private Envelope area = null;

    public DefaultMapContext(CoordinateReferenceSystem crs) {
        if(crs == null){
            throw new NullPointerException("CRS can't be null");
        }

        desc = StyleConstants.DEFAULT_DESCRIPTION;
        this.area = new GeneralEnvelope(crs);
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
        return desc;
    }

    /**
     * {@inheritDoc }
     * @param desc : Description can't be null
     */
    @Override
    public void setDescription(Description desc){
        if (desc == null) {
            throw new NullPointerException("description can't be null");
        }

        final Description oldDesc;
        synchronized (this) {
            oldDesc = this.desc;
            if(oldDesc.equals(desc)){
                return;
            }
            this.desc = desc;
        }
        firePropertyChange(DESCRIPTION_PROPERTY, oldDesc, this.desc);
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
            if(CRS.equalsIgnoreMetadata(this.area.getCoordinateReferenceSystem(),crs)) return;

            try {
                //update the area of interest
                final Envelope newEnv = CRS.transform(area, crs);
                setAreaOfInterest(newEnv);
            } catch (TransformException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }

        }
    }

    /**
     * {@inheritDoc }
     * This method is thread safe.
     */
    @Override
    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        return area.getCoordinateReferenceSystem();
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
        final CoordinateReferenceSystem crs = area.getCoordinateReferenceSystem();
        if (crs == null) {
            throw new IOException("CRS of this map context is null. Unable to get bounds.");
        }
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
                        LOGGER.log(Level.SEVERE,
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

    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue){
        //TODO make fire property change thread safe, preserve fire order

        final PropertyChangeEvent event = new PropertyChangeEvent(this,propertyName,oldValue,newValue);
        final ContextListener[] lists = listeners.getListeners(ContextListener.class);

        for(ContextListener listener : lists){
            listener.propertyChange(event);
        }

    }

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

    /**
     * {@inheritDoc }
     */
    @Override
    public void addContextListener(ContextListener listener){
        listeners.add(ContextListener.class, listener);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void removeContextListener(ContextListener listener){
        listeners.remove(ContextListener.class, listener);
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
        return new GeneralEnvelope(area);
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
            if(oldEnv.equals(aoi)){
                return;
            }

            this.area = aoi;

        }
        firePropertyChange(AREA_OF_INTEREST_PROPERTY, oldEnv, this.area);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setUserPropertie(String key,Object value){
        parameters.put(key, value);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Object getUserPropertie(String key){
        return parameters.get(key);
    }


}
