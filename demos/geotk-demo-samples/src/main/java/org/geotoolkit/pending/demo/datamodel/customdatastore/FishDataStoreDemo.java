

package org.geotoolkit.pending.demo.datamodel.customdatastore;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.data.FeatureStoreFinder;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.pending.demo.Demos;
import org.geotoolkit.storage.DataStoreException;
import org.opengis.feature.type.Name;

public class FishDataStoreDemo {

    public static void main(String[] args) throws DataStoreException {
        Demos.init();
        
        Map<String,Serializable> params = new HashMap<String, Serializable>();
        params.put("url", FishDataStoreDemo.class.getResource("/data/fishes.fsh"));

        FeatureStore store = FeatureStoreFinder.open(params);

        for(Name name : store.getNames()){
            System.out.println(store.getFeatureType(name));

            System.out.println("\n---------------------------\n");

            System.out.println(store.createSession(true).getFeatureCollection(QueryBuilder.all(name)));

        }


    }

}
