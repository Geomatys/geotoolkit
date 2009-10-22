/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 * 
 *    (C) 2003-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.data.crs;

import java.io.IOException;
import java.util.NoSuchElementException;

import org.geotoolkit.data.DataSourceException;
import org.geotoolkit.data.DelegatingFeatureReader;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.feature.FeatureTypeUtilities;
import org.geotoolkit.feature.SchemaException;
import org.geotoolkit.feature.simple.SimpleFeatureBuilder;
import org.geotoolkit.geometry.jts.GeometryCoordinateSequenceTransformer;
import org.geotoolkit.referencing.CRS;

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

/**
 * ReprojectFeatureReader provides a reprojection for FeatureTypes.
 * 
 * <p>
 * ReprojectFeatureReader  is a wrapper used to reproject  GeometryAttributes
 * to a user supplied CoordinateReferenceSystem from the original
 * CoordinateReferenceSystem supplied by the original FeatureReader.
 * </p>
 * 
 * <p>
 * Example Use:
 * <pre><code>
 * ReprojectFeatureReader reader =
 *     new ReprojectFeatureReader( originalReader, reprojectCS );
 * 
 * CoordinateReferenceSystem originalCS =
 *     originalReader.getFeatureType().getDefaultGeometry().getCoordinateSystem();
 * 
 * CoordinateReferenceSystem newCS =
 *     reader.getFeatureType().getDefaultGeometry().getCoordinateSystem();
 * 
 * assertEquals( reprojectCS, newCS );
 * </code></pre>
 * </p>
 * TODO: handle the case where there is more than one geometry and the other
 * geometries have a different CS than the default geometry
 *
 * @author jgarnett, Refractions Research, Inc.
 * @author aaime
 * @author $Author: jive $ (last modification)
 * @version $Id$
 * @module pending
 */
public class ReprojectFeatureReader implements DelegatingFeatureReader<SimpleFeatureType, SimpleFeature> {

    private final FeatureReader<SimpleFeatureType, SimpleFeature> reader;
    private final SimpleFeatureType schema;
    private final GeometryCoordinateSequenceTransformer transformer = new GeometryCoordinateSequenceTransformer();

    public ReprojectFeatureReader(final FeatureReader<SimpleFeatureType, SimpleFeature> reader,
            final SimpleFeatureType schema, final MathTransform transform) {
        if (schema == null) {
            throw new NullPointerException("Feature type can not be null");
        }
        if (reader == null) {
            throw new NullPointerException("Feature reader can not be null");
        }

        this.reader = reader;
        this.schema = schema;
        transformer.setMathTransform((MathTransform2D) transform);
    }

    public ReprojectFeatureReader(final FeatureReader<SimpleFeatureType, SimpleFeature> reader,
            final CoordinateReferenceSystem cs)
            throws SchemaException, OperationNotFoundException, NoSuchElementException, FactoryException {
        if (cs == null) {
            throw new NullPointerException("CoordinateSystem required");
        }

        final SimpleFeatureType type = reader.getFeatureType();
        final CoordinateReferenceSystem original = type.getGeometryDescriptor().getCoordinateReferenceSystem();

        if (cs.equals(original)) {
            throw new IllegalArgumentException("CoordinateSystem " + cs + " already used (check before using wrapper)");
        }

        this.schema = FeatureTypeUtilities.transform(type, cs);
        this.reader = reader;
        transformer.setMathTransform(CRS.findMathTransform(original, cs, true));
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureReader<SimpleFeatureType, SimpleFeature> getDelegate() {
        return reader;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public SimpleFeatureType getFeatureType() {
        return schema;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public SimpleFeature next() throws IOException, IllegalAttributeException, NoSuchElementException {

        SimpleFeature next = reader.next();
        Object[] attributes = next.getAttributes().toArray();

        try {
            for (int i = 0; i < attributes.length; i++) {
                if (attributes[i] instanceof Geometry) {
                    attributes[i] = transformer.transform((Geometry) attributes[i]);
                }
            }
        } catch (TransformException e) {
            throw new DataSourceException("A transformation exception occurred while reprojecting data on the fly", e);
        }

        return SimpleFeatureBuilder.build(schema, attributes, next.getID());
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean hasNext() throws IOException {
        return reader.hasNext();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void close() throws IOException {
        reader.close();
    }
}
