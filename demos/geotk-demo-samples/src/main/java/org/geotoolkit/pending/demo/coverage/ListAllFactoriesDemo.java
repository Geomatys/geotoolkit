
package org.geotoolkit.pending.demo.coverage;

import java.util.Iterator;
import org.geotoolkit.coverage.CoverageStoreFactory;
import org.geotoolkit.coverage.CoverageStoreFinder;
import org.opengis.parameter.ParameterDescriptorGroup;


public class ListAllFactoriesDemo {
    
    public static void main(String[] args) {
        
        // Listing or creating new coveragestores are made through the CoverageStoreFinder utility class        
        final Iterator<CoverageStoreFactory> ite = CoverageStoreFinder.getAllFactories();
        
        while(ite.hasNext()){
            
            final CoverageStoreFactory factory = ite.next();
            
            //display general informations about this factory
            System.out.println(factory.getDisplayName());
            System.out.println(factory.getDescription());
            
            //display the parameter requiered to create a new instance
            //of coveragestore of this type
            final ParameterDescriptorGroup description = factory.getParametersDescriptor();
            System.out.println(description);
            System.out.println("\n\n");
            
            
            //if we wanted to created a new coveragestore of this type we would proceed
            //like this :
            /*
            final ParameterValueGroup params = description.createValue();
            params.parameter("parameter_name_1").setValue("parameter value 1");
            params.parameter("parameter_name_2").setValue("parameter value 2");
            params.parameter("parameter_name_N").setValue("parameter value N");
            final Server server = factory.create(params);
            */
        }
        
        
    }
    
}
