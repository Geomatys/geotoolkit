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
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.geotoolkit.feature.collection.FeatureCollection;
import org.geotoolkit.feature.collection.FeatureIterator;
import org.geotoolkit.feature.FeatureTypeUtilities;
import org.geotoolkit.feature.SchemaException;
import org.geotoolkit.feature.collection.AbstractFeatureCollection;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;
import org.geotoolkit.referencing.CRS;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.OperationNotFoundException;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

/**
 * ReprojectFeatureReader provides a reprojection for FeatureTypes.
 * 
 * <p>
 * ReprojectFeatureResults  is a wrapper used to reproject  GeometryAttributes
 * to a user supplied CoordinateReferenceSystem from the original
 * CoordinateReferenceSystem supplied by the original FeatureResults.
 * </p>
 * 
 * <p>
 * Example Use:
 * <pre><code>
 * ReprojectFeatureResults results =
 *     new ReprojectFeatureResults( originalResults, reprojectCS );
 * 
 * CoordinateReferenceSystem originalCS =
 *     originalResults.getFeatureType().getDefaultGeometry().getCoordinateSystem();
 * 
 * CoordinateReferenceSystem newCS =
 *     results.getFeatureType().getDefaultGeometry().getCoordinateSystem();
 * 
 * assertEquals( reprojectCS, newCS );
 * </code></pre>
 * </p>
 *
 * @author aaime
 * @author $Author: jive $ (last modification)
 * @version $Id$ TODO: handle the case where there is more than one geometry and the other geometries have a different CS than the default geometry
 * @module pending
 */
public class ReprojectFeatureResults extends AbstractFeatureCollection {

    private final FeatureCollection<SimpleFeatureType, SimpleFeature> results;
    private final MathTransform transform;

    /**
     * Creates a new reprojecting feature results
     *
     * @param results
     * @param destinationCS
     *
     * @throws IOException
     * @throws SchemaException
     * @throws TransformException 
     * @throws FactoryException 
     * @throws NoSuchElementException 
     * @throws OperationNotFoundException 
     * @throws CannotCreateTransformException
     * @throws NullPointerException DOCUMENT ME!
     * @throws IllegalArgumentException
     */
    public ReprojectFeatureResults(final FeatureCollection<SimpleFeatureType, SimpleFeature> results,
            final CoordinateReferenceSystem destinationCS)
            throws IOException, SchemaException, TransformException, OperationNotFoundException, NoSuchElementException, FactoryException {

        super(forceType(origionalType(results), destinationCS), null);
        this.results = origionalCollection(results);

        final CoordinateReferenceSystem originalCs;
        if (results instanceof ForceCoordinateSystemFeatureResults) {
            originalCs = results.getSchema().getGeometryDescriptor().getCoordinateReferenceSystem();
        } else {
            originalCs = this.results.getSchema().getGeometryDescriptor().getCoordinateReferenceSystem();
        }
        this.transform = CRS.findMathTransform(originalCs, destinationCS, true);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Iterator openIterator() {
        return new ReprojectFeatureIterator(results.features(), getSchema(), transform);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void closeIterator(final Iterator close) {
        if (close == null) {
            return;
        }
        if (close instanceof ReprojectFeatureIterator) {
            ReprojectFeatureIterator iterator = (ReprojectFeatureIterator) close;
            iterator.close();
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int size() {
        return results.size();
    }

    private static FeatureCollection<SimpleFeatureType, SimpleFeature> origionalCollection(
            FeatureCollection<SimpleFeatureType, SimpleFeature> results) {
        if (results instanceof ReprojectFeatureResults) {
            results = ((ReprojectFeatureResults) results).getOrigin();
        }
        if (results instanceof ForceCoordinateSystemFeatureResults) {
            results = ((ForceCoordinateSystemFeatureResults) results).getOrigin();
        }
        return results;
    }

    private static SimpleFeatureType origionalType(FeatureCollection<SimpleFeatureType, SimpleFeature> results) {
        if (results instanceof ReprojectFeatureResults) {
            results = ((ReprojectFeatureResults) results).getOrigin();
        }
        if (results instanceof ForceCoordinateSystemFeatureResults) {
            results = ((ForceCoordinateSystemFeatureResults) results).getOrigin();
        }
        return results.getSchema();
    }

    private static SimpleFeatureType forceType(final SimpleFeatureType startingType,
            final CoordinateReferenceSystem forcedCS) throws SchemaException {
        if (forcedCS == null) {
            throw new NullPointerException("CoordinateSystem required");
        }
        final CoordinateReferenceSystem originalCs = startingType.getGeometryDescriptor().getCoordinateReferenceSystem();

        if (forcedCS.equals(originalCs)) {
            return startingType;
        } else {
            return FeatureTypeUtilities.transform(startingType, forcedCS);
        }
    }

    /**
     * This method computes reprojected bounds the hard way, but computing them
     * feature by feature. This method could be faster if computed the
     * reprojected bounds by reprojecting the original feature bounds a Shape
     * object, thus getting the true shape of the reprojected envelope, and
     * then computing the minumum and maximum coordinates of that new shape.
     * The result would not a true representation of the new bounds, but it
     * would be guaranteed to be larger that the true representation.
     */
    @Override
    public JTSEnvelope2D getBounds() {
        final FeatureIterator<SimpleFeature> r = features();
        try {
            final Envelope newBBox = new Envelope();

            while (r.hasNext()) {
                SimpleFeature feature = r.next();
                final Geometry geometry = ((Geometry) feature.getDefaultGeometry());
                if (geometry != null) {
                    Envelope internal = geometry.getEnvelopeInternal();
                    newBBox.expandToInclude(internal);
                }
            }
            return JTSEnvelope2D.reference(newBBox);
        } catch (Exception e) {
            throw new RuntimeException("Exception occurred while computing reprojected bounds",
                    e);
        } finally {
            r.close();
        }
    }

    /**
     * Returns the feature results wrapped by this reprojecting feature results
     */
    public FeatureCollection<SimpleFeatureType, SimpleFeature> getOrigin() {
        return results;
    }
}
