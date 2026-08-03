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
package org.geotoolkit.ogcapi.client.edr;

import java.util.List;
import java.util.Optional;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.ogcapi.client.AbstractApiTest;
import org.geotoolkit.ogcapi.request.common.GetCollection;
import org.geotoolkit.ogcapi.request.edr.GetArea;
import org.geotoolkit.ogcapi.request.edr.GetCorridor;
import org.geotoolkit.ogcapi.request.edr.GetCube;
import org.geotoolkit.ogcapi.request.edr.GetEdrInstances;
import org.geotoolkit.ogcapi.request.edr.GetLocation;
import org.geotoolkit.ogcapi.request.edr.GetLocations;
import org.geotoolkit.ogcapi.request.edr.GetPosition;
import org.geotoolkit.ogcapi.request.edr.GetRadius;
import org.geotoolkit.ogcapi.request.edr.GetTrajectory;
import org.geotoolkit.ogcapi.request.feature.GetItem;
import org.geotoolkit.ogcapi.request.feature.GetItems;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class EdrApiTest extends AbstractApiTest {

    private static final GeometryFactory GF = new GeometryFactory();

    /**
     * Testing {@linkplain EdrApi#getCollection(org.geotoolkit.ogcapi.request.common.GetCollection) };
     * Request the description of an EDR collection whose identifier contains a space.
     */
    @Test
    public void testGetCollection() {
        final var api = new EdrApi(newConfig());

        { //check incorrect request : missing collection id
            final var parameters = new GetCollection();
            assertThrows(IllegalArgumentException.class, () -> api.getCollection(parameters));
        }

        { //check valid request
            final var parameters = new GetCollection()
                    .collectionId("ocean temp")
                    .format("json");
            final var serviceRequest = api.getCollection(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections/ocean%20temp"
                    + "?f=json", request.uri().toString());
            assertEquals(Optional.of("application/json, text/html"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain EdrApi#getInstances(org.geotoolkit.ogcapi.request.edr.GetEdrInstances) };
     * Request the list of instances of a collection, in html format.
     */
    @Test
    public void testGetInstances() {
        final var api = new EdrApi(newConfig());

        { //check incorrect request : missing collection id
            final var parameters = new GetEdrInstances();
            assertThrows(IllegalArgumentException.class, () -> api.getInstances(parameters));
        }

        { //check valid request
            final var parameters = new GetEdrInstances()
                    .collectionId("ocean")
                    .f("html");
            final var serviceRequest = api.getInstances(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections/ocean/instances"
                    + "?f=html", request.uri().toString());
            assertEquals(Optional.of("application/json, text/html"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain EdrApi#getPosition(org.geotoolkit.ogcapi.request.edr.GetPosition) };
     * Request data at a point, on a collection instance, with a vertical filter, a temporal
     * filter, a subset of parameters, a CRS, a limit and a response format.
     */
    @Test
    public void testGetPosition() {
        final var api = new EdrApi(newConfig());

        { //check incorrect request : missing collection id
            final var parameters = new GetPosition();
            assertThrows(IllegalArgumentException.class, () -> api.getPosition(parameters));
        }

        { //check valid request
            final Geometry coords = GF.createPoint(new Coordinate(10, 20));
            final var parameters = new GetPosition()
                    .collectionId("ocean")
                    .instanceId("fcst01")
                    .coords(coords)
                    .z("10/100")
                    .datetime("2023-06-01T12:00:00Z")
                    .parameterName(List.of("temperature", "salinity"))
                    .crs("EPSG:4326")
                    .limit(10)
                    .f("coveragejson");
            final var serviceRequest = api.getPosition(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections/ocean/instances/fcst01/position"
                    + "?coords=POINT%20%2810%2020%29"
                    + "&z=10%2F100"
                    + "&datetime=2023-06-01T12%3A00%3A00Z"
                    + "&parameter-name=temperature%2Csalinity"
                    + "&crs=EPSG%3A4326"
                    + "&limit=10"
                    + "&f=coveragejson", request.uri().toString());
            assertEquals(Optional.of("application/vnd.cov+json, application/geo+json, text/xml"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain EdrApi#getRadius(org.geotoolkit.ogcapi.request.edr.GetRadius) };
     * Request data within a radius of a point, with a unit, a vertical filter, a temporal
     * filter, a parameter, a CRS, a limit and a response format.
     */
    @Test
    public void testGetRadius() {
        final var api = new EdrApi(newConfig());

        { //check incorrect request : missing collection id
            final var parameters = new GetRadius();
            assertThrows(IllegalArgumentException.class, () -> api.getRadius(parameters));
        }

        { //check valid request
            final Geometry coords = GF.createPoint(new Coordinate(5, 15));
            final var parameters = new GetRadius()
                    .collectionId("ocean")
                    .coords(coords)
                    .within(50.0)
                    .withinUnits("km")
                    .z("10")
                    .datetime("2023-06-01T12:00:00Z")
                    .parameterName(List.of("temperature"))
                    .crs("EPSG:4326")
                    .limit(5)
                    .f("coveragejson");
            final var serviceRequest = api.getRadius(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections/ocean/radius"
                    + "?coords=POINT%20%285%2015%29"
                    + "&within=50.0"
                    + "&within-units=km"
                    + "&z=10"
                    + "&datetime=2023-06-01T12%3A00%3A00Z"
                    + "&parameter-name=temperature"
                    + "&crs=EPSG%3A4326"
                    + "&limit=5"
                    + "&f=coveragejson", request.uri().toString());
            assertEquals(Optional.of("application/vnd.cov+json, application/geo+json, text/xml"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain EdrApi#getArea(org.geotoolkit.ogcapi.request.edr.GetArea) };
     * Request data within a polygon area, with a vertical filter, a temporal filter, a
     * parameter, a CRS, a resolution, a limit and a response format.
     */
    @Test
    public void testGetArea() {
        final var api = new EdrApi(newConfig());

        { //check incorrect request : missing collection id
            final var parameters = new GetArea();
            assertThrows(IllegalArgumentException.class, () -> api.getArea(parameters));
        }

        { //check valid request
            final Geometry coords = GF.createPolygon(new Coordinate[]{
                new Coordinate(0, 0),
                new Coordinate(10, 0),
                new Coordinate(0, 10),
                new Coordinate(0, 0)
            });
            final var parameters = new GetArea()
                    .collectionId("ocean")
                    .coords(coords)
                    .z("10")
                    .datetime("2023-06-01T12:00:00Z")
                    .parameterName(List.of("temperature"))
                    .crs("EPSG:4326")
                    .resolutionX(0.5)
                    .resolutionY(0.5)
                    .limit(20)
                    .f("coveragejson");
            final var serviceRequest = api.getArea(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections/ocean/area"
                    + "?coords=POLYGON%20%28%280%200%2C%2010%200%2C%200%2010%2C%200%200%29%29"
                    + "&z=10"
                    + "&datetime=2023-06-01T12%3A00%3A00Z"
                    + "&parameter-name=temperature"
                    + "&crs=EPSG%3A4326"
                    + "&resolution-x=0.5"
                    + "&resolution-y=0.5"
                    + "&limit=20"
                    + "&f=coveragejson", request.uri().toString());
            assertEquals(Optional.of("application/vnd.cov+json, application/geo+json, text/xml"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain EdrApi#getCube(org.geotoolkit.ogcapi.request.edr.GetCube) };
     * Request a data cube over a bounding box, with a vertical filter, a temporal filter, a
     * parameter, a resolution, a CRS and a response format.
     */
    @Test
    public void testGetCube() {
        final var api = new EdrApi(newConfig());

        { //check incorrect request : missing collection id
            final var parameters = new GetCube();
            assertThrows(IllegalArgumentException.class, () -> api.getCube(parameters));
        }

        { //check valid request
            final var bbox = new GeneralEnvelope(new double[]{10.0, 20.0}, new double[]{30.0, 40.5});
            final var parameters = new GetCube()
                    .collectionId("ocean")
                    .bbox(bbox)
                    .z("10")
                    .datetime("2023-06-01T12:00:00Z")
                    .parameterName(List.of("temperature"))
                    .resolutionX(0.5)
                    .resolutionY(0.5)
                    .resolutionZ(1.0)
                    .crs("EPSG:4326")
                    .f("coveragejson");
            final var serviceRequest = api.getCube(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections/ocean/cube"
                    + "?bbox=10.0%2C20.0%2C30.0%2C40.5"
                    + "&z=10"
                    + "&datetime=2023-06-01T12%3A00%3A00Z"
                    + "&parameter-name=temperature"
                    + "&resolution-x=0.5"
                    + "&resolution-y=0.5"
                    + "&resolution-z=1.0"
                    + "&crs=EPSG%3A4326"
                    + "&f=coveragejson", request.uri().toString());
            assertEquals(Optional.of("application/vnd.cov+json, application/geo+json, text/xml"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain EdrApi#getTrajectory(org.geotoolkit.ogcapi.request.edr.GetTrajectory) };
     * Request data along a trajectory, with a vertical filter, a temporal filter, a parameter,
     * a CRS and a response format.
     */
    @Test
    public void testGetTrajectory() {
        final var api = new EdrApi(newConfig());

        { //check incorrect request : missing collection id
            final var parameters = new GetTrajectory();
            assertThrows(IllegalArgumentException.class, () -> api.getTrajectory(parameters));
        }

        { //check valid request
            final Geometry coords = GF.createLineString(new Coordinate[]{
                new Coordinate(0, 0),
                new Coordinate(10, 10),
                new Coordinate(20, 5)
            });
            final var parameters = new GetTrajectory()
                    .collectionId("ocean")
                    .coords(coords)
                    .z("10")
                    .datetime("2023-06-01T12:00:00Z")
                    .parameterName(List.of("temperature"))
                    .crs("EPSG:4326")
                    .f("coveragejson");
            final var serviceRequest = api.getTrajectory(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections/ocean/trajectory"
                    + "?coords=LINESTRING%20%280%200%2C%2010%2010%2C%2020%205%29"
                    + "&z=10"
                    + "&datetime=2023-06-01T12%3A00%3A00Z"
                    + "&parameter-name=temperature"
                    + "&crs=EPSG%3A4326"
                    + "&f=coveragejson", request.uri().toString());
            assertEquals(Optional.of("application/vnd.cov+json, application/geo+json, text/xml"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain EdrApi#getCorridor(org.geotoolkit.ogcapi.request.edr.GetCorridor) };
     * Request data along a corridor, with a width and height (and their units), a resolution, a
     * vertical filter, a temporal filter, a parameter, a CRS and a response format.
     */
    @Test
    public void testGetCorridor() {
        final var api = new EdrApi(newConfig());

        { //check incorrect request : missing collection id
            final var parameters = new GetCorridor();
            assertThrows(IllegalArgumentException.class, () -> api.getCorridor(parameters));
        }

        { //check valid request
            final Geometry coords = GF.createLineString(new Coordinate[]{
                new Coordinate(0, 0),
                new Coordinate(10, 0)
            });
            final var parameters = new GetCorridor()
                    .collectionId("ocean")
                    .coords(coords)
                    .z("10")
                    .datetime("2023-06-01T12:00:00Z")
                    .parameterName(List.of("temperature"))
                    .corridorWidth(5.0)
                    .widthUnits("km")
                    .corridorHeight(2.0)
                    .heightUnits("m")
                    .resolutionX(0.5)
                    .resolutionY(0.5)
                    .crs("EPSG:4326")
                    .f("coveragejson");
            final var serviceRequest = api.getCorridor(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections/ocean/corridor"
                    + "?coords=LINESTRING%20%280%200%2C%2010%200%29"
                    + "&z=10"
                    + "&datetime=2023-06-01T12%3A00%3A00Z"
                    + "&parameter-name=temperature"
                    + "&corridor-width=5.0"
                    + "&width-units=km"
                    + "&corridor-height=2.0"
                    + "&height-units=m"
                    + "&resolution-x=0.5"
                    + "&resolution-y=0.5"
                    + "&crs=EPSG%3A4326"
                    + "&f=coveragejson", request.uri().toString());
            assertEquals(Optional.of("application/vnd.cov+json, application/geo+json, text/xml"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain EdrApi#getLocations(org.geotoolkit.ogcapi.request.edr.GetLocations) };
     * Request the list of locations of a collection, filtered by a bounding box, a temporal
     * filter and a limit.
     */
    @Test
    public void testGetLocations() {
        final var api = new EdrApi(newConfig());

        { //check incorrect request : missing collection id
            final var parameters = new GetLocations();
            assertThrows(IllegalArgumentException.class, () -> api.getLocations(parameters));
        }

        { //check valid request
            final var bbox = new GeneralEnvelope(new double[]{10.0, 20.0}, new double[]{30.0, 40.5});
            final var parameters = new GetLocations()
                    .collectionId("ocean")
                    .bbox(bbox)
                    .datetime("2023-06-01T12:00:00Z")
                    .limit(10)
                    .f("json");
            final var serviceRequest = api.getLocations(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections/ocean/locations"
                    + "?bbox=10.0%2C20.0%2C30.0%2C40.5"
                    + "&datetime=2023-06-01T12%3A00%3A00Z"
                    + "&limit=10"
                    + "&f=json", request.uri().toString());
            assertEquals(Optional.of("application/geo+json, application/json, text/html"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain EdrApi#getLocation(org.geotoolkit.ogcapi.request.edr.GetLocation) };
     * Request data for a named location, with a temporal filter, a parameter, a CRS, a limit
     * and a response format.
     */
    @Test
    public void testGetLocation() {
        final var api = new EdrApi(newConfig());

        { //check incorrect request : missing collection id
            final var parameters = new GetLocation();
            assertThrows(IllegalArgumentException.class, () -> api.getLocation(parameters));
        }

        { //check valid request
            final var parameters = new GetLocation()
                    .collectionId("ocean")
                    .locationId("loc1")
                    .datetime("2023-06-01T12:00:00Z")
                    .parameterName(List.of("temperature"))
                    .crs("EPSG:4326")
                    .limit(5)
                    .f("coveragejson");
            final var serviceRequest = api.getLocation(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections/ocean/locations/loc1"
                    + "?datetime=2023-06-01T12%3A00%3A00Z"
                    + "&parameter-name=temperature"
                    + "&crs=EPSG%3A4326"
                    + "&limit=5"
                    + "&f=coveragejson", request.uri().toString());
            assertEquals(Optional.of("application/vnd.cov+json, application/geo+json, text/xml"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain EdrApi#getItems(org.geotoolkit.ogcapi.request.feature.GetItems) };
     * Request the list of items of a collection, filtered by a bounding box, a temporal filter
     * and a limit.
     */
    @Test
    public void testGetItems() {
        final var api = new EdrApi(newConfig());

        { //check incorrect request : missing collection id
            final var parameters = new GetItems();
            assertThrows(IllegalArgumentException.class, () -> api.getItems(parameters));
        }

        { //check valid request
            final var bbox = new GeneralEnvelope(new double[]{10.0, 20.0}, new double[]{30.0, 40.5});
            final var parameters = new GetItems()
                    .collectionId("ocean")
                    .bbox(bbox)
                    .datetime("2023-06-01T12:00:00Z")
                    .limit(10)
                    .f("json");
            final var serviceRequest = api.getItems(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections/ocean/items"
                    + "?bbox=10.0%2C20.0%2C30.0%2C40.5"
                    + "&datetime=2023-06-01T12%3A00%3A00Z"
                    + "&limit=10"
                    + "&f=json", request.uri().toString());
            assertEquals(Optional.of("application/geo+json, application/json, text/html"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain EdrApi#getItem(org.geotoolkit.ogcapi.request.feature.GetItem) };
     * Request a single item whose identifier contains a space.
     */
    @Test
    public void testGetItem() {
        final var api = new EdrApi(newConfig());

        { //check incorrect request : missing collection id
            final var parameters = new GetItem();
            assertThrows(IllegalArgumentException.class, () -> api.getItem(parameters));
        }

        { //check valid request
            final var parameters = new GetItem()
                    .collectionId("ocean")
                    .itemId("item 1")
                    .f("json");
            final var serviceRequest = api.getItem(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections/ocean/items/item%201"
                    + "?f=json", request.uri().toString());
            assertEquals(Optional.of("application/geo+json, application/json, text/html"), request.headers().firstValue("Accept"));
        }
    }
}
