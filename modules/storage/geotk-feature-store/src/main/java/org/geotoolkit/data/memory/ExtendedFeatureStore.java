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
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.AbstractFeatureStore;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.data.FeatureStoreFactory;
import org.geotoolkit.data.FeatureStoreUtilities;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryCapabilities;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.data.session.SessionDecorator;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.SchemaException;
import org.geotoolkit.storage.StorageListener;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.feature.Feature;
import org.geotoolkit.feature.type.FeatureType;
import org.opengis.util.GenericName;
import org.geotoolkit.feature.type.PropertyDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.identity.FeatureId;
import org.opengis.geometry.Envelope;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Wraps a feature store and store additional queries which will be made available
 * like any other types.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public final class ExtendedFeatureStore extends AbstractFeatureStore{

    private final Map<GenericName,Query> queries = new ConcurrentHashMap<>();
    
    private final Map<GenericName,FeatureType> featureTypes = new ConcurrentHashMap<>();
    
    private final FeatureStore wrapped;

    public ExtendedFeatureStore(final FeatureStore wrapped) {
        super(null);
        ArgumentChecks.ensureNonNull("feature store", wrapped);
        this.wrapped = wrapped;
    }

    @Override
    public ParameterValueGroup getConfiguration() {
        return wrapped.getConfiguration();
    }

    @Override
    public FeatureStoreFactory getFactory() {
        return wrapped.getFactory();
    }

    public Set<GenericName> getQueryNames() {
        return queries.keySet();
    }

    public Query getQuery(final GenericName name){
        return queries.get(name);
    }

    public void addQuery(final Query query){
        ArgumentChecks.ensureNonNull("query name", query.getTypeName());
        queries.put(query.getTypeName(), query);
        featureTypes.clear();
    }

    public void removeQuery(final GenericName name){
        queries.remove(name);
        featureTypes.clear();
    }

    @Override
    public Set<GenericName> getNames() throws DataStoreException {
        final Set<GenericName> all = new HashSet<GenericName>(wrapped.getNames());
        all.addAll(getQueryNames());
        return all;
    }

    @Override
    public void createFeatureType(final GenericName typeName, final FeatureType featureType) throws DataStoreException {
        if(getQueryNames().contains(typeName)){
            throw new DataStoreException("Type for name "+typeName +" already exist.");
        }
        wrapped.createFeatureType(typeName, featureType);
    }

    @Override
    public void updateFeatureType(final GenericName typeName, final FeatureType featureType) throws DataStoreException {
        if(getQueryNames().contains(typeName)){
            throw new DataStoreException("Type for name "+typeName +" is a stred query, it can not be updated.");
        }
        wrapped.updateFeatureType(typeName, featureType);
        featureTypes.clear();
    }

    @Override
    public void deleteFeatureType(final GenericName typeName) throws DataStoreException {
        if(getQueryNames().contains(typeName)){
            removeQuery(typeName);
        }
        wrapped.deleteFeatureType(typeName);
        featureTypes.clear();
    }

    @Override
    public FeatureType getFeatureType(final GenericName typeName) throws DataStoreException {
        if(getQueryNames().contains(typeName)){
            // return from cache when available
            if (featureTypes.containsKey(typeName)) return featureTypes.get(typeName);
            
            final Query original = getQuery(typeName);
            try {
                final FeatureType ft = wrapped.getFeatureType(original);
                featureTypes.put(typeName, ft);
                return ft;
            } catch (SchemaException ex) {
                throw new DataStoreException(ex);
            }
        }
        return wrapped.getFeatureType(typeName);
    }
    
    @Override
    public boolean isWritable(final GenericName typeName) throws DataStoreException {
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
                return FeatureStoreUtilities.calculateCount(reader);
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
                return FeatureStoreUtilities.calculateEnvelope(reader);
            }finally{
                reader.close();
            }
        }
        return wrapped.getEnvelope(query);
    }

    public FeatureStore getWrapped() {
        return wrapped;
    }

    @Override
    public List<FeatureId> addFeatures(final GenericName groupName, 
            final Collection<? extends Feature> newFeatures, final Hints hints) throws DataStoreException {
        if(getQueryNames().contains(groupName)){
            throw new DataStoreException("Group name corresponed to a stored query, it can not be updated.");
        }
        return wrapped.addFeatures(groupName, newFeatures, hints);
    }

    @Override
    public void updateFeatures(final GenericName groupName, final Filter filter, 
            final PropertyDescriptor desc, final Object value) throws DataStoreException {
        if(getQueryNames().contains(groupName)){
            throw new DataStoreException("Group name corresponed to a stored query, it can not be updated.");
        }
        wrapped.updateFeatures(groupName, filter, desc, value);
    }

    @Override
    public void updateFeatures(final GenericName groupName, final Filter filter,
            final Map<? extends PropertyDescriptor, ? extends Object> values)
            throws DataStoreException {
        if(getQueryNames().contains(groupName)){
            throw new DataStoreException("Group name corresponed to a stored query, it can not be updated.");
        }
        wrapped.updateFeatures(groupName, filter, values);
    }

    @Override
    public void removeFeatures(final GenericName groupName, final Filter filter) throws DataStoreException {
        if(getQueryNames().contains(groupName)){
            throw new DataStoreException("Group name corresponed to a stored query, it can not be updated.");
        }
        wrapped.removeFeatures(groupName, filter);
    }

    @Override
    public FeatureReader getFeatureReader(final Query query) throws DataStoreException {
        final GenericName typeName = query.getTypeName();
        if(getQueryNames().contains(typeName)){
            final Query original = getQuery(typeName);
            final FeatureReader baseReader = wrapped.getFeatureReader(original);
            return handleRemaining(baseReader, query);
        }
        return wrapped.getFeatureReader(query);
    }

    @Override
    public FeatureWriter getFeatureWriter(final GenericName typeName, final Filter filter, final Hints hints) throws DataStoreException {
        if(getQueryNames().contains(typeName)){
            throw new DataStoreException("Group name corresponed to a stored query, writing is not possible.");
        }
        return wrapped.getFeatureWriter(typeName, filter);
    }

    @Override
    public FeatureWriter getFeatureWriterAppend(final GenericName typeName, final Hints hints) throws DataStoreException {
        if(getQueryNames().contains(typeName)){
            throw new DataStoreException("Group name corresponed to a stored query, writing is not possible.");
        }
        return wrapped.getFeatureWriterAppend(typeName);
    }

    @Override
    public void close() throws DataStoreException {
        wrapped.close();
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
        public FeatureStore getFeatureStore() {
            return ExtendedFeatureStore.this;
        }

    }

	@Override
	public void refreshMetaModel() {
		wrapped.refreshMetaModel();
		
	}

}
