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
import java.util.Arrays;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import org.geotoolkit.sos.xml.v100.EventTime;
import org.geotoolkit.sos.xml.v100.GetObservation;
import org.geotoolkit.sos.xml.v100.ResponseModeType;


/**
 * Abstract implementation of {@link GetObservationRequest}, which defines the
 * parameters for a GetObservation request.
 *
 * @author Cédric Briançon (Geomatys)
 * @module pending
 */
public abstract class AbstractGetObservation extends AbstractSOSRequest implements GetObservationRequest {

    /**
     * The version to use for this webservice request.
     */
    protected final String version;

    private String srsName = null;
    private String offering = null;
    private EventTime[] eventTimes = null;
    private String[] procedures = null;
    private String[] observedProperties = null;
    private GetObservation.FeatureOfInterest featureOfInterest = null;
    private GetObservation.Result result = null;
    private String responseFormat = null;
    private QName resultModel = null;
    private ResponseModeType responseMode = null;

    /**
     * Defines the server url and the service version for this kind of request.
     *
     * @param server The server.
     * @param version The version of the request.
     */
    protected AbstractGetObservation(final SensorObservationServiceServer server, final String version) {
        super(server);
        this.version = version;
    }

    @Override
    public EventTime[] getEventTimes() {
        return eventTimes;
    }

    @Override
    public GetObservation.FeatureOfInterest getFeatureOfInterest() {
        return featureOfInterest;
    }

    @Override
    public String[] getObservedProperties() {
        return observedProperties;
    }

    @Override
    public String getOffering() {
        return offering;
    }

    @Override
    public String[] getProcedures() {
        return procedures;
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
    public GetObservation.Result getResult() {
        return result;
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
    public void setEventTimes(final EventTime... eventTimes) {
        this.eventTimes = eventTimes;
    }

    @Override
    public void setFeatureOfInterest(final GetObservation.FeatureOfInterest featureOfInterest) {
        this.featureOfInterest = featureOfInterest;
    }

    @Override
    public void setObservedProperties(final String... observedProperties) {
        this.observedProperties = observedProperties;
    }

    @Override
    public void setOffering(final String offering) {
        this.offering = offering;
    }

    @Override
    public void setProcedures(final String... procedures) {
        this.procedures = procedures;
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
    public void setResult(final GetObservation.Result result) {
        this.result = result;
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
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public InputStream getResponseStream() throws IOException {
        if (offering == null) {
            throw new IllegalArgumentException("offering is not defined");
        }
        if (observedProperties == null) {
            throw new IllegalArgumentException("observedProperties is not defined");
        }
        if (responseFormat == null) {
            throw new IllegalArgumentException("responseFormat is not defined");
        }
        final URL url = new URL(serverURL);
        URLConnection conec = url.openConnection();
        conec = security.secure(conec);

        conec.setDoOutput(true);
        conec.setRequestProperty("Content-Type", "text/xml");

        OutputStream stream = conec.getOutputStream();
        stream = security.encrypt(stream);

        Marshaller marsh = null;
        try {
            marsh = POOL.acquireMarshaller();
            final GetObservation observXml = new GetObservation(version, offering,
                    (eventTimes != null) ? Arrays.asList(eventTimes) : null,
                    (procedures != null) ? Arrays.asList(procedures) : null,
                    Arrays.asList(observedProperties), featureOfInterest, result,
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
        return security.decrypt(conec.getInputStream());
    }

}
