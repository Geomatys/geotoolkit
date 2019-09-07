/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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

import java.util.Collection;
import java.util.Map;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.event.StoreEvent;
import org.apache.sis.storage.event.StoreListener;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.data.query.Query;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.version.Version;
import org.opengis.feature.Feature;
import org.opengis.filter.Filter;
import org.opengis.geometry.Envelope;

/**
 * Wraps a Session, this class is intended to be extended and methods may be
 * overwritten while other will have the normal behavior.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class SessionDecorator implements Session{

    protected final Session wrapped;

    public SessionDecorator(final Session wrapped) {
        ArgumentChecks.ensureNonNull("session", wrapped);
        this.wrapped = wrapped;
    }

    @Override
    public FeatureStore getFeatureStore() {
        return wrapped.getFeatureStore();
    }

    @Override
    public boolean isAsynchrone() {
        return wrapped.isAsynchrone();
    }

    @Override
    public Version getVersion() {
        return wrapped.getVersion();
    }

    @Override
    public FeatureCollection getFeatureCollection(final Query query) {
        return wrapped.getFeatureCollection(query);
    }

    @Override
    public FeatureIterator getFeatureIterator(final Query query) throws DataStoreException {
        return wrapped.getFeatureIterator(query);
    }

    @Override
    public void addFeatures(final String groupName, final Collection<? extends Feature> newFeatures)
            throws DataStoreException {
        wrapped.addFeatures(groupName, newFeatures);
    }


    @Override
    public void updateFeatures(final String groupName, final Filter filter,
            final Map<String, ?> values) throws DataStoreException {
        wrapped.updateFeatures(groupName, filter, values);
    }

    @Override
    public void removeFeatures(final String groupName, final Filter filter) throws DataStoreException {
        wrapped.removeFeatures(groupName, filter);
    }

    @Override
    public boolean hasPendingChanges() {
        return wrapped.hasPendingChanges();
    }

    @Override
    public void commit() throws DataStoreException {
        wrapped.commit();
    }

    @Override
    public void rollback() {
        wrapped.rollback();
    }

    @Override
    public long getCount(final Query query) throws DataStoreException {
        return wrapped.getCount(query);
    }

    @Override
    public Envelope getEnvelope(final Query query) throws DataStoreException {
        return wrapped.getEnvelope(query);
    }

    @Override
    public <T extends StoreEvent> void addListener(StoreListener<? super T> listener, Class<T> eventType) {
        wrapped.addListener(listener, eventType);
    }

    @Override
    public <T extends StoreEvent> void removeListener(StoreListener<? super T> listener, Class<T> eventType) {
        wrapped.removeListener(listener, eventType);
    }
}
