/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Geomatys
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

package org.geotoolkit.data;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.geotoolkit.data.query.QueryUtilities;
import org.geotoolkit.data.query.Selector;
import org.geotoolkit.data.query.Source;
import org.geotoolkit.data.session.Session;

import org.opengis.feature.Feature;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;
import org.opengis.geometry.Envelope;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class AbstractFeatureCollection<F extends Feature> extends AbstractCollection<F>
        implements FeatureCollection<F>, StorageListener{

    //@todo not thread safe, I dont think it's important
    private final Set<StorageListener> listeners = new HashSet<StorageListener>();

    protected String id;
    protected final Source source;

    public AbstractFeatureCollection(String id, Source source){

        if(id == null){
            throw new NullPointerException("Feature collection ID must not be null.");
        }
        if(source == null){
            throw new NullPointerException("Feature collection source must not be null.");
        }

        this.id = id;
        this.source = source;

        if(source != null){
            final Collection<Session> sessions = QueryUtilities.getSessions(source, null);
            for(Session s : sessions){
                //register a weak listener, to memory leak since we don't know when to remove the listener.
                //@todo should we have a dispose method on the session ? I dont think so
                s.addStorageListener(new WeakStorageListener(source, this));
            }
        }
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getID() {
        return id;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Session getSession() {
        if(source instanceof Selector){
            return ((Selector)source).getSession();
        }else{
            return null;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Source getSource() {
        return source;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isWritable() throws DataStoreRuntimeException {
        try {
            return QueryUtilities.isWritable(source);
        } catch (DataStoreException ex) {
            throw new DataStoreRuntimeException(ex);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public final FeatureIterator<F> iterator(){
        return iterator(null);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Envelope getEnvelope() throws DataStoreException{
        try{
            return DataUtilities.calculateEnvelope(iterator());
        }catch(DataStoreRuntimeException ex){
            throw new DataStoreException(ex);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int size() throws DataStoreRuntimeException{
        return (int) DataUtilities.calculateCount(iterator());
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void update(Filter filter, AttributeDescriptor desc, Object value) throws DataStoreException {
        update(filter, Collections.singletonMap(desc, value));
    }

    ////////////////////////////////////////////////////////////////////////////
    // listeners methods ///////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Forward event to listeners by changing source.
     */
    @Override
    public void structureChanged(StorageManagementEvent event){
        final FeatureType currentType = getFeatureType();

        //forward events only if the collection is typed and match the type name
        if(currentType != null && currentType.getName().equals(event.getFeatureTypeName())){
            event = StorageManagementEvent.resetSource(this, event);
            for(final StorageListener listener : listeners){
                listener.structureChanged(event);
            }
        }
    }

    /**
     * Forward event to listeners by changing source.
     */
    @Override
    public void contentChanged(StorageContentEvent event){
        final FeatureType currentType = getFeatureType();

        //forward events only if the collection is typed and match the type name
        if(currentType != null && currentType.getName().equals(event.getFeatureTypeName())){
            sendEvent(StorageContentEvent.resetSource(this, event));
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void addStorageListener(StorageListener listener) {
        listeners.add(listener);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void removeStorageListener(StorageListener listener) {
        listeners.remove(listener);
    }

    /**
     * Fires a features add event.
     *
     * @param name of the schema where features where added.
     */
    protected void fireFeaturesAdded(Name name){
        sendEvent(StorageContentEvent.createAddEvent(this, name));
    }

    /**
     * Fires a features update event.
     *
     * @param name of the schema where features where updated.
     */
    protected void fireFeaturesUpdated(Name name){
        sendEvent(StorageContentEvent.createUpdateEvent(this, name));
    }

    /**
     * Fires a features delete event.
     *
     * @param name of the schema where features where deleted
     */
    protected void fireFeaturesDeleted(Name name){
        sendEvent(StorageContentEvent.createDeleteEvent(this, name));
    }

    /**
     * Forward a features event to all listeners.
     * @param event , event to send to listeners.
     */
    protected void sendEvent(StorageContentEvent event){
        for(final StorageListener listener : listeners){
            listener.contentChanged(event);
        }
    }

}
