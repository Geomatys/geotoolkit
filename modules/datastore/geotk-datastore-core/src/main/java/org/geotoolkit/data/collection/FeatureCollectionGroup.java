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

package org.geotoolkit.data.collection;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;
import org.opengis.feature.simple.SimpleFeatureType;

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

    private FeatureCollectionGroup(FeatureCollection[] wrapped){
        super( (SimpleFeatureType)wrapped[0].getSchema(), "dummyId");
        this.wrapped = wrapped;
    }

    public static FeatureCollection sequence(FeatureCollection ... cols){
        return new FeatureCollectionGroup(cols);
    }

    @Override
    protected Iterator openIterator() {
        return new SequenceIterator();
    }

    @Override
    protected void closeIterator(Iterator close) {
        if(close instanceof SequenceIterator){
            ((SequenceIterator)close).close();
        }
    }

    @Override
    public int size() {
        int size = 0;
        for(FeatureCollection c : wrapped){
            size += c.size();
        }
        return size;
    }

    @Override
    public JTSEnvelope2D getBounds() {
        final JTSEnvelope2D env = new JTSEnvelope2D();

        for(FeatureCollection c : wrapped){
            env.expandToInclude(c.getBounds());
        }

        return env;
    }

    private class SequenceIterator implements Iterator{

        private int currentCollection = -1;
        private Iterator ite = null;

        public SequenceIterator(){
            currentCollection = 0;
            ite = wrapped[currentCollection].iterator();
        }


        public void close(){
            if(ite != null){
                wrapped[currentCollection].close(ite);
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
                wrapped[currentCollection].close(ite);
            }

            currentCollection++;
            while(currentCollection < wrapped.length){
                ite = wrapped[currentCollection].iterator();

                if(ite.hasNext()){
                    return true;
                }else{
                    wrapped[currentCollection].close(ite);
                }

                currentCollection++;
            }

            return false;
        }

        @Override
        public Object next() {
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
