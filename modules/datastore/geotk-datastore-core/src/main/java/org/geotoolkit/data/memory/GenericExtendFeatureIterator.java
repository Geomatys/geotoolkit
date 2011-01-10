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
import java.util.Map;
import org.geotoolkit.data.AbstractFeatureCollection;

import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.DataStoreRuntimeException;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.AbstractFeature;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.LenientFeatureFactory;
import org.geotoolkit.feature.type.DefaultAttributeDescriptor;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.util.converter.Classes;

import org.opengis.feature.Feature;
import org.opengis.feature.FeatureFactory;
import org.opengis.feature.Property;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.identity.FeatureId;

/**
 * Supports on the fly adding attributs of Feature contents.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class GenericExtendFeatureIterator<F extends Feature, R extends FeatureIterator<F>>
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
    private GenericExtendFeatureIterator(final R iterator, final FeatureType mask, final FeatureExtend extend) {
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

        private GenericSeparateExtendFeatureReader(final R reader, final T mask, final FeatureExtend extend){
            super(reader, mask, extend);
        }

        @Override
        public T getFeatureType() {
            return (T) mask;
        }
        
    }

    /**
     * Wrap a FeatureReader with a new featuretype.
     */
    public static <T extends FeatureType, F extends Feature> FeatureReader<T,F> wrap(
            final FeatureReader<T,F> reader, final FeatureExtend extend, final Hints hints){

        final FeatureType mask = extend.getExtendedType(reader.getFeatureType());

        if(mask.equals(reader.getFeatureType())){
            //same type mapping, no need to wrap it
            return reader;
        }
            
        return new GenericSeparateExtendFeatureReader(reader, mask, extend);
    }

    public static FeatureCollection wrap(final FeatureCollection col, final FeatureExtend extend, final Hints hints){
        return new ExtendFeatureCollection(col, extend, hints);
    }

    private static class NoCopyFeature extends AbstractFeature<Collection<Property>>{

        private NoCopyFeature(final AttributeDescriptor desc, final Collection<Property> props, final FeatureId id){
            super(desc, id);
            this.value = props;
        }

    }

    private static class ExtendFeatureCollection extends AbstractFeatureCollection{

        protected final FeatureCollection col;
        protected final FeatureType mask;
        protected final FeatureExtend extend;

        private ExtendFeatureCollection(final FeatureCollection col, final FeatureExtend extend, final Hints hints){
            super(col.getID(),col.getSource());
            this.mask = extend.getExtendedType(col.getFeatureType());
            this.extend = extend;
            this.col = col;
        }

        @Override
        public FeatureType getFeatureType() {
            return mask;
        }

        @Override
        public FeatureCollection subCollection(final Query query) throws DataStoreException {
            final FeatureCollection sub = col.subCollection(query);
            return new ExtendFeatureCollection(sub, extend, query.getHints());
        }

        @Override
        public FeatureIterator iterator(final Hints hints) throws DataStoreRuntimeException {
            final FeatureIterator sub = col.iterator(hints);
            return new GenericExtendFeatureIterator(sub, mask, extend);
        }

        @Override
        public void update(final Filter filter, final Map values) throws DataStoreException {
            col.update(filter, values);
        }

        @Override
        public void remove(final Filter filter) throws DataStoreException {
            col.remove(filter);
        }

    }

    /**
     * Object used to create the new properties for the feature.
     */
    public static abstract class FeatureExtend{

        protected final PropertyDescriptor[] descs;
        protected final FeatureType extendedType;

        public FeatureExtend(final PropertyDescriptor ... descs){
            this.descs = descs;
            this.extendedType = null;
        }

        public FeatureExtend(final FeatureType extendedType){
            this.descs = null;
            this.extendedType = extendedType;
        }

        public FeatureType getExtendedType(final FeatureType original){
            if(extendedType != null){
                return extendedType;
            }else{
                final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
                ftb.copy(original);
                ftb.addAll(descs);
                return ftb.buildFeatureType();
            }
        }

        public abstract void extendProperties(Feature candidate, Collection<Property> props);

    }

}
