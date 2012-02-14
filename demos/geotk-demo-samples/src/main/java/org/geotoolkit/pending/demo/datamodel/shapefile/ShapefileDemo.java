
package org.geotoolkit.pending.demo.datamodel.shapefile;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.geotoolkit.data.DataStore;
import org.geotoolkit.data.DataStoreFinder;
import org.geotoolkit.data.shapefile.ShapefileDataStoreFactory;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.storage.DataStoreException;
import org.opengis.parameter.ParameterValueGroup;

public class ShapefileDemo {
    
    public static void main(String[] args) throws DataStoreException {
        
        //create using a Parameters object--------------------------------------
        System.out.println(ShapefileDataStoreFactory.PARAMETERS_DESCRIPTOR);
        
        final ParameterValueGroup parameters = ShapefileDataStoreFactory.PARAMETERS_DESCRIPTOR.createValue();
        Parameters.getOrCreate(ShapefileDataStoreFactory.URLP,parameters).setValue(
                ShapefileDemo.class.getResource("/data/world/Countries.shp"));
        
        final DataStore store1 = DataStoreFinder.get(parameters);        
        
        
        //create using a Map----------------------------------------------------
        final Map<String,Serializable> map = new HashMap<String, Serializable>();
        map.put("url", ShapefileDemo.class.getResource("/data/world/Countries.shp"));
        
        final DataStore store2 = DataStoreFinder.get(map);
        
    }
    
}
