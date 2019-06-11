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
package org.geotoolkit.wms;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sis.storage.Aggregate;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.Resource;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.client.AbstractCoverageClient;
import org.geotoolkit.client.CapabilitiesException;
import org.geotoolkit.client.Client;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.security.ClientSecurity;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.storage.coverage.GridCoverageResource;
import org.geotoolkit.wms.auto.GetCapabilitiesAuto;
import org.geotoolkit.wms.v100.GetCapabilities100;
import org.geotoolkit.wms.v100.GetFeatureInfo100;
import org.geotoolkit.wms.v100.GetMap100;
import org.geotoolkit.wms.v110.GetCapabilities110;
import org.geotoolkit.wms.v110.GetFeatureInfo110;
import org.geotoolkit.wms.v110.GetLegend110;
import org.geotoolkit.wms.v110.GetMap110;
import org.geotoolkit.wms.v111.GetCapabilities111;
import org.geotoolkit.wms.v111.GetFeatureInfo111;
import org.geotoolkit.wms.v111.GetLegend111;
import org.geotoolkit.wms.v111.GetMap111;
import org.geotoolkit.wms.v130.GetCapabilities130;
import org.geotoolkit.wms.v130.GetFeatureInfo130;
import org.geotoolkit.wms.v130.GetLegend130;
import org.geotoolkit.wms.v130.GetMap130;
import org.geotoolkit.wms.xml.AbstractLayer;
import org.geotoolkit.wms.xml.AbstractWMSCapabilities;
import org.geotoolkit.wms.xml.WMSBindingUtilities;
import org.geotoolkit.wms.xml.WMSVersion;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.GenericName;


/**
 * Generates WMS requests objects on a WMS server.
 *
 * @author Olivier Terral (Geomatys)
 * @author Johann Sorel (Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @module
 */
public class WebMapClient extends AbstractCoverageClient implements Client, Aggregate {

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.wms");

    /**
     * Defines the timeout in milliseconds for the GetCapabilities request.
     */
    private static final long TIMEOUT_GETCAPS = 20000L;

    private AbstractWMSCapabilities capabilities;
    private Resource rootNode = null;

    /**
     * The request header map for this server
     * that contains a set of key-value for HTTP header fields (user-agent, referer, accept-language...)
     */
    private final Map<String,String> requestHeaderMap = new HashMap<>();

    /**
     * Constructor with only URL
     * @param serverURL
     */
    public WebMapClient(final URL serverURL){
        this(serverURL, null, null, null);
    }

    /**
     * Builds a web map server with the given server url and version.
     *
     * @param serverURL The server base url.
     * @param version A string representation of the service version.
     * @throws IllegalArgumentException if the version specified is not applyable.
     */
    public WebMapClient(final URL serverURL, final String version) {
        this(serverURL, WMSVersion.getVersion(version));
    }

    /**
     * Builds a web map server with the given server url and version.
     *
     * @param serverURL The server base url.
     * @param version The service version.
     */
    public WebMapClient(final URL serverURL, final WMSVersion version) {
        this(serverURL, version, null);
    }

    /**
     * Builds a web map server with the given server url and version.
     *
     * @param serverURL The server base url.
     * @param security The server security.
     * @param version The service version.
     */
    public WebMapClient(final URL serverURL, final ClientSecurity security,final WMSVersion version) {
        this(serverURL, security, version, null);
    }

    /**
     * Builds a web map server with the given server url, version and getCapabilities response.
     *
     * @param serverURL The server base url.
     * @param version A string representation of the service version.
     * @param capabilities A getCapabilities response.
     * @throws IllegalArgumentException if the version specified is not applyable.
     */
    public WebMapClient(final URL serverURL, final String version, final AbstractWMSCapabilities capabilities) {
        this(serverURL, WMSVersion.getVersion(version), capabilities);
    }

    /**
     * Builds a web map server with the given server url, version and getCapabilities response.
     *
     * @param serverURL The server base url.
     * @param version A string representation of the service version.
     * @param capabilities A getCapabilities response.
     */
    public WebMapClient(final URL serverURL, final WMSVersion version,
            final AbstractWMSCapabilities capabilities) {
        this(serverURL,null,version,capabilities);
    }

