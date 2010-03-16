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

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.DataStoreRuntimeException;

import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

/**
 * Basic support for a FeatureIterator that delegate to a standard java iterator.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class GenericWrapFeatureIterator<F extends Feature> implements FeatureIterator<F> {

    protected final Iterator<F> iterator;

    /**
     * Creates a new instance of GenericMaxFeatureIterator
     *
     * @param iterator FeatureReader to limit
     * @param maxFeatures maximum number of feature
     */
    private GenericWrapFeatureIterator(final Iterator<F> iterator) {
        this.iterator = iterator;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public F next() throws DataStoreRuntimeException {
        return iterator.next();
    }

    /**
     * {@inheritDoc }
     *
     * This implementation does nothing since standard iterator dont have a close
     * method.
     */
    @Override
    public void close() throws DataStoreRuntimeException {
        if (iterator instanceof Closeable) {
            try {
                ((Closeable) iterator).close();
            } catch (IOException ex) {
                Logger.getLogger(GenericWrapFeatureIterator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean hasNext() throws DataStoreRuntimeException {
        return iterator.hasNext();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void remove() {
        iterator.remove();
    }

    /**
     * Wrap an Iterator as a FeatureReader.
     * 
     * @param <T> extends FeatureType
     * @param <F> extends Feature
     * @param <R> extends FeatureReader<T,F>
     */
    private static final class GenericWrapFeatureReader<T extends FeatureType, F extends Feature>
            extends GenericWrapFeatureIterator<F> implements FeatureReader<T,F>{

        private final T type;

        private GenericWrapFeatureReader(Iterator<F> ite, T type){
            super(ite);
            this.type = type;
        }

        @Override
        public T getFeatureType() {
            return type;
        }
        
    }

    /**
     * Wrap an Iterator as a FeatureWriter.
     *
     * @param <T> extends FeatureType
     * @param <F> extends Feature
     * @param <R> extends FeatureWriter<T,F>
     */
    private static final class GenericWrapFeatureWriter<T extends FeatureType, F extends Feature>
            extends GenericWrapFeatureIterator<F> implements FeatureWriter<T,F>{

        private final T type;

        private GenericWrapFeatureWriter(Iterator<F> iterator, T type){
            super(iterator);
            this.type = type;
        }

        @Override
        public T getFeatureType() {
            return type;
        }

        @Override
        public void write() throws DataStoreRuntimeException {
            throw new DataStoreRuntimeException("Can not write on a wrapped iterator.");
        }
    }

    /**
     * Wrap an Iterator as a FeatureIterator.
     */
    public static <F extends Feature> FeatureIterator<F> wrapToIterator(Iterator<F> reader){
        return new GenericWrapFeatureIterator(reader);
    }

    /**
     * Wrap an Iterator as a FeatureReader.
     */
    public static <T extends FeatureType, F extends Feature> FeatureReader<T,F> wrapToReader(Iterator<F> reader, T type){
        return new GenericWrapFeatureReader(reader, type);
    }

    /**
     * Wrap an Iterator as a FeatureWriter.
     */
    public static <T extends FeatureType, F extends Feature> FeatureWriter<T,F> wrapToWriter(Iterator<F> writer, T type){
        return new GenericWrapFeatureWriter(writer, type);
    }

}
