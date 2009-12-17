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

import java.util.AbstractCollection;

import org.geotoolkit.data.query.Query;
import org.geotoolkit.feature.SchemaException;
import org.geotoolkit.geometry.DefaultBoundingBox;

import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.geometry.BoundingBox;
import org.opengis.geometry.Envelope;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class AbstractFeatureCollection<F extends Feature> extends AbstractCollection<F> implements FeatureCollection<F>{

    protected final String id;
    protected final FeatureType type;

    public AbstractFeatureCollection(String id, FeatureType type){
        this.id = id;
        this.type = type;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getID() {
        return id;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureType getSchema() {
        return type;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public abstract FeatureIterator<F> iterator();

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureCollection<F> subCollection(Query query) throws DataStoreException{
        try {
            return new DefaultSubFeatureCollection<F>(this, query);
        } catch (SchemaException ex) {
            throw new DataStoreException(ex);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Envelope getEnvelope() throws DataStoreException{
        BoundingBox env = null;

        final FeatureIterator<F> ite = iterator();
        try{
            while(ite.hasNext()){
                final F f = ite.next();
                final BoundingBox bbox = f.getBounds();
                if(!bbox.isEmpty()){
                    if(env != null){
                        env.include(bbox);
                    }else{
                        env = new DefaultBoundingBox(bbox, bbox.getCoordinateReferenceSystem());
                    }
                }
            }
        }finally{
            ite.close();
        }

        return env;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int size() {
        long count = 0;

        final FeatureIterator<F> reader = iterator();
        try{
            while(reader.hasNext()){
                reader.next();
                count++;
            }
        }finally{
            reader.close();
        }

        return (int) count;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void addListener() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void removeListener() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
