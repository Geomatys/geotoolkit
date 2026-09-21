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
package org.geotoolkit.ogcwxs.client.location;

import java.io.InputStream;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.function.Supplier;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import org.geotoolkit.client.service.AbstractService;
import org.geotoolkit.client.service.ServiceConfiguration;
import org.geotoolkit.client.service.ServiceRequest;
import org.geotoolkit.client.service.UncheckedServiceException;
import org.geotoolkit.ogcwxs.request.location.DetermineRouteRequest;
import org.geotoolkit.ogcwxs.request.location.DirectoryRequest;
import org.geotoolkit.ogcwxs.request.location.GeocodeRequest;
import org.geotoolkit.ogcwxs.request.location.GetPortrayMapCapabilitiesRequest;
import org.geotoolkit.ogcwxs.request.location.LocateRequest;
import org.geotoolkit.ogcwxs.request.location.PortrayMapRequest;
import org.geotoolkit.ogcwxs.request.location.ReverseGeocodeRequest;
import org.geotoolkit.ols.xml.OLSBindingUtilities;
import org.geotoolkit.ols.xml.OLSXmlFactory;
import org.geotoolkit.ols.xml.v121.AbstractRequestParametersType;
import org.geotoolkit.ols.xml.v121.AbstractResponseParametersType;
import org.geotoolkit.ols.xml.v121.DetermineRouteRequestType;
import org.geotoolkit.ols.xml.v121.DetermineRouteResponseType;
import org.geotoolkit.ols.xml.v121.DirectoryRequestType;
import org.geotoolkit.ols.xml.v121.DirectoryResponseType;
import org.geotoolkit.ols.xml.v121.GeocodeRequestType;
import org.geotoolkit.ols.xml.v121.GeocodeResponseType;
import org.geotoolkit.ols.xml.v121.GetPortrayMapCapabilitiesRequestType;
import org.geotoolkit.ols.xml.v121.GetPortrayMapCapabilitiesResponseType;
import org.geotoolkit.ols.xml.v121.ObjectFactory;
import org.geotoolkit.ols.xml.v121.PortrayMapRequestType;
import org.geotoolkit.ols.xml.v121.PortrayMapResponseType;
import org.geotoolkit.ols.xml.v121.RequestHeaderType;
import org.geotoolkit.ols.xml.v121.ReverseGeocodeRequestType;
import org.geotoolkit.ols.xml.v121.ReverseGeocodeResponseType;
import org.geotoolkit.ols.xml.v121.SLIAType;
import org.geotoolkit.ols.xml.v121.SLIRType;
import org.geotoolkit.ols.xml.v121.XLSType;
import org.geotoolkit.ogcwxs.request.location.AbstractLocationRequest;

/**
 * OGC OpenLS (OLS) 1.2.0 Location Service client.
 *
 * <p>
 * Covers the 5 OpenLS core services : Location Utility ({@code GeocodeRequest},
 * {@code ReverseGeocodeRequest}), Directory ({@code DirectoryRequest}), Route
 * ({@code DetermineRouteRequest}), Presentation ({@code PortrayMapRequest},
 * {@code GetPortrayMapCapabilitiesRequest}) and Gateway ({@code SLIR}). Every request is sent as
 * an HTTP POST of an {@code <XLS>} envelope, the only transport defined by OGC 07-074.
 * </p>
 *
 * @see https://www.ogc.org/standards/ols/
 * @author Quentin Bialota (Geomatys)
 */
public final class LocationApi extends AbstractService {

    private static final ObjectFactory FACTORY = new ObjectFactory();

    public LocationApi(ServiceConfiguration config) {
        super(config);
    }

    /**
     * Geocode one or more addresses.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Supplier&lt;GeocodeResponseType&gt;&gt;
     */
    public ServiceRequest<Supplier<GeocodeResponseType>> geocode(GeocodeRequest parameters) {
        final GeocodeRequestType request = new GeocodeRequestType();
        request.getAddress().addAll(parameters.getAddress());
        request.setReturnFreeForm(parameters.getReturnFreeForm());
        return post(parameters, "GeocodeRequest", FACTORY.createGeocodeRequest(request), GeocodeResponseType.class);
    }

    /**
     * Reverse-geocode a position into one or more addresses.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Supplier&lt;ReverseGeocodeResponseType&gt;&gt;
     */
    public ServiceRequest<Supplier<ReverseGeocodeResponseType>> reverseGeocode(ReverseGeocodeRequest parameters) {
        final ReverseGeocodeRequestType request = new ReverseGeocodeRequestType();
        request.setPosition(parameters.getPosition());
        request.getReverseGeocodePreference().addAll(parameters.getReverseGeocodePreference());
        return post(parameters, "ReverseGeocodeRequest", FACTORY.createReverseGeocodeRequest(request), ReverseGeocodeResponseType.class);
    }

    /**
     * Search points of interest.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Supplier&lt;DirectoryResponseType&gt;&gt;
     */
    public ServiceRequest<Supplier<DirectoryResponseType>> directoryRequest(DirectoryRequest parameters) {
        final DirectoryRequestType request = new DirectoryRequestType();
        request.setPOILocation(parameters.getPoiLocation());
        request.setPOISelectionCriteria(parameters.getPoiSelectionCriteria());
        request.setSortCriteria(parameters.getSortCriteria());
        if (parameters.getSortDirection() != null) request.setSortDirection(parameters.getSortDirection());
        if (parameters.getDistanceUnit() != null) request.setDistanceUnit(parameters.getDistanceUnit());
        return post(parameters, "DirectoryRequest", FACTORY.createDirectoryRequest(request), DirectoryResponseType.class);
    }

