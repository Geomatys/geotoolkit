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
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.ogcapi.client.AbstractApiTest;
import org.geotoolkit.ogcapi.request.common.GetCollection;
import org.geotoolkit.ogcapi.request.common.GetCollectionList;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class CollectionsApiTest extends AbstractApiTest {

    /**
     * Testing {@linkplain CollectionsApi#getCollection(org.geotoolkit.ogcapi.request.common.GetCollection) };
     * Request a specific collection `my collection`
     */
    @Test
    public void testGetCollection() {
        final CollectionsApi api = new CollectionsApi(newConfig());

        { //check incorrect request : missing collection id
            final var parameters = new GetCollection();
            assertThrows(IllegalArgumentException.class, () -> api.getCollection(parameters));
        }

        { //check valid request
            final var parameters = new GetCollection()
                    .collectionId("my collection")
                    .format("json");
            final var serviceRequest = api.getCollection(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections/my%20collection"
                    + "?f=json", request.uri().toString());
            assertEquals(Optional.of("application/json, text/html"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain CollectionsApi#getCollections(org.geotoolkit.ogcapi.request.common.GetCollections) };
     * Request a list of collection based on a bounding box, a datetime, in html format with a limit of 25.
     */
    @Test
    public void testGetCollections() {
        final CollectionsApi api = new CollectionsApi(newConfig());

        { //check valid request
            final var bbox = new GeneralEnvelope(new double[]{10.0, 20.0}, new double[]{30.0, 40.5});
            final var parameters = new GetCollectionList()
                    .datetime("2018-02-12T00:00:00Z")
                    .bbox(bbox)
                    .limit(25)
                    .format("html");
            final var serviceRequest = api.getCollections(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections"
                    + "?datetime=2018-02-12T00%3A00%3A00Z"
                    + "&bbox=10.0%2C20.0%2C30.0%2C40.5"
                    + "&limit=25"
                    + "&f=html", request.uri().toString());
            assertEquals(Optional.of("application/json, text/html"), request.headers().firstValue("Accept"));
        }
    }
}
