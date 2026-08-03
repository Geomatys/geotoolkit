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

import java.util.Optional;
import org.geotoolkit.ogcapi.client.AbstractApiTest;
import org.geotoolkit.ogcapi.dto.processes.Execute;
import org.geotoolkit.ogcapi.request.processes.ExecuteProcess;
import org.geotoolkit.ogcapi.request.processes.GetJob;
import org.geotoolkit.ogcapi.request.processes.GetProcess;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class ProcessesApiTest extends AbstractApiTest {

    /**
     * Testing {@linkplain ProcessesApi#getProcesses() };
     * Request the list of available processes.
     */
    @Test
    public void testGetProcesses() {
        final var api = new ProcessesApi(newConfig());

        { //check valid request
            final var serviceRequest = api.getProcesses();
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/processes", request.uri().toString());
            assertEquals(Optional.of("application/json"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain ProcessesApi#getProcess(org.geotoolkit.ogcapi.request.processes.GetProcess) };
     * Request the description of a process `buffer process`.
     */
    @Test
    public void testGetProcess() {
        final var api = new ProcessesApi(newConfig());

        { // check incorrect request : missing processId
            final var parameters = new GetProcess();
            assertThrows(IllegalArgumentException.class, () -> api.getProcess(parameters));
        }

        { // check valid request
            final var parameters = new GetProcess()
                    .processId("buffer process");
            final var serviceRequest = api.getProcess(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/processes/buffer%20process", request.uri().toString());
            assertEquals(Optional.of("application/json"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain ProcessesApi#executeSync(org.geotoolkit.ogcapi.request.processes.ExecuteProcess) };
     * Execute a process `raster calc` synchronously and return its inline results.
     */
    @Test
    public void testExecuteSync() {
        final var api = new ProcessesApi(newConfig());

        { // check incorrect request : missing processId
            final var parameters = new ExecuteProcess()
                    .body(new Execute().putInputsItem("threshold", 0.5));
            assertThrows(IllegalArgumentException.class, () -> api.executeSync(parameters));
        }

        { // check valid request
            final var parameters = new ExecuteProcess()
                    .processId("raster calc")
                    .body(new Execute().putInputsItem("threshold", 0.5));
            final var serviceRequest = api.executeSync(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("POST", request.method());
            assertEquals("http://localhost:8080/ogcapi/processes/raster%20calc/execution", request.uri().toString());
            assertEquals(Optional.of("application/json"), request.headers().firstValue("Accept"));
            assertEquals(Optional.of("application/json"), request.headers().firstValue("Content-Type"));
            assertTrue(request.bodyPublisher().isPresent());
        }
    }

    /**
     * Testing {@linkplain ProcessesApi#executeAsync(org.geotoolkit.ogcapi.request.processes.ExecuteProcess) };
     * Execute a process `ndvi:async` asynchronously, creating a job.
     */
    @Test
    public void testExecuteAsync() {
        final var api = new ProcessesApi(newConfig());

        { // check incorrect request : missing processId
            final var parameters = new ExecuteProcess()
                    .body(new Execute().putInputsItem("threshold", 0.5));
            assertThrows(IllegalArgumentException.class, () -> api.executeAsync(parameters));
        }

        { // check valid request
            final var parameters = new ExecuteProcess()
                    .processId("ndvi:async")
                    .body(new Execute().putInputsItem("threshold", 0.5));
            final var serviceRequest = api.executeAsync(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("POST", request.method());
            assertEquals("http://localhost:8080/ogcapi/processes/ndvi%3Aasync/execution", request.uri().toString());
            assertEquals(Optional.of("application/json"), request.headers().firstValue("Accept"));
            assertEquals(Optional.of("application/json"), request.headers().firstValue("Content-Type"));
            assertEquals(Optional.of("respond-async"), request.headers().firstValue("Prefer"));
            assertTrue(request.bodyPublisher().isPresent());
        }
    }

    /**
     * Testing {@linkplain ProcessesApi#getJob(org.geotoolkit.ogcapi.request.processes.GetJob) };
     * Request the status of a job `job-2024-001`.
     */
    @Test
    public void testGetJob() {
        final var api = new ProcessesApi(newConfig());

        { // check incorrect request : missing jobId
            final var parameters = new GetJob();
            assertThrows(IllegalArgumentException.class, () -> api.getJob(parameters));
        }

        { // check valid request
            final var parameters = new GetJob()
                    .jobId("job-2024-001");
            final var serviceRequest = api.getJob(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/jobs/job-2024-001", request.uri().toString());
            assertEquals(Optional.of("application/json"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain ProcessesApi#getJobResults(org.geotoolkit.ogcapi.request.processes.GetJob) };
     * Request the results of a finished job `job 42`.
     */
    @Test
    public void testGetJobResults() {
        final var api = new ProcessesApi(newConfig());

        { // check incorrect request : missing jobId
            final var parameters = new GetJob();
            assertThrows(IllegalArgumentException.class, () -> api.getJobResults(parameters));
        }

        { // check valid request
            final var parameters = new GetJob()
                    .jobId("job 42");
            final var serviceRequest = api.getJobResults(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/jobs/job%2042/results", request.uri().toString());
            assertEquals(Optional.of("application/json"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain ProcessesApi#dismissJob(org.geotoolkit.ogcapi.request.processes.GetJob) };
     * Cancel a running job `job/99`, or delete its finished results.
     */
    @Test
    public void testDismissJob() {
        final var api = new ProcessesApi(newConfig());

        { // check incorrect request : missing jobId
            final var parameters = new GetJob();
            assertThrows(IllegalArgumentException.class, () -> api.dismissJob(parameters));
        }

        { // check valid request
            final var parameters = new GetJob()
                    .jobId("job/99");
            final var serviceRequest = api.dismissJob(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("DELETE", request.method());
            assertEquals("http://localhost:8080/ogcapi/jobs/job%2F99", request.uri().toString());
            assertEquals(Optional.of("application/json"), request.headers().firstValue("Accept"));
        }
    }
}
