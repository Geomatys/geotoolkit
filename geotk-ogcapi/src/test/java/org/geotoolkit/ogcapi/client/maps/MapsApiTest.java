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
package org.geotoolkit.ogcapi.client.maps;

import java.util.List;
import java.util.Optional;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.ogcapi.client.AbstractApiTest;
import org.geotoolkit.ogcapi.request.maps.GetMap;
import org.geotoolkit.ogcapi.request.maps.GetProjections;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class MapsApiTest extends AbstractApiTest {

    /**
     * Testing {@linkplain MapsApi#getMap(org.geotoolkit.ogcapi.request.maps.GetMap) };
     * Request a styled collection map `my collection` / `night mode`, with a bounding box, a
     * datetime, a list of collections, a subset, various coloring and sizing options, a center
     * point, a custom crs projection and a png format.
     */
    @Test
    public void testGetMap() {
        final var api = new MapsApi(newConfig());

        { //check valid request
            final var bbox = new GeneralEnvelope(new double[]{10.0, 20.0}, new double[]{30.0, 40.5});
            final var center = new GeneralDirectPosition(12.5, 45.5);
            final var parameters = new GetMap()
                    .collectionId("my collection")
                    .styleId("night mode")
                    .bbox(bbox)
                    .datetime("2018-02-12T00:00:00Z")
                    .collections(List.of("collection A", "collection_b"))
                    .subset(List.of("Lat(10:20)", "Lon(30:40)"))
                    .crs("EPSG:4326")
                    .subsetCrs("EPSG:3857")
                    .bgColor("0x9C9C9C")
                    .transparency(Boolean.TRUE)
                    .voidColor("gray")
                    .voidTransparent(Boolean.FALSE)
                    .width(800)
                    .height(600)
                    .center(center)
                    .scaleDenominator(5000.0)
                    .mmPerPixel(0.28)
                    .orientation(90.0)
                    .crsProjMethod("Lambert Conic")
                    .crsProjCenter("Lat(46),Lon(2)")
                    .crsProjParams("scale_factor=1.0")
                    .crsDatum("WGS84")
                    .format("png");
            final var serviceRequest = api.getMap(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections/my%20collection/styles/night%20mode/map"
                    + "?bbox=10.0%2C20.0%2C30.0%2C40.5"
                    + "&datetime=2018-02-12T00%3A00%3A00Z"
                    + "&collections=collection%20A%2Ccollection_b"
                    + "&subset=Lat%2810%3A20%29%2CLon%2830%3A40%29"
                    + "&crs=EPSG%3A4326"
                    + "&subset-crs=EPSG%3A3857"
                    + "&bgcolor=0x9C9C9C"
                    + "&transparent=true"
                    + "&void-color=gray"
                    + "&void-transparent=false"
                    + "&width=800"
                    + "&height=600"
                    + "&center=12.5%2C45.5"
                    + "&scale-denominator=5000.0"
                    + "&mm-per-pixel=0.28"
                    + "&orientation=90.0"
                    + "&crs-proj-method=Lambert%20Conic"
                    + "&crs-proj-center=Lat%2846%29%2CLon%282%29"
                    + "&crs-proj-params=scale_factor%3D1.0"
                    + "&crs-datum=WGS84"
                    + "&f=png", request.uri().toString());
            assertEquals(Optional.of("image/png, image/jpeg, image/tiff; application=geotiff"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain MapsApi#getProjections(org.geotoolkit.ogcapi.request.maps.GetProjections) };
     * Request the list of custom CRS projection methods and datums, in json format.
     */
    @Test
    public void testGetProjections() {
        final var api = new MapsApi(newConfig());

        { //check valid request
            final var parameters = new GetProjections()
                    .format("json");
            final var serviceRequest = api.getProjections(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/projectionsAndDatums"
                    + "?f=json", request.uri().toString());
            assertEquals(Optional.of("application/json, text/html"), request.headers().firstValue("Accept"));
        }
    }
}
