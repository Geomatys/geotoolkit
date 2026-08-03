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
package org.geotoolkit.ogcapi.client.tiles;

import java.util.List;
import java.util.Optional;
import org.geotoolkit.ogcapi.client.AbstractApiTest;
import org.geotoolkit.ogcapi.request.tiles.GetTile;
import org.geotoolkit.ogcapi.request.tiles.GetTileMatrixSet;
import org.geotoolkit.ogcapi.request.tiles.GetTileSet;
import org.geotoolkit.ogcapi.request.tiles.GetTileSetsList;
import org.geotoolkit.ogcapi.request.tiles.TilingType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class TilesApiTest extends AbstractApiTest {

    /**
     * Testing {@linkplain TilesApi#getTile(org.geotoolkit.ogcapi.request.tiles.GetTile) };
     * Request a vector tile of collection `my collection` on tile matrix set WebMercatorQuad, tile 5/10/15.
     */
    @Test
    public void testGetTile() {
        final var api = new TilesApi(newConfig());

        { //check incorrect request : missing tileMatrix
            final var parameters = new GetTile();
            assertThrows(IllegalArgumentException.class, () -> api.getTile(parameters));
        }

        { //check valid request
            final var parameters = new GetTile()
                    .collectionId("my collection")
                    .tileMatrixSetId("WebMercatorQuad")
                    .tileMatrixId("5")
                    .tileRow(10)
                    .tileCol(15)
                    .collections(List.of("buildings", "roads"))
                    .subset(List.of("Lat(10:20)"))
                    .datetime("2018-02-12T00:00:00Z")
                    .crs("EPSG:3857")
                    .subsetCrs("EPSG:4326")
                    .bgColor("steelblue")
                    .transparency(true)
                    .format("mvt");
            final var serviceRequest = api.getTile(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections/my%20collection/tiles/WebMercatorQuad/5/10/15"
                    + "?collections=buildings%2Croads"
                    + "&subset=Lat%2810%3A20%29"
                    + "&datetime=2018-02-12T00%3A00%3A00Z"
                    + "&crs=EPSG%3A3857"
                    + "&subset-crs=EPSG%3A4326"
                    + "&bgcolor=steelblue"
                    + "&transparent=true"
                    + "&f=mvt", request.uri().toString());
            assertEquals(Optional.of("*/*"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain TilesApi#getTileSet(org.geotoolkit.ogcapi.request.tiles.GetTileSet) };
     * Request the tileset metadata of collection `my collection` for tile matrix set WebMercatorQuad.
     */
    @Test
    public void testGetTileSet() {
        final var api = new TilesApi(newConfig());

        { //check incorrect request : missing tileMatrixSetId
            final var parameters = new GetTileSet();
            assertThrows(IllegalArgumentException.class, () -> api.getTileSet(parameters));
        }

        { //check valid request
            final var parameters = new GetTileSet()
                    .collectionId("my collection")
                    .tileMatrixSetId("WebMercatorQuad")
                    .collections(List.of("buildings", "roads"))
                    .format("json");
            final var serviceRequest = api.getTileSet(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections/my%20collection/tiles/WebMercatorQuad"
                    + "?collections=buildings%2Croads"
                    + "&f=json", request.uri().toString());
            assertEquals(Optional.of("application/json"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain TilesApi#getTileSetsList(org.geotoolkit.ogcapi.request.tiles.GetTileSetsList) };
     * Request the list of available tilesets for a map tiling of collection `rivers data` with style `basic style`.
     */
    @Test
    public void testGetTileSetsList() {
        final var api = new TilesApi(newConfig());

        { //check valid request
            final var parameters = new GetTileSetsList()
                    .type(TilingType.MAP)
                    .collectionId("rivers data")
                    .styleId("basic style")
                    .format("html");
            final var serviceRequest = api.getTileSetsList(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/collections/rivers%20data/styles/basic%20style/map/tiles"
                    + "?f=html", request.uri().toString());
            assertEquals(Optional.of("application/json"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain TilesApi#getTileMatrixSet(org.geotoolkit.ogcapi.request.tiles.GetTileMatrixSet) };
     * Request the definition of tile matrix set `World CRS84Quad`.
     */
    @Test
    public void testGetTileMatrixSet() {
        final var api = new TilesApi(newConfig());

        { //check incorrect request : missing tileMatrixSetId
            final var parameters = new GetTileMatrixSet();
            assertThrows(IllegalArgumentException.class, () -> api.getTileMatrixSet(parameters));
        }

        { //check valid request
            final var parameters = new GetTileMatrixSet()
                    .tileMatrixSetId("World CRS84Quad")
                    .format("json");
            final var serviceRequest = api.getTileMatrixSet(parameters);
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/tileMatrixSets/World%20CRS84Quad"
                    + "?f=json", request.uri().toString());
            assertEquals(Optional.of("application/json"), request.headers().firstValue("Accept"));
        }
    }

    /**
     * Testing {@linkplain TilesApi#getTileMatrixSetsList(java.lang.String) };
     * Request the list of available tiling schemes in html format.
     */
    @Test
    public void testGetTileMatrixSetsList() {
        final var api = new TilesApi(newConfig());

        { //check valid request
            final var serviceRequest = api.getTileMatrixSetsList("html");
            final var request = serviceRequest.builder().build();

            assertEquals("GET", request.method());
            assertEquals("http://localhost:8080/ogcapi/tileMatrixSets"
                    + "?f=html", request.uri().toString());
            assertEquals(Optional.of("application/json"), request.headers().firstValue("Accept"));
        }
    }
}
