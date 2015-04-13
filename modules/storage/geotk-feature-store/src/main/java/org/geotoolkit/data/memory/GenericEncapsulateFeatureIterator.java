/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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


import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureStoreRuntimeException;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.FeatureUtilities;
import org.apache.sis.util.Classes;
import org.geotoolkit.feature.Feature;
import org.geotoolkit.feature.type.FeatureType;
import org.geotoolkit.feature.type.PropertyDescriptor;

/**
 * Supports on the fly encapsulation of features.
 * Each feature will have it's descriptor set.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class GenericEncapsulateFeatureIterator<R extends FeatureIterator> implements FeatureIterator {

    protected final PropertyDescriptor desc;
    protected final R iterator;

    /**
     * Creates a new instance of GenericEncapsulateFeatureIterator
     *
     * @param iterator FeatureReader to limit
     */
    private GenericEncapsulateFeatureIterator(final R iterator, final PropertyDescriptor desc) {
        this.iterator = iterator;
        this.desc = desc;
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
    public Feature next() throws FeatureStoreRuntimeException {
        final Feature next = iterator.next();
        return (Feature) FeatureUtilities.wrapProperty(next, desc);
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
     * @param <T> extends FeatureType
     * @param <F> extends Feature
     * @param <R> extends FeatureReader<T,F>
     */
    private static final class GenericEncapsulateFeatureReader extends GenericEncapsulateFeatureIterator<FeatureReader> implements FeatureReader{


        private GenericEncapsulateFeatureReader(final FeatureReader reader, final PropertyDescriptor desc){
            super(reader,desc);
        }

        @Override
        public FeatureType getFeatureType() {
            return iterator.getFeatureType();
        }
        
        @Override
        public void remove() {
            iterator.remove();
        }

    }

    private static final class GenericEncapsulateFeatureCollection extends WrapFeatureCollection{

        private final PropertyDescriptor desc;

        private GenericEncapsulateFeatureCollection(final FeatureCollection original, final PropertyDescriptor desc){
            super(original);
            this.desc = desc;
        }

        @Override
        public FeatureIterator iterator(final Hints hints) throws FeatureStoreRuntimeException {
            FeatureIterator ite = getOriginalFeatureCollection().iterator(hints);
            if(!(ite instanceof FeatureReader)){
                ite = GenericWrapFeatureIterator.wrapToReader(ite, getOriginalFeatureCollection().getFeatureType());
            }
            return wrap((FeatureReader)ite, desc, hints);
        }

        @Override
        protected Feature modify(Feature original) throws FeatureStoreRuntimeException {
            throw new UnsupportedOperationException("should not have been called.");
        }

    }

    /**
     * Wrap a FeatureReader with a PropertyDescriptor.
     */
    public static FeatureReader wrap(final FeatureReader reader, final PropertyDescriptor desc, final Hints hints){
        return new GenericEncapsulateFeatureReader(reader,desc);
    }

    /**
     * Create an encapsulated FeatureCollection wrapping the given collection.
     */
    public static FeatureCollection wrap(final FeatureCollection original, final PropertyDescriptor desc){
        return new GenericEncapsulateFeatureCollection(original, desc);
    }

}
