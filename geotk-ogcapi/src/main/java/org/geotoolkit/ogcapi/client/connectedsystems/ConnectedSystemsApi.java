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
package org.geotoolkit.ogcapi.client.connectedsystems;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import org.geotoolkit.client.openapi.AbstractOpenApi;
import org.geotoolkit.client.openapi.OpenApiConfiguration;
import org.geotoolkit.client.service.Pair;
import org.geotoolkit.client.service.ServiceRequest;
import org.geotoolkit.ogcapi.dto.connectedsystems.geojson.Deployment;
import org.geotoolkit.ogcapi.dto.connectedsystems.geojson.DeploymentCollection;
import org.geotoolkit.ogcapi.dto.connectedsystems.geojson.Procedure;
import org.geotoolkit.ogcapi.dto.connectedsystems.geojson.ProcedureCollection;
import org.geotoolkit.ogcapi.dto.connectedsystems.geojson.SamplingFeature;
import org.geotoolkit.ogcapi.dto.connectedsystems.geojson.SamplingFeatureCollection;
import org.geotoolkit.ogcapi.dto.connectedsystems.geojson.System;
import org.geotoolkit.ogcapi.dto.connectedsystems.geojson.SystemCollection;
import org.geotoolkit.ogcapi.dto.connectedsystems.sensorml.DerivedProperty;
import org.geotoolkit.ogcapi.dto.connectedsystems.sensorml.PropertyCollection;
import org.geotoolkit.ogcapi.request.connectedsystems.CreateDeployment;
import org.geotoolkit.ogcapi.request.connectedsystems.CreateProcedure;
import org.geotoolkit.ogcapi.request.connectedsystems.CreateProperty;
import org.geotoolkit.ogcapi.request.connectedsystems.CreateSubdeployment;
import org.geotoolkit.ogcapi.request.connectedsystems.CreateSubsystem;
import org.geotoolkit.ogcapi.request.connectedsystems.CreateSystem;
import org.geotoolkit.ogcapi.request.connectedsystems.CreateSystemSamplingFeature;
import org.geotoolkit.ogcapi.request.connectedsystems.DeleteDeployment;
import org.geotoolkit.ogcapi.request.connectedsystems.DeleteProcedure;
import org.geotoolkit.ogcapi.request.connectedsystems.DeleteProperty;
import org.geotoolkit.ogcapi.request.connectedsystems.DeleteSamplingFeature;
import org.geotoolkit.ogcapi.request.connectedsystems.DeleteSystem;
import org.geotoolkit.ogcapi.request.connectedsystems.GetDeployment;
import org.geotoolkit.ogcapi.request.connectedsystems.GetDeployments;
import org.geotoolkit.ogcapi.request.connectedsystems.GetProcedure;
import org.geotoolkit.ogcapi.request.connectedsystems.GetProcedures;
import org.geotoolkit.ogcapi.request.connectedsystems.GetProperties;
import org.geotoolkit.ogcapi.request.connectedsystems.GetProperty;
import org.geotoolkit.ogcapi.request.connectedsystems.GetSamplingFeature;
import org.geotoolkit.ogcapi.request.connectedsystems.GetSamplingFeatures;
import org.geotoolkit.ogcapi.request.connectedsystems.GetSubdeployments;
import org.geotoolkit.ogcapi.request.connectedsystems.GetSubsystems;
import org.geotoolkit.ogcapi.request.connectedsystems.GetSystem;
import org.geotoolkit.ogcapi.request.connectedsystems.GetSystemDeployments;
import org.geotoolkit.ogcapi.request.connectedsystems.GetSystemSamplingFeatures;
import org.geotoolkit.ogcapi.request.connectedsystems.GetSystems;
import org.geotoolkit.ogcapi.request.connectedsystems.ReplaceDeployment;
import org.geotoolkit.ogcapi.request.connectedsystems.ReplaceProcedure;
import org.geotoolkit.ogcapi.request.connectedsystems.ReplaceProperty;
import org.geotoolkit.ogcapi.request.connectedsystems.ReplaceSamplingFeature;
import org.geotoolkit.ogcapi.request.connectedsystems.ReplaceSystem;

