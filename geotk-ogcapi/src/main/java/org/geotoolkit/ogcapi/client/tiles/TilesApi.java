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
import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.client.openapi.AbstractOpenApi;
import org.geotoolkit.client.openapi.OpenApiConfiguration;
import static org.geotoolkit.client.service.AbstractService.toPairs;
import static org.geotoolkit.client.service.AbstractService.urlEncode;
import org.geotoolkit.client.service.Pair;
import org.geotoolkit.client.service.ServiceException;
import org.geotoolkit.client.service.ServiceResponse;
import org.geotoolkit.ogcapi.model.tiles.GetTileMatrixSetsListResponse;
import org.geotoolkit.ogcapi.model.tiles.GetTileSetsListResponse;
import org.geotoolkit.ogcapi.model.tiles.TileMatrixSet;
import org.geotoolkit.ogcapi.model.tiles.TileSet;
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
     * @return ServiceResponse&lt;byte[]&gt;
     * @throws ServiceException if fails to make API call
     */
    public ServiceResponse<byte[]> getTile(GetTile parameters) throws ServiceException {
        // verify the required parameter 'tileMatrix' is set
        if (parameters.getTileMatrixId() == null) {
            throw new ServiceException(400, "Missing the required parameter 'tileMatrix' when calling collectionCoverageGetTile");
        }
        // verify the required parameter 'tileRow' is set
        if (parameters.getTileRow() == null) {
            throw new ServiceException(400, "Missing the required parameter 'tileRow' when calling collectionCoverageGetTile");
        }
        // verify the required parameter 'tileCol' is set
        if (parameters.getTileCol() == null) {
            throw new ServiceException(400, "Missing the required parameter 'tileCol' when calling collectionCoverageGetTile");
        }
        // verify the required parameter 'tileMatrixSetId' is set
        if (parameters.getTileMatrixSetId() == null) {
            throw new ServiceException(400, "Missing the required parameter 'tileMatrixSetId' when calling collectionCoverageGetTile");
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
        return toSimpleResponse(send(request), byte[].class);
    }

    /**
     * Retrieve the tileset metadata for the specified tiling scheme (tile matrix set)
     *
     * @param parameters request parameters
     * @return ServiceResponse&lt;TileSet&gt;
     * @throws ServiceException if fails to make API call
     */
    public ServiceResponse<TileSet> getTileSet(GetTileSet parameters) throws ServiceException {
        // verify the required parameter 'tileMatrixSetId' is set
        if (parameters.getTileMatrixSetId() == null) {
            throw new ServiceException(400, "Missing the required parameter 'tileMatrixSetId' when calling collectionCoverageGetTileSet");
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
        return toSimpleResponse(send(request), TileSet.class);
    }

    /**
     * Retrieve the list of available tilesets.
     *
     * @param parameters request parameters
     * @return ServiceResponse&lt;GetTileSetsListResponse&gt;
     * @throws ServiceException if fails to make API call
     */
    public ServiceResponse<GetTileSetsListResponse> getTileSetsList(GetTileSetsList parameters) throws ServiceException {

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
        return toSimpleResponse(send(request), GetTileSetsListResponse.class);
    }

    /**
     * Retrieve the definition of the specified tiling scheme (tile matrix set)
     *
     * @param parameters request parameters
     * @return ServiceResponse&lt;TileMatrixSet&gt;
     * @throws ServiceException if fails to make API call
     */
    public ServiceResponse<TileMatrixSet> getTileMatrixSet(GetTileMatrixSet parameters) throws ServiceException {

        // verify the required parameter 'tileMatrixSetId' is set
        if (parameters.getTileMatrixSetId() == null) {
            throw new ServiceException(400, "Missing the required parameter 'tileMatrixSetId' when calling getTileMatrixSet");
        }

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/tileMatrixSets/" + urlEncode(parameters.getTileMatrixSetId()), toPairs("f", parameters.getFormat())));
        request.header("Accept", "application/json");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return toSimpleResponse(send(request), TileMatrixSet.class);
    }

    /**
     * Retrieve the list of available tiling schemes (tile matrix sets)
     *
     * @param f The format of the response. If no value is provided, the accept header is used to determine the format.
     * Accepted values are &#39;json&#39; or &#39;html&#39;. (optional)
     * @return ServiceResponse&lt;GetTileMatrixSetsList200Response&gt;
     * @throws ServiceException if fails to make API call
     */
    public ServiceResponse<GetTileMatrixSetsListResponse> getTileMatrixSetsList(@jakarta.annotation.Nullable String f) throws ServiceException {

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/tileMatrixSets", toPairs("f", f)));
        request.header("Accept", "application/json");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return toSimpleResponse(send(request), GetTileMatrixSetsListResponse.class);
    }

}
