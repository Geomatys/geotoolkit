
package org.geotoolkit.pending.demo.processing.registry;

import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.process.ProcessDescriptor;
import org.opengis.parameter.ParameterValueGroup;

public class AddProcess extends AbstractProcess{

    public AddProcess(final ProcessDescriptor descriptor, final ParameterValueGroup input){
        super(descriptor, input);
    }

    @Override
    protected void execute() {

        final double first = (Double)inputParameters.parameter(AddDescriptor.FIRST_NUMBER.getName().getCode()).getValue();
        final double second = (Double)inputParameters.parameter(AddDescriptor.SECOND_NUMBER.getName().getCode()).getValue();

        Double result = first + second;
        outputParameters.parameter(AddDescriptor.RESULT_NUMBER.getName().getCode()).setValue(result);
    }

}
