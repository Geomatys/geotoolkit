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

package org.geotoolkit.data.om.netcdf;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import org.apache.sis.metadata.iso.DefaultIdentifier;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.apache.sis.metadata.iso.identification.DefaultServiceIdentification;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.FeatureStoreFactory;
import org.geotoolkit.observation.AbstractObservationStoreFactory;
import org.geotoolkit.observation.Bundle;
import org.geotoolkit.storage.DataType;
import org.geotoolkit.storage.DefaultFactoryMetadata;
import org.geotoolkit.storage.FactoryMetadata;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.identification.Identification;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class NetcdfObservationStoreFactory extends AbstractObservationStoreFactory implements FeatureStoreFactory {

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
    public ParameterDescriptorGroup getOpenParameters() {
        return PARAMETERS_DESCRIPTOR;
    }

    @Override
    public FactoryMetadata getMetadata() {
        return new DefaultFactoryMetadata(DataType.SENSOR, true, false, false);
    }

    @Override
    public NetcdfObservationStore open(ParameterValueGroup params) throws DataStoreException {
        return new NetcdfObservationStore(params);
    }

    @Override
    public NetcdfObservationStore create(ParameterValueGroup params) throws DataStoreException {
        return new NetcdfObservationStore(params);
    }

}
