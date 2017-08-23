/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2017, Geomatys
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
package org.geotoolkit.wps;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.URLConnection;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.geotoolkit.client.AbstractRequest;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.security.ClientSecurity;
import org.geotoolkit.wps.xml.WPSMarshallerPool;
import org.geotoolkit.wps.xml.v200.Dismiss;

/**
 * WPS Dismiss request.
 *
 * Request is only for WPS 2.0.0 with dismiss extension
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class DismissRequest extends AbstractRequest {

    private Dismiss content;
    protected final boolean doGET;

    public DismissRequest(final String serverURL, final ClientSecurity security){
        this(serverURL, security, true, null);
    }

    public DismissRequest(final String serverURL, final ClientSecurity security, final boolean doGET, Integer timeout) {
        super(serverURL, security, null, timeout);
        this.doGET = doGET;
    }

    public Dismiss getContent() {
        return content;
    }

    public void setContent(Dismiss dismiss) {
        this.content = dismiss;
    }

    @Override
    protected void prepareParameters() {
        super.prepareParameters();
        requestParameters.putAll(content.toKVP());
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public InputStream getResponseStream() throws IOException {

        final Dismiss request = getContent();

        if (doGET) {

            //GET
            if (debug) {
                System.out.println("GET " + getURL());
            }
            final URLConnection conec = openConnection();
            return conec.getInputStream();

        } else {

            //POST
            final URLConnection conec = openPostConnection();
            conec.setDoOutput(true);
            conec.setRequestProperty("Content-Type", "text/xml");

            try (OutputStream stream = security.encrypt(conec.getOutputStream())) {
                Marshaller marshaller = WPSMarshallerPool.getInstance().acquireMarshaller();
                if (debug) {
                    marshaller.marshal(request, System.out);
                }
                marshaller.marshal(request, stream);
                WPSMarshallerPool.getInstance().recycle(marshaller);
            } catch (JAXBException ex) {
                throw new IOException(ex);
            }
            return security.decrypt(conec.getInputStream());
        }
    }

    /**
     * Send the request to the server URL in POST mode and return the unmarshalled response.
     *
     * @return Response of this request
     * @throws IOException if can't reach the server
     * @throws JAXBException if there is an error during Marshalling/Unmarshalling request or response.
     */
    public Object getResponse() throws JAXBException, IOException {

        // Parse the response
        Object response;
        try (final InputStream in = getResponseStream()) {
            final Unmarshaller unmarshaller = WPSMarshallerPool.getInstance().acquireUnmarshaller();
            if (debug) {
                String s = IOUtilities.toString(in);
                System.out.println(s);
                response = unmarshaller.unmarshal(new StringReader(s));
            } else {
                response = unmarshaller.unmarshal(in);
            }
            if (response instanceof JAXBElement) {
                return ((JAXBElement) response).getValue();
            }
            WPSMarshallerPool.getInstance().recycle(unmarshaller);
        }

        return response;
    }
}
