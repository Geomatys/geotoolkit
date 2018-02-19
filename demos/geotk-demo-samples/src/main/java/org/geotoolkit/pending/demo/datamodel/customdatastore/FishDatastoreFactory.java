

package org.geotoolkit.pending.demo.datamodel.customdatastore;

import org.geotoolkit.data.AbstractFeatureStoreFactory;
import org.geotoolkit.data.AbstractFileFeatureStoreFactory;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.storage.DataType;
import org.geotoolkit.storage.DefaultFactoryMetadata;
import org.geotoolkit.storage.FactoryMetadata;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

public class FishDatastoreFactory extends AbstractFileFeatureStoreFactory{

    /** factory identification **/
    public static final String NAME = "fish";

    public static final ParameterDescriptor<String> IDENTIFIER = new ParameterBuilder()
            .addName(AbstractFeatureStoreFactory.IDENTIFIER.getName().getCode())
            .setRemarks(AbstractFeatureStoreFactory.IDENTIFIER.getRemarks())
            .setRequired(true)
            .create(String.class, NAME);

    public static final ParameterDescriptorGroup PARAMETERS_DESCRIPTOR =
            new ParameterBuilder().addName(NAME).createGroup(IDENTIFIER, PATH);

    @Override
    public String getDescription() {
        return "Scientific fish files (*.fsh)";
    }

    @Override
    public ParameterDescriptorGroup getOpenParameters() {
        return PARAMETERS_DESCRIPTOR;
    }

    @Override
    public FishFeatureStore open(ParameterValueGroup params) throws DataStoreException {
        ensureCanProcess(params);
        return new FishFeatureStore(params);
    }

    @Override
    public FishFeatureStore create(ParameterValueGroup params) throws DataStoreException {
        throw new DataStoreException("Not supported.");
    }

    @Override
    public String[] getFileExtensions() {
        return new String[]{".fsh"};
    }

    @Override
    public FactoryMetadata getMetadata() {
        return new DefaultFactoryMetadata(DataType.VECTOR, true, false, false, false, GEOMS_ALL);
    }

}
