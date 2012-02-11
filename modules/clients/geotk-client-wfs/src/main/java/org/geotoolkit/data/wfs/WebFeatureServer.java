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
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.client.AbstractServer;
import org.geotoolkit.data.DataStore;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.StorageListener;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryCapabilities;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.data.wfs.v110.*;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.SchemaException;
import org.geotoolkit.security.ClientSecurity;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.wfs.xml.WFSBindingUtilities;
import org.geotoolkit.wfs.xml.WFSVersion;
import org.geotoolkit.wfs.xml.v110.WFSCapabilitiesType;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.identity.FeatureId;
import org.opengis.geometry.Envelope;

/**
 * WFS server, used to aquiere capabilites and requests objects.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class WebFeatureServer extends AbstractServer implements DataStore{

    private static final Logger LOGGER = Logging.getLogger(WebFeatureServer.class);

    private final WFSVersion version;
    private WFSCapabilitiesType capabilities;
    private WFSDataStore store = null; //created when needed
    private final boolean usePost;

    public WebFeatureServer(final URL serverURL, final String version) {
        this(serverURL,null,version);
    }
    
    public WebFeatureServer(final URL serverURL, final ClientSecurity security, final String version) {
        super(serverURL,security);
        if(version.equals("1.1.0")){
            this.version = WFSVersion.v110;
        }else{
            throw new IllegalArgumentException("unkonwed version : "+ version);
        }
        this.capabilities = null;
        this.usePost = false;
    }
    
    public WebFeatureServer(final URL serverURL, final ClientSecurity security, final WFSVersion version, final boolean usePost) {
        super(serverURL,security);
        this.version = version;
        if(version == null){
            throw new IllegalArgumentException("unkonwed version : "+ version);
        }
        this.capabilities = null;
        this.usePost = usePost;
    }
    
    private synchronized DataStore getStore() {
        if(store == null){
            try {
                store = new WFSDataStore(serverURL.toURI(), usePost);
            } catch (MalformedURLException ex) {
                LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            } catch (URISyntaxException ex) {
                LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
        return store;
    }
    
    /**
     * @return WFSCapabilitiesType : WFS server capabilities
     */
    public WFSCapabilitiesType getCapabilities() {

        if (capabilities != null) {
            return capabilities;
        }
        //Thread to prevent infinite request on a server
        final Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    capabilities = WFSBindingUtilities.unmarshall(createGetCapabilities().getURL(), version);
                } catch (Exception ex) {
                    capabilities = null;
                    try {
                        LOGGER.log(Level.WARNING, "Wrong URL, the server doesn't answer : " + createGetCapabilities().getURL().toString(), ex);
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
     * @return WFSVersion : currently used version for this server
     */
    public WFSVersion getVersion() {
        return version;
    }

    /**
     * Create a getCapabilities request.
     * @return GetCapabilitiesRequest : getCapabilities request.
     */
    public GetCapabilitiesRequest createGetCapabilities() {

        switch (version) {
            case v110:
                return new GetCapabilities110(serverURL.toString(),securityManager);
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    /**
     * Create a describe feature request
     * @return DescribeFeatureTypeRequest : describe feature request.
     */
    public DescribeFeatureTypeRequest createDescribeFeatureType(){
        switch (version) {
            case v110:
                return new DescribeFeatureType110(serverURL.toString(),securityManager);
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    /**
     * Create a get feature request
     * @return GetFeatureRequest : get feature request.
     */
    public GetFeatureRequest createGetFeature(){
        switch (version) {
            case v110:
                return new GetFeature110(serverURL.toString(),securityManager);
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    /**
     * Create a transaction request
     * @return TransactionRequest : transaction request.
     */
    public TransactionRequest createTransaction(){
        switch (version) {
            case v110:
                return new Transaction110(serverURL.toString(),securityManager);
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    public Insert createInsertElement(){
        switch (version) {
            case v110:
                return new Insert110();
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    public Update createUpdateElement(){
        switch (version) {
            case v110:
                return new Update110();
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    public Delete createDeleteElement(){
        switch (version) {
            case v110:
                return new Delete110();
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    public Native createNativeElement(){
        switch (version) {
            case v110:
                return new Native110();
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // DataStore methods ///////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    @Override
    public Session createSession(boolean asynchrone) {
        return getStore().createSession(asynchrone);
    }

    @Override
    public String[] getTypeNames() throws DataStoreException {
        return getStore().getTypeNames();
    }

    @Override
    public Set<Name> getNames() throws DataStoreException {
        return getStore().getNames();
    }

    @Override
    public void createSchema(Name typeName, FeatureType featureType) throws DataStoreException {
        getStore().createSchema(typeName, featureType);
    }

    @Override
    public void updateSchema(Name typeName, FeatureType featureType) throws DataStoreException {
        getStore().updateSchema(typeName, featureType);
    }

    @Override
    public void deleteSchema(Name typeName) throws DataStoreException {
        getStore().deleteSchema(typeName);
    }

    @Override
    public FeatureType getFeatureType(String typeName) throws DataStoreException {
        return getStore().getFeatureType(typeName);
    }

    @Override
    public FeatureType getFeatureType(Name typeName) throws DataStoreException {
        return getStore().getFeatureType(typeName);
    }

    @Override
    public FeatureType getFeatureType(Query query) throws DataStoreException, SchemaException {
        return getStore().getFeatureType(query);
    }

    @Override
    public boolean isWritable(Name typeName) throws DataStoreException {
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
    public List<FeatureId> addFeatures(Name groupName, Collection<? extends Feature> newFeatures) throws DataStoreException {
        return getStore().addFeatures(groupName, newFeatures);
    }

    @Override
    public List<FeatureId> addFeatures(Name groupName, Collection<? extends Feature> newFeatures, Hints hints) throws DataStoreException {
        return getStore().addFeatures(groupName, newFeatures, hints);
    }

    @Override
    public void updateFeatures(Name groupName, Filter filter, PropertyDescriptor desc, Object value) throws DataStoreException {
        getStore().updateFeatures(groupName, filter, desc, value);
    }

    @Override
    public void updateFeatures(Name groupName, Filter filter, Map<? extends PropertyDescriptor, ? extends Object> values) throws DataStoreException {
        getStore().updateFeatures(groupName, filter, values);
    }

    @Override
    public void removeFeatures(Name groupName, Filter filter) throws DataStoreException {
        getStore().removeFeatures(groupName, filter);
    }

    @Override
    public FeatureReader getFeatureReader(Query query) throws DataStoreException {
        return getStore().getFeatureReader(query);
    }

    @Override
    public FeatureWriter getFeatureWriter(Name typeName, Filter filter) throws DataStoreException {
        return getStore().getFeatureWriter(typeName,filter);
    }

    @Override
    public FeatureWriter getFeatureWriter(Name typeName, Filter filter, Hints hints) throws DataStoreException {
        return getStore().getFeatureWriter(typeName,filter,hints);
    }

    @Override
    public FeatureWriter getFeatureWriterAppend(Name typeName) throws DataStoreException {
        return getStore().getFeatureWriterAppend(typeName);
    }

    @Override
    public FeatureWriter getFeatureWriterAppend(Name typeName, Hints hints) throws DataStoreException {
        return getStore().getFeatureWriterAppend(typeName,hints);
    }

    @Override
    public void dispose() {
        getStore().dispose();
    }

    @Override
    public void addStorageListener(StorageListener listener) {
        getStore().addStorageListener(listener);
    }

    @Override
    public void removeStorageListener(StorageListener listener) {
        getStore().removeStorageListener(listener);
    }
    
}
