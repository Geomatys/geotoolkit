/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.Query;
import org.apache.sis.storage.event.ChangeEvent;
import org.apache.sis.storage.event.ChangeListener;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.client.AbstractClientProvider;
import org.geotoolkit.client.Client;
import org.geotoolkit.data.AbstractFeatureStore;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.query.QueryCapabilities;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.data.wfs.v100.GetFeature100;
import org.geotoolkit.data.wfs.v110.Delete110;
import org.geotoolkit.data.wfs.v110.GetFeature110;
import org.geotoolkit.data.wfs.v110.Insert110;
import org.geotoolkit.data.wfs.v110.Native110;
import org.geotoolkit.data.wfs.v110.Transaction110;
import org.geotoolkit.data.wfs.v110.Update110;
import org.geotoolkit.data.wfs.v200.GetFeature200;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.security.ClientSecurity;
import org.geotoolkit.security.DefaultClientSecurity;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.version.Version;
import org.geotoolkit.version.VersionControl;
import org.geotoolkit.version.VersioningException;
import org.geotoolkit.wfs.xml.WFSBindingUtilities;
import org.geotoolkit.wfs.xml.WFSCapabilities;
import org.geotoolkit.wfs.xml.WFSVersion;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.MismatchedFeatureException;
import org.opengis.filter.Filter;
import org.opengis.filter.identity.FeatureId;
import org.opengis.geometry.Envelope;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterNotFoundException;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.GenericName;

/**
 * WFS server, used to aquiere capabilites and requests objects.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class WebFeatureClient extends AbstractFeatureStore implements Client {

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.client");

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
    private WFSFeatureStore store; //created when needed

    @Deprecated
    public WebFeatureClient(final URL serverURL, final String version) {
        this(serverURL,null,version);
    }

    @Deprecated
    public WebFeatureClient(final URL serverURL, final ClientSecurity security, final String version) {
        this(create(WFSProvider.PARAMETERS_DESCRIPTOR, serverURL, security));
        if(version.equals("1.1.0")){
            parameters.getOrCreate(WFSProvider.VERSION).setValue(version);
        }else{
            throw new IllegalArgumentException("unknowned version : "+ version);
        }
        parameters.getOrCreate(WFSProvider.POST_REQUEST).setValue(false);
    }

    public WebFeatureClient(final URL serverURL, final ClientSecurity security, final WFSVersion version, final boolean usePost) {
        this(create(WFSProvider.PARAMETERS_DESCRIPTOR, serverURL, security));
        if(version == null){
            throw new IllegalArgumentException("unknowned version : "+ version);
        }
        parameters.getOrCreate(WFSProvider.VERSION).setValue(version.getCode());
        parameters.getOrCreate(WFSProvider.POST_REQUEST).setValue(usePost);
    }

    /**
     *
     * @param params the {@link ParameterValueGroup} to use
     * @deprecated because of the version set to {@link WFSVersion#v110}. Please explicitly set the version using
     * {@link WebFeatureClient#WebFeatureClient(org.opengis.parameter.ParameterValueGroup, org.geotoolkit.wfs.xml.WFSVersion) }
     */
    @Deprecated
    public WebFeatureClient(final ParameterValueGroup params) {
        super(params);
        this.serverURL = parameters.getValue(AbstractClientProvider.URL);
        ArgumentChecks.ensureNonNull("server url", serverURL);
        /*
        On ne souhaite plus forcer la version du WFS, mais la lire dans les paramètres
        Changement de comportement : Cela suppose qu'on ne le remplit plus dans les parameters si les params ne le fournissent pas. (À VALIDER)
        */
        //parameters.getOrCreate(WFSFeatureStoreFactory.VERSION).setValue("1.1.0");
        parameters.parameter(WFSProvider.POST_REQUEST.getName().getCode());
    }

    @Override
    public Parameters getOpenParameters() {
        if(parameters != null){
            //defensive copy
            return parameters.clone();
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
     * {@inheritDoc}
     */
    @Override
    public URI getURI() {
        try {
            return serverURL.toURI();
        } catch (URISyntaxException ex) {
            getLogger().log(Level.WARNING, ex.getLocalizedMessage(), ex);
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

    @Override
    public WFSProvider getProvider() {
        return (WFSProvider) DataStores.getProviderById(WFSProvider.NAME);
    }

    @Override
    public GenericName getIdentifier() {
        return null;
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

    private synchronized FeatureStore getStore() {
        if(store == null){
            store = new WFSFeatureStore(this);
        }
        return store;
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

    /**
     * @return default server logger.
     */
    @Override
    protected Logger getLogger(){
        return LOGGER;
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

    ////////////////////////////////////////////////////////////////////////////
    // FeatureStore methods ///////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    @Override
    public Session createSession(boolean asynchrone, Version version) {
        return getStore().createSession(asynchrone,version);
    }

    @Override
    public VersionControl getVersioning(String typeName) throws VersioningException {
        return store.getVersioning(typeName);
    }

    @Override
    public Set<GenericName> getNames() throws DataStoreException {
        return getStore().getNames();
    }

    @Override
    public void createFeatureType(FeatureType featureType) throws DataStoreException {
        getStore().createFeatureType(featureType);
    }

    @Override
    public void updateFeatureType(FeatureType featureType) throws DataStoreException {
        getStore().updateFeatureType(featureType);
    }

    @Override
    public void deleteFeatureType(String typeName) throws DataStoreException {
        getStore().deleteFeatureType(typeName);
    }

    @Override
    public FeatureType getFeatureType(String typeName) throws DataStoreException {
        return getStore().getFeatureType(typeName);
    }

    @Override
    public FeatureType getFeatureType(Query query) throws DataStoreException, MismatchedFeatureException {
        return getStore().getFeatureType(query);
    }

    @Override
    public boolean isWritable(String typeName) throws DataStoreException {
        return getStore().isWritable(typeName);
    }

    @Override
    public QueryCapabilities getQueryCapabilities() {
            return getStore().getQueryCapabilities();
    }

    @Override
    public long getCount(Query query) throws DataStoreException {
        return getStore().getCount(query);
    }

    @Override
    public Envelope getEnvelope(Query query) throws DataStoreException {
        return getStore().getEnvelope(query);
    }

    @Override
    public List<FeatureId> addFeatures(String groupName, Collection<? extends Feature> newFeatures, Hints hints) throws DataStoreException {
        return getStore().addFeatures(groupName, newFeatures, hints);
    }

    @Override
    public void updateFeatures(String groupName, Filter filter, Map<String, ? extends Object> values) throws DataStoreException {
        getStore().updateFeatures(groupName, filter, values);
    }

    @Override
    public void removeFeatures(String groupName, Filter filter) throws DataStoreException {
        getStore().removeFeatures(groupName, filter);
    }

    @Override
    public FeatureReader getFeatureReader(Query query) throws DataStoreException {
        return getStore().getFeatureReader(query);
    }

    @Override
    public FeatureWriter getFeatureWriter(Query query) throws DataStoreException {
        return getStore().getFeatureWriter(query);
    }

    @Override
    public void close() throws DataStoreException {
        getStore().close();
    }

    @Override
    public <T extends ChangeEvent> void addListener(ChangeListener<? super T> listener, Class<T> eventType) {
        getStore().addListener(listener, eventType);
    }

    @Override
    public <T extends ChangeEvent> void removeListener(ChangeListener<? super T> listener, Class<T> eventType) {
        getStore().removeListener(listener, eventType);
    }

    @Override
    public void refreshMetaModel() throws DataStoreException {
        getStore().refreshMetaModel();
    }

}
