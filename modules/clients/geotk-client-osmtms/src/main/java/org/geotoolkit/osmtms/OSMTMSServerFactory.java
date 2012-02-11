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
package org.geotoolkit.osmtms;

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
 * OSM TMS Server factory.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class OSMTMSServerFactory extends AbstractServerFactory implements CoverageStoreFactory{
    
    /**
     * Mandatory - the serveur max zoom level
     */
    public static final ParameterDescriptor<Integer> MAX_ZOOM_LEVEL =
            new DefaultParameterDescriptor<Integer>("maxZoomLevel","Maximum zoom level",Integer.class,18,true);

    public static final ParameterDescriptorGroup PARAMETERS =
            new DefaultParameterDescriptorGroup("OSMTMSParameters",
                URL,MAX_ZOOM_LEVEL,SECURITY);
    
    @Override
    public ParameterDescriptorGroup getParametersDescriptor() {
        return PARAMETERS;
    }

    @Override
    public OSMTileMapServer create(ParameterValueGroup params) throws DataStoreException {
        final URL url = (URL)Parameters.getOrCreate(URL, params).getValue();
        final int zoom = (Integer)Parameters.getOrCreate(MAX_ZOOM_LEVEL, params).getValue();
        ClientSecurity security = null;
        try{
            final ParameterValue val = params.parameter(SECURITY.getName().getCode());
            security = (ClientSecurity) val.getValue();
        }catch(ParameterNotFoundException ex){}
        
        return new OSMTileMapServer(url,security,zoom);
    }

    @Override
    public CoverageStore createCoverageStore(Map<String, ? extends Serializable> params) throws DataStoreException {
        try{
            return createCoverageStore(FeatureUtilities.toParameter(params,getParametersDescriptor()));
        }catch(InvalidParameterValueException ex){
            throw new DataStoreException(ex);
        }
    }

    @Override
    public CoverageStore createCoverageStore(ParameterValueGroup params) throws DataStoreException {
        return create(params);
    }

    @Override
    public CoverageStore createNewCoverageStore(Map<String, ? extends Serializable> params) throws DataStoreException {
        try{
            return createNewCoverageStore(FeatureUtilities.toParameter(params,getParametersDescriptor()));
        }catch(InvalidParameterValueException ex){
            throw new DataStoreException(ex);
        }
    }

    @Override
    public CoverageStore createNewCoverageStore(ParameterValueGroup params) throws DataStoreException {
        throw new DataStoreException("Can not create new OSM TMS coverage store.");
    }
    
}
