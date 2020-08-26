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
import java.util.Arrays;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.geotoolkit.client.AbstractRequest;
import org.geotoolkit.security.ClientSecurity;
import org.geotoolkit.ows.xml.AcceptVersions;
import org.geotoolkit.wfs.xml.WFSMarshallerPool;
import org.geotoolkit.wfs.xml.GetCapabilities;
import org.geotoolkit.wfs.xml.WFSVersion;
import org.geotoolkit.wfs.xml.WFSXmlFactory;


/**
 * Abstract get capabilities request.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class AbstractGetCapabilities extends AbstractRequest implements GetCapabilitiesRequest{

    protected final WFSVersion version;
    
    /**
     * The update sequence string, defining a unique id for the GetCapabilities response.
     */
    protected String updateSequence;


    public AbstractGetCapabilities(final String serverURL,final WFSVersion version, final ClientSecurity security){
        super(serverURL,security,null);
        this.version = version;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public String getUpdateSequence() {
        return updateSequence;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setUpdateSequence(String sequence) {
        this.updateSequence = sequence;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public URL getURL() throws MalformedURLException {
        requestParameters.put("SERVICE",    "WFS");
        requestParameters.put("REQUEST",    "GetCapabilities");
        if (version != null) {
            requestParameters.put("VERSION",    version.getCode());
        }
        if (updateSequence != null && !updateSequence.isEmpty()) {
            requestParameters.put("UPDATESEQUENCE", updateSequence);
        }
        return super.getURL();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public InputStream getResponseStream() throws IOException {
        final AcceptVersions versions;
        if (version != null) {
            versions = WFSXmlFactory.buildAcceptVersion(version.getCode(), Arrays.asList(version.getCode()));
        } else {
            versions = WFSXmlFactory.buildAcceptVersion(null, WFSVersion.codes());
        }

        final GetCapabilities request = WFSXmlFactory.buildGetCapabilities(version.getCode(), versions, null, null, updateSequence, "WFS");

        final URL url = new URL(serverURL);
        URLConnection conec = url.openConnection();
        conec = security.secure(conec);

        conec.setDoOutput(true);
        conec.setRequestProperty("Content-Type", "text/xml");

        OutputStream stream = conec.getOutputStream();
        stream = security.encrypt(stream);
        Marshaller marshaller = null;
        try {
            marshaller = WFSMarshallerPool.getInstance(version).acquireMarshaller();
            marshaller.marshal(request, stream);
            //marshaller.marshal(request, System.out);
            WFSMarshallerPool.getInstance().recycle(marshaller);
        } catch (JAXBException ex) {
            throw new IOException(ex);
        }
        stream.close();
        return security.decrypt(conec.getInputStream());
    }



}
