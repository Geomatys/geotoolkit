
package org.geotoolkit.pending.demo.datamodel;

import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.db.postgres.PostgresFeatureStoreFactory;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.data.shapefile.ShapefileFeatureStoreFactory;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.pending.demo.Demos;
import org.apache.sis.referencing.CRS;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.storage.DataStores;

import org.opengis.util.GenericName;
import org.opengis.filter.FilterFactory;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.util.FactoryException;

public class FeatureStoreReadingDemo {

    private static final FilterFactory FF = FactoryFinder.getFilterFactory(null);

    public static void main(String[] args) throws DataStoreException, NoSuchAuthorityCodeException, FactoryException, URISyntaxException {
        Demos.init();

        //getting a datastore
        final FeatureStore store = createUsingParameterGroup();

        //getting all available feature types -----------------------------------------------
        final Set<GenericName> typeNames = store.getNames();
        for(GenericName name : typeNames){
            System.out.println(store.getFeatureType(name.toString()));
        }


        //creating the session ---------------------------------------------------------------
        final Session session = store.createSession(true);


        //reading features -------------------------------------------------------------------
        final GenericName typeName = typeNames.iterator().next();
        FeatureCollection collection = session.getFeatureCollection(QueryBuilder.all(typeName));
        //showCollection(collection, 10);


        //advanced querying -------------------------------------------------------------------
        final QueryBuilder qb = new QueryBuilder(typeName.toString());
        qb.setCRS(CRS.forCode("EPSG:3395"));
        qb.setProperties(new String[]{"the_geom","LONG_NAME","SQKM"});
        qb.setFilter(FF.equals(FF.property("CURR_TYPE"), FF.literal("Norwegian Krone")));
        final Query query = qb.buildQuery();

        collection = session.getFeatureCollection(query);
        System.out.println(collection.getType());
        showCollection(collection, 10);

    }

    private static void showCollection(FeatureCollection collection, int limit){
        FeatureIterator ite = collection.iterator();
        try{
            int i=0;
            while(ite.hasNext()){
                if(i==limit) break;
                System.out.println(ite.next());
                i++;
            }
        }finally{
            ite.close();
        }
    }

    private static FeatureStore createUsingMap() throws DataStoreException, URISyntaxException {

        //we must know the parameters
        final Map<String,Serializable> parameters = new HashMap<String, Serializable>();
        String pathId = ShapefileFeatureStoreFactory.PATH.getName().getCode();
        parameters.put(pathId, FeatureStoreReadingDemo.class.getResource("/data/world/Countries.shp").toURI());

        final FeatureStore store = (FeatureStore) DataStores.open(parameters);
        return store;
    }

    private static FeatureStore createUsingParameterGroup() throws DataStoreException, URISyntaxException {

        //find out how to describe things
        System.out.println(ShapefileFeatureStoreFactory.PARAMETERS_DESCRIPTOR);
        System.out.println(PostgresFeatureStoreFactory.PARAMETERS_DESCRIPTOR);

        final ParameterValueGroup parameters = ShapefileFeatureStoreFactory.PARAMETERS_DESCRIPTOR.createValue();
        String pathId = ShapefileFeatureStoreFactory.PATH.getName().getCode();
        parameters.parameter(pathId).setValue(FeatureStoreReadingDemo.class.getResource("/data/world/Countries.shp").toURI());

        final FeatureStore store = (FeatureStore) DataStores.open(parameters);
        return store;
    }

}
