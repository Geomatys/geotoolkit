/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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
package org.geotoolkit.data.wfs;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import org.apache.sis.metadata.iso.DefaultMetadata;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.storage.Aggregate;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStoreProvider;
import org.apache.sis.storage.Resource;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.client.AbstractClientProvider;
import org.geotoolkit.client.Client;
import org.geotoolkit.data.wfs.v100.GetFeature100;
import org.geotoolkit.data.wfs.v110.Delete110;
import org.geotoolkit.data.wfs.v110.GetFeature110;
import org.geotoolkit.data.wfs.v110.Insert110;
import org.geotoolkit.data.wfs.v110.Native110;
import org.geotoolkit.data.wfs.v110.Transaction110;
import org.geotoolkit.data.wfs.v110.Update110;
import org.geotoolkit.data.wfs.v200.GetFeature200;
import org.geotoolkit.security.ClientSecurity;
import org.geotoolkit.security.DefaultClientSecurity;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.wfs.xml.FeatureTypeList;
import org.geotoolkit.wfs.xml.WFSBindingUtilities;
import org.geotoolkit.wfs.xml.WFSCapabilities;
import org.geotoolkit.wfs.xml.WFSVersion;
import org.opengis.metadata.Metadata;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterNotFoundException;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Web Feature service DataStore.
 *
 * @author Johann Sorel (Geomatys)
 */
public class WebFeatureClient extends DataStore implements Aggregate, Client {

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.client");

    private final Parameters parameters;
    private List<WFSFeatureSet> components;

    protected final URL serverURL;
    private final Map<String,Object> userProperties = new HashMap<>();
    private String sessionId = null;

    /**
     * Default timeout (in milliseconds).
     *
     * @todo Should be a parameter.
     */
    private static final long TIMEOUT = 60000;

    private volatile WFSCapabilities capabilities;


    public WebFeatureClient(final URL serverURL, final ClientSecurity security, final WFSVersion version, final boolean usePost) {
         Parameters parameters = Parameters.castOrWrap(create(WFSProvider.PARAMETERS_DESCRIPTOR, serverURL, security));
        this.serverURL = parameters.getMandatoryValue(AbstractClientProvider.URL);
        if(version == null){
            throw new IllegalArgumentException("unknowned version : "+ version);
        }
        parameters.getOrCreate(WFSProvider.VERSION).setValue(version.getCode());
        parameters.getOrCreate(WFSProvider.POST_REQUEST).setValue(usePost);
        this.parameters = Parameters.unmodifiable(parameters);
    }


    public WebFeatureClient(ParameterValueGroup params) {
        this.parameters = Parameters.unmodifiable(params);
        this.serverURL = parameters.getMandatoryValue(AbstractClientProvider.URL);
        /*
        On ne souhaite plus forcer la version du WFS, mais la lire dans les paramètres
        Changement de comportement : Cela suppose qu'on ne le remplit plus dans les parameters si les params ne le fournissent pas. (À VALIDER)
        */
        //parameters.getOrCreate(WFSFeatureStoreFactory.VERSION).setValue("1.1.0");
    }

    @Override
    public Optional<ParameterValueGroup> getOpenParameters() {
        return Optional.of(parameters);
    }

    @Override
    public DataStoreProvider getProvider() {
        return DataStores.getProviderById(WFSProvider.NAME);
    }

    @Override
    public Metadata getMetadata() throws DataStoreException {
        return new DefaultMetadata();
    }

    /**
     * @return default server logger.
     */
    Logger getLogger(){
        return LOGGER;
    }

    @Override
    public synchronized Collection<? extends Resource> components() throws DataStoreException {
        if (components == null) {
            components = new ArrayList<>();

            final WFSCapabilities capabilities = getServiceCapabilities();
            final FeatureTypeList lst = capabilities.getFeatureTypeList();

            for (final org.geotoolkit.wfs.xml.FeatureType ftt : lst.getFeatureType()) {
                WFSFeatureSet set = new WFSFeatureSet(this, capabilities, ftt);
                components.add(set);
            }
            components = Collections.unmodifiableList(components);
        }
        return components;
    }

    @Override
    public void close() throws DataStoreException {
    }

