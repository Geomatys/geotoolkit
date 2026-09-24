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
package org.geotoolkit.ogcapi.client.joins;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import org.geotoolkit.client.openapi.AbstractOpenApi;
import org.geotoolkit.client.openapi.OpenApiConfiguration;
import org.geotoolkit.client.service.Pair;
import org.geotoolkit.client.service.ServiceRequest;
import org.geotoolkit.ogcapi.dto.geojson.GeoJSONFeatureCollection;
import org.geotoolkit.ogcapi.dto.joins.CollectionKeys;
import org.geotoolkit.ogcapi.dto.joins.JoinSource;
import org.geotoolkit.ogcapi.dto.joins.JoinSources;
import org.geotoolkit.ogcapi.request.joins.CreateJoinSource;
import org.geotoolkit.ogcapi.request.joins.DeleteJoinSource;
import org.geotoolkit.ogcapi.request.joins.GetCollectionKeys;
import org.geotoolkit.ogcapi.request.joins.GetJoinSource;
import org.geotoolkit.ogcapi.request.joins.GetJoinSourceList;
import org.geotoolkit.ogcapi.request.joins.JoinFiles;

/**
 * OGC API — Joins — Part 1: Core.
 *
 * <p>
 * Discovery of collections and of collection key fields, plus creation, listing, retrieval and
 * deletion of join sources (spec sections 8 and 9). Also supports the standalone
 * file-to-file joining operation (spec section 10).
 * </p>
 *
 * @see https://docs.ogc.org/DRAFTS/22-026.html
 * @author Johann Sorel (Geomatys)
 */
public final class JoinsApi extends AbstractOpenApi {

    /**
     * Parameter names.
     */
    public static final String PARAM_DATETIME = "datetime";
    public static final String PARAM_F        = "f";
    public static final String PARAM_LIMIT    = "limit";

    /**
     * Path fragments.
     */
    public static final String PATH_COLLECTIONS = "/collections";
    public static final String PATH_FILEJOIN    = "/filejoin";
    public static final String PATH_JOINS       = "/joins";
    public static final String PATH_KEYS        = "/keys";

    public JoinsApi(OpenApiConfiguration config) {
        super(config);
    }

