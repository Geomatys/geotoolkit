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

package org.geotoolkit.data.session;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.data.FeatureStoreContentEvent;
import org.geotoolkit.data.FeatureStoreListener;
import org.geotoolkit.data.FeatureStoreManagementEvent;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.storage.StorageListener;
import static org.geotoolkit.util.ArgumentChecks.*;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;
import org.opengis.filter.Id;

/**
 *  Abstract session which handle listeners and add convenient fire event methods.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class AbstractSession implements Session, FeatureStoreListener{

    private final Set<StorageListener> listeners = new HashSet<StorageListener>();
    private final FeatureStoreListener.Weak weakListener = new Weak(this);
    protected final FeatureStore store;

    public AbstractSession(final FeatureStore store){
        ensureNonNull("feature store", store);
        this.store = store;
        weakListener.registerSource(store);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureStore getFeatureStore() {
        return store;
    }

    /**
     * {@inheritDoc }
     *
     * This implementation fallback on
     * @see  #update(org.opengis.feature.type.Name, org.opengis.filter.Filter, java.util.Map)
     */
    @Override
    public void updateFeatures(final Name groupName, final Filter filter, final AttributeDescriptor desc, final Object value) throws DataStoreException {
        updateFeatures(groupName, filter, Collections.singletonMap(desc, value));
    }

    ////////////////////////////////////////////////////////////////////////////
    // listeners methods ///////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Forward event to listeners by changing source.
     */
    @Override
    public void structureChanged(FeatureStoreManagementEvent event){
        event = FeatureStoreManagementEvent.resetSource(this, event);
        final FeatureStoreListener[] lst;
        synchronized (listeners) {
            lst = listeners.toArray(new FeatureStoreListener[listeners.size()]);
        }
        for(final FeatureStoreListener listener : lst){
            listener.structureChanged(event);
        }
    }

    /**
     * Forward event to listeners by changing source.
     */
    @Override
    public void contentChanged(final FeatureStoreContentEvent event){
        sendEvent(FeatureStoreContentEvent.resetSource(this, event));
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void addStorageListener(final StorageListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void removeStorageListener(final StorageListener listener) {
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
    protected void fireFeaturesAdded(final Name name, final Id ids){
        sendEvent(FeatureStoreContentEvent.createAddEvent(this, name, ids));
    }

    /**
     * Fires a features update event.
     *
     * @param name of the schema where features where updated.
     * @param ids modified feature ids.
     */
    protected void fireFeaturesUpdated(final Name name, final Id ids){
        sendEvent(FeatureStoreContentEvent.createUpdateEvent(this, name, ids));
    }

    /**
     * Fires a features delete event.
     *
     * @param name of the schema where features where deleted
     * @param ids modified feature ids.
     */
    protected void fireFeaturesDeleted(final Name name, final Id ids){
        sendEvent(FeatureStoreContentEvent.createDeleteEvent(this, name, ids));
    }

    /**
     * Fires a session event. when new pending changes are added.
     */
    protected void fireSessionChanged(){
        sendEvent(FeatureStoreContentEvent.createSessionEvent(this));
    }

    /**
     * Forward a features event to all listeners.
     * @param event , event to send to listeners.
     */
    protected void sendEvent(final FeatureStoreContentEvent event){
        final StorageListener[] lst;
        synchronized (listeners) {
            lst = listeners.toArray(new StorageListener[listeners.size()]);
        }
        for(final StorageListener listener : lst){
            listener.contentChanged(event);
        }
    }

}
