/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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
import java.util.HashSet;
import java.util.Set;

import org.geotoolkit.data.DataStore;
import org.geotoolkit.data.DataStoreException;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;

import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.Id;
import org.opengis.filter.identity.Identifier;
import org.opengis.geometry.Envelope;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultSession implements Session {

    protected static final FilterFactory2 FF = (FilterFactory2)
            FactoryFinder.getFilterFactory(new Hints(Hints.FILTER_FACTORY, FilterFactory2.class));

    private final DataStore store;
    private final DefaultSessionDiff diff;

    public DefaultSession(DataStore store){
        if(store == null){
            throw new NullPointerException("DataStore can not be null.");
        }

        this.store = store;
        this.diff = new DefaultSessionDiff();
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
     */
    @Override
    public FeatureCollection features(Query query) {
        return new SessionFeatureCollection(this, "id", query);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureIterator getFeatureIterator(Query query) throws DataStoreException {
        for(final Delta alt : diff.getDeltas()){
            query = alt.modify(query);
        }
        FeatureIterator reader = store.getFeatureReader(query);
        for(final Delta alt : diff.getDeltas()){
            reader = alt.modify(query,reader);
        }
        return reader;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void add(Name groupName, Collection newFeatures) throws DataStoreException {
        //will raise an error if the name doesnt exist
        store.getSchema(groupName);
        diff.add(new AddDelta(groupName, newFeatures));
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void update(Name groupName, AttributeDescriptor[] type, Object[] value, Filter filter) throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet.");
        //todo must add a new alteration
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void remove(Name groupName, Filter filter) throws DataStoreException {
        //will raise an error if the name doesnt exist
        store.getSchema(groupName);

        final Id removed;

        if(filter instanceof Id){
            removed = (Id)filter;
        }else{
            final Set<Identifier> identifiers = new HashSet<Identifier>();
            QueryBuilder qb = new QueryBuilder(groupName);
            qb.setFilter(filter);
            final FeatureIterator ite = getFeatureIterator(qb.buildQuery());
            try{
                while(ite.hasNext()){
                    identifiers.add(ite.next().getIdentifier());
                }
            }finally{
                ite.close();
            }

            if(identifiers.isEmpty()){
                //no feature match this filter, no need to create to remove delta
                return;
            }else{
                removed = FF.id(identifiers);
            }
        }

        diff.add(new RemoveDelta(groupName, removed));
        
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean hasPendingChanges() {
        return diff.getDeltas().length != 0;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void commit() throws DataStoreException {
        diff.commit(store);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void rollback() {
        diff.rollback();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public long getCount(Query query) throws DataStoreException {
        for(final Delta alt : diff.getDeltas()){
            query = alt.modify(query);
        }
        long count = store.getCount(query);
        for(final Delta alt : diff.getDeltas()){
            count = alt.modify(query,count);
        }
        return count;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Envelope getEnvelope(Query query) throws DataStoreException {
        for(final Delta alt : diff.getDeltas()){
            query = alt.modify(query);
        }
        Envelope env = store.getEnvelope(query);
        for(final Delta alt : diff.getDeltas()){
            env = alt.modify(query,env);
        }
        return env;
    }

}
