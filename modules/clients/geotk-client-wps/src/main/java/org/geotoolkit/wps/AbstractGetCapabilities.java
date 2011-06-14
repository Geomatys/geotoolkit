/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
import org.geotoolkit.ows.xml.v110.AcceptVersionsType;
import org.geotoolkit.security.ClientSecurity;
import org.geotoolkit.wps.xml.WPSMarshallerPool;
import org.geotoolkit.wps.xml.v100.GetCapabilities;

/**
 * Abstract get capabilities request.
 * 
 * @author Quentin Boileau
 * @module pending
 */
public abstract class AbstractGetCapabilities extends AbstractRequest implements GetCapabilitiesRequest{
    
    protected final String version;

    protected AbstractGetCapabilities(final String serverURL,final String version, final ClientSecurity security){
        super(serverURL,security,null);
        this.version = version;
    }

    @Override
    protected void prepareParameters() {
        super.prepareParameters();
        requestParameters.put("SERVICE",    "WPS");
        requestParameters.put("REQUEST",    "GetCapabilities");
        requestParameters.put("ACCEPTVERSIONS",    version);        
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public InputStream getResponseStream() throws IOException {

        final GetCapabilities request = makeRequest();

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
        } catch (JAXBException ex) {
            throw new IOException(ex);
        } finally {
            if (marshaller != null) {
                WPSMarshallerPool.getInstance().release(marshaller);
            }
        }
        stream.close();
        return security.decrypt(conec.getInputStream());
    }
    
    public GetCapabilities makeRequest(){
        
        final GetCapabilities request = new GetCapabilities();
        request.setService("WPS");
        request.setAcceptVersions(new AcceptVersionsType(version));
        return request;
    }
}
