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
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.geotoolkit.client.AbstractRequest;
import org.geotoolkit.security.ClientSecurity;
import org.geotoolkit.wps.xml.WPSMarshallerPool;
import org.geotoolkit.wps.xml.DescribeProcess;

/**
 * WPS DescribeProcess mutable request.
 *
 * @author Quentin Boileau (Geomatys)
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class DescribeProcessRequest extends AbstractRequest {

    private DescribeProcess content;
    protected final boolean doGET;

    public DescribeProcessRequest(final String serverURL, final ClientSecurity security){
        this(serverURL, security, true);
    }

    public DescribeProcessRequest(final String serverURL, final ClientSecurity security, final boolean doGET){
        super(serverURL,security,null);
        this.doGET = doGET;
    }

    public DescribeProcess getContent() {
        return content;
    }

    public void setContent(DescribeProcess value) {
        this.content = value;
    }

    /**
     * {@inheritDoc }
     */
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

        final DescribeProcess request = getContent();

        if (doGET) {

            //GET
            final URL url = getURL(); //build GET request
            URLConnection conec = url.openConnection();
            conec.setReadTimeout(timeout);
            conec.setConnectTimeout(timeout);
            conec = security.secure(conec);
            return conec.getInputStream();
        } else {

            //POST
            final URL url = new URL(serverURL);
            URLConnection conec = url.openConnection();
            conec = security.secure(conec);
            conec.setDoOutput(true);
            conec.setRequestProperty("Content-Type", "text/xml");

            OutputStream stream = conec.getOutputStream();
            stream = security.encrypt(stream);
            Marshaller marshaller = null;
            try {
                marshaller = WPSMarshallerPool.getInstance().acquireMarshaller();
                marshaller.marshal(request, stream);
                WPSMarshallerPool.getInstance().recycle(marshaller);
            } catch (JAXBException ex) {
                throw new IOException(ex);
            }
            stream.close();
            return security.decrypt(conec.getInputStream());
        }
    }

}
