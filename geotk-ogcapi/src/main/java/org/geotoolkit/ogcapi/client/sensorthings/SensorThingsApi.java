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
package org.geotoolkit.ogcapi.client.sensorthings;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import org.geotoolkit.client.openapi.AbstractOpenApi;
import org.geotoolkit.client.openapi.OpenApiConfiguration;
import org.geotoolkit.client.service.Pair;
import org.geotoolkit.client.service.ServiceRequest;
import org.geotoolkit.ogcapi.dto.sensorthings.EntityList;
import org.geotoolkit.ogcapi.request.sensorthings.CreateEntity;
import org.geotoolkit.ogcapi.request.sensorthings.DeleteEntity;
import org.geotoolkit.ogcapi.request.sensorthings.GetEntities;
import org.geotoolkit.ogcapi.request.sensorthings.GetEntity;
import org.geotoolkit.ogcapi.request.sensorthings.UpdateEntity;

/**
 * OGC SensorThings API — Part 1: Sensing, Part 2: Tasking Core, and the STAplus extension.
 *
 * <p>
 * Unlike every other family in this module, SensorThings is not built on OGC API Common's
 * {@code /collections} + OpenAPI conventions: it is an OData-flavored REST API over a fixed,
 * globally-navigable entity graph ({@code /v1.1/Things}, {@code /v1.1/Things(1)/Datastreams},
 * {@code $filter}/{@code $expand}/{@code $top}/{@code $skip}/...). Rather than one request/method
 * pair per entity type, this client is generic: callers pass the runtime OData entity-set name
 * (see the {@code *_SET} constants below) and a {@code Class<T>}/entity instance to pick the
 * deserialization target and request body, the same way {@code FeatureApi} uses a runtime
 * {@code collectionId} instead of a class per collection.
 * </p>
 *
 * <p>Out of scope: the MQTT pub/sub extension of both Part 1 and Part 2; the {@code $ref}
 * association endpoints ({@code .../$ref} GET/POST/DELETE) — attaching/detaching an existing
 * entity without touching its lifecycle is already achievable with {@link #updateEntity} by
 * PATCHing the owning entity's navigation property with an id-only linked entity; and the
 * {@code $resultFormat=dataArray} compact Observation-array response encoding.</p>
 *
 * @see https://docs.ogc.org/is/18-088/18-088.html
 * @see https://docs.ogc.org/is/17-079r1/17-079r1.html
 * @see https://docs.ogc.org/is/22-022r1/22-022r1.html
 * @author Quentin Bialota (Geomatys)
 */
public final class SensorThingsApi extends AbstractOpenApi {

    // Part 1: Sensing entity sets
    public static final String THINGS = "Things";
    public static final String LOCATIONS = "Locations";
    public static final String HISTORICAL_LOCATIONS = "HistoricalLocations";
    public static final String DATASTREAMS = "Datastreams";
    public static final String SENSORS = "Sensors";
    public static final String OBSERVED_PROPERTIES = "ObservedProperties";
    public static final String OBSERVATIONS = "Observations";
    public static final String FEATURES_OF_INTEREST = "FeaturesOfInterest";
    // MultiDatastream extension
    public static final String MULTI_DATASTREAMS = "MultiDatastreams";
    // Part 2: Tasking Core entity sets
    public static final String ACTUATORS = "Actuators";
    public static final String TASKS = "Tasks";
    public static final String TASKING_CAPABILITIES = "TaskingCapabilities";
    // STAplus entity sets
    public static final String PARTIES = "Parties";
    public static final String LICENSES = "Licenses";
    public static final String OBSERVATION_GROUPS = "ObservationGroups";
    public static final String RELATIONS = "Relations";
    public static final String CAMPAIGNS = "Campaigns";

    public SensorThingsApi(OpenApiConfiguration config) {
        super(config);
    }

    /**
     * Format an {@code @iot.id} value as an OData key predicate: numeric keys are written bare
     * ({@code 1}), string keys are single-quoted with embedded quotes doubled ({@code 'abc''s'}).
     */
    private static String formatKey(Object id) {
        if (id instanceof Number) {
            return String.valueOf(id);
        }
        return "'" + id.toString().replace("'", "''") + "'";
    }

    /**
     * Build the OData resource path for an entity set, optionally keyed and/or nested under a
     * parent entity, e.g. {@code Things(1)/Datastreams} or {@code Things(1)} or {@code Things}.
     */
    private static String buildPath(String parentEntitySet, Object parentId, String entitySet, Object id) {
        final StringBuilder sb = new StringBuilder();
        if (parentEntitySet != null) {
            sb.append(parentEntitySet);
            if (parentId != null) {
                sb.append('(').append(formatKey(parentId)).append(')');
            }
            sb.append('/');
        }
        sb.append(entitySet);
        if (id != null) {
            sb.append('(').append(formatKey(id)).append(')');
        }
        return sb.toString();
    }

