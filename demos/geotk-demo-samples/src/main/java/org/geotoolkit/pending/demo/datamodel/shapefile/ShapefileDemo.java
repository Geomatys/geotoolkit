
package org.geotoolkit.pending.demo.datamodel.shapefile;

import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.data.shapefile.ShapefileFeatureStoreFactory;
import org.geotoolkit.pending.demo.Demos;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.storage.DataStores;
import org.opengis.parameter.ParameterValueGroup;

public class ShapefileDemo {

    public static void main(String[] args) throws DataStoreException, URISyntaxException {
        Demos.init();

        //create using a Parameters object--------------------------------------
        System.out.println(ShapefileFeatureStoreFactory.PARAMETERS_DESCRIPTOR);

        final ParameterValueGroup parameters = ShapefileFeatureStoreFactory.PARAMETERS_DESCRIPTOR.createValue();
        parameters.parameter(ShapefileFeatureStoreFactory.PATH.getName().getCode()).setValue(
                ShapefileDemo.class.getResource("/data/world/Countries.shp").toURI());

        final FeatureStore store1 = (FeatureStore) DataStores.open(parameters);


        //create using a Map----------------------------------------------------
        final Map<String,Serializable> map = new HashMap<String, Serializable>();
        map.put("path", ShapefileDemo.class.getResource("/data/world/Countries.shp").toURI());

        final FeatureStore store2 = (FeatureStore) DataStores.open(map);

    }

}
