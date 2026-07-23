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
package org.geotoolkit.ogcapi.client.records;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import org.geotoolkit.client.openapi.AbstractOpenApi;
import org.geotoolkit.client.openapi.OpenApiConfiguration;
import org.geotoolkit.client.service.Pair;
import org.geotoolkit.client.service.ServiceRequest;
import org.geotoolkit.ogcapi.dto.geojson.GeoJSONFeature;
import org.geotoolkit.ogcapi.dto.geojson.GeoJSONFeatureCollection;
import org.geotoolkit.ogcapi.dto.records.Facets;
import org.geotoolkit.ogcapi.request.records.CreateRecord;
import org.geotoolkit.ogcapi.request.records.DeleteRecord;
import org.geotoolkit.ogcapi.request.records.GetFacets;
import org.geotoolkit.ogcapi.request.records.GetRecord;
import org.geotoolkit.ogcapi.request.records.GetRecords;
import org.geotoolkit.ogcapi.request.records.ReplaceRecord;
import org.geotoolkit.ogcapi.request.records.UpdateRecord;

/**
 * OGC API - Records, Parts 1 (Core), 2 (Facets, draft) and 3 (Create/Replace/Update/Delete/Harvest,
 * draft).
 *
 * <p>Catalog-level schema, queryables and sortables are exposed by
 * {@link org.geotoolkit.ogcapi.client.common.SchemaApi}, shared with OGC API - Common Part 3.</p>
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class RecordsApi extends AbstractOpenApi {

    private static final String ACCEPT_RECORDS = "application/geo+json, application/json, text/html";
    private static final String ACCEPT_FACETS = "application/facets+json, application/json";

    public RecordsApi(OpenApiConfiguration config) {
        super(config);
    }

    /**
     * Fetch the records of a catalog.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;GeoJSONFeatureCollection&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<GeoJSONFeatureCollection>> getRecords(GetRecords parameters) throws IllegalArgumentException {
        if (parameters.getCollectionId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'collectionId' when calling getRecords");
        }
        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs("csv", "bbox", toList(parameters.getBbox())));
        queryParams.addAll(toPairs("datetime", parameters.getDatetime()));
        queryParams.addAll(toPairs("q", parameters.getQ()));
        queryParams.addAll(toPairs("type", parameters.getType()));
        queryParams.addAll(toPairs("externalId", parameters.getExternalId()));
        queryParams.addAll(toPairs("filter", parameters.getFilter()));
        queryParams.addAll(toPairs("filter-lang", parameters.getFilterLang()));
        queryParams.addAll(toPairs("sortby", parameters.getSortby()));
        queryParams.addAll(toPairs("limit", parameters.getLimit()));
        queryParams.addAll(toPairs("offset", parameters.getOffset()));
        queryParams.addAll(toPairs("crs", parameters.getCrs()));
        queryParams.addAll(toPairs("f", parameters.getF()));
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/collections/" + urlEncode(parameters.getCollectionId()) + "/items", queryParams));
        request.header("Accept", ACCEPT_RECORDS);
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, GeoJSONFeatureCollection.class);
    }

    /**
     * Fetch a single record.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;GeoJSONFeature&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<GeoJSONFeature>> getRecord(GetRecord parameters) throws IllegalArgumentException {
        if (parameters.getCollectionId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'collectionId' when calling getRecord");
        }
        if (parameters.getRecordId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'recordId' when calling getRecord");
        }
        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs("crs", parameters.getCrs()));
        queryParams.addAll(toPairs("f", parameters.getF()));
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/collections/" + urlEncode(parameters.getCollectionId()) + "/items/" + urlEncode(parameters.getRecordId()), queryParams));
        request.header("Accept", ACCEPT_RECORDS);
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, GeoJSONFeature.class);
    }

    /**
     * Fetch the facets of a catalog.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Facets&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<Facets>> getFacets(GetFacets parameters) throws IllegalArgumentException {
        if (parameters.getCollectionId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'collectionId' when calling getFacets");
        }
        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs("facets", parameters.getFacets()));
        queryParams.addAll(toPairs("f", parameters.getF()));
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/collections/" + urlEncode(parameters.getCollectionId()) + "/facets", queryParams));
        request.header("Accept", ACCEPT_FACETS);
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, Facets.class);
    }

    /**
     * Create a new record in a catalog. On success, the URI of the new record is returned in the
     * response's {@code Location} header.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Void&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Void> createRecord(CreateRecord parameters) throws IllegalArgumentException {
        if (parameters.getCollectionId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'collectionId' when calling createRecord");
        }
        if (parameters.getContent() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'content' when calling createRecord");
        }
        if (parameters.getContentType() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'contentType' when calling createRecord");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/collections/" + urlEncode(parameters.getCollectionId()) + "/items", toPairs("dry-run", parameters.getDryRun())));
        request.header("Content-Type", parameters.getContentType());
        if (parameters.getHandling() != null) {
            request.header("Prefer", parameters.getHandling().headerValue);
        }
        request.method("POST", HttpRequest.BodyPublishers.ofByteArray(parameters.getContent()));
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    /**
     * Replace an existing record. All properties and the geometry of the record are replaced.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Void&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Void> replaceRecord(ReplaceRecord parameters) throws IllegalArgumentException {
        if (parameters.getCollectionId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'collectionId' when calling replaceRecord");
        }
        if (parameters.getRecordId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'recordId' when calling replaceRecord");
        }
        if (parameters.getContent() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'content' when calling replaceRecord");
        }
        if (parameters.getContentType() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'contentType' when calling replaceRecord");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/collections/" + urlEncode(parameters.getCollectionId()) + "/items/" + urlEncode(parameters.getRecordId()),
                toPairs("dry-run", parameters.getDryRun())));
        request.header("Content-Type", parameters.getContentType());
        if (parameters.getHandling() != null) {
            request.header("Prefer", parameters.getHandling().headerValue);
        }
        request.method("PUT", HttpRequest.BodyPublishers.ofByteArray(parameters.getContent()));
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    /**
     * Update an existing record with a partial document (RFC 7396 JSON Merge Patch).
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Void&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Void> updateRecord(UpdateRecord parameters) throws IllegalArgumentException {
        if (parameters.getCollectionId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'collectionId' when calling updateRecord");
        }
        if (parameters.getRecordId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'recordId' when calling updateRecord");
        }
        if (parameters.getContent() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'content' when calling updateRecord");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/collections/" + urlEncode(parameters.getCollectionId()) + "/items/" + urlEncode(parameters.getRecordId()),
                toPairs("dry-run", parameters.getDryRun())));
        request.header("Content-Type", "application/merge-patch+json");
        if (parameters.getHandling() != null) {
            request.header("Prefer", parameters.getHandling().headerValue);
        }
        request.method("PATCH", HttpRequest.BodyPublishers.ofByteArray(parameters.getContent()));
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    /**
     * Delete an existing record.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Void&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Void> deleteRecord(DeleteRecord parameters) throws IllegalArgumentException {
        if (parameters.getCollectionId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'collectionId' when calling deleteRecord");
        }
        if (parameters.getRecordId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'recordId' when calling deleteRecord");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/collections/" + urlEncode(parameters.getCollectionId()) + "/items/" + urlEncode(parameters.getRecordId()), null));
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
