/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003-2008, Open Source Geospatial Foundation (OSGeo)
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

package org.geotoolkit.data.memory;

import java.util.NoSuchElementException;

import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.DataStoreRuntimeException;

import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

/**
 * Basic support for a  FeatureIterator that starts at a given index.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class GenericStartIndexFeatureIterator<F extends Feature, R extends FeatureIterator<F>>
        implements FeatureIterator<F> {

    protected final R iterator;
    protected final int startIndex;
    private boolean translateDone = false;

    /**
     * Creates a new instance of GenericStartIndexFeatureIterator
     *
     * @param iterator FeatureReader to start at
     * @param startIndex starting index
     */
    private GenericStartIndexFeatureIterator(final R iterator, final int startIndex) {
        this.iterator = iterator;
        this.startIndex = startIndex;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public F next() throws DataStoreRuntimeException {
        if (hasNext()) {
            return iterator.next();
        } else {
            throw new NoSuchElementException("No such Feature exists");
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void close() throws DataStoreRuntimeException {
        iterator.close();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public synchronized boolean hasNext() throws DataStoreRuntimeException {
        if(!translateDone){
            for(int i=1;i<startIndex;i++){
                if(iterator.hasNext()){
                    iterator.next();
                }else{
                    break;
                }
            }
            translateDone = true;
        }

        return iterator.hasNext();
    }

    /**
     * Wrap a FeatureReader with a start index.
     *
     * @param <T> extends FeatureType
     * @param <F> extends Feature
     * @param <R> extends FeatureReader<T,F>
     */
    private static final class GenericStartIndexFeatureReader<T extends FeatureType, F extends Feature, R extends FeatureReader<T,F>>
            extends GenericStartIndexFeatureIterator<F,R> implements FeatureReader<T,F>{

        private GenericStartIndexFeatureReader(R reader,int limit){
            super(reader,limit);
        }

        @Override
        public T getFeatureType() {
            return iterator.getFeatureType();
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }

    /**
     * Wrap a FeatureWriter with a start index.
     *
     * @param <T> extends FeatureType
     * @param <F> extends Feature
     * @param <R> extends FeatureWriter<T,F>
     */
    private static final class GenericStartIndexFeatureWriter<T extends FeatureType, F extends Feature, R extends FeatureWriter<T,F>>
            extends GenericStartIndexFeatureIterator<F,R> implements FeatureWriter<T,F>{

        private GenericStartIndexFeatureWriter(R writer,int limit){
            super(writer,limit);
        }

        @Override
        public T getFeatureType() {
            return iterator.getFeatureType();
        }

        @Override
        public void remove() {
            iterator.remove();
        }

        @Override
        public void write() throws DataStoreRuntimeException {
            iterator.write();
        }
    }

    /**
     * Wrap a FeatureReader with a start index.
     */
    public static <T extends FeatureType, F extends Feature> FeatureReader<T,F> wrap(FeatureReader<T,F> reader, int limit){
        return new GenericStartIndexFeatureReader(reader, limit);
    }

    /**
     * Wrap a FeatureWriter with a start index.
     */
    public static <T extends FeatureType, F extends Feature> FeatureWriter<T,F> wrap(FeatureWriter<T,F> writer, int limit){
        return new GenericStartIndexFeatureWriter(writer, limit);
    }

}
