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

import org.geotoolkit.data.collection.AbstractFeatureCollection;
import org.geotoolkit.data.collection.FeatureCollection;
import org.geotoolkit.feature.FeatureTypeUtilities;
import org.geotoolkit.feature.SchemaException;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * ForceCoordinateSystemFeatureResults provides a CoordinateReferenceSystem for
 * FeatureTypes.
 *
 * <p>
 * ForceCoordinateSystemFeatureReader is a wrapper used to force
 * GeometryAttributes to a user supplied CoordinateReferenceSystem rather then
 * the default supplied by the DataStore.
 * </p>
 *
 * <p>
 * Example Use:
 * <pre><code>
 * ForceCoordinateSystemFeatureResults results =
 *     new ForceCoordinateSystemFeatureResults( originalResults, forceCS );
 *
 * CoordinateReferenceSystem originalCS =
 *     originalResults.getFeatureType().getDefaultGeometry().getCoordinateSystem();
 *
 * CoordinateReferenceSystem newCS =
 *     reader.getFeatureType().getDefaultGeometry().getCoordinateSystem();
 *
 * assertEquals( forceCS, newCS );
 * </code></pre>
 * </p>
 *
 * @author aaime
 * @version $Id$
 * @module pending
 */
public class ForceCoordinateSystemFeatureResults extends AbstractFeatureCollection {

    private final FeatureCollection<SimpleFeatureType, SimpleFeature> results;

    public ForceCoordinateSystemFeatureResults(final FeatureCollection<SimpleFeatureType, SimpleFeature> results,
            final CoordinateReferenceSystem forcedCS) throws IOException, SchemaException {
        this(results, forcedCS, false);
    }

    public ForceCoordinateSystemFeatureResults(final FeatureCollection<SimpleFeatureType, SimpleFeature> results,
            final CoordinateReferenceSystem forcedCS, final boolean forceOnlyMissing) throws IOException, SchemaException {
        super(forceType(origionalType(results), forcedCS, forceOnlyMissing), null);
        this.results = results;
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

    /**
     * {@inheritDoc }
     */
    @Override
    public Iterator openIterator() {
        return new ForceCoordinateSystemIterator(results.features(), getSchema());
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void closeIterator(final Iterator close) {
        if (close == null) {
            return;
        }
        if (close instanceof ForceCoordinateSystemIterator) {
            ForceCoordinateSystemIterator iterator = (ForceCoordinateSystemIterator) close;
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

    private static SimpleFeatureType forceType(final SimpleFeatureType startingType,
            final CoordinateReferenceSystem forcedCS, final boolean forceOnlyMissing) throws SchemaException {
        if (forcedCS == null) {
            throw new NullPointerException("CoordinateSystem required");
        }
        final CoordinateReferenceSystem originalCs = (startingType.getGeometryDescriptor() != null) ? startingType.getGeometryDescriptor().getCoordinateReferenceSystem() : null;

        if (forcedCS.equals(originalCs)) {
            return startingType;
        }
        return FeatureTypeUtilities.transform(startingType, forcedCS, forceOnlyMissing);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public JTSEnvelope2D getBounds() {
        JTSEnvelope2D env = results.getBounds();
        return new JTSEnvelope2D(env, getSchema().getCoordinateReferenceSystem());
    }

    /**
     * Returns the feature results wrapped by this
     * ForceCoordinateSystemFeatureResults
     */
    public FeatureCollection<SimpleFeatureType, SimpleFeature> getOrigin() {
        return results;
    }
}
