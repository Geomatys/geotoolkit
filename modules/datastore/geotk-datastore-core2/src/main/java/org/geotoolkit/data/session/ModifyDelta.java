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

import org.geotoolkit.data.DataStore;
import org.geotoolkit.data.DataStoreException;
import org.geotoolkit.data.DataUtilities;
import org.geotoolkit.data.DefaultFeatureCollection;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;

import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.type.Name;
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
    private final FeatureCollection<Feature> features;

    public ModifyDelta(Name typeName, Collection<Feature> features){
        if(typeName == null){
            throw new NullPointerException("Type name can not be null.");
        }
        if(features == null || features.isEmpty()){
            throw new IllegalArgumentException("Can not create an Add delta with no new features.");
        }

        this.type = typeName;
        this.features = new DefaultFeatureCollection<Feature>(null, null, Feature.class);
        this.features.addAll(features);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Query modify(Query query) {
        //add doesnt modify a query
        return query;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureIterator modify(Query query, FeatureIterator reader) throws DataStoreException {
        if(!query.getTypeName().equals(type)) return reader;

        //todo must handle properly sortOrder
        final FeatureIterator affected = features.subCollection(query).iterator();

        return DataUtilities.sequence(reader, affected);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public long modify(Query query, long count) throws DataStoreException{
        if(!query.getTypeName().equals(type)) return count;

        final int affected = features.subCollection(query).size();

        return count + affected;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Envelope modify(Query query, Envelope env) throws DataStoreException {
        if(!query.getTypeName().equals(type)) return env;

        final Envelope affected = features.subCollection(query).getEnvelope();
        final JTSEnvelope2D combine = new JTSEnvelope2D(env);
        combine.expandToInclude(new JTSEnvelope2D(affected));

        return combine;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void commit(DataStore store) throws DataStoreException {
        final FeatureWriter writer = store.getFeatureWriterAppend(type);

        try{
            for(final Feature f : features){
                final Feature candidate = writer.next();

                for(final Property property : f.getProperties()){
                    candidate.getProperty(property.getName()).setValue(property.getValue());
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
        features.clear();
    }

}
