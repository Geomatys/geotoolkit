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
package org.geotoolkit.ogcapi.client.geovolumes;

import java.util.Optional;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.ogcapi.client.AbstractApiTest;
import org.geotoolkit.ogcapi.request.common.GetCollectionList;
import org.geotoolkit.ogcapi.request.geovolumes.GetCollection;
import org.geotoolkit.ogcapi.request.geovolumes.GetModel;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class GeoVolumesApiTest extends AbstractApiTest {

    /**
     * Testing {@linkplain GeoVolumesApi#getCollections(org.geotoolkit.ogcapi.request.common.GetCollectionList) };
     * Request the list of 3D-Containers filtered by a 3D bounding box, in html format.
     */
    @Test
    public void testGetCollections() {
        final GeoVolumesApi api = new GeoVolumesApi(newConfig());

        { //check valid request
            final var bbox = new GeneralEnvelope(new double[]{10.0, 20.0, 0.0}, new double[]{30.0, 40.5, 100.0});
            final var parameters = new GetCollectionList()
                    .bbox(bbox)
                    .format("html");
            final var serviceRequest = api.getCollections(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections"
                    + "?bbox=10.0%2C20.0%2C0.0%2C30.0%2C40.5%2C100.0"
                    + "&f=html", request.uri().toString());
            assertEquals(Optional.of("application/json, text/html"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain GeoVolumesApi#getCollection(org.geotoolkit.ogcapi.request.geovolumes.GetCollection) };
     * Request a specific 3D-Container `Volume A` filtered by a 3D bounding box, in json format.
     */
    @Test
    public void testGetCollection() {
        final GeoVolumesApi api = new GeoVolumesApi(newConfig());

        { //check incorrect request : missing collection id
            final var parameters = new GetCollection();
            assertThrows(IllegalArgumentException.class, () -> api.getCollection(parameters));
        }

        { //check valid request
            final var bbox = new GeneralEnvelope(new double[]{2.0, 3.0, 4.0}, new double[]{6.0, 7.0, 8.0});
            final var parameters = new GetCollection()
                    .bbox(bbox);
            parameters.setCollectionId("Volume A");
            parameters.setFormat("json");
            final var serviceRequest = api.getCollection(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections/Volume%20A"
                    + "?bbox=2.0%2C3.0%2C4.0%2C6.0%2C7.0%2C8.0"
                    + "&f=json", request.uri().toString());
            assertEquals(Optional.of("application/json, text/html"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain GeoVolumesApi#getModel(org.geotoolkit.ogcapi.request.geovolumes.GetModel) };
     * Request a shared 3D model `model #7`.
     */
    @Test
    public void testGetModel() {
        final GeoVolumesApi api = new GeoVolumesApi(newConfig());

        { //check incorrect request : missing model id
            final var parameters = new GetModel();
            assertThrows(IllegalArgumentException.class, () -> api.getModel(parameters));
        }

        { //check valid request
            final var parameters = new GetModel()
                    .modelId("model #7");
            final var serviceRequest = api.getModel(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/models/model%20%237", request.uri().toString());
            assertEquals(Optional.of("*/*"), request.headers().firstValue("Accept"));
        }
    }
}