    /**
     * Builds a web map server with the given server url, version and getCapabilities response.
     *
     * @param serverURL The server base url.
     * @param security The server security.
     * @param version A string representation of the service version.
     * @param capabilities A getCapabilities response.
     */
    public WebMapClient(final URL serverURL, final ClientSecurity security,
            WMSVersion version, final AbstractWMSCapabilities capabilities) {
        super(create(WMSClientFactory.PARAMETERS, serverURL, security));

        this.capabilities = capabilities;

        //if version is null, call getCapabilities to found service version
        if(version==null){
            if(LOGGER.isLoggable(Level.FINE)){
                LOGGER.log(Level.FINE, "No version defined : search it on getCapabilities");
            }
            try {
                if(capabilities!=null){
                    this.capabilities = capabilities;
                }else{
                    this.capabilities = getServiceCapabilities();
                }

                //set version
                version = WMSVersion.getVersion(this.capabilities.getVersion());
            } catch (CapabilitiesException e) {
                LOGGER.log(Level.WARNING,  e.getLocalizedMessage(), e);
                version = WMSVersion.v130;
            }
        }
        parameters.getOrCreate(WMSClientFactory.VERSION).setValue(version.getCode());
    }

    public WebMapClient(ParameterValueGroup params) {
        super(params);
    }

    @Override
    public WMSClientFactory getProvider() {
        return (WMSClientFactory) DataStores.getProviderById(WMSClientFactory.NAME);
    }

