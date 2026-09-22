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
package org.geotoolkit.ogcwxs.client.transformation;

import java.io.InputStream;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import jakarta.xml.bind.JAXBException;
import org.geotoolkit.client.service.AbstractService;
import org.geotoolkit.client.service.Pair;
import org.geotoolkit.client.service.ServiceConfiguration;
import org.geotoolkit.client.service.ServiceRequest;
import org.geotoolkit.client.service.UncheckedServiceException;
import org.geotoolkit.ogcwxs.request.transformation.DescribeCRSRequest;
import org.geotoolkit.ogcwxs.request.transformation.DescribeMethodRequest;
import org.geotoolkit.ogcwxs.request.transformation.DescribeTransformationRequest;
import org.geotoolkit.ogcwxs.request.transformation.GetCapabilitiesRequest;
import org.geotoolkit.ogcwxs.request.transformation.GetTransformationRequest;
import org.geotoolkit.ogcwxs.request.transformation.IsTransformableRequest;
import org.geotoolkit.ogcwxs.request.transformation.TransformRequest;
import org.geotoolkit.wcts.xml.WCTSBindingUtilities;
import org.geotoolkit.wcts.xml.v030.CRSDescriptionType;
import org.geotoolkit.wcts.xml.v030.CapabilitiesType;
import org.geotoolkit.wcts.xml.v030.InputDataType;
import org.geotoolkit.wcts.xml.v030.IsTransformableRequestType;
import org.geotoolkit.wcts.xml.v030.IsTransformableResponseType;
import org.geotoolkit.wcts.xml.v030.MethodDescriptionType;
import org.geotoolkit.wcts.xml.v030.ReferenceType;
import org.geotoolkit.wcts.xml.v030.TransformDescriptionType;
import org.geotoolkit.wcts.xml.v030.TransformRequestType;
import org.geotoolkit.wcts.xml.v030.TransformationRefType;
import org.geotoolkit.wcts.xml.v030.TransformedDataType;

/**
 * OGC Web Coordinate Transformation Service (WCTS) client.
 *
 * <p>
 * Covers the 7 operations of the 05-013 0.3.0 Discussion Paper : {@code GetCapabilities},
 * {@code GetTransformation}, {@code DescribeTransformation}, {@code DescribeCRS} and
 * {@code DescribeMethod} are sent as HTTP GET (KVP), while {@code Transform} and
 * {@code IsTransformable} are sent as HTTP POST of an XML document, per the operations' mandatory
 * encoding in the spec.
 * </p>
 *
 * @see https://www.ogc.org/standards/ct/
 * @author Quentin Bialota (Geomatys)
 */
public final class TransformationApi extends AbstractService {

    public TransformationApi(ServiceConfiguration config) {
        super(config);
    }

