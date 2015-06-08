

package org.geotoolkit.pending.demo.datamodel.gpx;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.data.FeatureStoreFinder;
import org.geotoolkit.pending.demo.Demos;
import org.apache.sis.storage.DataStoreException;
import org.opengis.util.GenericName;

public class GPXDemo {

    public static void main(String[] args) throws DataStoreException {
        Demos.init();
        
        final Map<String,Serializable> parameters = new HashMap<String,Serializable>();
        parameters.put("url", GPXDemo.class.getResource("/data/sampleGPX.gpx"));

        final FeatureStore store = FeatureStoreFinder.open(parameters);

        System.out.println("=================== Feature types ====================");
        final Set<GenericName> names = store.getNames();
        for(GenericName name : names){
            System.out.println(store.getFeatureType(name));
        }


    }

}
