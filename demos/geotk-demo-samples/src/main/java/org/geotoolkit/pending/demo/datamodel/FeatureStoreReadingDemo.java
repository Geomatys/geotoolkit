
package org.geotoolkit.pending.demo.datamodel;

import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import org.apache.sis.internal.storage.query.SimpleQuery;
import org.apache.sis.internal.system.DefaultFactories;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.geotoolkit.data.shapefile.ShapefileProvider;
import org.geotoolkit.db.postgres.PostgresProvider;
import org.geotoolkit.pending.demo.Demos;
import org.geotoolkit.storage.DataStores;
import org.opengis.feature.Feature;
import org.opengis.filter.FilterFactory;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.util.FactoryException;

public class FeatureStoreReadingDemo {

    private static final FilterFactory FF = DefaultFactories.forBuildin(FilterFactory.class);

    public static void main(String[] args) throws DataStoreException, NoSuchAuthorityCodeException, FactoryException, URISyntaxException {
        Demos.init();

        //getting a datastore
        final DataStore store = createUsingParameterGroup();

        //getting all available feature types -----------------------------------------------
        for (FeatureSet fs : DataStores.flatten(store, true, FeatureSet.class)) {
            System.out.println(fs.getType());

            //reading features -------------------------------------------------------------------
            //showCollection(collection, 10);



            //advanced querying -------------------------------------------------------------------
            final SimpleQuery qb = new SimpleQuery();
            qb.setColumns(
                    new SimpleQuery.Column(FF.function("ST_Transform", FF.property("the_geom"), FF.literal("EPSG:3395")) ),
                    new SimpleQuery.Column(FF.property("LONG_NAME")),
                    new SimpleQuery.Column(FF.property("SQKM")));
            qb.setFilter(FF.equals(FF.property("CURR_TYPE"), FF.literal("Norwegian Krone")));

            FeatureSet collection = fs.subset(qb);
            System.out.println(collection.getType());
            showCollection(collection, 10);
        }

    }

    private static void showCollection(FeatureSet collection, int limit) throws DataStoreException{
        try (Stream<Feature> stream = collection.features(false).limit(limit)) {
            stream.forEach(System.out::println);
        }
    }

    private static DataStore createUsingMap() throws DataStoreException, URISyntaxException {

        //we must know the parameters
        final Map<String,Serializable> parameters = new HashMap<String, Serializable>();
        String pathId = ShapefileProvider.PATH.getName().getCode();
        parameters.put(pathId, FeatureStoreReadingDemo.class.getResource("/data/world/Countries.shp").toURI());

        return DataStores.open(parameters);
    }

    private static DataStore createUsingParameterGroup() throws DataStoreException, URISyntaxException {

        //find out how to describe things
        System.out.println(ShapefileProvider.PARAMETERS_DESCRIPTOR);
        System.out.println(PostgresProvider.PARAMETERS_DESCRIPTOR);

        final ParameterValueGroup parameters = ShapefileProvider.PARAMETERS_DESCRIPTOR.createValue();
        String pathId = ShapefileProvider.PATH.getName().getCode();
        parameters.parameter(pathId).setValue(FeatureStoreReadingDemo.class.getResource("/data/world/Countries.shp").toURI());

        return DataStores.open(parameters);
    }

}
