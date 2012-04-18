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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List;
import java.util.Map;

import org.geotoolkit.security.ClientSecurity;
import org.geotoolkit.security.DefaultClientSecurity;
import org.geotoolkit.util.ArgumentChecks;
import org.geotoolkit.util.logging.Logging;

/**
 * Default implementation of a Server.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class AbstractServer implements Server{

    private static final Logger LOGGER = Logging.getLogger(AbstractServer.class);
    
    protected final URL serverURL;
    protected final ClientSecurity securityManager;
    
    private final Map<String,Object> parameters = new HashMap<String,Object>();    
    private String sessionId = null;

    public AbstractServer(URL serverURL) {
        this(serverURL,null);
    }

    public AbstractServer(final URL serverURL, final ClientSecurity securityManager) {
        ArgumentChecks.ensureNonNull("server url", serverURL);
        this.serverURL = serverURL;
        this.securityManager = (securityManager == null) ?  DefaultClientSecurity.NO_SECURITY : securityManager;
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
        return securityManager;
    }
    
     /**
     * {@inheritDoc }
     */
    @Override
    public void setUserProperty(final String key,final Object value){
        parameters.put(key, value);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Object getUserProperty(final String key){
        return parameters.get(key);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Map<String, Object> getUserProperties() {
        return parameters;
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
        } else {
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
}
