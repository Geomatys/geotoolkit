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

import org.geotoolkit.feature.Feature;
import org.geotoolkit.feature.Property;
import org.geotoolkit.feature.type.GeometryDescriptor;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import static org.geotoolkit.parameter.Parameters.*;

/**
 * Compute the convex hull from a FeatureCollection. An optional parameter
 * geometry_name set the GeometryAttribute name used to compute the convex hull.
 * By default the process use the default GeometryAttribute in Features.
 * @author Quentin Boileau
 * @module pending
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
        final FeatureCollection inputFeatureList   = value(ConvexHullDescriptor.FEATURE_IN, inputParameters);
        final String geometryName                           = value(ConvexHullDescriptor.GEOMETRY_NAME, inputParameters);

        final Geometry hull = computeConvexHull(inputFeatureList, geometryName);

        getOrCreate(ConvexHullDescriptor.GEOMETRY_OUT, outputParameters).setValue(hull);
    }

    /**
     * Compute the convex hull from a feature collection on a geometry attribute name
     * @param inputFeatureList
     * @param geometryName
     * @return the convex hull geometry
     */
    private Geometry computeConvexHull(final FeatureCollection inputFeatureList, String geometryName) {


        Geometry convexHull = new GeometryFactory().buildGeometry(Collections.EMPTY_LIST);
        CoordinateReferenceSystem crs = null;
        final FeatureIterator iter = inputFeatureList.iterator();
        try {
            while (iter.hasNext()) {
                final Feature feature = iter.next();

                //in the first pass, if the geometry attribute name is null, we use the default geometry attribute name
                if (geometryName == null) {
                    geometryName = feature.getDefaultGeometryProperty().getName().getLocalPart();
                }
                for (Property property : feature.getProperties()) {
                    if (property.getDescriptor() instanceof GeometryDescriptor) {
                        final GeometryDescriptor desc = (GeometryDescriptor) property.getDescriptor();
                        if (desc.getName().getLocalPart().equals(geometryName)) {
                            crs = desc.getCoordinateReferenceSystem();

                            final Geometry tmpGeom = (Geometry) property.getValue();
                            convexHull = convexHull.union(tmpGeom);
                            convexHull = convexHull.convexHull();
                        }
                    }
                }
            }
        } finally {
            iter.close();
        }
        convexHull.setSRID(SRIDGenerator.toSRID(crs, SRIDGenerator.Version.V1));
        return convexHull;
    }
}
