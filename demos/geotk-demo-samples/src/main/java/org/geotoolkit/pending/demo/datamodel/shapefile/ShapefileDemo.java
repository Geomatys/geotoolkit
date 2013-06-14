
package org.geotoolkit.pending.demo.datamodel.shapefile;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.data.FeatureStoreFinder;
import org.geotoolkit.data.shapefile.ShapefileFeatureStoreFactory;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.pending.demo.Demos;
import org.apache.sis.storage.DataStoreException;
import org.opengis.parameter.ParameterValueGroup;

public class ShapefileDemo {
    
    public static void main(String[] args) throws DataStoreException {
        Demos.init();
        
        //create using a Parameters object--------------------------------------
        System.out.println(ShapefileFeatureStoreFactory.PARAMETERS_DESCRIPTOR);
        
        final ParameterValueGroup parameters = ShapefileFeatureStoreFactory.PARAMETERS_DESCRIPTOR.createValue();
        Parameters.getOrCreate(ShapefileFeatureStoreFactory.URLP,parameters).setValue(
                ShapefileDemo.class.getResource("/data/world/Countries.shp"));
        
        final FeatureStore store1 = FeatureStoreFinder.open(parameters);        
        
        
        //create using a Map----------------------------------------------------
        final Map<String,Serializable> map = new HashMap<String, Serializable>();
        map.put("url", ShapefileDemo.class.getResource("/data/world/Countries.shp"));
        
        final FeatureStore store2 = FeatureStoreFinder.open(map);
        
    }
    
}
