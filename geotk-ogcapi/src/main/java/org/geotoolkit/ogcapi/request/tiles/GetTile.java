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
package org.geotoolkit.ogcapi.request.tiles;

import org.geotoolkit.ogcapi.request.RequestParameters;
import java.util.List;

/**
 * Parameters for {@code GET /collections/{collectionId}/tiles/{tileMatrixSetId}/{tileMatrix}/{tileRow}/{tileCol}}:
 * the retrieval of a single tile.
 *
 * <blockquote><q>
 *     An HTTP GET request allows for the retrieval of a single tile that represents information
 *     coming from geospatial data resources.
 *     </q><i><small><a href="https://docs.ogc.org/is/20-057/20-057.html#_11c5aa51-15bf-6851-5c31-19138f5d4e79"> OGC API - Tiles - Part 1: Core - 7.1.1. Operation</a></small></i>
 * </blockquote>
 *
 * @author Johann Sorel (Geomatys)
 */
public final class GetTile extends RequestParameters {

    private TilingType type = TilingType.VECTOR;
    private String collectionId;
    private String tileMatrixSetId;
    private String tileMatrixId;
    private String styleId;
    private String format;
    private List<String> collections;
    private List<String> subset;
    private Integer tileRow;
    private Integer tileCol;
    private String datetime;
    private String crs;
    private String subsetCrs;
    private String bgcolor;
    private Boolean transparent;

    /**
     * @return the tiling type
     * @see #type(org.geotoolkit.ogcapi.request.tiles.TilingType)
     */
    public TilingType getType() {
        return type;
    }

    /**
     * @param type the type to set
     * @see #type(org.geotoolkit.ogcapi.request.tiles.TilingType)
     */
    public void setType(TilingType type) {
        this.type = type;
    }

    /**
     * <blockquote><q>
     *     Type of data represented in the tileset
     *     </q><i><small><a href="https://docs.ogc.org/is/20-057/20-057.html#_tilesets_list"> OGC API - Tiles - Part 1: Core - 9.2. Tilesets list (tileset schema, property dataType)</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional, default {@code VECTOR}.</li>
     *     <li>Spec-defined values of the underlying {@code dataType} property are {@code map},
     *     {@code vector} and {@code coverage}; not itself a request parameter, it only selects which
     *     tiling subpath ({@link TilingType#subpath}) this request targets.</li>
     * </ul>
     *
     * @param type the type to set
     */
    public GetTile type(TilingType type) {
        this.type = type;
        return this;
    }

    /**
     * @return the collectionId
     * @see #collectionId(java.lang.String)
     */
    public String getCollectionId() {
        return collectionId;
    }

    /**
     * @param collectionId the collectionId to set
     * @see #collectionId(java.lang.String)
     */
    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    /**
     * <blockquote><q>
     *     A tile resource is a fragment of a larger single geospatial data resource that is
     *     spatially constrained at the boundaries of the selected tile in a tile matrix set.
     *     </q><i><small><a href="https://docs.ogc.org/is/20-057/20-057.html#_945edcb6-a08e-8627-8c3c-4cdd8fa7ea36"> OGC API - Tiles - Part 1: Core - 11.5. Tiles</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required if requesting a tile of a specific collection.</li>
     *     <li>Local identifier of the geospatial data resource (collection) being tiled.</li>
     * </ul>
     *
     * @param collectionId the collectionId to set
     */
    public GetTile collectionId(String collectionId) {
        setCollectionId(collectionId);
        return this;
    }

    /**
     * @return the TileMatrixSet id
     * @see #tileMatrixSetId(java.lang.String)
     */
    public String getTileMatrixSetId() {
        return tileMatrixSetId;
    }

    /**
     * @param tileMatrixSetId the TileMatrixSet id to set
     * @see #tileMatrixSetId(java.lang.String)
     */
    public void setTileMatrixSetId(String tileMatrixSetId) {
        this.tileMatrixSetId = tileMatrixSetId;
    }

    /**
     * <blockquote><q>
     *     The {@code tileMatrixSetId} variable will be interpreted as one of TileMatrixSet
     *     identifiers supported by the resource. In other words, tileMatrixSetId represents all
     *     the TileMatrixSets supported by the resource.
     *     </q><i><small><a href="https://docs.ogc.org/is/20-057/20-057.html#_7f66635d-a86f-e3ed-427f-494127984475"> OGC API - Tiles - Part 1: Core - 7.1.5. Parameter tileMatrixSetId (optional)</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     * </ul>
     *
     * @param tileMatrixSetId the TileMatrixSet id to set
     */
    public GetTile tileMatrixSetId(String tileMatrixSetId) {
        setTileMatrixSetId(tileMatrixSetId);
        return this;
    }

    /**
     * @return the tile matrix
     * @see #tileMatrixId(java.lang.String)
     */
    public String getTileMatrixId() {
        return tileMatrixId;
    }

