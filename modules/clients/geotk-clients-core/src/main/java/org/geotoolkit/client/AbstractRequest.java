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
package org.geotoolkit.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.util.StringUtilities;
import org.geotoolkit.util.logging.Logging;


/**
 * Abstract implementation of {@link Request}. Defines methods to get the full url
 * of the request in REST Kvp mode.
 *
 * @author Johann Sorel (Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @module pending
 */
public abstract class AbstractRequest implements Request {
    /**
     * Logger specific for this implementation of {@link Request}.
     */
    private static final Logger LOGGER = Logging.getLogger(AbstractRequest.class);

    /**
     * The address of the web service.
     */
    protected final String serverURL;

    /**
     * Parameters of the request, for key-value-pair requests.
     */
    protected final Map<String, String> requestParameters = new HashMap<String, String>();

    protected AbstractRequest(final String serverURL) {
        this(serverURL,null);
    }

    protected AbstractRequest(String serverURL, final String subPath) {

        if(subPath != null){
            if(serverURL.endsWith("/")){
                serverURL = serverURL + subPath;
            }else{
                serverURL = serverURL + "/" + subPath;
            }
        }

        this.serverURL = serverURL;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public URL getURL() throws MalformedURLException {
        final StringBuilder sb = new StringBuilder();
        final List<String> keys = new ArrayList<String>(requestParameters.keySet());

        sb.append(serverURL);

        if(!keys.isEmpty() && !serverURL.contains("?")){
            sb.append("?");
        }

        if (!requestParameters.isEmpty()) {
            final char c = sb.charAt(sb.length()-1);
            if (!(c == '?' || c == '&')) {
                sb.append("&");
            }

            String key = keys.get(0);
            try {
                sb.append(StringUtilities.convertSpacesForUrl(key)).append('=')
                  .append(URLEncoder.encode(requestParameters.get(key), "UTF-8"));
            } catch (UnsupportedEncodingException ex) {
                // Should not occur.
                LOGGER.log(Level.FINER, ex.getLocalizedMessage(), ex);
            }

            for (int i = 1, n = keys.size(); i < n; i++) {
                key = keys.get(i);
                sb.append('&').append(StringUtilities.convertSpacesForUrl(key))
                        .append('=').append(StringUtilities.convertSpacesForUrl(requestParameters.get(key)));
            }
        }

        return new URL(sb.toString());
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public abstract InputStream getSOAPResponse() throws IOException;

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean equals(Object candidate) {
        if (!(candidate instanceof Request)) {
            return false;
        }
        try {
            return getURL().equals(((Request) candidate).getURL());
        } catch (MalformedURLException ex) {
            return false;
        }
    }
}