    /**
     * Compute a route between way points.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Supplier&lt;DetermineRouteResponseType&gt;&gt;
     */
    public ServiceRequest<Supplier<DetermineRouteResponseType>> determineRoute(DetermineRouteRequest parameters) {
        final DetermineRouteRequestType request = new DetermineRouteRequestType();
        request.setRouteHandle(parameters.getRouteHandle());
        request.setRoutePlan(parameters.getRoutePlan());
        request.setRouteInstructionsRequest(parameters.getRouteInstructionsRequest());
        request.setRouteGeometryRequest(parameters.getRouteGeometryRequest());
        request.setRouteMapRequest(parameters.getRouteMapRequest());
        request.setProvideRouteHandle(parameters.getProvideRouteHandle());
        if (parameters.getDistanceUnit() != null) request.setDistanceUnit(parameters.getDistanceUnit());
        return post(parameters, "DetermineRouteRequest", FACTORY.createDetermineRouteRequest(request), DetermineRouteResponseType.class);
    }

    /**
     * Render a map.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Supplier&lt;PortrayMapResponseType&gt;&gt;
     */
    public ServiceRequest<Supplier<PortrayMapResponseType>> portrayMap(PortrayMapRequest parameters) {
        final PortrayMapRequestType request = new PortrayMapRequestType();
        request.getOutput().addAll(parameters.getOutput());
        request.setBasemap(parameters.getBasemap());
        request.getOverlay().addAll(parameters.getOverlay());
        return post(parameters, "PortrayMapRequest", FACTORY.createPortrayMapRequest(request), PortrayMapResponseType.class);
    }

    /**
     * Retrieve the capabilities (available SRS, layers, formats, styles) of the Presentation
     * service.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Supplier&lt;GetPortrayMapCapabilitiesResponseType&gt;&gt;
     */
    public ServiceRequest<Supplier<GetPortrayMapCapabilitiesResponseType>> getPortrayMapCapabilities(GetPortrayMapCapabilitiesRequest parameters) {
        final GetPortrayMapCapabilitiesRequestType request = new GetPortrayMapCapabilitiesRequestType();
        return post(parameters, "GetPortrayMapCapabilitiesRequest", FACTORY.createGetPortrayMapCapabilitiesRequest(request), GetPortrayMapCapabilitiesResponseType.class);
    }

    /**
     * Locate one or more mobile subscribers (Gateway Service Standard Location Immediate
     * Request).
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Supplier&lt;SLIAType&gt;&gt;
     */
    public ServiceRequest<Supplier<SLIAType>> locate(LocateRequest parameters) {
        final SLIRType request = new SLIRType();
        request.setInputGatewayParameters(parameters.getInputGatewayParameters());
        return post(parameters, "SLIR", FACTORY.createSLIR(request), SLIAType.class);
    }

    /**
     * Wrap the given operation-specific payload into an {@code <XLS>} request envelope, POST it,
     * and unmarshal + validate the response as the given response parameters type.
     */
    private <T extends AbstractResponseParametersType> ServiceRequest<Supplier<T>> post(AbstractLocationRequest<?> parameters,
            String methodName, JAXBElement<? extends AbstractRequestParametersType> requestParameters, Class<T> responseType) {
        final RequestHeaderType header = toHeader(parameters);
        final XLSType xls = OLSXmlFactory.buildRequestEnvelope(header, methodName, parameters.getRequestID(),
                parameters.getMaximumResponses(), requestParameters);
        return request(toPostRequest(xls), (HttpResponse.ResponseInfo responseInfo) -> {
            final HttpResponse.BodySubscriber<InputStream> upstream = HttpResponse.BodySubscribers.ofInputStream();
            return HttpResponse.BodySubscribers.mapping(upstream, (InputStream in) -> () -> {
                try {
                    return OLSXmlFactory.extractResponseParameters(OLSBindingUtilities.unmarshall(in), responseType);
                } catch (JAXBException ex) {
                    throw new UncheckedServiceException(ex);
                }
            });
        });
    }

    private static RequestHeaderType toHeader(AbstractLocationRequest<?> parameters) {
        if (parameters.getClientName() == null && parameters.getClientPassword() == null
                && parameters.getSessionID() == null && parameters.getSrsName() == null
                && parameters.getMsid() == null) {
            return null;
        }
        return OLSXmlFactory.buildRequestHeader(parameters.getClientName(), parameters.getClientPassword(),
                parameters.getSessionID(), parameters.getSrsName(), parameters.getMsid());
    }

    private HttpRequest.Builder toPostRequest(XLSType xls) {
        final byte[] body;
        try {
            body = OLSBindingUtilities.marshall(xls);
        } catch (JAXBException ex) {
            throw new UncheckedServiceException(ex);
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("", List.of()));
        request.header("Content-Type", "text/xml");
        request.header("Accept", "text/xml, application/xml");
        request.method("POST", HttpRequest.BodyPublishers.ofByteArray(body));
        setConfig(request);
        return request;
    }
}
