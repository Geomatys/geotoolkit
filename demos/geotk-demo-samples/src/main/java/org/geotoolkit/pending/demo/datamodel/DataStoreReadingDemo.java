
package org.geotoolkit.pending.demo.datamodel;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.geotoolkit.data.DataStore;
import org.geotoolkit.data.DataStoreFinder;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.postgis.PostgisNGDataStoreFactory;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.data.shapefile.ShapefileDataStoreFactory;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.storage.DataStoreException;

import org.opengis.feature.type.Name;
import org.opengis.filter.FilterFactory;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.util.FactoryException;

public class DataStoreReadingDemo {

    private static final FilterFactory FF = FactoryFinder.getFilterFactory(null);

    public static void main(String[] args) throws DataStoreException, NoSuchAuthorityCodeException, FactoryException {

        //getting a datastore
        final DataStore store = createUsingParameterGroup();

        //getting all available feature types -----------------------------------------------
        final Set<Name> typeNames = store.getNames();
        for(Name name : typeNames){
            System.out.println(store.getFeatureType(name));
        }


        //creating the session ---------------------------------------------------------------
        final Session session = store.createSession(true);


        //reading features -------------------------------------------------------------------
        final Name typeName = typeNames.iterator().next();
        FeatureCollection collection = session.getFeatureCollection(QueryBuilder.all(typeName));
        //showCollection(collection, 10);


        //advanced querying -------------------------------------------------------------------
        final QueryBuilder qb = new QueryBuilder(typeName);
        qb.setCRS(CRS.decode("EPSG:3395"));
        qb.setProperties(new String[]{"the_geom","LONG_NAME","SQKM"});
        qb.setFilter(FF.equals(FF.property("CURR_TYPE"), FF.literal("Norwegian Krone")));
        final Query query = qb.buildQuery();

        collection = session.getFeatureCollection(query);
        System.out.println(collection.getFeatureType());
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

    private static DataStore createUsingMap() throws DataStoreException{

        //we must know the parameters
        final Map<String,Serializable> parameters = new HashMap<String, Serializable>();
        parameters.put("url", DataStoreReadingDemo.class.getResource("/data/world/Countries.shp"));

        final DataStore store = DataStoreFinder.get(parameters);
        return store;
    }

    private static DataStore createUsingParameterGroup() throws DataStoreException{

        //find out how to describe things
        System.out.println(ShapefileDataStoreFactory.PARAMETERS_DESCRIPTOR);
        System.out.println(PostgisNGDataStoreFactory.PARAMETERS_DESCRIPTOR);

        final ParameterValueGroup parameters = ShapefileDataStoreFactory.PARAMETERS_DESCRIPTOR.createValue();
        parameters.parameter("url").setValue(DataStoreReadingDemo.class.getResource("/data/world/Countries.shp"));
        
        final DataStore store = DataStoreFinder.get(parameters);
        return store;
    }

}
