/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
import java.util.List;

import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.DataStoreRuntimeException;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.HintsPending;
import org.geotoolkit.feature.LenientFeatureFactory;
import org.geotoolkit.feature.simple.DefaultSimpleFeature;
import org.geotoolkit.util.converter.Classes;

import org.opengis.feature.Feature;
import org.opengis.feature.FeatureFactory;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.PropertyDescriptor;

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

    protected static final FeatureFactory FF = FactoryFinder
            .getFeatureFactory(new Hints(Hints.FEATURE_FACTORY, LenientFeatureFactory.class));

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
    protected PropertyDescriptor[] typeAttributes(final FeatureType original, final FeatureType target) {

        if (target.equals(original)) {
            throw new IllegalArgumentException("FeatureReader already produces contents with the correct schema");
        }

        final Collection<PropertyDescriptor> targetDesc = target.getDescriptors();

        if (targetDesc.size() > original.getDescriptors().size()) {
            throw new IllegalArgumentException("Unable to retype  FeatureReader (original does not cover requested type)");
        }

        final PropertyDescriptor[] types = targetDesc.toArray(new PropertyDescriptor[targetDesc.size()]);

        //verify types binding
        for (PropertyDescriptor attrib : types) {
            final String xpath = attrib.getName().getLocalPart();
            final PropertyDescriptor check = original.getDescriptor(xpath);
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
     * Generate a table of indexes betwwen two simplefeaturetypes.
     * Allow acces by index rather then by names.
     *
     * @param target Desired FeatureType
     * @param original Original FeatureType
     *
     * @return Mapping from originoal to target FeatureType
     *
     * @throws IllegalArgumentException if unable to provide a mapping
     */
    protected int[] typeIndexes(final SimpleFeatureType original, final SimpleFeatureType target) {

        if (target.equals(original)) {
            throw new IllegalArgumentException("FeatureReader already produces contents with the correct schema");
        }

        final List<PropertyDescriptor> originalDesc = new ArrayList<PropertyDescriptor>(original.getDescriptors());
        final Collection<PropertyDescriptor> targetDesc = target.getDescriptors();

        if (targetDesc.size() > originalDesc.size()) {
            throw new IllegalArgumentException("Unable to retype  FeatureReader (original does not cover requested type)");
        }


        final int[] types = new int[targetDesc.size()];

        int i=0;
        for (PropertyDescriptor attrib : targetDesc) {
            final String xpath = attrib.getName().getLocalPart();
            final PropertyDescriptor check = original.getDescriptor(xpath);
            types[i] = originalDesc.indexOf(check);

            final Class<?> targetBinding = attrib.getType().getBinding();
            final Class<?> checkBinding = check.getType().getBinding();
            if (!targetBinding.isAssignableFrom(checkBinding)) {
                throw new IllegalArgumentException(
                        "Unable to retype FeatureReader for " + xpath +
                        " as " + Classes.getShortName(checkBinding) +
                        " cannot be assigned to " + Classes.getShortName(targetBinding));
            }
            i++;
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
     * Wrap a FeatureReader with a new featuretype.
     *
     * @param <T> extends FeatureType
     * @param <F> extends Feature
     * @param <R> extends FeatureReader<T,F>
     */
    private static final class GenericSeparateRetypeFeatureReader<T extends FeatureType, F extends Feature, R extends FeatureReader<T,F>>
            extends GenericRetypeFeatureIterator<F,R> implements FeatureReader<T,F>{

        /**
         * The descriptors we are going to from the original reader
         */
        private final PropertyDescriptor[] types;

        protected final T mask;

        private GenericSeparateRetypeFeatureReader(final R reader, final T mask){
            super(reader);
            this.mask = mask;
            types = typeAttributes(reader.getFeatureType(), mask);
        }

        @Override
        public F next() throws DataStoreRuntimeException {
            final Feature next = iterator.next();

            final Collection<Property> properties = new ArrayList<Property>();
            for(final PropertyDescriptor prop : types){
                properties.addAll(next.getProperties(prop.getName()));
            }
            return (F) FF.createFeature(properties, mask, next.getIdentifier().getID());
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
     * Wrap a FeatureReader with a new featuretype. reuse the same feature each time.
     *
     * @param <T> extends FeatureType
     * @param <F> extends Feature
     * @param <R> extends FeatureReader<T,F>
     */
    private static final class GenericReuseRetypeFeatureReader<T extends FeatureType, F extends Feature, R extends FeatureReader<T,F>>
            extends GenericRetypeFeatureIterator<F,R> implements FeatureReader<T,F>{

        /**
         * The descriptors we are going to from the original reader
         */
        private final int[] types;

        private final DefaultSimpleFeature feature;

        /**
         * Creates retyped features
         */
        protected final T mask;

        private GenericReuseRetypeFeatureReader(final R reader, final T mask){
            super(reader);
            this.mask = mask;
            types = typeIndexes((SimpleFeatureType)reader.getFeatureType(), (SimpleFeatureType)mask);

            feature = new DefaultSimpleFeature((SimpleFeatureType) mask, null,new Object[types.length], false);
        }

        @Override
        public F next() throws DataStoreRuntimeException {
            final SimpleFeature next = (SimpleFeature) iterator.next();
            feature.setId(next.getID());

            for (int i = 0; i < types.length; i++) {
                feature.setAttribute(i, next.getAttribute(types[i]));
            }
            
            return (F) feature;
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
    public static <T extends FeatureType, F extends Feature> FeatureReader<T,F> wrap(
            final FeatureReader<T,F> reader, final FeatureType mask, final Hints hints){
        final FeatureType original = reader.getFeatureType();
        if(mask.equals(original)){
            //same type mapping, no need to wrap it
            return reader;
        }

        final Boolean detached = (hints == null) ? null : (Boolean) hints.get(HintsPending.FEATURE_DETACHED);
        if(detached == null || detached || !(original instanceof SimpleFeatureType)
                || original.isAbstract() ){
            //default behavior, make separate features
            return new GenericSeparateRetypeFeatureReader(reader,mask);
        }else{
            //reuse same feature
            return new GenericReuseRetypeFeatureReader(reader, mask);
        }
        
    }

}
