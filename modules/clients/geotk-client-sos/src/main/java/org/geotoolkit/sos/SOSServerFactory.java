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
package org.geotoolkit.sos;

import java.net.URL;
import org.geotoolkit.client.AbstractServerFactory;
import org.geotoolkit.client.Server;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.security.ClientSecurity;
import org.geotoolkit.sos.xml.SOSVersion;
import org.geotoolkit.storage.DataStoreException;
import org.opengis.parameter.*;

/**
 * Sensor Observation Service Server factory.
 * 
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class SOSServerFactory extends AbstractServerFactory{

    /**
     * Version, Mandatory.
     */
    public static final ParameterDescriptor<SOSVersion> VERSION =
            new DefaultParameterDescriptor<SOSVersion>("version","Server version",SOSVersion.class,SOSVersion.v100,true);
    
    public static final ParameterDescriptorGroup PARAMETERS = 
            new DefaultParameterDescriptorGroup("SOSParameters", URL,VERSION,SECURITY);
    
    @Override
    public ParameterDescriptorGroup getParametersDescriptor() {
        return PARAMETERS;
    }

    @Override
    public Server create(ParameterValueGroup params) throws DataStoreException {
        final URL url = (URL)Parameters.getOrCreate(URL, params).getValue();
        final SOSVersion version = (SOSVersion)Parameters.getOrCreate(VERSION, params).getValue();
        ClientSecurity security = null;
        try{
            final ParameterValue val = params.parameter(SECURITY.getName().getCode());
            security = (ClientSecurity) val.getValue();
        }catch(ParameterNotFoundException ex){}
        
        return new SensorObservationServiceServer(url,security,version);
    }
    
}
