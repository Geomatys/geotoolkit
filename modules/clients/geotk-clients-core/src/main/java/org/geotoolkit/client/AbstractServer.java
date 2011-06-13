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
import java.util.logging.Level;
import java.util.logging.Logger;

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
     * @return default server logger.
     */
    protected Logger getLogger(){
        return LOGGER;
    }
    
}