    // WFS CLIENT methods //////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    @Override
    public URL getURL() {
        return serverURL;
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
     * {@inheritDoc}
     */
    @Override
    public ClientSecurity getClientSecurity() {
        ClientSecurity securityManager = null;
        try {
            securityManager = parameters.getValue(AbstractClientProvider.SECURITY);
        } catch (ParameterNotFoundException ex) {
            // do nothing
        }
        return (securityManager == null) ?  DefaultClientSecurity.NO_SECURITY : securityManager;
    }

    @Override
    public int getTimeOutValue() {
        Integer timeout = null;
        try {
            timeout = parameters.getValue(AbstractClientProvider.TIMEOUT);
        } catch (ParameterNotFoundException ex) {
            // do nothing
        }
        return (timeout == null) ?  AbstractClientProvider.TIMEOUT.getDefaultValue() : timeout;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setUserProperty(final String key,final Object value){
        userProperties.put(key, value);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Object getUserProperty(final String key){
        return userProperties.get(key);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Map<String, Object> getUserProperties() {
        return userProperties;
    }

    public WFSVersion getVersion(){
        return WFSVersion.fromCode(parameters.getValue(WFSProvider.VERSION));
    }

    public boolean getUsePost(){
        return parameters.getValue(WFSProvider.POST_REQUEST);
    }

    public boolean getLongitudeFirst(){
        return parameters.getValue(WFSProvider.LONGITUDE_FIRST);
    }

    /**
     * @return The WFS server capabilities.
     * @throws WebFeatureException if an error occurred while querying the capabilities from the server.
     */
    public WFSCapabilities getServiceCapabilities() throws WebFeatureException {
        if (capabilities != null) {
            return capabilities;
        }

        final GetCapabilitiesRequest capaRequest = createGetCapabilities();
        final Integer timeout = parameters.getValue(AbstractClientProvider.TIMEOUT);
        if (timeout != null) {
            capaRequest.setTimeout(timeout);
        } else {
            capaRequest.setTimeout(TIMEOUT);
        }

        try {
            capabilities = WFSBindingUtilities.unmarshall(capaRequest.getResponseStream(), getVersion());
        } catch (IOException|JAXBException ex) {
            throw new WebFeatureException("Cannot read GetCapabilities from server", ex);
        }

        return capabilities;
    }

    /**
     * Create a getCapabilities request.
     * @return GetCapabilitiesRequest : getCapabilities request.
     */
    public GetCapabilitiesRequest createGetCapabilities() {
        return new AbstractGetCapabilities(serverURL.toString(), getVersion(), getClientSecurity());
    }

    /**
     * Create a describe feature request
     * @return DescribeFeatureTypeRequest : describe feature request.
     */
    public DescribeFeatureTypeRequest createDescribeFeatureType() {
        return new AbstractDescribeFeatureType(serverURL.toString(), getVersion(), getClientSecurity());
    }

    /**
     * Create a get feature request
     * @return GetFeatureRequest : get feature request.
     */
    public GetFeatureRequest createGetFeature(){
        switch (getVersion()) {
            case v100:
                return new GetFeature100(serverURL.toString(), getClientSecurity());
            case v110:
                return new GetFeature110(serverURL.toString(), getClientSecurity());
            case v200:
            case v202:
                return new GetFeature200(serverURL.toString(), getClientSecurity());
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    /**
     * Create a transaction request
     * @return TransactionRequest : transaction request.
     */
    public TransactionRequest createTransaction(){
        switch (getVersion()) {
            case v110:
                return new Transaction110(serverURL.toString(),getClientSecurity());
            default:
                throw new UnsupportedOperationException("Unsupported version: "+getVersion());
        }
    }

    public Insert createInsertElement(){
        switch (getVersion()) {
            case v110:
                return new Insert110();
            default:
                throw new UnsupportedOperationException("Unsupported version: "+getVersion());
        }
    }

    public Update createUpdateElement(){
        switch (getVersion()) {
            case v110:
                return new Update110();
            default:
                throw new UnsupportedOperationException("Unsupported version: "+getVersion());
        }
    }

    public Delete createDeleteElement(){
        //switch (getVersion()) {
          //  case v110:
                return new Delete110();
            //default:
             //   throw new IllegalArgumentException("Version was not defined");
        //}
    }

    public Native createNativeElement(){
        switch (getVersion()) {
            case v110:
                return new Native110();
            default:
                throw new UnsupportedOperationException("Unsupported version: "+getVersion());
        }
    }

    protected void applySessionId(final URLConnection conec) {
        if (sessionId != null) {
            conec.setRequestProperty("Cookie", sessionId);
        }
    }

    protected void readSessionId(final URLConnection conec) {
        if (sessionId == null) {
            final Map<String, List<String>> headers = conec.getHeaderFields();
            for (String key : headers.keySet()) {
                for (String value : headers.get(key)) {
                    final int beginIndex = value.indexOf("JSESSIONID=");
                    if (beginIndex != -1) {
                        sessionId = value;
                    }
                }
            }
        }
    }

    protected static ParameterValueGroup create(final ParameterDescriptorGroup desc,
            final URL url, final ClientSecurity security){
        final Parameters param = Parameters.castOrWrap(desc.createValue());
        param.getOrCreate(AbstractClientProvider.URL).setValue(url);
        if (security != null) {
            param.getOrCreate(AbstractClientProvider.SECURITY).setValue(security);
        }
        return param;
    }
}
