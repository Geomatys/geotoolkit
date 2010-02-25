/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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

package org.geotoolkit.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.geotoolkit.data.memory.GenericWrapFeatureIterator;
import org.geotoolkit.data.memory.MemoryDataStore;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.util.NumberRange;
import org.geotoolkit.util.collection.NotifiedCheckedList;

import org.opengis.feature.Feature;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.Filter;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultFeatureCollection<F extends Feature> extends AbstractFeatureCollection<F>{

    private DataStore store = null;
    private Session session = null;
    private final List<F> features;

    public DefaultFeatureCollection(String id, FeatureType type, Class<F> featureClass){
        super(id,type);

        //type safe and synchronized list
        features = new NotifiedCheckedList<F>(featureClass) {

            @Override
            protected void notifyAdd(F item, int index) {
            }

            @Override
            protected void notifyAdd(Collection<? extends F> items, NumberRange<Integer> range) {
            }

            @Override
            protected void notifyRemove(F item, int index) {
            }

            @Override
            protected void notifyRemove(Collection<? extends F> items, NumberRange<Integer> range) {
            }
        };

    }

    @Override
    public synchronized Session getSession() {
        if(store == null){
            //create a dummy datastore that will allow us to have several session
            //for the collection and so use them in complexe queries.
            store = new MemoryDataStore(this, true);
            session = store.createSession(false);
        }
        return session;
    }

    @Override
    public FeatureIterator<F> iterator(Hints hints) {
        return GenericWrapFeatureIterator.wrapToIterator(features.iterator());
    }

    @Override
    public boolean add(F e) {
        return features.add(e);
    }

    @Override
    public boolean addAll(Collection clctn) {
        return features.addAll(clctn);
    }

    @Override
    public boolean remove(Object o) {
        return features.remove(o);
    }

    @Override
    public boolean removeAll(Collection<?> clctn) {
        return features.removeAll(clctn);
    }

    @Override
    public boolean isWritable() {
        return true;
    }

    @Override
    public void update(Filter filter, Map<? extends AttributeDescriptor, ? extends Object> values) throws DataStoreException {
        for(Feature f : features){
            if(filter.evaluate(f)){
                for(Entry<? extends AttributeDescriptor, ? extends Object> e : values.entrySet()){
                    f.getProperty(e.getKey().getLocalName()).setValue(e.getValue());
                }
            }
        }
    }

    @Override
    public void remove(Filter filter) throws DataStoreException {
        final List toRemove = new ArrayList();
        for(Feature f : features){
            if(filter.evaluate(f)){
                toRemove.add(f);
            }
        }
        removeAll(toRemove);
    }

}
