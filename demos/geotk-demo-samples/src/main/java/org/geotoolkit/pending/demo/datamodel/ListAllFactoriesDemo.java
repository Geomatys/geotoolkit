
package org.geotoolkit.pending.demo.datamodel;

import org.apache.sis.storage.DataStoreProvider;
import org.apache.sis.storage.DataStores;
import org.geotoolkit.pending.demo.Demos;
import org.opengis.parameter.ParameterDescriptorGroup;


public class ListAllFactoriesDemo {

    public static void main(String[] args) {
        Demos.init();

        // Listing or creating new datastores are made through the DataStoreFinder utility class

        for (DataStoreProvider provider : DataStores.providers()) {

            //display general informations about this provider
            System.out.println(provider.getShortName());

            //display the parameter requiered to open a new instance
            //of featurestore of this type
            final ParameterDescriptorGroup description = provider.getOpenParameters();
            System.out.println(description);
            System.out.println("\n\n");

        }


    }

}
