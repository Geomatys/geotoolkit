package org.geotoolkit.pending.demo.datamodel;

import com.vividsolutions.jts.geom.Point;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.memory.MemoryFeatureStore;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.session.Session;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.geotoolkit.pending.demo.Demos;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataStoreException;
import org.opengis.feature.FeatureType;

public class MemoryFeatureStoreDemo {

    public static void main(String[] args) throws DataStoreException {
        Demos.init();

        //create the datastore
        final MemoryFeatureStore store = new MemoryFeatureStore();


        //add a schema in the datastore
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("http://geomatys.com", "test");
        ftb.addAttribute(String.class).setName("type");
        ftb.addAttribute(Point.class).setName("the_geom").setCRS(CommonCRS.WGS84.normalizedGeographic());
        final FeatureType type = ftb.build();
        store.createFeatureType(type);


        //query the featurestore like any other
        final Session session = store.createSession(true);
        final Query query = QueryBuilder.all(type.getName());
        final FeatureCollection collection = session.getFeatureCollection(query);

    }
}