    @Override
    public GenericName getIdentifier() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URI getURI(){
        try {
            return serverURL.toURI();
        } catch (URISyntaxException ex) {
            LOGGER.log(Level.WARNING, ex.getLocalizedMessage(), ex);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URL getURL() {
        return serverURL;
    }

    /**
     * Returns the {@linkplain AbstractWMSCapabilities capabilities} response for this
     * request if the server answers in less than 20s, otherwise throws a
     * {@link CapabilitiesException}.
     *
     * @return {@linkplain AbstractWMSCapabilities capabilities} response but never {@code null}.
     * @throws CapabilitiesException
     * @see {@link #getCapabilities(long)}
     */
    public AbstractWMSCapabilities getServiceCapabilities() throws CapabilitiesException{
        return getServiceCapabilities(TIMEOUT_GETCAPS);
    }

    /**
     * Returns the {@linkplain AbstractWMSCapabilities capabilities} response for this
     * request if the server answers before the specified timeout, otherwise throws a
     * {@link CapabilitiesException}.
     *
     * @param timeout Timeout in milliseconds
     * @return {@linkplain AbstractWMSCapabilities capabilities} response but never {@code null}.
     * @throws CapabilitiesException
     */
    public AbstractWMSCapabilities getServiceCapabilities(final long timeout) throws CapabilitiesException {
        if (capabilities != null) {
            return capabilities;
        }
        final GetCapabilitiesRequest getCaps = createGetCapabilities();
        getCaps.getHeaderMap().putAll(getRequestHeaderMap());
        getCaps.setTimeout((int) (timeout & Integer.MAX_VALUE));

        // Useful, because it serves as trace and URL validation
        try {
            LOGGER.log(Level.FINE, getCaps.getURL().toString());
            capabilities = WMSBindingUtilities.unmarshall(getCaps.getResponseStream(), getVersion());
        } catch (Exception ex) {
            throw new CapabilitiesException("Cannot handle GetCapabilities", ex);
        }

        //force throw CapabilitiesException if the returned capabilities object is null
        if (capabilities == null) {
            throw new CapabilitiesException("The capabilities document is null.");
        }

        WMSVersion version = WMSVersion.getVersion(this.capabilities.getVersion());
        parameters.getOrCreate(WMSClientFactory.VERSION).setValue(version.getCode());
        return capabilities;
    }

    /**
     * Returns the request version.
     * @return
     */
    public WMSVersion getVersion() {
            return WMSVersion.getVersion(parameters.getValue(WMSClientFactory.VERSION));
    }

    /**
     * Returns the request object, in the version chosen.
     *
     * @return
     * @throws IllegalArgumentException if the version requested is not supported.
     */
    public GetMapRequest createGetMap() {
        switch (getVersion()) {
            case v100:
                return new GetMap100(this,getClientSecurity());
            case v110:
                return new GetMap110(this,getClientSecurity());
            case v111:
                return new GetMap111(this,getClientSecurity());
            case v130:
                return new GetMap130(this,getClientSecurity());
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    /**
     * Returns the request object, in the version chosen.
     *
     * @return
     * @throws IllegalArgumentException if the version requested is not supported.
     */
    public GetCapabilitiesRequest createGetCapabilities() {
        switch (getVersion()) {
            case v100:
                return new GetCapabilities100(serverURL.toString(),getClientSecurity());
            case v110:
                return new GetCapabilities110(serverURL.toString(),getClientSecurity());
            case v111:
                return new GetCapabilities111(serverURL.toString(),getClientSecurity());
            case v130:
                return new GetCapabilities130(serverURL.toString(),getClientSecurity());
            default:
                if(LOGGER.isLoggable(Level.FINE)){
                    LOGGER.log(Level.FINE, "Version was not defined");
                }
                return new GetCapabilitiesAuto(serverURL.toString(), getClientSecurity());
        }
    }

    /**
     * Returns the request object, in the version chosen.
     *
     * @return
     * @throws IllegalArgumentException if the version requested is not supported.
     */
    public GetLegendRequest createGetLegend(){
        switch (getVersion()) {
            case v110:
                return new GetLegend110(serverURL.toString(),getClientSecurity());
            case v111:
                return new GetLegend111(serverURL.toString(),getClientSecurity());
            case v130:
                return new GetLegend130(serverURL.toString(),getClientSecurity());
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    /**
     * Returns the request object, in the version chosen.
     *
     * @return
     * @throws IllegalArgumentException if the version requested is not supported.
     */
    public GetFeatureInfoRequest createGetFeatureInfo() {
        switch (getVersion()) {
            case v100:
                return new GetFeatureInfo100(this,getClientSecurity());
            case v110:
                return new GetFeatureInfo110(this,getClientSecurity());
            case v111:
                return new GetFeatureInfo111(this,getClientSecurity());
            case v130:
                return new GetFeatureInfo130(this,getClientSecurity());
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    /**
     * Returns the request header map for this server.
     * @return
     */
    public Map<String,String> getRequestHeaderMap() {
        return requestHeaderMap;
    }

    @Override
    public Collection<org.apache.sis.storage.Resource> components() throws DataStoreException {
        final Resource root = getRootResource();
        return (root == null) ? Collections.EMPTY_LIST : Collections.singletonList(root);
    }

    private synchronized Resource getRootResource() throws DataStoreException {
        if (rootNode != null) {
            return rootNode;
        }
        final AbstractWMSCapabilities capa;
        try {
            capa = getServiceCapabilities();
        } catch (CapabilitiesException ex) {
            throw new DataStoreException(ex);
        }
        rootNode = asResource(capa.getCapability().getLayer()).orElse(null);
        return rootNode;
    }

    Optional<Resource> asResource(final AbstractLayer layer) throws CoverageStoreException {
        if (layer == null) {
            return Optional.empty();
        }

        final boolean isData = layer.getName() != null && layer.isQueryable();
        final boolean isGroup = layer.getLayer() != null && !layer.getLayer().isEmpty();
        final Resource result;
        if (isData && isGroup) {
            result = new QueryableAggregate(this, layer);
        } else if (isGroup) {
            result = new WMSAggregate(this, layer);
        } else if (isData) {
            result = new WMSCoverageResource(this, layer.getName());
        } else {
            result = null;
        }

        return Optional.ofNullable(result);
    }

    /**
     * Override by WMS-c and NCWMS.
     *
     * @param name
     * @return
     */
    protected GridCoverageResource createReference(GenericName name) throws DataStoreException{
        return new WMSCoverageResource(this,name);
    }

    @Override
    public void close() {
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("WebMapServer[");
        sb.append("serverUrl: ").append(serverURL).append(", ")
          .append("version: ").append(getVersion()).append("]");
        return sb.toString();
    }
}
