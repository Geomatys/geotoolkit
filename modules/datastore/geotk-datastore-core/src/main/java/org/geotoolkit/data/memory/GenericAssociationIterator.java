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


import java.util.AbstractCollection;
import java.util.Collection;

import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.DataStoreRuntimeException;
import org.geotoolkit.data.DataUtilities;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.DefaultAssociation;
import org.geotoolkit.util.collection.CloseableIterator;
import org.geotoolkit.util.converter.Classes;

import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.type.AssociationDescriptor;

/**
 * Supports on the fly encapsulation of features.
 * Each feature will be encapsulation in an association property.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class GenericAssociationIterator implements CloseableIterator {

    protected final AssociationDescriptor desc;
    protected final Object link;
    protected final FeatureIterator iterator;

    /**
     * Creates a new instance of GenericEncapsulateFeatureIterator
     *
     * @param iterator FeatureReader to limit
     */
    private GenericAssociationIterator(final FeatureIterator iterator, 
            final AssociationDescriptor desc, final Object link) {
        this.iterator = iterator;
        this.desc = desc;
        this.link = link;
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
    public Property next() throws DataStoreRuntimeException {
        final Feature next = iterator.next();
        return new DefaultAssociation(next, desc, link);
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public boolean hasNext() throws DataStoreRuntimeException {
        return iterator.hasNext();
    }
    
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

    private static final class GenericAssociationCollection extends AbstractCollection {

        private final FeatureCollection original;
        private final AssociationDescriptor desc;
        private final Object link;

        private GenericAssociationCollection(final FeatureCollection original, 
                final AssociationDescriptor desc, final Object link){
            this.original = original;
            this.desc = desc;
            this.link = link;
        }

        public CloseableIterator iterator(final Hints hints) throws DataStoreRuntimeException {
            FeatureIterator ite = original.iterator(hints);
            return wrap(ite, desc, link, hints);
        }

        @Override
        public CloseableIterator iterator() {
            return iterator(null);
        }

        @Override
        public int size() {
            return (int) DataUtilities.calculateCount(iterator());
        }

    }

    /**
     * Wrap a FeatureReader with a PropertyDescriptor.
     */
    public static CloseableIterator wrap(final FeatureIterator reader, 
            final AssociationDescriptor desc, final Object link, final Hints hints){
        return new GenericAssociationIterator(reader,desc,link);
    }

    /**
     * Create an encapsulated FeatureCollection wrapping the given collection.
     */
    public static Collection wrap(final FeatureCollection original, 
            final AssociationDescriptor desc, final Object link){
        return new GenericAssociationCollection(original, desc, link);
    }

}
