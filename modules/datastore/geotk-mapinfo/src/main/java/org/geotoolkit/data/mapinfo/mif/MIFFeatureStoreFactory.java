package org.geotoolkit.data.mapinfo.mif;

import org.geotoolkit.data.AbstractFileFeatureStoreFactory;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.data.FileFeatureStoreFactory;
import org.geotoolkit.metadata.iso.DefaultIdentifier;
import org.geotoolkit.metadata.iso.citation.DefaultCitation;
import org.geotoolkit.metadata.iso.identification.DefaultServiceIdentification;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.storage.DataStoreException;
import org.opengis.metadata.identification.Identification;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class Description
 *
 * @author Alexis Manin (Geomatys)
 *         Date : 21/02/13
 */
public class MIFFeatureStoreFactory extends AbstractFileFeatureStoreFactory implements FileFeatureStoreFactory {

    public final static Logger LOGGER = Logger.getLogger(MIFFeatureStoreFactory.class.getName());

    /** factory identification **/
    public static final String NAME = "MIF-MID";
    public static final DefaultServiceIdentification IDENTIFICATION;
    static {
        IDENTIFICATION = new DefaultServiceIdentification();
        final DefaultIdentifier id = new DefaultIdentifier(NAME);
        final DefaultCitation citation = new DefaultCitation(NAME);
        citation.setIdentifiers(Collections.singleton(id));
        IDENTIFICATION.setCitation(citation);
    }
    public static final ParameterDescriptor<String> IDENTIFIER = createFixedIdentifier(NAME);

    public static final ParameterDescriptorGroup PARAMETERS_DESCRIPTOR =
            new DefaultParameterDescriptorGroup("MIFParameters", IDENTIFIER,URLP,NAMESPACE);

    @Override
    public CharSequence getDisplayName() {
        return NAME;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String[] getFileExtensions() {
        return new String[] {".mif"};
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Identification getIdentification() {
        return IDENTIFICATION;
    }

    @Override
    public ParameterDescriptorGroup getParametersDescriptor() {
        return PARAMETERS_DESCRIPTOR;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureStore open(ParameterValueGroup params) throws DataStoreException {
        checkCanProcessWithError(params);
        final URL filePath = (URL) params.parameter(URLP.getName().toString()).getValue();
        final String namespace = (String) params.parameter(NAMESPACE.getName().toString()).getValue();

        // Try to open a stream to ensure we've got an existing file.
        InputStream in = null;
        try {
            in = filePath.openStream();
        } catch (IOException ex) {
            throw new DataStoreException("Can't reach data pointed by given URL.", ex);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    LOGGER.log(Level.WARNING, "An input stream can't be closed.", e);
                }
            }
        }

        return new MIFFeatureStore(filePath, namespace);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureStore create(ParameterValueGroup params) throws DataStoreException {
        checkCanProcessWithError(params);
        final URL filePath = (URL) params.parameter(URLP.getName().toString()).getValue();
        final String namespace = (String) params.parameter(NAMESPACE.getName().toString()).getValue();

        return new MIFFeatureStore(filePath, namespace);
    }
}
