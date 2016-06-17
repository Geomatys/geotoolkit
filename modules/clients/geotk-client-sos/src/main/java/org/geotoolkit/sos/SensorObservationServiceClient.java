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
import org.geotoolkit.client.AbstractClient;
import org.geotoolkit.client.ClientFactory;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.security.ClientSecurity;
import org.geotoolkit.sos.v100.*;
import org.geotoolkit.sos.xml.SOSVersion;
import org.geotoolkit.storage.DataStores;
import org.opengis.parameter.ParameterValueGroup;

/**
 * SOS server.
 *
 * @author Cédric Briançon (Geomatys)
 * @module pending
 */
public class SensorObservationServiceClient extends AbstractClient {


    public SensorObservationServiceClient(final URL serverURL, final String version) {
        this(serverURL,null,version);
    }
    
    public SensorObservationServiceClient(final URL serverURL, final ClientSecurity security, final String version) {
        super(create(SOSClientFactory.PARAMETERS, serverURL, security));
        if (version.equals("1.0.0")){
            Parameters.getOrCreate(SOSClientFactory.VERSION, parameters).setValue(version);
        } else {
            throw new IllegalArgumentException("unknowned version : "+ version);
        }
    }
    
    public SensorObservationServiceClient(final URL serverURL, final ClientSecurity security, final SOSVersion version) {
        super(create(SOSClientFactory.PARAMETERS, serverURL, security));
        if(version == null){
            throw new IllegalArgumentException("unknowned version : "+ version);
        }
    }
    
    public SensorObservationServiceClient(ParameterValueGroup params) {
        super(params);
    }

    @Override
    public ClientFactory getFactory() {
        return (ClientFactory) DataStores.getFactoryById(SOSClientFactory.NAME);
    }

    /**
     * Returns the currently used version for this server
     */
    public SOSVersion getVersion() {
        return SOSVersion.fromCode(Parameters.value(SOSClientFactory.VERSION, parameters));
    }

    /**
     * Creates and returns a describeFeatureType request.
     */
    public DescribeFeatureTypeRequest createDescribeFeatureType() {

        switch (getVersion()) {
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

        switch (getVersion()) {
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

        switch (getVersion()) {
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

        switch (getVersion()) {
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

        switch (getVersion()) {
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

        switch (getVersion()) {
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

        switch (getVersion()) {
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

        switch (getVersion()) {
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

        switch (getVersion()) {
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

        switch (getVersion()) {
            case v100:
                return new GetResult100(this);
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }
}
