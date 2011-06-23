
package org.geotoolkit.pending.demo.processing;

import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.process.Process;
import org.opengis.parameter.ParameterValueGroup;

public class ProcessDemo {
    
    public static void main(String[] args) {
        
        //get the description of the process we want
        final ProcessDescriptor desc = ProcessFinder.getProcessDescriptor("mymaths", "add");
        
        //create a process
        final Process p = desc.createProcess();
        
        //set the input parameters
        final ParameterValueGroup input = desc.getInputDescriptor().createValue();
        input.parameter("first").setValue(15d);
        input.parameter("second").setValue(5d);
        p.setInput(input);
        
        //run the process
        p.run();
        
        //get the result
        final ParameterValueGroup result = p.getOutput();
        System.out.println(result);
        
                
        
    }
    
}
