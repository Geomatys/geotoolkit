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
package org.geotoolkit.wmts;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.coverage.AbstractCoverageStoreFactory;
import org.geotoolkit.coverage.CoverageStore;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.storage.DataStoreException;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class WMTSCoverageStoreFactory extends AbstractCoverageStoreFactory{

    private static URL DEFAULT_URL = null;
    static {
        try {
            DEFAULT_URL = new URL("http://server");
        } catch (MalformedURLException ex) {
            Logger.getLogger(WMTSCoverageStoreFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Mandatory - the serveur type
     */
    public static final ParameterDescriptor<String> TYPE =
            new DefaultParameterDescriptor<String>("servertype","Serveur type",String.class,"wmts",true);
    /**
     * Mandatory - the serveur url
     */
    public static final ParameterDescriptor<URL> URLP =
            new DefaultParameterDescriptor<URL>("url","Serveur URL",URL.class,DEFAULT_URL,true);
    /**
     * Mandatory - the serveur verion
     */
    public static final ParameterDescriptor<String> VERSION =
            new DefaultParameterDescriptor<String>("version","Serveur version",String.class,"1.0.0",true);

    public static final ParameterDescriptorGroup PARAMETERS_DESCRIPTOR =
            new DefaultParameterDescriptorGroup("WMTSParameters",
                TYPE,URLP,VERSION,NAMESPACE);
    
    @Override
    public String getDescription() {
        return "WMTS";
    }

    @Override
    public ParameterDescriptorGroup getParametersDescriptor() {
        return PARAMETERS_DESCRIPTOR;
    }

    @Override
    public boolean canProcess(ParameterValueGroup params) {
        final boolean valid = super.canProcess(params);
        if(!valid){
            return false;
        }
        final String type = params.parameter(TYPE.getName().getCode()).stringValue();
        return "wmts".equals(type);
    }

    @Override
    public CoverageStore createCoverageStore(ParameterValueGroup params) throws DataStoreException {
        if(!canProcess(params)){
            throw new DataStoreException("Can not process given parameters");
        }
        
        final URL url = (URL) params.parameter(URLP.getName().getCode()).getValue();
        final String version = (String) params.parameter(VERSION.getName().getCode()).getValue();
        return new WebMapTileServer(url, version);
    }

    @Override
    public CoverageStore createNewCoverageStore(ParameterValueGroup params) throws DataStoreException {
        throw new DataStoreException("Can not create new instance of WMTS store.");
    }
    
}
