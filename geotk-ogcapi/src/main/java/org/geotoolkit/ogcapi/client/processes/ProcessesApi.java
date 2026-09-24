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
package org.geotoolkit.ogcapi.client.processes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import java.net.http.HttpRequest;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.geotoolkit.client.openapi.AbstractOpenApi;
import org.geotoolkit.client.openapi.OpenApiConfiguration;
import org.geotoolkit.client.service.ServiceRequest;
import org.geotoolkit.ogcapi.dto.processes.ProcessDescription;
import org.geotoolkit.ogcapi.dto.processes.ProcessList;
import org.geotoolkit.ogcapi.dto.processes.StatusInfo;
import org.geotoolkit.ogcapi.request.processes.ExecuteProcess;
import org.geotoolkit.ogcapi.request.processes.GetJob;
import org.geotoolkit.ogcapi.request.processes.GetProcess;

/**
 * OGC API - Processes - Part 1: Core client bindings.
 *
 * @author Quentin BIALOTA (Geomatys)
 * @see <a href="https://docs.ogc.org/is/18-062r2/18-062r2.html">OGC API - Processes - Part 1: Core</a>
 */
public final class ProcessesApi extends AbstractOpenApi {

    /**
     * Path fragments.
     */
    public static final String PATH_EXECUTION = "/execution";
    public static final String PATH_JOBS      = "/jobs";
    public static final String PATH_PROCESSES = "/processes";
    public static final String PATH_RESULTS   = "/results";

    public ProcessesApi(OpenApiConfiguration config) {
        super(config);
    }

    /**
     * Retrieve the list of processes available from this service.
     *
     * @return ServiceRequest&lt;Supplier&lt;ProcessList&gt;&gt;
     */
    public ServiceRequest<Supplier<ProcessList>> getProcesses() {
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_PROCESSES, List.of()));
        request.header("Accept", "application/json");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, ProcessList.class);
    }

    /**
     * Retrieve the description of a process.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Supplier&lt;ProcessDescription&gt;&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<ProcessDescription>> getProcess(GetProcess parameters) throws IllegalArgumentException {
        if (parameters.getProcessId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'processId' when calling getProcess");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_PROCESSES + '/' + urlEncode(parameters.getProcessId()), List.of()));
        request.header("Accept", "application/json");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, ProcessDescription.class);
    }

    /**
     * Execute a process synchronously and return its results inline.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Supplier&lt;Map&lt;String, Object&gt;&gt;&gt; results, keyed by output id
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<Map<String, Object>>> executeSync(ExecuteProcess parameters) throws IllegalArgumentException {
        final HttpRequest.Builder request = executeRequest(parameters);
        return requestAsJson(request, new TypeReference<Map<String, Object>>() {});
    }

    /**
     * Execute a process asynchronously (job creation).
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Supplier&lt;StatusInfo&gt;&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<StatusInfo>> executeAsync(ExecuteProcess parameters) throws IllegalArgumentException {
        final HttpRequest.Builder request = executeRequest(parameters);
        request.header("Prefer", "respond-async");
        return requestAsJson(request, StatusInfo.class);
    }

    private HttpRequest.Builder executeRequest(ExecuteProcess parameters) throws IllegalArgumentException {
        if (parameters.getProcessId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'processId' when calling execute");
        }
        if (parameters.getBody() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'body' when calling execute");
        }
        final byte[] bodyBytes;
        try {
            bodyBytes = objectMapper.writeValueAsBytes(parameters.getBody());
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to serialize execute body", e);
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_PROCESSES + '/' + urlEncode(parameters.getProcessId()) + PATH_EXECUTION, List.of()));
        request.header("Accept", "application/json");
        request.header("Content-Type", "application/json");
        request.method("POST", HttpRequest.BodyPublishers.ofByteArray(bodyBytes));
        setConfig(request);
        return request;
    }

    /**
     * Retrieve the status of a job.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Supplier&lt;StatusInfo&gt;&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<StatusInfo>> getJob(GetJob parameters) throws IllegalArgumentException {
        if (parameters.getJobId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'jobId' when calling getJob");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_JOBS + '/' + urlEncode(parameters.getJobId()), List.of()));
        request.header("Accept", "application/json");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, StatusInfo.class);
    }

    /**
     * Retrieve the results of a finished job.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Supplier&lt;Map&lt;String, Object&gt;&gt;&gt; results, keyed by output id
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<Map<String, Object>>> getJobResults(GetJob parameters) throws IllegalArgumentException {
        if (parameters.getJobId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'jobId' when calling getJobResults");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_JOBS + '/' + urlEncode(parameters.getJobId()) + PATH_RESULTS, List.of()));
        request.header("Accept", "application/json");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, new TypeReference<Map<String, Object>>() {});
    }

    /**
     * Cancel a running job, or delete the finished job's results.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Supplier&lt;StatusInfo&gt;&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<StatusInfo>> dismissJob(GetJob parameters) throws IllegalArgumentException {
        if (parameters.getJobId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'jobId' when calling dismissJob");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_JOBS + '/' + urlEncode(parameters.getJobId()), List.of()));
        request.header("Accept", "application/json");
        request.method("DELETE", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, StatusInfo.class);
    }
}
