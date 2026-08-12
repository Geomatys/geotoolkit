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
package org.geotoolkit.ogcwxs.client.feature;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import org.apache.sis.xml.MarshallerPool;
import org.geotoolkit.client.service.AbstractService;
import org.geotoolkit.client.service.ServiceConfiguration;
import org.geotoolkit.client.service.ServiceRequest;
import org.geotoolkit.client.service.UncheckedServiceException;
import org.geotoolkit.ogc.xml.FilterMarshallerPool;
import org.geotoolkit.ogc.xml.FilterVersion;
import org.geotoolkit.ogc.xml.XMLFilter;
import org.geotoolkit.ogcwxs.request.feature.DescribeFeatureType;
import org.geotoolkit.ogcwxs.request.feature.GetCapabilities;
import org.geotoolkit.ogcwxs.request.feature.GetFeature;
import org.geotoolkit.ogcwxs.request.feature.GetPropertyValue;
import org.geotoolkit.wfs.xml.Query;
import org.geotoolkit.wfs.xml.ResultTypeType;
import org.geotoolkit.wfs.xml.ValueCollection;
import org.geotoolkit.wfs.xml.WFSBindingUtilities;
import org.geotoolkit.wfs.xml.WFSCapabilities;
import org.geotoolkit.wfs.xml.WFSFeatureCollection;
import org.geotoolkit.wfs.xml.WFSMarshallerPool;
import org.geotoolkit.wfs.xml.WFSVersion;
import org.geotoolkit.wfs.xml.WFSXmlFactory;
import org.opengis.filter.Filter;

/**
 * OGC Web Feature Service (WFS) client.
 *
 * <p>
 * Covers {@code GetCapabilities}, {@code DescribeFeatureType}, {@code GetFeature} and
 * {@code GetPropertyValue} (WFS 2.0+ only). Every request is sent as an HTTP POST with an XML
 * body, following the same transport used by the legacy {@code geotk-client-wfs} module : the
 * filter and query encodings make plain KVP/GET requests impractical.
 * </p>
 *
 * <p>
 * {@code Transaction}, {@code LockFeature} and Stored Queries are not covered by this client.
 * </p>
 *
 * @see https://www.ogc.org/standards/wfs/
 * @author Quentin Bialota (Geomatys)
 */
public final class WFSApi extends AbstractService {

    public WFSApi(ServiceConfiguration config) {
        super(config);
    }

    /**
     * Retrieve the service metadata document describing this WFS instance.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Supplier&lt;WFSCapabilities&gt;&gt;
     */
    public ServiceRequest<Supplier<WFSCapabilities>> getCapabilities(GetCapabilities parameters) {
        final WFSVersion version = version(parameters.getVersion());
        final org.geotoolkit.wfs.xml.GetCapabilities request = WFSXmlFactory.buildGetCapabilities(
                version.getCode(), null, null, null, parameters.getUpdateSequence(), "WFS");
        return post(request, version, WFSCapabilities.class);
    }

    /**
     * Retrieve, for a set of feature types, a description of their schema.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;byte[]&gt;
     */
    public ServiceRequest<byte[]> describeFeatureType(DescribeFeatureType parameters) {
        final WFSVersion version = version(parameters.getVersion());
        final org.geotoolkit.wfs.xml.DescribeFeatureType request = WFSXmlFactory.buildDecribeFeatureType(
                version.getCode(), "WFS", null, parameters.getTypeNames(), parameters.getOutputFormat());
        return post(request, version, HttpResponse.BodyHandlers.ofByteArray());
    }

    /**
     * Retrieve feature instances matching a filter on a single feature type.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Supplier&lt;WFSFeatureCollection&gt;&gt;
     */
    public ServiceRequest<Supplier<WFSFeatureCollection>> getFeature(GetFeature parameters) {
        final WFSVersion version = version(parameters.getVersion());
        return post(buildGetFeature(parameters, version), version, WFSFeatureCollection.class);
    }

    /**
     * Same request as {@link #getFeature(GetFeature)}, but returns the raw response stream
     * instead of unmarshalling it, for callers that stream features themselves (e.g. GML feature
     * parsing) rather than reading a {@link WFSFeatureCollection}.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;InputStream&gt;
     */
    public ServiceRequest<InputStream> getFeatureAsStream(GetFeature parameters) {
        final WFSVersion version = version(parameters.getVersion());
        return post(buildGetFeature(parameters, version), version, HttpResponse.BodyHandlers.ofInputStream());
    }

