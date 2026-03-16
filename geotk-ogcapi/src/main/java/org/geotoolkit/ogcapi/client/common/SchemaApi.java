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

import org.geotoolkit.client.openapi.AbstractOpenApi;
import org.geotoolkit.client.openapi.OpenApiConfiguration;
import org.geotoolkit.client.service.Pair;
import org.geotoolkit.client.service.ServiceException;
import org.geotoolkit.client.service.ServiceResponse;
import org.geotoolkit.ogcapi.model.common.CollectionDescription;
import org.geotoolkit.ogcapi.model.common.Collections;
import org.geotoolkit.ogcapi.model.common.Schema;
import org.geotoolkit.ogcapi.model.jsonschema.JSONSchema;
import org.geotoolkit.ogcapi.request.common.GetCollection;
import org.geotoolkit.ogcapi.request.common.GetCollectionList;
import org.geotoolkit.ogcapi.request.common.GetCollectionQueryables;
import org.geotoolkit.ogcapi.request.common.GetCollectionSchema;
import org.geotoolkit.ogcapi.request.common.GetCollectionSortables;
import org.opengis.geometry.Envelope;

import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class SchemaApi extends AbstractOpenApi {


    public SchemaApi(OpenApiConfiguration config) {
        super(config);
    }

    /**
     * Retrieve the schema of a description available from this service.
     *
     * @param parameters request parameters
     * @return ServiceResponse&lt;Schema&gt;
     * @throws ServiceException if fails to make API call
     */
    public ServiceResponse<Schema> getCollectionSchema(GetCollectionSchema parameters) throws ServiceException {
        if (parameters.getCollectionId() == null) {
            throw new ServiceException(400, "Missing the required parameter 'collectionId' when calling getCollectionSchema");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/collections/" + urlEncode(parameters.getCollectionId()) + "/schema", toPairs("f", parameters.getFormat())));
        request.header("Accept", "application/json");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return toSimpleResponse(send(request), Schema.class);
    }

    /**
     * Retrieve the queryables of a collection available from this service.
     *
     * @param parameters request parameters
     * @return ServiceResponse&lt;Queryables&gt;
     * @throws ServiceException if fails to make API call
     */
    public ServiceResponse<Schema> getCollectionQueryables(GetCollectionQueryables parameters) throws ServiceException {
        if (parameters.getCollectionId() == null) {
            throw new ServiceException(400, "Missing the required parameter 'collectionId' when calling getCollectionQueryables");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/collections/" + urlEncode(parameters.getCollectionId()) + "queryables", toPairs("f", parameters.getFormat())));
        request.header("Accept", "application/json");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return toSimpleResponse(send(request), Schema.class);
    }

    /**
     * Retrieve the sortables of a collection available from this service.
     *
     * @param parameters request parameters
     * @return ServiceResponse&lt;Sortables&gt;
     * @throws ServiceException if fails to make API call
     */
    public ServiceResponse<Schema> getCollectionSortables(GetCollectionSortables parameters) throws ServiceException {
        if (parameters.getCollectionId() == null) {
            throw new ServiceException(400, "Missing the required parameter 'collectionId' when calling getCollectionSortables");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/collections/" + urlEncode(parameters.getCollectionId()) + "sortables", toPairs("f", parameters.getFormat())));
        request.header("Accept", "application/json");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return toSimpleResponse(send(request), Schema.class);
    }
}
