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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.geotoolkit.client.AbstractRequest;
import org.geotoolkit.csw.xml.v202.GetDomainType;
import org.geotoolkit.ebrim.xml.EBRIMClassesContext;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.xml.MarshallerPool;


/**
 * Abstract implementation of {@link GetDomainRequest}, which defines the
 * parameters for a GetDomain request.
 *
 * @author Cédric Briançon (Geomatys)
 * @module pending
 */
public abstract class AbstractGetDomain extends AbstractRequest implements GetDomainRequest {
    /**
     * Default logger for all GetDomain requests.
     */
    protected static final Logger LOGGER = Logging.getLogger(AbstractGetDomain.class);

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
    protected AbstractGetDomain(final String serverURL, final String version){
        super(serverURL);
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

    /**
     * {@inheritDoc }
     */
    @Override
    public URL getURL() throws MalformedURLException {
        if (propertyName == null) {
            throw new IllegalArgumentException("The parameter \"propertyName\" is not defined");
        }

        requestParameters.put("SERVICE",      "CSW");
        requestParameters.put("REQUEST",      "GetDomain");
        requestParameters.put("VERSION",      version);
        requestParameters.put("PROPERTYNAME", propertyName);

        return super.getURL();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream getResponseStream() throws IOException {
        final URL url = new URL(serverURL);
        final URLConnection conec = url.openConnection();

        conec.setDoOutput(true);
        conec.setRequestProperty("Content-Type", "text/xml");

        final OutputStream stream = conec.getOutputStream();

        MarshallerPool pool = null;
        Marshaller marsh = null;
        try {
            pool = new MarshallerPool(EBRIMClassesContext.getAllClasses());
            marsh = pool.acquireMarshaller();
            final GetDomainType domainXml = new GetDomainType("CSW", version, propertyName, null);
            marsh.marshal(domainXml, stream);
        } catch (JAXBException ex) {
            throw new IOException(ex);
        } finally {
            if (pool != null && marsh != null) {
                pool.release(marsh);
            }
        }
        stream.close();
        return conec.getInputStream();
    }
}
