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

package org.geotoolkit.observation.file;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import org.apache.sis.metadata.iso.DefaultIdentifier;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.apache.sis.metadata.iso.identification.DefaultServiceIdentification;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.observation.AbstractObservationStoreFactory;
import org.geotoolkit.observation.Bundle;
import org.geotoolkit.observation.ObservationStore;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.identification.Identification;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class FileObservationStoreFactory extends AbstractObservationStoreFactory {

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
     * url to the file.
     */
    public static final ParameterDescriptor<URI> FILE_PATH =  new ParameterBuilder()
            .addName("path")
            .addName(Bundle.formatInternational(Bundle.Keys.paramURLAlias))
            .setRemarks(Bundle.formatInternational(Bundle.Keys.paramURLRemarks))
            .setRequired(true)
            .create(URI.class, null);
    
    public static final ParameterDescriptorGroup PARAMETERS_DESCRIPTOR =
            new ParameterBuilder().addName("ObservationFileParameters").createGroup(
                IDENTIFIER,NAMESPACE,FILE_PATH);
    
    @Override
    public Identification getIdentification() {
        return IDENTIFICATION;
    }

    @Override
    public ParameterDescriptorGroup getParametersDescriptor() {
        return PARAMETERS_DESCRIPTOR;
    }

    @Override
    public ObservationStore open(ParameterValueGroup params) throws DataStoreException {
        try {
            return new FileObservationStore(params);
        } catch (IOException | URISyntaxException e) {
            throw new DataStoreException(e.getLocalizedMessage(), e);
        }
    }

    @Override
    public ObservationStore create(ParameterValueGroup params) throws DataStoreException {
        try {
            return new FileObservationStore(params);
        } catch (IOException | URISyntaxException e) {
            throw new DataStoreException(e.getLocalizedMessage(), e);
        }
    }
    
}
