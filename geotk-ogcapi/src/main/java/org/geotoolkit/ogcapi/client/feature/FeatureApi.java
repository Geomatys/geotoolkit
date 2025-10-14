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
import org.geotoolkit.client.openapi.AbstractOpenApi;
import org.geotoolkit.client.openapi.OpenApiConfiguration;
import org.geotoolkit.client.service.ServiceException;
import org.geotoolkit.client.service.ServiceResponse;
import org.geotoolkit.ogcapi.model.feature.Functions;
import org.geotoolkit.ogcapi.model.jsonschema.JSONSchema;

public final class FeatureApi extends AbstractOpenApi {

    public FeatureApi(OpenApiConfiguration config) {
        super(config);
    }

    /**
     * Get the data schema.
     *
     * Features API Part 5.
     *
     * @return JSONSchema
     * @throws ServiceException if fails to make API call
     */
    public ServiceResponse<JSONSchema> collectinGetJsonSchema(
            @jakarta.annotation.Nonnull String collectionId,
            @jakarta.annotation.Nullable String f)
            throws ServiceException {
        if (collectionId == null) {
            throw new ServiceException(400, "Missing the required parameter 'collectionId' when calling collectionGetDGGRSList");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/collections/" + urlEncode(collectionId) + "/schema", toPairs("f", f)));
        request.header("Accept", "application/json");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return toSimpleResponse(send(request), JSONSchema.class);
    }

    /**
     * Get the data schema.
     *
     * Features API Part 5.
     *
     * @return JSONSchema
     * @throws ServiceException if fails to make API call
     */
    public ServiceResponse<JSONSchema> getJsonSchema() throws ServiceException {
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/schema", null));
        request.header("Accept", "application/json");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return toSimpleResponse(send(request), JSONSchema.class);
    }

    /**
     * Get the list of supported functions This operation returns the list of custom functions supported in CQL2
     * expressions.
     *
     * Features API Part 3.
     *
     * @return Functions
     * @throws ServiceException if fails to make API call
     */
    public ServiceResponse<Functions> getFunctions() throws ServiceException {
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/functions", null));
        request.header("Accept", "application/json");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return toSimpleResponse(send(request), Functions.class);
    }

    /**
     * Get the list of supported queryables for a collection This operation returns the list of supported queryables of
     * a collection. Queryables are the properties that may be used to construct a filter expression on items in the
     * collection. The response is a JSON Schema of a object where each property is a queryable.
     *
     * Features API Part 3.
     *
     * @param collectionId local identifier of a collection (required)
     * @return JSONSchema
     * @throws ServiceException if fails to make API call
     */
    public ServiceResponse<JSONSchema> getQueryables(@jakarta.annotation.Nonnull String collectionId) throws ServiceException {
        if (collectionId == null) {
            throw new ServiceException(400, "Missing the required parameter 'collectionId' when calling getQueryables");
        }

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/collections/" + urlEncode(collectionId) + "/queryables", null));
        request.header("Accept", "application/schema+json");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return toSimpleResponse(send(request), JSONSchema.class);
    }

}