/**
 * OGC API - Connected Systems - Part 1: Feature Resources (Systems, Deployments, Procedures,
 * Sampling Features, Property Definitions).
 *
 * <p>The generic {@code /collections/{collectionId}/items} endpoints (OGC API - Features Core and
 * Create/Replace/Delete) are already covered by {@link org.geotoolkit.ogcapi.client.feature.FeatureApi}
 * and are not duplicated here. Landing page, conformance and {@code /collections} are covered by
 * {@link org.geotoolkit.ogcapi.client.common.CoreApi} / {@link org.geotoolkit.ogcapi.client.common.CollectionsApi}.</p>
 *
 * <p>GET responses are deserialized as GeoJSON ({@code application/geo+json}), the resources'
 * primary encoding. The SensorML-JSON encoding ({@code application/sml+json}) of Systems,
 * Deployments and Procedures can be retrieved by callers by requesting a raw body (e.g. by
 * building the request manually or reading the response as bytes), then parsing it with
 * {@link org.geotoolkit.ogcapi.dto.connectedsystems.sensorml.DescribedObject} or
 * {@link org.geotoolkit.ogcapi.dto.connectedsystems.sensorml.Deployment}. Property Definitions have
 * no GeoJSON encoding and are always deserialized as SensorML-JSON.</p>
 *
 * @see <a href="https://docs.ogc.org/is/23-001/23-001.html">OGC API - Connected Systems - Part 1</a>
 * @author Johann Sorel (Geomatys)
 */
public final class ConnectedSystemsApi extends AbstractOpenApi {

    /**
     * Parameter names.
     */
    public static final String PARAM_BASE_PROPERTY       = "baseProperty";
    public static final String PARAM_BBOX                = "bbox";
    public static final String PARAM_CASCADE             = "cascade";
    public static final String PARAM_CONTROLLED_PROPERTY = "controlledProperty";
    public static final String PARAM_DATETIME            = "datetime";
    public static final String PARAM_DRY_RUN             = "dry-run";
    public static final String PARAM_FOI                 = "foi";
    public static final String PARAM_GEOM                = "geom";
    public static final String PARAM_ID                  = "id";
    public static final String PARAM_LIMIT               = "limit";
    public static final String PARAM_OBJECT_TYPE         = "objectType";
    public static final String PARAM_OBSERVED_PROPERTY   = "observedProperty";
    public static final String PARAM_PARENT              = "parent";
    public static final String PARAM_PROCEDURE           = "procedure";
    public static final String PARAM_Q                   = "q";
    public static final String PARAM_RECURSIVE           = "recursive";
    public static final String PARAM_SYSTEM              = "system";

    /**
     * Path fragments.
     */
    public static final String PATH_DEPLOYMENTS       = "/deployments";
    public static final String PATH_PROCEDURES        = "/procedures";
    public static final String PATH_PROPERTIES        = "/properties";
    public static final String PATH_SAMPLING_FEATURES = "/samplingFeatures";
    public static final String PATH_SUBDEPLOYMENTS    = "/subdeployments";
    public static final String PATH_SUBSYSTEMS        = "/subsystems";
    public static final String PATH_SYSTEMS           = "/systems";

    private static final String ACCEPT_FEATURES = "application/geo+json, application/sml+json, application/json, text/html";
    private static final String ACCEPT_PROPERTIES = "application/sml+json, application/json";

    public ConnectedSystemsApi(OpenApiConfiguration config) {
        super(config);
    }

    // ------------------------------------------------------------------
    // Systems
    // ------------------------------------------------------------------

