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
import org.opengis.filter.Filter;

/**
 * Basic support for a  FeatureIterator that filter feature based on the given filter.
 *
 * @author Chris Holmes
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class GenericFilterFeatureIterator<F extends Feature, R extends FeatureIterator<F>>
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
     * {@inheritDoc }
     */
    @Override
    public void remove() {
        iterator.remove();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(Classes.getShortClassName(this));
        sb.append("[Filter=").append(filter).append("]\n");
        String subIterator = "\u2514\u2500\u2500" + iterator.toString(); //move text to the right
        subIterator = subIterator.replaceAll("\n", "\n\u00A0\u00A0\u00A0"); //move text to the right
        sb.append(subIterator);
        return sb.toString();
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

        private GenericFilterFeatureReader(final R reader, final Filter filter){
            super(reader,filter);
        }
        
        @Override
        public T getFeatureType() {
            return iterator.getFeatureType();
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

        private GenericFilterFeatureWriter(final R writer, final Filter filter){
            super(writer,filter);
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

    private static final class GenericFilterFeatureCollection extends WrapFeatureCollection{

        private final Filter filter;

        private GenericFilterFeatureCollection(final FeatureCollection original, final Filter filter){
            super(original);
            this.filter = filter;
        }

        @Override
        public FeatureIterator iterator(final Hints hints) throws DataStoreRuntimeException {
            return wrap(getOriginalFeatureCollection().iterator(hints), filter);
        }

        @Override
        protected Feature modify(Feature original) throws DataStoreRuntimeException {
            throw new UnsupportedOperationException("should not have been called.");
        }

    }

    /**
     * Wrap a FeatureIterator with a filter.
     */
    public static <F extends Feature> FeatureIterator<F> wrap(final FeatureIterator<F> reader, final Filter filter){
        return new GenericFilterFeatureIterator(reader, filter);
    }

    /**
     * Wrap a FeatureReader with a filter.
     */
    public static <T extends FeatureType, F extends Feature> FeatureReader<T,F> wrap(final FeatureReader<T,F> reader, final Filter filter){
        return new GenericFilterFeatureReader(reader, filter);
    }

    /**
     * Wrap a FeatureWriter with a filter.
     */
    public static <T extends FeatureType, F extends Feature> FeatureWriter<T,F> wrap(final FeatureWriter<T,F> writer, final Filter filter){
        return new GenericFilterFeatureWriter(writer, filter);
    }

    /**
     * Create an filtered FeatureCollection wrapping the given collection.
     */
    public static FeatureCollection wrap(final FeatureCollection original, final Filter filter){
        return new GenericFilterFeatureCollection(original, filter);
    }

}
