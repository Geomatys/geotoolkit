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
import org.geotoolkit.ogcapi.request.common.GetCollectionQueryables;
import org.geotoolkit.ogcapi.request.common.GetCollectionSchema;
import org.geotoolkit.ogcapi.request.common.GetCollectionSortables;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class SchemaApiTest extends AbstractApiTest {

    /**
     * Testing {@linkplain SchemaApi#getSchema(org.geotoolkit.ogcapi.request.common.GetCollectionSchema) };
     * Request the dataset-level schema, then the schema of collection `my schema`.
     */
    @Test
    public void testGetSchema() {
        final SchemaApi api = new SchemaApi(newConfig());

        { //check valid request : dataset-level schema, no collection id
            final var parameters = new GetCollectionSchema()
                    .format("json");
            final var serviceRequest = api.getSchema(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/schema"
                    + "?f=json", request.uri().toString());
            assertEquals(Optional.of("application/json"), request.headers().firstValue("Accept"));
        }

        { //check valid request : collection-level schema
            final var parameters = new GetCollectionSchema()
                    .collectionId("my schema")
                    .format("json");
            final var serviceRequest = api.getSchema(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections/my%20schema/schema"
                    + "?f=json", request.uri().toString());
            assertEquals(Optional.of("application/json"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain SchemaApi#getCollectionQueryables(org.geotoolkit.ogcapi.request.common.GetCollectionQueryables) };
     * Request the queryables of collection `buildings`.
     */
    @Test
    public void testGetCollectionQueryables() {
        final SchemaApi api = new SchemaApi(newConfig());

        { //check incorrect request : missing collection id
            final var parameters = new GetCollectionQueryables();
            assertThrows(IllegalArgumentException.class, () -> api.getCollectionQueryables(parameters));
        }

        { //check valid request
            final var parameters = new GetCollectionQueryables()
                    .collectionId("buildings")
                    .format("json");
            final var serviceRequest = api.getCollectionQueryables(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections/buildings/queryables"
                    + "?f=json", request.uri().toString());
            assertEquals(Optional.of("application/json"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain SchemaApi#getCollectionSortables(org.geotoolkit.ogcapi.request.common.GetCollectionSortables) };
     * Request the sortables of collection `roads`, in html format.
     */
    @Test
    public void testGetCollectionSortables() {
        final SchemaApi api = new SchemaApi(newConfig());

        { //check incorrect request : missing collection id
            final var parameters = new GetCollectionSortables();
            assertThrows(IllegalArgumentException.class, () -> api.getCollectionSortables(parameters));
        }

        { //check valid request
            final var parameters = new GetCollectionSortables()
                    .collectionId("roads")
                    .format("html");
            final var serviceRequest = api.getCollectionSortables(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections/roads/sortables"
                    + "?f=html", request.uri().toString());
            assertEquals(Optional.of("application/json"), request.headers().firstValue("Accept"));
        }
    }
}
