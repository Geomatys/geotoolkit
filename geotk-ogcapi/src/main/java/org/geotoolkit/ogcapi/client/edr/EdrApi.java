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
package org.geotoolkit.ogcapi.client.edr;

import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import org.geotoolkit.client.openapi.AbstractOpenApi;
import org.geotoolkit.client.openapi.OpenApiConfiguration;
import org.geotoolkit.client.service.Pair;
import org.geotoolkit.client.service.ServiceRequest;
import org.geotoolkit.ogcapi.dto.coveragejson.CoverageJson;
import org.geotoolkit.ogcapi.dto.edr.EdrCollection;
import org.geotoolkit.ogcapi.dto.edr.EdrInstances;
import org.geotoolkit.ogcapi.dto.geojson.GeoJSONFeature;
import org.geotoolkit.ogcapi.dto.geojson.GeoJSONFeatureCollection;
import org.geotoolkit.ogcapi.request.common.GetCollection;
import org.geotoolkit.ogcapi.request.edr.GetArea;
import org.geotoolkit.ogcapi.request.edr.GetCorridor;
import org.geotoolkit.ogcapi.request.edr.GetCube;
import org.geotoolkit.ogcapi.request.edr.GetEdrInstances;
import org.geotoolkit.ogcapi.request.edr.GetItem;
import org.geotoolkit.ogcapi.request.edr.GetItems;
import org.geotoolkit.ogcapi.request.edr.GetLocation;
import org.geotoolkit.ogcapi.request.edr.GetLocations;
import org.geotoolkit.ogcapi.request.edr.GetPosition;
import org.geotoolkit.ogcapi.request.edr.GetRadius;
import org.geotoolkit.ogcapi.request.edr.GetTrajectory;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKTWriter;
import org.opengis.geometry.Envelope;

