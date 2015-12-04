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
package org.geotoolkit.processing.vector.startoffset;

import org.apache.sis.parameter.ParameterBuilder;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.processing.vector.VectorDescriptor;

import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Parameter description of StartOffset process.
 * name of the process : "startoffset"
 * inputs :
 * <ul>
 *     <li>FEATURE_IN "feature_in" FeatureCollection </li>
 *     <li>OFFSET_IN "offset_in" Start offset iteration on the FeatureCollection</li>
 * </ul>
 * outputs :
 * <ul>
 *     <li>FEATURE_OUT "feature_out" FeatureCollection</li>
 * </ul>
 * @author Quentin Boileau
 * @module pending
 */
public final class StartOffsetDescriptor extends VectorDescriptor {

    /**Process name : startoffset */
    public static final String NAME = "startoffset";
    /**
     * Mandatory - Start offset iteration on the FeatureCollection
     */
    public static final ParameterDescriptor<Integer> OFFSET_IN = new ParameterBuilder()
            .addName("offset_in")
            .setRemarks("Start offset iteration on the FeatureCollection")
            .setRequired(true)
            .create(Integer.class, null);

    /** Input Parameters */
    public static final ParameterDescriptorGroup INPUT_DESC =
            new ParameterBuilder().addName("InputParameters").createGroup(FEATURE_IN, OFFSET_IN);

    /** Ouput Parameters */
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new ParameterBuilder().addName("OutputParameters").createGroup(FEATURE_OUT);
    
    /** Instance */
    public static final ProcessDescriptor INSTANCE = new StartOffsetDescriptor();

    /**
     * Default constructor
     */
    private StartOffsetDescriptor() {
        super(NAME, "Start FeatureCollection iteration at given offset", INPUT_DESC, OUTPUT_DESC);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    public Process createProcess(final ParameterValueGroup input) {
        return new StartOffsetProcess(input);
    }
}