    private static org.geotoolkit.wfs.xml.GetFeature buildGetFeature(GetFeature parameters, WFSVersion version) {
        Objects.requireNonNull(parameters.getTypeName(), "Type name must be defined");

        final XMLFilter xmlFilter = toXmlFilter(parameters.getFilter(), version);
        final Query query = WFSXmlFactory.buildQuery(version.getCode(), xmlFilter,
                Collections.singletonList(parameters.getTypeName()), null, null, null, parameters.getPropertyNames());
        return WFSXmlFactory.buildGetFeature(version.getCode(), "WFS",
                null, null, parameters.getMaxFeatures(), query, ResultTypeType.RESULTS, parameters.getOutputFormat());
    }

    /**
     * Retrieve the value of a single property, for feature instances matching a filter on a
     * single feature type. Only supported by WFS 2.0+.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Supplier&lt;ValueCollection&gt;&gt;
     */
    public ServiceRequest<Supplier<ValueCollection>> getPropertyValue(GetPropertyValue parameters) {
        Objects.requireNonNull(parameters.getTypeName(), "Type name must be defined");
        Objects.requireNonNull(parameters.getValueReference(), "Value reference must be defined");
        final WFSVersion version = version(parameters.getVersion());

        final XMLFilter xmlFilter = toXmlFilter(parameters.getFilter(), version);
        final Query query = WFSXmlFactory.buildQuery(version.getCode(), xmlFilter,
                Collections.singletonList(parameters.getTypeName()), null, null, null, null);
        final org.geotoolkit.wfs.xml.GetPropertyValue request = WFSXmlFactory.buildGetPropertyValue(version.getCode(), "WFS",
                null, null, parameters.getMaxFeatures(), query, ResultTypeType.RESULTS, parameters.getOutputFormat(), parameters.getValueReference());

        return post(request, version, ValueCollection.class);
    }

    private static XMLFilter toXmlFilter(Filter filter, WFSVersion version) {
        if (filter == null || Filter.include().equals(filter)) return null;
        return FilterMarshallerPool.transform(filter, filterVersion(version));
    }

    private static FilterVersion filterVersion(WFSVersion version) {
        switch (version) {
            case v100: return FilterVersion.V100;
            case v110: return FilterVersion.V110;
            default:   return FilterVersion.V200;
        }
    }

    /**
     * Marshal the given WFS request object as the body of a POST request, and unmarshal the
     * response as the given data type.
     */
    private <T> ServiceRequest<Supplier<T>> post(Object requestObject, WFSVersion version, Class<T> dataType) {
        final HttpRequest.Builder request = toPostRequest(requestObject, version);
        return request(request, (HttpResponse.ResponseInfo responseInfo) -> {
            final HttpResponse.BodySubscriber<InputStream> upstream = HttpResponse.BodySubscribers.ofInputStream();
            return HttpResponse.BodySubscribers.mapping(upstream, (InputStream in) -> () -> {
                try {
                    return WFSBindingUtilities.unmarshall(in, version, dataType);
                } catch (JAXBException ex) {
                    throw new UncheckedServiceException(ex);
                }
            });
        });
    }

    private <T> ServiceRequest<T> post(Object requestObject, WFSVersion version, HttpResponse.BodyHandler<T> bodyHandler) {
        return request(toPostRequest(requestObject, version), bodyHandler);
    }

    private HttpRequest.Builder toPostRequest(Object requestObject, WFSVersion version) {
        final byte[] body = marshal(requestObject, version);
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("", List.of()));
        request.header("Content-Type", "text/xml");
        request.header("Accept", "text/xml, application/xml");
        request.method("POST", HttpRequest.BodyPublishers.ofByteArray(body));
        setConfig(request);
        return request;
    }

    private static byte[] marshal(Object requestObject, WFSVersion version) {
        final MarshallerPool pool = WFSMarshallerPool.getInstance(version);
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

    private static WFSVersion version(WFSVersion version) {
        //no "auto" version negotiation in WFSVersion, default to the latest version
        return version == null ? WFSVersion.v202 : version;
    }

}
