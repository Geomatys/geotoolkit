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

package org.geotoolkit.internal.data;

import org.geotoolkit.data.FeatureStoreRuntimeException;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.factory.Hints;
import org.apache.sis.util.Classes;
import org.geotoolkit.data.memory.WrapFeatureCollection;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.filter.Filter;

/**
 * Basic support for a  FeatureIterator that filter feature based on the given filter.
 *
 * @author Chris Holmes
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class GenericFilterFeatureIterator<R extends FeatureIterator> implements FeatureIterator {

    protected final R iterator;
    protected final Filter filter;
    protected Feature next = null;

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
    public Feature next() throws FeatureStoreRuntimeException {
        if (hasNext()) {
            // hasNext() ensures that next != null
            final Feature f = next;
            next = null;
            return f;
        } else {
            //if it's a writer, we switches to append mode
            //otherwise the iterator will raise an error
            return iterator.next();
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void close() throws FeatureStoreRuntimeException {
        iterator.close();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean hasNext() throws FeatureStoreRuntimeException {
        if (next != null) {
            return true;
        }

        Feature peek;
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
    private static final class GenericFilterFeatureReader extends GenericFilterFeatureIterator<FeatureReader> implements FeatureReader{

        private GenericFilterFeatureReader(final FeatureReader reader, final Filter filter){
            super(reader,filter);
        }

        @Override
        public FeatureType getFeatureType() {
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
    private static final class GenericFilterFeatureWriter extends GenericFilterFeatureIterator<FeatureWriter> implements FeatureWriter{

        private GenericFilterFeatureWriter(final FeatureWriter writer, final Filter filter){
            super(writer,filter);
        }

        @Override
        public FeatureType getFeatureType() {
            return iterator.getFeatureType();
        }

        @Override
        public void write() throws FeatureStoreRuntimeException {
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
        public FeatureIterator iterator(final Hints hints) throws FeatureStoreRuntimeException {
            return wrap(getOriginalFeatureCollection().iterator(hints), filter);
        }

        @Override
        protected Feature modify(Feature original) throws FeatureStoreRuntimeException {
            throw new UnsupportedOperationException("should not have been called.");
        }

    }

    /**
     * Wrap a FeatureIterator with a filter.
     */
    public static FeatureIterator wrap(final FeatureIterator reader, final Filter filter){
        if(reader instanceof FeatureReader){
            return wrap((FeatureReader)reader,filter);
        }else if(reader instanceof FeatureWriter){
            return wrap((FeatureWriter)reader,filter);
        }else{
            return new GenericFilterFeatureIterator(reader, filter);
        }
    }

    /**
     * Wrap a FeatureReader with a filter.
     */
    public static FeatureReader wrap(final FeatureReader reader, final Filter filter){
        return new GenericFilterFeatureReader(reader, filter);
    }

    /**
     * Wrap a FeatureWriter with a filter.
     */
    public static FeatureWriter wrap(final FeatureWriter writer, final Filter filter){
        return new GenericFilterFeatureWriter(writer, filter);
    }

    /**
     * Create an filtered FeatureCollection wrapping the given collection.
     */
    public static FeatureCollection wrap(final FeatureCollection original, final Filter filter){
        return new GenericFilterFeatureCollection(original, filter);
    }

}
