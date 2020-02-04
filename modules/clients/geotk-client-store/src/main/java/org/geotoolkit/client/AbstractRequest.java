/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2015, Geomatys
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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.zip.GZIPInputStream;
import org.apache.sis.io.TableAppender;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.security.ClientSecurity;
import org.geotoolkit.security.DefaultClientSecurity;
import org.geotoolkit.util.StringUtilities;


/**
 * Abstract implementation of {@link Request}. Defines methods to get the full url
 * of the request in REST Kvp mode.
 *
 * @author Johann Sorel (Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @module
 */
public abstract class AbstractRequest implements Request {
    /**
     * if this value is used for a parameter, only the parameter name will be
     * added without '=' character appended.
     */
    protected static final String DONT_ENCODE_EQUAL = new String();

    /**
     * Client security, never null.
     */
    protected ClientSecurity security;

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
    protected final Map<String, String> requestParameters = new HashMap<>();

    /**
     * The request header map that contains a set of key-value for HTTP header
     * fields (user-agent, referer, accept-language...)
     */
    protected final Map<String,String> headerMap = new HashMap<>();

    /**
     * Request timeout in milliseconds
     */
    protected long timeout;

    protected boolean debug = false;

    protected AbstractRequest(final Client server) {
        this(server, null);
    }

    protected AbstractRequest(final Client server, final String subPath) {
        this(server.getURL().toString(), server.getClientSecurity(), subPath, server.getTimeOutValue());
    }

    protected AbstractRequest(final String serverURL) {
        this(serverURL,null);
    }

    protected AbstractRequest(final String serverURL, final String subPath) {
        this(serverURL,null,subPath, null);
    }

    protected AbstractRequest(final String serverURL, final ClientSecurity security, final String subPath) {
        this(serverURL,security,subPath, null);
    }

