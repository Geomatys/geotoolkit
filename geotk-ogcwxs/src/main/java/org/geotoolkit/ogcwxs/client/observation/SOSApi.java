/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
package org.geotoolkit.ogcwxs.client.observation;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import org.apache.sis.xml.MarshallerPool;
import org.geotoolkit.client.service.AbstractService;
import org.geotoolkit.client.service.ServiceConfiguration;
import org.geotoolkit.client.service.ServiceRequest;
import org.geotoolkit.client.service.UncheckedServiceException;
import org.geotoolkit.ogcwxs.request.observation.DescribeFeatureType;
import org.geotoolkit.ogcwxs.request.observation.DescribeObservationType;
import org.geotoolkit.ogcwxs.request.observation.DescribeResultModel;
import org.geotoolkit.ogcwxs.request.observation.DescribeSensor;
import org.geotoolkit.ogcwxs.request.observation.GetFeatureOfInterest;
import org.geotoolkit.ogcwxs.request.observation.GetFeatureOfInterestTime;
import org.geotoolkit.ogcwxs.request.observation.GetObservation;
import org.geotoolkit.ogcwxs.request.observation.GetObservationById;
import org.geotoolkit.ogcwxs.request.observation.GetResult;
import org.geotoolkit.sos.xml.Capabilities;
import org.geotoolkit.sos.xml.SOSMarshallerPool;
import org.opengis.observation.ObservationCollection;

/**
 * OGC Sensor Observation Service (SOS) client.
 *
 * <p>
 * Covers {@code GetCapabilities}, {@code DescribeSensor}, {@code GetObservation},
 * {@code GetObservationById}, {@code GetResult}, {@code GetFeatureOfInterest},
 * {@code GetFeatureOfInterestTime}, {@code DescribeFeatureType}, {@code DescribeObservationType}
 * and {@code DescribeResultModel}. Only SOS 1.0.0 is supported, as it is the only version the
 * legacy {@code geotk-client-sos} module ever implemented.
 * </p>
 *
 * <p>
 * Insert operations ({@code InsertObservation}, {@code InsertSensor}, ...) are not covered by
 * this client, mirroring the legacy module which never implemented them either.
 * </p>
 *
 * @see https://www.ogc.org/standards/sos/
 * @author Quentin Bialota (Geomatys)
 */
public final class SOSApi extends AbstractService {

    private static final String VERSION = "1.0.0";
    private static final String SERVICE = "SOS";

    public SOSApi(ServiceConfiguration config) {
        super(config);
    }

    /**
     * Retrieve the service metadata document describing this SOS instance.
     *
     * @return ServiceRequest&lt;Supplier&lt;Capabilities&gt;&gt;
     */
    public ServiceRequest<Supplier<Capabilities>> getCapabilities() {
        final org.geotoolkit.sos.xml.v100.GetCapabilities request = new org.geotoolkit.sos.xml.v100.GetCapabilities();
        return post(request, Capabilities.class);
    }

    /**
     * Retrieve a description (typically SensorML) of a sensor.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;byte[]&gt;
     */
    public ServiceRequest<byte[]> describeSensor(DescribeSensor parameters) {
        final org.geotoolkit.sos.xml.v100.DescribeSensor request = new org.geotoolkit.sos.xml.v100.DescribeSensor(
                VERSION, SERVICE, parameters.getSensorId(), parameters.getOutputFormat());
        return post(request, HttpResponse.BodyHandlers.ofByteArray());
    }

    /**
     * Retrieve observations matching an offering, a time period and a set of observed properties.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Supplier&lt;ObservationCollection&gt;&gt;
     */
    public ServiceRequest<Supplier<ObservationCollection>> getObservation(GetObservation parameters) {
        final List<org.geotoolkit.sos.xml.v100.EventTime> eventTimes = parameters.getEventTimes() == null
                ? null : Arrays.asList(parameters.getEventTimes());
        final List<String> procedures = parameters.getProcedures() == null
                ? null : Arrays.asList(parameters.getProcedures());
        final List<String> observedProperties = parameters.getObservedProperties() == null
                ? null : Arrays.asList(parameters.getObservedProperties());
        final org.geotoolkit.sos.xml.v100.GetObservation request = new org.geotoolkit.sos.xml.v100.GetObservation(
                VERSION, parameters.getOffering(), eventTimes, procedures, observedProperties,
                parameters.getFeatureOfInterest(), parameters.getResult(), parameters.getResponseFormat(),
                parameters.getResultModel(), parameters.getResponseMode(), parameters.getSrsName());
        return post(request, ObservationCollection.class);
    }

    /**
     * Retrieve an observation identified by its id.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Supplier&lt;ObservationCollection&gt;&gt;
     */
    public ServiceRequest<Supplier<ObservationCollection>> getObservationById(GetObservationById parameters) {
        final org.geotoolkit.sos.xml.v100.GetObservationById request = new org.geotoolkit.sos.xml.v100.GetObservationById(
                VERSION, SERVICE, parameters.getObservationId(), parameters.getResponseFormat(),
                parameters.getResultModel(), parameters.getResponseMode(), parameters.getSrsName());
        return post(request, ObservationCollection.class);
    }