    /**
     * @param tileMatrixId the TileMatrix id to set
     * @see #tileMatrixId(java.lang.String)
     */
    public void setTileMatrixId(String tileMatrixId) {
        this.tileMatrixId = tileMatrixId;
    }

    /**
     * <blockquote><q>
     *     Identifier selecting one of the scales defined in the TileMatrixSet and representing the
     *     scaleDenominator the tile.
     *     </q><i><small><a href="https://docs.ogc.org/is/20-057/20-057.html#_parameter_tilematrix"> OGC API - Tiles - Part 1: Core - 7.1.2. Parameter tileMatrix</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     * </ul>
     * <pre>
     * name: tileMatrix
     * in: path
     * description: Identifier selecting one of the scales defined in the TileMatrixSet and representing the scaleDenominator the tile.
     * required: true
     * schema:
     *   type: string
     * example: '11'
     * </pre>
     *
     * @param tileMatrixId the TileMatrix id to set
     */
    public GetTile tileMatrixId(String tileMatrixId) {
        setTileMatrixId(tileMatrixId);
        return this;
    }

    /**
     * @return the style id
     * @see #styleId(java.lang.String)
     */
    public String getStyleId() {
        return styleId;
    }

    /**
     * @param styleId the style id to set
     * @see #styleId(java.lang.String)
     */
    public void setStyleId(String styleId) {
        this.styleId = styleId;
    }

    /**
     * An identifier representing a specific style.
     * <br>
     * <ul>
     *     <li>Optional; required if requesting a tile of a styled map or vector resource.</li>
     *     <li>Not formally defined as a request parameter by OGC API - Tiles - Part 1: Core; the
     *     Standard only shows a "Styled Map tileset list" example path of the form
     *     {@code .../styles/{styleId}/map/tiles} (Table 4/Table 5), with no verbatim parameter
     *     description available.</li>
     * </ul>
     *
     * @param styleId the style id to set
     */
    public GetTile styleId(String styleId) {
        setStyleId(styleId);
        return this;
    }

    /**
     * @return the tile row
     * @see #tileRow(java.lang.Integer)
     */
    public Integer getTileRow() {
        return tileRow;
    }

    /**
     * @param tileRow the Tile row to set
     * @see #tileRow(java.lang.Integer)
     */
    public void setTileRow(Integer tileRow) {
        this.tileRow = tileRow;
    }

    /**
     * <blockquote><q>
     *     Row index of the tile on the selected TileMatrix. It cannot exceed the MatrixWidth-1 for
     *     the selected TileMatrix
     *     </q><i><small><a href="https://docs.ogc.org/is/20-057/20-057.html#_parameter_tilerow"> OGC API - Tiles - Part 1: Core - 7.1.3. Parameter tileRow</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required, integer, minimum {@code 0}.</li>
     * </ul>
     * <pre>
     * name: tileRow
     * in: path
     * description: Row index of the tile on the selected TileMatrix. It cannot exceed the MatrixWidth-1 for the selected TileMatrix
     * required: true
     * schema:
     *   type: integer
     *   minimum: 0
     * example: '827'
     * </pre>
     *
     * @param tileRow the Tile row to set
     */
    public GetTile tileRow(Integer tileRow) {
        setTileRow(tileRow);
        return this;
    }

    /**
     * @return the tile col
     * @see #tileCol(java.lang.Integer)
     */
    public Integer getTileCol() {
        return tileCol;
    }

    /**
     * @param tileCol the Tile col to set
     * @see #tileCol(java.lang.Integer)
     */
    public void setTileCol(Integer tileCol) {
        this.tileCol = tileCol;
    }

    /**
     * <blockquote><q>
     *     Column index of the tile on the selected TileMatrix. It cannot exceed the
     *     MatrixHeight-1 for the selected TileMatrix.
     *     </q><i><small><a href="https://docs.ogc.org/is/20-057/20-057.html#_parameter_tilecol"> OGC API - Tiles - Part 1: Core - 7.1.4. Parameter tileCol</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required, integer, minimum {@code 0}.</li>
     * </ul>
     * <pre>
     * name: tileCol
     * in: path
     * description: Column index of the tile on the selected TileMatrix. It cannot exceed the MatrixHeight-1 for the selected TileMatrix.
     * required: true
     * schema:
     *   type: integer
     *   minimum: 0
     * example: 1231
     * </pre>
     *
     * @param tileCol the Tile col to set
     */
    public GetTile tileCol(Integer tileCol) {
        setTileCol(tileCol);
        return this;
    }

    /**
     * @return the format
     * @see #format(java.lang.String)
     */
    public String getFormat() {
        return format;
    }

