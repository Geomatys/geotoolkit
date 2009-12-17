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
 * Basic support for a  FeatureIterator that limits itself to a given number of features.
 *
 * @author Chris Holmes
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class GenericMaxFeatureIterator<F extends Feature, R extends FeatureIterator<F>>
        implements FeatureIterator<F> {

    protected final R iterator;
    protected final int maxFeatures;
    private int counter = 0;

    /**
     * Creates a new instance of GenericMaxFeatureIterator
     *
     * @param iterator FeatureReader to limit
     * @param maxFeatures maximum number of feature
     */
    private GenericMaxFeatureIterator(final R iterator, final int maxFeatures) {
        this.iterator = iterator;
        this.maxFeatures = maxFeatures;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public F next() throws DataStoreRuntimeException {
        if (hasNext()) {
            counter++;
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
     * @return <code>true</code> if the featureReader has not passed the max
     *         and still has more features.
     * @throws IOException If the reader we are filtering encounters a problem
     */
    @Override
    public boolean hasNext() throws DataStoreRuntimeException {
        return ((counter < maxFeatures) && iterator.hasNext());
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void remove() {
        iterator.remove();
    }

    /**
     * Wrap a FeatureReader with a max limit.
     * 
     * @param <T> extends FeatureType
     * @param <F> extends Feature
     * @param <R> extends FeatureReader<T,F>
     */
    private static final class GenericMaxFeatureReader<T extends FeatureType, F extends Feature, R extends FeatureReader<T,F>>
            extends GenericMaxFeatureIterator<F,R> implements FeatureReader<T,F>{

        private GenericMaxFeatureReader(R reader,int limit){
            super(reader,limit);
        }
        
        @Override
        public T getFeatureType() {
            return iterator.getFeatureType();
        }

    }

    /**
     * Wrap a FeatureWriter with a max limit.
     *
     * @param <T> extends FeatureType
     * @param <F> extends Feature
     * @param <R> extends FeatureWriter<T,F>
     */
    private static final class GenericMaxFeatureWriter<T extends FeatureType, F extends Feature, R extends FeatureWriter<T,F>>
            extends GenericMaxFeatureIterator<F,R> implements FeatureWriter<T,F>{

        private GenericMaxFeatureWriter(R writer,int limit){
            super(writer,limit);
        }

        @Override
        public T getFeatureType() {
            return iterator.getFeatureType();
        }

        @Override
        public void write() throws DataStoreRuntimeException {
            iterator.write();
        }
    }

    /**
     * Wrap a FeatureReader with a max limit.
     */
    public static <F extends Feature> FeatureIterator<F> wrap(FeatureIterator<F> reader, int limit){
        return new GenericMaxFeatureIterator(reader, limit);
    }

    /**
     * Wrap a FeatureReader with a max limit.
     */
    public static <T extends FeatureType, F extends Feature> FeatureReader<T,F> wrap(FeatureReader<T,F> reader, int limit){
        return new GenericMaxFeatureReader(reader, limit);
    }

    /**
     * Wrap a FeatureWriter with a max limit.
     */
    public static <T extends FeatureType, F extends Feature> FeatureWriter<T,F> wrap(FeatureWriter<T,F> writer, int limit){
        return new GenericMaxFeatureWriter(writer, limit);
    }

}
