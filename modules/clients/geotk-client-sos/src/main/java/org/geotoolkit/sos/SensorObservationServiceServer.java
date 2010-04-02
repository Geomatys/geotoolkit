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

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.client.Server;
import org.geotoolkit.sos.v100.DescribeFeatureType100;
import org.geotoolkit.sos.v100.DescribeObservationType100;
import org.geotoolkit.sos.v100.DescribeResultModel100;
import org.geotoolkit.sos.v100.DescribeSensor100;
import org.geotoolkit.sos.v100.GetCapabilities100;
import org.geotoolkit.sos.v100.GetFeatureOfInterest100;
import org.geotoolkit.sos.v100.GetFeatureOfInterestTime100;
import org.geotoolkit.sos.v100.GetObservation100;
import org.geotoolkit.sos.v100.GetObservationById100;
import org.geotoolkit.sos.v100.GetResult100;
import org.geotoolkit.sos.xml.SOSVersion;
import org.geotoolkit.util.logging.Logging;


/**
 * CSW server.
 *
 * @author Cédric Briançon (Geomatys)
 * @module pending
 */
public class SensorObservationServiceServer implements Server {
    private static final Logger LOGGER = Logging.getLogger(SensorObservationServiceServer.class);

    private final SOSVersion version;
    private final URL serverURL;

    public SensorObservationServiceServer(final URL serverURL, final String version) {
        if (version.equals("1.0.0")){
            this.version = SOSVersion.v100;
        } else {
            throw new IllegalArgumentException("unkonwed version : "+ version);
        }
        this.serverURL = serverURL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URI getURI() {
        try {
            return serverURL.toURI();
        } catch (URISyntaxException ex) {
            LOGGER.log(Level.WARNING, ex.getLocalizedMessage(), ex);
        }
        return null;
    }

    /**
     * Returns the currently used version for this server
     */
    public SOSVersion getVersion() {
        return version;
    }

    /**
     * Creates and returns a describeFeatureType request.
     */
    public DescribeFeatureTypeRequest createDescribeFeatureType() {

        switch (version) {
            case v100:
                return new DescribeFeatureType100(serverURL.toString());
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    /**
     * Creates and returns a describeObservationType request.
     */
    public DescribeObservationTypeRequest createDescribeObservationType() {

        switch (version) {
            case v100:
                return new DescribeObservationType100(serverURL.toString());
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    /**
     * Creates and returns a describeResultModel request.
     */
    public DescribeResultModelRequest createDescribeResultModel() {

        switch (version) {
            case v100:
                return new DescribeResultModel100(serverURL.toString());
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    /**
     * Creates and returns a describeSensor request.
     */
    public DescribeSensorRequest createDescribeSensor() {

        switch (version) {
            case v100:
                return new DescribeSensor100(serverURL.toString());
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    /**
     * Creates and returns a getCapabilities request.
     */
    public GetCapabilitiesRequest createGetCapabilities() {

        switch (version) {
            case v100:
                return new GetCapabilities100(serverURL.toString());
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    /**
     * Creates and returns a getFeatureOfInterest request.
     */
    public GetFeatureOfInterestRequest createGetFeatureOfInterest() {

        switch (version) {
            case v100:
                return new GetFeatureOfInterest100(serverURL.toString());
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    /**
     * Creates and returns a getFeatureOfInterestTime request.
     */
    public GetFeatureOfInterestTimeRequest createGetFeatureOfInterestTime() {

        switch (version) {
            case v100:
                return new GetFeatureOfInterestTime100(serverURL.toString());
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    /**
     * Creates and returns a getObservation request.
     */
    public GetObservationRequest createGetObservation() {

        switch (version) {
            case v100:
                return new GetObservation100(serverURL.toString());
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    /**
     * Creates and returns a getObservationById request.
     */
    public GetObservationByIdRequest createGetObservationById() {

        switch (version) {
            case v100:
                return new GetObservationById100(serverURL.toString());
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    /**
     * Creates and returns a describeRecord request.
     */
    public GetResultRequest createGetResult() {

        switch (version) {
            case v100:
                return new GetResult100(serverURL.toString());
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }
}
