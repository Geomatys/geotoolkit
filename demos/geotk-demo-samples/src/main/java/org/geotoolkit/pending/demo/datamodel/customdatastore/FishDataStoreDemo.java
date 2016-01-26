

package org.geotoolkit.pending.demo.datamodel.customdatastore;

import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.pending.demo.Demos;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.storage.DataStores;
import org.opengis.util.GenericName;

public class FishDataStoreDemo {

    public static void main(String[] args) throws DataStoreException, URISyntaxException {
        Demos.init();
        
        Map<String,Serializable> params = new HashMap<String, Serializable>();
        params.put("path", FishDataStoreDemo.class.getResource("/data/fishes.fsh").toURI());

        FeatureStore store = (FeatureStore) DataStores.open(params);

        for(GenericName name : store.getNames()){
            System.out.println(store.getFeatureType(name));

            System.out.println("\n---------------------------\n");

            System.out.println(store.createSession(true).getFeatureCollection(QueryBuilder.all(name)));

        }


    }

}