    /**
     * Fetch the collection of System resources.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;SystemCollection&gt;
     */
    public ServiceRequest<Supplier<SystemCollection>> getSystems(GetSystems parameters) {
        final List<Pair> queryParams = systemFilterPairs(parameters.getId(), parameters.getBbox(), parameters.getDatetime(),
                parameters.getGeom(), parameters.getKeyword(), parameters.getParent(), parameters.getProcedure(),
                parameters.getFoi(), parameters.getObservedProperty(), parameters.getControlledProperty(),
                parameters.getRecursive(), parameters.getLimit());
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_SYSTEMS, queryParams));
        request.header("Accept", ACCEPT_FEATURES);
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, SystemCollection.class);
    }

    /**
     * Create a new System resource. On success, the URI of the new resource is returned in the
     * response's {@code Location} header.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Void&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Void> createSystem(CreateSystem parameters) throws IllegalArgumentException {
        requireContent(parameters.getContent(), parameters.getContentType(), "createSystem");
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_SYSTEMS, toPairs(PARAM_DRY_RUN, parameters.getDryRun())));
        request.header("Content-Type", parameters.getContentType());
        setPrefer(request, parameters.getHandling());
        request.method("POST", HttpRequest.BodyPublishers.ofByteArray(parameters.getContent()));
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    /**
     * Fetch a single System resource.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;System&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<System>> getSystem(GetSystem parameters) throws IllegalArgumentException {
        if (parameters.getSystemId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'systemId' when calling getSystem");
        }
        final List<Pair> queryParams = toPairs(PARAM_DATETIME, parameters.getDatetime());
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_SYSTEMS + '/' + urlEncode(parameters.getSystemId()), queryParams));
        request.header("Accept", ACCEPT_FEATURES);
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, System.class);
    }

    /**
     * Replace an existing System resource.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Void&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Void> replaceSystem(ReplaceSystem parameters) throws IllegalArgumentException {
        if (parameters.getSystemId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'systemId' when calling replaceSystem");
        }
        requireContent(parameters.getContent(), parameters.getContentType(), "replaceSystem");
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_SYSTEMS + '/' + urlEncode(parameters.getSystemId()), toPairs(PARAM_DRY_RUN, parameters.getDryRun())));
        request.header("Content-Type", parameters.getContentType());
        setPrefer(request, parameters.getHandling());
        request.method("PUT", HttpRequest.BodyPublishers.ofByteArray(parameters.getContent()));
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    /**
     * Delete an existing System resource.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Void&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Void> deleteSystem(DeleteSystem parameters) throws IllegalArgumentException {
        if (parameters.getSystemId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'systemId' when calling deleteSystem");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_SYSTEMS + '/' + urlEncode(parameters.getSystemId()), toPairs(PARAM_CASCADE, parameters.getCascade())));
        request.method("DELETE", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    /**
     * Fetch the subsystems of a System resource.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;SystemCollection&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<SystemCollection>> getSubsystems(GetSubsystems parameters) throws IllegalArgumentException {
        if (parameters.getSystemId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'systemId' when calling getSubsystems");
        }
        final List<Pair> queryParams = systemFilterPairs(parameters.getId(), parameters.getBbox(), parameters.getDatetime(),
                parameters.getGeom(), parameters.getKeyword(), parameters.getParent(), parameters.getProcedure(),
                parameters.getFoi(), parameters.getObservedProperty(), parameters.getControlledProperty(),
                parameters.getRecursive(), parameters.getLimit());
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_SYSTEMS + '/' + urlEncode(parameters.getSystemId()) + PATH_SUBSYSTEMS, queryParams));
        request.header("Accept", ACCEPT_FEATURES);
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, SystemCollection.class);
    }

    /**
     * Create a new subsystem of a System resource.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Void&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Void> createSubsystem(CreateSubsystem parameters) throws IllegalArgumentException {
        if (parameters.getSystemId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'systemId' when calling createSubsystem");
        }
        requireContent(parameters.getContent(), parameters.getContentType(), "createSubsystem");
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_SYSTEMS + '/' + urlEncode(parameters.getSystemId()) + PATH_SUBSYSTEMS, toPairs(PARAM_DRY_RUN, parameters.getDryRun())));
        request.header("Content-Type", parameters.getContentType());
        setPrefer(request, parameters.getHandling());
        request.method("POST", HttpRequest.BodyPublishers.ofByteArray(parameters.getContent()));
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    // ------------------------------------------------------------------
    // Deployments
    // ------------------------------------------------------------------

    /**
     * Fetch the collection of Deployment resources.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;DeploymentCollection&gt;
     */
    public ServiceRequest<Supplier<DeploymentCollection>> getDeployments(GetDeployments parameters) {
        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs("csv", PARAM_ID, parameters.getId()));
        queryParams.addAll(toPairs("csv", PARAM_BBOX, toList(parameters.getBbox())));
        queryParams.addAll(toPairs(PARAM_DATETIME, parameters.getDatetime()));
        queryParams.addAll(toPairs(PARAM_GEOM, parameters.getGeom()));
        queryParams.addAll(toPairs("csv", PARAM_Q, parameters.getKeyword()));
        queryParams.addAll(toPairs("csv", PARAM_PARENT, parameters.getParent()));
        queryParams.addAll(toPairs("csv", PARAM_SYSTEM, parameters.getSystem()));
        queryParams.addAll(toPairs("csv", PARAM_FOI, parameters.getFoi()));
        queryParams.addAll(toPairs("csv", PARAM_OBSERVED_PROPERTY, parameters.getObservedProperty()));
        queryParams.addAll(toPairs("csv", PARAM_CONTROLLED_PROPERTY, parameters.getControlledProperty()));
        queryParams.addAll(toPairs(PARAM_LIMIT, parameters.getLimit()));
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_DEPLOYMENTS, queryParams));
        request.header("Accept", ACCEPT_FEATURES);
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, DeploymentCollection.class);
    }

    /**
     * Create a new Deployment resource.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Void&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Void> createDeployment(CreateDeployment parameters) throws IllegalArgumentException {
        requireContent(parameters.getContent(), parameters.getContentType(), "createDeployment");
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_DEPLOYMENTS, toPairs(PARAM_DRY_RUN, parameters.getDryRun())));
        request.header("Content-Type", parameters.getContentType());
        setPrefer(request, parameters.getHandling());
        request.method("POST", HttpRequest.BodyPublishers.ofByteArray(parameters.getContent()));
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    /**
     * Fetch a single Deployment resource.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Deployment&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<Deployment>> getDeployment(GetDeployment parameters) throws IllegalArgumentException {
        if (parameters.getDeploymentId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'deploymentId' when calling getDeployment");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_DEPLOYMENTS + '/' + urlEncode(parameters.getDeploymentId()), null));
        request.header("Accept", ACCEPT_FEATURES);
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, Deployment.class);
    }

    /**
     * Replace an existing Deployment resource.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Void&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Void> replaceDeployment(ReplaceDeployment parameters) throws IllegalArgumentException {
        if (parameters.getDeploymentId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'deploymentId' when calling replaceDeployment");
        }
        requireContent(parameters.getContent(), parameters.getContentType(), "replaceDeployment");
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_DEPLOYMENTS + '/' + urlEncode(parameters.getDeploymentId()), toPairs(PARAM_DRY_RUN, parameters.getDryRun())));
        request.header("Content-Type", parameters.getContentType());
        setPrefer(request, parameters.getHandling());
        request.method("PUT", HttpRequest.BodyPublishers.ofByteArray(parameters.getContent()));
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    /**
     * Delete an existing Deployment resource.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Void&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Void> deleteDeployment(DeleteDeployment parameters) throws IllegalArgumentException {
        if (parameters.getDeploymentId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'deploymentId' when calling deleteDeployment");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_DEPLOYMENTS + '/' + urlEncode(parameters.getDeploymentId()), null));
        request.method("DELETE", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    /**
     * Fetch the subdeployments of a Deployment resource.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;DeploymentCollection&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<DeploymentCollection>> getSubdeployments(GetSubdeployments parameters) throws IllegalArgumentException {
        if (parameters.getDeploymentId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'deploymentId' when calling getSubdeployments");
        }
        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs(PARAM_RECURSIVE, parameters.getRecursive()));
        queryParams.addAll(toPairs("csv", PARAM_ID, parameters.getId()));
        queryParams.addAll(toPairs("csv", PARAM_BBOX, toList(parameters.getBbox())));
        queryParams.addAll(toPairs(PARAM_DATETIME, parameters.getDatetime()));
        queryParams.addAll(toPairs(PARAM_GEOM, parameters.getGeom()));
        queryParams.addAll(toPairs("csv", PARAM_Q, parameters.getKeyword()));
        queryParams.addAll(toPairs("csv", PARAM_PARENT, parameters.getParent()));
        queryParams.addAll(toPairs("csv", PARAM_SYSTEM, parameters.getSystem()));
        queryParams.addAll(toPairs("csv", PARAM_FOI, parameters.getFoi()));
        queryParams.addAll(toPairs("csv", PARAM_OBSERVED_PROPERTY, parameters.getObservedProperty()));
        queryParams.addAll(toPairs("csv", PARAM_CONTROLLED_PROPERTY, parameters.getControlledProperty()));
        queryParams.addAll(toPairs(PARAM_LIMIT, parameters.getLimit()));
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_DEPLOYMENTS + '/' + urlEncode(parameters.getDeploymentId()) + PATH_SUBDEPLOYMENTS, queryParams));
        request.header("Accept", ACCEPT_FEATURES);
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, DeploymentCollection.class);
    }

    /**
     * Create a new subdeployment of a Deployment resource.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Void&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Void> createSubdeployment(CreateSubdeployment parameters) throws IllegalArgumentException {
        if (parameters.getDeploymentId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'deploymentId' when calling createSubdeployment");
        }
        requireContent(parameters.getContent(), parameters.getContentType(), "createSubdeployment");
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_DEPLOYMENTS + '/' + urlEncode(parameters.getDeploymentId()) + PATH_SUBDEPLOYMENTS, toPairs(PARAM_DRY_RUN, parameters.getDryRun())));
        request.header("Content-Type", parameters.getContentType());
        setPrefer(request, parameters.getHandling());
        request.method("POST", HttpRequest.BodyPublishers.ofByteArray(parameters.getContent()));
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    /**
     * Fetch the deployments of a System resource.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;DeploymentCollection&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<DeploymentCollection>> getSystemDeployments(GetSystemDeployments parameters) throws IllegalArgumentException {
        if (parameters.getSystemId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'systemId' when calling getSystemDeployments");
        }
        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs("csv", PARAM_ID, parameters.getId()));
        queryParams.addAll(toPairs("csv", PARAM_BBOX, toList(parameters.getBbox())));
        queryParams.addAll(toPairs(PARAM_DATETIME, parameters.getDatetime()));
        queryParams.addAll(toPairs(PARAM_GEOM, parameters.getGeom()));
        queryParams.addAll(toPairs("csv", PARAM_Q, parameters.getKeyword()));
        queryParams.addAll(toPairs("csv", PARAM_FOI, parameters.getFoi()));
        queryParams.addAll(toPairs(PARAM_LIMIT, parameters.getLimit()));
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_SYSTEMS + '/' + urlEncode(parameters.getSystemId()) + PATH_DEPLOYMENTS, queryParams));
        request.header("Accept", ACCEPT_FEATURES);
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, DeploymentCollection.class);
    }

    // ------------------------------------------------------------------
    // Procedures
    // ------------------------------------------------------------------

    /**
     * Fetch the collection of Procedure resources.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;ProcedureCollection&gt;
     */
    public ServiceRequest<Supplier<ProcedureCollection>> getProcedures(GetProcedures parameters) {
        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs("csv", PARAM_ID, parameters.getId()));
        queryParams.addAll(toPairs(PARAM_DATETIME, parameters.getDatetime()));
        queryParams.addAll(toPairs("csv", PARAM_Q, parameters.getKeyword()));
        queryParams.addAll(toPairs("csv", PARAM_OBSERVED_PROPERTY, parameters.getObservedProperty()));
        queryParams.addAll(toPairs("csv", PARAM_CONTROLLED_PROPERTY, parameters.getControlledProperty()));
        queryParams.addAll(toPairs(PARAM_LIMIT, parameters.getLimit()));
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_PROCEDURES, queryParams));
        request.header("Accept", ACCEPT_FEATURES);
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, ProcedureCollection.class);
    }

    /**
     * Create a new Procedure resource.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Void&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Void> createProcedure(CreateProcedure parameters) throws IllegalArgumentException {
        requireContent(parameters.getContent(), parameters.getContentType(), "createProcedure");
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_PROCEDURES, toPairs(PARAM_DRY_RUN, parameters.getDryRun())));
        request.header("Content-Type", parameters.getContentType());
        setPrefer(request, parameters.getHandling());
        request.method("POST", HttpRequest.BodyPublishers.ofByteArray(parameters.getContent()));
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    /**
     * Fetch a single Procedure resource.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Procedure&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<Procedure>> getProcedure(GetProcedure parameters) throws IllegalArgumentException {
        if (parameters.getProcedureId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'procedureId' when calling getProcedure");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_PROCEDURES + '/' + urlEncode(parameters.getProcedureId()), null));
        request.header("Accept", ACCEPT_FEATURES);
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, Procedure.class);
    }

    /**
     * Replace an existing Procedure resource.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Void&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Void> replaceProcedure(ReplaceProcedure parameters) throws IllegalArgumentException {
        if (parameters.getProcedureId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'procedureId' when calling replaceProcedure");
        }
        requireContent(parameters.getContent(), parameters.getContentType(), "replaceProcedure");
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_PROCEDURES + '/' + urlEncode(parameters.getProcedureId()), toPairs(PARAM_DRY_RUN, parameters.getDryRun())));
        request.header("Content-Type", parameters.getContentType());
        setPrefer(request, parameters.getHandling());
        request.method("PUT", HttpRequest.BodyPublishers.ofByteArray(parameters.getContent()));
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    /**
     * Delete an existing Procedure resource.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Void&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Void> deleteProcedure(DeleteProcedure parameters) throws IllegalArgumentException {
        if (parameters.getProcedureId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'procedureId' when calling deleteProcedure");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_PROCEDURES + '/' + urlEncode(parameters.getProcedureId()), null));
        request.method("DELETE", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    // ------------------------------------------------------------------
    // Sampling Features
    // ------------------------------------------------------------------

    /**
     * Fetch the collection of Sampling Feature resources.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;SamplingFeatureCollection&gt;
     */
    public ServiceRequest<Supplier<SamplingFeatureCollection>> getSamplingFeatures(GetSamplingFeatures parameters) {
        final List<Pair> queryParams = samplingFeatureFilterPairs(parameters.getId(), parameters.getBbox(),
                parameters.getDatetime(), parameters.getGeom(), parameters.getKeyword(), parameters.getFoi(),
                parameters.getObservedProperty(), parameters.getControlledProperty(), parameters.getLimit());
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_SAMPLING_FEATURES, queryParams));
        request.header("Accept", ACCEPT_FEATURES);
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, SamplingFeatureCollection.class);
    }

    /**
     * Fetch a single Sampling Feature resource.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;SamplingFeature&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<SamplingFeature>> getSamplingFeature(GetSamplingFeature parameters) throws IllegalArgumentException {
        if (parameters.getFeatureId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'featureId' when calling getSamplingFeature");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_SAMPLING_FEATURES + '/' + urlEncode(parameters.getFeatureId()), null));
        request.header("Accept", ACCEPT_FEATURES);
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, SamplingFeature.class);
    }

    /**
     * Replace an existing Sampling Feature resource.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Void&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Void> replaceSamplingFeature(ReplaceSamplingFeature parameters) throws IllegalArgumentException {
        if (parameters.getFeatureId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'featureId' when calling replaceSamplingFeature");
        }
        requireContent(parameters.getContent(), parameters.getContentType(), "replaceSamplingFeature");
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_SAMPLING_FEATURES + '/' + urlEncode(parameters.getFeatureId()), toPairs(PARAM_DRY_RUN, parameters.getDryRun())));
        request.header("Content-Type", parameters.getContentType());
        setPrefer(request, parameters.getHandling());
        request.method("PUT", HttpRequest.BodyPublishers.ofByteArray(parameters.getContent()));
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    /**
     * Delete an existing Sampling Feature resource.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Void&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Void> deleteSamplingFeature(DeleteSamplingFeature parameters) throws IllegalArgumentException {
        if (parameters.getFeatureId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'featureId' when calling deleteSamplingFeature");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_SAMPLING_FEATURES + '/' + urlEncode(parameters.getFeatureId()), null));
        request.method("DELETE", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    /**
     * Fetch the sampling features of a System resource.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;SamplingFeatureCollection&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<SamplingFeatureCollection>> getSystemSamplingFeatures(GetSystemSamplingFeatures parameters) throws IllegalArgumentException {
        if (parameters.getSystemId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'systemId' when calling getSystemSamplingFeatures");
        }
        final List<Pair> queryParams = samplingFeatureFilterPairs(parameters.getId(), parameters.getBbox(),
                parameters.getDatetime(), parameters.getGeom(), parameters.getKeyword(), parameters.getFoi(),
                parameters.getObservedProperty(), parameters.getControlledProperty(), parameters.getLimit());
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_SYSTEMS + '/' + urlEncode(parameters.getSystemId()) + PATH_SAMPLING_FEATURES, queryParams));
        request.header("Accept", ACCEPT_FEATURES);
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, SamplingFeatureCollection.class);
    }

    /**
     * Create a new sampling feature associated with a System resource.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Void&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Void> createSystemSamplingFeature(CreateSystemSamplingFeature parameters) throws IllegalArgumentException {
        if (parameters.getSystemId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'systemId' when calling createSystemSamplingFeature");
        }
        requireContent(parameters.getContent(), parameters.getContentType(), "createSystemSamplingFeature");
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_SYSTEMS + '/' + urlEncode(parameters.getSystemId()) + PATH_SAMPLING_FEATURES, toPairs(PARAM_DRY_RUN, parameters.getDryRun())));
        request.header("Content-Type", parameters.getContentType());
        setPrefer(request, parameters.getHandling());
        request.method("POST", HttpRequest.BodyPublishers.ofByteArray(parameters.getContent()));
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    // ------------------------------------------------------------------
    // Properties
    // ------------------------------------------------------------------

    /**
     * Fetch the collection of Property Definition resources.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;PropertyCollection&gt;
     */
    public ServiceRequest<Supplier<PropertyCollection>> getProperties(GetProperties parameters) {
        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs("csv", PARAM_ID, parameters.getId()));
        queryParams.addAll(toPairs("csv", PARAM_Q, parameters.getKeyword()));
        queryParams.addAll(toPairs("csv", PARAM_BASE_PROPERTY, parameters.getBaseProperty()));
        queryParams.addAll(toPairs("csv", PARAM_OBJECT_TYPE, parameters.getObjectType()));
        queryParams.addAll(toPairs(PARAM_LIMIT, parameters.getLimit()));
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_PROPERTIES, queryParams));
        request.header("Accept", ACCEPT_PROPERTIES);
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, PropertyCollection.class);
    }

    /**
     * Create a new Property Definition resource.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Void&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Void> createProperty(CreateProperty parameters) throws IllegalArgumentException {
        requireContent(parameters.getContent(), parameters.getContentType(), "createProperty");
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_PROPERTIES, toPairs(PARAM_DRY_RUN, parameters.getDryRun())));
        request.header("Content-Type", parameters.getContentType());
        setPrefer(request, parameters.getHandling());
        request.method("POST", HttpRequest.BodyPublishers.ofByteArray(parameters.getContent()));
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    /**
     * Fetch a single Property Definition resource.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;DerivedProperty&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<DerivedProperty>> getProperty(GetProperty parameters) throws IllegalArgumentException {
        if (parameters.getPropId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'propId' when calling getProperty");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_PROPERTIES + '/' + urlEncode(parameters.getPropId()), null));
        request.header("Accept", ACCEPT_PROPERTIES);
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, DerivedProperty.class);
    }

    /**
     * Replace an existing Property Definition resource.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Void&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Void> replaceProperty(ReplaceProperty parameters) throws IllegalArgumentException {
        if (parameters.getPropId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'propId' when calling replaceProperty");
        }
        requireContent(parameters.getContent(), parameters.getContentType(), "replaceProperty");
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_PROPERTIES + '/' + urlEncode(parameters.getPropId()), toPairs(PARAM_DRY_RUN, parameters.getDryRun())));
        request.header("Content-Type", parameters.getContentType());
        setPrefer(request, parameters.getHandling());
        request.method("PUT", HttpRequest.BodyPublishers.ofByteArray(parameters.getContent()));
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    /**
     * Delete an existing Property Definition resource.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Void&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Void> deleteProperty(DeleteProperty parameters) throws IllegalArgumentException {
        if (parameters.getPropId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'propId' when calling deleteProperty");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_PROPERTIES + '/' + urlEncode(parameters.getPropId()), null));
        request.method("DELETE", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private List<Pair> systemFilterPairs(List<String> id, org.opengis.geometry.Envelope bbox, String datetime,
            String geom, List<String> keyword, List<String> parent, List<String> procedure, List<String> foi,
            List<String> observedProperty, List<String> controlledProperty, Boolean recursive, Integer limit) {
        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs("csv", PARAM_ID, id));
        queryParams.addAll(toPairs("csv", PARAM_BBOX, toList(bbox)));
        queryParams.addAll(toPairs(PARAM_DATETIME, datetime));
        queryParams.addAll(toPairs(PARAM_GEOM, geom));
        queryParams.addAll(toPairs("csv", PARAM_Q, keyword));
        queryParams.addAll(toPairs("csv", PARAM_PARENT, parent));
        queryParams.addAll(toPairs("csv", PARAM_PROCEDURE, procedure));
        queryParams.addAll(toPairs("csv", PARAM_FOI, foi));
        queryParams.addAll(toPairs("csv", PARAM_OBSERVED_PROPERTY, observedProperty));
        queryParams.addAll(toPairs("csv", PARAM_CONTROLLED_PROPERTY, controlledProperty));
        queryParams.addAll(toPairs(PARAM_RECURSIVE, recursive));
        queryParams.addAll(toPairs(PARAM_LIMIT, limit));
        return queryParams;
    }

    private List<Pair> samplingFeatureFilterPairs(List<String> id, org.opengis.geometry.Envelope bbox, String datetime,
            String geom, List<String> keyword, List<String> foi, List<String> observedProperty,
            List<String> controlledProperty, Integer limit) {
        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs("csv", PARAM_ID, id));
        queryParams.addAll(toPairs("csv", PARAM_BBOX, toList(bbox)));
        queryParams.addAll(toPairs(PARAM_DATETIME, datetime));
        queryParams.addAll(toPairs(PARAM_GEOM, geom));
        queryParams.addAll(toPairs("csv", PARAM_Q, keyword));
        queryParams.addAll(toPairs("csv", PARAM_FOI, foi));
        queryParams.addAll(toPairs("csv", PARAM_OBSERVED_PROPERTY, observedProperty));
        queryParams.addAll(toPairs("csv", PARAM_CONTROLLED_PROPERTY, controlledProperty));
        queryParams.addAll(toPairs(PARAM_LIMIT, limit));
        return queryParams;
    }

    private static void requireContent(byte[] content, String contentType, String callingMethod) {
        if (content == null) {
            throw new IllegalArgumentException("Missing the required parameter 'content' when calling " + callingMethod);
        }
        if (contentType == null) {
            throw new IllegalArgumentException("Missing the required parameter 'contentType' when calling " + callingMethod);
        }
    }

    private static void setPrefer(HttpRequest.Builder request, org.geotoolkit.ogcapi.request.PreferHandling handling) {
        if (handling != null) {
            request.header("Prefer", handling.headerValue);
        }
    }

    private static List<Double> toList(org.opengis.geometry.Envelope env) {
        if (env == null) return null;
        final List<Double> lst = new ArrayList<>();
        for (double d : env.getLowerCorner().getCoordinates()) lst.add(d);
        for (double d : env.getUpperCorner().getCoordinates()) lst.add(d);
        return lst;
    }

}
