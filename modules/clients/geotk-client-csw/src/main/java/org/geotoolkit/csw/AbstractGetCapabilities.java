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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.geotoolkit.client.AbstractRequest;
import org.geotoolkit.csw.xml.v202.GetCapabilitiesType;
import org.geotoolkit.ebrim.xml.EBRIMClassesContext;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.xml.MarshallerPool;


/**
 * Abstract get capabilities request.
 *
 * @author Johann Sorel (Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @module pending
 */
public abstract class AbstractGetCapabilities extends AbstractRequest implements GetCapabilitiesRequest {
    /**
     * Default logger for all GetCapabilities requests.
     */
    protected static final Logger LOGGER = Logging.getLogger(AbstractGetCapabilities.class);

    private static final MarshallerPool POOL;
    static {
        MarshallerPool temp = null;
        try {
            temp = new MarshallerPool(EBRIMClassesContext.getAllClasses());
        } catch (JAXBException ex) {
            LOGGER.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
        }
        POOL = temp;
    }

    /**
     * The version of the CSW service.
     */
    protected final String version;

    protected AbstractGetCapabilities(final String serverURL, final String version) {
        super(serverURL);
        this.version = version;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public URL getURL() throws MalformedURLException {
        requestParameters.put("SERVICE",    "CSW");
        requestParameters.put("REQUEST",    "GetCapabilities");
        requestParameters.put("VERSION",    version);
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

        Marshaller marsh = null;
        try {
            marsh = POOL.acquireMarshaller();
            final GetCapabilitiesType getCapsXml = new GetCapabilitiesType(version);
            marsh.marshal(getCapsXml, stream);
        } catch (JAXBException ex) {
            throw new IOException(ex);
        } finally {
            if (POOL != null && marsh != null) {
                POOL.release(marsh);
            }
        }
        stream.close();
        return conec.getInputStream();
    }

}
