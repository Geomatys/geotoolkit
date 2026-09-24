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
import org.geotoolkit.ogcapi.request.edr.GetLocation;
import org.geotoolkit.ogcapi.request.edr.GetLocations;
import org.geotoolkit.ogcapi.request.edr.GetPosition;
import org.geotoolkit.ogcapi.request.edr.GetRadius;
import org.geotoolkit.ogcapi.request.edr.GetTrajectory;
import org.geotoolkit.ogcapi.request.feature.GetItem;
import org.geotoolkit.ogcapi.request.feature.GetItems;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKTWriter;
import org.opengis.geometry.Envelope;

/**
 * OGC API - Environmental Data Retrieval (EDR) Standard, Part 1: Core.
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class EdrApi extends AbstractOpenApi {

    /**
     * Parameter names.
     */
    public static final String PARAM_BBOX            = "bbox";
    public static final String PARAM_COORDS          = "coords";
    public static final String PARAM_CORRIDOR_HEIGHT = "corridor-height";
    public static final String PARAM_CORRIDOR_WIDTH  = "corridor-width";
    public static final String PARAM_CRS             = "crs";
    public static final String PARAM_DATETIME        = "datetime";
    public static final String PARAM_F               = "f";
    public static final String PARAM_HEIGHT_UNITS    = "height-units";
    public static final String PARAM_LIMIT           = "limit";
    public static final String PARAM_PARAMETER_NAME  = "parameter-name";
    public static final String PARAM_RESOLUTION_X    = "resolution-x";
    public static final String PARAM_RESOLUTION_Y    = "resolution-y";
    public static final String PARAM_RESOLUTION_Z    = "resolution-z";
    public static final String PARAM_WIDTH_UNITS     = "width-units";
    public static final String PARAM_WITHIN          = "within";
    public static final String PARAM_WITHIN_UNITS    = "within-units";
    public static final String PARAM_Z               = "z";

    /**
     * Path fragments.
     */
    public static final String PATH_AREA        = "/area";
    public static final String PATH_COLLECTIONS = "/collections";
    public static final String PATH_CORRIDOR    = "/corridor";
    public static final String PATH_CUBE        = "/cube";
    public static final String PATH_INSTANCES   = "/instances";
    public static final String PATH_ITEMS       = "/items";
    public static final String PATH_LOCATIONS   = "/locations";
    public static final String PATH_POSITION    = "/position";
    public static final String PATH_RADIUS      = "/radius";
    public static final String PATH_TRAJECTORY  = "/trajectory";

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
        request.uri(toUri(PATH_COLLECTIONS + '/' + urlEncode(parameters.getCollectionId()), toPairs(PARAM_F, parameters.getFormat())));
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
        request.uri(toUri(PATH_COLLECTIONS + '/' + urlEncode(parameters.getCollectionId()) + PATH_INSTANCES, toPairs(PARAM_F, parameters.getF())));
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
        queryParams.addAll(toPairs(PARAM_COORDS, toWkt(parameters.getCoords())));
        queryParams.addAll(toPairs(PARAM_Z, parameters.getZ()));
        queryParams.addAll(toPairs(PARAM_DATETIME, parameters.getDatetime()));
        queryParams.addAll(toPairs("csv", PARAM_PARAMETER_NAME, parameters.getParameterName()));
        queryParams.addAll(toPairs(PARAM_CRS, parameters.getCrs()));
        queryParams.addAll(toPairs(PARAM_LIMIT, parameters.getLimit()));
        queryParams.addAll(toPairs(PARAM_F, parameters.getF()));

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(basePath(parameters.getCollectionId(), parameters.getInstanceId()) + PATH_POSITION, queryParams));
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
        queryParams.addAll(toPairs(PARAM_COORDS, toWkt(parameters.getCoords())));
        queryParams.addAll(toPairs(PARAM_WITHIN, parameters.getWithin()));
        queryParams.addAll(toPairs(PARAM_WITHIN_UNITS, parameters.getWithinUnits()));
        queryParams.addAll(toPairs(PARAM_Z, parameters.getZ()));
        queryParams.addAll(toPairs(PARAM_DATETIME, parameters.getDatetime()));
        queryParams.addAll(toPairs("csv", PARAM_PARAMETER_NAME, parameters.getParameterName()));
        queryParams.addAll(toPairs(PARAM_CRS, parameters.getCrs()));
        queryParams.addAll(toPairs(PARAM_LIMIT, parameters.getLimit()));
        queryParams.addAll(toPairs(PARAM_F, parameters.getF()));

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(basePath(parameters.getCollectionId(), parameters.getInstanceId()) + PATH_RADIUS, queryParams));
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
        queryParams.addAll(toPairs(PARAM_COORDS, toWkt(parameters.getCoords())));
        queryParams.addAll(toPairs(PARAM_Z, parameters.getZ()));
        queryParams.addAll(toPairs(PARAM_DATETIME, parameters.getDatetime()));
        queryParams.addAll(toPairs("csv", PARAM_PARAMETER_NAME, parameters.getParameterName()));
        queryParams.addAll(toPairs(PARAM_CRS, parameters.getCrs()));
        queryParams.addAll(toPairs(PARAM_RESOLUTION_X, parameters.getResolutionX()));
        queryParams.addAll(toPairs(PARAM_RESOLUTION_Y, parameters.getResolutionY()));
        queryParams.addAll(toPairs(PARAM_LIMIT, parameters.getLimit()));
        queryParams.addAll(toPairs(PARAM_F, parameters.getF()));

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(basePath(parameters.getCollectionId(), parameters.getInstanceId()) + PATH_AREA, queryParams));
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
        queryParams.addAll(toPairs("csv", PARAM_BBOX, toList(parameters.getBbox())));
        queryParams.addAll(toPairs(PARAM_Z, parameters.getZ()));
        queryParams.addAll(toPairs(PARAM_DATETIME, parameters.getDatetime()));
        queryParams.addAll(toPairs("csv", PARAM_PARAMETER_NAME, parameters.getParameterName()));
        queryParams.addAll(toPairs(PARAM_RESOLUTION_X, parameters.getResolutionX()));
        queryParams.addAll(toPairs(PARAM_RESOLUTION_Y, parameters.getResolutionY()));
        queryParams.addAll(toPairs(PARAM_RESOLUTION_Z, parameters.getResolutionZ()));
        queryParams.addAll(toPairs(PARAM_CRS, parameters.getCrs()));
        queryParams.addAll(toPairs(PARAM_F, parameters.getF()));

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(basePath(parameters.getCollectionId(), parameters.getInstanceId()) + PATH_CUBE, queryParams));
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
        queryParams.addAll(toPairs(PARAM_COORDS, toWkt(parameters.getCoords())));
        queryParams.addAll(toPairs(PARAM_Z, parameters.getZ()));
        queryParams.addAll(toPairs(PARAM_DATETIME, parameters.getDatetime()));
        queryParams.addAll(toPairs("csv", PARAM_PARAMETER_NAME, parameters.getParameterName()));
        queryParams.addAll(toPairs(PARAM_CRS, parameters.getCrs()));
        queryParams.addAll(toPairs(PARAM_F, parameters.getF()));

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(basePath(parameters.getCollectionId(), parameters.getInstanceId()) + PATH_TRAJECTORY, queryParams));
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
        queryParams.addAll(toPairs(PARAM_COORDS, toWkt(parameters.getCoords())));
        queryParams.addAll(toPairs(PARAM_Z, parameters.getZ()));
        queryParams.addAll(toPairs(PARAM_DATETIME, parameters.getDatetime()));
        queryParams.addAll(toPairs("csv", PARAM_PARAMETER_NAME, parameters.getParameterName()));
        queryParams.addAll(toPairs(PARAM_CORRIDOR_WIDTH, parameters.getCorridorWidth()));
        queryParams.addAll(toPairs(PARAM_WIDTH_UNITS, parameters.getWidthUnits()));
        queryParams.addAll(toPairs(PARAM_CORRIDOR_HEIGHT, parameters.getCorridorHeight()));
        queryParams.addAll(toPairs(PARAM_HEIGHT_UNITS, parameters.getHeightUnits()));
        queryParams.addAll(toPairs(PARAM_RESOLUTION_X, parameters.getResolutionX()));
        queryParams.addAll(toPairs(PARAM_RESOLUTION_Y, parameters.getResolutionY()));
        queryParams.addAll(toPairs(PARAM_CRS, parameters.getCrs()));
        queryParams.addAll(toPairs(PARAM_F, parameters.getF()));

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(basePath(parameters.getCollectionId(), parameters.getInstanceId()) + PATH_CORRIDOR, queryParams));
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
        queryParams.addAll(toPairs("csv", PARAM_BBOX, toList(parameters.getBbox())));
        queryParams.addAll(toPairs(PARAM_DATETIME, parameters.getDatetime()));
        queryParams.addAll(toPairs(PARAM_LIMIT, parameters.getLimit()));
        queryParams.addAll(toPairs(PARAM_F, parameters.getF()));

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(basePath(parameters.getCollectionId(), parameters.getInstanceId()) + PATH_LOCATIONS, queryParams));
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
        queryParams.addAll(toPairs(PARAM_DATETIME, parameters.getDatetime()));
        queryParams.addAll(toPairs("csv", PARAM_PARAMETER_NAME, parameters.getParameterName()));
        queryParams.addAll(toPairs(PARAM_CRS, parameters.getCrs()));
        queryParams.addAll(toPairs(PARAM_LIMIT, parameters.getLimit()));
        queryParams.addAll(toPairs(PARAM_F, parameters.getF()));

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(basePath(parameters.getCollectionId(), parameters.getInstanceId()) + PATH_LOCATIONS + '/' + urlEncode(parameters.getLocationId()), queryParams));
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
        queryParams.addAll(toPairs("csv", PARAM_BBOX, toList(parameters.getBbox())));
        queryParams.addAll(toPairs(PARAM_DATETIME, parameters.getDatetime()));
        queryParams.addAll(toPairs(PARAM_LIMIT, parameters.getLimit()));
        queryParams.addAll(toPairs(PARAM_F, parameters.getF()));

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_COLLECTIONS + '/' + urlEncode(parameters.getCollectionId()) + PATH_ITEMS, queryParams));
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
        request.uri(toUri(PATH_COLLECTIONS + '/' + urlEncode(parameters.getCollectionId()) + PATH_ITEMS + '/' + urlEncode(parameters.getItemId()), toPairs(PARAM_F, parameters.getF())));
        request.header("Accept", ACCEPT_FEATURES);
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, GeoJSONFeature.class);
    }

    private static String basePath(String collectionId, String instanceId) {
        final StringBuilder pathBuilder = new StringBuilder(PATH_COLLECTIONS + '/').append(urlEncode(collectionId));
        if (instanceId != null) {
            pathBuilder.append(PATH_INSTANCES + '/').append(urlEncode(instanceId));
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
