

package org.geotoolkit.pending.demo.datamodel.customdatastore;

import java.net.URI;
import org.apache.sis.storage.base.Capability;
import org.apache.sis.storage.base.StoreMetadata;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStoreProvider;
import org.apache.sis.storage.ProbeResult;
import org.apache.sis.storage.StorageConnector;
import org.geotoolkit.storage.ResourceType;
import org.geotoolkit.storage.StoreMetadataExt;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

@StoreMetadata(
        formatName = FishProvider.NAME,
        capabilities = {Capability.READ},
        resourceTypes = {})
@StoreMetadataExt(resourceTypes = ResourceType.VECTOR)
public class FishProvider extends DataStoreProvider {

    /** factory identification **/
    public static final String NAME = "fish";

    public static final ParameterDescriptor<URI> PATH = new ParameterBuilder()
            .addName(LOCATION)
            .setRequired(true)
            .create(URI.class, null);

    public static final ParameterDescriptorGroup PARAMETERS_DESCRIPTOR =
            new ParameterBuilder().addName(NAME).createGroup(PATH);

    @Override
    public String getShortName() {
        return NAME;
    }

    @Override
    public ParameterDescriptorGroup getOpenParameters() {
        return PARAMETERS_DESCRIPTOR;
    }

    @Override
    public ProbeResult probeContent(StorageConnector connector) throws DataStoreException {
        throw new DataStoreException("Not supported.");
    }

    @Override
    public DataStore open(StorageConnector connector) throws DataStoreException {
        throw new DataStoreException("Not supported.");
    }

    @Override
    public FishStore open(ParameterValueGroup params) throws DataStoreException {
        return new FishStore(params);
    }

}
