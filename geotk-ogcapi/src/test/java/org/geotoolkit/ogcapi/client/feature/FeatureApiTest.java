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
package org.geotoolkit.ogcapi.client.feature;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.ogcapi.client.AbstractApiTest;
import org.geotoolkit.ogcapi.request.PreferHandling;
import org.geotoolkit.ogcapi.request.feature.CreateItem;
import org.geotoolkit.ogcapi.request.feature.DeleteItem;
import org.geotoolkit.ogcapi.request.feature.GetItem;
import org.geotoolkit.ogcapi.request.feature.GetItems;
import org.geotoolkit.ogcapi.request.feature.ReplaceItem;
import org.geotoolkit.ogcapi.request.feature.UpdateItem;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FeatureApiTest extends AbstractApiTest {

    private static final String ACCEPT_FEATURES = "application/geo+json, application/json, text/html";

    /**
     * Testing {@linkplain FeatureApi#getFunctions() };
     * Request the list of supported CQL2 functions.
     */
    @Test
    public void testGetFunctions() {
        final var api = new FeatureApi(newConfig());

        { //check valid request
            final var serviceRequest = api.getFunctions();
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/functions", request.uri().toString());
            assertEquals(Optional.of("application/json"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain FeatureApi#getItems(org.geotoolkit.ogcapi.request.feature.GetItems) };
     * Request the features of collection `green spaces` filtered on a bounding box, a datetime, a
     * limit, an offset, a crs, a CQL2 filter with a space and quotes, and a filter language, in
     * json format.
     */
    @Test
    public void testGetItems() {
        final var api = new FeatureApi(newConfig());

        { //check incorrect request : missing collection id
            final var parameters = new GetItems();
            assertThrows(IllegalArgumentException.class, () -> api.getItems(parameters));
        }

        { //check valid request
            final var bbox = new GeneralEnvelope(new double[]{10.0, 20.0}, new double[]{30.0, 40.5});
            final var parameters = new GetItems()
                    .collectionId("green spaces")
                    .bbox(bbox)
                    .datetime("2018-02-12T00:00:00Z")
                    .limit(10)
                    .offset(5)
                    .crs("http://www.opengis.net/def/crs/OGC/1.3/CRS84")
                    .filter("name = 'Springfield'")
                    .filterLang("cql2-text")
                    .f("json");
            final var serviceRequest = api.getItems(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections/green%20spaces/items"
                    + "?bbox=10.0%2C20.0%2C30.0%2C40.5"
                    + "&datetime=2018-02-12T00%3A00%3A00Z"
                    + "&limit=10"
                    + "&offset=5"
                    + "&crs=http%3A%2F%2Fwww.opengis.net%2Fdef%2Fcrs%2FOGC%2F1.3%2FCRS84"
                    + "&filter=name%20%3D%20%27Springfield%27"
                    + "&filter-lang=cql2-text"
                    + "&f=json", request.uri().toString());
            assertEquals(Optional.of(ACCEPT_FEATURES), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain FeatureApi#getItem(org.geotoolkit.ogcapi.request.feature.GetItem) };
     * Request feature `tree 42` of collection `green spaces` with a crs, in html format.
     */
    @Test
    public void testGetItem() {
        final var api = new FeatureApi(newConfig());

        { //check incorrect request : missing collection id
            final var parameters = new GetItem();
            assertThrows(IllegalArgumentException.class, () -> api.getItem(parameters));
        }

        { //check valid request
            final var parameters = new GetItem()
                    .collectionId("green spaces")
                    .itemId("tree 42")
                    .crs("urn:ogc:def:crs:OGC::CRS84")
                    .f("html");
            final var serviceRequest = api.getItem(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections/green%20spaces/items/tree%2042"
                    + "?crs=urn%3Aogc%3Adef%3Acrs%3AOGC%3A%3ACRS84"
                    + "&f=html", request.uri().toString());
            assertEquals(Optional.of(ACCEPT_FEATURES), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain FeatureApi#createItem(org.geotoolkit.ogcapi.request.feature.CreateItem) };
     * Create a new feature in collection `parks` with a dry-run and a Prefer handling, then
     * without either.
     */
    @Test
    public void testCreateItem() {
        final var api = new FeatureApi(newConfig());

        { //check incorrect request : missing collection id
            final var parameters = new CreateItem();
            assertThrows(IllegalArgumentException.class, () -> api.createItem(parameters));
        }

        { //check valid request : with dry-run and Prefer handling
            final byte[] content = "{\"type\":\"Feature\",\"properties\":{}}".getBytes(StandardCharsets.UTF_8);
            final var parameters = new CreateItem()
                    .collectionId("parks")
                    .content(content)
                    .contentType("application/geo+json")
                    .dryRun(true)
                    .handling(PreferHandling.STRICT);
            final var serviceRequest = api.createItem(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("POST", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections/parks/items"
                    + "?dry-run=true", request.uri().toString());
            assertEquals(Optional.of("application/geo+json"), request.headers().firstValue("Content-Type"));
            assertEquals(Optional.of("handling=strict"), request.headers().firstValue("Prefer"));
            assertEquals(content.length, request.bodyPublisher().get().contentLength());
        }

        { //check valid request : without dry-run nor Prefer handling
            final byte[] content = "{\"type\":\"Feature\"}".getBytes(StandardCharsets.UTF_8);
            final var parameters = new CreateItem()
                    .collectionId("parks")
                    .content(content)
                    .contentType("application/geo+json");
            final var serviceRequest = api.createItem(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("POST", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections/parks/items", request.uri().toString());
            assertEquals(Optional.of("application/geo+json"), request.headers().firstValue("Content-Type"));
            assertEquals(Optional.empty(), request.headers().firstValue("Prefer"));
            assertEquals(content.length, request.bodyPublisher().get().contentLength());
        }
    }

    /**
     * Testing {@linkplain FeatureApi#replaceItem(org.geotoolkit.ogcapi.request.feature.ReplaceItem) };
     * Replace feature `42` of collection `parks` with a dry-run and a Prefer handling.
     */
    @Test
    public void testReplaceItem() {
        final var api = new FeatureApi(newConfig());

        { //check incorrect request : missing collection id
            final var parameters = new ReplaceItem();
            assertThrows(IllegalArgumentException.class, () -> api.replaceItem(parameters));
        }

        { //check valid request
            final byte[] content = "{\"type\":\"Feature\",\"properties\":{\"status\":\"open\"}}".getBytes(StandardCharsets.UTF_8);
            final var parameters = new ReplaceItem()
                    .collectionId("parks")
                    .itemId("42")
                    .content(content)
                    .contentType("application/geo+json")
                    .dryRun(false)
                    .handling(PreferHandling.LENIENT);
            final var serviceRequest = api.replaceItem(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("PUT", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections/parks/items/42"
                    + "?dry-run=false", request.uri().toString());
            assertEquals(Optional.of("application/geo+json"), request.headers().firstValue("Content-Type"));
            assertEquals(Optional.of("handling=lenient"), request.headers().firstValue("Prefer"));
            assertEquals(content.length, request.bodyPublisher().get().contentLength());
        }
    }

    /**
     * Testing {@linkplain FeatureApi#updateItem(org.geotoolkit.ogcapi.request.feature.UpdateItem) };
     * Update feature `42` of collection `parks` with a JSON Merge Patch document and a dry-run,
     * without a Prefer handling.
     */
    @Test
    public void testUpdateItem() {
        final var api = new FeatureApi(newConfig());

        { //check incorrect request : missing collection id
            final var parameters = new UpdateItem();
            assertThrows(IllegalArgumentException.class, () -> api.updateItem(parameters));
        }

        { //check valid request
            final byte[] content = "{\"properties\":{\"status\":\"closed\"}}".getBytes(StandardCharsets.UTF_8);
            final var parameters = new UpdateItem()
                    .collectionId("parks")
                    .itemId("42")
                    .content(content)
                    .dryRun(true);
            final var serviceRequest = api.updateItem(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("PATCH", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections/parks/items/42"
                    + "?dry-run=true", request.uri().toString());
            assertEquals(Optional.of("application/merge-patch+json"), request.headers().firstValue("Content-Type"));
            assertEquals(Optional.empty(), request.headers().firstValue("Prefer"));
            assertEquals(content.length, request.bodyPublisher().get().contentLength());
        }
    }

    /**
     * Testing {@linkplain FeatureApi#deleteItem(org.geotoolkit.ogcapi.request.feature.DeleteItem) };
     * Delete feature `42` of collection `parks`.
     */
    @Test
    public void testDeleteItem() {
        final var api = new FeatureApi(newConfig());

        { //check incorrect request : missing collection id
            final var parameters = new DeleteItem();
            assertThrows(IllegalArgumentException.class, () -> api.deleteItem(parameters));
        }

        { //check valid request
            final var parameters = new DeleteItem()
                    .collectionId("parks")
                    .itemId("42");
            final var serviceRequest = api.deleteItem(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("DELETE", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections/parks/items/42", request.uri().toString());
        }
    }

}
