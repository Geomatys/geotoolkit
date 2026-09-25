/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.processing.vector.convexhull;

import java.util.Collections;
import java.util.Iterator;
import java.util.stream.Stream;
import org.apache.sis.feature.internal.shared.AttributeConvention;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.geotoolkit.feature.FeatureExt;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.AbstractProcess;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.Feature;
import org.opengis.feature.PropertyType;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Compute the convex hull from a FeatureCollection. An optional parameter
 * geometry_name set the GeometryAttribute name used to compute the convex hull.
 * By default the process use the default GeometryAttribute in Features.
 *
 * @author Quentin Boileau
 */
public class ConvexHullProcess extends AbstractProcess {
    /**
     * Default constructor
     */
    public ConvexHullProcess(final ParameterValueGroup input) {
        super(ConvexHullDescriptor.INSTANCE,input);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    protected void execute() throws ProcessException {
        final FeatureSet inputFeatureList = inputParameters.getValue(ConvexHullDescriptor.FEATURE_IN);
        final String geometryName         = inputParameters.getValue(ConvexHullDescriptor.GEOMETRY_NAME);
        final Geometry hull;
        try {
            hull = computeConvexHull(inputFeatureList, geometryName);
        } catch (DataStoreException ex) {
            throw new ProcessException(ex.getMessage(), this, ex);
        }
        outputParameters.getOrCreate(ConvexHullDescriptor.GEOMETRY_OUT).setValue(hull);
    }

    /**
     * Compute the convex hull from a feature collection on a geometry attribute name
     *
     * @return the convex hull geometry
     */
    private Geometry computeConvexHull(final FeatureSet inputFeatureList, String geometryName) throws DataStoreException {
        Geometry convexHull = JTS.getFactory().buildGeometry(Collections.EMPTY_LIST);
        CoordinateReferenceSystem crs = null;
        try (final Stream<Feature> stream = inputFeatureList.features(false)) {
            final Iterator<Feature> iter = stream.iterator();
            while (iter.hasNext()) {
                final Feature feature = iter.next();

                //in the first pass, if the geometry attribute name is null, we use the default geometry attribute name
                if (geometryName == null) {
                    geometryName = AttributeConvention.GEOMETRY;
                }
                for (PropertyType property : feature.getType().getProperties(true)) {
                    if (AttributeConvention.isGeometryAttribute(property)) {
                        final String name = property.getName().toString();
                        if (name.equals(geometryName)) {
                            crs = FeatureExt.getCRS(property);
                            final Geometry tmpGeom = (Geometry) feature.getPropertyValue(name);
                            convexHull = convexHull.union(tmpGeom);
                            convexHull = convexHull.convexHull();
                        }
                    }
                }
            }
        }

        if (crs != null) {
            JTS.setCRS(convexHull, crs);
        } else {
            fireWarningOccurred("Referencing output geometry", Float.NaN, new IllegalStateException("No coordinate reference system available. Output geometry will not have any CRS attached"));
        }
        return convexHull;
    }
}
