
package org.geotoolkit.pending.demo.datamodel;

import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureQuery;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.shapefile.ShapefileProvider;
import org.geotoolkit.filter.FilterUtilities;
import org.geotoolkit.pending.demo.Demos;
import org.geotoolkit.storage.DataStores;
import org.opengis.feature.Feature;
import org.opengis.filter.FilterFactory;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.util.FactoryException;

public class FeatureStoreReadingDemo {

    private static final FilterFactory FF = FilterUtilities.FF;

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
            final FeatureQuery qb = new FeatureQuery();
            qb.setProjection(
                    new FeatureQuery.NamedExpression(FF.function("ST_Transform", FF.property("the_geom"), FF.literal("EPSG:3395")) ),
                    new FeatureQuery.NamedExpression(FF.property("LONG_NAME")),
                    new FeatureQuery.NamedExpression(FF.property("SQKM")));
            qb.setSelection(FF.equal(FF.property("CURR_TYPE"), FF.literal("Norwegian Krone")));

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

    private static DataStore createUsingParameterGroup() throws DataStoreException, URISyntaxException {

        //find out how to describe things
        System.out.println(ShapefileProvider.provider().getOpenParameters());

        final ParameterValueGroup parameters = ShapefileProvider.provider().getOpenParameters().createValue();
        parameters.parameter(ShapefileProvider.LOCATION).setValue(FeatureStoreReadingDemo.class.getResource("/data/world/Countries.shp").toURI());

        return DataStores.open(parameters);
    }

}
