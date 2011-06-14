/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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
package org.geotoolkit.wcs;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.Unmarshaller;

import org.geotoolkit.client.AbstractServer;
import org.geotoolkit.security.ClientSecurity;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.wcs.v100.DescribeCoverage100;
import org.geotoolkit.wcs.v100.GetCapabilities100;
import org.geotoolkit.wcs.v100.GetCoverage100;
import org.geotoolkit.wcs.xml.WCSMarshallerPool;
import org.geotoolkit.wcs.xml.WCSVersion;
import org.geotoolkit.wcs.xml.v100.WCSCapabilitiesType;


/**
 * WCS server, used to aquiere capabilites and requests objects.
 *
 * @author Cédric Briançon (Geomatys)
 * @module pending
 */
public class WebCoverageServer extends AbstractServer {

    private static final Logger LOGGER = Logging.getLogger(WebCoverageServer.class);

    private final WCSVersion version;

    private WCSCapabilitiesType capabilities;

    public WebCoverageServer(final URL serverURL, final String version) {
        this(serverURL,null,version);
    }
    
    public WebCoverageServer(final URL serverURL, final ClientSecurity security, final String version) {
        super(serverURL,security);
        if (version.equals("1.0.0")) {
            this.version = WCSVersion.v100;
        } else {
            throw new IllegalArgumentException("unkonwed version : " + version);
        }
    }

    /**
     * Returns the {@linkplain WCSCapabilitiesType capabilities} response for this
     * server.
     */
    public WCSCapabilitiesType getCapabilities() {

        if (capabilities != null) {
            return capabilities;
        }
        //Thread to prevent infinite request on a server
        final Thread thread = new Thread() {
            @Override
            public void run() {
                Unmarshaller unmarshaller = null;
                try {
                    unmarshaller = WCSMarshallerPool.getInstance().acquireUnmarshaller();
                    final GetCapabilitiesRequest request = createGetCapabilities();
                    capabilities = (WCSCapabilitiesType) unmarshaller.unmarshal(request.getURL());
                } catch (Exception ex) {
                    capabilities = null;
                    try {
                        LOGGER.log(Level.WARNING, "Wrong URL, the server doesn't answer : " +
                                createGetCapabilities().getURL().toString(), ex);
                    } catch (MalformedURLException ex1) {
                        LOGGER.log(Level.WARNING, "Malformed URL, the server doesn't answer. ", ex1);
                    }
                }finally{
                    if(unmarshaller != null){
                        WCSMarshallerPool.getInstance().release(unmarshaller);
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
     * Create a describe coverage request.
     * @return DescribeCoverageRequest : describe coverage request.
     */
    public DescribeCoverageRequest createDescribeCoverage() {

        switch (version) {
            case v100:
                return new DescribeCoverage100(serverURL.toString(),securityManager);
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
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
     * Create a getCoverage request.
     * @return GetCoverageRequest : getCoverage request.
     */
    public GetCoverageRequest createGetCoverage() {

        switch (version) {
            case v100:
                return new GetCoverage100(serverURL.toString(),securityManager);
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }
}
