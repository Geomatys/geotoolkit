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
package org.geotoolkit.client;

import java.net.URLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.sis.storage.DataStoreException;

import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.security.ClientSecurity;
import org.geotoolkit.security.DefaultClientSecurity;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.storage.DataStore;
import org.geotoolkit.storage.StorageEvent;
import org.geotoolkit.storage.StorageListener;
import org.opengis.metadata.Metadata;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterNotFoundException;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Default implementation of a Client.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public abstract class AbstractClient extends DataStore implements Client{

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.client");

    protected final ParameterValueGroup parameters;
    protected final URL serverURL;

    private final Map<String,Object> userProperties = new HashMap<String,Object>();
    private String sessionId = null;
    protected final Set<StorageListener> listeners = new HashSet<>();


    public AbstractClient(final ParameterValueGroup params) {
        this.parameters = params;
        this.serverURL = Parameters.value(AbstractClientFactory.URL,params);
        ArgumentChecks.ensureNonNull("server url", serverURL);
    }

    @Override
    public Metadata getMetadata() throws DataStoreException {
        return null;
    }

    @Override
    public ParameterValueGroup getConfiguration() {
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
            securityManager = Parameters.value(AbstractClientFactory.SECURITY,parameters);
        } catch (ParameterNotFoundException ex) {
            // do nothing
        }
        return (securityManager == null) ?  DefaultClientSecurity.NO_SECURITY : securityManager;
    }

    @Override
    public int getTimeOutValue() {
        Integer timeout = null;
        try {
            timeout = Parameters.value(AbstractClientFactory.TIMEOUT,parameters);
        } catch (ParameterNotFoundException ex) {
            // do nothing
        }
        return (timeout == null) ?  AbstractClientFactory.TIMEOUT.getDefaultValue() : timeout;
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

    /**
     * @return default server logger.
     */
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
        final ParameterValueGroup param = desc.createValue();
        param.parameter(AbstractClientFactory.URL.getName().getCode()).setValue(url);
        if (security != null) {
            Parameters.getOrCreate(AbstractClientFactory.SECURITY, param).setValue(security);
        }
        return param;
    }

    @Override
    public void close() throws DataStoreException {
        //do nothing
    }


    public void addStorageListener(final StorageListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    public void removeStorageListener(final StorageListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    /**
     * Forward a structure event to all listeners.
     * @param event , event to send to listeners.
     */
    protected void sendStructureEvent(final StorageEvent event){
        final StorageListener[] lst;
        synchronized (listeners) {
            lst = listeners.toArray(new StorageListener[listeners.size()]);
        }
        for(final StorageListener listener : lst){
            listener.structureChanged(event);
        }
    }

    /**
     * Forward a data event to all listeners.
     * @param event , event to send to listeners.
     */
    protected void sendContentEvent(final StorageEvent event){
        final StorageListener[] lst;
        synchronized (listeners) {
            lst = listeners.toArray(new StorageListener[listeners.size()]);
        }
        for(final StorageListener listener : lst){
            listener.contentChanged(event);
        }
    }

    /**
     * Forward given event, changing the source by this object.
     * For implementation use only.
     * @param event
     */
    public void forwardStructureEvent(StorageEvent event){
        sendStructureEvent(event.copy(this));
    }

    /**
     * Forward given event, changing the source by this object.
     * For implementation use only.
     * @param event
     */
    public void forwardContentEvent(StorageEvent event){
        sendContentEvent(event.copy(this));
    }

}
