
package org.geotoolkit.pending.demo.coverage;

import java.util.Iterator;
import org.apache.sis.storage.DataStoreProvider;
import org.apache.sis.storage.DataStores;
import org.geotoolkit.pending.demo.Demos;
import org.opengis.parameter.ParameterDescriptorGroup;


public class ListAllProvidersDemo {

    public static void main(String[] args) {
        Demos.init();

        // Listing or creating new coveragestores are made through the CoverageStoreFinder utility class
        final Iterator<DataStoreProvider> ite = DataStores.providers().iterator();

        while(ite.hasNext()){
            final DataStoreProvider factory = ite.next();

            //display general informations about this factory
            System.out.println(factory.getShortName());

            //display the parameter requiered to create a new instance
            //of coveragestore of this type
            final ParameterDescriptorGroup description = factory.getOpenParameters();
            System.out.println(description);
            System.out.println("\n\n");

        }


    }

}
