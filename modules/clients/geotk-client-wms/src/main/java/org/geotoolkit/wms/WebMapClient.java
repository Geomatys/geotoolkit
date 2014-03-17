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

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotoolkit.client.AbstractCoverageClient;
import org.geotoolkit.client.CapabilitiesException;
import org.geotoolkit.client.ClientFinder;
import org.geotoolkit.coverage.CoverageReference;
import org.geotoolkit.coverage.CoverageStore;
import org.geotoolkit.coverage.CoverageType;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.security.ClientSecurity;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.storage.DataNode;
import org.geotoolkit.storage.DefaultDataNode;
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
import org.opengis.feature.type.Name;
import org.opengis.parameter.ParameterValueGroup;


/**
 * Generates WMS requests objects on a WMS server.
 *
 * @author Olivier Terral (Geomatys)
 * @author Johann Sorel (Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @module pending
 */
public class WebMapClient extends AbstractCoverageClient {

    private static final Logger LOGGER = Logging.getLogger(WebMapClient.class);

    /**
     * Defines the timeout in milliseconds for the GetCapabilities request.
     */
    private static final long TIMEOUT_GETCAPS = 20000L;

    private AbstractWMSCapabilities capabilities;
    private DataNode rootNode = null;

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
            final WMSVersion version, final AbstractWMSCapabilities capabilities) {
        super(create(WMSClientFactory.PARAMETERS, serverURL, security));
        Parameters.getOrCreate(WMSClientFactory.VERSION, parameters).setValue(version.getCode());
        this.capabilities = capabilities;
    }

    public WebMapClient(ParameterValueGroup params) {
        super(params);
    }

    @Override
    public WMSClientFactory getFactory() {
        return (WMSClientFactory)ClientFinder.getFactoryById(WMSClientFactory.NAME);
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
    public AbstractWMSCapabilities getCapabilities() throws CapabilitiesException{
    	return getCapabilities(TIMEOUT_GETCAPS);
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
    public AbstractWMSCapabilities getCapabilities(final long timeout) throws CapabilitiesException{

        if (capabilities != null) {
            return capabilities;
        }

        final CapabilitiesException[] exception = new CapabilitiesException[1];

        //Thread to prevent infinite request on a server
        final Thread thread = new Thread() {
            @Override
            public void run() {
                final GetCapabilitiesRequest getCaps = createGetCapabilities();

                //Filling the request header map from the map of the layer's server
                final Map<String, String> headerMap = getRequestHeaderMap();
                getCaps.getHeaderMap().putAll(headerMap);

                try {
                    System.out.println(getCaps.getURL());
                    capabilities = WMSBindingUtilities.unmarshall(getCaps.getResponseStream(), getVersion());
                } catch (Exception ex) {
                    capabilities = null;
                    try {
                        exception[0] = new CapabilitiesException("Wrong URL, the server doesn't answer : " +
                                createGetCapabilities().getURL().toString(), ex);
                    } catch (MalformedURLException ex1) {
                        exception[0] = new CapabilitiesException("Malformed URL, the server doesn't answer. ", ex);
                    }
                }
            }
        };

        thread.start();
        final long start = System.currentTimeMillis();
        try {
            thread.join(timeout);
        } catch (InterruptedException ex) {
            throw new CapabilitiesException("The thread to obtain GetCapabilities doesn't answer.");
        }

        if(exception[0] != null){
            throw exception[0];
        }

        if ((System.currentTimeMillis() - start) > timeout) {
            throw new CapabilitiesException("TimeOut error, the server takes too much time to answer.");
        }

        //force throw CapabilitiesException if the returned capabilities object is null
        if(capabilities == null){
            throw new CapabilitiesException("The capabilities document is null.");
        }

        return capabilities;
    }

    /**
     * Returns the request version.
     * @return
     */
    public WMSVersion getVersion() {
        return WMSVersion.getVersion(Parameters.value(WMSClientFactory.VERSION, parameters));
    }

    /**
     * Returns the request object, in the version chosen.
     *
     * @return
     * @throws IllegalArgumentException if the version requested is not supported.
     */
    public GetMapRequest createGetMap() {
        switch (getVersion()) {
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
            case v111:
                return new GetCapabilities111(serverURL.toString(),getClientSecurity());
            case v130:
                return new GetCapabilities130(serverURL.toString(),getClientSecurity());
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
    public GetLegendRequest createGetLegend(){
        switch (getVersion()) {
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
    public synchronized DataNode getRootNode() throws DataStoreException {
        if(rootNode == null){
            rootNode = new DefaultDataNode();
            final AbstractWMSCapabilities capa;
            try {
                capa = getCapabilities();
            } catch (CapabilitiesException ex) {
                throw new DataStoreException(ex);
            }

            final List<AbstractLayer> layers = capa.getLayers();
            for(AbstractLayer al : layers){
                final String name = al.getName();
                if(name != null){
                    final Name nn = DefaultName.valueOf(name);
                    final CoverageReference ref = createReference(nn);
                    rootNode.getChildren().add(ref);
                }
            }
        }

        return rootNode;
    }

    /**
     * Override by WMS-c and NCWMS.
     *
     * @param name
     * @return
     */
    protected CoverageReference createReference(Name name) throws DataStoreException{
        return new WMSCoverageReference(this,name);
    }

    @Override
    public void close() {
    }

    @Override
    public CoverageReference create(Name name) throws DataStoreException {
        throw new DataStoreException("Can not create new coverage.");
    }

    @Override
    public void delete(Name name) throws DataStoreException {
        throw new DataStoreException("Can not create new coverage.");
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("WebMapServer[");
        sb.append("serverUrl: ").append(serverURL).append(", ")
          .append("version: ").append(getVersion()).append("]");
        return sb.toString();
    }

	@Override
	public CoverageType getType() {
		return CoverageType.GRID;
	}
}
