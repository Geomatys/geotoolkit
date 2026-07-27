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
import java.util.Objects;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * Variable Matrix Width data structure
 */
@JsonPropertyOrder({
    VariableMatrixWidth.JSON_PROPERTY_COALESCE,
    VariableMatrixWidth.JSON_PROPERTY_MIN_TILE_ROW,
    VariableMatrixWidth.JSON_PROPERTY_MAX_TILE_ROW
})
public final class VariableMatrixWidth extends DataTransferObject {

    public static final String JSON_PROPERTY_COALESCE = "coalesce";
    private BigDecimal coalesce;

    public static final String JSON_PROPERTY_MIN_TILE_ROW = "minTileRow";
    private BigDecimal minTileRow;

    public static final String JSON_PROPERTY_MAX_TILE_ROW = "maxTileRow";
    private BigDecimal maxTileRow;

    public VariableMatrixWidth() {
    }

    public VariableMatrixWidth coalesce(BigDecimal coalesce) {
        this.coalesce = coalesce;
        return this;
    }

    /**
     * Number of tiles in width that coalesce in a single tile for these rows minimum: 2
     *
     * @return coalesce
     */
    @JsonProperty(JSON_PROPERTY_COALESCE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public BigDecimal getCoalesce() {
        return coalesce;
    }

    @JsonProperty(JSON_PROPERTY_COALESCE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setCoalesce(BigDecimal coalesce) {
        this.coalesce = coalesce;
    }

    public VariableMatrixWidth minTileRow(BigDecimal minTileRow) {
        this.minTileRow = minTileRow;
        return this;
    }

    /**
     * First tile row where the coalescence factor applies for this tilematrix minimum: 0
     *
     * @return minTileRow
     */
    @JsonProperty(JSON_PROPERTY_MIN_TILE_ROW)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public BigDecimal getMinTileRow() {
        return minTileRow;
    }

    @JsonProperty(JSON_PROPERTY_MIN_TILE_ROW)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setMinTileRow(BigDecimal minTileRow) {
        this.minTileRow = minTileRow;
    }

    public VariableMatrixWidth maxTileRow(BigDecimal maxTileRow) {
        this.maxTileRow = maxTileRow;
        return this;
    }

    /**
     * Last tile row where the coalescence factor applies for this tilematrix minimum: 0
     *
     * @return maxTileRow
     */
    @JsonProperty(JSON_PROPERTY_MAX_TILE_ROW)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public BigDecimal getMaxTileRow() {
        return maxTileRow;
    }

    @JsonProperty(JSON_PROPERTY_MAX_TILE_ROW)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setMaxTileRow(BigDecimal maxTileRow) {
        this.maxTileRow = maxTileRow;
    }

    /**
     * Return true if this variableMatrixWidth object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        VariableMatrixWidth other = (VariableMatrixWidth) o;
        return Objects.equals(this.coalesce, other.coalesce)
                && Objects.equals(this.minTileRow, other.minTileRow)
                && Objects.equals(this.maxTileRow, other.maxTileRow);
    }

    @Override
    public int hashCode() {
        return Objects.hash(coalesce, minTileRow, maxTileRow);
    }

}
