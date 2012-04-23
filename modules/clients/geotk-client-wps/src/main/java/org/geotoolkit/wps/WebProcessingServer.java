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
import org.geotoolkit.client.ServerFactory;
import org.geotoolkit.client.ServerFinder;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.security.ClientSecurity;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.wps.v100.DescribeProcess100;
import org.geotoolkit.wps.v100.Execute100;
import org.geotoolkit.wps.v100.GetCapabilities100;
import org.geotoolkit.wps.xml.WPSMarshallerPool;
import org.geotoolkit.wps.xml.v100.WPSCapabilitiesType;
import org.opengis.parameter.ParameterValueGroup;

/**
 * WPS server, used to aquiere capabilites and requests process.
 * @author Quentin Boileau
 * @modul pending
 */
public class WebProcessingServer extends AbstractServer{
    
    private static final Logger LOGGER = Logging.getLogger(WebProcessingServer.class);
    
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
        
        /**
         * Get the version enum from the string code.
         *
         * @param version
         * @return The enum which matches with the given string.
         * @throws IllegalArgumentException if the enum class does not contain any enum types
         *                                  for the given string value.
         */
        public static WPSVersion getVersion(final String version) {
            for (WPSVersion vers : values()) {
                if (vers.getCode().equals(version)) {
                    return vers;
                }
            }

            try{
                return WPSVersion.valueOf(version);
            }catch(IllegalArgumentException ex){}

            throw new IllegalArgumentException("The given string \""+ version +"\" is not " +
                    "a known version.");
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
        super(create(WPSServerFactory.PARAMETERS, serverURL, security));
       if (version.equals("1.0.0")) {
            Parameters.getOrCreate(WPSServerFactory.VERSION, parameters).setValue(WPSVersion.v100);
       } else {
           throw new IllegalArgumentException("Unkonwed version : " + version);
       }
       this.capabilities = null;
    }
   
   /**
     * Constructor
     * @param serverURL
     * @param version 
     */  
   public WebProcessingServer(final URL serverURL, final ClientSecurity security, final WPSVersion version) {
        super(create(WPSServerFactory.PARAMETERS, serverURL, security));
        if (version == null) {
            throw new IllegalArgumentException("Unkonwed version : " + version);
        }
        Parameters.getOrCreate(WPSServerFactory.VERSION, parameters).setValue(version);
        this.capabilities = null;
    }

    public WebProcessingServer(ParameterValueGroup params) {
        super(params);
    }

    @Override
    public ServerFactory getFactory() {
        return ServerFinder.getFactory(WPSServerFactory.NAME);
    }
    
    /**
     * @return WPSVersion : currently used version for this server
     */
    public WPSVersion getVersion() {
        return WPSVersion.getVersion(Parameters.value(WPSServerFactory.VERSION, parameters));
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

        switch (getVersion()) {
            case v100:
                return new GetCapabilities100(serverURL.toString(),getClientSecurity());
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    /**
     * Create a describe process request
     * @return DescribeProcessRequest : describe process request.
     */
    public DescribeProcessRequest createDescribeProcess(){
        switch (getVersion()) {
            case v100:
                return new DescribeProcess100(serverURL.toString(),getClientSecurity());
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }
    
    /**
     * Create an execute request
     * @return ExecuteRequest : execute request.
     */
    public ExecuteRequest createExecute(){
        switch (getVersion()) {
            case v100:
                return new Execute100(serverURL.toString(),getClientSecurity());
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }
}
