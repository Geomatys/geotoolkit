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
package org.geotoolkit.ogcapi.model.tiles;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.*;
import jakarta.xml.bind.annotation.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.geotoolkit.ogcapi.model.DataTransferObject;
import org.geotoolkit.ogcapi.model.common.Crs;

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
@XmlRootElement(name = "TileSetCenterPoint")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "TileSetCenterPoint")
public final class TileSetCenterPoint extends DataTransferObject {

    public static final String JSON_PROPERTY_COORDINATES = "coordinates";
    @XmlElement(name = "coordinates")
    @jakarta.annotation.Nonnull
    private List<BigDecimal> coordinates = new ArrayList<>();

    public static final String JSON_PROPERTY_CRS = "crs";
    @XmlElement(name = "crs")
    @jakarta.annotation.Nullable
    private Crs crs;

    public static final String JSON_PROPERTY_TILE_MATRIX = "tileMatrix";
    @XmlElement(name = "tileMatrix")
    @jakarta.annotation.Nullable
    private String tileMatrix;

    public static final String JSON_PROPERTY_SCALE_DENOMINATOR = "scaleDenominator";
    @XmlElement(name = "scaleDenominator")
    @jakarta.annotation.Nullable
    private BigDecimal scaleDenominator;

    public static final String JSON_PROPERTY_CELL_SIZE = "cellSize";
    @XmlElement(name = "cellSize")
    @jakarta.annotation.Nullable
    private BigDecimal cellSize;

    public TileSetCenterPoint() {
    }

    public TileSetCenterPoint coordinates(@jakarta.annotation.Nonnull List<BigDecimal> coordinates) {
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
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_COORDINATES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "coordinates")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<BigDecimal> getCoordinates() {
        return coordinates;
    }

    @JsonProperty(JSON_PROPERTY_COORDINATES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "coordinates")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setCoordinates(@jakarta.annotation.Nonnull List<BigDecimal> coordinates) {
        this.coordinates = coordinates;
    }

    public TileSetCenterPoint crs(@jakarta.annotation.Nullable Crs crs) {
        this.crs = crs;
        return this;
    }

    /**
     * Get crs
     *
     * @return crs
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_CRS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "crs")
    public Crs getCrs() {
        return crs;
    }

    @JsonProperty(JSON_PROPERTY_CRS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "crs")
    public void setCrs(@jakarta.annotation.Nullable Crs crs) {
        this.crs = crs;
    }

    public TileSetCenterPoint tileMatrix(@jakarta.annotation.Nullable String tileMatrix) {
        this.tileMatrix = tileMatrix;
        return this;
    }

    /**
     * TileMatrix identifier associated with the scaleDenominator
     *
     * @return tileMatrix
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_TILE_MATRIX)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "tileMatrix")
    public String getTileMatrix() {
        return tileMatrix;
    }

    @JsonProperty(JSON_PROPERTY_TILE_MATRIX)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "tileMatrix")
    public void setTileMatrix(@jakarta.annotation.Nullable String tileMatrix) {
        this.tileMatrix = tileMatrix;
    }

    public TileSetCenterPoint scaleDenominator(@jakarta.annotation.Nullable BigDecimal scaleDenominator) {
        this.scaleDenominator = scaleDenominator;
        return this;
    }

    /**
     * Scale denominator of the tile matrix selected
     *
     * @return scaleDenominator
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_SCALE_DENOMINATOR)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "scaleDenominator")
    public BigDecimal getScaleDenominator() {
        return scaleDenominator;
    }

    @JsonProperty(JSON_PROPERTY_SCALE_DENOMINATOR)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "scaleDenominator")
    public void setScaleDenominator(@jakarta.annotation.Nullable BigDecimal scaleDenominator) {
        this.scaleDenominator = scaleDenominator;
    }

    public TileSetCenterPoint cellSize(@jakarta.annotation.Nullable BigDecimal cellSize) {
        this.cellSize = cellSize;
        return this;
    }

    /**
     * Cell size of the tile matrix selected
     *
     * @return cellSize
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_CELL_SIZE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "cellSize")
    public BigDecimal getCellSize() {
        return cellSize;
    }

    @JsonProperty(JSON_PROPERTY_CELL_SIZE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "cellSize")
    public void setCellSize(@jakarta.annotation.Nullable BigDecimal cellSize) {
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
