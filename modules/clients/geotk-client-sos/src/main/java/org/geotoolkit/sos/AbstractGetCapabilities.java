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
package org.geotoolkit.sos;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.geotoolkit.client.AbstractRequest;
import org.geotoolkit.sos.xml.v100.GetCapabilities;
import org.geotoolkit.xml.MarshallerPool;


/**
 * Abstract implementation of {@link GetCapabilitiesRequest}, which defines the
 * parameters for a GetCapabilities request.
 *
 * @author Cédric Briançon (Geomatys)
 * @module pending
 */
public abstract class AbstractGetCapabilities extends AbstractRequest implements GetCapabilitiesRequest{
    /**
     * The version to use for this webservice request.
     */
    protected final String version;

    /**
     * Defines the server url and the service version for this kind of request.
     *
     * @param serverURL The server url.
     * @param version The version of the request.
     */
    protected AbstractGetCapabilities(final String serverURL, final String version) {
        super(serverURL);
        this.version = version;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public URL getURL() throws MalformedURLException {
        requestParameters.put("SERVICE", "SOS");
        requestParameters.put("REQUEST", "GetCapabilities");
        requestParameters.put("VERSION", version);
        return super.getURL();
    }

    /**
     * {@inheritDoc }
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
            pool = new MarshallerPool("org.geotoolkit.sos.xml.v100:" +
                                      "org.geotoolkit.gml.xml.v311:" +
                                      "org.geotoolkit.swe.xml.v100:" +
                                      "org.geotoolkit.swe.xml.v101:" +
                                      "org.geotoolkit.observation.xml.v100:" +
                                      "org.geotoolkit.sampling.xml.v100:" +
                                      "org.geotoolkit.sml.xml.v100:" +
                                      "org.geotoolkit.sml.xml.v101");
            marsh = pool.acquireMarshaller();
            final GetCapabilities getCapsXml = new GetCapabilities(version, null);
            marsh.marshal(getCapsXml, stream);
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
