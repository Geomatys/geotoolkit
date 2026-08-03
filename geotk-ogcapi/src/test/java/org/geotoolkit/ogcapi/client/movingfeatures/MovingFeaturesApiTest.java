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
package org.geotoolkit.ogcapi.client.movingfeatures;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.ogcapi.client.AbstractApiTest;
import org.geotoolkit.ogcapi.request.movingfeatures.CreateCollection;
import org.geotoolkit.ogcapi.request.movingfeatures.DeleteCollection;
import org.geotoolkit.ogcapi.request.movingfeatures.DeleteMovingFeature;
import org.geotoolkit.ogcapi.request.movingfeatures.DeleteTemporalGeometry;
import org.geotoolkit.ogcapi.request.movingfeatures.DeleteTemporalPrimitiveValue;
import org.geotoolkit.ogcapi.request.movingfeatures.DeleteTemporalProperty;
import org.geotoolkit.ogcapi.request.movingfeatures.GetAcceleration;
import org.geotoolkit.ogcapi.request.movingfeatures.GetDistance;
import org.geotoolkit.ogcapi.request.movingfeatures.GetMovingFeature;
import org.geotoolkit.ogcapi.request.movingfeatures.GetMovingFeatures;
import org.geotoolkit.ogcapi.request.movingfeatures.GetTemporalGeometrySequence;
import org.geotoolkit.ogcapi.request.movingfeatures.GetTemporalProperties;
import org.geotoolkit.ogcapi.request.movingfeatures.GetTemporalProperty;
import org.geotoolkit.ogcapi.request.movingfeatures.GetVelocity;
import org.geotoolkit.ogcapi.request.movingfeatures.InsertMovingFeatures;
import org.geotoolkit.ogcapi.request.movingfeatures.InsertTemporalGeometry;
import org.geotoolkit.ogcapi.request.movingfeatures.InsertTemporalPrimitiveValue;
import org.geotoolkit.ogcapi.request.movingfeatures.InsertTemporalProperty;
import org.geotoolkit.ogcapi.request.movingfeatures.ReplaceCollection;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class MovingFeaturesApiTest extends AbstractApiTest {

    /**
     * Testing {@linkplain MovingFeaturesApi#createCollection(org.geotoolkit.ogcapi.request.movingfeatures.CreateCollection) };
     * Register a new moving feature collection with a raw JSON body.
     */
    @Test
    public void testCreateCollection() {
        final MovingFeaturesApi api = new MovingFeaturesApi(newConfig());

        { //check incorrect request : missing content
            final var parameters = new CreateCollection();
            assertThrows(IllegalArgumentException.class, () -> api.createCollection(parameters));
        }

        { //check valid request
            final byte[] content = "{\"title\":\"Fleet A\"}".getBytes(StandardCharsets.UTF_8);
            final var parameters = new CreateCollection()
                    .content(content);
            final var serviceRequest = api.createCollection(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("POST", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections", request.uri().toString());
            assertEquals(Optional.of("application/json"), request.headers().firstValue("Content-Type"));
            assertEquals(content.length, request.bodyPublisher().get().contentLength());
        }
    }

    /**
     * Testing {@linkplain MovingFeaturesApi#replaceCollection(org.geotoolkit.ogcapi.request.movingfeatures.ReplaceCollection) };
     * Replace the metadata of the collection `fleet one`.
     */
    @Test
    public void testReplaceCollection() {
        final MovingFeaturesApi api = new MovingFeaturesApi(newConfig());

        { //check incorrect request : missing collection id
            final var parameters = new ReplaceCollection()
                    .content("{}".getBytes(StandardCharsets.UTF_8));
            assertThrows(IllegalArgumentException.class, () -> api.replaceCollection(parameters));
        }

        { //check valid request
            final byte[] content = "{\"title\":\"Fleet A updated\"}".getBytes(StandardCharsets.UTF_8);
            final var parameters = new ReplaceCollection()
                    .collectionId("fleet one")
                    .content(content);
            final var serviceRequest = api.replaceCollection(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("PUT", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections/fleet%20one", request.uri().toString());
            assertEquals(Optional.of("application/json"), request.headers().firstValue("Content-Type"));
            assertEquals(content.length, request.bodyPublisher().get().contentLength());
        }
    }

    /**
     * Testing {@linkplain MovingFeaturesApi#deleteCollection(org.geotoolkit.ogcapi.request.movingfeatures.DeleteCollection) };
     * Delete the collection `fleet 42`.
     */
    @Test
    public void testDeleteCollection() {
        final MovingFeaturesApi api = new MovingFeaturesApi(newConfig());

        { //check incorrect request : missing collection id
            final var parameters = new DeleteCollection();
            assertThrows(IllegalArgumentException.class, () -> api.deleteCollection(parameters));
        }

        { //check valid request
            final var parameters = new DeleteCollection()
                    .collectionId("fleet 42");
            final var serviceRequest = api.deleteCollection(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("DELETE", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections/fleet%2042", request.uri().toString());
        }
    }

    /**
     * Testing {@linkplain MovingFeaturesApi#getMovingFeatures(org.geotoolkit.ogcapi.request.movingfeatures.GetMovingFeatures) };
     * List the moving features of `fleet1` intersecting a bbox, restricted to a datetime interval,
     * with a limit and a subtrajectory flag.
     */
    @Test
    public void testGetMovingFeatures() {
        final MovingFeaturesApi api = new MovingFeaturesApi(newConfig());

        { //check incorrect request : missing collection id
            final var parameters = new GetMovingFeatures()
                    .datetime("2018-02-12T00:00:00Z");
            assertThrows(IllegalArgumentException.class, () -> api.getMovingFeatures(parameters));
        }

        { //check valid request
            final var bbox = new GeneralEnvelope(new double[]{10.0, 20.0}, new double[]{30.0, 40.5});
            final var parameters = new GetMovingFeatures()
                    .collectionId("fleet1")
                    .bbox(bbox)
                    .datetime("2018-02-12T00:00:00Z")
                    .limit(25)
                    .subtrajectory(true);
            final var serviceRequest = api.getMovingFeatures(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections/fleet1/items"
                    + "?bbox=10.0%2C20.0%2C30.0%2C40.5"
                    + "&datetime=2018-02-12T00%3A00%3A00Z"
                    + "&limit=25"
                    + "&subTrajectory=true", request.uri().toString());
            assertEquals(Optional.of("application/json"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain MovingFeaturesApi#insertMovingFeatures(org.geotoolkit.ogcapi.request.movingfeatures.InsertMovingFeatures) };
     * Insert moving features into `fleet1` from a raw movingFeature-mfjson document.
     */
    @Test
    public void testInsertMovingFeatures() {
        final MovingFeaturesApi api = new MovingFeaturesApi(newConfig());

        { //check incorrect request : missing collection id
            final var parameters = new InsertMovingFeatures()
                    .content("{}".getBytes(StandardCharsets.UTF_8));
            assertThrows(IllegalArgumentException.class, () -> api.insertMovingFeatures(parameters));
        }

        { //check valid request
            final byte[] content = "{\"type\":\"movingFeature\"}".getBytes(StandardCharsets.UTF_8);
            final var parameters = new InsertMovingFeatures()
                    .collectionId("fleet1")
                    .content(content);
            final var serviceRequest = api.insertMovingFeatures(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("POST", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections/fleet1/items", request.uri().toString());
            assertEquals(Optional.of("application/json"), request.headers().firstValue("Content-Type"));
            assertEquals(content.length, request.bodyPublisher().get().contentLength());
        }
    }

    /**
     * Testing {@linkplain MovingFeaturesApi#getMovingFeature(org.geotoolkit.ogcapi.request.movingfeatures.GetMovingFeature) };
     * Fetch the moving feature `veh 42` of `fleet1`.
     */
    @Test
    public void testGetMovingFeature() {
        final MovingFeaturesApi api = new MovingFeaturesApi(newConfig());

        { //check incorrect request : missing collection id
            final var parameters = new GetMovingFeature()
                    .mFeatureId("veh 42");
            assertThrows(IllegalArgumentException.class, () -> api.getMovingFeature(parameters));
        }

        { //check valid request
            final var parameters = new GetMovingFeature()
                    .collectionId("fleet1")
                    .mFeatureId("veh 42");
            final var serviceRequest = api.getMovingFeature(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections/fleet1/items/veh%2042", request.uri().toString());
            assertEquals(Optional.of("application/geo+json"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain MovingFeaturesApi#deleteMovingFeature(org.geotoolkit.ogcapi.request.movingfeatures.DeleteMovingFeature) };
     * Delete the moving feature `veh42` of `fleet1`.
     */
    @Test
    public void testDeleteMovingFeature() {
        final MovingFeaturesApi api = new MovingFeaturesApi(newConfig());

        { //check incorrect request : missing collection id
            final var parameters = new DeleteMovingFeature()
                    .mFeatureId("veh42");
            assertThrows(IllegalArgumentException.class, () -> api.deleteMovingFeature(parameters));
        }

        { //check valid request
            final var parameters = new DeleteMovingFeature()
                    .collectionId("fleet1")
                    .mFeatureId("veh42");
            final var serviceRequest = api.deleteMovingFeature(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("DELETE", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections/fleet1/items/veh42", request.uri().toString());
        }
    }

    /**
     * Testing {@linkplain MovingFeaturesApi#getTemporalGeometrySequence(org.geotoolkit.ogcapi.request.movingfeatures.GetTemporalGeometrySequence) };
     * Fetch the temporal geometry sequence of `veh42`, filtered by a bbox and a half-open datetime
     * interval, limited and interpolated at two leaf instants.
     */
    @Test
    public void testGetTemporalGeometrySequence() {
        final MovingFeaturesApi api = new MovingFeaturesApi(newConfig());

        { //check incorrect request : missing collection id
            final var parameters = new GetTemporalGeometrySequence()
                    .mFeatureId("veh42")
                    .datetime("2020-01-01T00:00:00Z/2020-01-02T00:00:00Z");
            assertThrows(IllegalArgumentException.class, () -> api.getTemporalGeometrySequence(parameters));
        }

        { //check valid request
            final var bbox = new GeneralEnvelope(new double[]{0.0, 0.0}, new double[]{10.0, 10.0});
            final var parameters = new GetTemporalGeometrySequence()
                    .collectionId("fleet1")
                    .mFeatureId("veh42")
                    .bbox(bbox)
                    .datetime("2020-01-01T00:00:00Z/2020-01-02T00:00:00Z")
                    .limit(5)
                    .leaf(List.of("2020-01-01T01:00:00Z", "2020-01-01T02:00:00Z"));
            final var serviceRequest = api.getTemporalGeometrySequence(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections/fleet1/items/veh42/tgsequence"
                    + "?bbox=0.0%2C0.0%2C10.0%2C10.0"
                    + "&datetime=2020-01-01T00%3A00%3A00Z%2F2020-01-02T00%3A00%3A00Z"
                    + "&limit=5"
                    + "&leaf=2020-01-01T01%3A00%3A00Z%2C2020-01-01T02%3A00%3A00Z", request.uri().toString());
            assertEquals(Optional.of("application/json"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain MovingFeaturesApi#insertTemporalGeometry(org.geotoolkit.ogcapi.request.movingfeatures.InsertTemporalGeometry) };
     * Append a temporal primitive geometry to the trajectory of `veh42`.
     */
    @Test
    public void testInsertTemporalGeometry() {
        final MovingFeaturesApi api = new MovingFeaturesApi(newConfig());

        { //check incorrect request : missing collection id
            final var parameters = new InsertTemporalGeometry()
                    .mFeatureId("veh42")
                    .content("{}".getBytes(StandardCharsets.UTF_8));
            assertThrows(IllegalArgumentException.class, () -> api.insertTemporalGeometry(parameters));
        }

        { //check valid request
            final byte[] content = "{\"type\":\"temporalPrimitiveGeometry\"}".getBytes(StandardCharsets.UTF_8);
            final var parameters = new InsertTemporalGeometry()
                    .collectionId("fleet1")
                    .mFeatureId("veh42")
                    .content(content);
            final var serviceRequest = api.insertTemporalGeometry(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("POST", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections/fleet1/items/veh42/tgsequence", request.uri().toString());
            assertEquals(Optional.of("application/json"), request.headers().firstValue("Content-Type"));
            assertEquals(content.length, request.bodyPublisher().get().contentLength());
        }
    }

    /**
     * Testing {@linkplain MovingFeaturesApi#deleteTemporalGeometry(org.geotoolkit.ogcapi.request.movingfeatures.DeleteTemporalGeometry) };
     * Delete the temporal primitive geometry `geom 1` from `veh42`.
     */
    @Test
    public void testDeleteTemporalGeometry() {
        final MovingFeaturesApi api = new MovingFeaturesApi(newConfig());

        { //check incorrect request : missing collection id
            final var parameters = new DeleteTemporalGeometry()
                    .mFeatureId("veh42")
                    .tGeometryId("geom 1");
            assertThrows(IllegalArgumentException.class, () -> api.deleteTemporalGeometry(parameters));
        }

        { //check valid request
            final var parameters = new DeleteTemporalGeometry()
                    .collectionId("fleet1")
                    .mFeatureId("veh42")
                    .tGeometryId("geom 1");
            final var serviceRequest = api.deleteTemporalGeometry(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("DELETE", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections/fleet1/items/veh42/tgsequence/geom%201", request.uri().toString());
        }
    }

    /**
     * Testing {@linkplain MovingFeaturesApi#getDistance(org.geotoolkit.ogcapi.request.movingfeatures.GetDistance) };
     * Fetch the time-to-distance curve of `geom1`, restricted to a datetime and interpolated at a
     * leaf instant, with subTemporalValue enabled.
     */
    @Test
    public void testGetDistance() {
        final MovingFeaturesApi api = new MovingFeaturesApi(newConfig());

        { //check incorrect request : missing collection id
            final var parameters = new GetDistance()
                    .mFeatureId("veh42")
                    .tGeometryId("geom1")
                    .datetime("2020-01-01T00:00:00Z");
            assertThrows(IllegalArgumentException.class, () -> api.getDistance(parameters));
        }

        { //check valid request
            final var parameters = new GetDistance()
                    .collectionId("fleet1")
                    .mFeatureId("veh42")
                    .tGeometryId("geom1")
                    .datetime("2020-01-01T00:00:00Z")
                    .leaf(List.of("2020-01-01T00:30:00Z"))
                    .subtemporalvalue(true);
            final var serviceRequest = api.getDistance(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections/fleet1/items/veh42/tgsequence/geom1/distance"
                    + "?datetime=2020-01-01T00%3A00%3A00Z"
                    + "&leaf=2020-01-01T00%3A30%3A00Z"
                    + "&subTemporalValue=true", request.uri().toString());
            assertEquals(Optional.of("application/json"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain MovingFeaturesApi#getVelocity(org.geotoolkit.ogcapi.request.movingfeatures.GetVelocity) };
     * Fetch the time-to-velocity curve of `geom1`, restricted to a datetime, with subTemporalValue
     * disabled.
     */
    @Test
    public void testGetVelocity() {
        final MovingFeaturesApi api = new MovingFeaturesApi(newConfig());

        { //check incorrect request : missing collection id
            final var parameters = new GetVelocity()
                    .mFeatureId("veh42")
                    .tGeometryId("geom1")
                    .datetime("2020-01-01T00:00:00Z");
            assertThrows(IllegalArgumentException.class, () -> api.getVelocity(parameters));
        }

        { //check valid request
            final var parameters = new GetVelocity()
                    .collectionId("fleet1")
                    .mFeatureId("veh42")
                    .tGeometryId("geom1")
                    .datetime("2020-01-01T00:00:00Z")
                    .subtemporalvalue(false);
            final var serviceRequest = api.getVelocity(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections/fleet1/items/veh42/tgsequence/geom1/velocity"
                    + "?datetime=2020-01-01T00%3A00%3A00Z"
                    + "&subTemporalValue=false", request.uri().toString());
            assertEquals(Optional.of("application/json"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain MovingFeaturesApi#getAcceleration(org.geotoolkit.ogcapi.request.movingfeatures.GetAcceleration) };
     * Fetch the time-to-acceleration curve of `geom1`, restricted to a datetime and interpolated at
     * two leaf instants.
     */
    @Test
    public void testGetAcceleration() {
        final MovingFeaturesApi api = new MovingFeaturesApi(newConfig());

        { //check incorrect request : missing collection id
            final var parameters = new GetAcceleration()
                    .mFeatureId("veh42")
                    .tGeometryId("geom1")
                    .datetime("2020-01-01T00:00:00Z");
            assertThrows(IllegalArgumentException.class, () -> api.getAcceleration(parameters));
        }

        { //check valid request
            final var parameters = new GetAcceleration()
                    .collectionId("fleet1")
                    .mFeatureId("veh42")
                    .tGeometryId("geom1")
                    .datetime("2020-01-01T00:00:00Z")
                    .leaf(List.of("2020-01-01T00:10:00Z", "2020-01-01T00:20:00Z"));
            final var serviceRequest = api.getAcceleration(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections/fleet1/items/veh42/tgsequence/geom1/acceleration"
                    + "?datetime=2020-01-01T00%3A00%3A00Z"
                    + "&leaf=2020-01-01T00%3A10%3A00Z%2C2020-01-01T00%3A20%3A00Z", request.uri().toString());
            assertEquals(Optional.of("application/json"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain MovingFeaturesApi#getTemporalProperties(org.geotoolkit.ogcapi.request.movingfeatures.GetTemporalProperties) };
     * Fetch the list of temporal properties of `veh42`, restricted to a datetime, limited, with
     * subTemporalValue enabled.
     */
    @Test
    public void testGetTemporalProperties() {
        final MovingFeaturesApi api = new MovingFeaturesApi(newConfig());

        { //check incorrect request : missing collection id
            final var parameters = new GetTemporalProperties()
                    .mFeatureId("veh42")
                    .datetime("2020-01-01T00:00:00Z");
            assertThrows(IllegalArgumentException.class, () -> api.getTemporalProperties(parameters));
        }

        { //check valid request
            final var parameters = new GetTemporalProperties()
                    .collectionId("fleet1")
                    .mFeatureId("veh42")
                    .datetime("2020-01-01T00:00:00Z")
                    .limit(10)
                    .subtemporalvalue(true);
            final var serviceRequest = api.getTemporalProperties(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections/fleet1/items/veh42/tproperties"
                    + "?datetime=2020-01-01T00%3A00%3A00Z"
                    + "&limit=10"
                    + "&subTemporalValue=true", request.uri().toString());
            assertEquals(Optional.of("application/json"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain MovingFeaturesApi#insertTemporalProperty(org.geotoolkit.ogcapi.request.movingfeatures.InsertTemporalProperty) };
     * Insert temporal properties into `veh42` from a raw JSON document.
     */
    @Test
    public void testInsertTemporalProperty() {
        final MovingFeaturesApi api = new MovingFeaturesApi(newConfig());

        { //check incorrect request : missing collection id
            final var parameters = new InsertTemporalProperty()
                    .mFeatureId("veh42")
                    .content("{}".getBytes(StandardCharsets.UTF_8));
            assertThrows(IllegalArgumentException.class, () -> api.insertTemporalProperty(parameters));
        }

        { //check valid request
            final byte[] content = "{\"speed\":{}}".getBytes(StandardCharsets.UTF_8);
            final var parameters = new InsertTemporalProperty()
                    .collectionId("fleet1")
                    .mFeatureId("veh42")
                    .content(content);
            final var serviceRequest = api.insertTemporalProperty(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("POST", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections/fleet1/items/veh42/tproperties", request.uri().toString());
            assertEquals(Optional.of("application/json"), request.headers().firstValue("Content-Type"));
            assertEquals(content.length, request.bodyPublisher().get().contentLength());
        }
    }

    /**
     * Testing {@linkplain MovingFeaturesApi#getTemporalProperty(org.geotoolkit.ogcapi.request.movingfeatures.GetTemporalProperty) };
     * Fetch the data of the named temporal property `speed kmh` of `veh42`, restricted to a
     * datetime and interpolated at a leaf instant, with subTemporalValue disabled.
     */
    @Test
    public void testGetTemporalProperty() {
        final MovingFeaturesApi api = new MovingFeaturesApi(newConfig());

        { //check incorrect request : missing collection id
            final var parameters = new GetTemporalProperty()
                    .mFeatureId("veh42")
                    .tPropertyName("speed kmh")
                    .datetime("2020-01-01T00:00:00Z");
            assertThrows(IllegalArgumentException.class, () -> api.getTemporalProperty(parameters));
        }

        { //check valid request
            final var parameters = new GetTemporalProperty()
                    .collectionId("fleet1")
                    .mFeatureId("veh42")
                    .tPropertyName("speed kmh")
                    .datetime("2020-01-01T00:00:00Z")
                    .leaf(List.of("2020-01-01T00:05:00Z"))
                    .subtemporalvalue(false);
            final var serviceRequest = api.getTemporalProperty(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections/fleet1/items/veh42/tproperties/speed%20kmh"
                    + "?datetime=2020-01-01T00%3A00%3A00Z"
                    + "&leaf=2020-01-01T00%3A05%3A00Z"
                    + "&subTemporalValue=false", request.uri().toString());
            assertEquals(Optional.of("application/json"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain MovingFeaturesApi#insertTemporalPrimitiveValue(org.geotoolkit.ogcapi.request.movingfeatures.InsertTemporalPrimitiveValue) };
     * Append a temporal primitive value to the named temporal property `speed` of `veh42`.
     */
    @Test
    public void testInsertTemporalPrimitiveValue() {
        final MovingFeaturesApi api = new MovingFeaturesApi(newConfig());

        { //check incorrect request : missing collection id
            final var parameters = new InsertTemporalPrimitiveValue()
                    .mFeatureId("veh42")
                    .tPropertyName("speed")
                    .content("{}".getBytes(StandardCharsets.UTF_8));
            assertThrows(IllegalArgumentException.class, () -> api.insertTemporalPrimitiveValue(parameters));
        }

        { //check valid request
            final byte[] content = "{\"value\":12.5}".getBytes(StandardCharsets.UTF_8);
            final var parameters = new InsertTemporalPrimitiveValue()
                    .collectionId("fleet1")
                    .mFeatureId("veh42")
                    .tPropertyName("speed")
                    .content(content);
            final var serviceRequest = api.insertTemporalPrimitiveValue(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("POST", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections/fleet1/items/veh42/tproperties/speed", request.uri().toString());
            assertEquals(Optional.of("application/json"), request.headers().firstValue("Content-Type"));
            assertEquals(content.length, request.bodyPublisher().get().contentLength());
        }
    }

    /**
     * Testing {@linkplain MovingFeaturesApi#deleteTemporalProperty(org.geotoolkit.ogcapi.request.movingfeatures.DeleteTemporalProperty) };
     * Delete the named temporal property `speed` and all its values from `veh42`.
     */
    @Test
    public void testDeleteTemporalProperty() {
        final MovingFeaturesApi api = new MovingFeaturesApi(newConfig());

        { //check incorrect request : missing collection id
            final var parameters = new DeleteTemporalProperty()
                    .mFeatureId("veh42")
                    .tPropertyName("speed");
            assertThrows(IllegalArgumentException.class, () -> api.deleteTemporalProperty(parameters));
        }

        { //check valid request
            final var parameters = new DeleteTemporalProperty()
                    .collectionId("fleet1")
                    .mFeatureId("veh42")
                    .tPropertyName("speed");
            final var serviceRequest = api.deleteTemporalProperty(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("DELETE", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections/fleet1/items/veh42/tproperties/speed", request.uri().toString());
        }
    }

    /**
     * Testing {@linkplain MovingFeaturesApi#deleteTemporalPrimitiveValue(org.geotoolkit.ogcapi.request.movingfeatures.DeleteTemporalPrimitiveValue) };
     * Delete the temporal primitive value `val 1` from the named temporal property `speed` of
     * `veh42`.
     */
    @Test
    public void testDeleteTemporalPrimitiveValue() {
        final MovingFeaturesApi api = new MovingFeaturesApi(newConfig());

        { //check incorrect request : missing collection id
            final var parameters = new DeleteTemporalPrimitiveValue()
                    .mFeatureId("veh42")
                    .tPropertyName("speed")
                    .tValueId("val 1");
            assertThrows(IllegalArgumentException.class, () -> api.deleteTemporalPrimitiveValue(parameters));
        }

        { //check valid request
            final var parameters = new DeleteTemporalPrimitiveValue()
                    .collectionId("fleet1")
                    .mFeatureId("veh42")
                    .tPropertyName("speed")
                    .tValueId("val 1");
            final var serviceRequest = api.deleteTemporalPrimitiveValue(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("DELETE", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections/fleet1/items/veh42/tproperties/speed/val%201", request.uri().toString());
        }
    }
}
