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
import com.fasterxml.jackson.core.type.TypeReference;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.geotoolkit.client.openapi.AbstractOpenApi;
import org.geotoolkit.client.openapi.OpenApiConfiguration;
import org.geotoolkit.client.service.Pair;
import org.geotoolkit.client.service.ServiceRequest;
import org.geotoolkit.openeo.dto.process.Job;
import org.geotoolkit.openeo.dto.process.JobEstimate;
import org.geotoolkit.openeo.dto.process.JobResults;
import org.geotoolkit.openeo.dto.process.Jobs;
import org.geotoolkit.openeo.dto.process.LogEntries;
import org.geotoolkit.openeo.dto.process.Process;
import org.geotoolkit.openeo.dto.process.Processes;
import org.geotoolkit.openeo.request.job.CancelJob;
import org.geotoolkit.openeo.request.job.CreateJob;
import org.geotoolkit.openeo.request.job.DeleteJob;
import org.geotoolkit.openeo.request.job.GetJob;
import org.geotoolkit.openeo.request.job.GetJobEstimate;
import org.geotoolkit.openeo.request.job.GetJobLogs;
import org.geotoolkit.openeo.request.job.GetJobResults;
import org.geotoolkit.openeo.request.job.GetJobs;
import org.geotoolkit.openeo.request.job.StartJob;
import org.geotoolkit.openeo.request.job.UpdateJob;
import org.geotoolkit.openeo.request.process.DeleteProcess;
import org.geotoolkit.openeo.request.process.ExecuteSync;
import org.geotoolkit.openeo.request.process.GetProcess;
import org.geotoolkit.openeo.request.process.GetProcesses;
import org.geotoolkit.openeo.request.process.GetProcessGraphs;
import org.geotoolkit.openeo.request.process.UpsertProcess;
import org.geotoolkit.openeo.request.process.ValidateProcess;

/**
 * OpenEO Processing API client.
 * Covers: process graph management and batch job lifecycle.
 *
 * @author Quentin BIALOTA (Geomatys)
 * Based on : <a href="https://api.openeo.org/#tag/Process-Graphs">OpenEO Doc</a>
 */
public final class ProcessingApi extends AbstractOpenApi {

    public ProcessingApi(OpenApiConfiguration config) {
        super(config);
    }

    // -------------------------------------------------------------------------
    // Processes
    // -------------------------------------------------------------------------

