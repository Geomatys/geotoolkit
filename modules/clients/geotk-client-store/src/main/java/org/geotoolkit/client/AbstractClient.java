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

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sis.metadata.iso.DefaultIdentifier;
import org.apache.sis.metadata.iso.DefaultMetadata;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.apache.sis.metadata.iso.identification.DefaultDataIdentification;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.referencing.NamedIdentifier;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.event.StoreEvent;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.security.ClientSecurity;
import org.geotoolkit.security.DefaultClientSecurity;
import org.geotoolkit.storage.StorageEvent;
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
public abstract class AbstractClient extends DataStore implements Client {

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.client");

    protected final Parameters parameters;
    protected final URL serverURL;

    private final Map<String,Object> userProperties = new HashMap<>();
    private String sessionId = null;


    public AbstractClient(final ParameterValueGroup params) {
        this.parameters = Parameters.castOrWrap(params);
        this.serverURL = parameters.getValue(AbstractClientProvider.URL);
        ArgumentChecks.ensureNonNull("server url", serverURL);
    }

    @Override
    public Metadata getMetadata() throws DataStoreException {
        //extract an identifier string from url
        String name = serverURL.getHost() + serverURL.getPath();
        final DefaultMetadata metadata = new DefaultMetadata();
        final DefaultDataIdentification identification = new DefaultDataIdentification();
        final NamedIdentifier identifier = new NamedIdentifier(new DefaultIdentifier(name));
        final DefaultCitation citation = new DefaultCitation(name);
        citation.setIdentifiers(Collections.singleton(identifier));
        identification.setCitation(citation);
        metadata.setIdentificationInfo(Collections.singleton(identification));
        metadata.transitionTo(DefaultMetadata.State.FINAL);
        return metadata;
    }

    @Override
    public Optional<ParameterValueGroup> getOpenParameters() {
        if (parameters != null) {
            //defensive copy
            return Optional.of(parameters.clone());
        }
        return Optional.empty();
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
            final URL url, final ClientSecurity security, final Integer timeout){
        final Parameters param = Parameters.castOrWrap(desc.createValue());
        param.getOrCreate(AbstractClientProvider.URL).setValue(url);
        if (security != null) {
            param.getOrCreate(AbstractClientProvider.SECURITY).setValue(security);
        }
        if (timeout != null) {
            param.getOrCreate(AbstractClientProvider.TIMEOUT).setValue(timeout);
        }
        return param;
    }

    @Override
    public void close() throws DataStoreException {
        //do nothing
    }

    /**
     * Forward a structure event to all listeners.
     * @param event , event to send to listeners.
     *
     * @todo should specify event type.
     */
    protected void sendEvent(final StorageEvent event) {
        listeners.fire(event, StoreEvent.class);
    }

    /**
     * Forward given event, changing the source by this object.
     * For implementation use only.
     */
    public void forwardEvent(StorageEvent event){
        sendEvent(event.copy(this));
    }

}
