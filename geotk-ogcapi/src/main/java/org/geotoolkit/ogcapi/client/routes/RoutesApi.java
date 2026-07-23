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
package org.geotoolkit.ogcapi.client.routes;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.function.Supplier;
import org.geotoolkit.client.openapi.AbstractOpenApi;
import org.geotoolkit.client.openapi.OpenApiConfiguration;
import static org.geotoolkit.client.service.AbstractService.urlEncode;
import org.geotoolkit.client.service.ServiceRequest;
import org.geotoolkit.ogcapi.dto.geojson.GeoJSONFeatureCollection;
import org.geotoolkit.ogcapi.dto.routes.RouteList;
import org.geotoolkit.ogcapi.request.routes.CreateRoute;
import org.geotoolkit.ogcapi.request.routes.GetRoute;

/**
 * OGC API - Routes - Part 1: Core / Manage Routes client bindings.
 *
 * @author Quentin BIALOTA (Geomatys)
 * @see <a href="https://docs.ogc.org/DRAFTS/21-000.html">OGC API - Routes - Part 1: Core (draft)</a>
 */
public final class RoutesApi extends AbstractOpenApi {

    public RoutesApi(OpenApiConfiguration config) {
        super(config);
    }

    /**
     * Retrieve the list of routes previously created on this server (Manage Routes).
     *
     * @return ServiceRequest&lt;Supplier&lt;RouteList&gt;&gt;
     */
    public ServiceRequest<Supplier<RouteList>> getRoutes() {
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/routes", List.of()));
        request.header("Accept", "application/json");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, RouteList.class);
    }

    /**
     * Compute a route synchronously. On success, the response body is the Route Exchange Model
     * (a GeoJSON FeatureCollection) and, if the route was persisted (Manage Routes), a
     * {@code Location} header carries the URI of the new route, readable via
     * {@link java.net.http.HttpResponse#headers()} on the sent request.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Supplier&lt;GeoJSONFeatureCollection&gt;&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<GeoJSONFeatureCollection>> computeRoute(CreateRoute parameters) throws IllegalArgumentException {
        if (parameters.getBody() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'body' when calling computeRoute");
        }
        final byte[] bodyBytes;
        try {
            bodyBytes = objectMapper.writeValueAsBytes(parameters.getBody());
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to serialize route definition", e);
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/routes", List.of()));
        request.header("Accept", "application/geo+json");
        request.header("Content-Type", "application/json");
        request.method("POST", HttpRequest.BodyPublishers.ofByteArray(bodyBytes));
        setConfig(request);
        return requestAsJson(request, GeoJSONFeatureCollection.class);
    }

    /**
     * Retrieve a previously created route (Manage Routes).
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Supplier&lt;GeoJSONFeatureCollection&gt;&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<GeoJSONFeatureCollection>> getRoute(GetRoute parameters) throws IllegalArgumentException {
        if (parameters.getRouteId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'routeId' when calling getRoute");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/routes/" + urlEncode(parameters.getRouteId()), List.of()));
        request.header("Accept", "application/geo+json");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, GeoJSONFeatureCollection.class);
    }

    /**
     * Retrieve the definition (input parameters) of a previously created route (Manage Routes).
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Supplier&lt;Object&gt;&gt; the request body originally submitted to create the route
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<Object>> getRouteDefinition(GetRoute parameters) throws IllegalArgumentException {
        if (parameters.getRouteId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'routeId' when calling getRouteDefinition");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/routes/" + urlEncode(parameters.getRouteId()) + "/definition", List.of()));
        request.header("Accept", "application/json");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, Object.class);
    }

    /**
     * Delete a previously created route (Manage Routes).
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Void&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Void> deleteRoute(GetRoute parameters) throws IllegalArgumentException {
        if (parameters.getRouteId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'routeId' when calling deleteRoute");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/routes/" + urlEncode(parameters.getRouteId()), null));
        request.method("DELETE", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

}
