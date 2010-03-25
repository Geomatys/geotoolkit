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
package org.geotoolkit.csw;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.client.Server;
import org.geotoolkit.csw.v202.DescribeRecord202;
import org.geotoolkit.csw.v202.GetCapabilities202;
import org.geotoolkit.csw.v202.GetDomain202;
import org.geotoolkit.csw.v202.GetRecordById202;
import org.geotoolkit.csw.v202.GetRecords202;
import org.geotoolkit.csw.v202.Harvest202;
import org.geotoolkit.csw.v202.Transaction202;
import org.geotoolkit.csw.xml.CSWVersion;
import org.geotoolkit.util.logging.Logging;


/**
 * CSW server.
 *
 * @author Cédric Briançon (Geomatys)
 * @module pending
 */
public class CatalogServicesServer implements Server {
    private static final Logger LOGGER = Logging.getLogger(CatalogServicesServer.class);

    private final CSWVersion version;
    private final URL serverURL;

    public CatalogServicesServer(final URL serverURL, final String version) {
        if (version.equals("2.0.2")){
            this.version = CSWVersion.v202;
        } else {
            throw new IllegalArgumentException("unkonwed version : "+ version);
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
     * Returns the currently used version for this server
     */
    public CSWVersion getVersion() {
        return version;
    }

    /**
     * Creates and returns a describeRecord request.
     */
    public DescribeRecordRequest createDescribeRecord() {

        switch (version) {
            case v202:
                return new DescribeRecord202(serverURL.toString());
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    /**
     * Creates and returns a getCapabilities request.
     */
    public GetCapabilitiesRequest createGetCapabilities() {

        switch (version) {
            case v202:
                return new GetCapabilities202(serverURL.toString());
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    /**
     * Creates and returns a getDomain request.
     */
    public GetDomainRequest createGetDomain() {

        switch (version) {
            case v202:
                return new GetDomain202(serverURL.toString());
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    /**
     * Creates and returns a getRecordById request.
     */
    public GetRecordByIdRequest createGetRecordById() {

        switch (version) {
            case v202:
                return new GetRecordById202(serverURL.toString());
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    /**
     * Creates and returns a getRecords request.
     */
    public GetRecordsRequest createGetRecords() {

        switch (version) {
            case v202:
                return new GetRecords202(serverURL.toString());
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    /**
     * Creates and returns a harvest request.
     */
    public HarvestRequest createHarvest() {

        switch (version) {
            case v202:
                return new Harvest202(serverURL.toString());
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    /**
     * Creates and returns a transaction request.
     */
    public TransactionRequest createTransaction() {

        switch (version) {
            case v202:
                return new Transaction202(serverURL.toString());
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }
}
