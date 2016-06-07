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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.client.AbstractFeatureClient;
import org.geotoolkit.client.Client;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryCapabilities;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.data.wfs.v110.*;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.security.ClientSecurity;
import org.geotoolkit.storage.StorageListener;
import org.geotoolkit.version.Version;
import org.geotoolkit.version.VersionControl;
import org.geotoolkit.version.VersioningException;
import org.geotoolkit.wfs.xml.WFSBindingUtilities;
import org.geotoolkit.wfs.xml.WFSCapabilities;
import org.geotoolkit.wfs.xml.WFSVersion;
import org.geotoolkit.feature.Feature;
import org.geotoolkit.feature.type.FeatureType;
import org.opengis.util.GenericName;
import org.geotoolkit.feature.type.PropertyDescriptor;
import org.geotoolkit.storage.DataStores;
import org.opengis.feature.MismatchedFeatureException;
import org.opengis.filter.Filter;
import org.opengis.filter.identity.FeatureId;
import org.opengis.geometry.Envelope;
import org.opengis.parameter.ParameterValueGroup;

/**
 * WFS server, used to aquiere capabilites and requests objects.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class WebFeatureClient extends AbstractFeatureClient implements Client {
    /**
     * Default timeout (in milliseconds).
     *
     * @todo Should be a parameter.
     */
    private static final long TIMEOUT = 60000;

    private volatile WFSCapabilities capabilities;
    private WFSFeatureStore store; //created when needed

    public WebFeatureClient(final URL serverURL, final String version) {
        this(serverURL,null,version);
    }

    public WebFeatureClient(final URL serverURL, final ClientSecurity security, final String version) {
        this(create(WFSFeatureStoreFactory.PARAMETERS_DESCRIPTOR, serverURL, security));
        if(version.equals("1.1.0")){
            Parameters.getOrCreate(WFSFeatureStoreFactory.VERSION, parameters).setValue(version);
        }else{
            throw new IllegalArgumentException("unknowned version : "+ version);
        }
        Parameters.getOrCreate(WFSFeatureStoreFactory.POST_REQUEST, parameters).setValue(false);
    }

    public WebFeatureClient(final URL serverURL, final ClientSecurity security, final WFSVersion version, final boolean usePost) {
        this(create(WFSFeatureStoreFactory.PARAMETERS_DESCRIPTOR, serverURL, security));
        if(version == null){
            throw new IllegalArgumentException("unknowned version : "+ version);
        }
        Parameters.getOrCreate(WFSFeatureStoreFactory.VERSION, parameters).setValue(version.getCode());
        Parameters.getOrCreate(WFSFeatureStoreFactory.POST_REQUEST, parameters).setValue(usePost);
    }

    public WebFeatureClient(final ParameterValueGroup params) {
        super(params);
        Parameters.getOrCreate(WFSFeatureStoreFactory.VERSION, parameters).setValue("1.1.0");
        parameters.parameter(WFSFeatureStoreFactory.POST_REQUEST.getName().getCode());
    }

    @Override
    public WFSFeatureStoreFactory getFactory() {
        return (WFSFeatureStoreFactory)DataStores.getFactoryById(WFSFeatureStoreFactory.NAME);
    }

    public WFSVersion getVersion(){
        return WFSVersion.fromCode(Parameters.value(WFSFeatureStoreFactory.VERSION, parameters));
    }

    public boolean getUsePost(){
        return Parameters.value(WFSFeatureStoreFactory.POST_REQUEST, parameters);
    }

    public boolean getLongitudeFirst(){
        return Parameters.getOrCreate(WFSFeatureStoreFactory.LONGITUDE_FIRST, parameters).booleanValue();
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
    public WFSCapabilities getCapabilities() throws WebFeatureException {
        WFSCapabilities cap = capabilities;

        if (cap != null) {
            return cap;
        }
        // Thread to prevent infinite request on a server
        // TODO: This is costly - we should use an other mechanism.
        final AtomicReference<Exception> error = new AtomicReference<>();
        final Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    capabilities = (WFSCapabilities) WFSBindingUtilities.unmarshall(createGetCapabilities().getResponseStream(), getVersion());
                } catch (Exception ex) {
                    error.set(ex);
                }
            }
        };
        thread.start();
        final long start = System.currentTimeMillis();
        try {
            thread.join(TIMEOUT);
        } catch (InterruptedException ex) {
            // Someone doesn't want to let us sleep. Go back to work.
        }
        cap = capabilities;
        if (cap == null) {
            final Exception cause = error.get();
            if (cause == null) {
                throw new WebFeatureException("TimeOut error, the server takes too much time to answer.");
            }
            String message;
            try {
                message = "Can not parse server answer at URL " + createGetCapabilities().getURL().toString();
            } catch (MalformedURLException ex1) {
                message = "Malformed URL, can not get server answer.";
            }
            throw new WebFeatureException(message, cause);
        }

        return cap;
    }

    /**
     * Create a getCapabilities request.
     * @return GetCapabilitiesRequest : getCapabilities request.
     */
    public GetCapabilitiesRequest createGetCapabilities() {

        switch (getVersion()) {
            case v110:
                return new GetCapabilities110(serverURL.toString(),getClientSecurity());
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    /**
     * Create a describe feature request
     * @return DescribeFeatureTypeRequest : describe feature request.
     */
    public DescribeFeatureTypeRequest createDescribeFeatureType(){
        switch (getVersion()) {
            case v110:
                return new DescribeFeatureType110(serverURL.toString(),getClientSecurity());
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    /**
     * Create a get feature request
     * @return GetFeatureRequest : get feature request.
     */
    public GetFeatureRequest createGetFeature(){
        switch (getVersion()) {
            case v110:
                return new GetFeature110(serverURL.toString(),getClientSecurity());
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
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    public Insert createInsertElement(){
        switch (getVersion()) {
            case v110:
                return new Insert110();
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    public Update createUpdateElement(){
        switch (getVersion()) {
            case v110:
                return new Update110();
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    public Delete createDeleteElement(){
        switch (getVersion()) {
            case v110:
                return new Delete110();
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    public Native createNativeElement(){
        switch (getVersion()) {
            case v110:
                return new Native110();
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
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
    public VersionControl getVersioning(GenericName typeName) throws VersioningException {
        return store.getVersioning(typeName);
    }

    @Override
    public String[] getTypeNames() throws DataStoreException {
        return getStore().getTypeNames();
    }

    @Override
    public Set<GenericName> getNames() throws DataStoreException {
        return getStore().getNames();
    }

    @Override
    public void createFeatureType(GenericName typeName, FeatureType featureType) throws DataStoreException {
        getStore().createFeatureType(typeName, featureType);
    }

    @Override
    public void updateFeatureType(GenericName typeName, FeatureType featureType) throws DataStoreException {
        getStore().updateFeatureType(typeName, featureType);
    }

    @Override
    public void deleteFeatureType(GenericName typeName) throws DataStoreException {
        getStore().deleteFeatureType(typeName);
    }

    @Override
    public FeatureType getFeatureType(String typeName) throws DataStoreException {
        return getStore().getFeatureType(typeName);
    }

    @Override
    public FeatureType getFeatureType(GenericName typeName) throws DataStoreException {
        return getStore().getFeatureType(typeName);
    }

    @Override
    public FeatureType getFeatureType(Query query) throws DataStoreException, MismatchedFeatureException {
        return getStore().getFeatureType(query);
    }

    @Override
    public boolean isWritable(GenericName typeName) throws DataStoreException {
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
    public List<FeatureId> addFeatures(GenericName groupName, Collection<? extends Feature> newFeatures, Hints hints) throws DataStoreException {
        return getStore().addFeatures(groupName, newFeatures, hints);
    }

    @Override
    public void updateFeatures(GenericName groupName, Filter filter, PropertyDescriptor desc, Object value) throws DataStoreException {
        getStore().updateFeatures(groupName, filter, desc, value);
    }

    @Override
    public void updateFeatures(GenericName groupName, Filter filter, Map<? extends PropertyDescriptor, ? extends Object> values) throws DataStoreException {
        getStore().updateFeatures(groupName, filter, values);
    }

    @Override
    public void removeFeatures(GenericName groupName, Filter filter) throws DataStoreException {
        getStore().removeFeatures(groupName, filter);
    }

    @Override
    public FeatureReader getFeatureReader(Query query) throws DataStoreException {
        return getStore().getFeatureReader(query);
    }

    @Override
    public FeatureWriter getFeatureWriter(GenericName typeName, Filter filter, Hints hints) throws DataStoreException {
        return getStore().getFeatureWriter(typeName,filter,hints);
    }

    @Override
    public FeatureWriter getFeatureWriterAppend(GenericName typeName, Hints hints) throws DataStoreException {
        return getStore().getFeatureWriterAppend(typeName,hints);
    }

    @Override
    public void close() throws DataStoreException {
        getStore().close();
    }

    @Override
    public void addStorageListener(StorageListener listener) {
        getStore().addStorageListener(listener);
    }

    @Override
    public void removeStorageListener(StorageListener listener) {
        getStore().removeStorageListener(listener);
    }

    @Override
    public void refreshMetaModel() {
        getStore().refreshMetaModel();
    }

}
