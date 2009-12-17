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

import java.util.NoSuchElementException;

import org.geotoolkit.geometry.jts.JTSEnvelope2D;

import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Provide a collection that link several collections in one.
 * All collection are appended in the order they are given like a sequence.
 * This implementation doesn't copy the features, it will call each wraped
 * collection one after the other.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class FeatureCollectionGroup extends AbstractFeatureCollection{

    private final FeatureCollection[] wrapped;

    private FeatureCollectionGroup(String id, FeatureCollection[] wrapped){
        super(id, (SimpleFeatureType)wrapped[0].getSchema());
        this.wrapped = wrapped;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int size() {
        int size = 0;
        for(FeatureCollection c : wrapped){
            size += c.size();
        }
        return size;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureIterator iterator() throws DataStoreRuntimeException {
        return new SequenceIterator();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Envelope getEnvelope() throws DataStoreException {
        CoordinateReferenceSystem crs = null;
        if (wrapped.length > 0) {
            crs = wrapped[0].getEnvelope().getCoordinateReferenceSystem();
        }
        final JTSEnvelope2D env = new JTSEnvelope2D(crs);

        for(FeatureCollection c : wrapped){
            env.expandToInclude(new JTSEnvelope2D(c.getEnvelope()));
        }

        return env;
    }

    public static FeatureCollection sequence(FeatureCollection ... cols){
        return new FeatureCollectionGroup("collection-1", cols);
    }

    public static FeatureCollection sequence(String id, FeatureCollection ... cols){
        return new FeatureCollectionGroup(id, cols);
    }

    private class SequenceIterator implements FeatureIterator{

        private int currentCollection = -1;
        private FeatureIterator ite = null;

        public SequenceIterator(){
            currentCollection = 0;
            ite = wrapped[currentCollection].iterator();
        }


        @Override
        public void close(){
            if(ite != null){
                ite.close();
            }
        }

        @Override
        public boolean hasNext() {

            if(ite == null){
                return false;
            }

            if(ite.hasNext()){
                return true;
            }else{
                ite.close();
            }

            currentCollection++;
            while(currentCollection < wrapped.length){
                ite = wrapped[currentCollection].iterator();

                if(ite.hasNext()){
                    return true;
                }else{
                    ite.close();
                }

                currentCollection++;
            }

            return false;
        }

        @Override
        public Feature next() {
            if(ite == null){
                throw new NoSuchElementException("No more elements");
            }else{
                return ite.next();
            }
        }

        @Override
        public void remove() {
            if(ite == null){
                throw new NoSuchElementException("No more elements");
            }else{
                ite.remove();
            }
        }

    }

}
