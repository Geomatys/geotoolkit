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
package org.geotoolkit.ogcapi.client.movingfeatures;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import org.geotoolkit.client.openapi.AbstractOpenApi;
import org.geotoolkit.client.openapi.OpenApiConfiguration;
import org.geotoolkit.client.service.Pair;
import org.geotoolkit.client.service.ServiceRequest;
import org.geotoolkit.ogcapi.dto.movingfeatures.MovingFeature;
import org.geotoolkit.ogcapi.dto.movingfeatures.MovingFeatures;
import org.geotoolkit.ogcapi.dto.movingfeatures.TemporalGeometrySequence;
import org.geotoolkit.ogcapi.dto.movingfeatures.TemporalProperties;
import org.geotoolkit.ogcapi.dto.movingfeatures.TemporalProperty;
import org.geotoolkit.ogcapi.request.movingfeatures.CreateCollection;
import org.geotoolkit.ogcapi.request.movingfeatures.DeleteCollection;
import org.geotoolkit.ogcapi.request.movingfeatures.DeleteMovingFeature;
import org.geotoolkit.ogcapi.request.movingfeatures.DeleteTemporalGeometry;
import org.geotoolkit.ogcapi.request.movingfeatures.DeleteTemporalPrimitiveValue;
import org.geotoolkit.ogcapi.request.movingfeatures.DeleteTemporalProperty;
import org.geotoolkit.ogcapi.request.movingfeatures.GetAcceleration;
import org.geotoolkit.ogcapi.request.movingfeatures.GetDistance;
import org.geotoolkit.ogcapi.request.movingfeatures.GetMovingFeature;
import org.geotoolkit.ogcapi.request.movingfeatures.GetMovingFeatures;
import org.geotoolkit.ogcapi.request.movingfeatures.GetTemporalGeometrySequence;
import org.geotoolkit.ogcapi.request.movingfeatures.GetTemporalProperties;
import org.geotoolkit.ogcapi.request.movingfeatures.GetTemporalProperty;
import org.geotoolkit.ogcapi.request.movingfeatures.GetVelocity;
import org.geotoolkit.ogcapi.request.movingfeatures.InsertMovingFeatures;
import org.geotoolkit.ogcapi.request.movingfeatures.InsertTemporalGeometry;
import org.geotoolkit.ogcapi.request.movingfeatures.InsertTemporalPrimitiveValue;
import org.geotoolkit.ogcapi.request.movingfeatures.InsertTemporalProperty;
import org.geotoolkit.ogcapi.request.movingfeatures.ReplaceCollection;

