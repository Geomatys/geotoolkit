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
package org.geotoolkit.ogcapi.client.joins;

import java.util.List;
import java.util.Optional;
import org.geotoolkit.ogcapi.client.AbstractApiTest;
import org.geotoolkit.ogcapi.request.joins.CreateJoinSource;
import org.geotoolkit.ogcapi.request.joins.DeleteJoinSource;
import org.geotoolkit.ogcapi.request.joins.GetCollectionKeys;
import org.geotoolkit.ogcapi.request.joins.GetJoinSource;
import org.geotoolkit.ogcapi.request.joins.GetJoinSourceList;
import org.geotoolkit.ogcapi.request.joins.JoinFiles;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class JoinsApiTest extends AbstractApiTest {

    /**
     * Testing {@linkplain JoinsApi#getCollectionKeys(org.geotoolkit.ogcapi.request.joins.GetCollectionKeys) };
     * Request the key fields of collection `my collection`.
     */
    @Test
    public void testGetCollectionKeys() {
        final var api = new JoinsApi(newConfig());

        { //check incorrect request : missing collection id
            final var parameters = new GetCollectionKeys();
            assertThrows(IllegalArgumentException.class, () -> api.getCollectionKeys(parameters));
        }

        { //check valid request
            final var parameters = new GetCollectionKeys()
                    .collectionId("my collection")
                    .format("json");
            final var serviceRequest = api.getCollectionKeys(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections/my%20collection/keys"
                    + "?f=json", request.uri().toString());
            assertEquals(Optional.of("application/json, text/html"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain JoinsApi#getJoinSources(org.geotoolkit.ogcapi.request.joins.GetJoinSourceList) };
     * Request the join sources of collection `hydro-rivers`, filtered by a datetime, with a limit of 25, in html format.
     */
    @Test
    public void testGetJoinSources() {
        final var api = new JoinsApi(newConfig());

        { //check incorrect request : missing collection id
            final var parameters = new GetJoinSourceList();
            assertThrows(IllegalArgumentException.class, () -> api.getJoinSources(parameters));
        }

        { //check valid request
            final var parameters = new GetJoinSourceList()
                    .collectionId("hydro-rivers")
                    .limit(25)
                    .datetime("2018-02-12T00:00:00Z")
                    .format("html");
            final var serviceRequest = api.getJoinSources(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections/hydro-rivers/joins"
                    + "?limit=25"
                    + "&datetime=2018-02-12T00%3A00%3A00Z"
                    + "&f=html", request.uri().toString());
            assertEquals(Optional.of("application/json, text/html"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain JoinsApi#createJoinSource(org.geotoolkit.ogcapi.request.joins.CreateJoinSource) };
     * Create a join source on collection `rivers` from a remote CSV attribute dataset.
     *
     * <p>
     * The request body is a {@code multipart/form-data} payload whose boundary is randomly
     * generated for each request ({@link MultipartFormDataBody}), so the exact {@code Content-Type}
     * header value and body bytes cannot be asserted literally.</p>
     */
    @Test
    public void testCreateJoinSource() {
        final var api = new JoinsApi(newConfig());

        { //check incorrect request : missing collection id
            final var parameters = new CreateJoinSource();
            assertThrows(IllegalArgumentException.class, () -> api.createJoinSource(parameters));
        }

        { //check valid request
            final var parameters = new CreateJoinSource()
                    .collectionId("rivers")
                    .collectionKey("river_id")
                    .rightDatasetFormat("http://www.opengis.net/spec/ogcapi-joins-1/1.0/conf/input-csv")
                    .rightDatasetUrl("http://example.com/data.csv")
                    .rightDatasetKey("0")
                    .rightDatasetDataValueList(List.of("1", "2"))
                    .csvFileDelimiter(",")
                    .csvFileHeaderRowNumber(1)
                    .csvFileDataStartRowNumber(2);
            final var serviceRequest = api.createJoinSource(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("POST", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections/rivers/joins", request.uri().toString());
            assertEquals(Optional.of("application/json, text/html"), request.headers().firstValue("Accept"));
            assertTrue(request.headers().firstValue("Content-Type").orElse("").startsWith("multipart/form-data; boundary=----geotk-joins-"));
            assertTrue(request.bodyPublisher().isPresent());
            assertTrue(request.bodyPublisher().get().contentLength() > 0);
        }
    }

    /**
     * Testing {@linkplain JoinsApi#getJoinSource(org.geotoolkit.ogcapi.request.joins.GetJoinSource) };
     * Request the join source `join 1` of collection `rivers`.
     */
    @Test
    public void testGetJoinSource() {
        final var api = new JoinsApi(newConfig());

        { //check incorrect request : missing collection id
            final var parameters = new GetJoinSource();
            assertThrows(IllegalArgumentException.class, () -> api.getJoinSource(parameters));
        }

        { //check valid request
            final var parameters = new GetJoinSource()
                    .collectionId("rivers")
                    .joinId("join 1")
                    .format("json");
            final var serviceRequest = api.getJoinSource(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections/rivers/joins/join%201"
                    + "?f=json", request.uri().toString());
            assertEquals(Optional.of("application/json, text/html"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain JoinsApi#deleteJoinSource(org.geotoolkit.ogcapi.request.joins.DeleteJoinSource) };
     * Delete the join source `join 1` of collection `rivers`.
     */
    @Test
    public void testDeleteJoinSource() {
        final var api = new JoinsApi(newConfig());

        { //check incorrect request : missing collection id
            final var parameters = new DeleteJoinSource();
            assertThrows(IllegalArgumentException.class, () -> api.deleteJoinSource(parameters));
        }

        { //check valid request
            final var parameters = new DeleteJoinSource()
                    .collectionId("rivers")
                    .joinId("join 1");
            final var serviceRequest = api.deleteJoinSource(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("DELETE", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections/rivers/joins/join%201", request.uri().toString());
        }
    }

    /**
     * Testing {@linkplain JoinsApi#joinFiles(org.geotoolkit.ogcapi.request.joins.JoinFiles) };
     * Join a remote GeoJSON dataset with a remote CSV dataset, returning a GeoJSON feature collection.
     *
     * <p>
     * The request body is a {@code multipart/form-data} payload whose boundary is randomly
     * generated for each request ({@link MultipartFormDataBody}), so the exact {@code Content-Type}
     * header value and body bytes cannot be asserted literally.</p>
     */
    @Test
    public void testJoinFiles() {
        final var api = new JoinsApi(newConfig());

        { //check incorrect request : missing left dataset format
            final var parameters = new JoinFiles();
            assertThrows(IllegalArgumentException.class, () -> api.joinFiles(parameters));
        }

        { //check valid request
            final var parameters = new JoinFiles()
                    .leftDatasetFormat("http://www.opengis.net/spec/ogcapi-joins-1/1.0/conf/input-geojson")
                    .leftDatasetUrl("http://example.com/left.geojson")
                    .leftDatasetKey("$.id")
                    .rightDatasetFormat("http://www.opengis.net/spec/ogcapi-joins-1/1.0/conf/input-csv")
                    .rightDatasetUrl("http://example.com/right.csv")
                    .rightDatasetKey("0")
                    .rightDatasetDataValueList(List.of("1", "2"))
                    .csvFileDelimiter(";")
                    .csvFileHeaderRowNumber(1)
                    .csvFileDataStartRowNumber(2);
            final var serviceRequest = api.joinFiles(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("POST", request.method());
            assertEquals("http://localhost:8080/ogcapi/filejoin", request.uri().toString());
            assertEquals(Optional.of("application/geo+json"), request.headers().firstValue("Accept"));
            assertTrue(request.headers().firstValue("Content-Type").orElse("").startsWith("multipart/form-data; boundary=----geotk-joins-"));
            assertTrue(request.bodyPublisher().isPresent());
            assertTrue(request.bodyPublisher().get().contentLength() > 0);
        }
    }
}
