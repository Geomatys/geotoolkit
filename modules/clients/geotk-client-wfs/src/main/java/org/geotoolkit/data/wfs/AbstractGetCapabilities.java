/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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
package org.geotoolkit.data.wfs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.geotoolkit.client.AbstractRequest;
import org.geotoolkit.ows.xml.v100.AcceptVersionsType;
import org.geotoolkit.security.ClientSecurity;
import org.geotoolkit.wfs.xml.WFSMarshallerPool;
import org.geotoolkit.wfs.xml.v110.GetCapabilitiesType;


/**
 * Abstract get capabilities request.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class AbstractGetCapabilities extends AbstractRequest implements GetCapabilitiesRequest{

    protected final String version;

    protected AbstractGetCapabilities(final String serverURL,final String version, final ClientSecurity security){
        super(serverURL,security,null);
        this.version = version;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public URL getURL() throws MalformedURLException {
        requestParameters.put("SERVICE",    "WFS");
        requestParameters.put("REQUEST",    "GetCapabilities");
        requestParameters.put("VERSION",    version);        
        return super.getURL();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public InputStream getResponseStream() throws IOException {

        final GetCapabilitiesType request = new GetCapabilitiesType("WFS");
        request.setAcceptVersions(new AcceptVersionsType(version));

        final URL url = new URL(serverURL);
        URLConnection conec = url.openConnection();
        conec = security.secure(conec);

        conec.setDoOutput(true);
        conec.setRequestProperty("Content-Type", "text/xml");

        OutputStream stream = conec.getOutputStream();
        stream = security.encrypt(stream);
        Marshaller marshaller = null;
        try {
            marshaller = WFSMarshallerPool.getInstance().acquireMarshaller();
            marshaller.marshal(request, stream);
            //marshaller.marshal(request, System.out);
        } catch (JAXBException ex) {
            throw new IOException(ex);
        } finally {
            if (marshaller != null) {
                WFSMarshallerPool.getInstance().release(marshaller);
            }
        }
        stream.close();
        return security.decrypt(conec.getInputStream());
    }



}
