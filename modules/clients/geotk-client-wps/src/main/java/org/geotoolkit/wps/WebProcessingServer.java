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
package org.geotoolkit.wps;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.Unmarshaller;

import org.geotoolkit.client.AbstractServer;
import org.geotoolkit.security.ClientSecurity;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.wps.v100.DescribeProcess100;
import org.geotoolkit.wps.v100.Execute100;
import org.geotoolkit.wps.v100.GetCapabilities100;
import org.geotoolkit.wps.xml.WPSMarshallerPool;
import org.geotoolkit.wps.xml.v100.WPSCapabilitiesType;

/**
 * WPS server, used to aquiere capabilites and requests process.
 * @author Quentin Boileau
 * @modul pending
 */
public class WebProcessingServer extends AbstractServer{
    
    private static final Logger LOGGER = Logging.getLogger(WebProcessingServer.class);
    
    private final WPSVersion version;
    private WPSCapabilitiesType capabilities;
    
    /**
     * Static enumeration of WPS server versions. 
     */
    public static enum WPSVersion{

        v100("1.0.0");
        private final String code;

        private WPSVersion(final String code) {
            this.code = code;
        }
        public String getCode(){
            return code;
        }
        
    }
    
   /**
     * Constructor
     * @param serverURL
     * @param version 
     */  
   public WebProcessingServer(final URL serverURL, final String version) {
        this(serverURL,null,version);
    }
   
   /**
     * Constructor
     * @param serverURL
     * @param version 
     */  
   public WebProcessingServer(final URL serverURL, final ClientSecurity security, final String version) {
       super(serverURL,security);
        if(version.equals("1.0.0")){
            this.version = WPSVersion.v100;
        }else{
            throw new IllegalArgumentException("Unkonwed version : "+ version);
        }
        this.capabilities = null;
    }
    
    /**
     * @return WPSVersion : currently used version for this server
     */
    public WPSVersion getVersion() {
        return version;
    }
    
    /**
     * @return WPSCapabilitiesType : WPS server capabilities
     */
    public WPSCapabilitiesType getCapabilities() {

        if (capabilities != null) {
            return capabilities;
        }
        //Thread to prevent infinite request on a server
        final Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    final URL url = createGetCapabilities().getURL();
                    final Unmarshaller unmarhaller = WPSMarshallerPool.getInstance().acquireUnmarshaller();
                    capabilities = (WPSCapabilitiesType)unmarhaller.unmarshal(url);
                } catch (Exception ex) {
                    capabilities = null;
                    try {
                        LOGGER.log(Level.WARNING, "Wrong URL, the server doesn't answer : " + createGetCapabilities().getURL().toString(), ex);
                    } catch (MalformedURLException ex1) {
                        LOGGER.log(Level.WARNING, "Malformed URL, the server doesn't answer. ", ex1);
                    }
                }
            }
        };
        thread.start();
        final long start = System.currentTimeMillis();
        try {
            thread.join(10000);
        } catch (InterruptedException ex) {
            LOGGER.log(Level.WARNING, "The thread to obtain GetCapabilities doesn't answer.", ex);
        }
        if ((System.currentTimeMillis() - start) > 10000) {
            LOGGER.log(Level.WARNING, "TimeOut error, the server takes too much time to answer. ");
        }

        return capabilities;
    }
    
   /**
     * Create a getCapabilities request.
     * @return GetCapabilitiesRequest : getCapabilities request.
     */
    public GetCapabilitiesRequest createGetCapabilities() {

        switch (version) {
            case v100:
                return new GetCapabilities100(serverURL.toString(),securityManager);
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    /**
     * Create a describe process request
     * @return DescribeProcessRequest : describe process request.
     */
    public DescribeProcessRequest createDescribeProcess(){
        switch (version) {
            case v100:
                return new DescribeProcess100(serverURL.toString(),securityManager);
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }
    
    /**
     * Create an execute request
     * @return ExecuteRequest : execute request.
     */
    public ExecuteRequest createExecute(){
        switch (version) {
            case v100:
                return new Execute100(serverURL.toString(),securityManager);
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }
}