/**
 * OGC API - Environmental Data Retrieval (EDR) Standard, Part 1: Core.
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class EdrApi extends AbstractOpenApi {

    private static final String ACCEPT_COVERAGE = "application/vnd.cov+json, application/geo+json, text/xml";
    private static final String ACCEPT_FEATURES = "application/geo+json, application/json, text/html";
    private static final String ACCEPT_COLLECTION = "application/json, text/html";

    public EdrApi(OpenApiConfiguration config) {
        super(config);
    }

    /**
     * Retrieve the description of an EDR collection.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;EdrCollection&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<EdrCollection>> getCollection(GetCollection parameters) throws IllegalArgumentException {
        if (parameters.getCollectionId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'collectionId' when calling getCollection");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/collections/" + urlEncode(parameters.getCollectionId()), toPairs("f", parameters.getFormat())));
        request.header("Accept", ACCEPT_COLLECTION);
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, EdrCollection.class);
    }

    /**
     * Retrieve the list of instances of an EDR collection.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;EdrInstances&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<EdrInstances>> getInstances(GetEdrInstances parameters) throws IllegalArgumentException {
        if (parameters.getCollectionId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'collectionId' when calling getInstances");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/collections/" + urlEncode(parameters.getCollectionId()) + "/instances", toPairs("f", parameters.getF())));
        request.header("Accept", ACCEPT_COLLECTION);
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, EdrInstances.class);
    }

    /**
     * The position query retrieves data at a single point location.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;CoverageJson&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<CoverageJson>> getPosition(GetPosition parameters) throws IllegalArgumentException {
        if (parameters.getCollectionId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'collectionId' when calling getPosition");
        }

        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs("coords", toWkt(parameters.getCoords())));
        queryParams.addAll(toPairs("z", parameters.getZ()));
        queryParams.addAll(toPairs("datetime", parameters.getDatetime()));
        queryParams.addAll(toPairs("csv", "parameter-name", parameters.getParameterName()));
        queryParams.addAll(toPairs("crs", parameters.getCrs()));
        queryParams.addAll(toPairs("limit", parameters.getLimit()));
        queryParams.addAll(toPairs("f", parameters.getF()));

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(basePath(parameters.getCollectionId(), parameters.getInstanceId()) + "/position", queryParams));
        request.header("Accept", ACCEPT_COVERAGE);
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, CoverageJson.class);
    }

    /**
     * The radius query retrieves data within a defined radius of a point location.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;CoverageJson&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<CoverageJson>> getRadius(GetRadius parameters) throws IllegalArgumentException {
        if (parameters.getCollectionId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'collectionId' when calling getRadius");
        }

        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs("coords", toWkt(parameters.getCoords())));
        queryParams.addAll(toPairs("within", parameters.getWithin()));
        queryParams.addAll(toPairs("within-units", parameters.getWithinUnits()));
        queryParams.addAll(toPairs("z", parameters.getZ()));
        queryParams.addAll(toPairs("datetime", parameters.getDatetime()));
        queryParams.addAll(toPairs("csv", "parameter-name", parameters.getParameterName()));
        queryParams.addAll(toPairs("crs", parameters.getCrs()));
        queryParams.addAll(toPairs("limit", parameters.getLimit()));
        queryParams.addAll(toPairs("f", parameters.getF()));

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(basePath(parameters.getCollectionId(), parameters.getInstanceId()) + "/radius", queryParams));
        request.header("Accept", ACCEPT_COVERAGE);
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, CoverageJson.class);
    }

    /**
     * The area query retrieves data within a defined area.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;CoverageJson&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<CoverageJson>> getArea(GetArea parameters) throws IllegalArgumentException {
        if (parameters.getCollectionId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'collectionId' when calling getArea");
        }

        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs("coords", toWkt(parameters.getCoords())));
        queryParams.addAll(toPairs("z", parameters.getZ()));
        queryParams.addAll(toPairs("datetime", parameters.getDatetime()));
        queryParams.addAll(toPairs("csv", "parameter-name", parameters.getParameterName()));
        queryParams.addAll(toPairs("crs", parameters.getCrs()));
        queryParams.addAll(toPairs("resolution-x", parameters.getResolutionX()));
        queryParams.addAll(toPairs("resolution-y", parameters.getResolutionY()));
        queryParams.addAll(toPairs("limit", parameters.getLimit()));
        queryParams.addAll(toPairs("f", parameters.getF()));

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(basePath(parameters.getCollectionId(), parameters.getInstanceId()) + "/area", queryParams));
        request.header("Accept", ACCEPT_COVERAGE);
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, CoverageJson.class);
    }

    /**
     * The cube query retrieves data for a defined data cube.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;CoverageJson&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<CoverageJson>> getCube(GetCube parameters) throws IllegalArgumentException {
        if (parameters.getCollectionId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'collectionId' when calling getCube");
        }

        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs("csv", "bbox", toList(parameters.getBbox())));
        queryParams.addAll(toPairs("z", parameters.getZ()));
        queryParams.addAll(toPairs("datetime", parameters.getDatetime()));
        queryParams.addAll(toPairs("csv", "parameter-name", parameters.getParameterName()));
        queryParams.addAll(toPairs("resolution-x", parameters.getResolutionX()));
        queryParams.addAll(toPairs("resolution-y", parameters.getResolutionY()));
        queryParams.addAll(toPairs("resolution-z", parameters.getResolutionZ()));
        queryParams.addAll(toPairs("crs", parameters.getCrs()));
        queryParams.addAll(toPairs("f", parameters.getF()));

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(basePath(parameters.getCollectionId(), parameters.getInstanceId()) + "/cube", queryParams));
        request.header("Accept", ACCEPT_COVERAGE);
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, CoverageJson.class);
    }

    /**
     * The trajectory query retrieves data along a defined trajectory.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;CoverageJson&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<CoverageJson>> getTrajectory(GetTrajectory parameters) throws IllegalArgumentException {
        if (parameters.getCollectionId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'collectionId' when calling getTrajectory");
        }

        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs("coords", toWkt(parameters.getCoords())));
        queryParams.addAll(toPairs("z", parameters.getZ()));
        queryParams.addAll(toPairs("datetime", parameters.getDatetime()));
        queryParams.addAll(toPairs("csv", "parameter-name", parameters.getParameterName()));
        queryParams.addAll(toPairs("crs", parameters.getCrs()));
        queryParams.addAll(toPairs("f", parameters.getF()));

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(basePath(parameters.getCollectionId(), parameters.getInstanceId()) + "/trajectory", queryParams));
        request.header("Accept", ACCEPT_COVERAGE);
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, CoverageJson.class);
    }

    /**
     * The corridor query retrieves data along a defined corridor.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;CoverageJson&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<CoverageJson>> getCorridor(GetCorridor parameters) throws IllegalArgumentException {
        if (parameters.getCollectionId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'collectionId' when calling getCorridor");
        }

        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs("coords", toWkt(parameters.getCoords())));
        queryParams.addAll(toPairs("z", parameters.getZ()));
        queryParams.addAll(toPairs("datetime", parameters.getDatetime()));
        queryParams.addAll(toPairs("csv", "parameter-name", parameters.getParameterName()));
        queryParams.addAll(toPairs("corridor-width", parameters.getCorridorWidth()));
        queryParams.addAll(toPairs("width-units", parameters.getWidthUnits()));
        queryParams.addAll(toPairs("corridor-height", parameters.getCorridorHeight()));
        queryParams.addAll(toPairs("height-units", parameters.getHeightUnits()));
        queryParams.addAll(toPairs("resolution-x", parameters.getResolutionX()));
        queryParams.addAll(toPairs("resolution-y", parameters.getResolutionY()));
        queryParams.addAll(toPairs("crs", parameters.getCrs()));
        queryParams.addAll(toPairs("f", parameters.getF()));

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(basePath(parameters.getCollectionId(), parameters.getInstanceId()) + "/corridor", queryParams));
        request.header("Accept", ACCEPT_COVERAGE);
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, CoverageJson.class);
    }

    /**
     * Retrieve the list of locations available for a collection or instance.
     *
     * <p>Unlike the other EDR queries, {@code locations} is only defined at collection or
     * instance scope, per the specification.</p>
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;GeoJSONFeatureCollection&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<GeoJSONFeatureCollection>> getLocations(GetLocations parameters) throws IllegalArgumentException {
        if (parameters.getCollectionId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'collectionId' when calling getLocations");
        }

        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs("csv", "bbox", toList(parameters.getBbox())));
        queryParams.addAll(toPairs("datetime", parameters.getDatetime()));
        queryParams.addAll(toPairs("limit", parameters.getLimit()));
        queryParams.addAll(toPairs("f", parameters.getF()));

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(basePath(parameters.getCollectionId(), parameters.getInstanceId()) + "/locations", queryParams));
        request.header("Accept", ACCEPT_FEATURES);
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, GeoJSONFeatureCollection.class);
    }

    /**
     * The locations query retrieves data for a specific named location.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;CoverageJson&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<CoverageJson>> getLocation(GetLocation parameters) throws IllegalArgumentException {
        if (parameters.getCollectionId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'collectionId' when calling getLocation");
        }
        if (parameters.getLocationId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'locationId' when calling getLocation");
        }

        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs("datetime", parameters.getDatetime()));
        queryParams.addAll(toPairs("csv", "parameter-name", parameters.getParameterName()));
        queryParams.addAll(toPairs("crs", parameters.getCrs()));
        queryParams.addAll(toPairs("limit", parameters.getLimit()));
        queryParams.addAll(toPairs("f", parameters.getF()));

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(basePath(parameters.getCollectionId(), parameters.getInstanceId()) + "/locations/" + urlEncode(parameters.getLocationId()), queryParams));
        request.header("Accept", ACCEPT_COVERAGE);
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, CoverageJson.class);
    }

    /**
     * Retrieve the list of items of a collection.
     *
     * <p>Unlike the other EDR queries, {@code items} is only defined at collection scope: the
     * specification does not expose an instance-scoped variant.</p>
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;GeoJSONFeatureCollection&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<GeoJSONFeatureCollection>> getItems(GetItems parameters) throws IllegalArgumentException {
        if (parameters.getCollectionId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'collectionId' when calling getItems");
        }

        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs("csv", "bbox", toList(parameters.getBbox())));
        queryParams.addAll(toPairs("datetime", parameters.getDatetime()));
        queryParams.addAll(toPairs("limit", parameters.getLimit()));
        queryParams.addAll(toPairs("f", parameters.getF()));

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/collections/" + urlEncode(parameters.getCollectionId()) + "/items", queryParams));
        request.header("Accept", ACCEPT_FEATURES);
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, GeoJSONFeatureCollection.class);
    }

    /**
     * Retrieve a single item of a collection.
     *
     * <p>Unlike the other EDR queries, {@code items} is only defined at collection scope: the
     * specification does not expose an instance-scoped variant.</p>
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;GeoJSONFeature&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<GeoJSONFeature>> getItem(GetItem parameters) throws IllegalArgumentException {
        if (parameters.getCollectionId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'collectionId' when calling getItem");
        }
        if (parameters.getItemId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'itemId' when calling getItem");
        }

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/collections/" + urlEncode(parameters.getCollectionId()) + "/items/" + urlEncode(parameters.getItemId()), toPairs("f", parameters.getF())));
        request.header("Accept", ACCEPT_FEATURES);
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, GeoJSONFeature.class);
    }

    private static String basePath(String collectionId, String instanceId) {
        final StringBuilder pathBuilder = new StringBuilder("/collections/").append(urlEncode(collectionId));
        if (instanceId != null) {
            pathBuilder.append("/instances/").append(urlEncode(instanceId));
        }
        return pathBuilder.toString();
    }

    private static String toWkt(Geometry geometry) {
        if (geometry == null) return null;
        return new WKTWriter().write(geometry);
    }

    private static List toList(Envelope env) {
        if (env == null) return null;
        final List lst = new ArrayList();
        lst.addAll(Arrays.stream(env.getLowerCorner().getCoordinates()).boxed().toList());
        lst.addAll(Arrays.stream(env.getUpperCorner().getCoordinates()).boxed().toList());
        return lst;
    }

}
