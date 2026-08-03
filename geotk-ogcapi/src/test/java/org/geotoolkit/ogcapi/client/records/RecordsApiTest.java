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
package org.geotoolkit.ogcapi.client.records;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.ogcapi.client.AbstractApiTest;
import org.geotoolkit.ogcapi.request.PreferHandling;
import org.geotoolkit.ogcapi.request.records.CreateRecord;
import org.geotoolkit.ogcapi.request.records.DeleteRecord;
import org.geotoolkit.ogcapi.request.records.GetFacets;
import org.geotoolkit.ogcapi.request.records.GetRecord;
import org.geotoolkit.ogcapi.request.records.GetRecords;
import org.geotoolkit.ogcapi.request.records.ReplaceRecord;
import org.geotoolkit.ogcapi.request.records.UpdateRecord;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class RecordsApiTest extends AbstractApiTest {

    /**
     * Testing {@linkplain RecordsApi#getRecords(org.geotoolkit.ogcapi.request.records.GetRecords) };
     * Request the records of catalog `my catalog`, filtered by a bounding box, a datetime and a
     * free text search, in json format with a limit of 50.
     */
    @Test
    public void testGetRecords() {
        final var api = new RecordsApi(newConfig());

        { //check incorrect request : missing collection id
            final var parameters = new GetRecords();
            assertThrows(IllegalArgumentException.class, () -> api.getRecords(parameters));
        }

        { //check valid request
            final var bbox = new GeneralEnvelope(new double[]{10.0, 20.0}, new double[]{30.0, 40.5});
            final var parameters = new GetRecords()
                    .collectionId("my catalog")
                    .bbox(bbox)
                    .datetime("2018-02-12T00:00:00Z")
                    .q("climate change")
                    .limit(50)
                    .f("json");
            final var serviceRequest = api.getRecords(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections/my%20catalog/items"
                    + "?bbox=10.0%2C20.0%2C30.0%2C40.5"
                    + "&datetime=2018-02-12T00%3A00%3A00Z"
                    + "&q=climate%20change"
                    + "&limit=50"
                    + "&f=json", request.uri().toString());
            assertEquals(Optional.of("application/geo+json, application/json, text/html"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain RecordsApi#getRecord(org.geotoolkit.ogcapi.request.records.GetRecord) };
     * Request a specific record `rec 001` of catalog `art forms`, in html format with an explicit
     * output CRS.
     */
    @Test
    public void testGetRecord() {
        final var api = new RecordsApi(newConfig());

        { //check incorrect request : missing collection id
            final var parameters = new GetRecord()
                    .recordId("rec 001");
            assertThrows(IllegalArgumentException.class, () -> api.getRecord(parameters));
        }

        { //check valid request
            final var parameters = new GetRecord()
                    .collectionId("art forms")
                    .recordId("rec 001")
                    .crs("http://www.opengis.net/def/crs/OGC/1.3/CRS84")
                    .f("html");
            final var serviceRequest = api.getRecord(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections/art%20forms/items/rec%20001"
                    + "?crs=http%3A%2F%2Fwww.opengis.net%2Fdef%2Fcrs%2FOGC%2F1.3%2FCRS84"
                    + "&f=html", request.uri().toString());
            assertEquals(Optional.of("application/geo+json, application/json, text/html"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain RecordsApi#getFacets(org.geotoolkit.ogcapi.request.records.GetFacets) };
     * Request the facets of catalog `sensors data`, in json format.
     */
    @Test
    public void testGetFacets() {
        final var api = new RecordsApi(newConfig());

        { //check incorrect request : missing collection id
            final var parameters = new GetFacets();
            assertThrows(IllegalArgumentException.class, () -> api.getFacets(parameters));
        }

        { //check valid request
            final var parameters = new GetFacets()
                    .collectionId("sensors data")
                    .facets("type:10:count-desc")
                    .f("json");
            final var serviceRequest = api.getFacets(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections/sensors%20data/facets"
                    + "?facets=type%3A10%3Acount-desc"
                    + "&f=json", request.uri().toString());
            assertEquals(Optional.of("application/facets+json, application/json"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain RecordsApi#createRecord(org.geotoolkit.ogcapi.request.records.CreateRecord) };
     * Create a new record in catalog `open data`, as a dry run, with strict handling.
     */
    @Test
    public void testCreateRecord() {
        final var api = new RecordsApi(newConfig());

        { //check incorrect request : missing collection id
            final var parameters = new CreateRecord()
                    .content("{\"type\":\"Feature\"}".getBytes(StandardCharsets.UTF_8))
                    .contentType("application/geo+json");
            assertThrows(IllegalArgumentException.class, () -> api.createRecord(parameters));
        }

        { //check valid request
            final byte[] content = "{\"type\":\"Feature\"}".getBytes(StandardCharsets.UTF_8);
            final var parameters = new CreateRecord()
                    .collectionId("open data")
                    .content(content)
                    .contentType("application/geo+json")
                    .dryRun(true)
                    .handling(PreferHandling.STRICT);
            final var serviceRequest = api.createRecord(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("POST", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections/open%20data/items"
                    + "?dry-run=true", request.uri().toString());
            assertEquals(Optional.of("application/geo+json"), request.headers().firstValue("Content-Type"));
            assertEquals(Optional.of("handling=strict"), request.headers().firstValue("Prefer"));
            assertEquals(content.length, request.bodyPublisher().get().contentLength());
        }
    }

    /**
     * Testing {@linkplain RecordsApi#replaceRecord(org.geotoolkit.ogcapi.request.records.ReplaceRecord) };
     * Replace record `rec 42` of catalog `open data`, with lenient handling.
     */
    @Test
    public void testReplaceRecord() {
        final var api = new RecordsApi(newConfig());

        { //check incorrect request : missing collection id
            final var parameters = new ReplaceRecord()
                    .recordId("rec 42")
                    .content("{\"type\":\"Feature\"}".getBytes(StandardCharsets.UTF_8))
                    .contentType("application/geo+json");
            assertThrows(IllegalArgumentException.class, () -> api.replaceRecord(parameters));
        }

        { //check valid request
            final byte[] content = "{\"type\":\"Feature\"}".getBytes(StandardCharsets.UTF_8);
            final var parameters = new ReplaceRecord()
                    .collectionId("open data")
                    .recordId("rec 42")
                    .content(content)
                    .contentType("application/geo+json")
                    .handling(PreferHandling.LENIENT);
            final var serviceRequest = api.replaceRecord(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("PUT", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections/open%20data/items/rec%2042", request.uri().toString());
            assertEquals(Optional.of("application/geo+json"), request.headers().firstValue("Content-Type"));
            assertEquals(Optional.of("handling=lenient"), request.headers().firstValue("Prefer"));
            assertEquals(content.length, request.bodyPublisher().get().contentLength());
        }
    }

    /**
     * Testing {@linkplain RecordsApi#updateRecord(org.geotoolkit.ogcapi.request.records.UpdateRecord) };
     * Update record `rec 42` of catalog `open data` with a JSON Merge Patch document, as a dry run.
     */
    @Test
    public void testUpdateRecord() {
        final var api = new RecordsApi(newConfig());

        { //check incorrect request : missing collection id
            final var parameters = new UpdateRecord()
                    .recordId("rec 42")
                    .content("{\"title\":\"New title\"}".getBytes(StandardCharsets.UTF_8));
            assertThrows(IllegalArgumentException.class, () -> api.updateRecord(parameters));
        }

        { //check valid request
            final byte[] content = "{\"title\":\"New title\"}".getBytes(StandardCharsets.UTF_8);
            final var parameters = new UpdateRecord()
                    .collectionId("open data")
                    .recordId("rec 42")
                    .content(content)
                    .dryRun(true);
            final var serviceRequest = api.updateRecord(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("PATCH", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections/open%20data/items/rec%2042"
                    + "?dry-run=true", request.uri().toString());
            assertEquals(Optional.of("application/merge-patch+json"), request.headers().firstValue("Content-Type"));
            assertEquals(Optional.empty(), request.headers().firstValue("Prefer"));
            assertEquals(content.length, request.bodyPublisher().get().contentLength());
        }
    }

    /**
     * Testing {@linkplain RecordsApi#deleteRecord(org.geotoolkit.ogcapi.request.records.DeleteRecord) };
     * Delete record `rec 42` of catalog `open data`.
     */
    @Test
    public void testDeleteRecord() {
        final var api = new RecordsApi(newConfig());

        { //check incorrect request : missing collection id
            final var parameters = new DeleteRecord()
                    .recordId("rec 42");
            assertThrows(IllegalArgumentException.class, () -> api.deleteRecord(parameters));
        }

        { //check valid request
            final var parameters = new DeleteRecord()
                    .collectionId("open data")
                    .recordId("rec 42");
            final var serviceRequest = api.deleteRecord(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("DELETE", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections/open%20data/items/rec%2042", request.uri().toString());
        }
    }
}
