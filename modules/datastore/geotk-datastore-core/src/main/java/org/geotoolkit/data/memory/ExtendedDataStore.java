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


package org.geotoolkit.data.memory;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.geotoolkit.data.AbstractDataStore;
import org.geotoolkit.data.DataStore;
import org.geotoolkit.data.DataUtilities;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.StorageListener;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryCapabilities;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.data.session.SessionDecorator;
import org.geotoolkit.feature.SchemaException;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.util.ArgumentChecks;

import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.identity.FeatureId;
import org.opengis.geometry.Envelope;

/**
 * Wraps a datastore and store additional queries which will be made available
 * like any other types.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public final class ExtendedDataStore extends AbstractDataStore{

    private final Map<Name,Query> queries = new ConcurrentHashMap<Name, Query>();
    
    private final DataStore wrapped;

    public ExtendedDataStore(final DataStore wrapped) {
        super(NO_NAMESPACE);
        ArgumentChecks.ensureNonNull("datastore", wrapped);
        this.wrapped = wrapped;
    }

    public Set<Name> getQueryNames() {
        return queries.keySet();
    }

    public Query getQuery(final Name name){
        return queries.get(name);
    }

    public void addQuery(final Query query, final Name name){
        queries.put(name, query);
    }

    public void removeQuery(final Name name){
        queries.remove(name);
    }

    @Override
    public Set<Name> getNames() throws DataStoreException {
        final Set<Name> all = new HashSet<Name>(wrapped.getNames());
        all.addAll(getQueryNames());
        return all;
    }

    @Override
    public void createSchema(final Name typeName, final FeatureType featureType) throws DataStoreException {
        if(getQueryNames().contains(typeName)){
            throw new DataStoreException("Type for name "+typeName +" already exist.");
        }
        wrapped.createSchema(typeName, featureType);
    }

    @Override
    public void updateSchema(final Name typeName, final FeatureType featureType) throws DataStoreException {
        if(getQueryNames().contains(typeName)){
            throw new DataStoreException("Type for name "+typeName +" is a stred query, it can not be updated.");
        }
        wrapped.updateSchema(typeName, featureType);
    }

    @Override
    public void deleteSchema(final Name typeName) throws DataStoreException {
        if(getQueryNames().contains(typeName)){
            removeQuery(typeName);
        }
        wrapped.deleteSchema(typeName);
    }

    @Override
    public FeatureType getFeatureType(final Name typeName) throws DataStoreException {
        if(getQueryNames().contains(typeName)){
            final Query original = getQuery(typeName);
            try {
                return wrapped.getFeatureType(original);
            } catch (SchemaException ex) {
                throw new DataStoreException(ex);
            }
        }
        return wrapped.getFeatureType(typeName);
    }
    
    @Override
    public boolean isWritable(final Name typeName) throws DataStoreException {
        if(getQueryNames().contains(typeName)){
            //stored queries are not writeable.
            return false;
        }
        return wrapped.isWritable(typeName);
    }

    @Override
    public QueryCapabilities getQueryCapabilities() {
        return wrapped.getQueryCapabilities();
    }

    @Override
    public long getCount(final Query query) throws DataStoreException {
        if(getQueryNames().contains(query.getTypeName())){
            final FeatureReader reader = getFeatureReader(query);
            try{
                return DataUtilities.calculateCount(reader);
            }finally{
                reader.close();
            }
        }
        return wrapped.getCount(query);
    }

    @Override
    public Envelope getEnvelope(final Query query) throws DataStoreException {
        if(getQueryNames().contains(query.getTypeName())){
            final FeatureReader reader = getFeatureReader(query);
            try{
                return DataUtilities.calculateEnvelope(reader);
            }finally{
                reader.close();
            }
        }
        return wrapped.getEnvelope(query);
    }

    @Override
    public List<FeatureId> addFeatures(final Name groupName, 
            final Collection<? extends Feature> newFeatures) throws DataStoreException {
        if(getQueryNames().contains(groupName)){
            throw new DataStoreException("Group name corresponed to a stored query, it can not be updated.");
        }
        return wrapped.addFeatures(groupName, newFeatures);
    }

    @Override
    public void updateFeatures(final Name groupName, final Filter filter, 
            final PropertyDescriptor desc, final Object value) throws DataStoreException {
        if(getQueryNames().contains(groupName)){
            throw new DataStoreException("Group name corresponed to a stored query, it can not be updated.");
        }
        wrapped.updateFeatures(groupName, filter, desc, value);
    }

    @Override
    public void updateFeatures(final Name groupName, final Filter filter,
            final Map<? extends PropertyDescriptor, ? extends Object> values)
            throws DataStoreException {
        if(getQueryNames().contains(groupName)){
            throw new DataStoreException("Group name corresponed to a stored query, it can not be updated.");
        }
        wrapped.updateFeatures(groupName, filter, values);
    }

    @Override
    public void removeFeatures(final Name groupName, final Filter filter) throws DataStoreException {
        if(getQueryNames().contains(groupName)){
            throw new DataStoreException("Group name corresponed to a stored query, it can not be updated.");
        }
        wrapped.removeFeatures(groupName, filter);
    }

    @Override
    public FeatureReader getFeatureReader(final Query query) throws DataStoreException {
        final Name typeName = query.getTypeName();
        if(getQueryNames().contains(typeName)){
            final Query original = getQuery(typeName);
            final FeatureReader baseReader = wrapped.getFeatureReader(original);
            return handleRemaining(baseReader, query);
        }
        return wrapped.getFeatureReader(query);
    }

    @Override
    public FeatureWriter getFeatureWriter(final Name typeName, final Filter filter) throws DataStoreException {
        if(getQueryNames().contains(typeName)){
            throw new DataStoreException("Group name corresponed to a stored query, writing is not possible.");
        }
        return wrapped.getFeatureWriter(typeName, filter);
    }

    @Override
    public FeatureWriter getFeatureWriterAppend(final Name typeName) throws DataStoreException {
        if(getQueryNames().contains(typeName)){
            throw new DataStoreException("Group name corresponed to a stored query, writing is not possible.");
        }
        return wrapped.getFeatureWriterAppend(typeName);
    }

    @Override
    public void dispose() {
        wrapped.dispose();
    }

    @Override
    public void addStorageListener(final StorageListener listener) {
        wrapped.addStorageListener(listener);
    }

    @Override
    public void removeStorageListener(final StorageListener listener) {
        wrapped.removeStorageListener(listener);
    }

    private class ExtendedSession extends SessionDecorator{

        public ExtendedSession(final Session session) {
            super(session);
        }

        @Override
        public DataStore getDataStore() {
            return ExtendedDataStore.this;
        }

    }

}
