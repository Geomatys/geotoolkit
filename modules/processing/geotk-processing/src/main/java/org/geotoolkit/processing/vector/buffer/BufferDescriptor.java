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
package org.geotoolkit.processing.vector.buffer;

import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.measure.Units;

import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.processing.vector.VectorDescriptor;

import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Parameters description for Buffer process.
 * Make a geometry buffer around Features geometry
 * Name of the process : "buffer"
 * Inputs :
 * <ul>
 *     <li>FEATURE_IN "feature_in" FeatureCollection to clip</li>
 *     <li>DISTANCE_IN "distance_in" buffer distance in meters</li>
 *     <li>LENIENT_TRANSFORM_IN "lenient_transform_in" CRS transformation accuracy</li>
 * </ul>
 * Outputs :
 * <ul>
 *     <li>FEATURE_OUT "feature_out" FeatureCollection buffered</li>
 * </ul>
 * @author Quentin Boileau
 * @module
 */
public final class BufferDescriptor extends VectorDescriptor {

    /**Process name : buffer */
    public static final String NAME = "buffer";

    /**
     * Mandatory - Buffer distance in meters
     */
    public static final ParameterDescriptor<Double> DISTANCE_IN = new ParameterBuilder()
            .addName("distance_in")
            .setRemarks("Input buffer distance in meters.")
            .setRequired(true)
            .create(1.0,Units.METRE);


    public static final ParameterDescriptor<Boolean> LENIENT_TRANSFORM_IN = new ParameterBuilder()
            .addName("lenient_transform_in")
            .setRemarks("Input boolean to set accuracy CRS transformation")
            .setRequired(false)
            .create(Boolean.class, true);

    /**Input parameters */
    public static final ParameterDescriptorGroup INPUT_DESC =
            new ParameterBuilder().addName("InputParameters").createGroup(FEATURE_IN, DISTANCE_IN, LENIENT_TRANSFORM_IN);

    /**Output parameters */
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new ParameterBuilder().addName("OutputParameters").createGroup(FEATURE_OUT);

    /**Instance */
    public static final ProcessDescriptor INSTANCE = new BufferDescriptor();

    /**
     * Default constructor
     */
    private BufferDescriptor() {
        super(NAME, "Apply buffer function to a FeatureCollection geometry", INPUT_DESC, OUTPUT_DESC);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    public Process createProcess(final ParameterValueGroup input) {
        return new BufferProcess(input);
    }
}
