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
import java.net.URLConnection;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import org.geotoolkit.client.AbstractRequest;
import org.geotoolkit.security.ClientSecurity;
import org.geotoolkit.wps.xml.WPSMarshallerPool;
import org.geotoolkit.wps.xml.v200.GetCapabilities;

/**
 * WPS GetCapabilities request.
 *
 * @author Quentin Boileau (Geomatys)
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class GetCapabilitiesRequest extends AbstractRequest {

    private GetCapabilities content;
    protected final boolean doGET;

    public GetCapabilitiesRequest(final String serverURL, final ClientSecurity security){
        this(serverURL, security, true, null);
    }

    public GetCapabilitiesRequest(final String serverURL, final ClientSecurity security, final boolean doGET, Integer timeout) {
        super(serverURL, security, null, timeout);
        this.doGET = doGET;
    }

    public GetCapabilities getContent() {
        return content;
    }

    public void setContent(GetCapabilities cap) {
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

        final GetCapabilities request = getContent();

        if (doGET) {

            //GET
            if (debug) {
                System.out.println("GET " + getURL());
            }
            URLConnection conec = openConnection();
            return conec.getInputStream();

        } else {

            //POST
            URLConnection conec = openPostConnection();
            conec.setDoOutput(true);
            conec.setRequestProperty("Content-Type", "text/xml");

            try (
                    final OutputStream stream = conec.getOutputStream();
                    final OutputStream securedStream = security.encrypt(stream)) {
                Marshaller marshaller = WPSMarshallerPool.getInstance().acquireMarshaller();
                marshaller.marshal(request, securedStream);
                WPSMarshallerPool.getInstance().recycle(marshaller);
            } catch (JAXBException ex) {
                throw new IOException(ex);
            }

            return security.decrypt(conec.getInputStream());
        }
    }

}
