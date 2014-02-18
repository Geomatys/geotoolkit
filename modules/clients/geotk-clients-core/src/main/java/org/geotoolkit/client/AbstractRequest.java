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
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;
import org.apache.sis.io.TableAppender;
import org.geotoolkit.security.ClientSecurity;
import org.geotoolkit.security.DefaultClientSecurity;
import org.geotoolkit.util.StringUtilities;
import org.apache.sis.util.logging.Logging;


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
     * if this value is used for a parameter, only the parameter name will be
     * added without '=' character appended.
     */
    protected static final String DONT_ENCODE_EQUAL = new String();

    /**
     * Client security.
     */
    protected final ClientSecurity security;

    /**
     * The address of the web service.
     */
    protected final String serverURL;

    /**
     * Rest adress might use sub adress path for requests.
     */
    protected final String subPath;

    /**
     * Parameters of the request, for key-value-pair requests.
     */
    protected final Map<String, String> requestParameters = new HashMap<String, String>();

    /**
     * The request header map that contains a set of key-value for HTTP header
     * fields (user-agent, referer, accept-language...)
     */
    protected final Map<String,String> headerMap = new HashMap<String, String>();

    protected int timeout;

    protected AbstractRequest(final Client server) {
        this(server, null);
    }

    protected AbstractRequest(final Client server, final String subPath) {
        this(server.getURL().toString(), server.getClientSecurity(), subPath);
        this.timeout = server.getTimeOutValue();
    }

    protected AbstractRequest(final String serverURL) {
        this(serverURL,null);
    }

    protected AbstractRequest(final String serverURL, final String subPath) {
        this(serverURL,null,subPath);
    }

    protected AbstractRequest(final String serverURL, final ClientSecurity security, final String subPath) {
        this.serverURL = serverURL;
        this.security = (security==null) ? DefaultClientSecurity.NO_SECURITY : security ;
        this.subPath = subPath;
        this.timeout = AbstractClientFactory.TIMEOUT.getDefaultValue();
    }

    /**
     * Child class may override this method to return different subpath on different
     * parameter values.
     * @return adress subpath
     */
    protected String getSubPath() {
        return subPath;
    }

    /**
     * Called by the getURL method to fill the request parameter map.
     * Subclasses should override this method rather then getURL.
     */
    protected void prepareParameters(){};

    /**
     * {@inheritDoc }
     */
    @Override
    public Map<String,String> getHeaderMap(){
        return headerMap;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public URL getURL() throws MalformedURLException {
        prepareParameters();

        String completeURL = this.serverURL;
        final String subpath = getSubPath();
        if (subpath != null) {
            if (completeURL.endsWith("/")) {
                if (subpath.startsWith("/")) {
                    completeURL = completeURL.substring(0, completeURL.length()-1);
                }
            } else {
                if (!subpath.startsWith("/")) {
                    completeURL = completeURL + '/';
                }
            }
            completeURL = completeURL + subpath;
        }

        final StringBuilder sb = new StringBuilder(completeURL);
        if (!requestParameters.isEmpty()) {
            if (!completeURL.contains("?")) {
                sb.append('?');
            }
            final char c = sb.charAt(sb.length()-1);
            if (!(c == '?' || c == '&')) {
                sb.append('&');
            }

            boolean firstKeyRead = false;
            for (Entry<String,String> entry : requestParameters.entrySet()) {
                final String key = entry.getKey();
                if (key == null) {
                    throw new MalformedURLException("A key in the given URL is null. Please check the URL. " +
                            "Here is the current decoding of the URL: "+ sb.toString());
                }
                if (firstKeyRead) {
                    sb.append('&');
                }
                try {
                    sb.append(URLEncoder.encode(key, "UTF-8"));
                    final String value = entry.getValue();
                    if(DONT_ENCODE_EQUAL != value){
                        sb.append('=');
                        if (value != null) {
                            sb.append(URLEncoder.encode(value, "UTF-8"));
                        }
                    }
                } catch (UnsupportedEncodingException ex) {
                    Logging.getLogger(AbstractRequest.class).warning("Unsupported charset encoding:" + ex.getMessage());
                }
                firstKeyRead = true;
            }
        }

        final URL url = new URL(sb.toString());

        //security
        return security.secure(url);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public InputStream getResponseStream() throws IOException{
        URLConnection cnx = getURL().openConnection();

        //Set all fields from the headerMap to the properties of this URLConnection.
        for(final Entry<String,String> entry : headerMap.entrySet()){
            cnx.setRequestProperty(entry.getKey(),entry.getValue());
        }

        //security
        cnx = security.secure(cnx);

        return openRichException(cnx);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean equals(final Object candidate) {
        if (!(candidate instanceof Request)) {
            return false;
        }
        try {
            //using equals on URL can block because it will try to resolve
            //the domain name.
            return getURL().toString().equals(((Request) candidate).getURL().toString());
        } catch (MalformedURLException ex) {
            return false;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + (this.serverURL != null ? this.serverURL.hashCode() : 0);
        hash = 41 * hash + (this.subPath != null ? this.subPath.hashCode() : 0);
        return hash;
    }

    protected InputStream openRichException(final URLConnection cnx) throws IOException {
        return openRichException(cnx, security, timeout);
    }

    public static InputStream openRichException(final URLConnection cnx, final ClientSecurity security) throws IOException {
        return openRichException(cnx, security, AbstractClientFactory.TIMEOUT.getDefaultValue());
    }

    public static InputStream openRichException(final URLConnection cnx, final ClientSecurity security, int timeout) throws IOException {
        try {
            cnx.setConnectTimeout(timeout);
            cnx.setReadTimeout(timeout*2);
            InputStream stream = cnx.getInputStream();
            //security
            stream = security.decrypt(stream);

            if ("gzip".equalsIgnoreCase(cnx.getContentEncoding())) {
                return new GZIPInputStream(stream);
            } else {
                return stream;
            }
        } catch(IOException ex) {
            final StringWriter writer = new StringWriter();
            final TableAppender tablewriter = new TableAppender(writer);

            tablewriter.writeHorizontalSeparator();

            for(Entry<String,List<String>> entry : cnx.getHeaderFields().entrySet()){
                tablewriter.append((entry.getKey()!= null)? entry.getKey() : "null");
                tablewriter.append('\t');
                tablewriter.append(StringUtilities.toCommaSeparatedValues(entry.getValue()));
                tablewriter.append('\n');
            }
            tablewriter.writeHorizontalSeparator();

            try {
                tablewriter.flush();
            } catch (IOException e) {
                //will never happen is this case
                e.printStackTrace();
            }

            throw new IOException('\n'+ writer.toString(), ex);
        }
    }

}
