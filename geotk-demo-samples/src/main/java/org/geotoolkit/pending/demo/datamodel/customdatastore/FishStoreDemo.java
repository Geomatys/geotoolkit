

package org.geotoolkit.pending.demo.datamodel.customdatastore;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.geotoolkit.pending.demo.Demos;
import org.geotoolkit.storage.DataStores;
import org.opengis.feature.Feature;

public class FishStoreDemo {

    public static void main(String[] args) throws DataStoreException, URISyntaxException {
        Demos.init();

        final URI uri = FishStoreDemo.class.getResource("/data/fishes.fsh").toURI();

        final DataStore store = new FishStore(uri);
        final FeatureSet featureSet = (FeatureSet) store;

        System.out.println(featureSet.getType());

        try (Stream<Feature> stream = featureSet.features(true)) {
            stream.forEach(System.out::println);
        }

    }

}
