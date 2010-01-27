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

import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.DataStoreRuntimeException;
import org.geotoolkit.feature.FeatureTypeUtilities;
import org.geotoolkit.feature.SchemaException;
import org.geotoolkit.feature.simple.SimpleFeatureBuilder;
import org.geotoolkit.geometry.jts.GeometryCoordinateSequenceTransformer;
import org.geotoolkit.referencing.CRS;

import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
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

    /**
     * Wrap a FeatureReader with a reprojection.
     *
     * @param <T> extends FeatureType
     * @param <F> extends Feature
     * @param <R> extends FeatureReader<T,F>
     */
    private static final class GenericReprojectFeatureReader<T extends FeatureType, F extends Feature, R extends FeatureReader<T,F>>
            extends GenericReprojectFeatureIterator<F,R> implements FeatureReader<T,F>{

        private final SimpleFeatureType schema;
        private final GeometryCoordinateSequenceTransformer transformer = new GeometryCoordinateSequenceTransformer();

        private GenericReprojectFeatureReader(R reader, CoordinateReferenceSystem crs) throws FactoryException, SchemaException{
            super(reader);

            if (crs == null) {
                throw new NullPointerException("CRS can not be null.");
            }

            final SimpleFeatureType type = (SimpleFeatureType) reader.getFeatureType();
            final CoordinateReferenceSystem original = type.getGeometryDescriptor().getCoordinateReferenceSystem();

            if (crs.equals(original)) {
                throw new IllegalArgumentException("CoordinateSystem " + crs + " already used (check before using wrapper)");
            }

            this.schema = FeatureTypeUtilities.transform(type, crs);
            transformer.setMathTransform(CRS.findMathTransform(original, crs, true));
        }


        @Override
        public F next() throws DataStoreRuntimeException {

            SimpleFeature next = (SimpleFeature) iterator.next();
            Object[] attributes = next.getAttributes().toArray();

            try {
                for (int i = 0; i < attributes.length; i++) {
                    if (attributes[i] instanceof Geometry) {
                        attributes[i] = transformer.transform((Geometry) attributes[i]);
                    }
                }
            } catch (TransformException e) {
                throw new DataStoreRuntimeException("A transformation exception occurred while reprojecting data on the fly", e);
            }

            return (F) SimpleFeatureBuilder.build(schema, attributes, next.getID());
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
