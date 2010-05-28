/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

import java.util.ArrayList;
import java.util.Collection;

import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.DataStoreRuntimeException;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.AbstractFeature;
import org.geotoolkit.feature.LenientFeatureFactory;
import org.geotoolkit.feature.type.DefaultAttributeDescriptor;
import org.geotoolkit.util.converter.Classes;

import org.opengis.feature.Feature;
import org.opengis.feature.FeatureFactory;
import org.opengis.feature.Property;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.identity.FeatureId;

/**
 * Supports on the fly adding attributs of Feature contents.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class GenericExtendFeatureIterator<F extends Feature, R extends FeatureIterator<F>>
        implements FeatureIterator<F> {

    protected static final FeatureFactory FF = FactoryFinder
            .getFeatureFactory(new Hints(Hints.FEATURE_FACTORY, LenientFeatureFactory.class));

    protected final FeatureType mask;
    protected final R iterator;
    protected final FeatureExtend extend;

    /**
     * Creates a new instance of GenericExtendFeatureIterator
     * @param iterator : FeatureReader to extend
     * @param mask : expected feature type
     * @param extend : classe that provide new properties
     */
    private GenericExtendFeatureIterator(final R iterator, FeatureType mask, FeatureExtend extend) {
        this.iterator = iterator;
        this.mask = mask;
        this.extend = extend;
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

    private static final class GenericSeparateExtendFeatureReader<T extends FeatureType, F extends Feature, R extends FeatureReader<T,F>>
            extends GenericExtendFeatureIterator<F,R> implements FeatureReader<T,F>{

        private GenericSeparateExtendFeatureReader(R reader, T mask, FeatureExtend extend){
            super(reader, mask, extend);
        }

        @Override
        public T getFeatureType() {
            return (T) mask;
        }
        
        @Override
        public F next() throws DataStoreRuntimeException {
            final Feature next = iterator.next();
            final Collection<Property> properties = new ArrayList<Property>(next.getProperties());
            final AttributeDescriptor desc = new DefaultAttributeDescriptor( mask, mask.getName(), 1, 1, true, null);
            final NoCopyFeature feature = new NoCopyFeature(desc, properties, next.getIdentifier());
            extend.extendProperties(feature, properties);
            return (F) feature;
        }

        @Override
        public void remove() {
            iterator.remove();
        }

    }

    /**
     * Wrap a FeatureReader with a new featuretype.
     */
    public static <T extends FeatureType, F extends Feature> FeatureReader<T,F> wrap(
            FeatureReader<T,F> reader, FeatureType mask, FeatureExtend extend, Hints hints){
        if(mask.equals(reader.getFeatureType())){
            //same type mapping, no need to wrap it
            return reader;
        }
            
        return new GenericSeparateExtendFeatureReader(reader, mask, extend);
    }

    private static class NoCopyFeature extends AbstractFeature<Collection<Property>>{

        private NoCopyFeature(AttributeDescriptor desc, Collection<Property> props, FeatureId id){
            super(desc, id);
            this.value = props;
        }

    }

    /**
     * Object used to create the new properties for the feature.
     */
    public static interface FeatureExtend{

        void extendProperties(Feature candidate, Collection<Property> props);

    }

}