    /**
     * List the entities of an entity set.
     *
     * @param parameters request parameters
     * @param entityType the entity class to deserialize each {@code value} item as
     * @return ServiceRequest&lt;EntityList&lt;T&gt;&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public <T> ServiceRequest<Supplier<EntityList<T>>> getEntities(GetEntities parameters, Class<T> entityType) throws IllegalArgumentException {
        if (parameters.getEntitySet() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'entitySet' when calling getEntities");
        }

        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs("$filter", parameters.getFilter()));
        queryParams.addAll(toPairs("$expand", parameters.getExpand()));
        queryParams.addAll(toPairs("$select", parameters.getSelect()));
        queryParams.addAll(toPairs("$orderby", parameters.getOrderby()));
        queryParams.addAll(toPairs("$top", parameters.getTop()));
        queryParams.addAll(toPairs("$skip", parameters.getSkip()));
        queryParams.addAll(toPairs("$count", parameters.getCount()));
        queryParams.addAll(toPairs("$resultFormat", parameters.getResultFormat()));

        final String path = buildPath(parameters.getParentEntitySet(), parameters.getParentId(), parameters.getEntitySet(), null);
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/" + path, queryParams));
        request.header("Accept", "application/json");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);

        return requestAsJson(request, objectMapper.getTypeFactory().constructParametricType(EntityList.class, entityType));
    }

    /**
     * Retrieve a single entity by id.
     *
     * @param parameters request parameters
     * @param entityType the entity class to deserialize the response as
     * @return ServiceRequest&lt;T&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public <T> ServiceRequest<Supplier<T>> getEntity(GetEntity parameters, Class<T> entityType) throws IllegalArgumentException {
        if (parameters.getEntitySet() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'entitySet' when calling getEntity");
        }
        if (parameters.getId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'id' when calling getEntity");
        }

        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs("$select", parameters.getSelect()));
        queryParams.addAll(toPairs("$expand", parameters.getExpand()));

        final String path;
        if (parameters.getParentEntitySet() != null) {
            path = buildPath(parameters.getParentEntitySet(), parameters.getParentId(), parameters.getEntitySet(), null) + "(" + formatKey(parameters.getId()) + ")";
        } else {
            path = buildPath(null, null, parameters.getEntitySet(), parameters.getId());
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/" + path, queryParams));
        request.header("Accept", "application/json");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);

        return requestAsJson(request, entityType);
    }

    /**
     * Create a new entity.
     *
     * <p>// ponytail: real STA servers return the created entity's {@code @iot.id} via the
     * response's {@code Location} header; not currently surfaced here. Add a header-reading
     * variant if a caller needs the id back.</p>
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Void&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Void> createEntity(CreateEntity parameters) throws IllegalArgumentException {
        if (parameters.getEntitySet() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'entitySet' when calling createEntity");
        }
        if (parameters.getContent() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'content' when calling createEntity");
        }
        if (parameters.getContentType() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'contentType' when calling createEntity");
        }

        final String path = buildPath(parameters.getParentEntitySet(), parameters.getParentId(), parameters.getEntitySet(), null);
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/" + path, null));
        request.header("Content-Type", parameters.getContentType());
        request.header("Accept", "application/json");
        request.method("POST", HttpRequest.BodyPublishers.ofByteArray(parameters.getContent()));
        setConfig(request);

        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    /**
     * Partially update an existing entity.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Void&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Void> updateEntity(UpdateEntity parameters) throws IllegalArgumentException {
        if (parameters.getEntitySet() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'entitySet' when calling updateEntity");
        }
        if (parameters.getId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'id' when calling updateEntity");
        }
        if (parameters.getContent() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'content' when calling updateEntity");
        }

        final String path = buildPath(null, null, parameters.getEntitySet(), parameters.getId());
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/" + path, null));
        request.header("Content-Type", parameters.getContentType() == null ? "application/json" : parameters.getContentType());
        request.header("Accept", "application/json");
        request.method("PATCH", HttpRequest.BodyPublishers.ofByteArray(parameters.getContent()));
        setConfig(request);

        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    /**
     * Delete an existing entity.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Void&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Void> deleteEntity(DeleteEntity parameters) throws IllegalArgumentException {
        if (parameters.getEntitySet() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'entitySet' when calling deleteEntity");
        }
        if (parameters.getId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'id' when calling deleteEntity");
        }

        final String path = buildPath(null, null, parameters.getEntitySet(), parameters.getId());
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/" + path, null));
        request.method("DELETE", HttpRequest.BodyPublishers.noBody());
        setConfig(request);

        return request(request, HttpResponse.BodyHandlers.discarding());
    }

}
