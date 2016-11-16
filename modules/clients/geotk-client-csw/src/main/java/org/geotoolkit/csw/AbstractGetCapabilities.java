/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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
package org.geotoolkit.csw;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import static org.geotoolkit.csw.AbstractCSWRequest.POOL;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.geotoolkit.csw.xml.CswXmlFactory;
import org.geotoolkit.csw.xml.GetCapabilities;
import org.geotoolkit.security.ClientSecurity;


/**
 * Abstract get capabilities request.
 *
 * @author Johann Sorel (Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @module
 */
public abstract class AbstractGetCapabilities extends AbstractCSWRequest implements GetCapabilitiesRequest {

    /**
     * The version of the CSW service.
     */
    protected final String version;

    protected AbstractGetCapabilities(final String serverURL, final String version, final ClientSecurity security) {
        super(serverURL,security);
        this.version = version;
    }

    @Override
    protected void prepareParameters() {
        super.prepareParameters();
        requestParameters.put("SERVICE",    "CSW");
        requestParameters.put("REQUEST",    "GetCapabilities");
        requestParameters.put("VERSION",    version);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream getResponseStream() throws IOException {
        final URL url = getURL();
        URLConnection conec = url.openConnection();
        conec = security.secure(conec);

        conec.setDoOutput(true);
        conec.setRequestProperty("Content-Type", "text/xml");

        OutputStream stream = conec.getOutputStream();
        stream = security.encrypt(stream);

        try {
            final Marshaller marsh = POOL.acquireMarshaller();
            final GetCapabilities getCapsXml = CswXmlFactory.createGetCapabilities(version, null, null, null, null, "CSW");
            marsh.marshal(getCapsXml, stream);
            POOL.recycle(marsh);
        } catch (JAXBException ex) {
            throw new IOException(ex);
        }
        stream.close();
        return security.decrypt(conec.getInputStream());
    }

}
