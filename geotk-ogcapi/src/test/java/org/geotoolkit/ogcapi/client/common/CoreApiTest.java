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
package org.geotoolkit.ogcapi.client.common;

import java.util.Optional;
import org.geotoolkit.ogcapi.client.AbstractApiTest;
import org.geotoolkit.ogcapi.request.common.GetApi;
import org.geotoolkit.ogcapi.request.common.GetConformance;
import org.geotoolkit.ogcapi.request.common.GetLandingPage;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class CoreApiTest extends AbstractApiTest {

    /**
     * Testing {@linkplain CoreApi#getApi(org.geotoolkit.ogcapi.request.common.GetApi) };
     * Request the API definition document in json format.
     */
    @Test
    public void testGetApi() {
        final CoreApi api = new CoreApi(newConfig());

        { //check valid request
            final var parameters = new GetApi()
                    .format("json");
            final var serviceRequest = api.getApi(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/api"
                    + "?f=json", request.uri().toString());
            assertEquals(Optional.of("application/json, text/html"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain CoreApi#getConformance(org.geotoolkit.ogcapi.request.common.GetConformance) };
     * Request the conformance classes declared by the service, in html format.
     */
    @Test
    public void testGetConformance() {
        final CoreApi api = new CoreApi(newConfig());

        { //check valid request
            final var parameters = new GetConformance()
                    .format("html");
            final var serviceRequest = api.getConformance(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/conformance"
                    + "?f=html", request.uri().toString());
            assertEquals(Optional.of("application/json, text/html"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain CoreApi#getLandingPage(org.geotoolkit.ogcapi.request.common.GetLandingPage) };
     * Request the landing page in json format.
     */
    @Test
    public void testGetLandingPage() {
        final CoreApi api = new CoreApi(newConfig());

        { //check valid request
            final var parameters = new GetLandingPage()
                    .format("json");
            final var serviceRequest = api.getLandingPage(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/"
                    + "?f=json", request.uri().toString());
            assertEquals(Optional.of("application/json, text/html"), request.headers().firstValue("Accept"));
        }
    }
}