    /**
     * @param format the format to set
     * @see #format(java.lang.String)
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * <blockquote><q>
     *     A successful response to a tile GET operation will be consistent with the media type of
     *     the resource requested. This Standard does not impose any media type or file format.
     *     </q><i><small><a href="https://docs.ogc.org/is/20-057/20-057.html#_59e763b4-fbce-b306-dde2-3ed4e8fa64e4"> OGC API - Tiles - Part 1: Core - 7.1.6. Response</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional; the desired encoding is normally selected through HTTP content negotiation.</li>
     *     <li>For features: GeoJSON ({@code application/geo+json}) or Mapbox Vector Tiles
     *     ({@code application/vnd.mapbox-vector-tile}).</li>
     *     <li>For coverages: GeoTIFF ({@code image/tiff}) or netCDF ({@code application/netcdf}).</li>
     *     <li>For maps: JPEG ({@code image/jpeg}) or PNG ({@code image/png}).</li>
     * </ul>
     *
     * @param format the format to set
     */
    public GetTile format(String format) {
        setFormat(format);
        return this;
    }

    /**
     * @return the collections
     * @see #collections(java.util.List)
     */
    public List<String> getCollections() {
        return collections;
    }

    /**
     * @param collections the collections to set
     * @see #collections(java.util.List)
     */
    public void setCollections(List<String> collections) {
        this.collections = collections;
    }

    /**
     * <blockquote><q>
     *     Only the collections of geospatial data enumerated in the values of the collections
     *     parameter SHALL be used to generate the responses for the resources (tiles and tilesets)
     *     to which they apply.
     *     </q><i><small><a href="https://docs.ogc.org/is/20-057/20-057.html#_feeeca6c-3a49-084f-d02b-377494bc4189"> OGC API - Tiles - Part 1: Core - 12.2. Operation</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional; only applies to a tile consisting of multiple geospatial data
     *     sub-resources (e.g. a dataset-wide tile).</li>
     *     <li>Comma-separated list of collection identifiers, or full URLs to local geospatial
     *     resources.</li>
     *     <li>Rendered starting with the first (bottom) and ending with the last (topmost), unless
     *     overridden by styling rules.</li>
     * </ul>
     *
     * @param collections the collections to set
     */
    public GetTile collections(List<String> collections) {
        setCollections(collections);
        return this;
    }

    /**
     * <blockquote><q>
     *     The datetime parameter selects resources based on their temporal extent. The definition
     *     of temporal extent is specific to the resource type being filtered.
     *     </q><i><small><a href="https://docs.ogc.org/is/20-057/20-057.html#_1b2f37bb-b5d1-476a-f00f-4f993796fb94"> OGC API - Tiles - Part 1: Core - 13.3. datetime query parameter request and response</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>Either a date-time or an interval, per RFC 3339, section 5.6.</li>
     *     <li>Unbounded start/end use a double-dot ({@code ..}) or an empty string.</li>
     * </ul>
     * <pre>
     * datetime=2018-02-12T23:20:50Z
     * datetime=2018-02-12T00:00:00Z/2018-03-18T12:31:12Z
     * datetime=2018-02-12T00:00:00Z/..
     * datetime=../2018-03-18T12:31:12Z
     * </pre>
     *
     * @return the datetime
     */
    public String getDatetime() {
        return datetime;
    }

    /**
     * @param datetime the datetime to set
     * @see #datetime(java.lang.String)
     */
    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    /**
     * @param datetime the datetime to set
     * @see #datetime(java.lang.String)
     */
    public GetTile datetime(String datetime) {
        setDatetime(datetime);
        return this;
    }

    /**
     * @return the subset
     * @see #subset(java.util.List)
     */
    public List<String> getSubset() {
        return subset;
    }

    /**
     * @param subset the subset to set
     * @see #subset(java.util.List)
     */
    public void setSubset(List<String> subset) {
        this.subset = subset;
    }

    /**
     * <blockquote><q>
     *     The subset parameter is a common building block used to select a subset of a geospatial
     *     resource shared with other OGC API Standards. This can be specified as an interval between
     *     lower and higher bound values or as a single value.
     *     </q><i><small><a href="https://docs.ogc.org/is/20-058/20-058.html#_02a878e9-746a-4800-9531-2f930bb367ce"> OGC API - Maps - Part 1: Core - 13.3.2. Parameter subset</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>Not defined by OGC API - Tiles - Part 1: Core, which has no {@code subset} query
     *     parameter; reused (with attribution) from OGC API - Maps for tiles of {@code dataType=map}.</li>
     *     <li>Trimming: {@code {axisAbbrev}({low}:{high})}; slicing: {@code {axisAbbrev}({value})}.</li>
     *     <li>An asterisk ({@code *}) can be used instead of {@code low}/{@code high} to indicate
     *     the minimum/maximum value.</li>
     * </ul>
     *
     * @param subset the subset to set
     */
    public GetTile subset(List<String> subset) {
        setSubset(subset);
        return this;
    }