/**
 * OGC API - Moving Features, Part 1: Core.
 *
 * <p>Landing page, conformance, collection listing and single-collection access are exposed by
 * {@link org.geotoolkit.ogcapi.client.common.CoreApi} and
 * {@link org.geotoolkit.ogcapi.client.common.CollectionsApi}, shared with OGC API - Common.</p>
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class MovingFeaturesApi extends AbstractOpenApi {

    private static final String CONTENT_TYPE_JSON = "application/json";
    private static final String CONTENT_TYPE_GEOJSON = "application/geo+json";

    public MovingFeaturesApi(OpenApiConfiguration config) {
        super(config);
    }

    /**
     * Register a new moving feature collection.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Void&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Void> createCollection(CreateCollection parameters) throws IllegalArgumentException {
        if (parameters.getContent() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'content' when calling createCollection");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/collections", null));
        request.header("Content-Type", CONTENT_TYPE_JSON);
        request.method("POST", HttpRequest.BodyPublishers.ofByteArray(parameters.getContent()));
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    /**
     * Replace the metadata of an existing moving feature collection. {@code updateFrequency} is
     * not updated by this operation.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Void&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Void> replaceCollection(ReplaceCollection parameters) throws IllegalArgumentException {
        if (parameters.getCollectionId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'collectionId' when calling replaceCollection");
        }
        if (parameters.getContent() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'content' when calling replaceCollection");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/collections/" + urlEncode(parameters.getCollectionId()), null));
        request.header("Content-Type", CONTENT_TYPE_JSON);
        request.method("PUT", HttpRequest.BodyPublishers.ofByteArray(parameters.getContent()));
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    /**
     * Delete a moving feature collection.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Void&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Void> deleteCollection(DeleteCollection parameters) throws IllegalArgumentException {
        if (parameters.getCollectionId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'collectionId' when calling deleteCollection");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/collections/" + urlEncode(parameters.getCollectionId()), null));
        request.method("DELETE", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    /**
     * Fetch the moving features of a collection.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;MovingFeatures&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<MovingFeatures>> getMovingFeatures(GetMovingFeatures parameters) throws IllegalArgumentException {
        if (parameters.getCollectionId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'collectionId' when calling getMovingFeatures");
        }
        if (parameters.getDatetime() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'datetime' when calling getMovingFeatures");
        }
        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs("csv", "bbox", toList(parameters.getBbox())));
        queryParams.addAll(toPairs("datetime", parameters.getDatetime()));
        queryParams.addAll(toPairs("limit", parameters.getLimit()));
        queryParams.addAll(toPairs("subTrajectory", parameters.getSubtrajectory()));
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/collections/" + urlEncode(parameters.getCollectionId()) + "/items", queryParams));
        request.header("Accept", CONTENT_TYPE_JSON);
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, MovingFeatures.class);
    }

    /**
     * Insert one or more moving features into a collection. On success, the URI(s) of the new
     * resource(s) are returned in the response's {@code Location}/{@code Locations} header.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Void&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Void> insertMovingFeatures(InsertMovingFeatures parameters) throws IllegalArgumentException {
        if (parameters.getCollectionId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'collectionId' when calling insertMovingFeatures");
        }
        if (parameters.getContent() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'content' when calling insertMovingFeatures");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/collections/" + urlEncode(parameters.getCollectionId()) + "/items", null));
        request.header("Content-Type", CONTENT_TYPE_JSON);
        request.method("POST", HttpRequest.BodyPublishers.ofByteArray(parameters.getContent()));
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    /**
     * Fetch a single moving feature.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;MovingFeature&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<MovingFeature>> getMovingFeature(GetMovingFeature parameters) throws IllegalArgumentException {
        if (parameters.getCollectionId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'collectionId' when calling getMovingFeature");
        }
        if (parameters.getMFeatureId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'mFeatureId' when calling getMovingFeature");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/collections/" + urlEncode(parameters.getCollectionId()) + "/items/" + urlEncode(parameters.getMFeatureId()), null));
        request.header("Accept", CONTENT_TYPE_GEOJSON);
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, MovingFeature.class);
    }

    /**
     * Delete a moving feature.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Void&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Void> deleteMovingFeature(DeleteMovingFeature parameters) throws IllegalArgumentException {
        if (parameters.getCollectionId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'collectionId' when calling deleteMovingFeature");
        }
        if (parameters.getMFeatureId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'mFeatureId' when calling deleteMovingFeature");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/collections/" + urlEncode(parameters.getCollectionId()) + "/items/" + urlEncode(parameters.getMFeatureId()), null));
        request.method("DELETE", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    /**
     * Fetch the temporal geometry sequence of a moving feature.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;TemporalGeometrySequence&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<TemporalGeometrySequence>> getTemporalGeometrySequence(GetTemporalGeometrySequence parameters) throws IllegalArgumentException {
        if (parameters.getCollectionId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'collectionId' when calling getTemporalGeometrySequence");
        }
        if (parameters.getMFeatureId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'mFeatureId' when calling getTemporalGeometrySequence");
        }
        if (parameters.getDatetime() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'datetime' when calling getTemporalGeometrySequence");
        }
        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs("csv", "bbox", toList(parameters.getBbox())));
        queryParams.addAll(toPairs("datetime", parameters.getDatetime()));
        queryParams.addAll(toPairs("limit", parameters.getLimit()));
        queryParams.addAll(toPairs("csv", "leaf", parameters.getLeaf()));
        queryParams.addAll(toPairs("subTrajectory", parameters.getSubtrajectory()));
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/collections/" + urlEncode(parameters.getCollectionId()) + "/items/" + urlEncode(parameters.getMFeatureId()) + "/tgsequence", queryParams));
        request.header("Accept", CONTENT_TYPE_JSON);
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, TemporalGeometrySequence.class);
    }

    /**
     * Insert a temporal primitive geometry into a moving feature. On success, the URI of the new
     * resource is returned in the response's {@code Location} header.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Void&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Void> insertTemporalGeometry(InsertTemporalGeometry parameters) throws IllegalArgumentException {
        if (parameters.getCollectionId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'collectionId' when calling insertTemporalGeometry");
        }
        if (parameters.getMFeatureId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'mFeatureId' when calling insertTemporalGeometry");
        }
        if (parameters.getContent() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'content' when calling insertTemporalGeometry");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/collections/" + urlEncode(parameters.getCollectionId()) + "/items/" + urlEncode(parameters.getMFeatureId()) + "/tgsequence", null));
        request.header("Content-Type", CONTENT_TYPE_JSON);
        request.method("POST", HttpRequest.BodyPublishers.ofByteArray(parameters.getContent()));
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    /**
     * Delete a temporal primitive geometry from a moving feature.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Void&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Void> deleteTemporalGeometry(DeleteTemporalGeometry parameters) throws IllegalArgumentException {
        if (parameters.getCollectionId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'collectionId' when calling deleteTemporalGeometry");
        }
        if (parameters.getMFeatureId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'mFeatureId' when calling deleteTemporalGeometry");
        }
        if (parameters.getTGeometryId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'tGeometryId' when calling deleteTemporalGeometry");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/collections/" + urlEncode(parameters.getCollectionId()) + "/items/" + urlEncode(parameters.getMFeatureId())
                + "/tgsequence/" + urlEncode(parameters.getTGeometryId()), null));
        request.method("DELETE", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    /**
     * Fetch the time-to-distance curve of a temporal primitive geometry.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;TemporalProperty&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<TemporalProperty>> getDistance(GetDistance parameters) throws IllegalArgumentException {
        if (parameters.getCollectionId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'collectionId' when calling getDistance");
        }
        if (parameters.getMFeatureId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'mFeatureId' when calling getDistance");
        }
        if (parameters.getTGeometryId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'tGeometryId' when calling getDistance");
        }
        if (parameters.getDatetime() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'datetime' when calling getDistance");
        }
        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs("datetime", parameters.getDatetime()));
        queryParams.addAll(toPairs("csv", "leaf", parameters.getLeaf()));
        queryParams.addAll(toPairs("subTemporalValue", parameters.getSubtemporalvalue()));
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/collections/" + urlEncode(parameters.getCollectionId()) + "/items/" + urlEncode(parameters.getMFeatureId())
                + "/tgsequence/" + urlEncode(parameters.getTGeometryId()) + "/distance", queryParams));
        request.header("Accept", CONTENT_TYPE_JSON);
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, TemporalProperty.class);
    }

    /**
     * Fetch the time-to-velocity curve of a temporal primitive geometry.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;TemporalProperty&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<TemporalProperty>> getVelocity(GetVelocity parameters) throws IllegalArgumentException {
        if (parameters.getCollectionId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'collectionId' when calling getVelocity");
        }
        if (parameters.getMFeatureId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'mFeatureId' when calling getVelocity");
        }
        if (parameters.getTGeometryId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'tGeometryId' when calling getVelocity");
        }
        if (parameters.getDatetime() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'datetime' when calling getVelocity");
        }
        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs("datetime", parameters.getDatetime()));
        queryParams.addAll(toPairs("csv", "leaf", parameters.getLeaf()));
        queryParams.addAll(toPairs("subTemporalValue", parameters.getSubtemporalvalue()));
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/collections/" + urlEncode(parameters.getCollectionId()) + "/items/" + urlEncode(parameters.getMFeatureId())
                + "/tgsequence/" + urlEncode(parameters.getTGeometryId()) + "/velocity", queryParams));
        request.header("Accept", CONTENT_TYPE_JSON);
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, TemporalProperty.class);
    }

    /**
     * Fetch the time-to-acceleration curve of a temporal primitive geometry.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;TemporalProperty&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<TemporalProperty>> getAcceleration(GetAcceleration parameters) throws IllegalArgumentException {
        if (parameters.getCollectionId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'collectionId' when calling getAcceleration");
        }
        if (parameters.getMFeatureId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'mFeatureId' when calling getAcceleration");
        }
        if (parameters.getTGeometryId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'tGeometryId' when calling getAcceleration");
        }
        if (parameters.getDatetime() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'datetime' when calling getAcceleration");
        }
        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs("datetime", parameters.getDatetime()));
        queryParams.addAll(toPairs("csv", "leaf", parameters.getLeaf()));
        queryParams.addAll(toPairs("subTemporalValue", parameters.getSubtemporalvalue()));
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/collections/" + urlEncode(parameters.getCollectionId()) + "/items/" + urlEncode(parameters.getMFeatureId())
                + "/tgsequence/" + urlEncode(parameters.getTGeometryId()) + "/acceleration", queryParams));
        request.header("Accept", CONTENT_TYPE_JSON);
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, TemporalProperty.class);
    }

    /**
     * Fetch the list of temporal properties of a moving feature.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;TemporalProperties&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<TemporalProperties>> getTemporalProperties(GetTemporalProperties parameters) throws IllegalArgumentException {
        if (parameters.getCollectionId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'collectionId' when calling getTemporalProperties");
        }
        if (parameters.getMFeatureId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'mFeatureId' when calling getTemporalProperties");
        }
        if (parameters.getDatetime() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'datetime' when calling getTemporalProperties");
        }
        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs("datetime", parameters.getDatetime()));
        queryParams.addAll(toPairs("limit", parameters.getLimit()));
        queryParams.addAll(toPairs("subTemporalValue", parameters.getSubtemporalvalue()));
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/collections/" + urlEncode(parameters.getCollectionId()) + "/items/" + urlEncode(parameters.getMFeatureId()) + "/tproperties", queryParams));
        request.header("Accept", CONTENT_TYPE_JSON);
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, TemporalProperties.class);
    }

    /**
     * Insert one or more temporal properties into a moving feature. On success, the URI(s) of the
     * new resource(s) are returned in the response's {@code Locations} header.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Void&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Void> insertTemporalProperty(InsertTemporalProperty parameters) throws IllegalArgumentException {
        if (parameters.getCollectionId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'collectionId' when calling insertTemporalProperty");
        }
        if (parameters.getMFeatureId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'mFeatureId' when calling insertTemporalProperty");
        }
        if (parameters.getContent() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'content' when calling insertTemporalProperty");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/collections/" + urlEncode(parameters.getCollectionId()) + "/items/" + urlEncode(parameters.getMFeatureId()) + "/tproperties", null));
        request.header("Content-Type", CONTENT_TYPE_JSON);
        request.method("POST", HttpRequest.BodyPublishers.ofByteArray(parameters.getContent()));
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    /**
     * Fetch the (subsequence of the) data of a single named temporal property.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;TemporalProperty&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<TemporalProperty>> getTemporalProperty(GetTemporalProperty parameters) throws IllegalArgumentException {
        if (parameters.getCollectionId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'collectionId' when calling getTemporalProperty");
        }
        if (parameters.getMFeatureId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'mFeatureId' when calling getTemporalProperty");
        }
        if (parameters.getTPropertyName() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'tPropertyName' when calling getTemporalProperty");
        }
        if (parameters.getDatetime() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'datetime' when calling getTemporalProperty");
        }
        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs("datetime", parameters.getDatetime()));
        queryParams.addAll(toPairs("csv", "leaf", parameters.getLeaf()));
        queryParams.addAll(toPairs("subTemporalValue", parameters.getSubtemporalvalue()));
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/collections/" + urlEncode(parameters.getCollectionId()) + "/items/" + urlEncode(parameters.getMFeatureId())
                + "/tproperties/" + urlEncode(parameters.getTPropertyName()), queryParams));
        request.header("Accept", CONTENT_TYPE_JSON);
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, TemporalProperty.class);
    }

    /**
     * Append a temporal primitive value to a named temporal property. On success, the URI of the
     * new resource is returned in the response's {@code Location} header.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Void&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Void> insertTemporalPrimitiveValue(InsertTemporalPrimitiveValue parameters) throws IllegalArgumentException {
        if (parameters.getCollectionId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'collectionId' when calling insertTemporalPrimitiveValue");
        }
        if (parameters.getMFeatureId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'mFeatureId' when calling insertTemporalPrimitiveValue");
        }
        if (parameters.getTPropertyName() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'tPropertyName' when calling insertTemporalPrimitiveValue");
        }
        if (parameters.getContent() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'content' when calling insertTemporalPrimitiveValue");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/collections/" + urlEncode(parameters.getCollectionId()) + "/items/" + urlEncode(parameters.getMFeatureId())
                + "/tproperties/" + urlEncode(parameters.getTPropertyName()), null));
        request.header("Content-Type", CONTENT_TYPE_JSON);
        request.method("POST", HttpRequest.BodyPublishers.ofByteArray(parameters.getContent()));
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    /**
     * Delete a named temporal property and all its values from a moving feature.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Void&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Void> deleteTemporalProperty(DeleteTemporalProperty parameters) throws IllegalArgumentException {
        if (parameters.getCollectionId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'collectionId' when calling deleteTemporalProperty");
        }
        if (parameters.getMFeatureId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'mFeatureId' when calling deleteTemporalProperty");
        }
        if (parameters.getTPropertyName() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'tPropertyName' when calling deleteTemporalProperty");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/collections/" + urlEncode(parameters.getCollectionId()) + "/items/" + urlEncode(parameters.getMFeatureId())
                + "/tproperties/" + urlEncode(parameters.getTPropertyName()), null));
        request.method("DELETE", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    /**
     * Delete a single temporal primitive value from a named temporal property.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Void&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Void> deleteTemporalPrimitiveValue(DeleteTemporalPrimitiveValue parameters) throws IllegalArgumentException {
        if (parameters.getCollectionId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'collectionId' when calling deleteTemporalPrimitiveValue");
        }
        if (parameters.getMFeatureId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'mFeatureId' when calling deleteTemporalPrimitiveValue");
        }
        if (parameters.getTPropertyName() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'tPropertyName' when calling deleteTemporalPrimitiveValue");
        }
        if (parameters.getTValueId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'tValueId' when calling deleteTemporalPrimitiveValue");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/collections/" + urlEncode(parameters.getCollectionId()) + "/items/" + urlEncode(parameters.getMFeatureId())
                + "/tproperties/" + urlEncode(parameters.getTPropertyName()) + "/" + urlEncode(parameters.getTValueId()), null));
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
