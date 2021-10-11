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
import org.geotoolkit.csw.xml.GetDomain;
import org.geotoolkit.security.ClientSecurity;


/**
 * Abstract implementation of {@link GetDomainRequest}, which defines the
 * parameters for a GetDomain request.
 *
 * @author Cédric Briançon (Geomatys)
 * @module
 */
public abstract class AbstractGetDomain extends AbstractCSWRequest implements GetDomainRequest {

    /**
     * The version to use for this webservice request.
     */
    protected final String version;

    /**
     * The property name value for the request.
     */
    private String propertyName = null;

    /**
     * Defines the server url and the service version for this kind of request.
     *
     * @param serverURL The server url.
     * @param version The version of the request.
     */
    protected AbstractGetDomain(final String serverURL, final String version, final ClientSecurity security){
        super(serverURL,security);
        this.version = version;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getPropertyName() {
        return propertyName;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setPropertyName(final String propertyName) {
        this.propertyName = propertyName;
    }

    @Override
    protected void prepareParameters() {
        super.prepareParameters();
        if (propertyName == null) {
            throw new IllegalArgumentException("The parameter \"propertyName\" is not defined");
        }

        requestParameters.put("SERVICE",      "CSW");
        requestParameters.put("REQUEST",      "GetDomain");
        requestParameters.put("VERSION",      version);
        requestParameters.put("PROPERTYNAME", propertyName);
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
            final GetDomain domainXml = CswXmlFactory.createGetDomain(version, "CSW",  propertyName, null);
            marsh.marshal(domainXml, stream);
            POOL.recycle(marsh);
        } catch (JAXBException ex) {
            throw new IOException(ex);
        }
        stream.close();
        return security.decrypt(conec.getInputStream());
    }
}
