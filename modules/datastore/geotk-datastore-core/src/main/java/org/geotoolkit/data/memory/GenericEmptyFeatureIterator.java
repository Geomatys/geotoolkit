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

package org.geotoolkit.data.memory;

import java.util.NoSuchElementException;
import org.geotoolkit.data.AbstractFeatureCollection;

import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.DataStoreRuntimeException;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.factory.Hints;

import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

/**
 * Basic support for an empty FeatureIterator.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class GenericEmptyFeatureIterator<F extends Feature> implements FeatureIterator<F> {

    /**
     * Creates a new instance of GenericEmptyFeatureIterator
     */
    private GenericEmptyFeatureIterator() {
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public F next() throws DataStoreRuntimeException {
        throw new NoSuchElementException("No such Feature exists");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void close() throws DataStoreRuntimeException {
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean hasNext() throws DataStoreRuntimeException {
        return false;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Can not remove empty feature iterator.");
    }

    /**
     * An empty FeatureReader of the given type.
     * 
     * @param <T> extends FeatureType
     * @param <F> extends Feature
     * @param <R> extends FeatureReader<T,F>
     */
    private static final class GenericEmptyFeatureReader<T extends FeatureType, F extends Feature>
            extends GenericEmptyFeatureIterator<F> implements FeatureReader<T,F>{

        private final T type;

        private GenericEmptyFeatureReader(final T type){
            this.type = type;
        }
        
        @Override
        public T getFeatureType() {
            return type;
        }

    }

    /**
     * An empty FeatureWriter of the given type.
     *
     * @param <T> extends FeatureType
     * @param <F> extends Feature
     * @param <R> extends FeatureWriter<T,F>
     */
    private static final class GenericEmptyFeatureWriter<T extends FeatureType, F extends Feature>
            extends GenericEmptyFeatureIterator<F> implements FeatureWriter<T,F>{

        private final T type;

        private GenericEmptyFeatureWriter(final T type){
            this.type = type;
        }

        @Override
        public T getFeatureType() {
            return type;
        }

        @Override
        public void write() throws DataStoreRuntimeException {
            throw new DataStoreRuntimeException("FeatureWriter is empty and does not support write()");
        }
    }

    private static final class GenericEmptyFeatureCollection extends WrapFeatureCollection{

        private GenericEmptyFeatureCollection(final FeatureCollection original){
            super(original);
        }

        @Override
        public FeatureIterator iterator(final Hints hints) throws DataStoreRuntimeException {
            return createReader(getOriginalFeatureCollection().getFeatureType());
        }

        @Override
        protected Feature modify(Feature original) throws DataStoreRuntimeException {
            throw new UnsupportedOperationException("should not have been called.");
        }

    }

    /**
     * Create an empty FeatureReader of the given type.
     * @param type FeatureType can be null
     */
    public static <T extends FeatureType, F extends Feature> FeatureReader<T,F> createReader(final T type){
        return new GenericEmptyFeatureReader<T,F>(type);
    }

    /**
     * Create an empty FeatureWriter of the given type.
     * @param type FeatureType can be null
     */
    public static <T extends FeatureType, F extends Feature> FeatureWriter<T,F> createWriter(final T type){
        return new GenericEmptyFeatureWriter<T,F>(type);
    }

    /**
     * Create an empty FeatureIterator of the given type.
     * @param type FeatureType can be null
     */
    public static <F extends Feature> FeatureIterator<F> createIterator(){
        return new GenericEmptyFeatureIterator<F>();
    }

    /**
     * Create an empty FeaturCollection wrapping the collection.
     */
    public static FeatureCollection wrap(final FeatureCollection original){
        return new GenericEmptyFeatureCollection(original);
    }

}
