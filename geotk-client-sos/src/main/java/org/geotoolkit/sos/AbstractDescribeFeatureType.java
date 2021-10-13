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
import static org.geotoolkit.sos.AbstractSOSRequest.POOL;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.geotoolkit.sos.xml.v100.DescribeFeatureType;


/**
 * Abstract implementation of {@link DescribeFeatureTypeRequest}, which defines the
 * parameters for a DescribeFeatureType request.
 *
 * @author Cédric Briançon (Geomatys)
 * @module
 */
public abstract class AbstractDescribeFeatureType extends AbstractSOSRequest implements DescribeFeatureTypeRequest {

    /**
     * The version to use for this webservice request.
     */
    protected final String version;

    private String featureId = null;

    /**
     * Defines the server url and the service version for this kind of request.
     *
     * @param server The server.
     * @param version The version of the request.
     */
    protected AbstractDescribeFeatureType(final SensorObservationServiceClient server, final String version) {
        super(server);
        this.version = version;
    }

    @Override
    public String getFeatureId() {
        return featureId;
    }

    @Override
    public void setFeatureId(final String featureId) {
        this.featureId = featureId;
    }

    @Override
    public URL getURL() throws MalformedURLException {
        throw new UnsupportedOperationException("Can not generate DescribeFeature URL.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream getResponseStream() throws IOException {
        if (featureId == null) {
            throw new IllegalArgumentException("The parameter \"featureId\" is not defined");
        }
        final URL url = new URL(serverURL);
        URLConnection conec = url.openConnection();
        conec = security.secure(conec);

        conec.setDoOutput(true);
        conec.setRequestProperty("Content-Type", "text/xml");

        OutputStream stream = conec.getOutputStream();
        stream = security.encrypt(stream);

        try {
            final Marshaller marsh = POOL.acquireMarshaller();
            final DescribeFeatureType describeFeatureTypeXml =
                    new DescribeFeatureType(version, featureId);
            marsh.marshal(describeFeatureTypeXml, stream);
            POOL.recycle(marsh);
        } catch (JAXBException ex) {
            throw new IOException(ex);
        }
        stream.close();
        return security.decrypt(conec.getInputStream());
    }

}
