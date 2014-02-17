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
import org.geotoolkit.sos.xml.v100.EventTime;
import org.geotoolkit.sos.xml.v100.GetFeatureOfInterest;
import org.opengis.filter.Filter;
import static org.geotoolkit.sos.AbstractSOSRequest.POOL;


/**
 * Abstract implementation of {@link GetFeatureOfInterestRequest}, which defines the
 * parameters for a GetFeatureOfInterest request.
 *
 * @author Cédric Briançon (Geomatys)
 * @module pending
 */
public abstract class AbstractGetFeatureOfInterest extends AbstractSOSRequest implements GetFeatureOfInterestRequest {

    /**
     * The version to use for this webservice request.
     */
    protected final String version;

    private String featureOfInterestId = null;
    private EventTime eventTime = null;
    private Filter location = null;

    /**
     * Defines the server url and the service version for this kind of request.
     *
     * @param server The server.
     * @param version The version of the request.
     */
    protected AbstractGetFeatureOfInterest(final SensorObservationServiceServer server, final String version) {
        super(server);
        this.version = version;
    }

    @Override
    public EventTime getEventTime() {
        return eventTime;
    }

    @Override
    public String getFeatureOfInterestId() {
        return featureOfInterestId;
    }

    @Override
    public Filter getLocation() {
        return location;
    }

    @Override
    public void setEventTime(final EventTime eventTime) {
        this.eventTime = eventTime;
    }

    @Override
    public void setFeatureOfInterestId(final String featureOfInterestId) {
        this.featureOfInterestId = featureOfInterestId;
    }

    @Override
    public void setLocation(final Filter location) {
        this.location = location;
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
        final URL url = new URL(serverURL);
        URLConnection conec = url.openConnection();
        conec = security.secure(conec);

        conec.setDoOutput(true);
        conec.setRequestProperty("Content-Type", "text/xml");

        OutputStream stream = conec.getOutputStream();
        stream = security.encrypt(stream);

        try {
            final Marshaller marsh = POOL.acquireMarshaller();
            final GetFeatureOfInterest observXml;
            if (featureOfInterestId != null) {
                observXml = new GetFeatureOfInterest(version, "SOS", featureOfInterestId);
            } else if (location != null) {
                observXml = new GetFeatureOfInterest(version, "SOS", location);
            } else {
                throw new IllegalArgumentException("Either location or featureOfInterestId should have a value!");
            }
            marsh.marshal(observXml, stream);
            POOL.recycle(marsh);
        } catch (JAXBException ex) {
            throw new IOException(ex);
        }
        stream.close();
        return security.decrypt(conec.getInputStream());
    }

}
