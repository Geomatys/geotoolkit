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
package org.geotoolkit.ogcapi.client.routes;

import java.util.Optional;
import org.geotoolkit.ogcapi.client.AbstractApiTest;
import org.geotoolkit.ogcapi.dto.routes.RouteDefinition;
import org.geotoolkit.ogcapi.request.routes.CreateRoute;
import org.geotoolkit.ogcapi.request.routes.GetRoute;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class RoutesApiTest extends AbstractApiTest {

    /**
     * Testing {@linkplain RoutesApi#getRoutes() };
     * Request the list of previously created routes.
     */
    @Test
    public void testGetRoutes() {
        final var api = new RoutesApi(newConfig());

        final var serviceRequest = api.getRoutes();
        final var request = serviceRequest.builder().build();

        assertEquals("GET", request.method());
        assertEquals("http://localhost:8080/ogcapi/routes", request.uri().toString());
        assertEquals(Optional.of("application/json"), request.headers().firstValue("Accept"));
    }

    /**
     * Testing {@linkplain RoutesApi#computeRoute(org.geotoolkit.ogcapi.request.routes.CreateRoute) };
     * Compute a route from a route definition body `route to work`.
     */
    @Test
    public void testComputeRoute() {
        final var api = new RoutesApi(newConfig());

        { //check incorrect request : missing body
            final var parameters = new CreateRoute();
            assertThrows(IllegalArgumentException.class, () -> api.computeRoute(parameters));
        }

        { //check valid request
            final var body = new RouteDefinition()
                    .name("route to work")
                    .mode("driving-car");
            final var parameters = new CreateRoute()
                    .body(body);
            final var serviceRequest = api.computeRoute(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("POST", request.method());
            assertEquals("http://localhost:8080/ogcapi/routes", request.uri().toString());
            assertEquals(Optional.of("application/geo+json"), request.headers().firstValue("Accept"));
            assertEquals(Optional.of("application/json"), request.headers().firstValue("Content-Type"));
            assertTrue(request.bodyPublisher().isPresent());
        }
    }

    /**
     * Testing {@linkplain RoutesApi#getRoute(org.geotoolkit.ogcapi.request.routes.GetRoute) };
     * Request a specific route `route 42`.
     */
    @Test
    public void testGetRoute() {
        final var api = new RoutesApi(newConfig());

        { //check incorrect request : missing route id
            final var parameters = new GetRoute();
            assertThrows(IllegalArgumentException.class, () -> api.getRoute(parameters));
        }

        { //check valid request
            final var parameters = new GetRoute()
                    .routeId("route 42");
            final var serviceRequest = api.getRoute(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/routes/route%2042", request.uri().toString());
            assertEquals(Optional.of("application/geo+json"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain RoutesApi#getRouteDefinition(org.geotoolkit.ogcapi.request.routes.GetRoute) };
     * Request the definition of a specific route `trip:42`.
     */
    @Test
    public void testGetRouteDefinition() {
        final var api = new RoutesApi(newConfig());

        { //check incorrect request : missing route id
            final var parameters = new GetRoute();
            assertThrows(IllegalArgumentException.class, () -> api.getRouteDefinition(parameters));
        }

        { //check valid request
            final var parameters = new GetRoute()
                    .routeId("trip:42");
            final var serviceRequest = api.getRouteDefinition(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/routes/trip%3A42/definition", request.uri().toString());
            assertEquals(Optional.of("application/json"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain RoutesApi#deleteRoute(org.geotoolkit.ogcapi.request.routes.GetRoute) };
     * Delete a specific route `route/99`.
     */
    @Test
    public void testDeleteRoute() {
        final var api = new RoutesApi(newConfig());

        { //check incorrect request : missing route id
            final var parameters = new GetRoute();
            assertThrows(IllegalArgumentException.class, () -> api.deleteRoute(parameters));
        }

        { //check valid request
            final var parameters = new GetRoute()
                    .routeId("route/99");
            final var serviceRequest = api.deleteRoute(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("DELETE", request.method());
            assertEquals("http://localhost:8080/ogcapi/routes/route%2F99", request.uri().toString());
        }
    }
}
