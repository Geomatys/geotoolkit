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
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import org.geotoolkit.sos.xml.v100.DescribeResultModel;


/**
 * Abstract implementation of {@link DescribeObservationTypeRequest}, which defines the
 * parameters for a DescribeObservationType request.
 *
 * @author Cédric Briançon (Geomatys)
 * @module
 */
public abstract class AbstractDescribeResultModel extends AbstractSOSRequest implements DescribeResultModelRequest {

    /**
     * The version to use for this webservice request.
     */
    protected final String version;

    private QName resultName = null;

    /**
     * Defines the server url and the service version for this kind of request.
     *
     * @param server The server.
     * @param version The version of the request.
     */
    protected AbstractDescribeResultModel(final SensorObservationServiceClient server, final String version) {
        super(server);
        this.version = version;
    }

    @Override
    public QName getResultName() {
        return resultName;
    }

    @Override
    public void setResultName(final QName observedProperty) {
        this.resultName = observedProperty;
    }

    @Override
    public URL getURL() throws MalformedURLException {
        throw new UnsupportedOperationException("Can not generate abstract describe result URL.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream getResponseStream() throws IOException {
        if (resultName == null) {
            throw new IllegalArgumentException("The parameter \"resultName\" is not defined");
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
            final DescribeResultModel descObsTypeXml =
                    new DescribeResultModel(version, resultName);
            marsh.marshal(descObsTypeXml, stream);
            POOL.recycle(marsh);
        } catch (JAXBException ex) {
            throw new IOException(ex);
        }
        stream.close();
        return security.decrypt(conec.getInputStream());
    }

}