    protected AbstractRequest(final String serverURL, final ClientSecurity security, final String subPath, final Integer timeout) {
        this.serverURL = serverURL;
        this.security = (security==null) ? DefaultClientSecurity.NO_SECURITY : security ;
        this.subPath = subPath;
        if (timeout != null) {
            if (timeout < 0) {
                throw new IllegalArgumentException("Time out value must be > 0");
            }
            this.timeout = timeout;
        } else {
            this.timeout = AbstractClientProvider.TIMEOUT.getDefaultValue();
        }
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
     * Returns request timeout.
     *
     * @return timeout in milliseconds.
     */
    @Override
    public long getTimeout() {
        return timeout;
    }

    /**
     * Set request timeout.
     *
     * @param timeout in milliseconds.
     */
    @Override
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    /**
     * Get client securing object.
     *
     * @return ClientSecurity, never null.
     */
    public ClientSecurity getClientSecurity() {
        return security;
    }

    /**
     * Set client securing object.
     *
     * @param security not null
     */
    public void setClientSecurity(ClientSecurity security) {
        ArgumentChecks.ensureNonNull("security", security);
        this.security = security;
    }

    /**
     * Active the printing of all the sent/received request.
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
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
                    Logging.getLogger("org.geotoolkit.client").log(Level.WARNING, "Unsupported charset encoding:{0}", ex.getMessage());
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

        return followLink(cnx);
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

    /**
     * Open an url connection from getURL method.
     * connection timeout and security are configured.
     *
     * @return URLConnection
     */
    protected URLConnection openConnection() throws MalformedURLException, IOException {
        URLConnection cnx = getURL().openConnection();
        cnx = security.secure(cnx);
        setTimeout(cnx, timeout);
        return cnx;
    }

    /**
     * Open an url connection from base server URL.
     * connection timeout and security are configured.
     *
     * @return URLConnection
     */
    protected URLConnection openPostConnection() throws MalformedURLException, IOException {
        final URL url = security.secure(new URL(serverURL));
        URLConnection cnx = url.openConnection();
        cnx = security.secure(cnx);
        setTimeout(cnx, timeout);
        return cnx;
    }

    /**
     * Java do not follow urls if there is a change in protocol.
     * See : http://bugs.java.com/bugdatabase/view_bug.do?bug_id=4620571
     */
    protected InputStream followLink(URLConnection cnx) throws IOException {

        while(cnx instanceof HttpURLConnection) {
            HttpURLConnection httpCnx = (HttpURLConnection) cnx;

            final InputStream is = openRichException(httpCnx);
            final int status = httpCnx.getResponseCode();
            final boolean redirect = status == HttpURLConnection.HTTP_MOVED_TEMP
                                  || status == HttpURLConnection.HTTP_MOVED_PERM
                                  || status == HttpURLConnection.HTTP_SEE_OTHER;

            if (redirect) {
                // get redirection url
                final String newUrl = httpCnx.getHeaderField("Location");
                // get new cookies
                final String cookies = httpCnx.getHeaderField("Set-Cookie");
                is.close();

                // open redirection
                httpCnx = (HttpURLConnection) new URL(newUrl).openConnection();
                cnx = httpCnx;
                httpCnx.setRequestProperty("Cookie", cookies);

                //Set all fields from the headerMap to the properties of this URLConnection.
                for(final Entry<String,String> entry : headerMap.entrySet()){
                    httpCnx.setRequestProperty(entry.getKey(),entry.getValue());
                }
                //security
                httpCnx = (HttpURLConnection)security.secure(httpCnx);
            }else{
                return is;
            }
        }

        return openRichException(cnx);
    }

    protected InputStream openRichException(final URLConnection cnx) throws IOException {
        return openRichException(cnx, security, timeout);
    }

    public static InputStream openRichException(final URLConnection cnx, final ClientSecurity security) throws IOException {
        return openRichException(cnx, security, AbstractClientProvider.TIMEOUT.getDefaultValue());
    }

    public static InputStream openRichException(final URLConnection cnx, final ClientSecurity security, long timeout) throws IOException {
        try {
            setTimeout(cnx, timeout, timeout*2);
            InputStream stream = cnx.getInputStream();
            //security
            stream = security.decrypt(stream);

            if ("gzip".equalsIgnoreCase(cnx.getContentEncoding())) {
                return new GZIPInputStream(stream);
            } else {
                return stream;
            }
        } catch (IOException ex) {
            final StringWriter writer = new StringWriter();
            final TableAppender tablewriter = new TableAppender(writer);

            tablewriter.appendHorizontalSeparator();

            for(Entry<String,List<String>> entry : cnx.getHeaderFields().entrySet()){
                tablewriter.append((entry.getKey()!= null)? entry.getKey() : "null");
                tablewriter.append('\t');
                tablewriter.append(StringUtilities.toCommaSeparatedValues(entry.getValue()));
                tablewriter.append('\n');
            }
            tablewriter.appendHorizontalSeparator();

            try {
                tablewriter.flush();
            } catch (IOException e) {
                //will never happen is this case
                e.printStackTrace();
            }
            if (cnx instanceof HttpURLConnection) {
                HttpURLConnection httpCnx = (HttpURLConnection) cnx;
                if (httpCnx.getErrorStream() != null) {
                    String serverError = IOUtilities.toString(httpCnx.getErrorStream());
                    writer.append("Error response from server:\n" + serverError);
                } else {
                    writer.append("No error response from server.");
                }
            }
            writer.append("\n On URL : " + cnx.getURL());
            throw new IOException('\n'+ writer.toString(), ex);
        }
    }

    private static void setTimeout(final URLConnection target, final long timeout) {
        setTimeout(target, timeout, timeout);
    }

    private static void setTimeout(final URLConnection target, final long connectTimeout, final long readTimeout) {
        target.setConnectTimeout(Math.toIntExact(Math.min(Integer.MAX_VALUE, connectTimeout)));
        target.setReadTimeout(Math.toIntExact(Math.min(Integer.MAX_VALUE, readTimeout)));
    }
}
