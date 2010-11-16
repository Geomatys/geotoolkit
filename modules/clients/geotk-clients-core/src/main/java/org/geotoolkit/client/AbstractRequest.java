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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.geotoolkit.io.TableWriter;
import org.geotoolkit.util.StringUtilities;


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

    protected AbstractRequest(final String serverURL) {
        this(serverURL,null);
    }

    protected AbstractRequest(final String serverURL, final String subPath) {
        this.serverURL = serverURL;
        this.subPath = subPath;
    }

    /**
     * Child class may override this method to return different subpath on different
     * parameter values.
     * @return adress subpath
     */
    protected String getSubPath() {
        return subPath;
    }

    protected void prepareParameters(){};

    /**
     * {@inheritDoc }
     */
    @Override
    public URL getURL() throws MalformedURLException {
        prepareParameters();

        String completeURL = this.serverURL;
        final String subPath = getSubPath();
        if (subPath != null) {
            if (completeURL.endsWith("/")) {
                if (subPath.startsWith("/")) {
                    completeURL = completeURL.substring(0, completeURL.length()-1);
                }
            } else {
                if (!subPath.startsWith("/")) {
                    completeURL = completeURL + '/';
                }
            }
            completeURL = completeURL + subPath;
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
                sb.append(StringUtilities.convertSpacesForUrl(key));
                final String value = entry.getValue();
                if(DONT_ENCODE_EQUAL != value){
                    sb.append('=');
                    if (value != null) {
                        sb.append(StringUtilities.convertSpacesForUrl(value));
                    }
                }
                firstKeyRead = true;
            }
        }

        return new URL(sb.toString());
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public InputStream getResponseStream() throws IOException{
        final URLConnection cnx = getURL().openConnection();
        return openRichException(cnx);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean equals(Object candidate) {
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

    protected InputStream openRichException(URLConnection cnx) throws IOException {
        try {
            return cnx.getInputStream();
        } catch(IOException ex) {
            final StringWriter writer = new StringWriter();
            final TableWriter tablewriter = new TableWriter(writer);

            tablewriter.nextLine(TableWriter.DOUBLE_HORIZONTAL_LINE);

            for(Entry<String,List<String>> entry : cnx.getHeaderFields().entrySet()){
                tablewriter.write((entry.getKey()!= null)? entry.getKey() : "null");
                tablewriter.write('\t');
                tablewriter.write(StringUtilities.toCommaSeparatedValues(entry.getValue()));
                tablewriter.write('\n');
            }
            tablewriter.nextLine(TableWriter.DOUBLE_HORIZONTAL_LINE);

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
