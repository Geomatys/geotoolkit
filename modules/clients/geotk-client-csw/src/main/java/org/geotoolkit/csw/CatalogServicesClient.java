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
package org.geotoolkit.csw;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.client.AbstractClient;
import org.geotoolkit.client.ClientFactory;
import org.geotoolkit.csw.v202.DescribeRecord202;
import org.geotoolkit.csw.v202.GetCapabilities202;
import org.geotoolkit.csw.v202.GetDomain202;
import org.geotoolkit.csw.v202.GetRecordById202;
import org.geotoolkit.csw.v202.GetRecords202;
import org.geotoolkit.csw.v202.Harvest202;
import org.geotoolkit.csw.v202.Transaction202;
import org.geotoolkit.csw.xml.AbstractCapabilities;
import org.geotoolkit.csw.xml.CSWBindingUtilities;
import org.geotoolkit.csw.xml.CSWVersion;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.security.ClientSecurity;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.storage.DataStores;


/**
 * CSW server that allows creation of all needed request
 * objects for calling several operations on catalog service.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Mehdi Sidhoum (Geomatys)
 * @module
 */
public class CatalogServicesClient extends AbstractClient {
    /**
     * Used for debugging purpose
     */
    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.csw");
    /**
     * Stored capabilities object that can be updated by calling {@link CatalogServicesServer.updateGetCapabilities}
     */
    private AbstractCapabilities capabilities;


    /**
     * Creates a new instance of {@link CatalogServicesServer} without passing version argument,
     * It performs a getCapabilities request and set the version depending on the response.
     *
     *
     * @param serverURL {@link URL} for the server instance
     * @throws IllegalStateException throws an exception if the capabilities cannot be resolved for serverUrl
     */
    public CatalogServicesClient(final URL serverURL) throws IllegalStateException{
        super(create(CSWClientFactory.PARAMETERS, serverURL, null));
        final AbstractCapabilities capa = getCapabilities();
        if(capa == null){
            throw new IllegalStateException("Cannot get Capabilities document from the server "+serverURL.toString());
        }
        final String v = capa.getVersion();
        try {
            parameters.parameter(CSWClientFactory.VERSION.getName().getCode()).setValue(CSWVersion.fromCode(v).getCode());
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException("unknown CSW version : " + v,ex);
        }
    }

    /**
     * Creates a new instance of {@link CatalogServicesServer} for serverUrl and version value
     * @param serverURL {@link URL} of the server CSW
     * @param version value of the CSW version, usually 2.0.2.
     */
    public CatalogServicesClient(final URL serverURL, final String version) {
        this(serverURL,null,version);
    }

    public CatalogServicesClient(final URL serverURL, final ClientSecurity security, final CSWVersion version) {
        this(serverURL, security, version.getCode());

    }

    public CatalogServicesClient(final URL serverURL, final ClientSecurity security, final String version) {
        super(create(CSWClientFactory.PARAMETERS, serverURL, security));
        ArgumentChecks.ensureNonNull("version", version);
        parameters.parameter(CSWClientFactory.VERSION.getName().getCode()).setValue(version);
    }

    @Override
    public ClientFactory getFactory() {
        return (ClientFactory) DataStores.getFactoryById(CSWClientFactory.NAME);
    }

    private static CSWVersion toVersion(final String version){
        if (version.equals("2.0.2")) {
            return CSWVersion.v202;
        } else {
            throw new IllegalArgumentException("unknown version : "+ version);
        }
    }

    /**
     * Returns the currently used version for this server
     */
    public CSWVersion getVersion() {
        return CSWVersion.fromCode(Parameters.value(CSWClientFactory.VERSION, parameters));
    }

    /**
     * Creates and returns a describeRecord request.
     */
    public DescribeRecordRequest createDescribeRecord() {

        switch (getVersion()) {
            case v202:
                return new DescribeRecord202(serverURL.toString(),getClientSecurity());
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    /**
     * Creates and returns a getCapabilities request.
     *
     * @TODO rename this method to getCapabilitiesRequest
     */
    public GetCapabilitiesRequest createGetCapabilities() {

        switch (getVersion()) {
            case v202:
                return new GetCapabilities202(serverURL.toString(),getClientSecurity());
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    /**
     * Creates and returns a getDomain request.
     */
    public GetDomainRequest createGetDomain() {

        switch (getVersion()) {
            case v202:
                return new GetDomain202(serverURL.toString(),getClientSecurity());
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    /**
     * Creates and returns a getRecordById request.
     */
    public GetRecordByIdRequest createGetRecordById() {

        switch (getVersion()) {
            case v202:
                return new GetRecordById202(serverURL.toString(),getClientSecurity());
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    /**
     * Creates and returns a getRecords request.
     */
    public GetRecordsRequest createGetRecords() {

        switch (getVersion()) {
            case v202:
                return new GetRecords202(serverURL.toString(),getClientSecurity());
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    /**
     * Creates and returns a harvest request.
     */
    public HarvestRequest createHarvest() {

        switch (getVersion()) {
            case v202:
                return new Harvest202(serverURL.toString(),getClientSecurity());
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    /**
     * Creates and returns a transaction request.
     */
    public TransactionRequest createTransaction() {

        switch (getVersion()) {
            case v202:
                return new Transaction202(serverURL.toString(),getClientSecurity());
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    /**
     * Returns the {@link AbstractCapabilities capabilities} response for this
     * server.
     * This is a lazy loading for stored capabilities object.
     *
     */
    public AbstractCapabilities getCapabilities() {

        if (capabilities != null) {
            return capabilities;
        }
        //Thread to prevent infinite request on a server
        final Thread thread = new Thread() {

            @Override
            public void run() {
                try {
                    final URL getcapaUrl = new URL(serverURL.toString() + "?SERVICE=CSW&REQUEST=GetCapabilities");
                    capabilities = CSWBindingUtilities.unmarshall(getcapaUrl);
                } catch (Exception ex) {
                    capabilities = null;
                    try {
                        LOGGER.log(Level.WARNING, "Wrong URL, the server doesn't answer : "
                                + createGetCapabilities().getURL().toString(), ex);
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
}
