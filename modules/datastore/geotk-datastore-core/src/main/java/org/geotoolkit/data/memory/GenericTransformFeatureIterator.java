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

import com.vividsolutions.jts.geom.Geometry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.DataStoreRuntimeException;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.HintsPending;
import org.geotoolkit.feature.AbstractFeature;
import org.geotoolkit.feature.DefaultFeature;
import org.geotoolkit.feature.LenientFeatureFactory;
import org.geotoolkit.feature.simple.DefaultSimpleFeature;
import org.geotoolkit.geometry.jts.transform.GeometryTransformer;
import org.geotoolkit.util.converter.Classes;

import org.opengis.feature.Feature;
import org.opengis.feature.FeatureFactory;
import org.opengis.feature.GeometryAttribute;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.referencing.operation.TransformException;

/**
 * Basic support for a  FeatureIterator that transform the geometry attribut.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class GenericTransformFeatureIterator<F extends Feature, R extends FeatureIterator<F>>
        implements FeatureIterator<F> {

    protected static final FeatureFactory FF = FactoryFinder
            .getFeatureFactory(new Hints(Hints.FEATURE_FACTORY, LenientFeatureFactory.class));

    protected final R iterator;
    protected final GeometryTransformer transformer;

    /**
     * Creates a new instance of GenericTransformFeatureIterator
     *
     * @param iterator FeatureReader to limit
     * @param transformer the transformer to use on each geometry
     */
    protected GenericTransformFeatureIterator(final R iterator, final GeometryTransformer transformer) {
        this.iterator = iterator;
        this.transformer = transformer;
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
        sb.append(" : ").append(transformer);
        sb.append('\n');
        String subIterator = "\u2514\u2500\u2500" + iterator.toString(); //move text to the right
        subIterator = subIterator.replaceAll("\n", "\n\u00A0\u00A0\u00A0"); //move text to the right
        sb.append(subIterator);
        return sb.toString();
    }

    /**
     * Wrap a FeatureReader with a transform operation.
     *
     * @param <T> extends FeatureType
     * @param <F> extends Feature
     * @param <R> extends FeatureReader<T,F>
     */
    protected static final class GenericTransformFeatureReader<T extends FeatureType, F extends Feature, R extends FeatureReader<T,F>>
            extends GenericTransformFeatureIterator<F,R> implements FeatureReader<T,F>{

        private GenericTransformFeatureReader(final R reader, final GeometryTransformer transformer) {
            super(reader, transformer);            
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public F next() throws DataStoreRuntimeException {
            final Feature next = iterator.next();

            final Collection<Property> properties = new ArrayList<Property>();
            for(Property prop : next.getProperties()){
                if(prop instanceof GeometryAttribute){
                    Object value = prop.getValue();
                    if(value != null){
                        //create a new property with the transformed geometry
                        prop = FF.createGeometryAttribute(value,
                                (GeometryDescriptor)prop.getDescriptor(), null, null);

                        try {
                            //transform the geometry
                            prop.setValue(transformer.transform((Geometry) value));
                        } catch (TransformException e) {
                            throw new DataStoreRuntimeException("A transformation exception occurred while reprojecting data on the fly", e);
                        }

                    }
                }
                properties.add(prop);
            }
            return (F) FF.createFeature(properties, next.getType(), next.getIdentifier().getID());
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public T getFeatureType() {
            return iterator.getFeatureType();
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void remove() {
            iterator.remove();
        }
    }

    /**
     * Wrap a FeatureReader with a transform operation.
     *
     * @param <T> extends FeatureType
     * @param <F> extends Feature
     * @param <R> extends FeatureReader<T,F>
     */
    protected static final class GenericReuseTransformFeatureReader<T extends FeatureType, F extends Feature, R extends FeatureReader<T,F>>
            extends GenericTransformFeatureIterator<F,R> implements FeatureReader<T,F>{

        private final List<Property> properties = new ArrayList<Property>();
        private final AbstractFeature feature;

        private GenericReuseTransformFeatureReader(final R reader, final GeometryTransformer transformer) {
            super(reader, transformer);

            final FeatureType ft = reader.getFeatureType();
            if(ft instanceof SimpleFeatureType){
                feature = new DefaultSimpleFeature((SimpleFeatureType)ft, null, properties, false);
            }else{
                feature = new DefaultFeature(properties, ft, null);
            }

        }

        /**
         * {@inheritDoc }
         */
        @Override
        public F next() throws DataStoreRuntimeException {
            final Feature next = iterator.next();
            feature.setId(next.getIdentifier());

            properties.clear();
            for(Property prop : next.getProperties()){
                if(prop instanceof GeometryAttribute){
                    Object value = prop.getValue();
                    if(value != null){
                        try {
                            //transform the geometry
                            prop.setValue(transformer.transform((Geometry) value));
                        } catch (TransformException e) {
                            throw new DataStoreRuntimeException("A transformation exception occurred while reprojecting data on the fly", e);
                        }

                    }
                }
                properties.add(prop);
            }
            return (F)feature;
        }

        @Override
        public T getFeatureType() {
            return iterator.getFeatureType();
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }

    private static final class GenericTransformFeatureCollection extends WrapFeatureCollection{

        private final GeometryTransformer transformer;

        private GenericTransformFeatureCollection(final FeatureCollection original, final GeometryTransformer transformer){
            super(original);
            this.transformer = transformer;
        }

        @Override
        public FeatureIterator iterator(final Hints hints) throws DataStoreRuntimeException {
            FeatureIterator ite = getOriginalFeatureCollection().iterator(hints);
            if(!(ite instanceof FeatureReader)){
                ite = GenericWrapFeatureIterator.wrapToReader(ite, getFeatureType());
            }
            return wrap((FeatureReader) ite, transformer, hints);
        }

        @Override
        protected Feature modify(Feature original) throws DataStoreRuntimeException {
            throw new UnsupportedOperationException("should not have been called.");
        }

    }

    /**
     * Wrap a FeatureReader with a reprojection.
     */
    public static <T extends FeatureType, F extends Feature> FeatureReader<T, F> wrap(
            final FeatureReader<T, F> reader, final GeometryTransformer transformer, final Hints hints) {
        final GeometryDescriptor desc = reader.getFeatureType().getGeometryDescriptor();
        if (desc != null) {

            final Boolean detached = (hints == null) ? null : (Boolean) hints.get(HintsPending.FEATURE_DETACHED);
            if(detached == null || detached){
                //default behavior, make separate features
                return new GenericTransformFeatureReader(reader,transformer);
            }else{
                //re-use same feature
                return new GenericReuseTransformFeatureReader(reader, transformer);
            }
            
        } else {
            return reader;
        }
    }

    /**
     * Create a reproject FeatureCollection wrapping the given collection.
     */
    public static FeatureCollection wrap(final FeatureCollection original, final GeometryTransformer transformer){
        return new GenericTransformFeatureCollection(original, transformer);
    }

}
