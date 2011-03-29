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
 * Basic support for a  FeatureIterator that starts at a given index.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class GenericStartIndexFeatureIterator<F extends Feature, R extends FeatureIterator<F>>
        implements FeatureIterator<F> {

    protected final R iterator;
    protected final int startIndex;
    protected F nextFeature = null;
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
            // hasNext() ensures that next != null
            final F f = nextFeature;
            nextFeature = null;
            return f;
        } else {
            throw new NoSuchElementException("No more features.");
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
        if(nextFeature != null) return true;

        if(!translateDone){
            for(int i=0;i<startIndex;i++){
                if(iterator.hasNext()){
                    iterator.next();
                }else{
                    break;
                }
            }
            translateDone = true;
        }

        if(iterator.hasNext()){
            nextFeature = iterator.next();
        }

        return nextFeature != null;
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
        sb.append("[StartAt=").append(startIndex).append("]\n");
        String subIterator = "\u2514\u2500\u2500" + iterator.toString(); //move text to the right
        subIterator = subIterator.replaceAll("\n", "\n\u00A0\u00A0\u00A0"); //move text to the right
        sb.append(subIterator);
        return sb.toString();
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

        private GenericStartIndexFeatureReader(final R reader,final int limit){
            super(reader,limit);
        }

        @Override
        public T getFeatureType() {
            return iterator.getFeatureType();
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

        private GenericStartIndexFeatureWriter(final R writer,final int limit){
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

    private static final class GenericStartIndexFeatureCollection extends WrapFeatureCollection{

        private final int startIndex;

        private GenericStartIndexFeatureCollection(final FeatureCollection original, final int startIndex){
            super(original);
            this.startIndex = startIndex;
        }

        @Override
        public FeatureIterator iterator(final Hints hints) throws DataStoreRuntimeException {
            return wrap(getOriginalFeatureCollection().iterator(hints), startIndex);
        }

        @Override
        protected Feature modify(Feature original) throws DataStoreRuntimeException {
            throw new UnsupportedOperationException("should not have been called.");
        }

    }


    /**
     * Wrap a FeatureIterator with a start index.
     */
    public static <F extends Feature> FeatureIterator<F> wrap(final FeatureIterator<F> reader, final int limit){
        return new GenericStartIndexFeatureIterator(reader, limit);
    }

    /**
     * Wrap a FeatureReader with a start index.
     */
    public static <T extends FeatureType, F extends Feature> FeatureReader<T,F> wrap(final FeatureReader<T,F> reader, final int limit){
        return new GenericStartIndexFeatureReader(reader, limit);
    }

    /**
     * Wrap a FeatureWriter with a start index.
     */
    public static <T extends FeatureType, F extends Feature> FeatureWriter<T,F> wrap(final FeatureWriter<T,F> writer, final int limit){
        return new GenericStartIndexFeatureWriter(writer, limit);
    }

    /**
     * Create an differed start index FeatureCollection wrapping the given collection.
     */
    public static FeatureCollection wrap(final FeatureCollection original, final int startIndex){
        return new GenericStartIndexFeatureCollection(original, startIndex);
    }

}
