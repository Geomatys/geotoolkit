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
package org.geotoolkit.processing.math.absolute;

import org.apache.sis.parameter.ParameterBuilder;
import org.geotoolkit.processing.AbstractProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.processing.GeotkProcessingRegistry;
import org.apache.sis.util.iso.SimpleInternationalString;

import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * @author Quentin Boileau (Geomatys)
 * @module
 */
public class AbsoluteDescriptor extends AbstractProcessDescriptor{
        
    /**Process name : absolute */
    public static final String NAME = "math:absolute";
    
    /**
     * Input parameters
     */
    public static final ParameterDescriptor<Double> FIRST_NUMBER = new ParameterBuilder()
            .addName("first")
            .setRemarks("first number")
            .setRequired(true)
            .create(Double.class, null);
  
    public static final ParameterDescriptorGroup INPUT_DESC = new ParameterBuilder()
            .addName("InputParameters").createGroup(FIRST_NUMBER);
    
    /**
     * OutputParameters
     */
    public static final ParameterDescriptor<Double> RESULT_NUMBER = new ParameterBuilder()
            .addName("result")
            .setRemarks("absolute result")
            .setRequired(true)
            .create(Double.class, null);
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new ParameterBuilder().addName("OutputParameters").createGroup(RESULT_NUMBER);

    /** Instance */
    public static final ProcessDescriptor INSTANCE = new AbsoluteDescriptor();

    private AbsoluteDescriptor() {
        super(NAME, GeotkProcessingRegistry.IDENTIFICATION,
                new SimpleInternationalString("Return the absolute value of a double."),
                INPUT_DESC, OUTPUT_DESC);
    }

    @Override
    public Process createProcess(final ParameterValueGroup input) {
        return new AbsoluteProcess(input);
    }
    
}
