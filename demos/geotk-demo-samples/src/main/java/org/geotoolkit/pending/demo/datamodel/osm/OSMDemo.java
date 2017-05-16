

package org.geotoolkit.pending.demo.datamodel.osm;

import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.pending.demo.Demos;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.storage.DataStores;
import org.opengis.util.GenericName;

public class OSMDemo {

    public static void main(String[] args) throws DataStoreException, URISyntaxException {
        Demos.init();

        final Map<String,Serializable> parameters = new HashMap<String,Serializable>();
        parameters.put("path", OSMDemo.class.getResource("/data/sampleOSM.osm").toURI());

        final FeatureStore store = (FeatureStore) DataStores.open(parameters);

        System.out.println("=================== Feature types ====================");
        final Set<GenericName> names = store.getNames();
        for(GenericName name : names){
            System.out.println(store.getFeatureType(name.toString()));
        }

    }

}
