/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 * 
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.data.store;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.geotoolkit.factory.FactoryRegistryException;
import org.geotoolkit.factory.AuthorityFactoryFinder;
import org.geotoolkit.feature.simple.SimpleFeatureBuilder;
import org.geotoolkit.geometry.jts.GeometryCoordinateSequenceTransformer;

import org.opengis.feature.IllegalAttributeException;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.OperationNotFoundException;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Geometry;

public class ReprojectingIterator implements Iterator<SimpleFeature> {

    /**
     * decorated iterator
     */
    private final Iterator<SimpleFeature> delegate;
    /**
     * The target coordinate reference system
     */
    private final CoordinateReferenceSystem target;
    /**
     * schema of reprojected features
     */
    private final SimpleFeatureType schema;
    /**
     * Transformer
     */
    private final GeometryCoordinateSequenceTransformer tx;

    public ReprojectingIterator(Iterator<SimpleFeature> delegate, CoordinateReferenceSystem source, CoordinateReferenceSystem target,
            SimpleFeatureType schema, GeometryCoordinateSequenceTransformer transformer)
            throws OperationNotFoundException, FactoryRegistryException, FactoryException {
        this(delegate,
             AuthorityFactoryFinder.getCoordinateOperationFactory(null)
                .createOperation(source, target).getMathTransform(),
             schema,
             transformer);
    }

    public ReprojectingIterator(Iterator<SimpleFeature> delegate, MathTransform transform,
            SimpleFeatureType schema, GeometryCoordinateSequenceTransformer transformer)
            throws OperationNotFoundException, FactoryRegistryException, FactoryException {
        this.delegate = delegate;
        this.target = null;
        this.schema = schema;

        tx = transformer;
        tx.setMathTransform((MathTransform2D) transform);
    }

    public Iterator getDelegate() {
        return delegate;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void remove() {
        delegate.remove();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean hasNext() {
        return delegate.hasNext();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public SimpleFeature next() {
        final SimpleFeature feature = delegate.next();
        try {
            return reproject(feature);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private SimpleFeature reproject(SimpleFeature feature) throws IOException {
        //make a copy, the original one is immutable
        List<Object> attributes = new ArrayList<Object>(feature.getAttributes());

        for (int i=0, n=attributes.size(); i<n; i++) {
            Object object = attributes.get(i);
            if (object instanceof Geometry) {
                // do the transformation
                Geometry geometry = (Geometry) object;
                try {
                    Geometry projectedGeom = tx.transform(geometry);
                    attributes.set(i, projectedGeom);
                } catch (TransformException e) {
                    String msg = "Error occured transforming " + geometry.toString();
                    throw (IOException) new IOException(msg).initCause(e);
                }
            }
        }

        try {
            return SimpleFeatureBuilder.build(schema, attributes, feature.getID());
        } catch (IllegalAttributeException e) {
            String msg = "Error creating reprojeced feature";
            throw (IOException) new IOException(msg).initCause(e);
        }
    }
}
