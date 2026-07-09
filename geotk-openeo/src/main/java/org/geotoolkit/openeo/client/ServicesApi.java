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

import com.fasterxml.jackson.core.JsonProcessingException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import org.geotoolkit.client.openapi.AbstractOpenApi;
import org.geotoolkit.client.openapi.OpenApiConfiguration;
import org.geotoolkit.client.service.Pair;
import org.geotoolkit.client.service.ServiceRequest;
import org.geotoolkit.openeo.dto.process.LogEntries;
import org.geotoolkit.openeo.request.service.CreateService;
import org.geotoolkit.openeo.request.service.DeleteService;
import org.geotoolkit.openeo.request.service.GetService;
import org.geotoolkit.openeo.request.service.GetServiceLogs;
import org.geotoolkit.openeo.request.service.GetServices;
import org.geotoolkit.openeo.request.service.UpdateService;
import org.geotoolkit.openeo.dto.service.Service;
import org.geotoolkit.openeo.dto.service.ServiceList;

/**
 * OpenEO Secondary Services API client.
 * Covers: listing, creating, describing, updating, deleting secondary web services and retrieving their logs.
 *
 * @author Quentin BIALOTA (Geomatys)
 * Based on : <a href="https://api.openeo.org/#tag/Secondary-Services">OpenEO Doc</a>
 */
public final class ServicesApi extends AbstractOpenApi {

    public ServicesApi(OpenApiConfiguration config) {
        super(config);
    }

    /**
     * List all secondary web services owned by the authenticated user.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Supplier&lt;ServiceList&gt;&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<ServiceList>> getServices(GetServices parameters) throws IllegalArgumentException {
        final List<Pair> queryParams = new ArrayList<>(toPairs("limit", parameters.getLimit()));
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/services", queryParams));
        request.header("Accept", "application/json");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, ServiceList.class);
    }

    /**
     * Create a new secondary web service.
     * The back-end responds with 201 Created and a Location header.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Void&gt; (201 Created)
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Void> createService(CreateService parameters) throws IllegalArgumentException {
        if (parameters.getBody() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'body' when calling createService");
        }
        final byte[] bodyBytes;
        try {
            bodyBytes = objectMapper.writeValueAsBytes(parameters.getBody());
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to serialize service body", e);
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/services", List.of()));
        request.header("Accept", "application/json");
        request.header("Content-Type", "application/json");
        request.method("POST", HttpRequest.BodyPublishers.ofByteArray(bodyBytes));
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    /**
     * Retrieve full metadata for a secondary web service.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Supplier&lt;Service&gt;&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<Service>> getService(GetService parameters) throws IllegalArgumentException {
        if (parameters.getServiceId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'serviceId' when calling getService");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/services/" + urlEncode(parameters.getServiceId()), List.of()));
        request.header("Accept", "application/json");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, Service.class);
    }

    /**
     * Update metadata or configuration of an existing secondary web service.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Void&gt; (204 No Content)
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Void> updateService(UpdateService parameters) throws IllegalArgumentException {
        if (parameters.getServiceId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'serviceId' when calling updateService");
        }
        final byte[] bodyBytes;
        try {
            bodyBytes = objectMapper.writeValueAsBytes(parameters.getBody());
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to serialize service update body", e);
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/services/" + urlEncode(parameters.getServiceId()), List.of()));
        request.header("Accept", "application/json");
        request.header("Content-Type", "application/json");
        request.method("PATCH", HttpRequest.BodyPublishers.ofByteArray(bodyBytes));
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    /**
     * Delete a secondary web service and stop it if running.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Void&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Void> deleteService(DeleteService parameters) throws IllegalArgumentException {
        if (parameters.getServiceId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'serviceId' when calling deleteService");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/services/" + urlEncode(parameters.getServiceId()), List.of()));
        request.header("Accept", "application/json");
        request.method("DELETE", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    /**
     * Retrieve log entries for a secondary web service.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Supplier&lt;LogEntries&gt;&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<LogEntries>> getServiceLogs(GetServiceLogs parameters) throws IllegalArgumentException {
        if (parameters.getServiceId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'serviceId' when calling getServiceLogs");
        }
        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs("offset", parameters.getOffset()));
        queryParams.addAll(toPairs("limit", parameters.getLimit()));
        queryParams.addAll(toPairs("level", parameters.getLevel()));
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/services/" + urlEncode(parameters.getServiceId()) + "/logs", queryParams));
        request.header("Accept", "application/json");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, LogEntries.class);
    }
}
