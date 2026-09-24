/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
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
package org.geotoolkit.ogcapi.client.feature;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import org.geotoolkit.client.openapi.AbstractOpenApi;
import org.geotoolkit.client.openapi.OpenApiConfiguration;
import org.geotoolkit.client.service.Pair;
import org.geotoolkit.client.service.ServiceRequest;
import org.geotoolkit.ogcapi.dto.feature.Functions;
import org.geotoolkit.ogcapi.dto.geojson.GeoJSONFeature;
import org.geotoolkit.ogcapi.dto.geojson.GeoJSONFeatureCollection;
import org.geotoolkit.ogcapi.request.feature.CreateItem;
import org.geotoolkit.ogcapi.request.feature.DeleteItem;
import org.geotoolkit.ogcapi.request.feature.GetItem;
import org.geotoolkit.ogcapi.request.feature.GetItems;
import org.geotoolkit.ogcapi.request.feature.ReplaceItem;
import org.geotoolkit.ogcapi.request.feature.UpdateItem;

/**
 * OGC API - Features, Parts 1 (Core), 3 (Filtering) and 4 (Create/Replace/Update/Delete).
 *
 * <p>Dataset/collection-level schema, queryables and sortables (Part 5) are exposed by
 * {@link org.geotoolkit.ogcapi.client.common.SchemaApi}, shared with OGC API - Common Part 3.</p>
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class FeatureApi extends AbstractOpenApi {

    /**
     * Parameter names.
     */
    public static final String PARAM_BBOX        = "bbox";
    public static final String PARAM_CRS         = "crs";
    public static final String PARAM_DATETIME    = "datetime";
    public static final String PARAM_DRY_RUN     = "dry-run";
    public static final String PARAM_F           = "f";
    public static final String PARAM_FILTER      = "filter";
    public static final String PARAM_FILTER_LANG = "filter-lang";
    public static final String PARAM_LIMIT       = "limit";
    public static final String PARAM_OFFSET      = "offset";

    /**
     * Path fragments.
     */
    public static final String PATH_COLLECTIONS = "/collections";
    public static final String PATH_FUNCTIONS   = "/functions";
    public static final String PATH_ITEMS       = "/items";

    private static final String ACCEPT_FEATURES = "application/geo+json, application/json, text/html";

    public FeatureApi(OpenApiConfiguration config) {
        super(config);
    }

    /**
     * Get the list of supported functions. This operation returns the list of custom functions supported in CQL2
     * expressions.
     *
     * Features API Part 3.
     *
     * @return Functions
     */
    public ServiceRequest<Supplier<Functions>> getFunctions() {
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_FUNCTIONS, null));
        request.header("Accept", "application/json");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, Functions.class);
    }

    /**
     * Fetch the features of a collection.
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
        queryParams.addAll(toPairs(PARAM_OFFSET, parameters.getOffset()));
        queryParams.addAll(toPairs(PARAM_CRS, parameters.getCrs()));
        queryParams.addAll(toPairs(PARAM_FILTER, parameters.getFilter()));
        queryParams.addAll(toPairs(PARAM_FILTER_LANG, parameters.getFilterLang()));
        queryParams.addAll(toPairs(PARAM_F, parameters.getF()));
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_COLLECTIONS + '/' + urlEncode(parameters.getCollectionId()) + PATH_ITEMS, queryParams));
        request.header("Accept", ACCEPT_FEATURES);
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, GeoJSONFeatureCollection.class);
    }

    /**
     * Fetch a single feature.
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
        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs(PARAM_CRS, parameters.getCrs()));
        queryParams.addAll(toPairs(PARAM_F, parameters.getF()));
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_COLLECTIONS + '/' + urlEncode(parameters.getCollectionId()) + PATH_ITEMS + '/' + urlEncode(parameters.getItemId()), queryParams));
        request.header("Accept", ACCEPT_FEATURES);
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, GeoJSONFeature.class);
    }

    /**
     * Create a new feature in a collection. On success, the URI of the new feature is returned in
     * the response's {@code Location} header.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Void&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Void> createItem(CreateItem parameters) throws IllegalArgumentException {
        if (parameters.getCollectionId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'collectionId' when calling createItem");
        }
        if (parameters.getContent() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'content' when calling createItem");
        }
        if (parameters.getContentType() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'contentType' when calling createItem");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_COLLECTIONS + '/' + urlEncode(parameters.getCollectionId()) + PATH_ITEMS, toPairs(PARAM_DRY_RUN, parameters.getDryRun())));
        request.header("Content-Type", parameters.getContentType());
        if (parameters.getHandling() != null) {
            request.header("Prefer", parameters.getHandling().headerValue);
        }
        request.method("POST", HttpRequest.BodyPublishers.ofByteArray(parameters.getContent()));
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    /**
     * Replace an existing feature. All properties and the geometry of the feature are replaced.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Void&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Void> replaceItem(ReplaceItem parameters) throws IllegalArgumentException {
        if (parameters.getCollectionId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'collectionId' when calling replaceItem");
        }
        if (parameters.getItemId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'itemId' when calling replaceItem");
        }
        if (parameters.getContent() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'content' when calling replaceItem");
        }
        if (parameters.getContentType() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'contentType' when calling replaceItem");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_COLLECTIONS + '/' + urlEncode(parameters.getCollectionId()) + PATH_ITEMS + '/' + urlEncode(parameters.getItemId()),
                toPairs(PARAM_DRY_RUN, parameters.getDryRun())));
        request.header("Content-Type", parameters.getContentType());
        if (parameters.getHandling() != null) {
            request.header("Prefer", parameters.getHandling().headerValue);
        }
        request.method("PUT", HttpRequest.BodyPublishers.ofByteArray(parameters.getContent()));
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    /**
     * Update an existing feature with a partial document (RFC 7396 JSON Merge Patch).
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Void&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Void> updateItem(UpdateItem parameters) throws IllegalArgumentException {
        if (parameters.getCollectionId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'collectionId' when calling updateItem");
        }
        if (parameters.getItemId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'itemId' when calling updateItem");
        }
        if (parameters.getContent() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'content' when calling updateItem");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_COLLECTIONS + '/' + urlEncode(parameters.getCollectionId()) + PATH_ITEMS + '/' + urlEncode(parameters.getItemId()),
                toPairs(PARAM_DRY_RUN, parameters.getDryRun())));
        request.header("Content-Type", "application/merge-patch+json");
        if (parameters.getHandling() != null) {
            request.header("Prefer", parameters.getHandling().headerValue);
        }
        request.method("PATCH", HttpRequest.BodyPublishers.ofByteArray(parameters.getContent()));
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    /**
     * Delete an existing feature.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Void&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Void> deleteItem(DeleteItem parameters) throws IllegalArgumentException {
        if (parameters.getCollectionId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'collectionId' when calling deleteItem");
        }
        if (parameters.getItemId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'itemId' when calling deleteItem");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_COLLECTIONS + '/' + urlEncode(parameters.getCollectionId()) + PATH_ITEMS + '/' + urlEncode(parameters.getItemId()), null));
        request.method("DELETE", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    private static List<Double> toList(org.opengis.geometry.Envelope env) {
        if (env == null) return null;
        final List<Double> lst = new ArrayList<>();
        for (double d : env.getLowerCorner().getCoordinates()) lst.add(d);
        for (double d : env.getUpperCorner().getCoordinates()) lst.add(d);
        return lst;
    }

}
