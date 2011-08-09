
package org.geotoolkit.pending.demo.processing.factory;

import org.geotoolkit.process.AbstractProcess;
import org.opengis.parameter.ParameterValueGroup;

public class AddProcess extends AbstractProcess{
    
    public AddProcess(final ParameterValueGroup input){
        super(AddDescriptor.INSTANCE, input);
    }
    
    @Override
    public ParameterValueGroup call() {
        
        final double first = (Double)inputParameters.parameter("first").getValue();   
        final double second = (Double)inputParameters.parameter("second").getValue();       
        
        Double result = first + second;
        outputParameters.parameter("result").setValue(result);
        return outputParameters;
    }

    
}
