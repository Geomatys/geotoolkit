/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Geomatys
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sis.storage.Aggregate;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.client.AbstractClientFactory;
import org.geotoolkit.client.AbstractCoverageClient;
import org.geotoolkit.client.Client;
import org.geotoolkit.security.ClientSecurity;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.storage.Resource;
import org.geotoolkit.storage.coverage.GridCoverageResource;
import org.geotoolkit.util.NamesExt;
import org.geotoolkit.wmts.v100.GetCapabilities100;
import org.geotoolkit.wmts.v100.GetTile100;
import org.geotoolkit.wmts.xml.WMTSBindingUtilities;
import org.geotoolkit.wmts.xml.WMTSVersion;
import org.geotoolkit.wmts.xml.v100.Capabilities;
import org.geotoolkit.wmts.xml.v100.LayerType;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.GenericName;


/**
 * Generates WMTS requests objects on a WMTS server.
 *
 * @author Guilhem Legal (Geomatys)
 * @module
 */
public class WebMapTileClient extends AbstractCoverageClient implements Client, Aggregate {

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.wmts");

    private Capabilities capabilities;
    private List<Resource> resources = null;

    /**
     * Defines the timeout in milliseconds for the GetCapabilities request.
     * default is 10 seconds.
     */
    private static final long TIMEOUT_GETCAPS = 10000L;

    /**
     * The request header map for this server
     * that contains a set of key-value for HTTP header fields (user-agent, referer, accept-language...)
     */
    private final Map<String,String> requestHeaderMap = new HashMap<>();

    /**
     * Builds a web map server with the given server url and version.
     *
     * @param serverURL The server base url.
     * @param version A string representation of the service version.
     * @throws IllegalArgumentException if the version specified is not applyable.
     */
    public WebMapTileClient(final URL serverURL, final String version) {
        this(serverURL, WMTSVersion.getVersion(version));
    }

    /**
     * Builds a web map server with the given server url, a security and version.
     *
     * @param serverURL The server base url.
     * @param security The server security.
     * @param version The service version.
     * @throws IllegalArgumentException if the version specified is not applyable.
     */
    public WebMapTileClient(final URL serverURL, final ClientSecurity security, final WMTSVersion version) {
        this(serverURL, security, version, null, false);
    }

    /**
     * Builds a web map server with the given server url and version.
     *
     * @param serverURL The server base url.
     * @param version The service version.
     */
    public WebMapTileClient(final URL serverURL, final WMTSVersion version) {
        this(serverURL, version, null);
    }

    /**
     * Builds a web map server with the given server url, version and getCapabilities response.
     *
     * @param serverURL The server base url.
     * @param version A string representation of the service version.
     * @param capabilities A getCapabilities response.
     * @throws IllegalArgumentException if the version specified is not applyable.
     */
    public WebMapTileClient(final URL serverURL, final String version, final Capabilities capabilities) {
        this(serverURL, WMTSVersion.getVersion(version), capabilities);
    }

    /**
     * Builds a web map server with the given server url, version and getCapabilities response.
     *
     * @param serverURL The server base url.
     * @param version A string representation of the service version.
     * @param capabilities A getCapabilities response.
     */
    public WebMapTileClient(final URL serverURL, final WMTSVersion version, final Capabilities capabilities) {
        this(serverURL,null,version,capabilities,false);
    }

    /**
     * Builds a web map server with the given server url, version and getCapabilities response.
     *
     * @param serverURL The server base url.
     * @param security The server security.
     * @param version A string representation of the service version.
     * @param capabilities A getCapabilities response.
     */
    public WebMapTileClient(final URL serverURL, final ClientSecurity security,
            final WMTSVersion version, final Capabilities capabilities, boolean cacheImage) {
        super(create(WMTSClientFactory.PARAMETERS, serverURL, security));
        parameters.getOrCreate(WMTSClientFactory.VERSION).setValue(version.getCode());
        parameters.getOrCreate(WMTSClientFactory.IMAGE_CACHE).setValue(cacheImage);
        this.capabilities = capabilities;
    }

    public WebMapTileClient(ParameterValueGroup param){
        super(param);
    }

    @Override
    public WMTSClientFactory getProvider() {
        return (WMTSClientFactory)DataStores.getFactoryById(WMTSClientFactory.NAME);
    }

    @Override
    public GenericName getIdentifier() {
        return null;
    }

    /**
     * Returns the {@linkplain Capabilities capabilities} response for this request.
     *
     * @return {@linkplain Capabilities capabilities} response but never {@code null}.
     * @see {@link #getCapabilities(long)}
     */
    public Capabilities getServiceCapabilities() {
        return getCapabilities(TIMEOUT_GETCAPS);
    }

    /**
     * Returns the {@linkplain Capabilities capabilities} response for this
     * request.
     *
     * @param timeout Timeout in milliseconds
     */
    public Capabilities getCapabilities(final long timeout) {

        if (capabilities != null) {
            return capabilities;
        }
        //Thread to prevent infinite request on a server
        final Thread thread = new Thread() {
            @Override
            public void run() {
                final GetCapabilitiesRequest getCaps =  createGetCapabilities();
                //Filling the request header map from the map of the layer's server
                final Map<String, String> headerMap = getRequestHeaderMap();
                getCaps.getHeaderMap().putAll(headerMap);
                try {
                    capabilities = WMTSBindingUtilities.unmarshall(getCaps.getResponseStream(), getVersion());
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
            thread.join(timeout);
        } catch (InterruptedException ex) {
            LOGGER.log(Level.WARNING, "The thread to obtain GetCapabilities doesn't answer.", ex);
        }
        if ((System.currentTimeMillis() - start) > timeout) {
            LOGGER.log(Level.WARNING, "TimeOut error, the server takes too much time to answer. ");
        }

        return capabilities;
    }

    /**
     * Returns the request version.
     */
    public WMTSVersion getVersion() {
        return WMTSVersion.getVersion(parameters.getValue(WMTSClientFactory.VERSION));
    }

    public boolean getImageCache(){
        return parameters.getValue(AbstractClientFactory.IMAGE_CACHE);
    }

    /**
     * Returns the request object, in the version chosen.
     *
     * @throws IllegalArgumentException if the version requested is not supported.
     */
    public GetTileRequest createGetTile() {
        switch (getVersion()) {
            case v100:
                return new GetTile100(serverURL.toString(),getClientSecurity());
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    /**
     * Returns the request object, in the version chosen.
     *
     * @throws IllegalArgumentException if the version requested is not supported.
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
     * Returns the request header map for this server.
     * @return {@code Map}
     */
    public Map<String,String> getRequestHeaderMap() {
        return requestHeaderMap;
    }

    @Override
    public synchronized Collection<org.apache.sis.storage.Resource> components() throws DataStoreException {
        if(resources == null){
            resources = new ArrayList<>();

            final Capabilities capa = getServiceCapabilities();
            if(capa == null){
                throw new DataStoreException("Could not get Capabilities.");
            }
            final List<LayerType> layers = capa.getContents().getLayers();
            for(LayerType lt : layers){
                final String name = lt.getIdentifier().getValue();
                final GenericName nn = NamesExt.create(name);
                final GridCoverageResource ref = new WMTSCoverageResource(this,nn,getImageCache());
                resources.add(ref);
            }

        }
        return Collections.unmodifiableList(resources);
    }

    @Override
    public void close() {
    }

}
