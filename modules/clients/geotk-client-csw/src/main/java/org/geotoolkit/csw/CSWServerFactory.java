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
package org.geotoolkit.csw;

import java.net.URL;
import org.geotoolkit.client.AbstractServerFactory;
import org.geotoolkit.client.Server;
import org.geotoolkit.csw.xml.CSWVersion;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.security.ClientSecurity;
import org.geotoolkit.storage.DataStoreException;
import org.opengis.parameter.*;

/**
 * CSW Server factory.
 * 
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class CSWServerFactory extends AbstractServerFactory{

    /**
     * Version, Mandatory.
     */
    public static final ParameterDescriptor<CSWVersion> VERSION =
            new DefaultParameterDescriptor<CSWVersion>("version","Server version",CSWVersion.class,CSWVersion.v202,true);
    
    public static final ParameterDescriptorGroup PARAMETERS = 
            new DefaultParameterDescriptorGroup("CSWParameters", URL,VERSION,SECURITY);
    
    @Override
    public ParameterDescriptorGroup getParametersDescriptor() {
        return PARAMETERS;
    }

    @Override
    public Server create(ParameterValueGroup params) throws DataStoreException {
        final URL url = (URL)Parameters.getOrCreate(URL, params).getValue();
        final CSWVersion version = (CSWVersion)Parameters.getOrCreate(VERSION, params).getValue();
        ClientSecurity security = null;
        try{
            final ParameterValue val = params.parameter(SECURITY.getName().getCode());
            security = (ClientSecurity) val.getValue();
        }catch(ParameterNotFoundException ex){}
        
        return new CatalogServicesServer(url,security,version);
    }
    
}
