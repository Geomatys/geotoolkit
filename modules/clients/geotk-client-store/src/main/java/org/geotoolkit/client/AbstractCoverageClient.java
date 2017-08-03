/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.storage.coverage.AbstractCoverageStore;
import org.geotoolkit.security.ClientSecurity;
import org.geotoolkit.security.DefaultClientSecurity;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterNotFoundException;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class AbstractCoverageClient extends AbstractCoverageStore {

    private final Map<String,Object> userProperties = new HashMap<String,Object>();
    protected final URL serverURL;
    private String sessionId = null;

    public AbstractCoverageClient(ParameterValueGroup params){
        super(params);
        this.serverURL = parameters.getValue(AbstractClientFactory.URL);
        ArgumentChecks.ensureNonNull("server url", serverURL);
    }

    /**
     * {@inheritDoc}
     */
    public URL getURL() {
        return serverURL;
    }

    /**
     * {@inheritDoc}
     */
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
    public ClientSecurity getClientSecurity() {
        ClientSecurity securityManager = null;
        try {
            securityManager = parameters.getValue(AbstractClientFactory.SECURITY);
        } catch (ParameterNotFoundException ex) {
            // do nothing
        }
        return (securityManager == null) ?  DefaultClientSecurity.NO_SECURITY : securityManager;
    }

    public int getTimeOutValue() {
        Integer timeout = null;
        try {
            timeout = parameters.getValue(AbstractClientFactory.TIMEOUT);
        } catch (ParameterNotFoundException ex) {
            // do nothing
        }
        return (timeout == null) ?  AbstractClientFactory.TIMEOUT.getDefaultValue() : timeout;
    }

    /**
     * {@inheritDoc }
     */
    public void setUserProperty(final String key,final Object value){
        userProperties.put(key, value);
    }

    /**
     * {@inheritDoc }
     */
    public Object getUserProperty(final String key){
        return userProperties.get(key);
    }

    /**
     * {@inheritDoc }
     */
    public Map<String, Object> getUserProperties() {
        return userProperties;
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

    protected static Parameters create(final ParameterDescriptorGroup desc,
            final URL url, final ClientSecurity security){
        final Parameters param = Parameters.castOrWrap(desc.createValue());
        param.getOrCreate(AbstractClientFactory.URL).setValue(url);
        if (security != null) {
            param.getOrCreate(AbstractClientFactory.SECURITY).setValue(security);
        }
        return param;
    }

}
