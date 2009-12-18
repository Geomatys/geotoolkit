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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.geotoolkit.data.DataStore;
import org.geotoolkit.data.DataStoreException;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryBuilder;

import org.opengis.feature.Feature;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.Name;
import org.opengis.filter.Id;
import org.opengis.geometry.Envelope;

/**
 * Delta which modify a collection of features.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 * @todo make this concurrent
 */
public class ModifyDelta extends AbstractDelta{

    private final Name type;
    private final Id filter;
    private final Map<AttributeDescriptor,Object> values = new HashMap<AttributeDescriptor, Object>();

    public ModifyDelta(Session session, Name typeName, Id filter, Map<AttributeDescriptor,Object> values){
        super(session);
        if(typeName == null){
            throw new NullPointerException("Type name can not be null.");
        }
        if(filter == null){
            throw new NullPointerException("Filter can not be null. Did you mean Filter.INCLUDE ?");
        }
        if(values == null || values.isEmpty()){
            throw new IllegalArgumentException("Modified values can not be null or empty. A modify delta is useless in this case.");
        }

        this.type = typeName;
        this.filter = filter;
        this.values.putAll(values);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Query modify(Query query) {
        if(!query.getTypeName().equals(type)) return query;

        //we exclude the modified features
        //they will be handle in the other modified methods
        final QueryBuilder builder = new QueryBuilder(query);
        builder.setFilter(FF.and(builder.getFilter(),FF.not(filter)));

        return builder.buildQuery();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureIterator modify(Query query, FeatureIterator reader) throws DataStoreException {

        //todo must encapsulate

        return reader;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public long modify(Query query, long count) throws DataStoreException{

        //doesnt work

//        //we request only the features modified
//        final QueryBuilder builder = new QueryBuilder(query);
//        builder.setFilter(FF.and(builder.getFilter(),filter));
//
//        session.getFeatureIterator(builder.buildQuery());

        //todo
        return count;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Envelope modify(Query query, Envelope env) throws DataStoreException {
        //todo
        return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void commit(DataStore store) throws DataStoreException {
        final FeatureWriter writer = store.getFeatureWriter(type,filter);

        try{
            while(writer.hasNext()){
                final Feature f = writer.next();
                for(final Entry<AttributeDescriptor,Object> entry : values.entrySet()){
                    f.getProperty(entry.getKey().getName()).setValue(entry.getValue());
                }
                writer.write();
            }
        }finally{
            writer.close();
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void dispose() {
    }

}
