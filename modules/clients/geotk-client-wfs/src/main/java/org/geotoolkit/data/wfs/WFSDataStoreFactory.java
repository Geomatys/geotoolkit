/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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

package org.geotoolkit.data.wfs;

import java.io.Serializable;
import java.net.URL;
import java.util.Map;
import org.geotoolkit.client.AbstractServerFactory;
import org.geotoolkit.client.ServerFactory;
import org.geotoolkit.data.AbstractDataStoreFactory;
import org.geotoolkit.data.DataStore;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.security.ClientSecurity;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.wfs.xml.WFSVersion;
import org.opengis.parameter.*;

/**
 * Datastore factory for WFS client.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class WFSDataStoreFactory extends AbstractDataStoreFactory implements ServerFactory{

    /**
     * Version, Mandatory.
     */
    public static final ParameterDescriptor<WFSVersion> VERSION =
            new DefaultParameterDescriptor<WFSVersion>("version","Server version",WFSVersion.class,WFSVersion.v110,true);
    /**
     * Optional -post request
     */
    public static final ParameterDescriptor<Boolean> POST_REQUEST =
            new DefaultParameterDescriptor<Boolean>("post request","post request",Boolean.class,false,false);    

    public static final ParameterDescriptorGroup PARAMETERS_DESCRIPTOR =
            new DefaultParameterDescriptorGroup("WFSParameters",
                AbstractServerFactory.URL, VERSION, AbstractServerFactory.SECURITY, POST_REQUEST);

    /**
     * {@inheritDoc }
     */
    @Override
    public String getDescription() {
        return "OGC Web Feature Service datastore factory";
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ParameterDescriptorGroup getParametersDescriptor() {
        return PARAMETERS_DESCRIPTOR;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public DataStore createNew(final ParameterValueGroup params) throws DataStoreException {
        throw new DataStoreException("Can not create any new WFS DataStore");
    }

    @Override
    public WebFeatureServer create(Map<String, ? extends Serializable> params) throws DataStoreException {
        return (WebFeatureServer)super.create(params);
    }

    @Override
    public WebFeatureServer create(ParameterValueGroup params) throws DataStoreException {
        final URL url = (URL)Parameters.getOrCreate(AbstractServerFactory.URL, params).getValue();
        final WFSVersion version = (WFSVersion)Parameters.getOrCreate(VERSION, params).getValue();
        final boolean usePost = (Boolean)Parameters.getOrCreate(POST_REQUEST, params).getValue();
        ClientSecurity security = null;
        try{
            final ParameterValue val = params.parameter(AbstractServerFactory.SECURITY.getName().getCode());
            security = (ClientSecurity) val.getValue();
        }catch(ParameterNotFoundException ex){}
        
        return new WebFeatureServer(url,security,version,usePost);
    }

}
