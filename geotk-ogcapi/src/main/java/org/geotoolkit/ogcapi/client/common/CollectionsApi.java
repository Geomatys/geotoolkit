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
package org.geotoolkit.ogcapi.client.common;

import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import org.geotoolkit.client.openapi.AbstractOpenApi;
import org.geotoolkit.client.openapi.OpenApiConfiguration;
import org.geotoolkit.client.service.Pair;
import org.geotoolkit.client.service.ServiceRequest;
import org.geotoolkit.ogcapi.dto.common.CollectionDescription;
import org.geotoolkit.ogcapi.dto.common.Collections;
import org.geotoolkit.ogcapi.request.common.GetCollection;
import org.geotoolkit.ogcapi.request.common.GetCollectionList;
import org.opengis.geometry.Envelope;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class CollectionsApi extends AbstractOpenApi {


    public CollectionsApi(OpenApiConfiguration config) {
        super(config);
    }

    /**
     * Retrieve the description of a collection available from this service.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;CollectionDesc&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<CollectionDescription>> getCollection(GetCollection parameters) throws IllegalArgumentException {
        if (parameters.getCollectionId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'collectionId' when calling getCollection");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/collections/" + urlEncode(parameters.getCollectionId()), toPairs("f", parameters.getFormat())));
        request.header("Accept", "application/json, text/html");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, CollectionDescription.class);
    }

    /**
     * Retrieve the list of geospatial data collections available from this service.
     *
     * @param parameters request parameters
     * @return ApiResponse&lt;Collections&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<Collections>> getCollections(GetCollectionList parameters) throws IllegalArgumentException {
        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs("datetime", parameters.getDatetime()));
        queryParams.addAll(toPairs("csv", "bbox", toList(parameters.getBbox())));
        queryParams.addAll(toPairs("limit", parameters.getLimit()));
        queryParams.addAll(toPairs("f", parameters.getFormat()));
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/collections", queryParams));
        request.header("Accept", "application/json, text/html");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, Collections.class);
    }

    private static List toList(Envelope env) {
        if (env == null) return null;
        final List lst = new ArrayList();
        lst.addAll(Arrays.stream(env.getLowerCorner().getCoordinates()).boxed().toList());
        lst.addAll(Arrays.stream(env.getUpperCorner().getCoordinates()).boxed().toList());
        return lst;
    }
}