    /**
     * Retrieve the capabilities document of the service.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Supplier&lt;CapabilitiesType&gt;&gt;
     */
    public ServiceRequest<Supplier<CapabilitiesType>> getCapabilities(GetCapabilitiesRequest parameters) {
        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs("SERVICE", "WCTS"));
        queryParams.addAll(toPairs("REQUEST", "GetCapabilities"));
        queryParams.addAll(toPairs("UPDATESEQUENCE", parameters.getUpdateSequence()));
        return requestAsXml(toGetRequest(queryParams), CapabilitiesType.class);
    }

    /**
     * Retrieve the transformation(s) a server would use between a source and a target CRS.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Supplier&lt;TransformDescriptionType&gt;&gt;
     */
    public ServiceRequest<Supplier<TransformDescriptionType>> getTransformation(GetTransformationRequest parameters) {
        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs("SERVICE", "WCTS"));
        queryParams.addAll(toPairs("VERSION", "0.3.0"));
        queryParams.addAll(toPairs("REQUEST", "GetTransformation"));
        queryParams.addAll(toPairs("SOURCECRS", parameters.getSourceCRS()));
        queryParams.addAll(toPairs("TARGETCRS", parameters.getTargetCRS()));
        return requestAsXml(toGetRequest(queryParams), TransformDescriptionType.class);
    }

    /**
     * Retrieve a description of one or more named transformations known by the service.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Supplier&lt;TransformDescriptionType&gt;&gt;
     */
    public ServiceRequest<Supplier<TransformDescriptionType>> describeTransformation(DescribeTransformationRequest parameters) {
        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs("SERVICE", "WCTS"));
        queryParams.addAll(toPairs("VERSION", "0.3.0"));
        queryParams.addAll(toPairs("REQUEST", "DescribeTransformation"));
        queryParams.addAll(toPairs("csv", "TRANSFORMATION", parameters.getTransformation()));
        return requestAsXml(toGetRequest(queryParams), TransformDescriptionType.class);
    }

    /**
     * Retrieve a description of one or more CRSs known by the service.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Supplier&lt;CRSDescriptionType&gt;&gt;
     */
    public ServiceRequest<Supplier<CRSDescriptionType>> describeCRS(DescribeCRSRequest parameters) {
        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs("SERVICE", "WCTS"));
        queryParams.addAll(toPairs("VERSION", "0.3.0"));
        queryParams.addAll(toPairs("REQUEST", "DescribeCRS"));
        queryParams.addAll(toPairs("csv", "CRS", parameters.getCrs()));
        return requestAsXml(toGetRequest(queryParams), CRSDescriptionType.class);
    }

    /**
     * Retrieve a description of one or more coordinate operation methods known by the service.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Supplier&lt;MethodDescriptionType&gt;&gt;
     */
    public ServiceRequest<Supplier<MethodDescriptionType>> describeMethod(DescribeMethodRequest parameters) {
        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs("SERVICE", "WCTS"));
        queryParams.addAll(toPairs("VERSION", "0.3.0"));
        queryParams.addAll(toPairs("REQUEST", "DescribeMethod"));
        queryParams.addAll(toPairs("csv", "METHOD", parameters.getMethod()));
        return requestAsXml(toGetRequest(queryParams), MethodDescriptionType.class);
    }

    /**
     * Transform input data from a source CRS to a target CRS, or through a named transformation.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Supplier&lt;TransformedDataType&gt;&gt;
     */
    public ServiceRequest<Supplier<TransformedDataType>> transform(TransformRequest parameters) {
        final TransformRequestType request = new TransformRequestType();
        request.setSourceCRS(parameters.getSourceCRS());
        request.setTargetCRS(parameters.getTargetCRS());
        if (parameters.getTransformation() != null) {
            request.setTransformation(new TransformationRefType(parameters.getTransformation()));
        }
        final InputDataType inputData = new InputDataType();
        if (parameters.getInputData() != null) {
            for (String href : parameters.getInputData()) {
                inputData.getReference().add(new ReferenceType(href));
            }
        }
        request.setInputData(inputData);
        request.setInterpolationMethod(parameters.getInterpolationMethod());
        request.setOutputFormat(parameters.getOutputFormat());
        request.setStore(parameters.getStore());
        return postAsXml(request, TransformedDataType.class);
    }

    /**
     * Check whether the service can transform between two CRSs, or perform a named transformation
     * or method, for given geometry/coverage types.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Supplier&lt;IsTransformableResponseType&gt;&gt;
     */
    public ServiceRequest<Supplier<IsTransformableResponseType>> isTransformable(IsTransformableRequest parameters) {
        final IsTransformableRequestType request = new IsTransformableRequestType();
        request.setSourceCRS(parameters.getSourceCRS());
        request.setTargetCRS(parameters.getTargetCRS());
        request.setTransformation(parameters.getTransformation());
        request.setMethod(parameters.getMethod());
        if (parameters.getGeometryType() != null) {
            request.getGeometryType().addAll(parameters.getGeometryType());
        }
        if (parameters.getCoverageType() != null) {
            request.getCoverageType().addAll(parameters.getCoverageType());
        }
        if (parameters.getInterpolationMethod() != null) {
            request.getInterpolationMethod().addAll(parameters.getInterpolationMethod());
        }
        return postAsXml(request, IsTransformableResponseType.class);
    }

    private HttpRequest.Builder toGetRequest(List<Pair> queryParams) {
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("", queryParams));
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return request;
    }

    private <T> ServiceRequest<Supplier<T>> requestAsXml(HttpRequest.Builder request, Class<T> responseType) {
        return request(request, (HttpResponse.ResponseInfo responseInfo) -> {
            final HttpResponse.BodySubscriber<InputStream> upstream = HttpResponse.BodySubscribers.ofInputStream();
            return HttpResponse.BodySubscribers.mapping(upstream, (InputStream in) -> () -> {
                try {
                    return WCTSBindingUtilities.unmarshall(in, responseType);
                } catch (JAXBException ex) {
                    throw new UncheckedServiceException(ex);
                }
            });
        });
    }

    private <T> ServiceRequest<Supplier<T>> postAsXml(Object requestBody, Class<T> responseType) {
        final byte[] body;
        try {
            body = WCTSBindingUtilities.marshall(requestBody);
        } catch (JAXBException ex) {
            throw new UncheckedServiceException(ex);
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("", List.of()));
        request.header("Content-Type", "text/xml");
        request.header("Accept", "text/xml, application/xml");
        request.method("POST", HttpRequest.BodyPublishers.ofByteArray(body));
        setConfig(request);
        return requestAsXml(request, responseType);
    }
}
