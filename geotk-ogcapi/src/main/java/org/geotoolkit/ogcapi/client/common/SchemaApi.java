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
import java.util.function.Supplier;
import org.geotoolkit.client.openapi.AbstractOpenApi;
import org.geotoolkit.client.openapi.OpenApiConfiguration;
import org.geotoolkit.client.service.ServiceRequest;
import org.geotoolkit.ogcapi.dto.common.Schema;
import org.geotoolkit.ogcapi.request.common.GetCollectionQueryables;
import org.geotoolkit.ogcapi.request.common.GetCollectionSchema;
import org.geotoolkit.ogcapi.request.common.GetCollectionSortables;

/**
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class SchemaApi extends AbstractOpenApi {

    /**
     * Parameter names.
     */
    public static final String PARAM_F = "f";

    /**
     * Path fragments.
     */
    public static final String PATH_COLLECTIONS = "/collections";
    public static final String PATH_QUERYABLES  = "/queryables";
    public static final String PATH_SCHEMA      = "/schema";
    public static final String PATH_SORTABLES   = "/sortables";

    public SchemaApi(OpenApiConfiguration config) {
        super(config);
    }

    /**
     * Retrieve the schema of a collection, or the dataset-level schema when
     * {@code parameters.getCollectionId()} is {@code null}.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Schema&gt;
     */
    public ServiceRequest<Supplier<Schema>> getSchema(GetCollectionSchema parameters) {
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        final String path = parameters.getCollectionId() == null
                ? PATH_SCHEMA
                : PATH_COLLECTIONS + '/' + urlEncode(parameters.getCollectionId()) + PATH_SCHEMA;
        request.uri(toUri(path, toPairs(PARAM_F, parameters.getFormat())));
        request.header("Accept", "application/json");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, Schema.class);
    }

    /**
     * Retrieve the queryables of a collection available from this service.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Queryables&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<Schema>> getCollectionQueryables(GetCollectionQueryables parameters) throws IllegalArgumentException {
        if (parameters.getCollectionId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'collectionId' when calling getCollectionQueryables");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_COLLECTIONS + '/' + urlEncode(parameters.getCollectionId()) + PATH_QUERYABLES, toPairs(PARAM_F, parameters.getFormat())));
        request.header("Accept", "application/json");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, Schema.class);
    }

    /**
     * Retrieve the sortables of a collection available from this service.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Sortables&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<Schema>> getCollectionSortables(GetCollectionSortables parameters) throws IllegalArgumentException {
        if (parameters.getCollectionId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'collectionId' when calling getCollectionSortables");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_COLLECTIONS + '/' + urlEncode(parameters.getCollectionId()) + PATH_SORTABLES, toPairs(PARAM_F, parameters.getFormat())));
        request.header("Accept", "application/json");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, Schema.class);
    }
}
