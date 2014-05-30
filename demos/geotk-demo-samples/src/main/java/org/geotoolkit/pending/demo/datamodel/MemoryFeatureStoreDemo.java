package org.geotoolkit.pending.demo.datamodel;

import com.vividsolutions.jts.geom.Point;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.memory.MemoryFeatureStore;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.pending.demo.Demos;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.feature.type.FeatureType;

public class MemoryFeatureStoreDemo {

    public static void main(String[] args) throws DataStoreException {
        Demos.init();
        
        //create the datastore
        final MemoryFeatureStore store = new MemoryFeatureStore();


        //add a schema in the datastore
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("http://geomatys.com", "test");
        ftb.add("type", String.class);
        ftb.add("the_geom", Point.class, DefaultGeographicCRS.WGS84);
        final FeatureType type = ftb.buildFeatureType();
        store.createFeatureType(type.getName(), type);


        //query the featurestore like any other
        final Session session = store.createSession(true);
        final Query query = QueryBuilder.all(type.getName());
        final FeatureCollection collection = session.getFeatureCollection(query);

    }
}
