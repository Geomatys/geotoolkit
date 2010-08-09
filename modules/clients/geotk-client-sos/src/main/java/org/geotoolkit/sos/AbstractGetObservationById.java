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
import javax.xml.namespace.QName;
import org.geotoolkit.sos.xml.v100.GetObservationById;
import org.geotoolkit.sos.xml.v100.ResponseModeType;


/**
 * Abstract implementation of {@link GetObservationByIdRequest}, which defines the
 * parameters for a GetObservation request.
 *
 * @author Cédric Briançon (Geomatys)
 * @module pending
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
     * @param serverURL The server url.
     * @param version The version of the request.
     */
    protected AbstractGetObservationById(final String serverURL, final String version) {
        super(serverURL);
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
    public void setObservationId(String observationId) {
        this.observationId = observationId;
    }

    @Override
    public void setResponseFormat(String responseFormat) {
        this.responseFormat = responseFormat;
    }

    @Override
    public void setResponseMode(ResponseModeType responseMode) {
        this.responseMode = responseMode;
    }

    @Override
    public void setResultModel(QName resultModel) {
        this.resultModel = resultModel;
    }

    @Override
    public void setSrsName(String srsName) {
        this.srsName = srsName;
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
        if (observationId == null) {
            throw new IllegalArgumentException("The parameter \"observationId\" is not defined");
        }
        if (responseFormat == null) {
            throw new IllegalArgumentException("The parameter \"responseFormat\" is not defined");
        }
        final URL url = new URL(serverURL);
        final URLConnection conec = url.openConnection();

        conec.setDoOutput(true);
        conec.setRequestProperty("Content-Type", "text/xml");

        final OutputStream stream = conec.getOutputStream();

        Marshaller marsh = null;
        try {
            marsh = POOL.acquireMarshaller();
            final GetObservationById observXml = new GetObservationById(version, observationId,
                    responseFormat, resultModel, responseMode, srsName);
            marsh.marshal(observXml, stream);
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
