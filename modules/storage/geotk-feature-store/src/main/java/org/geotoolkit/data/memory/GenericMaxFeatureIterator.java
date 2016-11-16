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
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureStoreRuntimeException;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.factory.Hints;
import org.apache.sis.util.Classes;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;

/**
 * Basic support for a  FeatureIterator that limits itself to a given number of features.
 *
 * @author Chris Holmes
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class GenericMaxFeatureIterator<R extends FeatureIterator> implements FeatureIterator {

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
    public Feature next() throws FeatureStoreRuntimeException {
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
    public void close() throws FeatureStoreRuntimeException {
        iterator.close();
    }

    /**
     * @return <code>true</code> if the featureReader has not passed the max
     *         and still has more features.
     * @throws IOException If the reader we are filtering encounters a problem
     */
    @Override
    public boolean hasNext() throws FeatureStoreRuntimeException {
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
     */
    private static final class GenericMaxFeatureReader extends GenericMaxFeatureIterator<FeatureReader> implements FeatureReader{

        private GenericMaxFeatureReader(final FeatureReader reader,final int limit){
            super(reader,limit);
        }
        
        @Override
        public FeatureType getFeatureType() {
            return iterator.getFeatureType();
        }

    }

    /**
     * Wrap a FeatureWriter with a max limit.
     *
     */
    private static final class GenericMaxFeatureWriter extends GenericMaxFeatureIterator<FeatureWriter> implements FeatureWriter{

        private GenericMaxFeatureWriter(final FeatureWriter writer,final int limit){
            super(writer,limit);
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

    private static final class GenericMaxFeatureCollection extends WrapFeatureCollection{

        private final int max;

        private GenericMaxFeatureCollection(final FeatureCollection original, final int max){
            super(original);
            this.max = max;
        }

        @Override
        public FeatureIterator iterator(final Hints hints) throws FeatureStoreRuntimeException {
            return wrap(getOriginalFeatureCollection().iterator(hints), max);
        }

        @Override
        protected Feature modify(Feature original) throws FeatureStoreRuntimeException {
            throw new UnsupportedOperationException("should not have been called.");
        }

    }

    /**
     * Wrap a FeatureReader with a max limit.
     */
    public static FeatureIterator wrap(final FeatureIterator reader, final int limit){
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
    public static FeatureReader wrap(final FeatureReader reader, final int limit){
        return new GenericMaxFeatureReader(reader, limit);
    }

    /**
     * Wrap a FeatureWriter with a max limit.
     */
    public static  FeatureWriter wrap(final FeatureWriter writer, final int limit){
        return new GenericMaxFeatureWriter(writer, limit);
    }

    /**
     * Create an limited FeatureCollection wrapping the given collection.
     */
    public static FeatureCollection wrap(final FeatureCollection original, final int max){
        return new GenericMaxFeatureCollection(original, max);
    }

}
