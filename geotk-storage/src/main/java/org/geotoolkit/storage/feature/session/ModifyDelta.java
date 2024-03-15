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
package org.geotoolkit.storage.feature.session;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.geotoolkit.storage.feature.FeatureStore;
import org.geotoolkit.storage.feature.FeatureIterator;
import org.geotoolkit.storage.feature.GenericModifyFeatureIterator;
import org.geotoolkit.storage.memory.WrapFeatureIterator;
import org.geotoolkit.storage.feature.query.Query;
import org.apache.sis.storage.DataStoreException;
import org.opengis.feature.Feature;
import org.opengis.geometry.Envelope;
import static org.apache.sis.util.ArgumentChecks.*;
import org.opengis.filter.Filter;
import org.opengis.filter.ResourceId;


/**
 * Delta which modify a collection of features.
 *
 * @author Johann Sorel (Geomatys)
 * @todo make this concurrent
 */
public class ModifyDelta extends AbstractDelta{

    protected final Map<String,Object> values = new HashMap<>();
    protected Filter<Object> filter;

    public ModifyDelta(final Session session, final String typeName, final Filter filter, final Map<String,?> values){
        super(session,typeName);
        ensureNonNull("type name", typeName);
        if(filter == null){
            throw new NullPointerException("Filter can not be null. Did you mean Filter.include()?");
        }
        if(values == null || values.isEmpty()){
            throw new IllegalArgumentException("Modified values can not be null or empty. A modify delta is useless in this case.");
        }
        this.filter = filter;
        this.values.putAll(values);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void update(Map<String, String> idUpdates) {
        if(idUpdates == null || idUpdates.isEmpty())return;
        final List<Filter<Object>> ids = list(filter);
        final Set<Filter<Object>> newIds = new HashSet<>();
        for (Filter<Object> id : ids) {
            String newId = idUpdates.get(((ResourceId) id).getIdentifier());
            if (newId != null) {
                //id has change
                id = FF.resourceId(newId);
            }
            newIds.add(id);
        }
        switch (newIds.size()) {
            case 0: filter = Filter.exclude(); break;
            case 1: filter = newIds.iterator().next(); break;
            case 2: filter = FF.or(newIds);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Query modify(final Query query) {
        if(!query.getTypeName().equals(type)) return query;

        //we always include the modified features
        //they will be filtered at return time in the other modified methods
        //todo we should modify this query for count and envelope
        final Query builder = new Query();
        builder.copy(query);
        builder.setSelection(FF.or((Filter) builder.getSelection(),filter));

        return builder;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureIterator modify(final Query query, final FeatureIterator reader) throws DataStoreException {

        final FeatureIterator wrap = new WrapFeatureIterator(reader) {

            @Override
            protected Feature modify(Feature feature) {

                if(!filter.test(feature)){
                    return feature;
                }

                //modify the feature
                feature = GenericModifyFeatureIterator.apply(feature, values);
                return feature;
            }
        };
        return wrap;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public long modify(final Query query, final long count) throws DataStoreException{
        //todo must find a correct wayto alterate the count
        //the send request should be modified
        return count;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Envelope modify(final Query query, final Envelope env) throws DataStoreException {
        //todo must find a correct wayto alterate the envelope
        //the send request should be modified
        return env;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Map<String,String> commit(final FeatureStore store) throws DataStoreException {
        store.updateFeatures(type, filter, values);
        return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void dispose() {
    }
}
