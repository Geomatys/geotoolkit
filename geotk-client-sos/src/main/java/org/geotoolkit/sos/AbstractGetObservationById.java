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
import javax.xml.namespace.QName;
import org.geotoolkit.sos.xml.v100.GetObservationById;
import org.geotoolkit.sos.xml.ResponseModeType;


/**
 * Abstract implementation of {@link GetObservationByIdRequest}, which defines the
 * parameters for a GetObservation request.
 *
 * @author Cédric Briançon (Geomatys)
 * @module
 */
public abstract class AbstractGetObservationById extends AbstractSOSRequest implements GetObservationByIdRequest {

    /**
     * The version to use for this webservice request.
     */
    protected final String version;

    private String observationId = null;
    private String srsName = null;
    private String responseFormat = null;
    private QName resultModel = null;
    private ResponseModeType responseMode = null;

    /**
     * Defines the server url and the service version for this kind of request.
     *
     * @param server The server.
     * @param version The version of the request.
     */
    protected AbstractGetObservationById(final SensorObservationServiceClient server, final String version) {
        super(server);
        this.version = version;
    }

    @Override
    public String getObservationId() {
        return observationId;
    }

    @Override
    public String getResponseFormat() {
        return responseFormat;
    }

    @Override
    public ResponseModeType getResponseMode() {
        return responseMode;
    }

    @Override
    public QName getResultModel() {
        return resultModel;
    }

    @Override
    public String getSrsName() {
        return srsName;
    }

    @Override
    public void setObservationId(final String observationId) {
        this.observationId = observationId;
    }

    @Override
    public void setResponseFormat(final String responseFormat) {
        this.responseFormat = responseFormat;
    }

    @Override
    public void setResponseMode(final ResponseModeType responseMode) {
        this.responseMode = responseMode;
    }

    @Override
    public void setResultModel(final QName resultModel) {
        this.resultModel = resultModel;
    }

    @Override
    public void setSrsName(final String srsName) {
        this.srsName = srsName;
    }

    @Override
    public URL getURL() throws MalformedURLException {
        throw new UnsupportedOperationException("Can not generate URL.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream getResponseStream() throws IOException {
        if (observationId == null) {
            throw new IllegalArgumentException("The parameter \"observationId\" is not defined");
        }
        if (responseFormat == null) {
            throw new IllegalArgumentException("The parameter \"responseFormat\" is not defined");
        }
        final URL url = new URL(serverURL);
        URLConnection conec = url.openConnection();
        conec = security.secure(conec);

        conec.setDoOutput(true);
        conec.setRequestProperty("Content-Type", "text/xml");

        OutputStream stream = conec.getOutputStream();
        stream = security.encrypt(stream);

        try {
            Marshaller marsh = POOL.acquireMarshaller();
            final GetObservationById observXml = new GetObservationById(version, observationId,
                    responseFormat, resultModel, responseMode, srsName);
            marsh.marshal(observXml, stream);
            POOL.recycle(marsh);
        } catch (JAXBException ex) {
            throw new IOException(ex);
        }
        stream.close();
        return security.decrypt(conec.getInputStream());
    }

}
