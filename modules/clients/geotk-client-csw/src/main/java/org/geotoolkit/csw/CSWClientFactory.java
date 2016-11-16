/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
 *    (C) 2012, Johann Sorel
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
package org.geotoolkit.csw;

import java.net.URL;
import java.util.Collections;
import org.geotoolkit.client.AbstractClientFactory;
import org.geotoolkit.client.Client;
import org.geotoolkit.csw.xml.CSWVersion;
import org.apache.sis.metadata.iso.DefaultIdentifier;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.apache.sis.metadata.iso.identification.DefaultServiceIdentification;
import org.apache.sis.parameter.ParameterBuilder;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.security.ClientSecurity;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.storage.DataType;
import org.geotoolkit.storage.DefaultFactoryMetadata;
import org.geotoolkit.storage.FactoryMetadata;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.identification.Identification;
import org.opengis.parameter.*;

/**
 * CSW Server factory.
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @module
 */
public class CSWClientFactory extends AbstractClientFactory{

    /** factory identification **/
    public static final String NAME = "csw";
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
     * Version, Mandatory.
     */
    public static final ParameterDescriptor<String> VERSION;
    static{
        final CSWVersion[] values = CSWVersion.values();
        final String[] validValues =  new String[values.length];
        for(int i=0;i<values.length;i++){
            validValues[i] = values[i].getCode();
        }
        VERSION = createVersionDescriptor(validValues, CSWVersion.v202.getCode());
    }

    public static final ParameterDescriptorGroup PARAMETERS =
            new ParameterBuilder().addName("CSWParameters").createGroup(IDENTIFIER,URL,VERSION,SECURITY,TIMEOUT);

    @Override
    public Identification getIdentification() {
        return IDENTIFICATION;
    }

    @Override
    public ParameterDescriptorGroup getParametersDescriptor() {
        return PARAMETERS;
    }

    @Override
    public CharSequence getDescription() {
        return Bundle.formatInternational(Bundle.Keys.serverDescription);
    }

    @Override
    public CharSequence getDisplayName() {
        return Bundle.formatInternational(Bundle.Keys.serverTitle);
    }

    @Override
    public FactoryMetadata getMetadata() {
        return new DefaultFactoryMetadata(DataType.METADATA, true, false, true);
    }

    @Override
    public CatalogServicesClient open(ParameterValueGroup params) throws DataStoreException {
        ensureCanProcess(params);
        final URL url = (URL)Parameters.getOrCreate(URL, params).getValue();
        final String version = (String)Parameters.getOrCreate(VERSION, params).getValue();
        ClientSecurity security = null;
        try{
            final ParameterValue val = params.parameter(SECURITY.getName().getCode());
            security = (ClientSecurity) val.getValue();
        }catch(ParameterNotFoundException ex){}

        return new CatalogServicesClient(url,security,version);
    }

}
