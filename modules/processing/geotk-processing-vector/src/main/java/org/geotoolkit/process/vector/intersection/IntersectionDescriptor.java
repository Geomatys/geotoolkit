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
package org.geotoolkit.process.vector.intersection;

import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.vector.VectorDescriptor;

import org.opengis.feature.Feature;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Parameters description of Intersection process.
 * name of the process : "intersection"
 * inputs :
 * <ul>
 *     <li>FEATURE_IN "feature_in" FeatureCollection source</li>
 *     <li>FEATURE_INTER "feature_inter" FeatureCollection for intersection</li>
 *     <li>GEOMETRY_NAME "regroup_attribute" Geometry property name. Optional</li>
 * </ul>
 * outputs :
 * <ul>
 *     <li>FEATURE_OUT "feature_out" resulting FeatureCollection</li>
 * </ul>
 * @author Quentin Boileau
 * @module pending
 */
public final class IntersectionDescriptor extends VectorDescriptor {

    /**Process name : intersection */
    public static final String NAME = "intersection";

    /**
     * Mandatory - Feature Collection for clipping
     */
    public static final ParameterDescriptor<FeatureCollection<Feature>> FEATURE_INTER =
            new DefaultParameterDescriptor("feature_inter", "Inpute FeatureCollection for the intersection", FeatureCollection.class, null, true);

    /**
     * Optional - Geometry property name. Refer to the geometry to use for the intersection process
     */
    public static final ParameterDescriptor<String> GEOMETRY_NAME =
            new DefaultParameterDescriptor("geometry_name", "Geometry property name", String.class, null, false);

    /** Input Parameters */
    public static final ParameterDescriptorGroup INPUT_DESC =
            new DefaultParameterDescriptorGroup("InputParameters",
            new GeneralParameterDescriptor[]{FEATURE_IN, FEATURE_INTER,GEOMETRY_NAME});

    /** Ouput Parameters */
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new DefaultParameterDescriptorGroup("OutputParameters",
            new GeneralParameterDescriptor[]{FEATURE_OUT});
    
    /** Instance */
    public static final ProcessDescriptor INSTANCE = new IntersectionDescriptor();

    /**
     * Default constructor
     */
    private IntersectionDescriptor() {
        super(NAME, "Return a new FeatureCollection where each Feature is "
                + "create from an intersection Geometry", INPUT_DESC, OUTPUT_DESC);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    public Process createProcess(final ParameterValueGroup input) {
        return new Intersection(input);
    }
}
