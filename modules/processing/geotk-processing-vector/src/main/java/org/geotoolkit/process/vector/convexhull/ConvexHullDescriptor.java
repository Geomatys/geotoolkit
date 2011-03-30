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
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.process.AbstractProcessDescriptor;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.vector.VectorProcessFactory;
import org.geotoolkit.util.SimpleInternationalString;

import org.opengis.feature.Feature;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;

/**
 * Parameters description of ConvexHull process.
 * name of the process : "convexhull"
 * inputs :
 * <ul>
 *     <li>FEATURE_IN "feature_in" FeatureCollection source</li>
 *     <li>GEOMETRY_NAME "geometry_name"  GeometryAttribute name used to compute convex hull</li>
 * </ul>
 * outputs :
 * <ul>
 *     <li>GEOMETRY_OUT "geometry_out" convex hull Geometry</li>
 * </ul>
 * @author Quentin Boileau
 * @module pending
 */
public final class ConvexHullDescriptor extends AbstractProcessDescriptor {

    /**Process name : convexhull */
    public static final String NAME = "convexhull";
    /**
     * Mandatory - Feature Collection
     */
    public static final ParameterDescriptor<FeatureCollection<Feature>> FEATURE_IN =
            new DefaultParameterDescriptor("feature_in", "Inpute Feature", FeatureCollection.class, null, true);
    /**
     * Optional - GeometryAttribute name used to compute convex hull
     */
    public static final ParameterDescriptor<String> GEOMETRY_NAME =
            new DefaultParameterDescriptor("geometry_name", "Geometry used", String.class, null, false);
    
    public static final ParameterDescriptor<Geometry> GEOMETRY_OUT =
            new DefaultParameterDescriptor("geometry_out", "Convex Hull geometry", Geometry.class, null, false);
    /** Input Parameters */
    public static final ParameterDescriptorGroup INPUT_DESC =
            new DefaultParameterDescriptorGroup("InputParameters",
            new GeneralParameterDescriptor[]{FEATURE_IN, GEOMETRY_NAME});
    /** Ouput Parameters */
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new DefaultParameterDescriptorGroup("OutputParameters",
            new GeneralParameterDescriptor[]{GEOMETRY_OUT});
    /** Instance */
    public static final ProcessDescriptor INSTANCE = new ConvexHullDescriptor();

    /**
     * Default constructor
     */
    protected ConvexHullDescriptor() {

        super(NAME, VectorProcessFactory.IDENTIFICATION,
                new SimpleInternationalString("Return the convex hull based on FeatureCollection geometries"),
                INPUT_DESC, OUTPUT_DESC);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    public Process createProcess() {
        return new ConvexHull();
    }
}
