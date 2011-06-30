
package org.geotoolkit.pending.demo.processing.factory;

import org.geotoolkit.process.AbstractProcess;
import org.opengis.parameter.ParameterValueGroup;

public class AddProcess extends AbstractProcess{
    
    public AddProcess(){
        super(AddDescriptor.INSTANCE);
    }
    
    @Override
    public void run() {
        
        final double first = (Double)inputParameters.parameter("first").getValue();   
        final double second = (Double)inputParameters.parameter("second").getValue();       
        
        Double result = first + second;
        final ParameterValueGroup res =  super.getOutput();
        res.parameter("result").setValue(result);
    }

    
}
