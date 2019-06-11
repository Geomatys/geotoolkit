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
import org.apache.sis.parameter.Parameters;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStoreProvider;
import org.apache.sis.storage.IllegalNameException;
import org.apache.sis.storage.Query;
import org.apache.sis.storage.UnsupportedQueryException;
import org.apache.sis.storage.event.ChangeEvent;
import org.apache.sis.storage.event.ChangeListener;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.data.AbstractFeatureStore;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.data.FeatureStoreUtilities;
import org.geotoolkit.data.FeatureStreams;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.query.QueryCapabilities;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.internal.data.GenericNameIndex;
import org.geotoolkit.util.NamesExt;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.MismatchedFeatureException;
import org.opengis.filter.Filter;
import org.opengis.filter.identity.FeatureId;
import org.opengis.geometry.Envelope;
import org.opengis.util.GenericName;

/**
 * Wraps a feature store and store additional queries which will be made available
 * like any other types.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public final class ExtendedFeatureStore extends AbstractFeatureStore {

    private final GenericNameIndex<Query> queries = new GenericNameIndex<>();

    private final Map<GenericName,FeatureType> featureTypes = new ConcurrentHashMap<>();

    private final FeatureStore wrapped;

    public ExtendedFeatureStore(final FeatureStore wrapped) {
        super(null);
        ArgumentChecks.ensureNonNull("feature store", wrapped);
        this.wrapped = wrapped;
    }

    @Override
    public Parameters getOpenParameters() {
        return Parameters.castOrWrap(wrapped.getOpenParameters());
    }

    @Override
    public GenericName getIdentifier() throws DataStoreException {
        return wrapped.getIdentifier();
    }

    @Override
    public DataStoreProvider getProvider() {
        return wrapped.getProvider();
    }

    public Set<GenericName> getQueryNames() {
        return queries.getNames();
    }

    public Query getQuery(final GenericName name) throws IllegalNameException{
        return queries.get(this, name.toString());
    }

    public void addQuery(final Query query) throws IllegalNameException{
        if (!(query instanceof org.geotoolkit.data.query.Query))  throw new IllegalNameException("Unsuported query type");

        final org.geotoolkit.data.query.Query gquery = (org.geotoolkit.data.query.Query) query;
        ArgumentChecks.ensureNonNull("query name", gquery.getTypeName());
        queries.add(this, NamesExt.valueOf(gquery.getTypeName()), query);
        featureTypes.clear();
    }

    public void removeQuery(final GenericName name) throws IllegalNameException{
        queries.remove(this, name);
        featureTypes.clear();
    }

    @Override
    public Set<GenericName> getNames() throws DataStoreException {
        final Set<GenericName> all = new HashSet<>(wrapped.getNames());
        all.addAll(getQueryNames());
        return all;
    }

    @Override
    public void createFeatureType(final FeatureType featureType) throws DataStoreException {
        if(getQueryNames().contains(featureType.getName())){
            throw new DataStoreException("Type for name "+featureType.getName() +" already exist.");
        }
        wrapped.createFeatureType(featureType);
    }

    @Override
    public void updateFeatureType(final FeatureType featureType) throws DataStoreException {
        if(getQueryNames().contains(featureType.getName())){
            throw new DataStoreException("Type for name "+featureType.getName() +" is a stred query, it can not be updated.");
        }
        wrapped.updateFeatureType(featureType);
        featureTypes.clear();
    }

    @Override
    public void deleteFeatureType(final String typeName) throws DataStoreException {
        if(queries.get(this, typeName)!=null){
            removeQuery(NamesExt.valueOf(typeName));
        }
        wrapped.deleteFeatureType(typeName);
        featureTypes.clear();
    }

    @Override
    public FeatureType getFeatureType(final String typeName) throws DataStoreException {
        if(queries.get(this, typeName)!=null){
            // return from cache when available
            final GenericName n = NamesExt.valueOf(typeName);
            if (featureTypes.containsKey(n)) {
                return featureTypes.get(n);
            }

            final Query original = queries.get(this, typeName);
            try {
                final FeatureType ft = wrapped.getFeatureType(original);
                featureTypes.put(n, ft);
                return ft;
            } catch (MismatchedFeatureException ex) {
                throw new DataStoreException(ex);
            }
        }
        return wrapped.getFeatureType(typeName);
    }

    @Override
    public boolean isWritable(final String typeName) throws DataStoreException {
        if(queries.get(this, typeName)!=null){
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
        if (!(query instanceof org.geotoolkit.data.query.Query))  throw new UnsupportedQueryException();

        final org.geotoolkit.data.query.Query gquery = (org.geotoolkit.data.query.Query) query;
        if(queries.get(this, gquery.getTypeName())!=null){
            try(FeatureReader reader = getFeatureReader(query)) {
                return FeatureStoreUtilities.calculateCount(reader);
            }
        }
        return wrapped.getCount(query);
    }

    @Override
    public Envelope getEnvelope(final Query query) throws DataStoreException {
        if (!(query instanceof org.geotoolkit.data.query.Query))  throw new UnsupportedQueryException();

        final org.geotoolkit.data.query.Query gquery = (org.geotoolkit.data.query.Query) query;
        if(queries.get(this, gquery.getTypeName())!=null){
            try(FeatureReader reader = getFeatureReader(query)) {
                return FeatureStoreUtilities.calculateEnvelope(reader);
            }
        }
        return wrapped.getEnvelope(query);
    }

    public FeatureStore getWrapped() {
        return wrapped;
    }

    @Override
    public List<FeatureId> addFeatures(final String typeName,
            final Collection<? extends Feature> newFeatures, final Hints hints) throws DataStoreException {
        if(queries.get(this, typeName)!=null){
            throw new DataStoreException("Group name corresponed to a stored query, it can not be updated.");
        }
        return wrapped.addFeatures(typeName, newFeatures, hints);
    }


    @Override
    public void updateFeatures(final String typeName, final Filter filter,
            final Map<String, ?> values)
            throws DataStoreException {
        if(queries.get(this, typeName)!=null){
            throw new DataStoreException("Group name corresponed to a stored query, it can not be updated.");
        }
        wrapped.updateFeatures(typeName, filter, values);
    }

    @Override
    public void removeFeatures(final String typeName, final Filter filter) throws DataStoreException {
        if(queries.get(this, typeName)!=null){
            throw new DataStoreException("Group name corresponed to a stored query, it can not be updated.");
        }
        wrapped.removeFeatures(typeName, filter);
    }

    @Override
    public FeatureReader getFeatureReader(final Query query) throws DataStoreException {
        if (!(query instanceof org.geotoolkit.data.query.Query))  throw new UnsupportedQueryException();

        final org.geotoolkit.data.query.Query gquery = (org.geotoolkit.data.query.Query) query;
        final String typeName = gquery.getTypeName();
        if(queries.get(this, typeName)!=null){
            final Query original = queries.get(this, typeName);
            final FeatureReader baseReader = wrapped.getFeatureReader(original);
            return FeatureStreams.subset(baseReader, gquery);
        }
        return wrapped.getFeatureReader(query);
    }

    @Override
    public FeatureWriter getFeatureWriter(Query query) throws DataStoreException {
        if (!(query instanceof org.geotoolkit.data.query.Query))  throw new UnsupportedQueryException();

        final org.geotoolkit.data.query.Query gquery = (org.geotoolkit.data.query.Query) query;
        if(queries.get(this, gquery.getTypeName())!=null){
            throw new DataStoreException("Group name corresponed to a stored query, writing is not possible.");
        }
        return wrapped.getFeatureWriter(query);
    }

    @Override
    public void close() throws DataStoreException {
        wrapped.close();
    }

    @Override
    public <T extends ChangeEvent> void addListener(ChangeListener<? super T> listener, Class<T> eventType) {
        wrapped.addListener(listener, eventType);
    }

    @Override
    public <T extends ChangeEvent> void removeListener(ChangeListener<? super T> listener, Class<T> eventType) {
        wrapped.removeListener(listener, eventType);
    }

    @Override
    public void refreshMetaModel() throws DataStoreException {
        wrapped.refreshMetaModel();
    }

}
