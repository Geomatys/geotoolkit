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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.geotoolkit.storage.feature.FeatureStore;
import org.geotoolkit.storage.feature.FeatureIterator;
import org.geotoolkit.storage.feature.query.Query;
import org.geotoolkit.storage.feature.query.QueryBuilder;
import org.apache.sis.storage.DataStoreException;
import static org.apache.sis.util.ArgumentChecks.*;
import static org.geotoolkit.storage.feature.session.AbstractDelta.list;
import org.opengis.filter.Filter;
import org.opengis.filter.ResourceId;
import org.opengis.geometry.Envelope;

/**
 * Delta which remove a collection of features.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 * @todo make this concurrent
 */
public class RemoveDelta extends AbstractDelta{

    protected Filter removedIds;

    public RemoveDelta(final Session session, final String typeName, final Filter filter){
        super(session,typeName);
        ensureNonNull("type name", typeName);
        ensureNonNull("filter", filter);
        this.removedIds = filter;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void update(Map<String, String> idUpdates) {
        if(idUpdates == null || idUpdates.isEmpty())return;
        final List<Filter<Object>> ids = list(removedIds);
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
            case 0: removedIds = Filter.exclude(); break;
            case 1: removedIds = newIds.iterator().next(); break;
            case 2: removedIds = FF.or(newIds);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Query modify(final Query query) {
        if(!query.getTypeName().equals(type)) return query;

        final QueryBuilder builder = new QueryBuilder(query);
        builder.setFilter(FF.and(builder.getFilter(),FF.not(removedIds)));

        return builder.buildQuery();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureIterator modify(final Query query, final FeatureIterator reader) throws DataStoreException {
        //nothing to do, the send query has already been modified
        return reader;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public long modify(final Query query, final long count) throws DataStoreException{
        //nothing to do, the send query has already been modified
        return count;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Envelope modify(final Query query, final Envelope env) throws DataStoreException {
        //nothing to do, the send query has already been modified
        return env;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Map<String,String> commit(final FeatureStore store) throws DataStoreException {
        store.removeFeatures(type, removedIds);
        return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void dispose() {
    }

}
