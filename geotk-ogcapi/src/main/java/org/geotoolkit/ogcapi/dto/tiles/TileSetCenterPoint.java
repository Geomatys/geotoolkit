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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.geotoolkit.ogcapi.dto.DataTransferObject;
import org.geotoolkit.ogcapi.dto.common.Crs;

/**
 * TileSetCenterPoint
 */
@JsonPropertyOrder({
    TileSetCenterPoint.JSON_PROPERTY_COORDINATES,
    TileSetCenterPoint.JSON_PROPERTY_CRS,
    TileSetCenterPoint.JSON_PROPERTY_TILE_MATRIX,
    TileSetCenterPoint.JSON_PROPERTY_SCALE_DENOMINATOR,
    TileSetCenterPoint.JSON_PROPERTY_CELL_SIZE
})
public final class TileSetCenterPoint extends DataTransferObject {

    public static final String JSON_PROPERTY_COORDINATES = "coordinates";
    private List<BigDecimal> coordinates = new ArrayList<>();

    public static final String JSON_PROPERTY_CRS = "crs";
    private Crs crs;

    public static final String JSON_PROPERTY_TILE_MATRIX = "tileMatrix";
    private String tileMatrix;

    public static final String JSON_PROPERTY_SCALE_DENOMINATOR = "scaleDenominator";
    private BigDecimal scaleDenominator;

    public static final String JSON_PROPERTY_CELL_SIZE = "cellSize";
    private BigDecimal cellSize;

    public TileSetCenterPoint() {
    }

    public TileSetCenterPoint coordinates(List<BigDecimal> coordinates) {
        this.coordinates = coordinates;
        return this;
    }

    public TileSetCenterPoint addCoordinatesItem(BigDecimal coordinatesItem) {
        if (this.coordinates == null) {
            this.coordinates = new ArrayList<>();
        }
        this.coordinates.add(coordinatesItem);
        return this;
    }

    /**
     * Get coordinates
     *
     * @return coordinates
     */
    @JsonProperty(JSON_PROPERTY_COORDINATES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public List<BigDecimal> getCoordinates() {
        return coordinates;
    }

    @JsonProperty(JSON_PROPERTY_COORDINATES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setCoordinates(List<BigDecimal> coordinates) {
        this.coordinates = coordinates;
    }

    public TileSetCenterPoint crs(Crs crs) {
        this.crs = crs;
        return this;
    }

    /**
     * Get crs
     *
     * @return crs
     */
    @JsonProperty(JSON_PROPERTY_CRS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Crs getCrs() {
        return crs;
    }

    @JsonProperty(JSON_PROPERTY_CRS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setCrs(Crs crs) {
        this.crs = crs;
    }

    public TileSetCenterPoint tileMatrix(String tileMatrix) {
        this.tileMatrix = tileMatrix;
        return this;
    }

    /**
     * TileMatrix identifier associated with the scaleDenominator
     *
     * @return tileMatrix
     */
    @JsonProperty(JSON_PROPERTY_TILE_MATRIX)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getTileMatrix() {
        return tileMatrix;
    }

    @JsonProperty(JSON_PROPERTY_TILE_MATRIX)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setTileMatrix(String tileMatrix) {
        this.tileMatrix = tileMatrix;
    }

    public TileSetCenterPoint scaleDenominator(BigDecimal scaleDenominator) {
        this.scaleDenominator = scaleDenominator;
        return this;
    }

    /**
     * Scale denominator of the tile matrix selected
     *
     * @return scaleDenominator
     */
    @JsonProperty(JSON_PROPERTY_SCALE_DENOMINATOR)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public BigDecimal getScaleDenominator() {
        return scaleDenominator;
    }

    @JsonProperty(JSON_PROPERTY_SCALE_DENOMINATOR)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setScaleDenominator(BigDecimal scaleDenominator) {
        this.scaleDenominator = scaleDenominator;
    }

    public TileSetCenterPoint cellSize(BigDecimal cellSize) {
        this.cellSize = cellSize;
        return this;
    }

    /**
     * Cell size of the tile matrix selected
     *
     * @return cellSize
     */
    @JsonProperty(JSON_PROPERTY_CELL_SIZE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public BigDecimal getCellSize() {
        return cellSize;
    }

    @JsonProperty(JSON_PROPERTY_CELL_SIZE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setCellSize(BigDecimal cellSize) {
        this.cellSize = cellSize;
    }

    /**
     * Return true if this tileSet_centerPoint object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TileSetCenterPoint other = (TileSetCenterPoint) o;
        return Objects.equals(this.coordinates, other.coordinates)
                && Objects.equals(this.crs, other.crs)
                && Objects.equals(this.tileMatrix, other.tileMatrix)
                && Objects.equals(this.scaleDenominator, other.scaleDenominator)
                && Objects.equals(this.cellSize, other.cellSize);
    }

    @Override
    public int hashCode() {
        return Objects.hash(coordinates, crs, tileMatrix, scaleDenominator, cellSize);
    }

}
