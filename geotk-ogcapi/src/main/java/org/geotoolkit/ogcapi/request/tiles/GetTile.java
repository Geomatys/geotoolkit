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
 * To retrieve tiles.
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
     * Get tiling type (Map,Coverage,Vector).
     * Default is Vector.
     *
     * @return
     */
    public TilingType getType() {
        return type;
    }

    /**
     * @param type the type to set
     * @see #getType()
     */
    public void setType(TilingType type) {
        this.type = type;
    }

    /**
     * @param type the type to set
     * @see #getType()
     */
    public GetTile type(TilingType type) {
        this.type = type;
        return this;
    }

    /**
     * Local identifier of a collection (required if request on a collection)
     *
     * @return the collectionId
     */
    public String getCollectionId() {
        return collectionId;
    }

    /**
     * @param collectionId the collectionId to set
     * @see #getCollectionId()
     */
    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    /**
     * @param collectionId the collectionId to set
     * @see #getCollectionId()
     */
    public GetTile collectionId(String collectionId) {
        setCollectionId(collectionId);
        return this;
    }

    /**
     * Identifier for a supported TileMatrixSet (required)
     *
     * @return the TileMatrixSet id
     */
    public String getTileMatrixSetId() {
        return tileMatrixSetId;
    }

    /**
     * @param tileMatrixSetId the TileMatrixSet id to set
     * @see #getTileMatrixSetId()
     */
    public void setTileMatrixSetId(String tileMatrixSetId) {
        this.tileMatrixSetId = tileMatrixSetId;
    }

    /**
     * @param tileMatrixSetId the TileMatrixSet id to set
     * @see #getTileMatrixSetId()
     */
    public GetTile tileMatrixSetId(String tileMatrixSetId) {
        setTileMatrixSetId(tileMatrixSetId);
        return this;
    }

    /**
     * Identifier selecting one of the scales defined in the TileMatrixSet and representing the
     * scaleDenominator the tile. For example, Ireland is fully within the Tile at WebMercatorQuad tileMatrix&#x3D;5,
     * tileRow&#x3D;10 and tileCol&#x3D;15. (required)
     *
     * @return the tile matrix
     */
    public String getTileMatrixId() {
        return tileMatrixId;
    }

    /**
     * @param tileMatrixId the TileMatrix id to set
     * @see #getTileMatrixId()
     */
    public void setTileMatrixId(String tileMatrixId) {
        this.tileMatrixId = tileMatrixId;
    }

    /**
     * @param tileMatrixId the TileMatrix id to set
     * @see #getTileMatrixId()
     */
    public GetTile tileMatrixId(String tileMatrixId) {
        setTileMatrixId(tileMatrixId);
        return this;
    }

    /**
     * An identifier representing a specific style. (required if a styled map or vector)
     *
     * @return the style id
     */
    public String getStyleId() {
        return styleId;
    }

    /**
     * @param styleId the style id to set
     * @see #getStyleId()
     */
    public void setStyleId(String styleId) {
        this.styleId = styleId;
    }

    /**
     * @param styleId the style id to set
     * @see #getStyleId()
     */
    public GetTile styleId(String styleId) {
        setStyleId(styleId);
        return this;
    }

    /**
     * Row index of the tile on the selected TileMatrix. It cannot exceed the MatrixWidth-1 for the
     * selected TileMatrix. For example, Ireland is fully within the Tile at WebMercatorQuad tileMatrix&#x3D;5,
     * tileRow&#x3D;10 and tileCol&#x3D;15. (required)
     *
     * @return the tile row
     */
    public Integer getTileRow() {
        return tileRow;
    }

    /**
     * @param tileRow the Tile row to set
     * @see #getTileRow()
     */
    public void setTileRow(Integer tileRow) {
        this.tileRow = tileRow;
    }

    /**
     * @param tileRow the Tile row to set
     * @see #getTileRow()
     */
    public GetTile tileRow(Integer tileRow) {
        setTileRow(tileRow);
        return this;
    }

    /**
     * tileCol Column index of the tile on the selected TileMatrix. It cannot exceed the MatrixHeight-1 for the
     * selected TileMatrix. For example, Ireland is fully within the Tile at WebMercatorQuad tileMatrix&#x3D;5,
     * tileRow&#x3D;10 and tileCol&#x3D;15. (required)
     *
     * @return the tile col
     */
    public Integer getTileCol() {
        return tileCol;
    }

    /**
     * @param tileCol the Tile col to set
     * @see #getTileRow()
     */
    public void setTileCol(Integer tileCol) {
        this.tileCol = tileCol;
    }

    /**
     * @param tileCol the Tile col to set
     * @see #getTileCol()
     */
    public GetTile tileCol(Integer tileCol) {
        setTileCol(tileCol);
        return this;
    }

    /**
     * The format of the tile response
     * (e.g. tiff, netcdf, png for coverage tiles).
     * (e.g. mvt, json (geojson) for vector tiles).
     *
     * @return the format
     */
    public String getFormat() {
        return format;
    }

    /**
     * @param format the format to set
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * @param format the format to set
     */
    public GetTile format(String format) {
        setFormat(format);
        return this;
    }

    /**
     * The collections that should be included in the response. The parameter value is a
     * comma-separated list of collection identifiers. If the parameters is missing, some or all collections will be
     * included. The collection will be rendered in the order specified, with the last one showing on top, unless the
     * priority is overridden by styling rules. (optional)
     *
     * @return the collections
     */
    public List<String> getCollections() {
        return collections;
    }

    /**
     * @param collections the collections to set
     * @see #getCollections()
     */
    public void setCollections(List<String> collections) {
        this.collections = collections;
    }

    /**
     * @param collections the collections to set
     * @see #getCollections()
     */
    public GetTile collections(List<String> collections) {
        setCollections(collections);
        return this;
    }

    /**
     * Either a date-time or an interval, half-bounded or bounded. Date and time expressions adhere to
     * RFC 3339. Half-bounded intervals are expressed using double-dots. Examples: * A date-time:
     * \&quot;2018-02-12T23:20:50Z\&quot; * A bounded interval: \&quot;2018-02-12T00:00:00Z/2018-03-18T12:31:12Z\&quot;
     * * Half-bounded intervals: \&quot;2018-02-12T00:00:00Z/..\&quot; or \&quot;../2018-03-18T12:31:12Z\&quot; Only
     * features that have a temporal property that intersects the value of &#x60;datetime&#x60; are selected. If a
     * feature has multiple temporal properties, it is the decision of the server whether only a single temporal
     * property is used to determine the extent or all relevant temporal properties. (optional)
     *
     * @return the datetime
     */
    public String getDatetime() {
        return datetime;
    }

    /**
     * @param datetime the datetime to set
     * @see #getDatetime()
     */
    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    /**
     * @param datetime the datetime to set
     * @see #getDatetime()
     */
    public GetTile datetime(String datetime) {
        setDatetime(datetime);
        return this;
    }

    /**
     * Retrieve only part of the data by slicing or trimming along one or more axis For trimming:
     * {axisAbbrev}({low}:{high}) (preserves dimensionality) An asterisk (&#x60;*&#x60;) can be used instead of {low} or
     * {high} to indicate the minimum/maximum value. For slicing: {axisAbbrev}({value}) (reduces dimensionality)
     * (optional)
     *
     * @return the subset
     */
    public List<String> getSubset() {
        return subset;
    }

    /**
     * @param subset the subset to set
     * @see #getSubset()
     */
    public void setSubset(List<String> subset) {
        this.subset = subset;
    }

    /**
     * @param subset the subset to set
     * @see #getSubset()
     */
    public GetTile subset(List<String> subset) {
        setSubset(subset);
        return this;
    }

    /**
     * reproject the output to the given crs (optional)
     *
     * @return the crs
     */
    public String getCrs() {
        return crs;
    }

    /**
     * @param crs the crs to set
     * @see #getCrs()
     */
    public void setCrs(String crs) {
        this.crs = crs;
    }

    /**
     * @param crs the crs to set
     * @see #getCrs()
     */
    public GetTile crs(String crs) {
        setCrs(crs);
        return this;
    }

    /**
     * crs for the specified subset (optional)
     *
     * @return the subsetCrs
     */
    public String getSubsetCrs() {
        return subsetCrs;
    }

    /**
     * @param subsetCrs the subsetCrs to set
     * @see #getSubsetCrs()
     */
    public void setSubsetCrs(String subsetCrs) {
        this.subsetCrs = subsetCrs;
    }

    /**
     * @param subsetCrs the subsetCrs to set
     * @see #getSubsetCrs()
     */
    public GetTile subsetCrs(String subsetCrs) {
        setSubsetCrs(subsetCrs);
        return this;
    }

    /**
     * Web color name or hexadecimal 0x[AA]RRGGBB color value for the background color (default to 0x9C9C9C gray).
     * If alpha is not specified, full opacity is assumed. (optional, default to 0xFFFFFF)
     *
     * @return the bgColor
     */
    public String getBgColor() {
        return bgcolor;
    }

    /**
     * @param bgColor the bgColor to set
     * @see #getBgColor()
     */
    public void setBgColor(String bgcolor) {
        this.bgcolor = bgcolor;
    }

    /**
     * @param bgColor the bgColor to set
     * @see #getBgColor()
     */
    public GetTile bgColor(String bgcolor) {
        setBgColor(bgcolor);
        return this;
    }

    /**
     * Background transparency of map (default&#x3D;true). (optional, default to true)
     *
     * @return the background transparency
     */
    public Boolean getTransparency() {
        return transparent;
    }

    /**
     * @param trs the background transparency to set
     * @see #getTransparency()
     */
    public void setTransparency(Boolean trs) {
        this.transparent = trs;
    }

    /**
     * @param trs the background transparency to set
     * @see #getTransparency()
     */
    public GetTile transparency(Boolean trs) {
        setTransparency(trs);
        return this;
    }

}
