/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.data.mapinfo.mif;

import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.AbstractFileFeatureStoreFactory;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.data.FileFeatureStoreFactory;
import org.apache.sis.metadata.iso.DefaultIdentifier;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.apache.sis.metadata.iso.identification.DefaultServiceIdentification;
import org.geotoolkit.nio.IOUtilities;
import org.opengis.metadata.identification.Identification;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Collections;
import java.util.logging.Logger;
import org.apache.sis.parameter.ParameterBuilder;
import org.geotoolkit.storage.DataType;
import org.geotoolkit.storage.DefaultFactoryMetadata;
import org.geotoolkit.storage.FactoryMetadata;
import org.apache.sis.util.logging.Logging;

/**
 * Class Description
 *
 * @author Alexis Manin (Geomatys)
 *         Date : 21/02/13
 */
public class MIFFeatureStoreFactory extends AbstractFileFeatureStoreFactory implements FileFeatureStoreFactory {

    public final static Logger LOGGER = Logging.getLogger("org.geotoolkit.data.mapinfo.mif");

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
            new ParameterBuilder().addName("MIFParameters").createGroup(IDENTIFIER, PATH,NAMESPACE);

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
        final URI filePath = (URI) params.parameter(PATH.getName().toString()).getValue();
        final String namespace = (String) params.parameter(NAMESPACE.getName().toString()).getValue();

        // Try to open a stream to ensure we've got an existing file.
        try (InputStream stream = IOUtilities.open(filePath)){
            //do nothing (stream can be created)
        } catch (IOException ex) {
            throw new DataStoreException("Can't reach data pointed by given URI.", ex);
        }

        return new MIFFeatureStore(filePath, namespace);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureStore create(ParameterValueGroup params) throws DataStoreException {
        checkCanProcessWithError(params);
        final URI filePath = (URI) params.parameter(PATH.getName().toString()).getValue();
        final String namespace = (String) params.parameter(NAMESPACE.getName().toString()).getValue();

        return new MIFFeatureStore(filePath, namespace);
    }

    @Override
    public FactoryMetadata getMetadata() {
        return new DefaultFactoryMetadata(DataType.VECTOR, true, false, false, false, GEOMS_ALL);
    }
}
