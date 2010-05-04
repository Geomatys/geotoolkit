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

import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotoolkit.data.DataStore;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.data.DataUtilities;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.feature.simple.SimpleFeatureBuilder;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;
import org.geotoolkit.util.logging.Logging;

import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;
import org.opengis.geometry.Envelope;

/**
 * Delta which add a collection of features.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 * @todo make this concurrent
 */
class AddDelta extends AbstractDelta{

    private final Name type;
    private final FeatureCollection<Feature> features;

    /**
     *
     * @param session
     * @param typeName
     * @param features : can be empty, even so it would be useless,
     * We do not check the size since this collection may be relying on
     * a datastore which may be slow or changing with time.
     * this features from the given collection will be copied.
     */
    AddDelta(Session session, Name typeName, Collection<Feature> features){
        super(session);
        if(typeName == null){
            throw new NullPointerException("Type name can not be null.");
        }
        if(features == null){
            throw new IllegalArgumentException("Can not create an Add delta with no collection.");
        }

        this.type = typeName;

        FeatureType ft;
        try {
            ft = session.getDataStore().getFeatureType(typeName);
        } catch (DataStoreException ex) {
            Logger.getLogger(AddDelta.class.getName()).log(Level.WARNING, null, ex);
            ft = null;
        }

        this.features = DataUtilities.collection("temp", ft);

        //we must copy the features since they might be changed later
        final Iterator<? extends Feature> ite = features.iterator();
        try{
            while(ite.hasNext()){
                SimpleFeature sf = (SimpleFeature) ite.next();
                sf = SimpleFeatureBuilder.deep(sf);
                this.features.add(sf);
            }
        }finally{
            if(ite instanceof Closeable){
                try {
                    ((Closeable) ite).close();
                } catch (IOException ex) {
                    Logging.getLogger(AddDelta.class).log(Level.WARNING, "Error while closing iterator : ", ex);
                }
            }
        }
        
        
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

        final FeatureIterator affected = features.subCollection(query).iterator();

        if(query.getSortBy() != null){
            return DataUtilities.combine(query.getSortBy(), reader, affected);
        }else{
            return DataUtilities.sequence(reader, affected);
        }
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
        store.addFeatures(type, features);
        features.clear();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void dispose() {
        features.clear();
    }

}
