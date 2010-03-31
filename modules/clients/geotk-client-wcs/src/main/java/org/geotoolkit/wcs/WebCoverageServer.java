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

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.client.Server;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.wcs.v100.DescribeCoverage100;
import org.geotoolkit.wcs.v100.GetCapabilities100;
import org.geotoolkit.wcs.v100.GetCoverage100;
import org.geotoolkit.wcs.xml.WCSVersion;


/**
 * WCS server, used to aquiere capabilites and requests objects.
 *
 * @author Cédric Briançon (Geomatys)
 * @module pending
 */
public class WebCoverageServer implements Server {

    private static final Logger LOGGER = Logging.getLogger(WebCoverageServer.class);

    private final WCSVersion version;

    private final URL serverURL;

    public WebCoverageServer(URL serverURL, String version) {
        if (version.equals("1.0.0")) {
            this.version = WCSVersion.v100;
        } else {
            throw new IllegalArgumentException("unkonwed version : " + version);
        }
        this.serverURL = serverURL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URI getURI() {
        try {
            return serverURL.toURI();
        } catch (URISyntaxException ex) {
            LOGGER.log(Level.WARNING, ex.getLocalizedMessage(), ex);
        }
        return null;
    }

    /**
     * Create a describe coverage request.
     * @return DescribeCoverageRequest : describe coverage request.
     */
    public DescribeCoverageRequest createDescribeCoverage() {

        switch (version) {
            case v100:
                return new DescribeCoverage100(serverURL.toString());
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
                return new GetCapabilities100(serverURL.toString());
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
                return new GetCoverage100(serverURL.toString());
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }
}
