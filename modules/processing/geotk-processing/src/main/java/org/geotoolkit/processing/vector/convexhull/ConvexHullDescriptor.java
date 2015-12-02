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
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.processing.AbstractProcessDescriptor;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.processing.vector.VectorProcessingRegistry;

import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

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
    public static final ParameterDescriptor<FeatureCollection> FEATURE_IN = new ParameterBuilder()
            .addName("feature_in")
            .setRemarks("Inpute Feature")
            .setRequired(true)
            .create(FeatureCollection.class, null);
    /**
     * Optional - GeometryAttribute name used to compute convex hull
     */
    public static final ParameterDescriptor<String> GEOMETRY_NAME = new ParameterBuilder()
            .addName("geometry_name")
            .setRemarks("Geometry used")
            .setRequired(false)
            .create(String.class, null);
    
    public static final ParameterDescriptor<Geometry> GEOMETRY_OUT = new ParameterBuilder()
            .addName("geometry_out")
            .setRemarks("Convex Hull geometry")
            .setRequired(false)
            .create(Geometry.class, null);
    /** Input Parameters */
    public static final ParameterDescriptorGroup INPUT_DESC =
            new ParameterBuilder().addName("InputParameters").createGroup(FEATURE_IN, GEOMETRY_NAME);
    /** Ouput Parameters */
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new ParameterBuilder().addName("OutputParameters").createGroup(GEOMETRY_OUT);
    /** Instance */
    public static final ProcessDescriptor INSTANCE = new ConvexHullDescriptor();

    /**
     * Default constructor
     */
    protected ConvexHullDescriptor() {

        super(NAME, VectorProcessingRegistry.IDENTIFICATION,
                new SimpleInternationalString("Return the convex hull based on FeatureCollection geometries"),
                INPUT_DESC, OUTPUT_DESC);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    public Process createProcess(final ParameterValueGroup input) {
        return new ConvexHullProcess(input);
    }
}
