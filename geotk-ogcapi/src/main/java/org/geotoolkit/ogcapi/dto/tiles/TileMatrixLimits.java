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
package org.geotoolkit.ogcapi.dto.tiles;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.Objects;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * The limits for an individual tile matrix of a TileSet&#39;s TileMatrixSet, as defined in the OGC 2D TileMatrixSet and
 * TileSet Metadata Standard
 */
@JsonPropertyOrder({
    TileMatrixLimits.JSON_PROPERTY_TILE_MATRIX,
    TileMatrixLimits.JSON_PROPERTY_MIN_TILE_ROW,
    TileMatrixLimits.JSON_PROPERTY_MAX_TILE_ROW,
    TileMatrixLimits.JSON_PROPERTY_MIN_TILE_COL,
    TileMatrixLimits.JSON_PROPERTY_MAX_TILE_COL
})
public final class TileMatrixLimits extends DataTransferObject {

    public static final String JSON_PROPERTY_TILE_MATRIX = "tileMatrix";
    private String tileMatrix;

    public static final String JSON_PROPERTY_MIN_TILE_ROW = "minTileRow";
    private Integer minTileRow;

    public static final String JSON_PROPERTY_MAX_TILE_ROW = "maxTileRow";
    private Integer maxTileRow;

    public static final String JSON_PROPERTY_MIN_TILE_COL = "minTileCol";
    private Integer minTileCol;

    public static final String JSON_PROPERTY_MAX_TILE_COL = "maxTileCol";
    private Integer maxTileCol;

    public TileMatrixLimits() {
    }

    public TileMatrixLimits tileMatrix(String tileMatrix) {
        this.tileMatrix = tileMatrix;
        return this;
    }

    /**
     * Get tileMatrix
     *
     * @return tileMatrix
     */
    @JsonProperty(JSON_PROPERTY_TILE_MATRIX)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public String getTileMatrix() {
        return tileMatrix;
    }

    @JsonProperty(JSON_PROPERTY_TILE_MATRIX)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setTileMatrix(String tileMatrix) {
        this.tileMatrix = tileMatrix;
    }

    public TileMatrixLimits minTileRow(Integer minTileRow) {
        this.minTileRow = minTileRow;
        return this;
    }

    /**
     * Get minTileRow minimum: 0
     *
     * @return minTileRow
     */
    @JsonProperty(JSON_PROPERTY_MIN_TILE_ROW)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public Integer getMinTileRow() {
        return minTileRow;
    }

    @JsonProperty(JSON_PROPERTY_MIN_TILE_ROW)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setMinTileRow(Integer minTileRow) {
        this.minTileRow = minTileRow;
    }

    public TileMatrixLimits maxTileRow(Integer maxTileRow) {
        this.maxTileRow = maxTileRow;
        return this;
    }

    /**
     * Get maxTileRow minimum: 0
     *
     * @return maxTileRow
     */
    @JsonProperty(JSON_PROPERTY_MAX_TILE_ROW)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public Integer getMaxTileRow() {
        return maxTileRow;
    }

    @JsonProperty(JSON_PROPERTY_MAX_TILE_ROW)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setMaxTileRow(Integer maxTileRow) {
        this.maxTileRow = maxTileRow;
    }

    public TileMatrixLimits minTileCol(Integer minTileCol) {
        this.minTileCol = minTileCol;
        return this;
    }

    /**
     * Get minTileCol minimum: 0
     *
     * @return minTileCol
     */
    @JsonProperty(JSON_PROPERTY_MIN_TILE_COL)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public Integer getMinTileCol() {
        return minTileCol;
    }

    @JsonProperty(JSON_PROPERTY_MIN_TILE_COL)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setMinTileCol(Integer minTileCol) {
        this.minTileCol = minTileCol;
    }

    public TileMatrixLimits maxTileCol(Integer maxTileCol) {
        this.maxTileCol = maxTileCol;
        return this;
    }

    /**
     * Get maxTileCol minimum: 0
     *
     * @return maxTileCol
     */
    @JsonProperty(JSON_PROPERTY_MAX_TILE_COL)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public Integer getMaxTileCol() {
        return maxTileCol;
    }

    @JsonProperty(JSON_PROPERTY_MAX_TILE_COL)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setMaxTileCol(Integer maxTileCol) {
        this.maxTileCol = maxTileCol;
    }

    /**
     * Return true if this tileMatrixLimits object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TileMatrixLimits other = (TileMatrixLimits) o;
        return Objects.equals(this.tileMatrix, other.tileMatrix)
                && Objects.equals(this.minTileRow, other.minTileRow)
                && Objects.equals(this.maxTileRow, other.maxTileRow)
                && Objects.equals(this.minTileCol, other.minTileCol)
                && Objects.equals(this.maxTileCol, other.maxTileCol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tileMatrix, minTileRow, maxTileRow, minTileCol, maxTileCol);
    }

}
