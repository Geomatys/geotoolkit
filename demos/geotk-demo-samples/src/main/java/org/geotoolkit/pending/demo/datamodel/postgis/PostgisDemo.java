package org.geotoolkit.pending.demo.datamodel.postgis;

import org.apache.sis.parameter.Parameters;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.db.postgres.PostgresFeatureStoreFactory;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.gui.swing.render2d.JMap2DFrame;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.pending.demo.Demos;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.style.RandomStyleBuilder;
import org.opengis.util.GenericName;

public class PostgisDemo {

    public static void main(String[] args) throws DataStoreException {
        Demos.init();

        System.out.println(PostgresFeatureStoreFactory.PARAMETERS_DESCRIPTOR);

        final Parameters parameters = Parameters.castOrWrap(PostgresFeatureStoreFactory.PARAMETERS_DESCRIPTOR.createValue());
        parameters.getOrCreate(PostgresFeatureStoreFactory.HOST).setValue("hote");
        parameters.getOrCreate(PostgresFeatureStoreFactory.PORT).setValue(5432);
        parameters.getOrCreate(PostgresFeatureStoreFactory.DATABASE).setValue("base");
        parameters.getOrCreate(PostgresFeatureStoreFactory.USER).setValue("user");
        parameters.getOrCreate(PostgresFeatureStoreFactory.PASSWORD).setValue("secret");

        final FeatureStore store = (FeatureStore) DataStores.open(parameters);

        final MapContext context = MapBuilder.createContext();

        for(GenericName n : store.getNames()){
            System.out.println(store.getFeatureType(n.toString()));

            final FeatureCollection col = store.createSession(true).getFeatureCollection(QueryBuilder.all(n.toString()));
            context.layers().add(MapBuilder.createFeatureLayer(col, RandomStyleBuilder.createRandomVectorStyle(col.getType())));
        }


        JMap2DFrame.show(context);

    }

}
