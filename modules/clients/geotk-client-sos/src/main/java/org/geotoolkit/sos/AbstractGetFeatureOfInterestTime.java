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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.geotoolkit.client.AbstractRequest;
import org.geotoolkit.sos.xml.v100.GetFeatureOfInterestTime;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.xml.MarshallerPool;


/**
 * Abstract implementation of {@link GetFeatureOfInterestTimeRequest}, which defines the
 * parameters for a GetFeatureOfInterestTime request.
 *
 * @author Cédric Briançon (Geomatys)
 * @module pending
 */
public abstract class AbstractGetFeatureOfInterestTime extends AbstractRequest implements GetFeatureOfInterestTimeRequest {
    /**
     * Logger specific for this implementation of {@link Request}.
     */
    private static final Logger LOGGER = Logging.getLogger(AbstractGetFeatureOfInterestTime.class);

    private static final MarshallerPool POOL;
    static {
        MarshallerPool temp = null;
        try {
            temp = new MarshallerPool("org.geotoolkit.sos.xml.v100:" +
                                      "org.geotoolkit.gml.xml.v311:" +
                                      "org.geotoolkit.swe.xml.v100:" +
                                      "org.geotoolkit.swe.xml.v101:" +
                                      "org.geotoolkit.observation.xml.v100:" +
                                      "org.geotoolkit.sampling.xml.v100:" +
                                      "org.geotoolkit.sml.xml.v100:" +
                                      "org.geotoolkit.sml.xml.v101");
        } catch (JAXBException ex) {
            LOGGER.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
        }
        POOL = temp;
    }

    /**
     * The version to use for this webservice request.
     */
    protected final String version;

    private String featureOfInterestId = null;

    /**
     * Defines the server url and the service version for this kind of request.
     *
     * @param serverURL The server url.
     * @param version The version of the request.
     */
    protected AbstractGetFeatureOfInterestTime(final String serverURL, final String version) {
        super(serverURL);
        this.version = version;
    }

    @Override
    public String getFeatureOfInterestId() {
        return featureOfInterestId;
    }

    @Override
    public void setFeatureOfInterestId(String featureOfInterestId) {
        this.featureOfInterestId = featureOfInterestId;
    }

    @Override
    public URL getURL() throws MalformedURLException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream getResponseStream() throws IOException {
        if (featureOfInterestId == null) {
            throw new IllegalArgumentException("The parameter \"featureOfInterestId\" is not defined");
        }
        final URL url = new URL(serverURL);
        final URLConnection conec = url.openConnection();

        conec.setDoOutput(true);
        conec.setRequestProperty("Content-Type", "text/xml");

        final OutputStream stream = conec.getOutputStream();

        Marshaller marsh = null;
        try {
            marsh = POOL.acquireMarshaller();
            final GetFeatureOfInterestTime featureOfInterestTimeXml =
                    new GetFeatureOfInterestTime(version, featureOfInterestId);
            marsh.marshal(featureOfInterestTimeXml, stream);
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
