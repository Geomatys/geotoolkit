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
package org.geotoolkit.processing.vector.spatialjoin;

import org.apache.sis.parameter.ParameterBuilder;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.processing.vector.VectorDescriptor;

import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Parameters description for SpatialJoin process.
 * name of the process : "spatialjoin"
 * inputs :
 * <ul>
 *     <li>FEATURE_IN "feature_in" Source FeatureCollection</li>
 *     <li>FEATURE_TARGET "feature_target" Target FeatureCollection </li>
 *     <li>INTERSECT "intersect" Method used. true => Intersection, false => Nearest</li>
 * </ul>
 * outputs :
 * <ul>
 *     <li>FEATURE_OUT "feature_out" FeatureCollection intersected</li>
 * </ul>
 * @author Quentin Boileau
 * @module
 */
public final class SpatialJoinDescriptor extends VectorDescriptor {

    /**Process name : spatialjoin */
    public static final String NAME = "vector:spatialjoin";

    /**
     * Mandatory - Target FeatureCollection
     */
    public static final ParameterDescriptor<FeatureCollection> FEATURE_TARGET = new ParameterBuilder()
            .addName("feature_target")
            .setRemarks("Target Features")
            .setRequired(true)
            .create(FeatureCollection.class, null);

     /**
     * Optional - Method used. true => Intersection, false => Nearest
     */
    public static final ParameterDescriptor<Boolean> INTERSECT = new ParameterBuilder()
            .addName("intersect")
            .setRemarks("Method used, intersect or nearest")
            .setRequired(false)
            .create(Boolean.class, true);

    /** Input Parameters */
    public static final ParameterDescriptorGroup INPUT_DESC =
            new ParameterBuilder().addName("InputParameters").createGroup(FEATURE_IN, FEATURE_TARGET,INTERSECT);

    /** Ouput Parameters */
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new ParameterBuilder().addName("OutputParameters").createGroup(FEATURE_OUT);

    /** Instance */
    public static final ProcessDescriptor INSTANCE = new SpatialJoinDescriptor();

    /**
     * Default constructor
     */
    private SpatialJoinDescriptor() {
        super(NAME, "Return the target FeatureCollection with source FeatureCollection attributes."
                + "The link between target and source depend of method used (Intersect or Nearest)", INPUT_DESC, OUTPUT_DESC);
    }
    /**
     *  {@inheritDoc }
     */
    @Override
    public Process createProcess(final ParameterValueGroup input) {
        return new SpatialJoinProcess(input);
    }
}
