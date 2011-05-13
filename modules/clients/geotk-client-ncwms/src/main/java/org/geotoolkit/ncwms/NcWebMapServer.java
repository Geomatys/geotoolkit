/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
import java.util.logging.Logger;

import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.ncwms.v111.NcGetFeatureInfo111;
import org.geotoolkit.ncwms.v111.NcGetLegend111;
import org.geotoolkit.ncwms.v111.NcGetMap111;
import org.geotoolkit.ncwms.v130.NcGetFeatureInfo130;
import org.geotoolkit.ncwms.v130.NcGetLegend130;
import org.geotoolkit.ncwms.v130.NcGetMap130;
import org.geotoolkit.wms.xml.AbstractWMSCapabilities;
import org.geotoolkit.wms.xml.WMSVersion;
import org.geotoolkit.wms.WebMapServer;


/**
 * Generates ncWMS requests objects.
 *
 * @author Olivier Terral (Geomatys)
 * @module pending
 */
public class NcWebMapServer extends WebMapServer{

    private static final Logger LOGGER = Logging.getLogger(NcWebMapServer.class);

    /**
     * {@inheritDoc }
     */    
    public NcWebMapServer(final URL serverURL, final String version) {
        super(serverURL, WMSVersion.getVersion(version));
    }

    /**
     * {@inheritDoc }
     */
    public NcWebMapServer(final URL serverURL, final WMSVersion version) {
        super(serverURL, version, null);
    }

    /**
     * {@inheritDoc }
     */
    public NcWebMapServer(final URL serverURL, final String version, final AbstractWMSCapabilities capabilities) {
        super(serverURL, WMSVersion.getVersion(version), capabilities);
    }

    /**
     * {@inheritDoc }
     */
    public NcWebMapServer(final URL serverURL, final WMSVersion version, final AbstractWMSCapabilities capabilities) {
        super(serverURL, version, capabilities);
    }
    
    /**
     * Create a NcWebMapServer from a WebMapServer
     * 
     * @param wms a WebMapServer
     */
    public NcWebMapServer(final WebMapServer wms) {
        super(wms.getURL(), wms.getVersion());
    }
    
    /**
     * Create a NcWebMapServer from a WebMapServer and a Getcapabilities
     * 
     * @param wms a WebMapServer
     */
    public NcWebMapServer(final WebMapServer wms, final AbstractWMSCapabilities cap) {
        super(wms.getURL(), wms.getVersion(), cap);
    }
   
    /**
     * {@inheritDoc }
     */
    @Override
    public NcGetMapRequest createGetMap() {
        
        switch (getVersion()) {
            
            case v111:
                return new NcGetMap111(getURI().toString());
                
            case v130:
                return new NcGetMap130(getURI().toString());
                
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public NcGetLegendRequest createGetLegend(){
        
        switch (getVersion()) {
            
            case v111:
                return new NcGetLegend111(getURI().toString());
                
            case v130:
                return new NcGetLegend130(getURI().toString());
                
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    
    /**
     * {@inheritDoc }
     */
    @Override
    public NcGetFeatureInfoRequest createGetFeatureInfo() {
        
        switch (getVersion()) {
            
            case v111:
                return new NcGetFeatureInfo111(getURI().toString());
                
            case v130:
                return new NcGetFeatureInfo130(getURI().toString());
                
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }    
    
    /**
     * Returns the GetMetadata request object. 
     */
    public NcGetMetadataRequest createGetMetadata() {
        return new NcGetMetadata(getURI().toString());
    }
    
    /**
     * Returns the GetMetadata?item=menu request object. 
     */
    public NcGetMetadataRequest createGetMetadataMenu()  {
        final NcGetMetadataRequest request = createGetMetadata();
        request.setItem("menu");
        return request;
    }
    
    /**
     * Returns the GetTransect request object. 
     */
    public NcGetTransectRequest createGetTransect() {
        return new NcGetTransect(getURI().toString());
    }
    
    /**
     * Returns the GetVerticalProfile request object. 
     */
    public NcGetVerticalProfileRequest createGetVerticalProfile() {
        return new NcGetVerticalProfile(getURI().toString());
    }

}
