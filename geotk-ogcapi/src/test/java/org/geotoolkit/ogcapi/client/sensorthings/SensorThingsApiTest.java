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

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.geotoolkit.ogcapi.client.AbstractApiTest;
import org.geotoolkit.ogcapi.dto.sensorthings.Datastream;
import org.geotoolkit.ogcapi.request.sensorthings.CreateEntity;
import org.geotoolkit.ogcapi.request.sensorthings.DeleteEntity;
import org.geotoolkit.ogcapi.request.sensorthings.GetEntities;
import org.geotoolkit.ogcapi.request.sensorthings.GetEntity;
import org.geotoolkit.ogcapi.request.sensorthings.UpdateEntity;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class SensorThingsApiTest extends AbstractApiTest {

    /**
     * Testing {@linkplain SensorThingsApi#getEntities(org.geotoolkit.ogcapi.request.sensorthings.GetEntities, java.lang.Class) };
     * Request the `Datastreams` nested under `Things(1)`, with a filter, expand, select, orderby,
     * top, skip, count and resultFormat OData query options.
     */
    @Test
    public void testGetEntities() {
        final SensorThingsApi api = new SensorThingsApi(newConfig());

        { // check incorrect request : missing entitySet
            final var parameters = new GetEntities();
            assertThrows(IllegalArgumentException.class, () -> api.getEntities(parameters, Datastream.class));
        }

        { // check valid request
            final var parameters = new GetEntities()
                    .entitySet(SensorThingsApi.DATASTREAMS)
                    .parentEntitySet(SensorThingsApi.THINGS)
                    .parentId(1)
                    .filter("name eq 'A thing'")
                    .expand("Sensor")
                    .select("name,description")
                    .orderby("phenomenonTime desc")
                    .top(10)
                    .skip(5)
                    .count(Boolean.TRUE)
                    .resultFormat("dataArray");
            final var serviceRequest = api.getEntities(parameters, Datastream.class);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/Things(1)/Datastreams"
                    + "?%24filter=name%20eq%20%27A%20thing%27"
                    + "&%24expand=Sensor"
                    + "&%24select=name%2Cdescription"
                    + "&%24orderby=phenomenonTime%20desc"
                    + "&%24top=10"
                    + "&%24skip=5"
                    + "&%24count=true"
                    + "&%24resultFormat=dataArray", request.uri().toString());
            assertEquals(Optional.of("application/json"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain SensorThingsApi#getEntity(org.geotoolkit.ogcapi.request.sensorthings.GetEntity, java.lang.Class) };
     * Request a single `Datastreams(42)` nested under `Things(1)`, with a select and expand OData
     * query option.
     */
    @Test
    public void testGetEntity() {
        final SensorThingsApi api = new SensorThingsApi(newConfig());

        { // check incorrect request : missing entitySet
            final var parameters = new GetEntity();
            assertThrows(IllegalArgumentException.class, () -> api.getEntity(parameters, Datastream.class));
        }

        { // check incorrect request : missing id
            final var parameters = new GetEntity()
                    .entitySet(SensorThingsApi.DATASTREAMS);
            assertThrows(IllegalArgumentException.class, () -> api.getEntity(parameters, Datastream.class));
        }

        { // check valid request
            final var parameters = new GetEntity()
                    .entitySet(SensorThingsApi.DATASTREAMS)
                    .id(42)
                    .parentEntitySet(SensorThingsApi.THINGS)
                    .parentId(1)
                    .select("name,description")
                    .expand("Thing");
            final var serviceRequest = api.getEntity(parameters, Datastream.class);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/Things(1)/Datastreams(42)"
                    + "?%24select=name%2Cdescription"
                    + "&%24expand=Thing", request.uri().toString());
            assertEquals(Optional.of("application/json"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain SensorThingsApi#createEntity(org.geotoolkit.ogcapi.request.sensorthings.CreateEntity) };
     * Create a `Task` nested under `TaskingCapabilities(7)`.
     */
    @Test
    public void testCreateEntity() {
        final SensorThingsApi api = new SensorThingsApi(newConfig());

        { // check incorrect request : missing entitySet
            final var parameters = new CreateEntity()
                    .content("{}".getBytes(StandardCharsets.UTF_8))
                    .contentType("application/json");
            assertThrows(IllegalArgumentException.class, () -> api.createEntity(parameters));
        }

        { // check incorrect request : missing content
            final var parameters = new CreateEntity()
                    .entitySet(SensorThingsApi.TASKS)
                    .contentType("application/json");
            assertThrows(IllegalArgumentException.class, () -> api.createEntity(parameters));
        }

        { // check incorrect request : missing contentType
            final var parameters = new CreateEntity()
                    .entitySet(SensorThingsApi.TASKS)
                    .content("{}".getBytes(StandardCharsets.UTF_8));
            assertThrows(IllegalArgumentException.class, () -> api.createEntity(parameters));
        }

        { // check valid request
            final byte[] content = "{\"taskingParameters\":{\"value\":1}}".getBytes(StandardCharsets.UTF_8);
            final var parameters = new CreateEntity()
                    .entitySet(SensorThingsApi.TASKS)
                    .parentEntitySet(SensorThingsApi.TASKING_CAPABILITIES)
                    .parentId(7)
                    .content(content)
                    .contentType("application/json");
            final var serviceRequest = api.createEntity(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("POST", request.method());
            assertEquals("http://localhost:8080/ogcapi/TaskingCapabilities(7)/Tasks", request.uri().toString());
            assertEquals(Optional.of("application/json"), request.headers().firstValue("Content-Type"));
            assertEquals(Optional.of("application/json"), request.headers().firstValue("Accept"));
            assertEquals(content.length, request.bodyPublisher().get().contentLength());
        }
    }

    /**
     * Testing {@linkplain SensorThingsApi#updateEntity(org.geotoolkit.ogcapi.request.sensorthings.UpdateEntity) };
     * Partially update `Things('thing-01')` with an explicit content type, and `Sensors(3)`
     * relying on the default `application/json` content type.
     */
    @Test
    public void testUpdateEntity() {
        final SensorThingsApi api = new SensorThingsApi(newConfig());

        { // check incorrect request : missing entitySet
            final var parameters = new UpdateEntity()
                    .content("{}".getBytes(StandardCharsets.UTF_8));
            assertThrows(IllegalArgumentException.class, () -> api.updateEntity(parameters));
        }

        { // check incorrect request : missing id
            final var parameters = new UpdateEntity()
                    .entitySet(SensorThingsApi.THINGS)
                    .content("{}".getBytes(StandardCharsets.UTF_8));
            assertThrows(IllegalArgumentException.class, () -> api.updateEntity(parameters));
        }

        { // check incorrect request : missing content
            final var parameters = new UpdateEntity()
                    .entitySet(SensorThingsApi.THINGS)
                    .id(1);
            assertThrows(IllegalArgumentException.class, () -> api.updateEntity(parameters));
        }

        { // check valid request : explicit content type
            final byte[] content = "{\"description\":\"A thing\"}".getBytes(StandardCharsets.UTF_8);
            final var parameters = new UpdateEntity()
                    .entitySet(SensorThingsApi.THINGS)
                    .id("thing-01")
                    .content(content)
                    .contentType("application/merge-patch+json");
            final var serviceRequest = api.updateEntity(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("PATCH", request.method());
            assertEquals("http://localhost:8080/ogcapi/Things('thing-01')", request.uri().toString());
            assertEquals(Optional.of("application/merge-patch+json"), request.headers().firstValue("Content-Type"));
            assertEquals(Optional.of("application/json"), request.headers().firstValue("Accept"));
            assertEquals(content.length, request.bodyPublisher().get().contentLength());
        }

        { // check valid request : default content type
            final byte[] content = "{\"name\":\"A sensor\"}".getBytes(StandardCharsets.UTF_8);
            final var parameters = new UpdateEntity()
                    .entitySet(SensorThingsApi.SENSORS)
                    .id(3)
                    .content(content);
            final var serviceRequest = api.updateEntity(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("PATCH", request.method());
            assertEquals("http://localhost:8080/ogcapi/Sensors(3)", request.uri().toString());
            assertEquals(Optional.of("application/json"), request.headers().firstValue("Content-Type"));
            assertEquals(Optional.of("application/json"), request.headers().firstValue("Accept"));
            assertEquals(content.length, request.bodyPublisher().get().contentLength());
        }
    }

    /**
     * Testing {@linkplain SensorThingsApi#deleteEntity(org.geotoolkit.ogcapi.request.sensorthings.DeleteEntity) };
     * Delete `Observations(99)`.
     */
    @Test
    public void testDeleteEntity() {
        final SensorThingsApi api = new SensorThingsApi(newConfig());

        { // check incorrect request : missing entitySet
            final var parameters = new DeleteEntity();
            assertThrows(IllegalArgumentException.class, () -> api.deleteEntity(parameters));
        }

        { // check incorrect request : missing id
            final var parameters = new DeleteEntity()
                    .entitySet(SensorThingsApi.OBSERVATIONS);
            assertThrows(IllegalArgumentException.class, () -> api.deleteEntity(parameters));
        }

        { // check valid request
            final var parameters = new DeleteEntity()
                    .entitySet(SensorThingsApi.OBSERVATIONS)
                    .id(99);
            final var serviceRequest = api.deleteEntity(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("DELETE", request.method());
            assertEquals("http://localhost:8080/ogcapi/Observations(99)", request.uri().toString());
        }
    }
}
