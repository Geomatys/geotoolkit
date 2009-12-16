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
import org.opengis.filter.Filter;

/**
 * Basic support for a  FeatureIterator that filter feature based on the given filter.
 *
 * @author Chris Holmes
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class GenericFilterFeatureIterator<F extends Feature, R extends FeatureIterator<F>>
        implements FeatureIterator<F> {

    protected final R iterator;
    protected final Filter filter;
    protected F next = null;

    /**
     * Creates a new instance of GenericFilterFeatureIterator
     *
     * @param iterator FeatureReader to limit
     * @param Filter filter
     */
    private GenericFilterFeatureIterator(final R iterator, final Filter filter) {
        this.iterator = iterator;
        this.filter = filter;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public F next() throws DataStoreRuntimeException {
        if (hasNext()) {
            // hasNext() ensures that next != null
            final F f = next;
            next = null;
            return f;
        } else {
            throw new NoSuchElementException("No such Feature exsists");
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
    public boolean hasNext() throws DataStoreRuntimeException {
        if (next != null) {
            return true;
        }

        F peek;
        while (iterator.hasNext()) {
            peek = iterator.next();

            if (filter.evaluate(peek)) {
                next = peek;
                return true;
            }
        }

        return false;
    }

    /**
     * Wrap a FeatureReader with a filter.
     * 
     * @param <T> extends FeatureType
     * @param <F> extends Feature
     * @param <R> extends FeatureReader<T,F>
     */
    private static final class GenericFilterFeatureReader<T extends FeatureType, F extends Feature, R extends FeatureReader<T,F>>
            extends GenericFilterFeatureIterator<F,R> implements FeatureReader<T,F>{

        private GenericFilterFeatureReader(R reader, Filter filter){
            super(reader,filter);
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
     * Wrap a FeatureWriter with a filter.
     *
     * @param <T> extends FeatureType
     * @param <F> extends Feature
     * @param <R> extends FeatureWriter<T,F>
     */
    private static final class GenericFilterFeatureWriter<T extends FeatureType, F extends Feature, R extends FeatureWriter<T,F>>
            extends GenericFilterFeatureIterator<F,R> implements FeatureWriter<T,F>{

        private GenericFilterFeatureWriter(R writer, Filter filter){
            super(writer,filter);
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
     * Wrap a FeatureReader with a filter.
     */
    public static <T extends FeatureType, F extends Feature> FeatureReader<T,F> wrap(FeatureReader<T,F> reader, Filter filter){
        return new GenericFilterFeatureReader(reader, filter);
    }

    /**
     * Wrap a FeatureWriter with a filter.
     */
    public static <T extends FeatureType, F extends Feature> FeatureWriter<T,F> wrap(FeatureWriter<T,F> writer, Filter filter){
        return new GenericFilterFeatureWriter(writer, filter);
    }

}
