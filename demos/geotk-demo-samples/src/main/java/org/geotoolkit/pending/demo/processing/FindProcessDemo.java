
package org.geotoolkit.pending.demo.processing;

import java.util.Iterator;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.process.ProcessingRegistry;
import org.geotoolkit.process.ProcessDescriptor;


public class FindProcessDemo {
    
    public static void main(String[] args) {
        
        //Get an iterator on all process factory
        final Iterator<ProcessingRegistry> factoryIte = ProcessFinder.getProcessFactories();

        
        while (factoryIte.hasNext()) {

            //Get a factory
            final ProcessingRegistry factory = factoryIte.next();
            System.out.println("Factory : "+factory.getIdentification().getCitation().getTitle().toString());
            
            //Get factory process descriptor in order to find the description of all process of this factory.
            for (ProcessDescriptor descriptor : factory.getDescriptors()) {
                System.out.println("    Process : "+descriptor.getIdentifier().getCode());
              
            }
            System.out.println("---------------------------------------------------------------------------");
        }
    }
}
