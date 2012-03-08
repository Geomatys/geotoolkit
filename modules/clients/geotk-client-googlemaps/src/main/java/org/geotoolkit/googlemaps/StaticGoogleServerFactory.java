/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.googlemaps;

import java.io.Serializable;
import java.net.URL;
import java.util.Map;
import org.geotoolkit.client.AbstractServerFactory;
import org.geotoolkit.coverage.CoverageStore;
import org.geotoolkit.coverage.CoverageStoreFactory;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.security.ClientSecurity;
import org.geotoolkit.storage.DataStoreException;
import org.opengis.parameter.*;

/**
 * Google Static Map Server factory.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class StaticGoogleServerFactory extends AbstractServerFactory implements CoverageStoreFactory{
    
    /**
     * Mandatory - the map type
     */
    public static final ParameterDescriptor<String> MAPTYPE =
            new DefaultParameterDescriptor<String>("mapType","Map type",String.class,GetMapRequest.TYPE_ROADMAP,true);

    public static final ParameterDescriptorGroup PARAMETERS =
            new DefaultParameterDescriptorGroup("GSParameters",
                URL,MAPTYPE,SECURITY,IMAGE_CACHE);
    
    @Override
    public ParameterDescriptorGroup getParametersDescriptor() {
        return PARAMETERS;
    }

    @Override
    public StaticGoogleMapsServer create(ParameterValueGroup params) throws DataStoreException {
        final URL url = (URL)Parameters.getOrCreate(URL, params).getValue();
        final String key = (String)Parameters.getOrCreate(MAPTYPE, params).getValue();
        ClientSecurity security = null;
        try{
            final ParameterValue val = params.parameter(SECURITY.getName().getCode());
            security = (ClientSecurity) val.getValue();
        }catch(ParameterNotFoundException ex){}
        
        boolean cacheImage = false;
        try{
            final ParameterValue val = params.parameter(IMAGE_CACHE.getName().getCode());
            cacheImage = (Boolean) val.getValue();
        }catch(ParameterNotFoundException ex){}
        
        return new StaticGoogleMapsServer(url,key,security,cacheImage);
    }

    @Override
    public StaticGoogleMapsServer create(Map<String, ? extends Serializable> params) throws DataStoreException {
        return (StaticGoogleMapsServer) super.create(params);
    }

    @Override
    public CoverageStore createNew(Map<String, ? extends Serializable> params) throws DataStoreException {
        try{
            return createNew(FeatureUtilities.toParameter(params,getParametersDescriptor()));
        }catch(InvalidParameterValueException ex){
            throw new DataStoreException(ex);
        }
    }

    @Override
    public CoverageStore createNew(ParameterValueGroup params) throws DataStoreException {
        throw new DataStoreException("Can not create new Google Static coverage store.");
    }
    
}
