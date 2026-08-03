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

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import org.geotoolkit.ogcapi.client.AbstractApiTest;
import org.geotoolkit.ogcapi.request.PreferHandling;
import org.geotoolkit.ogcapi.request.connectedsystems.CreateCommand;
import org.geotoolkit.ogcapi.request.connectedsystems.CreateCommandResult;
import org.geotoolkit.ogcapi.request.connectedsystems.CreateCommandStatus;
import org.geotoolkit.ogcapi.request.connectedsystems.CreateControlStream;
import org.geotoolkit.ogcapi.request.connectedsystems.CreateDataStream;
import org.geotoolkit.ogcapi.request.connectedsystems.CreateObservation;
import org.geotoolkit.ogcapi.request.connectedsystems.CreateSystemEvent;
import org.geotoolkit.ogcapi.request.connectedsystems.DeleteCommand;
import org.geotoolkit.ogcapi.request.connectedsystems.DeleteCommandResult;
import org.geotoolkit.ogcapi.request.connectedsystems.DeleteCommandStatus;
import org.geotoolkit.ogcapi.request.connectedsystems.DeleteControlStream;
import org.geotoolkit.ogcapi.request.connectedsystems.DeleteDataStream;
import org.geotoolkit.ogcapi.request.connectedsystems.DeleteObservation;
import org.geotoolkit.ogcapi.request.connectedsystems.DeleteSystemEvent;
import org.geotoolkit.ogcapi.request.connectedsystems.DeleteSystemHistoryRevision;
import org.geotoolkit.ogcapi.request.connectedsystems.GetAllSystemEvents;
import org.geotoolkit.ogcapi.request.connectedsystems.GetCommand;
import org.geotoolkit.ogcapi.request.connectedsystems.GetCommandResult;
import org.geotoolkit.ogcapi.request.connectedsystems.GetCommandResults;
import org.geotoolkit.ogcapi.request.connectedsystems.GetCommandStatus;
import org.geotoolkit.ogcapi.request.connectedsystems.GetCommandStatuses;
import org.geotoolkit.ogcapi.request.connectedsystems.GetCommands;
import org.geotoolkit.ogcapi.request.connectedsystems.GetControlStream;
import org.geotoolkit.ogcapi.request.connectedsystems.GetControlStreamSchema;
import org.geotoolkit.ogcapi.request.connectedsystems.GetControlStreams;
import org.geotoolkit.ogcapi.request.connectedsystems.GetDataStream;
import org.geotoolkit.ogcapi.request.connectedsystems.GetDataStreamSchema;
import org.geotoolkit.ogcapi.request.connectedsystems.GetDataStreams;
import org.geotoolkit.ogcapi.request.connectedsystems.GetObservation;
import org.geotoolkit.ogcapi.request.connectedsystems.GetObservations;
import org.geotoolkit.ogcapi.request.connectedsystems.GetSystemEvent;
import org.geotoolkit.ogcapi.request.connectedsystems.GetSystemEvents;
import org.geotoolkit.ogcapi.request.connectedsystems.GetSystemHistory;
import org.geotoolkit.ogcapi.request.connectedsystems.GetSystemHistoryRevision;
import org.geotoolkit.ogcapi.request.connectedsystems.ReplaceCommand;
import org.geotoolkit.ogcapi.request.connectedsystems.ReplaceCommandResult;
import org.geotoolkit.ogcapi.request.connectedsystems.ReplaceCommandStatus;
import org.geotoolkit.ogcapi.request.connectedsystems.ReplaceControlStream;
import org.geotoolkit.ogcapi.request.connectedsystems.ReplaceControlStreamSchema;
import org.geotoolkit.ogcapi.request.connectedsystems.ReplaceDataStream;
import org.geotoolkit.ogcapi.request.connectedsystems.ReplaceDataStreamSchema;
import org.geotoolkit.ogcapi.request.connectedsystems.ReplaceObservation;
import org.geotoolkit.ogcapi.request.connectedsystems.ReplaceSystemEvent;
import org.geotoolkit.ogcapi.request.connectedsystems.ReplaceSystemHistoryRevision;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class ConnectedSystemsDynamicApiTest extends AbstractApiTest {

    private static final String ACCEPT_JSON = "application/json, text/html";
    private static final String ACCEPT_SYSTEM = "application/geo+json, application/json, text/html";

    // ------------------------------------------------------------------
    // DataStreams
    // ------------------------------------------------------------------

    /**
     * Testing {@linkplain ConnectedSystemsDynamicApi#getDataStreams(org.geotoolkit.ogcapi.request.connectedsystems.GetDataStreams) };
     */
    @Test
    public void testGetDataStreams() {
        final var api = new ConnectedSystemsDynamicApi(newConfig());

        { //check valid request
            final var parameters = new GetDataStreams()
                    .id(List.of("ds1", "ds2"))
                    .q(List.of("my sensor"))
                    .phenomenonTime("2018-02-12T00:00:00Z")
                    .resultTime("2019-05-01T00:00:00Z")
                    .system(List.of("sys1"))
                    .foi(List.of("foi1"))
                    .observedProperty(List.of("temp"))
                    .limit(10);
            final var serviceRequest = api.getDataStreams(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/datastreams"
                    + "?id=ds1%2Cds2"
                    + "&q=my%20sensor"
                    + "&phenomenonTime=2018-02-12T00%3A00%3A00Z"
                    + "&resultTime=2019-05-01T00%3A00%3A00Z"
                    + "&system=sys1"
                    + "&foi=foi1"
                    + "&observedProperty=temp"
                    + "&limit=10", request.uri().toString());
            assertEquals(Optional.of(ACCEPT_JSON), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain ConnectedSystemsDynamicApi#createDataStream(org.geotoolkit.ogcapi.request.connectedsystems.CreateDataStream) };
     */
    @Test
    public void testCreateDataStream() {
        final var api = new ConnectedSystemsDynamicApi(newConfig());

        { //check incorrect request : missing systemId
            final var parameters = new CreateDataStream();
            assertThrows(IllegalArgumentException.class, () -> api.createDataStream(parameters));
        }

        { //check valid request
            final byte[] content = "{}".getBytes(StandardCharsets.UTF_8);
            final var parameters = new CreateDataStream()
                    .systemId("sys 1")
                    .content(content)
                    .contentType("application/json")
                    .dryRun(true)
                    .handling(PreferHandling.STRICT);
            final var serviceRequest = api.createDataStream(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("POST", request.method());
            assertEquals("http://localhost:8080/ogcapi/systems/sys%201/datastreams"
                    + "?dry-run=true", request.uri().toString());
            assertEquals(Optional.of("application/json"), request.headers().firstValue("Content-Type"));
            assertEquals(Optional.of("handling=strict"), request.headers().firstValue("Prefer"));
            assertEquals(content.length, request.bodyPublisher().get().contentLength());
        }
    }

    /**
     * Testing {@linkplain ConnectedSystemsDynamicApi#getDataStream(org.geotoolkit.ogcapi.request.connectedsystems.GetDataStream) };
     */
    @Test
    public void testGetDataStream() {
        final var api = new ConnectedSystemsDynamicApi(newConfig());

        { //check incorrect request : missing dataStreamId
            final var parameters = new GetDataStream();
            assertThrows(IllegalArgumentException.class, () -> api.getDataStream(parameters));
        }

        { //check valid request
            final var parameters = new GetDataStream()
                    .dataStreamId("ds:1");
            final var serviceRequest = api.getDataStream(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/datastreams/ds%3A1", request.uri().toString());
            assertEquals(Optional.of(ACCEPT_JSON), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain ConnectedSystemsDynamicApi#replaceDataStream(org.geotoolkit.ogcapi.request.connectedsystems.ReplaceDataStream) };
     */
    @Test
    public void testReplaceDataStream() {
        final var api = new ConnectedSystemsDynamicApi(newConfig());

        { //check incorrect request : missing dataStreamId
            final var parameters = new ReplaceDataStream();
            assertThrows(IllegalArgumentException.class, () -> api.replaceDataStream(parameters));
        }

        { //check valid request
            final byte[] content = "{}".getBytes(StandardCharsets.UTF_8);
            final var parameters = new ReplaceDataStream()
                    .dataStreamId("ds1")
                    .content(content)
                    .contentType("application/json")
                    .dryRun(false)
                    .handling(PreferHandling.LENIENT);
            final var serviceRequest = api.replaceDataStream(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("PUT", request.method());
            assertEquals("http://localhost:8080/ogcapi/datastreams/ds1"
                    + "?dry-run=false", request.uri().toString());
            assertEquals(Optional.of("application/json"), request.headers().firstValue("Content-Type"));
            assertEquals(Optional.of("handling=lenient"), request.headers().firstValue("Prefer"));
            assertEquals(content.length, request.bodyPublisher().get().contentLength());
        }
    }

    /**
     * Testing {@linkplain ConnectedSystemsDynamicApi#deleteDataStream(org.geotoolkit.ogcapi.request.connectedsystems.DeleteDataStream) };
     */
    @Test
    public void testDeleteDataStream() {
        final var api = new ConnectedSystemsDynamicApi(newConfig());

        { //check incorrect request : missing dataStreamId
            final var parameters = new DeleteDataStream();
            assertThrows(IllegalArgumentException.class, () -> api.deleteDataStream(parameters));
        }

        { //check valid request
            final var parameters = new DeleteDataStream()
                    .dataStreamId("ds1")
                    .cascade(true);
            final var serviceRequest = api.deleteDataStream(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("DELETE", request.method());
            assertEquals("http://localhost:8080/ogcapi/datastreams/ds1"
                    + "?cascade=true", request.uri().toString());
        }
    }

    /**
     * Testing {@linkplain ConnectedSystemsDynamicApi#getDataStreamSchema(org.geotoolkit.ogcapi.request.connectedsystems.GetDataStreamSchema) };
     */
    @Test
    public void testGetDataStreamSchema() {
        final var api = new ConnectedSystemsDynamicApi(newConfig());

        { //check incorrect request : missing dataStreamId
            final var parameters = new GetDataStreamSchema();
            assertThrows(IllegalArgumentException.class, () -> api.getDataStreamSchema(parameters));
        }

        { //check valid request
            final var parameters = new GetDataStreamSchema()
                    .dataStreamId("ds1")
                    .obsFormat("application/om+json");
            final var serviceRequest = api.getDataStreamSchema(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/datastreams/ds1/schema"
                    + "?obsFormat=application%2Fom%2Bjson", request.uri().toString());
            assertEquals(Optional.of(ACCEPT_JSON), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain ConnectedSystemsDynamicApi#replaceDataStreamSchema(org.geotoolkit.ogcapi.request.connectedsystems.ReplaceDataStreamSchema) };
     */
    @Test
    public void testReplaceDataStreamSchema() {
        final var api = new ConnectedSystemsDynamicApi(newConfig());

        { //check incorrect request : missing dataStreamId
            final var parameters = new ReplaceDataStreamSchema();
            assertThrows(IllegalArgumentException.class, () -> api.replaceDataStreamSchema(parameters));
        }

        { //check valid request
            final byte[] content = "{}".getBytes(StandardCharsets.UTF_8);
            final var parameters = new ReplaceDataStreamSchema()
                    .dataStreamId("ds1")
                    .content(content)
                    .contentType("application/om+json");
            final var serviceRequest = api.replaceDataStreamSchema(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("PUT", request.method());
            assertEquals("http://localhost:8080/ogcapi/datastreams/ds1/schema", request.uri().toString());
            assertEquals(Optional.of("application/om+json"), request.headers().firstValue("Content-Type"));
            assertEquals(Optional.empty(), request.headers().firstValue("Prefer"));
            assertEquals(content.length, request.bodyPublisher().get().contentLength());
        }
    }

    // ------------------------------------------------------------------
    // Observations
    // ------------------------------------------------------------------

    /**
     * Testing {@linkplain ConnectedSystemsDynamicApi#getObservations(org.geotoolkit.ogcapi.request.connectedsystems.GetObservations) };
     */
    @Test
    public void testGetObservations() {
        final var api = new ConnectedSystemsDynamicApi(newConfig());

        { //check valid request
            final var parameters = new GetObservations()
                    .id(List.of("obs1"))
                    .q(List.of("wind speed"))
                    .phenomenonTime("2020-01-01T00:00:00Z")
                    .resultTime("2020-01-01T01:00:00Z")
                    .dataStream(List.of("ds1", "ds2"))
                    .foi(List.of("foi1"))
                    .observedProperty(List.of("wind"))
                    .limit(5);
            final var serviceRequest = api.getObservations(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/observations"
                    + "?id=obs1"
                    + "&q=wind%20speed"
                    + "&phenomenonTime=2020-01-01T00%3A00%3A00Z"
                    + "&resultTime=2020-01-01T01%3A00%3A00Z"
                    + "&dataStream=ds1%2Cds2"
                    + "&foi=foi1"
                    + "&observedProperty=wind"
                    + "&limit=5", request.uri().toString());
            assertEquals(Optional.of(ACCEPT_JSON), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain ConnectedSystemsDynamicApi#createObservation(org.geotoolkit.ogcapi.request.connectedsystems.CreateObservation) };
     */
    @Test
    public void testCreateObservation() {
        final var api = new ConnectedSystemsDynamicApi(newConfig());

        { //check incorrect request : missing dataStreamId
            final var parameters = new CreateObservation();
            assertThrows(IllegalArgumentException.class, () -> api.createObservation(parameters));
        }

        { //check valid request
            final byte[] content = "{}".getBytes(StandardCharsets.UTF_8);
            final var parameters = new CreateObservation()
                    .dataStreamId("ds1")
                    .content(content)
                    .contentType("application/json")
                    .dryRun(true)
                    .handling(PreferHandling.STRICT);
            final var serviceRequest = api.createObservation(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("POST", request.method());
            assertEquals("http://localhost:8080/ogcapi/datastreams/ds1/observations"
                    + "?dry-run=true", request.uri().toString());
            assertEquals(Optional.of("application/json"), request.headers().firstValue("Content-Type"));
            assertEquals(Optional.of("handling=strict"), request.headers().firstValue("Prefer"));
            assertEquals(content.length, request.bodyPublisher().get().contentLength());
        }
    }

    /**
     * Testing {@linkplain ConnectedSystemsDynamicApi#getObservation(org.geotoolkit.ogcapi.request.connectedsystems.GetObservation) };
     */
    @Test
    public void testGetObservation() {
        final var api = new ConnectedSystemsDynamicApi(newConfig());

        { //check incorrect request : missing obsId
            final var parameters = new GetObservation();
            assertThrows(IllegalArgumentException.class, () -> api.getObservation(parameters));
        }

        { //check valid request
            final var parameters = new GetObservation()
                    .obsId("obs 1");
            final var serviceRequest = api.getObservation(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/observations/obs%201", request.uri().toString());
            assertEquals(Optional.of(ACCEPT_JSON), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain ConnectedSystemsDynamicApi#replaceObservation(org.geotoolkit.ogcapi.request.connectedsystems.ReplaceObservation) };
     */
    @Test
    public void testReplaceObservation() {
        final var api = new ConnectedSystemsDynamicApi(newConfig());

        { //check incorrect request : missing obsId
            final var parameters = new ReplaceObservation();
            assertThrows(IllegalArgumentException.class, () -> api.replaceObservation(parameters));
        }

        { //check valid request
            final byte[] content = "{}".getBytes(StandardCharsets.UTF_8);
            final var parameters = new ReplaceObservation()
                    .obsId("obs1")
                    .content(content)
                    .contentType("application/json")
                    .dryRun(false)
                    .handling(PreferHandling.LENIENT);
            final var serviceRequest = api.replaceObservation(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("PUT", request.method());
            assertEquals("http://localhost:8080/ogcapi/observations/obs1"
                    + "?dry-run=false", request.uri().toString());
            assertEquals(Optional.of("application/json"), request.headers().firstValue("Content-Type"));
            assertEquals(Optional.of("handling=lenient"), request.headers().firstValue("Prefer"));
            assertEquals(content.length, request.bodyPublisher().get().contentLength());
        }
    }

    /**
     * Testing {@linkplain ConnectedSystemsDynamicApi#deleteObservation(org.geotoolkit.ogcapi.request.connectedsystems.DeleteObservation) };
     */
    @Test
    public void testDeleteObservation() {
        final var api = new ConnectedSystemsDynamicApi(newConfig());

        { //check incorrect request : missing obsId
            final var parameters = new DeleteObservation();
            assertThrows(IllegalArgumentException.class, () -> api.deleteObservation(parameters));
        }

        { //check valid request
            final var parameters = new DeleteObservation()
                    .obsId("obs1");
            final var serviceRequest = api.deleteObservation(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("DELETE", request.method());
            assertEquals("http://localhost:8080/ogcapi/observations/obs1", request.uri().toString());
        }
    }

    // ------------------------------------------------------------------
    // ControlStreams
    // ------------------------------------------------------------------

    /**
     * Testing {@linkplain ConnectedSystemsDynamicApi#getControlStreams(org.geotoolkit.ogcapi.request.connectedsystems.GetControlStreams) };
     */
    @Test
    public void testGetControlStreams() {
        final var api = new ConnectedSystemsDynamicApi(newConfig());

        { //check valid request
            final var parameters = new GetControlStreams()
                    .id(List.of("cs1"))
                    .q(List.of("temp control"))
                    .issueTime("2021-01-01T00:00:00Z")
                    .executionTime("2021-01-01T01:00:00Z")
                    .statusCode(List.of("PENDING", "COMPLETED"))
                    .sender(List.of("sender1"))
                    .system(List.of("sys1"))
                    .foi(List.of("foi1"))
                    .controlledProperty(List.of("prop1"))
                    .limit(20);
            final var serviceRequest = api.getControlStreams(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/controlstreams"
                    + "?id=cs1"
                    + "&q=temp%20control"
                    + "&issueTime=2021-01-01T00%3A00%3A00Z"
                    + "&executionTime=2021-01-01T01%3A00%3A00Z"
                    + "&statusCode=PENDING%2CCOMPLETED"
                    + "&sender=sender1"
                    + "&system=sys1"
                    + "&foi=foi1"
                    + "&controlledProperty=prop1"
                    + "&limit=20", request.uri().toString());
            assertEquals(Optional.of(ACCEPT_JSON), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain ConnectedSystemsDynamicApi#createControlStream(org.geotoolkit.ogcapi.request.connectedsystems.CreateControlStream) };
     */
    @Test
    public void testCreateControlStream() {
        final var api = new ConnectedSystemsDynamicApi(newConfig());

        { //check incorrect request : missing systemId
            final var parameters = new CreateControlStream();
            assertThrows(IllegalArgumentException.class, () -> api.createControlStream(parameters));
        }

        { //check valid request
            final byte[] content = "{}".getBytes(StandardCharsets.UTF_8);
            final var parameters = new CreateControlStream()
                    .systemId("sys1")
                    .content(content)
                    .contentType("application/json")
                    .dryRun(true)
                    .handling(PreferHandling.STRICT);
            final var serviceRequest = api.createControlStream(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("POST", request.method());
            assertEquals("http://localhost:8080/ogcapi/systems/sys1/controlstreams"
                    + "?dry-run=true", request.uri().toString());
            assertEquals(Optional.of("application/json"), request.headers().firstValue("Content-Type"));
            assertEquals(Optional.of("handling=strict"), request.headers().firstValue("Prefer"));
            assertEquals(content.length, request.bodyPublisher().get().contentLength());
        }
    }

    /**
     * Testing {@linkplain ConnectedSystemsDynamicApi#getControlStream(org.geotoolkit.ogcapi.request.connectedsystems.GetControlStream) };
     */
    @Test
    public void testGetControlStream() {
        final var api = new ConnectedSystemsDynamicApi(newConfig());

        { //check incorrect request : missing controlStreamId
            final var parameters = new GetControlStream();
            assertThrows(IllegalArgumentException.class, () -> api.getControlStream(parameters));
        }

        { //check valid request
            final var parameters = new GetControlStream()
                    .controlStreamId("cs1");
            final var serviceRequest = api.getControlStream(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/controlstreams/cs1", request.uri().toString());
            assertEquals(Optional.of(ACCEPT_JSON), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain ConnectedSystemsDynamicApi#replaceControlStream(org.geotoolkit.ogcapi.request.connectedsystems.ReplaceControlStream) };
     */
    @Test
    public void testReplaceControlStream() {
        final var api = new ConnectedSystemsDynamicApi(newConfig());

        { //check incorrect request : missing controlStreamId
            final var parameters = new ReplaceControlStream();
            assertThrows(IllegalArgumentException.class, () -> api.replaceControlStream(parameters));
        }

        { //check valid request
            final byte[] content = "{}".getBytes(StandardCharsets.UTF_8);
            final var parameters = new ReplaceControlStream()
                    .controlStreamId("cs1")
                    .content(content)
                    .contentType("application/json")
                    .dryRun(false)
                    .handling(PreferHandling.LENIENT);
            final var serviceRequest = api.replaceControlStream(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("PUT", request.method());
            assertEquals("http://localhost:8080/ogcapi/controlstreams/cs1"
                    + "?dry-run=false", request.uri().toString());
            assertEquals(Optional.of("application/json"), request.headers().firstValue("Content-Type"));
            assertEquals(Optional.of("handling=lenient"), request.headers().firstValue("Prefer"));
            assertEquals(content.length, request.bodyPublisher().get().contentLength());
        }
    }

    /**
     * Testing {@linkplain ConnectedSystemsDynamicApi#deleteControlStream(org.geotoolkit.ogcapi.request.connectedsystems.DeleteControlStream) };
     */
    @Test
    public void testDeleteControlStream() {
        final var api = new ConnectedSystemsDynamicApi(newConfig());

        { //check incorrect request : missing controlStreamId
            final var parameters = new DeleteControlStream();
            assertThrows(IllegalArgumentException.class, () -> api.deleteControlStream(parameters));
        }

        { //check valid request
            final var parameters = new DeleteControlStream()
                    .controlStreamId("cs1")
                    .cascade(true);
            final var serviceRequest = api.deleteControlStream(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("DELETE", request.method());
            assertEquals("http://localhost:8080/ogcapi/controlstreams/cs1"
                    + "?cascade=true", request.uri().toString());
        }
    }

    /**
     * Testing {@linkplain ConnectedSystemsDynamicApi#getControlStreamSchema(org.geotoolkit.ogcapi.request.connectedsystems.GetControlStreamSchema) };
     */
    @Test
    public void testGetControlStreamSchema() {
        final var api = new ConnectedSystemsDynamicApi(newConfig());

        { //check incorrect request : missing controlStreamId
            final var parameters = new GetControlStreamSchema();
            assertThrows(IllegalArgumentException.class, () -> api.getControlStreamSchema(parameters));
        }

        { //check valid request
            final var parameters = new GetControlStreamSchema()
                    .controlStreamId("cs1")
                    .cmdFormat("application/om+json");
            final var serviceRequest = api.getControlStreamSchema(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/controlstreams/cs1/schema"
                    + "?cmdFormat=application%2Fom%2Bjson", request.uri().toString());
            assertEquals(Optional.of(ACCEPT_JSON), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain ConnectedSystemsDynamicApi#replaceControlStreamSchema(org.geotoolkit.ogcapi.request.connectedsystems.ReplaceControlStreamSchema) };
     */
    @Test
    public void testReplaceControlStreamSchema() {
        final var api = new ConnectedSystemsDynamicApi(newConfig());

        { //check incorrect request : missing controlStreamId
            final var parameters = new ReplaceControlStreamSchema();
            assertThrows(IllegalArgumentException.class, () -> api.replaceControlStreamSchema(parameters));
        }

        { //check valid request
            final byte[] content = "{}".getBytes(StandardCharsets.UTF_8);
            final var parameters = new ReplaceControlStreamSchema()
                    .controlStreamId("cs1")
                    .content(content)
                    .contentType("application/om+json");
            final var serviceRequest = api.replaceControlStreamSchema(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("PUT", request.method());
            assertEquals("http://localhost:8080/ogcapi/controlstreams/cs1/schema", request.uri().toString());
            assertEquals(Optional.of("application/om+json"), request.headers().firstValue("Content-Type"));
            assertEquals(Optional.empty(), request.headers().firstValue("Prefer"));
            assertEquals(content.length, request.bodyPublisher().get().contentLength());
        }
    }

    // ------------------------------------------------------------------
    // Commands
    // ------------------------------------------------------------------

    /**
     * Testing {@linkplain ConnectedSystemsDynamicApi#getCommands(org.geotoolkit.ogcapi.request.connectedsystems.GetCommands) };
     */
    @Test
    public void testGetCommands() {
        final var api = new ConnectedSystemsDynamicApi(newConfig());

        { //check valid request
            final var parameters = new GetCommands()
                    .id(List.of("cmd1"))
                    .q(List.of("turn on"))
                    .issueTime("2021-02-01T00:00:00Z")
                    .executionTime("2021-02-01T01:00:00Z")
                    .statusCode(List.of("PENDING"))
                    .sender(List.of("op1"))
                    .system(List.of("sys1"))
                    .foi(List.of("foi1"))
                    .controlledProperty(List.of("prop1"))
                    .limit(15);
            final var serviceRequest = api.getCommands(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/commands"
                    + "?id=cmd1"
                    + "&q=turn%20on"
                    + "&issueTime=2021-02-01T00%3A00%3A00Z"
                    + "&executionTime=2021-02-01T01%3A00%3A00Z"
                    + "&statusCode=PENDING"
                    + "&sender=op1"
                    + "&system=sys1"
                    + "&foi=foi1"
                    + "&controlledProperty=prop1"
                    + "&limit=15", request.uri().toString());
            assertEquals(Optional.of(ACCEPT_JSON), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain ConnectedSystemsDynamicApi#createCommand(org.geotoolkit.ogcapi.request.connectedsystems.CreateCommand) };
     */
    @Test
    public void testCreateCommand() {
        final var api = new ConnectedSystemsDynamicApi(newConfig());

        { //check incorrect request : missing controlStreamId
            final var parameters = new CreateCommand();
            assertThrows(IllegalArgumentException.class, () -> api.createCommand(parameters));
        }

        { //check valid request
            final byte[] content = "{}".getBytes(StandardCharsets.UTF_8);
            final var parameters = new CreateCommand()
                    .controlStreamId("cs1")
                    .content(content)
                    .contentType("application/json")
                    .dryRun(true)
                    .handling(PreferHandling.STRICT);
            final var serviceRequest = api.createCommand(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("POST", request.method());
            assertEquals("http://localhost:8080/ogcapi/controlstreams/cs1/commands"
                    + "?dry-run=true", request.uri().toString());
            assertEquals(Optional.of("application/json"), request.headers().firstValue("Content-Type"));
            assertEquals(Optional.of("handling=strict"), request.headers().firstValue("Prefer"));
            assertEquals(content.length, request.bodyPublisher().get().contentLength());
        }
    }

    /**
     * Testing {@linkplain ConnectedSystemsDynamicApi#getCommand(org.geotoolkit.ogcapi.request.connectedsystems.GetCommand) };
     */
    @Test
    public void testGetCommand() {
        final var api = new ConnectedSystemsDynamicApi(newConfig());

        { //check incorrect request : missing cmdId
            final var parameters = new GetCommand();
            assertThrows(IllegalArgumentException.class, () -> api.getCommand(parameters));
        }

        { //check valid request
            final var parameters = new GetCommand()
                    .cmdId("cmd1");
            final var serviceRequest = api.getCommand(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/commands/cmd1", request.uri().toString());
            assertEquals(Optional.of(ACCEPT_JSON), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain ConnectedSystemsDynamicApi#replaceCommand(org.geotoolkit.ogcapi.request.connectedsystems.ReplaceCommand) };
     */
    @Test
    public void testReplaceCommand() {
        final var api = new ConnectedSystemsDynamicApi(newConfig());

        { //check incorrect request : missing cmdId
            final var parameters = new ReplaceCommand();
            assertThrows(IllegalArgumentException.class, () -> api.replaceCommand(parameters));
        }

        { //check valid request
            final byte[] content = "{}".getBytes(StandardCharsets.UTF_8);
            final var parameters = new ReplaceCommand()
                    .cmdId("cmd1")
                    .content(content)
                    .contentType("application/json")
                    .dryRun(false)
                    .handling(PreferHandling.LENIENT);
            final var serviceRequest = api.replaceCommand(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("PUT", request.method());
            assertEquals("http://localhost:8080/ogcapi/commands/cmd1"
                    + "?dry-run=false", request.uri().toString());
            assertEquals(Optional.of("application/json"), request.headers().firstValue("Content-Type"));
            assertEquals(Optional.of("handling=lenient"), request.headers().firstValue("Prefer"));
            assertEquals(content.length, request.bodyPublisher().get().contentLength());
        }
    }

    /**
     * Testing {@linkplain ConnectedSystemsDynamicApi#deleteCommand(org.geotoolkit.ogcapi.request.connectedsystems.DeleteCommand) };
     */
    @Test
    public void testDeleteCommand() {
        final var api = new ConnectedSystemsDynamicApi(newConfig());

        { //check incorrect request : missing cmdId
            final var parameters = new DeleteCommand();
            assertThrows(IllegalArgumentException.class, () -> api.deleteCommand(parameters));
        }

        { //check valid request
            final var parameters = new DeleteCommand()
                    .cmdId("cmd1");
            final var serviceRequest = api.deleteCommand(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("DELETE", request.method());
            assertEquals("http://localhost:8080/ogcapi/commands/cmd1", request.uri().toString());
        }
    }

    // ------------------------------------------------------------------
    // Command Status
    // ------------------------------------------------------------------

    /**
     * Testing {@linkplain ConnectedSystemsDynamicApi#getCommandStatuses(org.geotoolkit.ogcapi.request.connectedsystems.GetCommandStatuses) };
     */
    @Test
    public void testGetCommandStatuses() {
        final var api = new ConnectedSystemsDynamicApi(newConfig());

        { //check incorrect request : missing cmdId
            final var parameters = new GetCommandStatuses();
            assertThrows(IllegalArgumentException.class, () -> api.getCommandStatuses(parameters));
        }

        { //check valid request
            final var parameters = new GetCommandStatuses()
                    .cmdId("cmd1");
            final var serviceRequest = api.getCommandStatuses(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/commands/cmd1/status", request.uri().toString());
            assertEquals(Optional.of(ACCEPT_JSON), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain ConnectedSystemsDynamicApi#createCommandStatus(org.geotoolkit.ogcapi.request.connectedsystems.CreateCommandStatus) };
     */
    @Test
    public void testCreateCommandStatus() {
        final var api = new ConnectedSystemsDynamicApi(newConfig());

        { //check incorrect request : missing cmdId
            final var parameters = new CreateCommandStatus();
            assertThrows(IllegalArgumentException.class, () -> api.createCommandStatus(parameters));
        }

        { //check valid request
            final byte[] content = "{}".getBytes(StandardCharsets.UTF_8);
            final var parameters = new CreateCommandStatus()
                    .cmdId("cmd1")
                    .content(content)
                    .contentType("application/json")
                    .dryRun(true)
                    .handling(PreferHandling.STRICT);
            final var serviceRequest = api.createCommandStatus(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("POST", request.method());
            assertEquals("http://localhost:8080/ogcapi/commands/cmd1/status"
                    + "?dry-run=true", request.uri().toString());
            assertEquals(Optional.of("application/json"), request.headers().firstValue("Content-Type"));
            assertEquals(Optional.of("handling=strict"), request.headers().firstValue("Prefer"));
            assertEquals(content.length, request.bodyPublisher().get().contentLength());
        }
    }

    /**
     * Testing {@linkplain ConnectedSystemsDynamicApi#getCommandStatus(org.geotoolkit.ogcapi.request.connectedsystems.GetCommandStatus) };
     */
    @Test
    public void testGetCommandStatus() {
        final var api = new ConnectedSystemsDynamicApi(newConfig());

        { //check incorrect request : missing cmdId and statusId
            final var parameters = new GetCommandStatus();
            assertThrows(IllegalArgumentException.class, () -> api.getCommandStatus(parameters));
        }

        { //check valid request
            final var parameters = new GetCommandStatus()
                    .cmdId("cmd1")
                    .statusId("st1");
            final var serviceRequest = api.getCommandStatus(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/commands/cmd1/status/st1", request.uri().toString());
            assertEquals(Optional.of(ACCEPT_JSON), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain ConnectedSystemsDynamicApi#replaceCommandStatus(org.geotoolkit.ogcapi.request.connectedsystems.ReplaceCommandStatus) };
     */
    @Test
    public void testReplaceCommandStatus() {
        final var api = new ConnectedSystemsDynamicApi(newConfig());

        { //check incorrect request : missing cmdId and statusId
            final var parameters = new ReplaceCommandStatus();
            assertThrows(IllegalArgumentException.class, () -> api.replaceCommandStatus(parameters));
        }

        { //check valid request
            final byte[] content = "{}".getBytes(StandardCharsets.UTF_8);
            final var parameters = new ReplaceCommandStatus()
                    .cmdId("cmd1")
                    .statusId("st1")
                    .content(content)
                    .contentType("application/json");
            final var serviceRequest = api.replaceCommandStatus(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("PUT", request.method());
            assertEquals("http://localhost:8080/ogcapi/commands/cmd1/status/st1", request.uri().toString());
            assertEquals(Optional.of("application/json"), request.headers().firstValue("Content-Type"));
            assertEquals(Optional.empty(), request.headers().firstValue("Prefer"));
            assertEquals(content.length, request.bodyPublisher().get().contentLength());
        }
    }

    /**
     * Testing {@linkplain ConnectedSystemsDynamicApi#deleteCommandStatus(org.geotoolkit.ogcapi.request.connectedsystems.DeleteCommandStatus) };
     */
    @Test
    public void testDeleteCommandStatus() {
        final var api = new ConnectedSystemsDynamicApi(newConfig());

        { //check incorrect request : missing cmdId and statusId
            final var parameters = new DeleteCommandStatus();
            assertThrows(IllegalArgumentException.class, () -> api.deleteCommandStatus(parameters));
        }

        { //check valid request
            final var parameters = new DeleteCommandStatus()
                    .cmdId("cmd1")
                    .statusId("st1");
            final var serviceRequest = api.deleteCommandStatus(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("DELETE", request.method());
            assertEquals("http://localhost:8080/ogcapi/commands/cmd1/status/st1", request.uri().toString());
        }
    }

    // ------------------------------------------------------------------
    // Command Result
    // ------------------------------------------------------------------

    /**
     * Testing {@linkplain ConnectedSystemsDynamicApi#getCommandResults(org.geotoolkit.ogcapi.request.connectedsystems.GetCommandResults) };
     */
    @Test
    public void testGetCommandResults() {
        final var api = new ConnectedSystemsDynamicApi(newConfig());

        { //check incorrect request : missing cmdId
            final var parameters = new GetCommandResults();
            assertThrows(IllegalArgumentException.class, () -> api.getCommandResults(parameters));
        }

        { //check valid request
            final var parameters = new GetCommandResults()
                    .cmdId("cmd1");
            final var serviceRequest = api.getCommandResults(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/commands/cmd1/result", request.uri().toString());
            assertEquals(Optional.of(ACCEPT_JSON), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain ConnectedSystemsDynamicApi#createCommandResult(org.geotoolkit.ogcapi.request.connectedsystems.CreateCommandResult) };
     */
    @Test
    public void testCreateCommandResult() {
        final var api = new ConnectedSystemsDynamicApi(newConfig());

        { //check incorrect request : missing cmdId
            final var parameters = new CreateCommandResult();
            assertThrows(IllegalArgumentException.class, () -> api.createCommandResult(parameters));
        }

        { //check valid request
            final byte[] content = "{}".getBytes(StandardCharsets.UTF_8);
            final var parameters = new CreateCommandResult()
                    .cmdId("cmd1")
                    .content(content)
                    .contentType("application/json")
                    .dryRun(true)
                    .handling(PreferHandling.STRICT);
            final var serviceRequest = api.createCommandResult(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("POST", request.method());
            assertEquals("http://localhost:8080/ogcapi/commands/cmd1/result"
                    + "?dry-run=true", request.uri().toString());
            assertEquals(Optional.of("application/json"), request.headers().firstValue("Content-Type"));
            assertEquals(Optional.of("handling=strict"), request.headers().firstValue("Prefer"));
            assertEquals(content.length, request.bodyPublisher().get().contentLength());
        }
    }

    /**
     * Testing {@linkplain ConnectedSystemsDynamicApi#getCommandResult(org.geotoolkit.ogcapi.request.connectedsystems.GetCommandResult) };
     */
    @Test
    public void testGetCommandResult() {
        final var api = new ConnectedSystemsDynamicApi(newConfig());

        { //check incorrect request : missing cmdId and resultId
            final var parameters = new GetCommandResult();
            assertThrows(IllegalArgumentException.class, () -> api.getCommandResult(parameters));
        }

        { //check valid request
            final var parameters = new GetCommandResult()
                    .cmdId("cmd1")
                    .resultId("res1");
            final var serviceRequest = api.getCommandResult(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/commands/cmd1/result/res1", request.uri().toString());
            assertEquals(Optional.of(ACCEPT_JSON), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain ConnectedSystemsDynamicApi#replaceCommandResult(org.geotoolkit.ogcapi.request.connectedsystems.ReplaceCommandResult) };
     */
    @Test
    public void testReplaceCommandResult() {
        final var api = new ConnectedSystemsDynamicApi(newConfig());

        { //check incorrect request : missing cmdId and resultId
            final var parameters = new ReplaceCommandResult();
            assertThrows(IllegalArgumentException.class, () -> api.replaceCommandResult(parameters));
        }

        { //check valid request
            final byte[] content = "{}".getBytes(StandardCharsets.UTF_8);
            final var parameters = new ReplaceCommandResult()
                    .cmdId("cmd1")
                    .resultId("res1")
                    .content(content)
                    .contentType("application/json");
            final var serviceRequest = api.replaceCommandResult(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("PUT", request.method());
            assertEquals("http://localhost:8080/ogcapi/commands/cmd1/result/res1", request.uri().toString());
            assertEquals(Optional.of("application/json"), request.headers().firstValue("Content-Type"));
            assertEquals(Optional.empty(), request.headers().firstValue("Prefer"));
            assertEquals(content.length, request.bodyPublisher().get().contentLength());
        }
    }

    /**
     * Testing {@linkplain ConnectedSystemsDynamicApi#deleteCommandResult(org.geotoolkit.ogcapi.request.connectedsystems.DeleteCommandResult) };
     */
    @Test
    public void testDeleteCommandResult() {
        final var api = new ConnectedSystemsDynamicApi(newConfig());

        { //check incorrect request : missing cmdId and resultId
            final var parameters = new DeleteCommandResult();
            assertThrows(IllegalArgumentException.class, () -> api.deleteCommandResult(parameters));
        }

        { //check valid request
            final var parameters = new DeleteCommandResult()
                    .cmdId("cmd1")
                    .resultId("res1");
            final var serviceRequest = api.deleteCommandResult(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("DELETE", request.method());
            assertEquals("http://localhost:8080/ogcapi/commands/cmd1/result/res1", request.uri().toString());
        }
    }

    // ------------------------------------------------------------------
    // System Events
    // ------------------------------------------------------------------

    /**
     * Testing {@linkplain ConnectedSystemsDynamicApi#getAllSystemEvents(org.geotoolkit.ogcapi.request.connectedsystems.GetAllSystemEvents) };
     */
    @Test
    public void testGetAllSystemEvents() {
        final var api = new ConnectedSystemsDynamicApi(newConfig());

        { //check valid request
            final var parameters = new GetAllSystemEvents()
                    .eventTime("2022-01-01T00:00:00Z")
                    .eventType(List.of("CREATE", "UPDATE"))
                    .q(List.of("system online"))
                    .system(List.of("sys1"))
                    .limit(30);
            final var serviceRequest = api.getAllSystemEvents(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/systemEvents"
                    + "?datetime=2022-01-01T00%3A00%3A00Z"
                    + "&eventType=CREATE%2CUPDATE"
                    + "&q=system%20online"
                    + "&system=sys1"
                    + "&limit=30", request.uri().toString());
            assertEquals(Optional.of(ACCEPT_JSON), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain ConnectedSystemsDynamicApi#getSystemEvents(org.geotoolkit.ogcapi.request.connectedsystems.GetSystemEvents) };
     */
    @Test
    public void testGetSystemEvents() {
        final var api = new ConnectedSystemsDynamicApi(newConfig());

        { //check incorrect request : missing systemId
            final var parameters = new GetSystemEvents();
            assertThrows(IllegalArgumentException.class, () -> api.getSystemEvents(parameters));
        }

        { //check valid request
            final var parameters = new GetSystemEvents()
                    .systemId("sys1")
                    .eventTime("2022-02-01T00:00:00Z")
                    .eventType(List.of("DELETE"))
                    .q(List.of("offline"))
                    .system(List.of("sys2"))
                    .limit(10);
            final var serviceRequest = api.getSystemEvents(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/systems/sys1/events"
                    + "?datetime=2022-02-01T00%3A00%3A00Z"
                    + "&eventType=DELETE"
                    + "&q=offline"
                    + "&system=sys2"
                    + "&limit=10", request.uri().toString());
            assertEquals(Optional.of(ACCEPT_JSON), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain ConnectedSystemsDynamicApi#createSystemEvent(org.geotoolkit.ogcapi.request.connectedsystems.CreateSystemEvent) };
     */
    @Test
    public void testCreateSystemEvent() {
        final var api = new ConnectedSystemsDynamicApi(newConfig());

        { //check incorrect request : missing systemId
            final var parameters = new CreateSystemEvent();
            assertThrows(IllegalArgumentException.class, () -> api.createSystemEvent(parameters));
        }

        { //check valid request
            final byte[] content = "{}".getBytes(StandardCharsets.UTF_8);
            final var parameters = new CreateSystemEvent()
                    .systemId("sys1")
                    .content(content)
                    .contentType("application/json")
                    .dryRun(true)
                    .handling(PreferHandling.STRICT);
            final var serviceRequest = api.createSystemEvent(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("POST", request.method());
            assertEquals("http://localhost:8080/ogcapi/systems/sys1/events"
                    + "?dry-run=true", request.uri().toString());
            assertEquals(Optional.of("application/json"), request.headers().firstValue("Content-Type"));
            assertEquals(Optional.of("handling=strict"), request.headers().firstValue("Prefer"));
            assertEquals(content.length, request.bodyPublisher().get().contentLength());
        }
    }

    /**
     * Testing {@linkplain ConnectedSystemsDynamicApi#getSystemEvent(org.geotoolkit.ogcapi.request.connectedsystems.GetSystemEvent) };
     */
    @Test
    public void testGetSystemEvent() {
        final var api = new ConnectedSystemsDynamicApi(newConfig());

        { //check incorrect request : missing systemId and eventId
            final var parameters = new GetSystemEvent();
            assertThrows(IllegalArgumentException.class, () -> api.getSystemEvent(parameters));
        }

        { //check valid request
            final var parameters = new GetSystemEvent()
                    .systemId("sys1")
                    .eventId("evt1");
            final var serviceRequest = api.getSystemEvent(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/systems/sys1/events/evt1", request.uri().toString());
            assertEquals(Optional.of(ACCEPT_JSON), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain ConnectedSystemsDynamicApi#replaceSystemEvent(org.geotoolkit.ogcapi.request.connectedsystems.ReplaceSystemEvent) };
     */
    @Test
    public void testReplaceSystemEvent() {
        final var api = new ConnectedSystemsDynamicApi(newConfig());

        { //check incorrect request : missing systemId and eventId
            final var parameters = new ReplaceSystemEvent();
            assertThrows(IllegalArgumentException.class, () -> api.replaceSystemEvent(parameters));
        }

        { //check valid request
            final byte[] content = "{}".getBytes(StandardCharsets.UTF_8);
            final var parameters = new ReplaceSystemEvent()
                    .systemId("sys1")
                    .eventId("evt1")
                    .content(content)
                    .contentType("application/json");
            final var serviceRequest = api.replaceSystemEvent(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("PUT", request.method());
            assertEquals("http://localhost:8080/ogcapi/systems/sys1/events/evt1", request.uri().toString());
            assertEquals(Optional.of("application/json"), request.headers().firstValue("Content-Type"));
            assertEquals(Optional.empty(), request.headers().firstValue("Prefer"));
            assertEquals(content.length, request.bodyPublisher().get().contentLength());
        }
    }

    /**
     * Testing {@linkplain ConnectedSystemsDynamicApi#deleteSystemEvent(org.geotoolkit.ogcapi.request.connectedsystems.DeleteSystemEvent) };
     */
    @Test
    public void testDeleteSystemEvent() {
        final var api = new ConnectedSystemsDynamicApi(newConfig());

        { //check incorrect request : missing systemId and eventId
            final var parameters = new DeleteSystemEvent();
            assertThrows(IllegalArgumentException.class, () -> api.deleteSystemEvent(parameters));
        }

        { //check valid request
            final var parameters = new DeleteSystemEvent()
                    .systemId("sys1")
                    .eventId("evt1");
            final var serviceRequest = api.deleteSystemEvent(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("DELETE", request.method());
            assertEquals("http://localhost:8080/ogcapi/systems/sys1/events/evt1", request.uri().toString());
        }
    }

    // ------------------------------------------------------------------
    // System History
    // ------------------------------------------------------------------

    /**
     * Testing {@linkplain ConnectedSystemsDynamicApi#getSystemHistory(org.geotoolkit.ogcapi.request.connectedsystems.GetSystemHistory) };
     */
    @Test
    public void testGetSystemHistory() {
        final var api = new ConnectedSystemsDynamicApi(newConfig());

        { //check incorrect request : missing systemId
            final var parameters = new GetSystemHistory();
            assertThrows(IllegalArgumentException.class, () -> api.getSystemHistory(parameters));
        }

        { //check valid request
            final var parameters = new GetSystemHistory()
                    .systemId("sys1")
                    .validTime("2023-01-01T00:00:00Z")
                    .q(List.of("archived version"))
                    .limit(5);
            final var serviceRequest = api.getSystemHistory(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/systems/sys1/history"
                    + "?validTime=2023-01-01T00%3A00%3A00Z"
                    + "&q=archived%20version"
                    + "&limit=5", request.uri().toString());
            assertEquals(Optional.of(ACCEPT_SYSTEM), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain ConnectedSystemsDynamicApi#getSystemHistoryRevision(org.geotoolkit.ogcapi.request.connectedsystems.GetSystemHistoryRevision) };
     */
    @Test
    public void testGetSystemHistoryRevision() {
        final var api = new ConnectedSystemsDynamicApi(newConfig());

        { //check incorrect request : missing systemId and revId
            final var parameters = new GetSystemHistoryRevision();
            assertThrows(IllegalArgumentException.class, () -> api.getSystemHistoryRevision(parameters));
        }

        { //check valid request
            final var parameters = new GetSystemHistoryRevision()
                    .systemId("sys1")
                    .revId("rev1");
            final var serviceRequest = api.getSystemHistoryRevision(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/systems/sys1/history/rev1", request.uri().toString());
            assertEquals(Optional.of(ACCEPT_SYSTEM), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain ConnectedSystemsDynamicApi#replaceSystemHistoryRevision(org.geotoolkit.ogcapi.request.connectedsystems.ReplaceSystemHistoryRevision) };
     */
    @Test
    public void testReplaceSystemHistoryRevision() {
        final var api = new ConnectedSystemsDynamicApi(newConfig());

        { //check incorrect request : missing systemId and revId
            final var parameters = new ReplaceSystemHistoryRevision();
            assertThrows(IllegalArgumentException.class, () -> api.replaceSystemHistoryRevision(parameters));
        }

        { //check valid request
            final byte[] content = "{}".getBytes(StandardCharsets.UTF_8);
            final var parameters = new ReplaceSystemHistoryRevision()
                    .systemId("sys1")
                    .revId("rev1")
                    .content(content)
                    .contentType("application/json");
            final var serviceRequest = api.replaceSystemHistoryRevision(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("PUT", request.method());
            assertEquals("http://localhost:8080/ogcapi/systems/sys1/history/rev1", request.uri().toString());
            assertEquals(Optional.of("application/json"), request.headers().firstValue("Content-Type"));
            assertEquals(content.length, request.bodyPublisher().get().contentLength());
        }
    }

    /**
     * Testing {@linkplain ConnectedSystemsDynamicApi#deleteSystemHistoryRevision(org.geotoolkit.ogcapi.request.connectedsystems.DeleteSystemHistoryRevision) };
     */
    @Test
    public void testDeleteSystemHistoryRevision() {
        final var api = new ConnectedSystemsDynamicApi(newConfig());

        { //check incorrect request : missing systemId and revId
            final var parameters = new DeleteSystemHistoryRevision();
            assertThrows(IllegalArgumentException.class, () -> api.deleteSystemHistoryRevision(parameters));
        }

        { //check valid request
            final var parameters = new DeleteSystemHistoryRevision()
                    .systemId("sys1")
                    .revId("rev1");
            final var serviceRequest = api.deleteSystemHistoryRevision(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("DELETE", request.method());
            assertEquals("http://localhost:8080/ogcapi/systems/sys1/history/rev1", request.uri().toString());
        }
    }

}
