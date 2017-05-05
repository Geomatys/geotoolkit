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
 * Basic support for a  FeatureIterator that starts at a given index.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class GenericStartIndexFeatureIterator<R extends FeatureIterator> implements FeatureIterator {

    protected final R iterator;
    protected final int startIndex;
    protected Feature nextFeature = null;
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
    public Feature next() throws FeatureStoreRuntimeException {
        if (hasNext()) {
            // hasNext() ensures that next != null
            final Feature f = nextFeature;
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
    public void close() throws FeatureStoreRuntimeException {
        iterator.close();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public synchronized boolean hasNext() throws FeatureStoreRuntimeException {
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
    private static final class GenericStartIndexFeatureReader extends GenericStartIndexFeatureIterator<FeatureReader> implements FeatureReader{

        private GenericStartIndexFeatureReader(final FeatureReader reader,final int limit){
            super(reader,limit);
        }

        @Override
        public FeatureType getFeatureType() {
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
    private static final class GenericStartIndexFeatureWriter extends GenericStartIndexFeatureIterator<FeatureWriter> implements FeatureWriter{

        private GenericStartIndexFeatureWriter(final FeatureWriter writer,final int limit){
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

    private static final class GenericStartIndexFeatureCollection extends WrapFeatureCollection{

        private final int startIndex;

        private GenericStartIndexFeatureCollection(final FeatureCollection original, final int startIndex){
            super(original);
            this.startIndex = startIndex;
        }

        @Override
        public FeatureIterator iterator(final Hints hints) throws FeatureStoreRuntimeException {
            return wrap(getOriginalFeatureCollection().iterator(hints), startIndex);
        }

        @Override
        protected Feature modify(Feature original) throws FeatureStoreRuntimeException {
            throw new UnsupportedOperationException("should not have been called.");
        }

    }


    /**
     * Wrap a FeatureIterator with a start index.
     */
    public static FeatureIterator wrap(final FeatureIterator reader, final int limit){
        if(reader instanceof FeatureReader){
            return wrap((FeatureReader)reader,limit);
        }else if(reader instanceof FeatureWriter){
            return wrap((FeatureWriter)reader,limit);
        }else{
            return new GenericStartIndexFeatureIterator(reader, limit);
        }
    }

    /**
     * Wrap a FeatureReader with a start index.
     */
    public static FeatureReader wrap(final FeatureReader reader, final int limit){
        return new GenericStartIndexFeatureReader(reader, limit);
    }

    /**
     * Wrap a FeatureWriter with a start index.
     */
    public static FeatureWriter wrap(final FeatureWriter writer, final int limit){
        return new GenericStartIndexFeatureWriter(writer, limit);
    }

    /**
     * Create an differed start index FeatureCollection wrapping the given collection.
     */
    public static FeatureCollection wrap(final FeatureCollection original, final int startIndex){
        return new GenericStartIndexFeatureCollection(original, startIndex);
    }

}
