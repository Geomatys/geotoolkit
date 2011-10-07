package org.geotoolkit.pending.demo.datamodel.postgis;

import org.geotoolkit.data.DataStore;
import org.geotoolkit.data.DataStoreFinder;
import org.geotoolkit.data.postgis.PostgisNGDataStoreFactory;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.storage.DataStoreException;
import org.opengis.parameter.ParameterValueGroup;

public class PostgisDemo {

    public static void main(String[] args) throws DataStoreException {
        
        System.out.println(PostgisNGDataStoreFactory.PARAMETERS_DESCRIPTOR);

        final ParameterValueGroup parameters = PostgisNGDataStoreFactory.PARAMETERS_DESCRIPTOR.createValue();
        Parameters.getOrCreate(PostgisNGDataStoreFactory.HOST, parameters).setValue("hote");
        Parameters.getOrCreate(PostgisNGDataStoreFactory.PORT, parameters).setValue(5432);
        Parameters.getOrCreate(PostgisNGDataStoreFactory.DATABASE, parameters).setValue("base");
        Parameters.getOrCreate(PostgisNGDataStoreFactory.USER, parameters).setValue("user");
        Parameters.getOrCreate(PostgisNGDataStoreFactory.PASSWD, parameters).setValue("secret");
        
        final DataStore store = DataStoreFinder.getDataStore(parameters);
    }

}
