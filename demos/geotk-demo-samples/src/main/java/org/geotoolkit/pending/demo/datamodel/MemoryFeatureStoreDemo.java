package org.geotoolkit.pending.demo.datamodel;

import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.geotoolkit.data.DefiningFeatureSet;
import org.geotoolkit.data.memory.InMemoryStore;
import org.geotoolkit.pending.demo.Demos;
import org.geotoolkit.storage.DataStores;
import org.locationtech.jts.geom.Point;
import org.opengis.feature.FeatureType;

public class MemoryFeatureStoreDemo {

    public static void main(String[] args) throws DataStoreException {
        Demos.init();

        //create the datastore
        final InMemoryStore store = new InMemoryStore();


        //add a schema in the datastore
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("http://geomatys.com", "test");
        ftb.addAttribute(String.class).setName("type");
        ftb.addAttribute(Point.class).setName("the_geom").setCRS(CommonCRS.WGS84.normalizedGeographic());
        final FeatureType type = ftb.build();
        store.add(new DefiningFeatureSet(type, null));


        //query the featurestore like any other
        for (FeatureSet fs : DataStores.flatten(store, true, FeatureSet.class)) {
            System.out.println(fs.getType());
        }

    }
}
