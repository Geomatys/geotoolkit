
package org.geotoolkit.pending.demo.datamodel;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.data.FeatureStoreFinder;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.postgis.PostgisNGFeatureStoreFactory;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.data.shapefile.ShapefileFeatureStoreFactory;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.pending.demo.Demos;
import org.geotoolkit.referencing.CRS;
import org.apache.sis.storage.DataStoreException;

import org.opengis.feature.type.Name;
import org.opengis.filter.FilterFactory;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.util.FactoryException;

public class FeatureStoreReadingDemo {

    private static final FilterFactory FF = FactoryFinder.getFilterFactory(null);

    public static void main(String[] args) throws DataStoreException, NoSuchAuthorityCodeException, FactoryException {
        Demos.init();
        
        //getting a datastore
        final FeatureStore store = createUsingParameterGroup();

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

    private static FeatureStore createUsingMap() throws DataStoreException{

        //we must know the parameters
        final Map<String,Serializable> parameters = new HashMap<String, Serializable>();
        parameters.put("url", FeatureStoreReadingDemo.class.getResource("/data/world/Countries.shp"));

        final FeatureStore store = FeatureStoreFinder.open(parameters);
        return store;
    }

    private static FeatureStore createUsingParameterGroup() throws DataStoreException{

        //find out how to describe things
        System.out.println(ShapefileFeatureStoreFactory.PARAMETERS_DESCRIPTOR);
        System.out.println(PostgisNGFeatureStoreFactory.PARAMETERS_DESCRIPTOR);

        final ParameterValueGroup parameters = ShapefileFeatureStoreFactory.PARAMETERS_DESCRIPTOR.createValue();
        parameters.parameter("url").setValue(FeatureStoreReadingDemo.class.getResource("/data/world/Countries.shp"));
        
        final FeatureStore store = FeatureStoreFinder.open(parameters);
        return store;
    }

}
