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
package org.geotoolkit.openeo.client;

import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import org.geotoolkit.client.openapi.AbstractOpenApi;
import org.geotoolkit.client.openapi.OpenApiConfiguration;
import org.geotoolkit.client.service.Pair;
import org.geotoolkit.client.service.ServiceRequest;
import org.geotoolkit.ogcapi.dto.common.Collections;
import org.geotoolkit.ogcapi.dto.common.Schema;
import org.geotoolkit.ogcapi.request.common.GetCollection;
import org.geotoolkit.ogcapi.request.common.GetCollectionList;
import org.geotoolkit.ogcapi.request.common.GetCollectionQueryables;
import org.geotoolkit.openeo.dto.collection.Collection;
import org.opengis.geometry.Envelope;

/**
 * OpenEO Collections API client (STAC-based).
 * Covers: listing and describing EO data collections.
 *
 * <p>TODO: delegate to StacClient when available instead of making direct HTTP calls.</p>
 *
 * @author Quentin BIALOTA (Geomatys)
 * Based on : <a href="https://api.openeo.org/#tag/EO-Data-Discovery">OpenEO Doc</a>
 */
public final class CollectionsApi extends AbstractOpenApi {

    public CollectionsApi(OpenApiConfiguration config) {
        super(config);
    }

    /**
     * List available collections. Returns the OGC API - Common collection summaries;
     * use {@link #getCollection(GetCollection)} for the full STAC {@link Collection} record.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Supplier&lt;Collections&gt;&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<Collections>> getCollections(GetCollectionList parameters) throws IllegalArgumentException {
        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs("limit", parameters.getLimit()));
        queryParams.addAll(toPairs("csv", "bbox", toList(parameters.getBbox())));
        queryParams.addAll(toPairs("datetime", parameters.getDatetime()));
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/collections", queryParams));
        request.header("Accept", "application/json");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, Collections.class);
    }

    /**
     * Describe a specific collection.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Supplier&lt;Collection&gt;&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<Collection>> getCollection(GetCollection parameters) throws IllegalArgumentException {
        if (parameters.getCollectionId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'collectionId' when calling getCollection");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/collections/" + urlEncode(parameters.getCollectionId()), List.of()));
        request.header("Accept", "application/json");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, Collection.class);
    }

    /**
     * Lists the properties of a collection that can be used in filter expressions (OGC API - Features queryables).
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Supplier&lt;Schema&gt;&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<Schema>> getQueryables(GetCollectionQueryables parameters) throws IllegalArgumentException {
        if (parameters.getCollectionId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'collectionId' when calling getQueryables");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/collections/" + urlEncode(parameters.getCollectionId()) + "/queryables", List.of()));
        request.header("Accept", "application/schema+json");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, Schema.class);
    }

    // duplicated from ogcapi.client.common.CollectionsApi (private there); fixes its
    // lower/upper corner order to match the OGC API - Features bbox convention (min then max).
    private static List toList(Envelope env) {
        if (env == null) return null;
        final List lst = new ArrayList();
        lst.addAll(Arrays.stream(env.getLowerCorner().getCoordinates()).boxed().toList());
        lst.addAll(Arrays.stream(env.getUpperCorner().getCoordinates()).boxed().toList());
        return lst;
    }
}
