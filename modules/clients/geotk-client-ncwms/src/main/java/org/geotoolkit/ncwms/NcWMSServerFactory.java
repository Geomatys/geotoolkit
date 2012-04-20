/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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
package org.geotoolkit.ncwms;

import java.net.URL;
import java.util.Collections;
import org.geotoolkit.client.AbstractServerFactory;
import org.geotoolkit.client.Server;
import org.geotoolkit.metadata.iso.DefaultIdentifier;
import org.geotoolkit.metadata.iso.citation.DefaultCitation;
import org.geotoolkit.metadata.iso.identification.DefaultServiceIdentification;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.security.ClientSecurity;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.wms.xml.WMSVersion;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.identification.Identification;
import org.opengis.parameter.*;

/**
 * NcWMS Server factory.
 * 
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class NcWMSServerFactory extends AbstractServerFactory{
        
    /** factory identification **/
    public static final String NAME = "ncWMS";
    public static final DefaultServiceIdentification IDENTIFICATION;
    static {
        IDENTIFICATION = new DefaultServiceIdentification();
        final Identifier id = new DefaultIdentifier(NAME);
        final DefaultCitation citation = new DefaultCitation(NAME);
        citation.setIdentifiers(Collections.singleton(id));
        IDENTIFICATION.setCitation(citation);
    }

    public static final ParameterDescriptor<String> IDENTIFIER = new DefaultParameterDescriptor<String>(
                    AbstractServerFactory.IDENTIFIER.getName().getCode(),
                    AbstractServerFactory.IDENTIFIER.getRemarks(), String.class,NAME,true);
    
    /**
     * Version, Mandatory.
     */
    public static final ParameterDescriptor<WMSVersion> VERSION =
            new DefaultParameterDescriptor<WMSVersion>("version","Server version",WMSVersion.class,WMSVersion.v130,true);
    
    public static final ParameterDescriptorGroup PARAMETERS = 
            new DefaultParameterDescriptorGroup("NcWMSParameters", IDENTIFIER,URL,VERSION,SECURITY);
    
    @Override
    public Identification getIdentification() {
        return IDENTIFICATION;
    }
    
    @Override
    public ParameterDescriptorGroup getParametersDescriptor() {
        return PARAMETERS;
    }

    @Override
    public Server create(ParameterValueGroup params) throws DataStoreException {
        final URL url = (URL)Parameters.getOrCreate(URL, params).getValue();
        final WMSVersion version = (WMSVersion)Parameters.getOrCreate(VERSION, params).getValue();
        ClientSecurity security = null;
        try{
            final ParameterValue val = params.parameter(SECURITY.getName().getCode());
            security = (ClientSecurity) val.getValue();
        }catch(ParameterNotFoundException ex){}
        
        return new NcWebMapServer(url,security,version,null);
    }
    
}
