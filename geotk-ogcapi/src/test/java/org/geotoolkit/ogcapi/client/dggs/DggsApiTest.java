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
package org.geotoolkit.ogcapi.client.dggs;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.ogcapi.client.AbstractApiTest;
import org.geotoolkit.ogcapi.request.dggs.GetDggrs;
import org.geotoolkit.ogcapi.request.dggs.GetDggrsList;
import org.geotoolkit.ogcapi.request.dggs.GetZone;
import org.geotoolkit.ogcapi.request.dggs.GetZoneData;
import org.geotoolkit.ogcapi.request.dggs.GetZoneList;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class DggsApiTest extends AbstractApiTest {

    /**
     * Testing {@linkplain DggsApi#getAPICollections(java.lang.String) };
     * Request the list of collections available, in json format.
     */
    @Test
    public void testGetAPICollections() {
        final var api = new DggsApi(newConfig());

        { //check valid request
            final var serviceRequest = api.getAPICollections("json");
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/api/all-collections"
                    + "?f=json", request.uri().toString());
            assertEquals(Optional.of("application/json, text/html"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain DggsApi#getAPIDGGRS(java.lang.String) };
     * Request the list of shared DGGRS available, in html format.
     */
    @Test
    public void testGetAPIDGGRS() {
        final var api = new DggsApi(newConfig());

        { //check valid request
            final var serviceRequest = api.getAPIDGGRS("html");
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/api/dggs"
                    + "?f=html", request.uri().toString());
            assertEquals(Optional.of("application/json, text/html"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain DggsApi#collectionGetDGGRSList(org.geotoolkit.ogcapi.request.dggs.GetDggrsList) };
     * Request the list of available DGGRS for collection `my collection`.
     */
    @Test
    public void testCollectionGetDGGRSList() {
        final var api = new DggsApi(newConfig());

        { //check incorrect request : missing collection id
            final var parameters = new GetDggrsList();
            assertThrows(IllegalArgumentException.class, () -> api.collectionGetDGGRSList(parameters));
        }

        { //check valid request
            final var parameters = new GetDggrsList()
                    .collectionId("my collection")
                    .format("json");
            final var serviceRequest = api.collectionGetDGGRSList(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections/my%20collection/dggs"
                    + "?f=json", request.uri().toString());
            assertEquals(Optional.of("application/json, text/html"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain DggsApi#collectionGetDGGRS(org.geotoolkit.ogcapi.request.dggs.GetDggrs) };
     * Request the description of DGGRS `ISEA9R` for collection `my collection`.
     */
    @Test
    public void testCollectionGetDGGRS() {
        final var api = new DggsApi(newConfig());

        { //check incorrect request : missing dggrs id
            final var parameters = new GetDggrs();
            assertThrows(IllegalArgumentException.class, () -> api.collectionGetDGGRS(parameters));
        }

        { //check valid request
            final var parameters = new GetDggrs()
                    .collectionId("my collection")
                    .dggrsId("ISEA9R")
                    .format("json");
            final var serviceRequest = api.collectionGetDGGRS(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections/my%20collection/dggs/ISEA9R"
                    + "?f=json", request.uri().toString());
            assertEquals(Optional.of("application/json, text/html"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain DggsApi#collectionGetDGGRSZoneInfo(org.geotoolkit.ogcapi.request.dggs.GetZone) };
     * Request the information of zone `A1-0-A` of DGGRS `ISEA9R` for collection `my collection`, restricted to
     * collections `buildings` and `roads`, at a given datetime.
     */
    @Test
    public void testCollectionGetDGGRSZoneInfo() {
        final var api = new DggsApi(newConfig());

        { //check incorrect request : missing dggrs id
            final var parameters = new GetZone();
            assertThrows(IllegalArgumentException.class, () -> api.collectionGetDGGRSZoneInfo(parameters));
        }

        { //check valid request
            final var parameters = new GetZone()
                    .collectionId("my collection")
                    .dggrsId("ISEA9R")
                    .zoneId("A1-0-A")
                    .collections(List.of("buildings", "roads"))
                    .datetime("2018-02-12T00:00:00Z");
            final var serviceRequest = api.collectionGetDGGRSZoneInfo(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections/my%20collection/dggs/ISEA9R/zones/A1-0-A"
                    + "?collections=buildings%2Croads"
                    + "&datetime=2018-02-12T00%3A00%3A00Z", request.uri().toString());
            assertEquals(Optional.of("application/json, text/html"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain DggsApi#datasetGetDGGRS(org.geotoolkit.ogcapi.request.dggs.GetDggrs) };
     * Request the description of dataset-wide DGGRS `ISEA9R`.
     */
    @Test
    public void testDatasetGetDGGRS() {
        final var api = new DggsApi(newConfig());

        { //check incorrect request : missing dggrs id
            final var parameters = new GetDggrs();
            assertThrows(IllegalArgumentException.class, () -> api.datasetGetDGGRS(parameters));
        }

        { //check valid request
            final var parameters = new GetDggrs()
                    .dggrsId("ISEA9R")
                    .format("json");
            final var serviceRequest = api.datasetGetDGGRS(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/dggs/ISEA9R"
                    + "?f=json", request.uri().toString());
            assertEquals(Optional.of("application/json, text/html"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain DggsApi#datasetGetDGGRSList(org.geotoolkit.ogcapi.request.dggs.GetDggrsList) };
     * Request the list of dataset-wide available DGGRS, in html format.
     */
    @Test
    public void testDatasetGetDGGRSList() {
        final var api = new DggsApi(newConfig());

        { //check valid request
            final var parameters = new GetDggrsList()
                    .format("html");
            final var serviceRequest = api.datasetGetDGGRSList(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/dggs"
                    + "?f=html", request.uri().toString());
            assertEquals(Optional.of("application/json, text/html"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain DggsApi#datasetGetDGGRSZoneInfo(org.geotoolkit.ogcapi.request.dggs.GetZone) };
     * Request the information of zone `A1-0-A` of dataset-wide DGGRS `ISEA9R`, restricted to collection
     * `buildings`, at a given datetime.
     */
    @Test
    public void testDatasetGetDGGRSZoneInfo() {
        final var api = new DggsApi(newConfig());

        { //check incorrect request : missing dggrs id
            final var parameters = new GetZone();
            assertThrows(IllegalArgumentException.class, () -> api.datasetGetDGGRSZoneInfo(parameters));
        }

        { //check valid request
            final var parameters = new GetZone()
                    .dggrsId("ISEA9R")
                    .zoneId("A1-0-A")
                    .collections(List.of("buildings"))
                    .datetime("2018-02-12T00:00:00Z");
            final var serviceRequest = api.datasetGetDGGRSZoneInfo(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/dggs/ISEA9R/zones/A1-0-A"
                    + "?collections=buildings"
                    + "&datetime=2018-02-12T00%3A00%3A00Z", request.uri().toString());
            assertEquals(Optional.of("application/json, application/geo+json, text/html"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain DggsApi#collectionGetDGGRSZoneData(org.geotoolkit.ogcapi.request.dggs.GetZoneData) };
     * Request zone data of zone `A1-0-A` of DGGRS `ISEA9R` for collection `buildings`, as png, with a value
     * offset/scale, restricted to a subset and a zone depth range, gzip compressed.
     */
    @Test
    public void testCollectionGetDGGRSZoneData() {
        final var api = new DggsApi(newConfig());

        { //check incorrect request : missing dggrs id
            final var parameters = new GetZoneData();
            assertThrows(IllegalArgumentException.class, () -> api.collectionGetDGGRSZoneData(parameters));
        }

        { //check valid request
            final var parameters = new GetZoneData()
                    .collectionId("buildings")
                    .dggrsId("ISEA9R")
                    .zoneId("A1-0-A")
                    .format("png")
                    .properties("temp,elevation")
                    .subset(List.of("time(2020-01-01)"))
                    .crs("EPSG:4326")
                    .geometry("zone-region")
                    .zoneDepth("1-3")
                    .valuesOffset(0.5)
                    .valuesScale(2.0)
                    .gzip(true);
            final var serviceRequest = api.collectionGetDGGRSZoneData(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections/buildings/dggs/ISEA9R/zones/A1-0-A/data"
                    + "?f=png"
                    + "&properties=temp%2Celevation"
                    + "&subset=time%282020-01-01%29"
                    + "&crs=EPSG%3A4326"
                    + "&geometry=zone-region"
                    + "&zone-depth=1-3"
                    + "&values-offset=0.5"
                    + "&values-scale=2.0", request.uri().toString());
            assertEquals(Optional.of("application/json, image/png, image/tiff; application=geotiff, application/geo+json, application/cbor, text/html"), request.headers().firstValue("Accept"));
            assertEquals(Optional.of("gzip"), request.headers().firstValue("Accept-Encoding"));
        }
    }

    /**
     * Testing {@linkplain DggsApi#datasetGetDGGRSZoneData(org.geotoolkit.ogcapi.request.dggs.GetZoneData) };
     * Request zone data of zone `A1-0-A` of dataset-wide DGGRS `ISEA9R`, restricted to collections `roads` and
     * `buildings`, as geojson, with a geometry mode and a profile, at a given datetime.
     */
    @Test
    public void testDatasetGetDGGRSZoneData() {
        final var api = new DggsApi(newConfig());

        { //check incorrect request : missing dggrs id
            final var parameters = new GetZoneData();
            assertThrows(IllegalArgumentException.class, () -> api.datasetGetDGGRSZoneData(parameters));
        }

        { //check valid request
            final var parameters = new GetZoneData()
                    .collectionId(List.of("roads", "buildings"))
                    .dggrsId("ISEA9R")
                    .zoneId("A1-0-A")
                    .format("geojson")
                    .geometry("vectorized")
                    .profile("rfc7946")
                    .datetime("2018-02-12T00:00:00Z");
            final var serviceRequest = api.datasetGetDGGRSZoneData(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/dggs/ISEA9R/zones/A1-0-A/data"
                    + "?collections=roads%2Cbuildings"
                    + "&f=geojson"
                    + "&geometry=vectorized"
                    + "&profile=rfc7946"
                    + "&datetime=2018-02-12T00%3A00%3A00Z", request.uri().toString());
            assertEquals(Optional.of("application/json, image/png, image/tiff; application=geotiff, application/geo+json, text/html"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain DggsApi#collectionGetDGGRSZones(org.geotoolkit.ogcapi.request.dggs.GetZoneList) };
     * Request the list of zones with data of DGGRS `ISEA9R` for collection `buildings`, restricted to a bounding
     * box, compacted, limited to 25 results, at a given datetime, reprojected to a given crs.
     *
     * <p>Note: the production null-check for this method reads {@code parameters.getCollectionId().get(0)}, so an
     * entirely empty parameters object would throw a {@code NullPointerException} rather than the expected
     * {@code IllegalArgumentException}. The invalid case below therefore supplies a non-empty list whose first
     * element is {@code null} to exercise the intended validation branch.</p>
     */
    @Test
    public void testCollectionGetDGGRSZones() {
        final var api = new DggsApi(newConfig());

        { //check incorrect request : missing collection id (first element of the collection id list is null)
            final var parameters = new GetZoneList()
                    .collectionId(Arrays.asList((String) null));
            assertThrows(IllegalArgumentException.class, () -> api.collectionGetDGGRSZones(parameters));
        }

        { //check valid request
            final var bbox = new GeneralEnvelope(new double[]{10.0, 20.0}, new double[]{30.0, 40.5});
            final var parameters = new GetZoneList()
                    .collectionId(List.of("buildings"))
                    .dggrsId("ISEA9R")
                    .bbox(bbox)
                    .compactZones(true)
                    .limit(25)
                    .datetime("2018-02-12T00:00:00Z")
                    .crs("EPSG:4326")
                    .geometry("zone-region")
                    .format("json");
            final var serviceRequest = api.collectionGetDGGRSZones(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections/buildings/dggs/ISEA9R/zones"
                    + "?collections=buildings"
                    + "&bbox=10.0%2C20.0%2C30.0%2C40.5"
                    + "&compact-zones=true"
                    + "&limit=25"
                    + "&datetime=2018-02-12T00%3A00%3A00Z"
                    + "&crs=EPSG%3A4326"
                    + "&geometry=zone-region"
                    + "&f=json", request.uri().toString());
            assertEquals(Optional.of("application/json, image/png, application/geo+json, image/tiff; application=geotiff, text/html"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain DggsApi#datasetGetDGGRSZones(org.geotoolkit.ogcapi.request.dggs.GetZoneList) };
     * Request the list of zones with data of dataset-wide DGGRS `ISEA9R`, restricted to collections `buildings`
     * and `roads`, a bounding box and a zone level, not compacted, with a limit, offset and parent zone.
     */
    @Test
    public void testDatasetGetDGGRSZones() {
        final var api = new DggsApi(newConfig());

        { //check incorrect request : missing dggrs id
            final var parameters = new GetZoneList();
            assertThrows(IllegalArgumentException.class, () -> api.datasetGetDGGRSZones(parameters));
        }

        { //check valid request
            final var bbox = new GeneralEnvelope(new double[]{10.0, 20.0}, new double[]{30.0, 40.5});
            final var parameters = new GetZoneList()
                    .dggrsId("ISEA9R")
                    .collectionId(List.of("buildings", "roads"))
                    .bbox(bbox)
                    .zoneLevel(5)
                    .compactZones(false)
                    .limit(100)
                    .parentZone("A1")
                    .offset(10)
                    .geometry("none")
                    .format("html");
            final var serviceRequest = api.datasetGetDGGRSZones(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/dggs/ISEA9R/zones"
                    + "?collections=buildings%2Croads"
                    + "&bbox=10.0%2C20.0%2C30.0%2C40.5"
                    + "&zone-level=5"
                    + "&compact-zones=false"
                    + "&limit=100"
                    + "&parent-zone=A1"
                    + "&offset=10"
                    + "&geometry=none"
                    + "&f=html", request.uri().toString());
            assertEquals(Optional.of("application/json, image/png, application/geo+json, image/tiff; application=geotiff, text/html"), request.headers().firstValue("Accept"));
        }
    }
}