    /**
     * List all pre-defined processes available on the back-end.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Supplier&lt;Processes&gt;&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<Processes>> getProcesses(GetProcesses parameters) throws IllegalArgumentException {
        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs("limit", parameters.getLimit()));
        queryParams.addAll(toPairs("namespace", parameters.getNamespace()));
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/processes", queryParams));
        request.header("Accept", "application/json");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, Processes.class);
    }

    /**
     * Get a specific user-defined process graph.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Supplier&lt;Process&gt;&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<Process>> getProcess(GetProcess parameters) throws IllegalArgumentException {
        if (parameters.getProcessId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'processId' when calling getProcess");
        }
        final List<Pair> queryParams = new ArrayList<>(toPairs("namespace", parameters.getNamespace()));
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/process_graphs/" + urlEncode(parameters.getProcessId()), queryParams));
        request.header("Accept", "application/json");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, Process.class);
    }

    /**
     * Store or update a user-defined process on the back-end.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Void&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Void> upsertProcess(UpsertProcess parameters) throws IllegalArgumentException {
        if (parameters.getProcessId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'processId' when calling upsertProcess");
        }
        final byte[] bodyBytes;
        try {
            bodyBytes = objectMapper.writeValueAsBytes(parameters.getBody());
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to serialize process body", e);
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/process_graphs/" + urlEncode(parameters.getProcessId()), List.of()));
        request.header("Accept", "application/json");
        request.header("Content-Type", "application/json");
        request.method("PUT", HttpRequest.BodyPublishers.ofByteArray(bodyBytes));
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    /**
     * Delete a user-defined process.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Void&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Void> deleteProcess(DeleteProcess parameters) throws IllegalArgumentException {
        if (parameters.getProcessId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'processId' when calling deleteProcess");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/process_graphs/" + urlEncode(parameters.getProcessId()), List.of()));
        request.header("Accept", "application/json");
        request.method("DELETE", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    // -------------------------------------------------------------------------
    // Jobs
    // -------------------------------------------------------------------------

    /**
     * List all batch jobs of the current user.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Supplier&lt;Jobs&gt;&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<Jobs>> getJobs(GetJobs parameters) throws IllegalArgumentException {
        final List<Pair> queryParams = new ArrayList<>(toPairs("limit", parameters.getLimit()));
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/jobs", queryParams));
        request.header("Accept", "application/json");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, Jobs.class);
    }

    /**
     * Create a new batch job.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Void&gt; (201 No Content)
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Void> createJob(CreateJob parameters) throws IllegalArgumentException {
        if (parameters.getBody() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'body' when calling createJob");
        }
        final byte[] bodyBytes;
        try {
            bodyBytes = objectMapper.writeValueAsBytes(parameters.getBody());
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to serialize job body", e);
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/jobs", List.of()));
        request.header("Accept", "application/json");
        request.header("Content-Type", "application/json");
        request.method("POST", HttpRequest.BodyPublishers.ofByteArray(bodyBytes));
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    /**
     * Full information about a batch job.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Supplier&lt;Job&gt;&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<Job>> getJob(GetJob parameters) throws IllegalArgumentException {
        if (parameters.getJobId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'jobId' when calling getJob");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/jobs/" + urlEncode(parameters.getJobId()), List.of()));
        request.header("Accept", "application/json");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, Job.class);
    }

    /**
     * Modify a batch job.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Void&gt; (204 No Content)
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Void> updateJob(UpdateJob parameters) throws IllegalArgumentException {
        if (parameters.getJobId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'jobId' when calling updateJob");
        }
        final byte[] bodyBytes;
        try {
            bodyBytes = objectMapper.writeValueAsBytes(parameters.getBody());
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to serialize job update body", e);
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/jobs/" + urlEncode(parameters.getJobId()), List.of()));
        request.header("Accept", "application/json");
        request.header("Content-Type", "application/json");
        request.method("PATCH", HttpRequest.BodyPublishers.ofByteArray(bodyBytes));
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    /**
     * Delete a batch job.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Void&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Void> deleteJob(DeleteJob parameters) throws IllegalArgumentException {
        if (parameters.getJobId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'jobId' when calling deleteJob");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/jobs/" + urlEncode(parameters.getJobId()), List.of()));
        request.header("Accept", "application/json");
        request.method("DELETE", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    /**
     * Queue a batch job for processing (POST /jobs/{jobId}/results).
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Void&gt; (202 Accepted)
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Void> startJob(StartJob parameters) throws IllegalArgumentException {
        if (parameters.getJobId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'jobId' when calling startJob");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/jobs/" + urlEncode(parameters.getJobId()) + "/results", List.of()));
        request.header("Accept", "application/json");
        request.method("POST", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    /**
     * Cancel processing a batch job (DELETE /jobs/{jobId}/results).
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Void&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Void> cancelJob(CancelJob parameters) throws IllegalArgumentException {
        if (parameters.getJobId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'jobId' when calling cancelJob");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/jobs/" + urlEncode(parameters.getJobId()) + "/results", List.of()));
        request.header("Accept", "application/json");
        request.method("DELETE", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    /**
     * Get results of a finished batch job as a STAC FeatureCollection.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Supplier&lt;JobResults&gt;&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<JobResults>> getJobResults(GetJobResults parameters) throws IllegalArgumentException {
        if (parameters.getJobId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'jobId' when calling getJobResults");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/jobs/" + urlEncode(parameters.getJobId()) + "/results", List.of()));
        request.header("Accept", "application/json");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, JobResults.class);
    }

    /**
     * Get logs for a batch job.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Supplier&lt;LogEntries&gt;&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<LogEntries>> getJobLogs(GetJobLogs parameters) throws IllegalArgumentException {
        if (parameters.getJobId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'jobId' when calling getJobLogs");
        }
        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs("offset", parameters.getOffset()));
        queryParams.addAll(toPairs("limit", parameters.getLimit()));
        queryParams.addAll(toPairs("level", parameters.getLevel()));
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/jobs/" + urlEncode(parameters.getJobId()) + "/logs", queryParams));
        request.header("Accept", "application/json");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, LogEntries.class);
    }

    // -------------------------------------------------------------------------
    // User-defined process graphs (listing)
    // -------------------------------------------------------------------------

    /**
     * List all user-defined process graphs stored on the back-end.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Supplier&lt;Processes&gt;&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<Processes>> getProcessGraphs(GetProcessGraphs parameters) throws IllegalArgumentException {
        final List<Pair> queryParams = new ArrayList<>(toPairs("limit", parameters.getLimit()));
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/process_graphs", queryParams));
        request.header("Accept", "application/json");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, Processes.class);
    }

    // -------------------------------------------------------------------------
    // Validation and synchronous execution
    // -------------------------------------------------------------------------

    /**
     * Validate a process graph without executing it.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Supplier&lt;List&lt;Map&lt;String, Object&gt;&gt;&gt;&gt; validation errors (empty = valid)
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<List<Map<String, Object>>>> validateProcess(ValidateProcess parameters) throws IllegalArgumentException {
        if (parameters.getBody() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'body' when calling validateProcess");
        }
        final byte[] bodyBytes;
        try {
            bodyBytes = objectMapper.writeValueAsBytes(parameters.getBody());
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to serialize process body", e);
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/validation", List.of()));
        request.header("Accept", "application/json");
        request.header("Content-Type", "application/json");
        request.method("POST", HttpRequest.BodyPublishers.ofByteArray(bodyBytes));
        setConfig(request);
        return requestAsJsonField(request, "errors", new TypeReference<List<Map<String, Object>>>() {});
    }

    /**
     * Execute a process graph synchronously and stream back the result file.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;byte[]&gt; raw result bytes
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<byte[]> executeSync(ExecuteSync parameters) throws IllegalArgumentException {
        if (parameters.getBody() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'body' when calling executeSync");
        }
        final byte[] bodyBytes;
        try {
            bodyBytes = objectMapper.writeValueAsBytes(parameters.getBody());
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to serialize process body", e);
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/result", List.of()));
        request.header("Accept", "*/*");
        request.header("Content-Type", "application/json");
        request.method("POST", HttpRequest.BodyPublishers.ofByteArray(bodyBytes));
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.ofByteArray());
    }

    /**
     * Get cost and resource estimates for a batch job.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Supplier&lt;JobEstimate&gt;&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<JobEstimate>> getJobEstimate(GetJobEstimate parameters) throws IllegalArgumentException {
        if (parameters.getJobId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'jobId' when calling getJobEstimate");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/jobs/" + urlEncode(parameters.getJobId()) + "/estimate", List.of()));
        request.header("Accept", "application/json");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, JobEstimate.class);
    }
}
