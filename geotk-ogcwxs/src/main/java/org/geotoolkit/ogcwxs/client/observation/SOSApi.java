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
import org.geotoolkit.ogc.xml.FilterMarshallerPool;
import org.geotoolkit.ogc.xml.FilterVersion;
import org.geotoolkit.ogcwxs.request.observation.DescribeFeatureType;
import org.geotoolkit.ogcwxs.request.observation.DescribeObservationType;
import org.geotoolkit.ogcwxs.request.observation.DescribeResultModel;
import org.geotoolkit.ogcwxs.request.observation.DescribeSensor;
import org.geotoolkit.ogcwxs.request.observation.GetCapabilities;
import org.geotoolkit.ogcwxs.request.observation.GetFeatureOfInterest;
import org.geotoolkit.ogcwxs.request.observation.GetFeatureOfInterestTime;
import org.geotoolkit.ogcwxs.request.observation.GetObservation;
import org.geotoolkit.ogcwxs.request.observation.GetObservationById;
import org.geotoolkit.ogcwxs.request.observation.GetResult;
import org.geotoolkit.ogcwxs.request.observation.SOSVersion;
import org.geotoolkit.sos.xml.Capabilities;
import org.geotoolkit.sos.xml.SOSMarshallerPool;
import org.geotoolkit.sos.xml.SOSXmlFactory;
import org.opengis.filter.Filter;
import org.opengis.observation.ObservationCollection;

/**
 * OGC Sensor Observation Service (SOS) client.
 *
 * <p>
 * Covers {@code GetCapabilities}, {@code DescribeSensor}, {@code GetObservation},
 * {@code GetObservationById}, {@code GetResult}, {@code GetFeatureOfInterest},
 * {@code GetFeatureOfInterestTime}, {@code DescribeFeatureType}, {@code DescribeObservationType}
 * and {@code DescribeResultModel}. Both SOS 1.0.0 and SOS 2.0.0 are supported for these read
 * operations; {@code DescribeFeatureType}, {@code DescribeObservationType},
 * {@code DescribeResultModel} and {@code GetFeatureOfInterestTime} remain 1.0.0-only, since
 * {@code geotk-xml-sos} has no 2.0.0 binding for them.
 * </p>
 *
 * <p>
 * Insert operations ({@code InsertObservation}, {@code InsertResult}, {@code InsertResultTemplate},
 * {@code InsertSensor}, ...) and {@code GetResultTemplate} are not covered by this client: it is
 * read-only, mirroring the legacy {@code geotk-client-sos} module which never implemented any
 * write operation either.
 * </p>
 *
 * @see https://www.ogc.org/standards/sos/
 * @author Quentin Bialota (Geomatys)
 */
public final class SOSApi extends AbstractService {

    private static final String SERVICE = "SOS";

    public SOSApi(ServiceConfiguration config) {
        super(config);
    }

    /**
     * Retrieve the service metadata document describing this SOS instance.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Supplier&lt;Capabilities&gt;&gt;
     */
    public ServiceRequest<Supplier<Capabilities>> getCapabilities(GetCapabilities parameters) {
        final SOSVersion version = version(parameters.getVersion());
        final org.geotoolkit.sos.xml.GetCapabilities request = SOSXmlFactory.buildGetCapabilities(version.getCode(), SERVICE);
        return post(request, Capabilities.class);
    }

    /**
     * Retrieve a description (typically SensorML) of a sensor.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;byte[]&gt;
     */
    public ServiceRequest<byte[]> describeSensor(DescribeSensor parameters) {
        final SOSVersion version = version(parameters.getVersion());
        final org.geotoolkit.swes.xml.DescribeSensor request = SOSXmlFactory.buildDescribeSensor(
                version.getCode(), SERVICE, parameters.getSensorId(), parameters.getOutputFormat());
        return post(request, HttpResponse.BodyHandlers.ofByteArray());
    }

