/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012-2013, Geomatys
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
package org.geotoolkit.processing.chain;

import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.processing.AbstractProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

public class MockMultiplyDescriptor extends AbstractProcessDescriptor{

    public static final String NAME = "multiply";

    public static final ParameterDescriptor<Double> FIRST_NUMBER = new DefaultParameterDescriptor<Double>("first", "", Double.class, null, true);
    public static final ParameterDescriptor<Double> SECOND_NUMBER = new DefaultParameterDescriptor<Double>("second", "", Double.class, null, true);    
    public static final ParameterDescriptorGroup INPUT_DESC =
            new DefaultParameterDescriptorGroup("InputParameters",
            new GeneralParameterDescriptor[]{FIRST_NUMBER,SECOND_NUMBER});

    public static final ParameterDescriptor<Double> RESULT_NUMBER = new DefaultParameterDescriptor<Double>("result", "", Double.class, null, true);
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new DefaultParameterDescriptorGroup("OutputParameters",
            new GeneralParameterDescriptor[]{RESULT_NUMBER});

    public static final ProcessDescriptor INSTANCE = new MockMultiplyDescriptor();

    private MockMultiplyDescriptor() {
        super(NAME, MockProcessRegistry.IDENTIFICATION,
                new SimpleInternationalString(""),INPUT_DESC, OUTPUT_DESC);
    }

    @Override
    public Process createProcess(final ParameterValueGroup input) {
        return new MultiplyProcess(this, input);
    }

    public class MultiplyProcess extends AbstractProcess {

        public MultiplyProcess(final ProcessDescriptor descriptor, final ParameterValueGroup input) {
            super(descriptor, input);
        }

        @Override
        protected void execute() {
            final double first = (Double) inputParameters.parameter(MockAddDescriptor.FIRST_NUMBER.getName().getCode()).getValue();
            final double second = (Double) inputParameters.parameter(MockAddDescriptor.SECOND_NUMBER.getName().getCode()).getValue();
            Double result = first * second;
            outputParameters.parameter(MockAddDescriptor.RESULT_NUMBER.getName().getCode()).setValue(result);
        }
    }
    
}