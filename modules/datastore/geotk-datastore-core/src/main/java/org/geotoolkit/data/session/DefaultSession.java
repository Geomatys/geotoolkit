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
import java.util.Map;
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
public class DefaultSession extends AbstractSession {

    protected static final FilterFactory2 FF = (FilterFactory2)
            FactoryFinder.getFilterFactory(new Hints(Hints.FILTER_FACTORY, FilterFactory2.class));

    private final DataStore store;
    private final DefaultSessionDiff diff;
    private final boolean async;

    public DefaultSession(DataStore store, boolean async){
        if(store == null){
            throw new NullPointerException("DataStore can not be null.");
        }

        this.store = store;
        this.diff = new DefaultSessionDiff();
        this.async = async;
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
    public boolean isAsynchrone() {
        return async;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureCollection getFeatureCollection(Query query) {
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
    public void addFeatures(Name groupName, Collection newFeatures) throws DataStoreException {
        //will raise an error if the name doesnt exist
        store.getFeatureType(groupName);

        if(async){
            diff.add(new AddDelta(this, groupName, newFeatures));
        }else{
            store.addFeatures(groupName, newFeatures);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void updateFeatures(Name groupName, Filter filter, Map<? extends AttributeDescriptor,? extends Object> values) throws DataStoreException {
        //will raise an error if the name doesnt exist
        store.getFeatureType(groupName);

        if(values == null || values.isEmpty()){
            //no modifications, no need to create a modify delta
            //todo should we raise an error ?
            return;
        }

        if(async){
            final Id modified;

            if(filter instanceof Id){
                modified = (Id)filter;
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
                    //no feature match this filter, no need to create a modify delta
                    return;
                }else{
                    modified = FF.id(identifiers);
                }
            }

            diff.add(new ModifyDelta(this, groupName, modified, values));
        }else{
            store.updateFeatures(groupName, filter, values);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void removeFeatures(Name groupName, Filter filter) throws DataStoreException {
        //will raise an error if the name doesnt exist
        store.getFeatureType(groupName);

        if(async){
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

            diff.add(new RemoveDelta(this, groupName, removed));
        }else{
            store.removeFeatures(groupName, filter);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean hasPendingChanges() {
        return diff.getDeltas().size() != 0;
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
