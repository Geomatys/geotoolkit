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

import java.net.URL;
import org.geotoolkit.client.AbstractServer;
import org.geotoolkit.security.ClientSecurity;
import org.geotoolkit.sos.v100.*;
import org.geotoolkit.sos.xml.SOSVersion;

/**
 * CSW server.
 *
 * @author Cédric Briançon (Geomatys)
 * @module pending
 */
public class SensorObservationServiceServer extends AbstractServer {

    private final SOSVersion version;

    public SensorObservationServiceServer(final URL serverURL, final String version) {
        this(serverURL,null,version);
    }
    
    public SensorObservationServiceServer(final URL serverURL, final ClientSecurity security, final String version) {
        super(serverURL,security);
        if (version.equals("1.0.0")){
            this.version = SOSVersion.v100;
        } else {
            throw new IllegalArgumentException("unkonwed version : "+ version);
        }
    }
    
    public SensorObservationServiceServer(final URL serverURL, final ClientSecurity security, final SOSVersion version) {
        super(serverURL,security);
        this.version = version;
        if(version == null){
            throw new IllegalArgumentException("unkonwed version : "+ version);
        }
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
                return new DescribeFeatureType100(this);
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
                return new DescribeObservationType100(this);
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
                return new DescribeResultModel100(this);
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
                return new DescribeSensor100(this);
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
                return new GetCapabilities100(this);
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
                return new GetFeatureOfInterest100(this);
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
                return new GetFeatureOfInterestTime100(this);
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
                return new GetObservation100(this);
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
                return new GetObservationById100(this);
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
                return new GetResult100(this);
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }
}
