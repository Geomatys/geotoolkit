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

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotoolkit.data.wfs.v110.DescribeFeatureType110;
import org.geotoolkit.data.wfs.v110.GetCapabilities110;
import org.geotoolkit.data.wfs.v110.GetFeature110;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.wfs.xml.WFSBindingUtilities;
import org.geotoolkit.wfs.xml.WFSVersion;
import org.geotoolkit.wfs.xml.v110.WFSCapabilitiesType;

/**
 * WFS server, used to aquiere capabilites and requests objects.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class WebFeatureServer {

    private static final Logger LOGGER = Logging.getLogger(WebFeatureServer.class);

    private final WFSVersion version;
    private final URL serverURL;
    private WFSCapabilitiesType capabilities;

    public WebFeatureServer(URL serverURL, String version) {
        if(version.equals("1.1.0")){
            this.version = WFSVersion.v110;
        }else{
            throw new IllegalArgumentException("unkonwed version : "+ version);
        }
        this.serverURL = serverURL;
        this.capabilities = null;
    }

    /**
     * @return URI : server URI
     */
    public URI getURI(){
        try {
            return serverURL.toURI();
        } catch (URISyntaxException ex) {
            Logger.getLogger(WebFeatureServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * @return WFSCapabilitiesType : WFS server capabilities
     */
    public WFSCapabilitiesType getCapabilities() {

        if (capabilities != null) {
            return capabilities;
        }
        //Thread to prevent infinite request on a server
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    capabilities = WFSBindingUtilities.unmarshall(createGetCapabilities().getURL(), version);
                } catch (Exception ex) {
                    capabilities = null;
                    try {
                        LOGGER.log(Level.SEVERE, "Wrong URL, the server doesn't answer : " + createGetCapabilities().getURL().toString(), ex);
                    } catch (MalformedURLException ex1) {
                        LOGGER.log(Level.SEVERE, "Malformed URL, the server doesn't answer. ", ex1);
                    }
                }
            }
        };
        thread.start();
        long start = System.currentTimeMillis();
        try {
            thread.join(10000);
        } catch (InterruptedException ex) {
            LOGGER.log(Level.SEVERE, "The thread to obtain GetCapabilities doesn't answer.", ex);
        }
        if ((System.currentTimeMillis() - start) > 10000) {
            LOGGER.log(Level.SEVERE, "TimeOut error, the server takes too much time to answer. ");
        }

        return capabilities;
    }

    /**
     * @return WFSVersion : currently used version for this server
     */
    public WFSVersion getVersion() {
        return version;
    }

    /**
     * Create a getCapabilities request.
     * @return GetCapabilitiesRequest : getCapabilities request.
     */
    public GetCapabilitiesRequest createGetCapabilities() {

        switch (version) {
            case v110:
                return new GetCapabilities110(serverURL.toString());
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    /**
     * Create a describe feature request
     * @return DescribeFeatureTypeRequest : describe feature request.
     */
    public DescribeFeatureTypeRequest createDescribeFeatureType(){
        switch (version) {
            case v110:
                return new DescribeFeatureType110(serverURL.toString());
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    /**
     * Create a get feature request
     * @return GetFeatureRequest : get feature request.
     */
    public GetFeatureRequest createGetFeature(){
        switch (version) {
            case v110:
                return new GetFeature110(serverURL.toString());
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

}
