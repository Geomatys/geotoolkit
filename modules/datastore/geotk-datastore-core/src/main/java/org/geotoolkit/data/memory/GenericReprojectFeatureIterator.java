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

import com.vividsolutions.jts.geom.Geometry;
import java.util.ArrayList;
import java.util.Collection;

import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.DataStoreRuntimeException;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.FeatureTypeUtilities;
import org.geotoolkit.feature.LenientFeatureFactory;
import org.geotoolkit.feature.SchemaException;
import org.geotoolkit.geometry.jts.GeometryCoordinateSequenceTransformer;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.util.converter.Classes;

import org.opengis.feature.Feature;
import org.opengis.feature.FeatureFactory;
import org.opengis.feature.GeometryAttribute;
import org.opengis.feature.Property;
import org.opengis.feature.type.FeatureType;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

/**
 * Basic support for a  FeatureIterator that reprojects the geometry attribut.
 *
 * @author Chris Holmes
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class GenericReprojectFeatureIterator<F extends Feature, R extends FeatureIterator<F>>
        implements FeatureIterator<F> {

    protected static final FeatureFactory FF = FactoryFinder
            .getFeatureFactory(new Hints(Hints.FEATURE_FACTORY, LenientFeatureFactory.class));

    protected final R iterator;

    /**
     * Creates a new instance of GenericReprojectFeatureIterator
     *
     * @param iterator FeatureReader to limit
     * @param maxFeatures maximum number of feature
     */
    private GenericReprojectFeatureIterator(final R iterator) {
        this.iterator = iterator;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public F next() throws DataStoreRuntimeException {
        return iterator.next();
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
     * Wrap a FeatureReader with a reprojection.
     *
     * @param <T> extends FeatureType
     * @param <F> extends Feature
     * @param <R> extends FeatureReader<T,F>
     */
    private static final class GenericReprojectFeatureReader<T extends FeatureType, F extends Feature, R extends FeatureReader<T,F>>
            extends GenericReprojectFeatureIterator<F,R> implements FeatureReader<T,F>{

        private final FeatureType schema;
        private final GeometryCoordinateSequenceTransformer transformer = new GeometryCoordinateSequenceTransformer();

        private GenericReprojectFeatureReader(R reader, CoordinateReferenceSystem crs) throws FactoryException, SchemaException{
            super(reader);

            if (crs == null) {
                throw new NullPointerException("CRS can not be null.");
            }

            final FeatureType type = reader.getFeatureType();
            final CoordinateReferenceSystem original = type.getGeometryDescriptor().getCoordinateReferenceSystem();

            if (crs.equals(original)) {
                throw new IllegalArgumentException("CoordinateSystem " + crs + " already used (check before using wrapper)");
            }

            this.schema = FeatureTypeUtilities.transform(type, crs);
            transformer.setMathTransform(CRS.findMathTransform(original, crs, true));
        }


        @Override
        public F next() throws DataStoreRuntimeException {
            final Feature next = iterator.next();
            
            final Collection<Property> properties = new ArrayList<Property>();
            for(Property prop : next.getProperties()){
                if(prop instanceof GeometryAttribute){
                    Object value = prop.getValue();
                    if(value != null){
                        try {
                            prop.setValue(transformer.transform((Geometry) value));
                        } catch (TransformException e) {
                            throw new DataStoreRuntimeException("A transformation exception occurred while reprojecting data on the fly", e);
                        }
                    }
                }
                properties.add(prop);
            }
            return (F) FF.createFeature(properties, schema, next.getIdentifier().getID());
        }

        @Override
        public T getFeatureType() {
            return (T) schema;
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }

    /**
     * Wrap a FeatureReader with a reprojection.
     */
    public static <T extends FeatureType, F extends Feature> FeatureReader<T, F> wrap(
            FeatureReader<T, F> reader, CoordinateReferenceSystem crs) throws FactoryException, SchemaException {
        if (reader.getFeatureType().getGeometryDescriptor() != null) {
            return new GenericReprojectFeatureReader(reader, crs);
        } else {
            return reader;
        }
    }

}
