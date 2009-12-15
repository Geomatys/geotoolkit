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

import java.io.IOException;
import java.util.Collection;

import org.geotoolkit.data.DataStore;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.query.Query;

import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;
import org.opengis.geometry.Envelope;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class DefaultSession implements Session {

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
    public FeatureReader getFeatureIterator(Query query) throws IOException {
        FeatureReader reader = store.getFeatureReader(query);
        for(final Delta alt : getDiff().alterations()){
            reader = alt.modify(reader);
        }
        return reader;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void add(Name groupName, Collection newFeatures) {
        throw new UnsupportedOperationException("Not supported yet.");
        //todo must add a new alteration
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void update(Name groupName, AttributeDescriptor[] type, Object[] value, Filter filter) {
        throw new UnsupportedOperationException("Not supported yet.");
        //todo must add a new alteration
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void remove(Name groupName, Filter filter) {
        throw new UnsupportedOperationException("Not supported yet.");
        //todo must add a new alteration
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void commit() throws IOException {
        //todo : must lock on the diff to avoid sync issues
        for(final Delta alt : getDiff().alterations()){
            alt.modify(store);
            alt.dispose();
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void rollback() {
        diff.reset();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public long getCount(Query query) throws IOException {
        long count = store.getCount(query);
        for(final Delta alt : getDiff().alterations()){
            count = alt.modify(count);
        }
        return count;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Envelope getEnvelope(Query query) throws IOException {
        Envelope env = store.getEnvelope(query);
        for(final Delta alt : getDiff().alterations()){
            env = alt.modify(env);
        }
        return env;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public SessionDiff getDiff() {
        return diff;
    }

}
