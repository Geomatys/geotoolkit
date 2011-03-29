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
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.util.converter.Classes;

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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(Classes.getShortClassName(this));
        sb.append("[Max=").append(maxFeatures).append("]\n");
        String subIterator = "\u2514\u2500\u2500" + iterator.toString(); //move text to the right
        subIterator = subIterator.replaceAll("\n", "\n\u00A0\u00A0\u00A0"); //move text to the right
        sb.append(subIterator);
        return sb.toString();
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

        private GenericMaxFeatureReader(final R reader,final int limit){
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

        private GenericMaxFeatureWriter(final R writer,final int limit){
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

    private static final class GenericMaxFeatureCollection extends WrapFeatureCollection{

        private final int max;

        private GenericMaxFeatureCollection(final FeatureCollection original, final int max){
            super(original);
            this.max = max;
        }

        @Override
        public FeatureIterator iterator(final Hints hints) throws DataStoreRuntimeException {
            return wrap(getOriginalFeatureCollection().iterator(hints), max);
        }

        @Override
        protected Feature modify(Feature original) throws DataStoreRuntimeException {
            throw new UnsupportedOperationException("should not have been called.");
        }

    }

    /**
     * Wrap a FeatureReader with a max limit.
     */
    public static <F extends Feature> FeatureIterator<F> wrap(final FeatureIterator<F> reader, final int limit){
        if(reader instanceof FeatureReader){
            return wrap((FeatureReader)reader,limit);
        }else if(reader instanceof FeatureWriter){
            return wrap((FeatureWriter)reader,limit);
        }else{
            return new GenericMaxFeatureIterator(reader, limit);
        }
    }

    /**
     * Wrap a FeatureReader with a max limit.
     */
    public static <T extends FeatureType, F extends Feature> FeatureReader<T,F> wrap(final FeatureReader<T,F> reader, final int limit){
        return new GenericMaxFeatureReader(reader, limit);
    }

    /**
     * Wrap a FeatureWriter with a max limit.
     */
    public static <T extends FeatureType, F extends Feature> FeatureWriter<T,F> wrap(final FeatureWriter<T,F> writer, final int limit){
        return new GenericMaxFeatureWriter(writer, limit);
    }

    /**
     * Create an limited FeatureCollection wrapping the given collection.
     */
    public static FeatureCollection wrap(final FeatureCollection original, final int max){
        return new GenericMaxFeatureCollection(original, max);
    }

}
