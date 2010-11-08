package org.geotoolkit.pending.demo.datamodel;

import com.vividsolutions.jts.geom.Point;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.memory.MemoryDataStore;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.storage.DataStoreException;
import org.opengis.feature.type.FeatureType;

public class MemoryDataStoreDemo {

    public static void main(String[] args) throws DataStoreException {

        //create the datastore
        final MemoryDataStore store = new MemoryDataStore();


        //add a schema in the datastore
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("http://geomatys.com", "test");
        ftb.add("type", String.class);
        ftb.add("the_geom", Point.class, DefaultGeographicCRS.WGS84);
        final FeatureType type = ftb.buildFeatureType();
        store.createSchema(type.getName(), type);


        //query the datastore like any other
        final Session session = store.createSession(true);
        final Query query = QueryBuilder.all(type.getName());
        final FeatureCollection collection = session.getFeatureCollection(query);

    }
}
