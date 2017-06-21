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

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureStoreRuntimeException;
import org.geotoolkit.data.FeatureWriter;
import org.apache.sis.util.Classes;
import org.apache.sis.util.logging.Logging;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;

/**
 * Basic support for a FeatureIterator that delegate to a standard java iterator.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
class GenericWrapFeatureIterator implements FeatureIterator {

    protected final Iterator<? extends Feature> iterator;

    /**
     * Creates a new instance of GenericWrapFeatureIterator
     *
     * @param iterator FeatureReader to limit
     * @param maxFeatures maximum number of feature
     */
    private GenericWrapFeatureIterator(final Iterator<? extends Feature> iterator) {
        this.iterator = iterator;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Feature next() throws FeatureStoreRuntimeException {
        return iterator.next();
    }

    /**
     * {@inheritDoc }
     *
     * This implementation does nothing since standard iterator dont have a close
     * method.
     */
    @Override
    public void close() throws FeatureStoreRuntimeException {
        if (iterator instanceof Closeable) {
            try {
                ((Closeable) iterator).close();
            } catch (IOException ex) {
                Logging.getLogger("org.geotoolkit.data.memory").log(Level.WARNING, null, ex);
            }
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean hasNext() throws FeatureStoreRuntimeException {
        return iterator.hasNext();
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
        sb.append('\n');
        String subIterator = "\u2514\u2500\u2500" + iterator.toString(); //move text to the right
        subIterator = subIterator.replaceAll("\n", "\n\u00A0\u00A0\u00A0"); //move text to the right
        sb.append(subIterator);
        return sb.toString();
    }

    /**
     * Wrap an Iterator as a FeatureReader.
     *
     * @param <T> extends FeatureType
     * @param <F> extends Feature
     * @param <R> extends FeatureReader<T,F>
     */
    private static final class GenericWrapFeatureReader extends GenericWrapFeatureIterator implements FeatureReader{

        private final FeatureType type;

        private GenericWrapFeatureReader(final Iterator<? extends Feature> ite, final FeatureType type){
            super(ite);
            this.type = type;
        }

        @Override
        public FeatureType getFeatureType() {
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
    private static final class GenericWrapFeatureWriter extends GenericWrapFeatureIterator implements FeatureWriter{

        private final FeatureType type;

        private GenericWrapFeatureWriter(final Iterator<? extends Feature> iterator, final FeatureType type){
            super(iterator);
            this.type = type;
        }

        @Override
        public FeatureType getFeatureType() {
            return type;
        }

        @Override
        public void write() throws FeatureStoreRuntimeException {
            throw new FeatureStoreRuntimeException("Can not write on a wrapped iterator.");
        }
    }

    /**
     * Wrap an Iterator as a FeatureIterator.
     */
    static FeatureIterator wrapToIterator(final Iterator<? extends Feature> reader){
        return new GenericWrapFeatureIterator(reader);
    }

    /**
     * Wrap an Iterator as a FeatureReader.
     */
    static FeatureReader wrapToReader(final Iterator<? extends Feature> reader, final FeatureType type){
        return new GenericWrapFeatureReader(reader, type);
    }

    /**
     * Wrap an Iterator as a FeatureWriter.
     */
    static FeatureWriter wrapToWriter(final Iterator<? extends Feature> writer, final FeatureType type){
        return new GenericWrapFeatureWriter(writer, type);
    }

}
