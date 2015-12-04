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
package org.geotoolkit.processing.math.divide;

import org.apache.sis.parameter.ParameterBuilder;
import org.geotoolkit.processing.AbstractProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.processing.math.MathProcessingRegistry;
import org.apache.sis.util.iso.SimpleInternationalString;

import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * @author Quentin Boileau (Geomatys)
 * @module pending
 */
public class DivideDescriptor extends AbstractProcessDescriptor{
        
    /**Process name : divide */
    public static final String NAME = "divide";
    
    /**
     * Input parameters
     */
    public static final ParameterDescriptor<Double> FIRST_NUMBER = new ParameterBuilder()
            .addName("first")
            .setRemarks("first number")
            .setRequired(true)
            .create(Double.class, null);
    public static final ParameterDescriptor<Double> SECOND_NUMBER = new ParameterBuilder()
            .addName("second")
            .setRemarks("second number")
            .setRequired(true)
            .create(Double.class, null);
    public static final ParameterDescriptorGroup INPUT_DESC =
            new ParameterBuilder().addName("InputParameters").createGroup(FIRST_NUMBER,SECOND_NUMBER);
    
    /**
     * OutputParameters
     */
    public static final ParameterDescriptor<Double> RESULT_NUMBER = new ParameterBuilder()
            .addName("result")
            .setRemarks("divide result")
            .setRequired(true)
            .create(Double.class, null);
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new ParameterBuilder().addName("OutputParameters").createGroup(RESULT_NUMBER);

    /** Instance */
    public static final ProcessDescriptor INSTANCE = new DivideDescriptor();

    private DivideDescriptor() {
        super(NAME, MathProcessingRegistry.IDENTIFICATION,
                new SimpleInternationalString("Divide two double (first/second)."),
                INPUT_DESC, OUTPUT_DESC);
    }

    @Override
    public Process createProcess(final ParameterValueGroup input) {
        return new DivideProcess(input);
    }
    
}
