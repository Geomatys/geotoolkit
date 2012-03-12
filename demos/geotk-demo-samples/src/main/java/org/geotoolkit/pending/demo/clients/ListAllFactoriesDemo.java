
package org.geotoolkit.pending.demo.clients;

import java.util.Iterator;
import org.geotoolkit.client.ServerFactory;
import org.geotoolkit.client.ServerFinder;
import org.opengis.parameter.ParameterDescriptorGroup;


public class ListAllFactoriesDemo {
    
    public static void main(String[] args) {
        
        // Listing or creating new servers are made through the serverFinder utility class        
        final Iterator<ServerFactory> ite = ServerFinder.getAllFactories();
        
        while(ite.hasNext()){
            
            final ServerFactory factory = ite.next();
            
            //display general informations about this factory
            System.out.println(factory.getDisplayName());
            System.out.println(factory.getDescription());
            
            //display the parameter requiered to create a new instance
            //of server of this type
            final ParameterDescriptorGroup description = factory.getParametersDescriptor();
            System.out.println(description);
            System.out.println("\n\n");
            
            
            //if we wanted to created a new server of this type we would proceed
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
