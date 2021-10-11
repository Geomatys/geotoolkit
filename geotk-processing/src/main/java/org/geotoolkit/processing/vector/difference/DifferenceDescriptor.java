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
package org.geotoolkit.processing.vector.difference;

import org.apache.sis.parameter.ParameterBuilder;
import org.geotoolkit.storage.feature.FeatureCollection;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.processing.vector.VectorDescriptor;

import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Parameters description of Difference process.
 * name of the process : "difference"
 * inputs :
 * <ul>
 *     <li>FEATURE_IN "feature_in" FeatureCollection source </li>
 *     <li>FEATURE_DIFF "feature_diff" FeatureCollection used for compute the difference</li>
 * </ul>
 * outputs :
 * <ul>
 *     <li>FEATURE_OUT "feature_out" the result FeatureCollection </li>
 * </ul>
 * @author Quentin Boileau
 * @module
 */
public final class DifferenceDescriptor extends VectorDescriptor {

    /**Process name : difference */
    public static final String NAME = "vector:difference";

    /**
     * Mandatory - Feature Collection for clipping
     */
    public static final ParameterDescriptor<FeatureCollection> FEATURE_DIFF = new ParameterBuilder()
            .addName("feature_diff")
            .setRemarks("Input FeatureCollection used for compute the difference")
            .setRequired(true)
            .create(FeatureCollection.class, null);

    /** Input Parameters */
    public static final ParameterDescriptorGroup INPUT_DESC =
            new ParameterBuilder().addName("InputParameters").createGroup(FEATURE_IN, FEATURE_DIFF);

    /** Ouput Parameters */
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new ParameterBuilder().addName("OutputParameters").createGroup(FEATURE_OUT);

    /** Instance */
    public static final ProcessDescriptor INSTANCE = new DifferenceDescriptor();

    /**
     * Default constructor
     */
    private DifferenceDescriptor() {
        super(NAME, "Return the result FeatureCollection of Difference process", INPUT_DESC, OUTPUT_DESC);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    public Process createProcess(final ParameterValueGroup input) {
        return new DifferenceProcess(input);
    }
}
