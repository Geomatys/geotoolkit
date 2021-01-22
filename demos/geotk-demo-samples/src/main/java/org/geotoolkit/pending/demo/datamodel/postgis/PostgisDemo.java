package org.geotoolkit.pending.demo.datamodel.postgis;

import org.apache.sis.parameter.Parameters;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.geotoolkit.db.postgres.PostgresProvider;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.pending.demo.Demos;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.storage.feature.FeatureStore;
import org.geotoolkit.storage.feature.query.QueryBuilder;
import org.geotoolkit.style.RandomStyleBuilder;
import org.opengis.util.GenericName;

public class PostgisDemo {

    public static void main(String[] args) throws DataStoreException {
        Demos.init();

        System.out.println(PostgresProvider.PARAMETERS_DESCRIPTOR);

        final Parameters parameters = Parameters.castOrWrap(PostgresProvider.PARAMETERS_DESCRIPTOR.createValue());
        parameters.getOrCreate(PostgresProvider.HOST).setValue("hote");
        parameters.getOrCreate(PostgresProvider.PORT).setValue(5432);
        parameters.getOrCreate(PostgresProvider.DATABASE).setValue("base");
        parameters.getOrCreate(PostgresProvider.USER).setValue("user");
        parameters.getOrCreate(PostgresProvider.PASSWORD).setValue("secret");

        final FeatureStore store = (FeatureStore) DataStores.open(parameters);

        final MapContext context = MapBuilder.createContext();

        for(GenericName n : store.getNames()){
            System.out.println(store.getFeatureType(n.toString()));

            final FeatureSet col = store.createSession(true).getFeatureCollection(QueryBuilder.all(n.toString()));
            final MapLayer layer = MapBuilder.createLayer(col);
            layer.setStyle(RandomStyleBuilder.createRandomVectorStyle(col.getType()));
            context.getComponents().add(layer);
        }


//        FXMapFrame.show(context);

    }

}
