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
package org.geotoolkit.wps.client;

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
import org.geotoolkit.security.ClientSecurity;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.wps.xml.WPSMarshallerPool;
import org.geotoolkit.wps.xml.v200.Execute;

/**
 * WPS Execute request.
 *
 * @author Quentin Boileau (Geomatys)
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class ExecuteRequest extends AbstractRequest {

    private final Execute content;

    /**
     * Constructor, initialize status, lineage and storage to false and output form to "document"
     */
    public ExecuteRequest(Execute content, final String serverURL, final ClientSecurity security, Integer timeout) {
        super(serverURL, security, null, timeout);
        this.content = content;
    }

    public Execute getContent() {
        return content;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public InputStream getResponseStream() throws IOException {
        final URLConnection conec = openPostConnection();
        conec.setDoOutput(true);
        conec.setRequestProperty("Content-Type", "text/xml");

        try (OutputStream connecStream = conec.getOutputStream();
                OutputStream securedStream = security.encrypt(connecStream)) {
            final Marshaller marshaller = WPSMarshallerPool.getInstance().acquireMarshaller();
            if (debug) {
                marshaller.marshal(content, System.out);// TODO : use loggers and logging level instead
            }
            marshaller.marshal(content, securedStream);
            WPSMarshallerPool.getInstance().recycle(marshaller);
            securedStream.flush();
        } catch (JAXBException ex) {
            throw new IOException(ex);
        }

        return openRichException(conec);
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
            WPSMarshallerPool.getInstance().recycle(unmarshaller);

            if (response instanceof JAXBElement) {
                return ((JAXBElement) response).getValue();
            }
        }

        return response;
    }
}
