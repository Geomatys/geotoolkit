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
package org.geotoolkit.processing.vector.intersection;

import org.apache.sis.parameter.ParameterBuilder;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.processing.vector.VectorDescriptor;

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
 * @module
 */
public final class IntersectionDescriptor extends VectorDescriptor {

    /**Process name : intersection */
    public static final String NAME = "vector:intersection";

    /**
     * Mandatory - Feature Collection for clipping
     */
    public static final ParameterDescriptor<FeatureCollection> FEATURE_INTER = new ParameterBuilder()
            .addName("feature_inter")
            .setRemarks("Inpute FeatureCollection for the intersection")
            .setRequired(true)
            .create(FeatureCollection.class, null);

    /**
     * Optional - Geometry property name. Refer to the geometry to use for the intersection process
     */
    public static final ParameterDescriptor<String> GEOMETRY_NAME = new ParameterBuilder()
            .addName("geometry_name")
            .setRemarks("Geometry property name")
            .setRequired(false)
            .create(String.class, null);

    /** Input Parameters */
    public static final ParameterDescriptorGroup INPUT_DESC =
            new ParameterBuilder().addName("InputParameters").createGroup(FEATURE_IN, FEATURE_INTER,GEOMETRY_NAME);

    /** Ouput Parameters */
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new ParameterBuilder().addName("OutputParameters").createGroup(FEATURE_OUT);
    
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
        return new IntersectionProcess(input);
    }
}