    /**
     * Retrieve the key fields of a collection that can be used in data joins.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;CollectionKeys&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<CollectionKeys>> getCollectionKeys(GetCollectionKeys parameters) throws IllegalArgumentException {
        if (parameters.getCollectionId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'collectionId' when calling getCollectionKeys");
        }

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_COLLECTIONS + '/' + urlEncode(parameters.getCollectionId()) + PATH_KEYS, toPairs(PARAM_F, parameters.getFormat())));
        request.header("Accept", "application/json, text/html");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);

        return requestAsJson(request, CollectionKeys.class);
    }

    /**
     * Retrieve the list of join sources available on the server for a specific collection.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;JoinSources&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<JoinSources>> getJoinSources(GetJoinSourceList parameters) throws IllegalArgumentException {
        if (parameters.getCollectionId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'collectionId' when calling getJoinSources");
        }

        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs(PARAM_LIMIT, parameters.getLimit()));
        queryParams.addAll(toPairs(PARAM_DATETIME, parameters.getDatetime()));
        queryParams.addAll(toPairs(PARAM_F, parameters.getFormat()));

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_COLLECTIONS + '/' + urlEncode(parameters.getCollectionId()) + PATH_JOINS, queryParams));
        request.header("Accept", "application/json, text/html");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);

        return requestAsJson(request, JoinSources.class);
    }

    /**
     * Create a new join source by saving tabular data from an input data file to the server, sent
     * as a {@code multipart/form-data} request (spec section 8.6.1, Table 5).
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;JoinSource&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<JoinSource>> createJoinSource(CreateJoinSource parameters) throws IllegalArgumentException {
        if (parameters.getCollectionId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'collectionId' when calling createJoinSource");
        }
        if (parameters.getRightDatasetFormat() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'rightDatasetFormat' when calling createJoinSource");
        }
        if (parameters.getRightDatasetFile() == null && parameters.getRightDatasetUrl() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'rightDatasetFile' or 'rightDatasetUrl' when calling createJoinSource");
        }
        if (parameters.getRightDatasetKey() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'rightDatasetKey' when calling createJoinSource");
        }
        if (parameters.getRightDatasetDataValueList() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'rightDatasetDataValueList' when calling createJoinSource");
        }

        final MultipartFormDataBody body = new MultipartFormDataBody();
        body.addField("collection-key", parameters.getCollectionKey());
        body.addField("right-dataset-format", parameters.getRightDatasetFormat());
        body.addFile("right-dataset-file", parameters.getRightDatasetFile());
        body.addField("right-dataset-url", parameters.getRightDatasetUrl());
        body.addField("right-dataset-key", parameters.getRightDatasetKey());
        body.addField("right-dataset-data-value-list", String.join(",", parameters.getRightDatasetDataValueList()));
        body.addField("csv-file-delimiter", parameters.getCsvFileDelimiter());
        body.addField("csv-file-header-row-number", parameters.getCsvFileHeaderRowNumber());
        body.addField("csv-file-data-start-row-number", parameters.getCsvFileDataStartRowNumber());

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_COLLECTIONS + '/' + urlEncode(parameters.getCollectionId()) + PATH_JOINS, null));
        request.header("Content-Type", body.getContentType());
        request.header("Accept", "application/json, text/html");
        request.method("POST", HttpRequest.BodyPublishers.ofByteArray(body.build()));
        setConfig(request);

        return requestAsJson(request, JoinSource.class);
    }

    /**
     * Retrieve metadata on a specific join source.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;JoinSource&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<JoinSource>> getJoinSource(GetJoinSource parameters) throws IllegalArgumentException {
        if (parameters.getCollectionId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'collectionId' when calling getJoinSource");
        }
        if (parameters.getJoinId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'joinId' when calling getJoinSource");
        }

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_COLLECTIONS + '/' + urlEncode(parameters.getCollectionId()) + PATH_JOINS + '/' + urlEncode(parameters.getJoinId()), toPairs(PARAM_F, parameters.getFormat())));
        request.header("Accept", "application/json, text/html");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);

        return requestAsJson(request, JoinSource.class);
    }

    /**
     * Delete a specific join source. Requires the optional "Join Source Delete" requirements
     * class.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Void&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Void> deleteJoinSource(DeleteJoinSource parameters) throws IllegalArgumentException {
        if (parameters.getCollectionId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'collectionId' when calling deleteJoinSource");
        }
        if (parameters.getJoinId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'joinId' when calling deleteJoinSource");
        }

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_COLLECTIONS + '/' + urlEncode(parameters.getCollectionId()) + PATH_JOINS + '/' + urlEncode(parameters.getJoinId()), null));
        request.method("DELETE", HttpRequest.BodyPublishers.noBody());
        setConfig(request);

        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    /**
     * Join data from an input data file directly with another input data file, sent as a
     * {@code multipart/form-data} request (spec section 10.2.1, Table 6). The result is the left
     * dataset with the joined attributes from the right dataset, encoded as GeoJSON.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;GeoJSONFeatureCollection&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<GeoJSONFeatureCollection>> joinFiles(JoinFiles parameters) throws IllegalArgumentException {
        if (parameters.getLeftDatasetFormat() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'leftDatasetFormat' when calling joinFiles");
        }
        if (parameters.getLeftDatasetFile() == null && parameters.getLeftDatasetUrl() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'leftDatasetFile' or 'leftDatasetUrl' when calling joinFiles");
        }
        if (parameters.getLeftDatasetKey() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'leftDatasetKey' when calling joinFiles");
        }
        if (parameters.getRightDatasetFormat() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'rightDatasetFormat' when calling joinFiles");
        }
        if (parameters.getRightDatasetFile() == null && parameters.getRightDatasetUrl() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'rightDatasetFile' or 'rightDatasetUrl' when calling joinFiles");
        }
        if (parameters.getRightDatasetKey() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'rightDatasetKey' when calling joinFiles");
        }
        if (parameters.getRightDatasetDataValueList() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'rightDatasetDataValueList' when calling joinFiles");
        }

        final MultipartFormDataBody body = new MultipartFormDataBody();
        body.addField("left-dataset-format", parameters.getLeftDatasetFormat());
        body.addFile("left-dataset-file", parameters.getLeftDatasetFile());
        body.addField("left-dataset-url", parameters.getLeftDatasetUrl());
        body.addField("left-dataset-key", parameters.getLeftDatasetKey());
        body.addField("right-dataset-format", parameters.getRightDatasetFormat());
        body.addFile("right-dataset-file", parameters.getRightDatasetFile());
        body.addField("right-dataset-url", parameters.getRightDatasetUrl());
        body.addField("right-dataset-key", parameters.getRightDatasetKey());
        body.addField("right-dataset-data-value-list", String.join(",", parameters.getRightDatasetDataValueList()));
        body.addField("csv-file-delimiter", parameters.getCsvFileDelimiter());
        body.addField("csv-file-header-row-number", parameters.getCsvFileHeaderRowNumber());
        body.addField("csv-file-data-start-row-number", parameters.getCsvFileDataStartRowNumber());

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_FILEJOIN, null));
        request.header("Content-Type", body.getContentType());
        request.header("Accept", "application/geo+json");
        request.method("POST", HttpRequest.BodyPublishers.ofByteArray(body.build()));
        setConfig(request);

        return requestAsJson(request, GeoJSONFeatureCollection.class);
    }

}
