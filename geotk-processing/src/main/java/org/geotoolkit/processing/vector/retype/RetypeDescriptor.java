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
package org.geotoolkit.processing.vector.retype;

import org.apache.sis.parameter.ParameterBuilder;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.processing.vector.VectorDescriptor;

import org.opengis.feature.FeatureType;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Parameter description of Retype process.
 * name of the process : "maxlimit"
 * inputs :
 * <ul>
 *     <li>FEATURE_IN "feature_in" FeatureCollection </li>
 *     <li>MASK_IN "mask_in" FeatureType used to as a mask to retype the FeatureCollection</li>
 * </ul>
 * outputs :
 * <ul>
 *     <li>FEATURE_OUT "feature_out" FeatureCollection</li>
 * </ul>
 * @author Quentin Boileau
 * @module
 */
public final class RetypeDescriptor extends VectorDescriptor {

    /**Process name : retype */
    public static final String NAME = "vector:retype";

    /**
     * Mandatory - FeatureType used to as a mask to retype the FeatureCollection
     */
    public static final ParameterDescriptor<FeatureType> MASK_IN = new ParameterBuilder()
            .addName("mask_in")
            .setRemarks("FeatureType used to as a mask to retype the FeatureCollection")
            .setRequired(true)
            .create(FeatureType.class, null);

    /** Input Parameters */
    public static final ParameterDescriptorGroup INPUT_DESC =
            new ParameterBuilder().addName("InputParameters").createGroup(FEATURE_IN, MASK_IN);

    /** Ouput Parameters */
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new ParameterBuilder().addName("OutputParameters").createGroup(FEATURE_OUT);

    /** Instance */
    public static final ProcessDescriptor INSTANCE = new RetypeDescriptor();

    /**
     * Default constructor
     */
    private RetypeDescriptor() {
        super(NAME, "Limit a FeatureCollection returns to a maximum", INPUT_DESC, OUTPUT_DESC);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    public Process createProcess(final ParameterValueGroup input) {
        return new RetypeProcess(input);
    }
}
