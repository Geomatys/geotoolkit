/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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

import org.apache.sis.feature.DecoratedFeatureType;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureStoreRuntimeException;
import org.geotoolkit.factory.Hints;
import org.apache.sis.util.Classes;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;

/**
 * Supports on the fly feature decoration of FeatureIterator contents.
 *
 * @author Johann Sorel (Geomatys)
 * @param <R>
 */
public abstract class GenericDecoratedFeatureIterator<R extends FeatureIterator> implements FeatureIterator {

    protected final R iterator;

    /**
     * Creates a new instance of GenericRetypeFeatureIterator
     *
     * @param iterator FeatureReader to limit
     */
    private GenericDecoratedFeatureIterator(final R iterator) {
        this.iterator = iterator;
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
        return iterator.hasNext();
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
     * Decorate a FeatureReader.
     */
    private static final class GenericRetypeFeatureReader extends GenericDecoratedFeatureIterator<FeatureReader> implements FeatureReader{

        private final DecoratedFeatureType mask;

        private GenericRetypeFeatureReader(final FeatureReader reader, final DecoratedFeatureType mask){
            super(reader);
            this.mask = mask;
        }

        @Override
        public Feature next() throws FeatureStoreRuntimeException {
            return mask.newInstance(iterator.next());
        }

        @Override
        public FeatureType getFeatureType() {
            return mask;
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }

    private static final class GenericRetypeFeatureCollection extends WrapFeatureCollection{

        private final DecoratedFeatureType mask;

        private GenericRetypeFeatureCollection(final FeatureCollection original, final DecoratedFeatureType mask){
            super(original);
            this.mask = mask;
        }

        @Override
        public FeatureType getFeatureType() {
            return mask;
        }

        @Override
        public FeatureIterator iterator(final Hints hints) throws FeatureStoreRuntimeException {
            FeatureIterator ite = getOriginalFeatureCollection().iterator(hints);
            if(!(ite instanceof FeatureReader)){
                ite = GenericWrapFeatureIterator.wrapToReader(ite, getOriginalFeatureCollection().getFeatureType());
            }
            return wrap((FeatureReader)ite, mask, hints);
        }

        @Override
        protected Feature modify(Feature original) throws FeatureStoreRuntimeException {
            throw new UnsupportedOperationException("should not have been called.");
        }

    }

    /**
     * Decorate a feature reader.
     */
    public static FeatureReader wrap(final FeatureReader reader, final DecoratedFeatureType mask, final Hints hints){
        final FeatureType original = reader.getFeatureType();
        if(mask.equals(original)){
            //same type mapping, no need to wrap it
            return reader;
        } else {
            return new GenericRetypeFeatureReader(reader,mask);
        }
    }

    /**
     * Decorate a FeatureCollection.
     */
    public static FeatureCollection wrap(final FeatureCollection original, final DecoratedFeatureType mask){
        return new GenericRetypeFeatureCollection(original, mask);
    }

}
