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
import java.util.Objects;
import org.geotoolkit.ogcapi.model.DataTransferObject;

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
@XmlRootElement(name = "TileMatrixLimits")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "TileMatrixLimits")
public final class TileMatrixLimits extends DataTransferObject {

    public static final String JSON_PROPERTY_TILE_MATRIX = "tileMatrix";
    @XmlElement(name = "tileMatrix")
    @jakarta.annotation.Nonnull
    private String tileMatrix;

    public static final String JSON_PROPERTY_MIN_TILE_ROW = "minTileRow";
    @XmlElement(name = "minTileRow")
    @jakarta.annotation.Nonnull
    private Integer minTileRow;

    public static final String JSON_PROPERTY_MAX_TILE_ROW = "maxTileRow";
    @XmlElement(name = "maxTileRow")
    @jakarta.annotation.Nonnull
    private Integer maxTileRow;

    public static final String JSON_PROPERTY_MIN_TILE_COL = "minTileCol";
    @XmlElement(name = "minTileCol")
    @jakarta.annotation.Nonnull
    private Integer minTileCol;

    public static final String JSON_PROPERTY_MAX_TILE_COL = "maxTileCol";
    @XmlElement(name = "maxTileCol")
    @jakarta.annotation.Nonnull
    private Integer maxTileCol;

    public TileMatrixLimits() {
    }

    public TileMatrixLimits tileMatrix(@jakarta.annotation.Nonnull String tileMatrix) {
        this.tileMatrix = tileMatrix;
        return this;
    }

    /**
     * Get tileMatrix
     *
     * @return tileMatrix
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_TILE_MATRIX)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "tileMatrix")
    public String getTileMatrix() {
        return tileMatrix;
    }

    @JsonProperty(JSON_PROPERTY_TILE_MATRIX)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "tileMatrix")
    public void setTileMatrix(@jakarta.annotation.Nonnull String tileMatrix) {
        this.tileMatrix = tileMatrix;
    }

    public TileMatrixLimits minTileRow(@jakarta.annotation.Nonnull Integer minTileRow) {
        this.minTileRow = minTileRow;
        return this;
    }

    /**
     * Get minTileRow minimum: 0
     *
     * @return minTileRow
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_MIN_TILE_ROW)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "minTileRow")
    public Integer getMinTileRow() {
        return minTileRow;
    }

    @JsonProperty(JSON_PROPERTY_MIN_TILE_ROW)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "minTileRow")
    public void setMinTileRow(@jakarta.annotation.Nonnull Integer minTileRow) {
        this.minTileRow = minTileRow;
    }

    public TileMatrixLimits maxTileRow(@jakarta.annotation.Nonnull Integer maxTileRow) {
        this.maxTileRow = maxTileRow;
        return this;
    }

    /**
     * Get maxTileRow minimum: 0
     *
     * @return maxTileRow
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_MAX_TILE_ROW)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "maxTileRow")
    public Integer getMaxTileRow() {
        return maxTileRow;
    }

    @JsonProperty(JSON_PROPERTY_MAX_TILE_ROW)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "maxTileRow")
    public void setMaxTileRow(@jakarta.annotation.Nonnull Integer maxTileRow) {
        this.maxTileRow = maxTileRow;
    }

    public TileMatrixLimits minTileCol(@jakarta.annotation.Nonnull Integer minTileCol) {
        this.minTileCol = minTileCol;
        return this;
    }

    /**
     * Get minTileCol minimum: 0
     *
     * @return minTileCol
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_MIN_TILE_COL)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "minTileCol")
    public Integer getMinTileCol() {
        return minTileCol;
    }

    @JsonProperty(JSON_PROPERTY_MIN_TILE_COL)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "minTileCol")
    public void setMinTileCol(@jakarta.annotation.Nonnull Integer minTileCol) {
        this.minTileCol = minTileCol;
    }

    public TileMatrixLimits maxTileCol(@jakarta.annotation.Nonnull Integer maxTileCol) {
        this.maxTileCol = maxTileCol;
        return this;
    }

    /**
     * Get maxTileCol minimum: 0
     *
     * @return maxTileCol
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_MAX_TILE_COL)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "maxTileCol")
    public Integer getMaxTileCol() {
        return maxTileCol;
    }

    @JsonProperty(JSON_PROPERTY_MAX_TILE_COL)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "maxTileCol")
    public void setMaxTileCol(@jakarta.annotation.Nonnull Integer maxTileCol) {
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
