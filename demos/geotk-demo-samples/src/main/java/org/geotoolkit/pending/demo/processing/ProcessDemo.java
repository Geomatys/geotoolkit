
package org.geotoolkit.pending.demo.processing;

import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.process.Process;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.NoSuchIdentifierException;

public class ProcessDemo {
    
    public static void main(String[] args) throws NoSuchIdentifierException, ProcessException {
        
        //get the description of the process we want
        final ProcessDescriptor desc = ProcessFinder.getProcessDescriptor("mymaths", "add");
        
        //create a process
        
        //set the input parameters
        final ParameterValueGroup input = desc.getInputDescriptor().createValue();
        input.parameter("first").setValue(15d);
        input.parameter("second").setValue(5d);
        final Process p = desc.createProcess(input);
        
        
        //get the result
        final ParameterValueGroup result = p.call();
        System.out.println(result);
        
                
        
    }
    
}
