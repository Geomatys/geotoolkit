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
package org.geotoolkit.process.vector.convexhull;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import java.util.Collections;

import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.process.AbstractProcess;
import org.geotoolkit.process.ProcessEvent;

import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Compute the convex hull from a FeatureCollection. An optional parameter
 * geometry_name set the GeometryAttribute name used to compute the convex hull.
 * By default the process use the default GeometryAttribute in Features.
 * @author Quentin Boileau
 * @module pending
 */
public class ConvexHull extends AbstractProcess {

    ParameterValueGroup result;

    /**
     * Default constructor
     */
    public ConvexHull() {
        super(ConvexHullDescriptor.INSTANCE);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ParameterValueGroup getOutput() {
        return result;
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    public void run() {
        getMonitor().started(new ProcessEvent(this, 0, null, null));
        final FeatureCollection<Feature> inputFeatureList = Parameters.value(ConvexHullDescriptor.FEATURE_IN, inputParameters);
        final String geometryName = Parameters.value(ConvexHullDescriptor.GEOMETRY_NAME, inputParameters);

        final Geometry hull = computeConvexHull(inputFeatureList, geometryName);

        result = super.getOutput();
        result.parameter(ConvexHullDescriptor.GEOMETRY_OUT.getName().getCode()).setValue(hull);
        getMonitor().ended(new ProcessEvent(this, 100, null, null));
    }

    /**
     * Compute the convex hull from a feature collection on a geometry attribute name
     * @param inputFeatureList
     * @param geometryName
     * @return the convex hull geometry
     */
    private Geometry computeConvexHull(final FeatureCollection<Feature> inputFeatureList, String geometryName) {


        Geometry convexHull = new GeometryFactory().buildGeometry(Collections.EMPTY_LIST);

        final FeatureIterator<Feature> iter = inputFeatureList.iterator();
        try {
            while (iter.hasNext()) {
                final Feature feature = iter.next();

                //in the first pass, if the geometry attribute name is null, we use the default geometry attribute name
                if (geometryName == null) {
                    geometryName = feature.getDefaultGeometryProperty().getName().getLocalPart();
                }
                for (Property property : feature.getProperties()) {
                    if (property.getDescriptor() instanceof GeometryDescriptor) {
                        if (property.getName().getLocalPart().equals(geometryName)) {
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
        return convexHull;
    }
}