    /**
     * Retrieve only the result data of observations matching an observation template.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;byte[]&gt;
     */
    public ServiceRequest<byte[]> getResult(GetResult parameters) {
        final List<org.geotoolkit.sos.xml.v100.EventTime> eventTimes = parameters.getEventTimes() == null
                ? null : Arrays.asList(parameters.getEventTimes());
        final org.geotoolkit.sos.xml.v100.GetResult request = new org.geotoolkit.sos.xml.v100.GetResult(
                parameters.getObservationTemplateId(), eventTimes, VERSION);
        return post(request, HttpResponse.BodyHandlers.ofByteArray());
    }

    /**
     * Retrieve a feature-of-interest either by its identifier or by a spatial filter on its
     * location.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;byte[]&gt;
     */
    public ServiceRequest<byte[]> getFeatureOfInterest(GetFeatureOfInterest parameters) {
        final org.geotoolkit.sos.xml.v100.GetFeatureOfInterest request;
        if (parameters.getLocation() != null) {
            request = new org.geotoolkit.sos.xml.v100.GetFeatureOfInterest(VERSION, SERVICE, parameters.getLocation());
        } else {
            request = new org.geotoolkit.sos.xml.v100.GetFeatureOfInterest(VERSION, SERVICE, parameters.getFeatureOfInterestId());
        }
        return post(request, HttpResponse.BodyHandlers.ofByteArray());
    }

    /**
     * Retrieve the time period during which a feature-of-interest has been observed.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;byte[]&gt;
     */
    public ServiceRequest<byte[]> getFeatureOfInterestTime(GetFeatureOfInterestTime parameters) {
        final org.geotoolkit.sos.xml.v100.GetFeatureOfInterestTime request = new org.geotoolkit.sos.xml.v100.GetFeatureOfInterestTime(
                VERSION, parameters.getFeatureOfInterestId());
        return post(request, HttpResponse.BodyHandlers.ofByteArray());
    }

    /**
     * Retrieve a description of the feature-of-interest type used by a sensor.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;byte[]&gt;
     */
    public ServiceRequest<byte[]> describeFeatureType(DescribeFeatureType parameters) {
        final org.geotoolkit.sos.xml.v100.DescribeFeatureType request = new org.geotoolkit.sos.xml.v100.DescribeFeatureType(
                VERSION, parameters.getFeatureId());
        return post(request, HttpResponse.BodyHandlers.ofByteArray());
    }

    /**
     * Retrieve a description of the type of an observed property.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;byte[]&gt;
     */
    public ServiceRequest<byte[]> describeObservationType(DescribeObservationType parameters) {
        final org.geotoolkit.sos.xml.v100.DescribeObservationType request = new org.geotoolkit.sos.xml.v100.DescribeObservationType(
                VERSION, parameters.getObservedProperty());
        return post(request, HttpResponse.BodyHandlers.ofByteArray());
    }

    /**
     * Retrieve a description of the model used for observation results of a given type.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;byte[]&gt;
     */
    public ServiceRequest<byte[]> describeResultModel(DescribeResultModel parameters) {
        final org.geotoolkit.sos.xml.v100.DescribeResultModel request = new org.geotoolkit.sos.xml.v100.DescribeResultModel(
                VERSION, parameters.getResultName());
        return post(request, HttpResponse.BodyHandlers.ofByteArray());
    }

    /**
     * Marshal the given SOS request object as the body of a POST request, and unmarshal the
     * response as the given data type.
     */
    private <T> ServiceRequest<Supplier<T>> post(Object requestObject, Class<T> dataType) {
        final HttpRequest.Builder request = toPostRequest(requestObject);
        return request(request, (HttpResponse.ResponseInfo responseInfo) -> {
            final HttpResponse.BodySubscriber<InputStream> upstream = HttpResponse.BodySubscribers.ofInputStream();
            return HttpResponse.BodySubscribers.mapping(upstream, (InputStream in) -> () -> unmarshal(in, dataType));
        });
    }

    private <T> ServiceRequest<T> post(Object requestObject, HttpResponse.BodyHandler<T> bodyHandler) {
        return request(toPostRequest(requestObject), bodyHandler);
    }

    private HttpRequest.Builder toPostRequest(Object requestObject) {
        final byte[] body = marshal(requestObject);
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("", List.of()));
        request.header("Content-Type", "text/xml");
        request.header("Accept", "text/xml, application/xml");
        request.method("POST", HttpRequest.BodyPublishers.ofByteArray(body));
        setConfig(request);
        return request;
    }

    private static byte[] marshal(Object requestObject) {
        final MarshallerPool pool = SOSMarshallerPool.getInstance();
        try {
            final Marshaller marshaller = pool.acquireMarshaller();
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            marshaller.marshal(requestObject, out);
            pool.recycle(marshaller);
            return out.toByteArray();
        } catch (JAXBException ex) {
            throw new UncheckedServiceException(ex);
        }
    }

    private static <T> T unmarshal(InputStream in, Class<T> dataType) {
        final MarshallerPool pool = SOSMarshallerPool.getInstance();
        try {
            final Unmarshaller unmarshaller = pool.acquireUnmarshaller();
            Object obj = unmarshaller.unmarshal(in);
            pool.recycle(unmarshaller);
            if (obj instanceof JAXBElement<?> element) {
                obj = element.getValue();
            }
            return dataType.cast(obj);
        } catch (JAXBException ex) {
            throw new UncheckedServiceException(ex);
        }
    }

}
