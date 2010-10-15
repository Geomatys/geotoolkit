/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Open Source Geospatial Foundation (OSGeo)
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

package org.geotoolkit.data.session;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.geotoolkit.data.DataStore;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.data.StorageContentEvent;
import org.geotoolkit.data.StorageManagementEvent;
import org.geotoolkit.data.StorageListener;

import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;
import org.opengis.filter.Id;

/**
 *  Abstract session which handle listeners and add convinient fire event methods.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class AbstractSession implements Session, StorageListener{

    private final Set<StorageListener> listeners = new HashSet<StorageListener>();
    private final StorageListener.Weak weakListener = new Weak(this);
    protected final DataStore store;

    public AbstractSession(DataStore store){
        if(store == null){
            throw new NullPointerException("DataStore can not be null.");
        }

        this.store = store;
        weakListener.registerSource(store);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public DataStore getDataStore() {
        return store;
    }

    /**
     * {@inheritDoc }
     *
     * This implementation fallback on
     * @see  #update(org.opengis.feature.type.Name, org.opengis.filter.Filter, java.util.Map)
     */
    @Override
    public void updateFeatures(Name groupName, Filter filter, AttributeDescriptor desc, Object value) throws DataStoreException {
        updateFeatures(groupName, filter, Collections.singletonMap(desc, value));
    }

    ////////////////////////////////////////////////////////////////////////////
    // listeners methods ///////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Forward event to listeners by changing source.
     */
    @Override
    public void structureChanged(StorageManagementEvent event){
        event = StorageManagementEvent.resetSource(this, event);
        final StorageListener[] lst;
        synchronized (listeners) {
            lst = listeners.toArray(new StorageListener[listeners.size()]);
        }
        for(final StorageListener listener : lst){
            listener.structureChanged(event);
        }
    }

    /**
     * Forward event to listeners by changing source.
     */
    @Override
    public void contentChanged(StorageContentEvent event){
        sendEvent(StorageContentEvent.resetSource(this, event));
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void addStorageListener(StorageListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void removeStorageListener(StorageListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    /**
     * Fires a features add event.
     *
     * @param name of the schema where features where added.
     * @param ids modified feature ids.
     */
    protected void fireFeaturesAdded(Name name, Id ids){
        sendEvent(StorageContentEvent.createAddEvent(this, name, ids));
    }

    /**
     * Fires a features update event.
     *
     * @param name of the schema where features where updated.
     * @param ids modified feature ids.
     */
    protected void fireFeaturesUpdated(Name name, Id ids){
        sendEvent(StorageContentEvent.createUpdateEvent(this, name, ids));
    }

    /**
     * Fires a features delete event.
     *
     * @param name of the schema where features where deleted
     * @param ids modified feature ids.
     */
    protected void fireFeaturesDeleted(Name name, Id ids){
        sendEvent(StorageContentEvent.createDeleteEvent(this, name, ids));
    }

    /**
     * Forward a features event to all listeners.
     * @param event , event to send to listeners.
     */
    protected void sendEvent(StorageContentEvent event){
        final StorageListener[] lst;
        synchronized (listeners) {
            lst = listeners.toArray(new StorageListener[listeners.size()]);
        }
        for(final StorageListener listener : lst){
            listener.contentChanged(event);
        }
    }

}
