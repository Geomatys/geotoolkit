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
import org.geotoolkit.client.ServerFactory;
import org.geotoolkit.client.ServerFinder;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.security.ClientSecurity;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.wcs.v100.DescribeCoverage100;
import org.geotoolkit.wcs.v100.GetCapabilities100;
import org.geotoolkit.wcs.v100.GetCoverage100;
import org.geotoolkit.wcs.xml.WCSMarshallerPool;
import org.geotoolkit.wcs.xml.WCSVersion;
import org.geotoolkit.wcs.xml.v100.WCSCapabilitiesType;
import org.opengis.parameter.ParameterValueGroup;


/**
 * WCS server, used to aquiere capabilites and requests objects.
 *
 * @author Cédric Briançon (Geomatys)
 * @module pending
 */
public class WebCoverageServer extends AbstractServer {

    private static final Logger LOGGER = Logging.getLogger(WebCoverageServer.class);

    private WCSCapabilitiesType capabilities;

    public WebCoverageServer(final URL serverURL, final String version) {
        this(serverURL,null,version);
    }

    public WebCoverageServer(final URL serverURL, final ClientSecurity security, final String version) {
        super(create(WCSServerFactory.PARAMETERS, serverURL, security));
        if (version.equals("1.0.0")) {
            Parameters.getOrCreate(WCSServerFactory.VERSION, parameters).setValue(WCSVersion.v100);
        } else {
            throw new IllegalArgumentException("unkonwed version : " + version);
        }
    }

    public WebCoverageServer(final URL serverURL, final ClientSecurity security, final WCSVersion version) {
        super(create(WCSServerFactory.PARAMETERS, serverURL, security));
        if(version == null){
            throw new IllegalArgumentException("unkonwed version : " + version);
        }
        Parameters.getOrCreate(WCSServerFactory.VERSION, parameters).setValue(version);
    }

    public WebCoverageServer(final ParameterValueGroup params) {
        super(params);
    }

    @Override
    public ServerFactory getFactory() {
        return ServerFinder.getFactoryById(WCSServerFactory.NAME);
    }

    /**
     * Returns the currently used version for this server
     */
    public WCSVersion getVersion() {
        return WCSVersion.fromCode(Parameters.value(WCSServerFactory.VERSION, parameters));
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
                try {
                    final Unmarshaller unmarshaller = WCSMarshallerPool.getInstance().acquireUnmarshaller();
                    final GetCapabilitiesRequest request = createGetCapabilities();
                    capabilities = (WCSCapabilitiesType) unmarshaller.unmarshal(request.getURL());
                    WCSMarshallerPool.getInstance().recycle(unmarshaller);
                } catch (Exception ex) {
                    capabilities = null;
                    try {
                        LOGGER.log(Level.WARNING, "Wrong URL, the server doesn't answer : " +
                                createGetCapabilities().getURL().toString(), ex);
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
     * Create a describe coverage request.
     * @return DescribeCoverageRequest : describe coverage request.
     */
    public DescribeCoverageRequest createDescribeCoverage() {

        switch (getVersion()) {
            case v100:
                return new DescribeCoverage100(serverURL.toString(),getClientSecurity());
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
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
     * Create a getCoverage request.
     * @return GetCoverageRequest : getCoverage request.
     */
    public GetCoverageRequest createGetCoverage() {

        switch (getVersion()) {
            case v100:
                return new GetCoverage100(serverURL.toString(),getClientSecurity());
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }
}
