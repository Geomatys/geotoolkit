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

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import org.geotoolkit.client.openapi.AbstractOpenApi;
import org.geotoolkit.client.openapi.OpenApiConfiguration;
import static org.geotoolkit.client.service.AbstractService.toPairs;
import static org.geotoolkit.client.service.AbstractService.urlEncode;
import org.geotoolkit.client.service.Pair;
import org.geotoolkit.client.service.ServiceRequest;
import org.geotoolkit.ogcapi.dto.tiles.GetTileMatrixSetsListResponse;
import org.geotoolkit.ogcapi.dto.tiles.GetTileSetsListResponse;
import org.geotoolkit.ogcapi.dto.tiles.TileMatrixSet;
import org.geotoolkit.ogcapi.dto.tiles.TileSet;
import org.geotoolkit.ogcapi.request.tiles.GetTile;
import org.geotoolkit.ogcapi.request.tiles.GetTileMatrixSet;
import org.geotoolkit.ogcapi.request.tiles.GetTileSet;
import org.geotoolkit.ogcapi.request.tiles.GetTileSetsList;

/**
 * OGCAPI Tiles.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class TilesApi extends AbstractOpenApi {

    public TilesApi(OpenApiConfiguration apiClient) {
        super(apiClient);
    }

    /**
     * Retrieve a tile
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;byte[]&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<byte[]> getTile(GetTile parameters) throws IllegalArgumentException {
        // verify the required parameter 'tileMatrix' is set
        if (parameters.getTileMatrixId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'tileMatrix' when calling collectionCoverageGetTile");
        }
        // verify the required parameter 'tileRow' is set
        if (parameters.getTileRow() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'tileRow' when calling collectionCoverageGetTile");
        }
        // verify the required parameter 'tileCol' is set
        if (parameters.getTileCol() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'tileCol' when calling collectionCoverageGetTile");
        }
        // verify the required parameter 'tileMatrixSetId' is set
        if (parameters.getTileMatrixSetId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'tileMatrixSetId' when calling collectionCoverageGetTile");
        }

        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs("csv", "collections", parameters.getCollections()));
        queryParams.addAll(toPairs("csv", "subset", parameters.getSubset()));
        queryParams.addAll(toPairs("datetime", parameters.getDatetime()));
        queryParams.addAll(toPairs("crs", parameters.getCrs()));
        queryParams.addAll(toPairs("subset-crs", parameters.getSubsetCrs()));
        queryParams.addAll(toPairs("bgcolor", parameters.getBgColor()));
        queryParams.addAll(toPairs("transparent", parameters.getTransparency()));
        queryParams.addAll(toPairs("f", parameters.getFormat()));

        final StringBuilder pathBuilder = new StringBuilder();
        if (parameters.getCollectionId() != null) pathBuilder.append("/collections/").append(urlEncode(parameters.getCollectionId()));
        if (parameters.getStyleId() != null) pathBuilder.append("/styles/").append(urlEncode(parameters.getStyleId()));
        pathBuilder.append(parameters.getType().subpath);
        pathBuilder.append("/tiles/").append(urlEncode(parameters.getTileMatrixSetId()));
        pathBuilder.append("/").append(urlEncode(parameters.getTileMatrixId()));
        pathBuilder.append("/").append(urlEncode(parameters.getTileRow().toString()));
        pathBuilder.append("/").append(urlEncode(parameters.getTileCol().toString()));

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(pathBuilder.toString(), queryParams));
        request.header("Accept", "*/*");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.ofByteArray());
    }

    /**
     * Retrieve the tileset metadata for the specified tiling scheme (tile matrix set)
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;TileSet&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<TileSet>> getTileSet(GetTileSet parameters) throws IllegalArgumentException {
        // verify the required parameter 'tileMatrixSetId' is set
        if (parameters.getTileMatrixSetId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'tileMatrixSetId' when calling collectionCoverageGetTileSet");
        }

        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs("csv", "collections", parameters.getCollections()));
        queryParams.addAll(toPairs("f", parameters.getFormat()));

        final StringBuilder pathBuilder = new StringBuilder();
        if (parameters.getCollectionId() != null) pathBuilder.append("/collections/").append(urlEncode(parameters.getCollectionId()));
        if (parameters.getStyleId() != null) pathBuilder.append("/styles/").append(urlEncode(parameters.getStyleId()));
        pathBuilder.append(parameters.getType().subpath);
        pathBuilder.append("/tiles/").append(urlEncode(parameters.getTileMatrixSetId()));

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(pathBuilder.toString(), queryParams));
        request.header("Accept", "application/json");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, TileSet.class);
    }

    /**
     * Retrieve the list of available tilesets.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;GetTileSetsListResponse&gt;
     */
    public ServiceRequest<Supplier<GetTileSetsListResponse>> getTileSetsList(GetTileSetsList parameters) {

        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs("f", parameters.getFormat()));

        final StringBuilder pathBuilder = new StringBuilder();
        if (parameters.getCollectionId() != null) pathBuilder.append("/collections/").append(urlEncode(parameters.getCollectionId()));
        if (parameters.getStyleId() != null) pathBuilder.append("/styles/").append(urlEncode(parameters.getStyleId()));
        pathBuilder.append(parameters.getType().subpath);
        pathBuilder.append("/tiles");

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(pathBuilder.toString(), queryParams));
        request.header("Accept", "application/json");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, GetTileSetsListResponse.class);
    }

    /**
     * Retrieve the definition of the specified tiling scheme (tile matrix set)
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;TileMatrixSet&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<TileMatrixSet>> getTileMatrixSet(GetTileMatrixSet parameters) throws IllegalArgumentException {

        // verify the required parameter 'tileMatrixSetId' is set
        if (parameters.getTileMatrixSetId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'tileMatrixSetId' when calling getTileMatrixSet");
        }

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/tileMatrixSets/" + urlEncode(parameters.getTileMatrixSetId()), toPairs("f", parameters.getFormat())));
        request.header("Accept", "application/json");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, TileMatrixSet.class);
    }

    /**
     * Retrieve the list of available tiling schemes (tile matrix sets)
     *
     * @param f The format of the response. If no value is provided, the accept header is used to determine the format.
     * Accepted values are &#39;json&#39; or &#39;html&#39;. (optional)
     * @return ServiceRequest&lt;GetTileMatrixSetsList200Response&gt;
     */
    public ServiceRequest<Supplier<GetTileMatrixSetsListResponse>> getTileMatrixSetsList(String f) {

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/tileMatrixSets", toPairs("f", f)));
        request.header("Accept", "application/json");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, GetTileMatrixSetsListResponse.class);
    }

}
