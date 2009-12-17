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

import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.DataStoreRuntimeException;
import org.geotoolkit.feature.simple.SimpleFeatureBuilder;
import org.geotoolkit.util.converter.Classes;

import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;

/**
 * Supports on the fly retyping of  FeatureIterator contents.
 * This handle limiting visible attributs.
 *
 * @author Jody Garnett (Refractions Research)
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class GenericRetypeFeatureIterator<F extends Feature, R extends FeatureIterator<F>>
        implements FeatureIterator<F> {

    protected final R iterator;

    /**
     * Creates a new instance of GenericRetypeFeatureIterator
     *
     * @param iterator FeatureReader to limit
     */
    private GenericRetypeFeatureIterator(final R iterator) {
        this.iterator = iterator;
    }

    /**
     * Supplies mapping from original to target FeatureType.
     *
     * <p>
     * Will also ensure that mapping results in a valid selection of values
     * from the original. Only the xpath expression and binding are checked.
     * </p>
     *
     * @param target Desired FeatureType
     * @param original Original FeatureType
     *
     * @return Mapping from originoal to target FeatureType
     *
     * @throws IllegalArgumentException if unable to provide a mapping
     */
    protected AttributeDescriptor[] typeAttributes(
            final SimpleFeatureType original, final SimpleFeatureType target) {

        if (target.equals(original)) {
            throw new IllegalArgumentException("FeatureReader already produces contents with the correct schema");
        }

        if (target.getAttributeCount() > original.getAttributeCount()) {
            throw new IllegalArgumentException("Unable to retype  FeatureReader (original does not cover requested type)");
        }


        final AttributeDescriptor[] types = new AttributeDescriptor[target.getAttributeCount()];

        for (int i=0; i<target.getAttributeCount(); i++) {
            final AttributeDescriptor attrib = target.getDescriptor(i);
            final String xpath = attrib.getLocalName();

            types[i] = attrib;

            final AttributeDescriptor check = original.getDescriptor(xpath);
            final Class<?> targetBinding = attrib.getType().getBinding();
            final Class<?> checkBinding = check.getType().getBinding();
            if (!targetBinding.isAssignableFrom(checkBinding)) {
                throw new IllegalArgumentException(
                        "Unable to retype FeatureReader for " + xpath +
                        " as " + Classes.getShortName(checkBinding) +
                        " cannot be assigned to " + Classes.getShortName(targetBinding));
            }
            
        }

        return types;
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

    /**
     * Wrap a FeatureReader with a new featuretype.
     *
     * @param <T> extends FeatureType
     * @param <F> extends Feature
     * @param <R> extends FeatureReader<T,F>
     */
    private static final class GenericRetypeFeatureReader<T extends FeatureType, F extends Feature, R extends FeatureReader<T,F>>
            extends GenericRetypeFeatureIterator<F,R> implements FeatureReader<T,F>{

        /**
         * The descriptors we are going to from the original reader
         */
        private final AttributeDescriptor[] types;
        /**
         * Creates retyped features
         */
        private final SimpleFeatureBuilder builder;
        protected final T mask;

        private GenericRetypeFeatureReader(R reader, T mask){
            super(reader);
            this.mask = mask;
            types = typeAttributes((SimpleFeatureType)reader.getFeatureType(), (SimpleFeatureType)mask);
            builder = new SimpleFeatureBuilder((SimpleFeatureType) mask);
        }

        @Override
        public F next() throws DataStoreRuntimeException {
            final SimpleFeature next = (SimpleFeature) iterator.next();
            final String id = next.getID();

            String xpath;
            for (int i = 0; i < types.length; i++) {
                xpath = types[i].getLocalName();
                builder.add(next.getAttribute(xpath));
            }

            return (F) builder.buildFeature(id);
        }

        @Override
        public T getFeatureType() {
            return mask;
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }

    /**
     * Wrap a FeatureReader with a new featuretype.
     */
    public static <T extends FeatureType, F extends Feature> FeatureReader<T,F> wrap(FeatureReader<T,F> reader, FeatureType mask){
        return new GenericRetypeFeatureReader(reader, mask);
    }

}
