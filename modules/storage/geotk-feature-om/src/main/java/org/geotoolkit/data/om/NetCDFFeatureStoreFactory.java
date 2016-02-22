/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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

package org.geotoolkit.data.om;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import org.apache.sis.metadata.iso.DefaultIdentifier;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.apache.sis.metadata.iso.identification.DefaultServiceIdentification;
import org.apache.sis.metadata.iso.quality.DefaultConformanceResult;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.AbstractFeatureStoreFactory;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.storage.DataType;
import org.geotoolkit.storage.DefaultFactoryMetadata;
import org.geotoolkit.storage.FactoryMetadata;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.identification.Identification;
import org.opengis.metadata.quality.ConformanceResult;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 *  @author Guilhem Legal (Geomatys)
 */
public class NetCDFFeatureStoreFactory extends AbstractFeatureStoreFactory {

    /** factory identification **/
    public static final String NAME = "observationFile";
    public static final DefaultServiceIdentification IDENTIFICATION;
    static {
        IDENTIFICATION = new DefaultServiceIdentification();
        final Identifier id = new DefaultIdentifier(NAME);
        final DefaultCitation citation = new DefaultCitation(NAME);
        citation.setIdentifiers(Collections.singleton(id));
        IDENTIFICATION.setCitation(citation);
    }

    public static final ParameterDescriptor<String> IDENTIFIER = createFixedIdentifier(NAME);

    /**
     * Parameter for database port
     */
    public static final ParameterDescriptor<URI> FILE_PATH = new ParameterBuilder()
            .addName("path")
            .setRemarks("path")
            .setRequired(true)
            .create(URI.class, null);


    public static final ParameterDescriptorGroup PARAMETERS_DESCRIPTOR =
            new ParameterBuilder().addName("ObservationFileParameters").createGroup(IDENTIFIER,NAMESPACE,FILE_PATH);

    @Override
    public Identification getIdentification() {
        return IDENTIFICATION;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public CharSequence getDescription() {
        return Bundle.formatInternational(Bundle.Keys.NCdatastoreDescription);
    }

    @Override
    public CharSequence getDisplayName() {
        return Bundle.formatInternational(Bundle.Keys.NCdatastoreTitle);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ParameterDescriptorGroup getParametersDescriptor() {
        return PARAMETERS_DESCRIPTOR;
    }

    /**
     * Can process if input File exist and has "nc" extension
     *
     * @param params
     * @return
     */
    @Override
    public boolean canProcess(final ParameterValueGroup params) {
        boolean valid = super.canProcess(params);
        if(valid){
            URI value = (URI) params.parameter(FILE_PATH.getName().toString()).getValue();
            if (value != null) {
                try {
                    Path path = Paths.get(value);
                    return Files.exists(path) && "nc".equalsIgnoreCase(org.apache.sis.internal.storage.IOUtilities.extension(path));
                } catch (IllegalArgumentException| FileSystemNotFoundException e) {
                    return false;
                }
            }
        }
        return false;
    }

    @Override
    public FeatureStore open(final ParameterValueGroup params) throws DataStoreException {
        checkCanProcessWithError(params);
        
        try {
            final Path dataSource = IOUtilities.toPath(params.parameter(FILE_PATH.getName().toString()).getValue());
            return new NetCDFFeatureStore(params, dataSource);
        } catch (IOException e) {
            throw new DataStoreException(e.getLocalizedMessage(), e);
        }
    }

    @Override
    public FeatureStore create(final ParameterValueGroup params) throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ConformanceResult availability() {
        DefaultConformanceResult result =  new DefaultConformanceResult();
        result.setPass(true);
        return result;
    }
    
    @Override
    public FactoryMetadata getMetadata() {
        return new DefaultFactoryMetadata(DataType.VECTOR, true, false, false, false, GEOMS_ALL);
    }
}
