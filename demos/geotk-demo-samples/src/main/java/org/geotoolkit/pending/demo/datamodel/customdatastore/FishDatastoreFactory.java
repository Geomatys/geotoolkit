

package org.geotoolkit.pending.demo.datamodel.customdatastore;

import java.net.URISyntaxException;
import java.net.URL;

import org.geotoolkit.data.AbstractFileDataStoreFactory;
import org.geotoolkit.data.DataStore;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.storage.DataStoreException;

import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

public class FishDatastoreFactory extends AbstractFileDataStoreFactory{

    public static final ParameterDescriptorGroup PARAMETERS_DESCRIPTOR =
            new DefaultParameterDescriptorGroup("FishParameters",
                URLP,NAMESPACE);

    @Override
    public String getDescription() {
        return "Scientific fish files (*.fsh)";
    }

    @Override
    public ParameterDescriptorGroup getParametersDescriptor() {
        return PARAMETERS_DESCRIPTOR;
    }

    @Override
    public DataStore createDataStore(ParameterValueGroup params) throws DataStoreException {

        URL url = (URL) params.parameter(URLP.getName().toString()).getValue();
        String namespace = (String) params.parameter(NAMESPACE.getName().toString()).getValue();
        
        try {
            return new FishDataStore(url, namespace);
        } catch (URISyntaxException ex) {
            throw new DataStoreException(ex);
        }
    }

    @Override
    public DataStore createNewDataStore(ParameterValueGroup params) throws DataStoreException {
        throw new DataStoreException("Not supported.");
    }

    @Override
    public String[] getFileExtensions() {
        return new String[]{".fsh"};
    }

}
