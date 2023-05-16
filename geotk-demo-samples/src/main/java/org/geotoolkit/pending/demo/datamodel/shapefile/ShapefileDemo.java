
package org.geotoolkit.pending.demo.datamodel.shapefile;

import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.shapefile.ShapefileProvider;
import org.geotoolkit.pending.demo.Demos;
import org.geotoolkit.storage.DataStores;
import org.opengis.parameter.ParameterValueGroup;

public class ShapefileDemo {

    public static void main(String[] args) throws DataStoreException, URISyntaxException {
        Demos.init();

        //create using a Parameters object--------------------------------------
        System.out.println(ShapefileProvider.PARAMETERS_DESCRIPTOR);

        final ParameterValueGroup parameters = ShapefileProvider.PARAMETERS_DESCRIPTOR.createValue();
        parameters.parameter(ShapefileProvider.PATH.getName().getCode()).setValue(
                ShapefileDemo.class.getResource("/data/world/Countries.shp").toURI());

        final DataStore store1 = DataStores.open(parameters);


        //create using a Map----------------------------------------------------
        final Map<String,Serializable> map = new HashMap<String, Serializable>();
        map.put("path", ShapefileDemo.class.getResource("/data/world/Countries.shp").toURI());

        final DataStore store2 = DataStores.open(map);

    }

}