    /**
     * @return the crs
     * @see #crs(java.lang.String)
     */
    public String getCrs() {
        return crs;
    }

    /**
     * @param crs the crs to set
     * @see #crs(java.lang.String)
     */
    public void setCrs(String crs) {
        this.crs = crs;
    }

    /**
     * Reproject the output tile to the given CRS.
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>Not defined by OGC API - Tiles - Part 1: Core, which has no {@code crs} query
     *     parameter of its own (tiles are normally reprojected implicitly by the TileMatrixSet's
     *     CRS); no verbatim spec text available for this field.</li>
     * </ul>
     *
     * @param crs the crs to set
     */
    public GetTile crs(String crs) {
        setCrs(crs);
        return this;
    }

    /**
     * @return the subsetCrs
     * @see #subsetCrs(java.lang.String)
     */
    public String getSubsetCrs() {
        return subsetCrs;
    }

    /**
     * @param subsetCrs the subsetCrs to set
     * @see #subsetCrs(java.lang.String)
     */
    public void setSubsetCrs(String subsetCrs) {
        this.subsetCrs = subsetCrs;
    }

    /**
     * <blockquote><q>
     *     A URI (or safe CURIE) of the coordinate reference system for the coordinates specified in
     *     the {@code subset} parameter. The valid values are [OGC:CRS84], the native (storage) CRS (if
     *     different), or the output {@code crs} (if specified).
     *     </q><i><small><a href="https://docs.ogc.org/is/20-058/20-058.html#_8bcbded1-5f70-4020-b90a-596fe8b8326b"> OGC API - Maps - Part 1: Core - 13.2.2. Parameter subset-crs</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional, defaults to {@code [OGC:CRS84]} if not specified.</li>
     *     <li>Not defined by OGC API - Tiles - Part 1: Core; reused (with attribution) from OGC
     *     API - Maps for tiles of {@code dataType=map}.</li>
     *     <li>Ignored if no {@link #subset(java.util.List)} parameter is used.</li>
     * </ul>
     *
     * @param subsetCrs the subsetCrs to set
     */
    public GetTile subsetCrs(String subsetCrs) {
        setSubsetCrs(subsetCrs);
        return this;
    }

    /**
     * @return the bgColor
     * @see #bgColor(java.lang.String)
     */
    public String getBgColor() {
        return bgcolor;
    }

    /**
     * @param bgColor the bgColor to set
     * @see #bgColor(java.lang.String)
     */
    public void setBgColor(String bgcolor) {
        this.bgcolor = bgcolor;
    }

    /**
     * <blockquote><q>
     *     Hexadecimal red-green-blue color value (0-255) or case-insensitive W3C web color name for
     *     the background color (default=0xFFFFFF).
     *     </q><i><small><a href="https://docs.ogc.org/is/20-058/20-058.html#_9571a0cb-12d6-4533-8c91-d8d69bc75762"> OGC API - Maps - Part 1: Core - 9.2.1. Parameters transparent and bgcolor</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional, default {@code 0xFFFFFF}.</li>
     *     <li>Not defined by OGC API - Tiles - Part 1: Core; reused (with attribution) from OGC
     *     API - Maps for tiles of {@code dataType=map}.</li>
     *     <li>Six hex digits: red, green, blue intensities in that order.</li>
     * </ul>
     *
     * @param bgColor the bgColor to set
     */
    public GetTile bgColor(String bgcolor) {
        setBgColor(bgcolor);
        return this;
    }

    /**
     * @return the background transparency
     * @see #transparency(java.lang.Boolean)
     */
    public Boolean getTransparency() {
        return transparent;
    }

    /**
     * @param trs the background transparency to set
     * @see #transparency(java.lang.Boolean)
     */
    public void setTransparency(Boolean trs) {
        this.transparent = trs;
    }

    /**
     * <blockquote><q>
     *     (defaults to {@code true} without a {@code bgcolor} specified, but to {@code false} when a
     *     {@code bgcolor} is used).
     *     </q><i><small><a href="https://docs.ogc.org/is/20-058/20-058.html#_9571a0cb-12d6-4533-8c91-d8d69bc75762"> OGC API - Maps - Part 1: Core - 9.2.1. Parameters transparent and bgcolor</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional, default {@code true}.</li>
     *     <li>Not defined by OGC API - Tiles - Part 1: Core; reused (with attribution) from OGC
     *     API - Maps for tiles of {@code dataType=map}.</li>
     *     <li>If {@code true} and a {@link #bgColor(java.lang.String)} is specified, the background's
     *     opacity is set to 0.</li>
     * </ul>
     *
     * @param trs the background transparency to set
     */
    public GetTile transparency(Boolean trs) {
        setTransparency(trs);
        return this;
    }

}