    /**
     * Retrieve observations matching an offering, a time period and a set of observed properties.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Supplier&lt;ObservationCollection&gt;&gt;
     */
    public ServiceRequest<Supplier<ObservationCollection>> getObservation(GetObservation parameters) {
        final SOSVersion version = version(parameters.getVersion());
        final List<String> procedures = parameters.getProcedures() == null
                ? null : Arrays.asList(parameters.getProcedures());
        final List<String> observedProperties = parameters.getObservedProperties() == null
                ? null : Arrays.asList(parameters.getObservedProperties());
        final List<String> offering = parameters.getOffering() == null
                ? null : List.of(parameters.getOffering());
        final List<Filter> temporalFilter = toXmlFilters(parameters.getTemporalFilters(), version);
        final Filter spatialFilter = toXmlFilter(parameters.getSpatialFilter(), version);
        final Filter resultFilter = toXmlFilter(parameters.getResultFilter(), version);
        final org.geotoolkit.sos.xml.GetObservation request = SOSXmlFactory.buildGetObservation(
                version.getCode(), SERVICE, offering, observedProperties, procedures, parameters.getFeatureOfInterestIds(),
                parameters.getResponseFormat(), temporalFilter, spatialFilter, resultFilter,
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
        final SOSVersion version = version(parameters.getVersion());
        final List<String> observations = parameters.getObservationId() == null
                ? null : List.of(parameters.getObservationId());
        final org.geotoolkit.sos.xml.GetObservationById request = SOSXmlFactory.buildGetObservationById(
                version.getCode(), SERVICE, observations, parameters.getResultModel(), parameters.getResponseMode(),
                parameters.getSrsName(), parameters.getResponseFormat(), null);
        return post(request, ObservationCollection.class);
    }

    /**
     * Retrieve only the result data of observations, either matching an observation template
     * (SOS 1.0.0) or an offering/observedProperty/foid selection (SOS 2.0.0).
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;byte[]&gt;
     */
    public ServiceRequest<byte[]> getResult(GetResult parameters) {
        final SOSVersion version = version(parameters.getVersion());
        final List<Filter> temporalFilter = toXmlFilters(parameters.getTemporalFilters(), version);
        final org.geotoolkit.sos.xml.GetResult request;
        if (version == SOSVersion.v200) {
            final Filter spatialFilter = toXmlFilter(parameters.getSpatialFilter(), version);
            request = SOSXmlFactory.buildGetResult(version.getCode(), SERVICE, parameters.getOffering(),
                    parameters.getObservedProperty(), parameters.getFeatureOfInterestIds(), spatialFilter, temporalFilter, null);
        } else {
            request = SOSXmlFactory.buildGetResult(version.getCode(), parameters.getObservationTemplateId(), temporalFilter);
        }
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
        final SOSVersion version = version(parameters.getVersion());
        final org.geotoolkit.sos.xml.GetFeatureOfInterest request;
        if (parameters.getLocation() != null) {
            final Filter spatialFilter = toXmlFilter(parameters.getLocation(), version);
            request = SOSXmlFactory.buildGetFeatureOfInterest(version.getCode(), SERVICE, null, null, null, spatialFilter, null);
        } else {
            final List<String> featureId = parameters.getFeatureOfInterestId() == null
                    ? null : List.of(parameters.getFeatureOfInterestId());
            request = SOSXmlFactory.buildGetFeatureOfInterest(version.getCode(), SERVICE, featureId);
        }
        return post(request, HttpResponse.BodyHandlers.ofByteArray());
    }

    /**
     * Retrieve the time period during which a feature-of-interest has been observed.
     *
     * <p>SOS 1.0.0 only: {@code geotk-xml-sos} has no 2.0.0 binding for this operation.</p>
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;byte[]&gt;
     */
    public ServiceRequest<byte[]> getFeatureOfInterestTime(GetFeatureOfInterestTime parameters) {
        final org.geotoolkit.sos.xml.v100.GetFeatureOfInterestTime request = new org.geotoolkit.sos.xml.v100.GetFeatureOfInterestTime(
                SOSVersion.v100.getCode(), parameters.getFeatureOfInterestId());
        return post(request, HttpResponse.BodyHandlers.ofByteArray());
    }

    /**
     * Retrieve a description of the feature-of-interest type used by a sensor.
     *
     * <p>SOS 1.0.0 only: {@code geotk-xml-sos} has no 2.0.0 binding for this operation.</p>
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;byte[]&gt;
     */
    public ServiceRequest<byte[]> describeFeatureType(DescribeFeatureType parameters) {
        final org.geotoolkit.sos.xml.v100.DescribeFeatureType request = new org.geotoolkit.sos.xml.v100.DescribeFeatureType(
                SOSVersion.v100.getCode(), parameters.getFeatureId());
        return post(request, HttpResponse.BodyHandlers.ofByteArray());
    }

    /**
     * Retrieve a description of the type of an observed property.
     *
     * <p>SOS 1.0.0 only: {@code geotk-xml-sos} has no 2.0.0 binding for this operation.</p>
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;byte[]&gt;
     */
    public ServiceRequest<byte[]> describeObservationType(DescribeObservationType parameters) {
        final org.geotoolkit.sos.xml.v100.DescribeObservationType request = new org.geotoolkit.sos.xml.v100.DescribeObservationType(
                SOSVersion.v100.getCode(), parameters.getObservedProperty());
        return post(request, HttpResponse.BodyHandlers.ofByteArray());
    }

    /**
     * Retrieve a description of the model used for observation results of a given type.
     *
     * <p>SOS 1.0.0 only: {@code geotk-xml-sos} has no 2.0.0 binding for this operation.</p>
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;byte[]&gt;
     */
    public ServiceRequest<byte[]> describeResultModel(DescribeResultModel parameters) {
        final org.geotoolkit.sos.xml.v100.DescribeResultModel request = new org.geotoolkit.sos.xml.v100.DescribeResultModel(
                SOSVersion.v100.getCode(), parameters.getResultName());
        return post(request, HttpResponse.BodyHandlers.ofByteArray());
    }

    private static SOSVersion version(SOSVersion version) {
        return version != null ? version : SOSVersion.v100;
    }

    private static Filter toXmlFilter(Filter filter, SOSVersion version) {
        if (filter == null || Filter.include().equals(filter)) return null;
        return (Filter) FilterMarshallerPool.transform(filter, filterVersion(version));
    }

    private static List<Filter> toXmlFilters(List<Filter> filters, SOSVersion version) {
        if (filters == null) return null;
        return filters.stream().map(f -> toXmlFilter(f, version)).toList();
    }

    private static FilterVersion filterVersion(SOSVersion version) {
        return version == SOSVersion.v200 ? FilterVersion.V200 : FilterVersion.V110;
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
