

package org.geotoolkit.pending.demo.datamodel.customdatastore;

import java.util.Collections;
import org.geotoolkit.data.AbstractFeatureStoreFactory;
import org.geotoolkit.data.AbstractFileFeatureStoreFactory;
import org.geotoolkit.data.FeatureStore;
import org.apache.sis.metadata.iso.DefaultIdentifier;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.apache.sis.metadata.iso.identification.DefaultServiceIdentification;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.storage.DataType;
import org.geotoolkit.storage.DefaultFactoryMetadata;
import org.geotoolkit.storage.FactoryMetadata;

import org.opengis.metadata.Identifier;
import org.opengis.metadata.identification.Identification;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

public class FishDatastoreFactory extends AbstractFileFeatureStoreFactory{

    /** factory identification **/
    public static final String NAME = "fish";
    public static final DefaultServiceIdentification IDENTIFICATION;
    static {
        IDENTIFICATION = new DefaultServiceIdentification();
        final Identifier id = new DefaultIdentifier(NAME);
        final DefaultCitation citation = new DefaultCitation(NAME);
        citation.setIdentifiers(Collections.singleton(id));
        IDENTIFICATION.setCitation(citation);
    }
    
    public static final ParameterDescriptor<String> IDENTIFIER = new ParameterBuilder()
            .addName(AbstractFeatureStoreFactory.IDENTIFIER.getName().getCode())
            .setRemarks(AbstractFeatureStoreFactory.IDENTIFIER.getRemarks())
            .setRequired(true)
            .create(String.class, NAME);
    
    public static final ParameterDescriptorGroup PARAMETERS_DESCRIPTOR =
            new ParameterBuilder().addName("FishParameters").createGroup(IDENTIFIER, PATH,NAMESPACE);

    @Override
    public Identification getIdentification() {
        return IDENTIFICATION;
    }

    @Override
    public String getDescription() {
        return "Scientific fish files (*.fsh)";
    }

    @Override
    public ParameterDescriptorGroup getParametersDescriptor() {
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
