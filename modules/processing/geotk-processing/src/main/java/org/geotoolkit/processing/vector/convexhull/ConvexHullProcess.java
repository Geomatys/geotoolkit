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

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import java.util.Collections;

import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.geometry.jts.SRIDGenerator;
import org.geotoolkit.processing.AbstractProcess;

import org.opengis.feature.Feature;
import org.opengis.feature.PropertyType;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.geotoolkit.feature.FeatureExt;
import org.apache.sis.internal.feature.AttributeConvention;

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
    protected void execute() {
        final FeatureCollection inputFeatureList = inputParameters.getValue(ConvexHullDescriptor.FEATURE_IN);
        final String geometryName                = inputParameters.getValue(ConvexHullDescriptor.GEOMETRY_NAME);
        final Geometry hull = computeConvexHull(inputFeatureList, geometryName);
        outputParameters.getOrCreate(ConvexHullDescriptor.GEOMETRY_OUT).setValue(hull);
    }

    /**
     * Compute the convex hull from a feature collection on a geometry attribute name
     *
     * @return the convex hull geometry
     */
    private Geometry computeConvexHull(final FeatureCollection inputFeatureList, String geometryName) {
        Geometry convexHull = new GeometryFactory().buildGeometry(Collections.EMPTY_LIST);
        CoordinateReferenceSystem crs = null;
        try (final FeatureIterator iter = inputFeatureList.iterator()) {
            while (iter.hasNext()) {
                final Feature feature = iter.next();

                //in the first pass, if the geometry attribute name is null, we use the default geometry attribute name
                if (geometryName == null) {
                    geometryName = AttributeConvention.GEOMETRY_PROPERTY.toString();
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
        convexHull.setSRID(SRIDGenerator.toSRID(crs, SRIDGenerator.Version.V1));
        return convexHull;
    }
}
