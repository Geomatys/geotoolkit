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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotoolkit.data.DataStore;
import org.geotoolkit.data.DataStoreException;
import org.geotoolkit.data.DefaultFeatureCollection;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.query.Query;

import org.opengis.feature.Feature;
import org.opengis.feature.type.Name;
import org.opengis.geometry.Envelope;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class AddDelta implements Delta{

    private final Name type;
    private final FeatureCollection<Feature> features;

    public AddDelta(Name typeName, Collection<Feature> features){
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
    public FeatureReader modify(Query query, FeatureReader reader) {
        if(!query.getTypeName().equals(type)) return reader;

        //todo;
        return reader;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public long modify(Query query, long count) {
        if(!query.getTypeName().equals(type)) return count;

        try {
            int affected = features.subCollection(query).size();
            count += affected;
        } catch (DataStoreException ex) {
            Logger.getLogger(AddDelta.class.getName()).log(Level.SEVERE, null, ex);
        }

        return count;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Envelope modify(Query query, Envelope env) {
        if(!query.getTypeName().equals(type)) return env;

        //todo

        return env;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void commit(DataStore store) {
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void dispose() {
    }

}
