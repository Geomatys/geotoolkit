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

import org.geotoolkit.data.DataStore;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryBuilder;

import org.opengis.feature.type.Name;
import org.opengis.filter.Id;
import org.opengis.geometry.Envelope;

/**
 * Delta which remove a collection of features.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 * @todo make this concurrent
 */
class RemoveDelta extends AbstractDelta{

    private final Name type;
    private final Id removedIds;

    RemoveDelta(final Session session, final Name typeName, final Id filter){
        super(session);
        if(typeName == null){
            throw new NullPointerException("Type name can not be null.");
        }
        if(filter == null ){
            throw new IllegalArgumentException("Can not create a remove delta with no filter.");
        }

        this.type = typeName;
        this.removedIds = filter;
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
    public void commit(final DataStore store) throws DataStoreException {
        store.removeFeatures(type, removedIds);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void dispose() {
    }

}
