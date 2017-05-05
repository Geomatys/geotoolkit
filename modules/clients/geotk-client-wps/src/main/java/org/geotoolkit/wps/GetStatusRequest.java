/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2016, Geomatys
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
import java.net.URL;
import java.net.URLConnection;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.geotoolkit.client.AbstractRequest;
import org.geotoolkit.security.ClientSecurity;
import org.geotoolkit.wps.xml.WPSMarshallerPool;
import org.geotoolkit.wps.xml.v200.GetStatus;

/**
 * WPS GetStatus request.
 *
 * Request is only for WPS 2.0.0
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class GetStatusRequest extends AbstractRequest {

    private GetStatus content;
    protected final boolean doGET;

    public GetStatusRequest(final String serverURL, final ClientSecurity security){
        this(serverURL, security, true);
    }

    public GetStatusRequest(final String serverURL, final ClientSecurity security, final boolean doGET){
        super(serverURL, security, null);
        this.doGET = doGET;
    }

    public GetStatus getContent() {
        return content;
    }

    public void setContent(GetStatus cap) {
        this.content = cap;
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

        final GetStatus request = getContent();

        if (doGET) {

            //GET
            final URL url = getURL(); //build GET request
            final URLConnection conec = security.secure(url.openConnection());
            return conec.getInputStream();

        } else {

            //POST
            final URL url = new URL(serverURL);
            final URLConnection conec = security.secure(url.openConnection());
            conec.setDoOutput(true);
            conec.setRequestProperty("Content-Type", "text/xml");

            try (OutputStream stream = security.encrypt(conec.getOutputStream())) {
                Marshaller marshaller = WPSMarshallerPool.getInstance().acquireMarshaller();
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
            response = unmarshaller.unmarshal(in);
            if (response instanceof JAXBElement) {
                return ((JAXBElement) response).getValue();
            }
            WPSMarshallerPool.getInstance().recycle(unmarshaller);
        }

        return response;
    }

}
