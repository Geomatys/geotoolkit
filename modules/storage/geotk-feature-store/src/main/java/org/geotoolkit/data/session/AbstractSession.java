/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Geomatys
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

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.Resource;
import org.apache.sis.storage.event.StoreEvent;
import org.apache.sis.storage.event.StoreListener;
import static org.apache.sis.util.ArgumentChecks.*;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.data.FeatureStoreContentEvent;
import org.geotoolkit.storage.StorageEvent;
import org.geotoolkit.storage.StorageListener;
import org.opengis.filter.Id;
import org.opengis.metadata.Metadata;
import org.opengis.util.GenericName;

/**
 *  Abstract session which handle listeners and add convenient fire event methods.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public abstract class AbstractSession implements Resource, Session, StoreListener<StoreEvent> {

    private final StorageListener.Weak weakListener = new StorageListener.Weak(this);
    protected final FeatureStore store;
    protected final Set<StoreListener> listeners = new HashSet<>();

    public AbstractSession(final FeatureStore store){
        ensureNonNull("feature store", store);
        this.store = store;
        weakListener.registerSource(store);
    }

    @Override
    public Optional<GenericName> getIdentifier() {
        return Optional.empty();
    }

    @Override
    public Metadata getMetadata() throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureStore getFeatureStore() {
        return store;
    }

    ////////////////////////////////////////////////////////////////////////////
    // listeners methods ///////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    public <T extends StoreEvent> void addListener(Class<T> eventType, StoreListener<? super T> listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    public <T extends StoreEvent> void removeListener(Class<T> eventType, StoreListener<? super T> listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    /**
     * Forward an event to all listeners.
     * @param event , event to send to listeners.
     */
    protected void sendEvent(final StoreEvent event){
        final StoreListener[] lst;
        synchronized (listeners) {
            lst = listeners.toArray(new StoreListener[listeners.size()]);
        }
        for (final StoreListener listener : lst) {
            listener.eventOccured(event);
        }
    }

    /**
     * Forward given event, changing the source by this object.
     * For implementation use only.
     * @param event
     */
    public void forwardEvent(StorageEvent event){
        sendEvent(event.copy((Resource) this));
    }

    /**
     * Forward event to listeners by changing source.
     */
    @Override
    public void eventOccured(StoreEvent event) {
        if (event instanceof StorageEvent) event = ((StorageEvent)event).copy(this);
        sendEvent(event);
    }
    /**
     * Fires a features add event.
     *
     * @param name of the schema where features where added.
     * @param ids modified feature ids.
     */
    protected void fireFeaturesAdded(final GenericName name, final Id ids){
        sendEvent(FeatureStoreContentEvent.createAddEvent(this, name, ids));
    }

    /**
     * Fires a features update event.
     *
     * @param name of the schema where features where updated.
     * @param ids modified feature ids.
     */
    protected void fireFeaturesUpdated(final GenericName name, final Id ids){
        sendEvent(FeatureStoreContentEvent.createUpdateEvent(this, name, ids));
    }

    /**
     * Fires a features delete event.
     *
     * @param name of the schema where features where deleted
     * @param ids modified feature ids.
     */
    protected void fireFeaturesDeleted(final GenericName name, final Id ids){
        sendEvent(FeatureStoreContentEvent.createDeleteEvent(this, name, ids));
    }

    /**
     * Fires a session event. when new pending changes are added.
     */
    protected void fireSessionChanged(){
        sendEvent(FeatureStoreContentEvent.createSessionEvent(this));
    }
}
