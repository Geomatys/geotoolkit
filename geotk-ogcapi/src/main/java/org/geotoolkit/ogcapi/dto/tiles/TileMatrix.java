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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * A tile matrix, usually corresponding to a particular zoom level of a TileMatrixSet.
 */
@JsonPropertyOrder({
    TileMatrix.JSON_PROPERTY_TITLE,
    TileMatrix.JSON_PROPERTY_DESCRIPTION,
    TileMatrix.JSON_PROPERTY_KEYWORDS,
    TileMatrix.JSON_PROPERTY_ID,
    TileMatrix.JSON_PROPERTY_SCALE_DENOMINATOR,
    TileMatrix.JSON_PROPERTY_CELL_SIZE,
    TileMatrix.JSON_PROPERTY_CORNER_OF_ORIGIN,
    TileMatrix.JSON_PROPERTY_POINT_OF_ORIGIN,
    TileMatrix.JSON_PROPERTY_TILE_WIDTH,
    TileMatrix.JSON_PROPERTY_TILE_HEIGHT,
    TileMatrix.JSON_PROPERTY_MATRIX_HEIGHT,
    TileMatrix.JSON_PROPERTY_MATRIX_WIDTH,
    TileMatrix.JSON_PROPERTY_VARIABLE_MATRIX_WIDTHS
})
public final class TileMatrix extends DataTransferObject {

    public static final String JSON_PROPERTY_TITLE = "title";
    private String title;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    private String description;

    public static final String JSON_PROPERTY_KEYWORDS = "keywords";
    private List<String> keywords = new ArrayList<>();

    public static final String JSON_PROPERTY_ID = "id";
    private String id;

    public static final String JSON_PROPERTY_SCALE_DENOMINATOR = "scaleDenominator";
    private BigDecimal scaleDenominator;

    public static final String JSON_PROPERTY_CELL_SIZE = "cellSize";
    private BigDecimal cellSize;

    /**
     * The corner of the tile matrix (_topLeft_ or _bottomLeft_) used as the origin for numbering tile rows and columns.
     * This corner is also a corner of the (0, 0) tile.
     */
    public enum CornerOfOriginEnum {
        TOP_LEFT(String.valueOf("topLeft")),
        BOTTOM_LEFT(String.valueOf("bottomLeft"));

        private String value;

        CornerOfOriginEnum(String value) {
            this.value = value;
        }

        @JsonValue
        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static CornerOfOriginEnum fromValue(String value) {
            for (CornerOfOriginEnum b : CornerOfOriginEnum.values()) {
                if (b.value.equals(value)) {
                    return b;
                }
            }
            throw new IllegalArgumentException("Unexpected value '" + value + "'");
        }
    }

    public static final String JSON_PROPERTY_CORNER_OF_ORIGIN = "cornerOfOrigin";
    private CornerOfOriginEnum cornerOfOrigin = CornerOfOriginEnum.TOP_LEFT;

    public static final String JSON_PROPERTY_POINT_OF_ORIGIN = "pointOfOrigin";
    private Object pointOfOrigin;

    public static final String JSON_PROPERTY_TILE_WIDTH = "tileWidth";
    private BigDecimal tileWidth;

    public static final String JSON_PROPERTY_TILE_HEIGHT = "tileHeight";
    private BigDecimal tileHeight;

    public static final String JSON_PROPERTY_MATRIX_HEIGHT = "matrixHeight";
    private BigDecimal matrixHeight;

    public static final String JSON_PROPERTY_MATRIX_WIDTH = "matrixWidth";
    private BigDecimal matrixWidth;

    public static final String JSON_PROPERTY_VARIABLE_MATRIX_WIDTHS = "variableMatrixWidths";
    private List<VariableMatrixWidth> variableMatrixWidths = new ArrayList<>();

    public TileMatrix() {
    }

    public TileMatrix title(String title) {
        this.title = title;
        return this;
    }

    /**
     * Title of this tile matrix, normally used for display to a human
     *
     * @return title
     */
    @JsonProperty(JSON_PROPERTY_TITLE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getTitle() {
        return title;
    }

    @JsonProperty(JSON_PROPERTY_TITLE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setTitle(String title) {
        this.title = title;
    }

    public TileMatrix description(String description) {
        this.description = description;
        return this;
    }

    /**
     * Brief narrative description of this tile matrix set, normally available for display to a human
     *
     * @return description
     */
    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getDescription() {
        return description;
    }

    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setDescription(String description) {
        this.description = description;
    }

    public TileMatrix keywords(List<String> keywords) {
        this.keywords = keywords;
        return this;
    }

    public TileMatrix addKeywordsItem(String keywordsItem) {
        if (this.keywords == null) {
            this.keywords = new ArrayList<>();
        }
        this.keywords.add(keywordsItem);
        return this;
    }

    /**
     * Unordered list of one or more commonly used or formalized word(s) or phrase(s) used to describe this dataset
     *
     * @return keywords
     */
    @JsonProperty(JSON_PROPERTY_KEYWORDS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public List<String> getKeywords() {
        return keywords;
    }

    @JsonProperty(JSON_PROPERTY_KEYWORDS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public TileMatrix id(String id) {
        this.id = id;
        return this;
    }

    /**
     * Identifier selecting one of the scales defined in the TileMatrixSet and representing the scaleDenominator the
     * tile. Implementation of &#39;identifier&#39;
     *
     * @return id
     */
    @JsonProperty(JSON_PROPERTY_ID)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public String getId() {
        return id;
    }

    @JsonProperty(JSON_PROPERTY_ID)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setId(String id) {
        this.id = id;
    }

    public TileMatrix scaleDenominator(BigDecimal scaleDenominator) {
        this.scaleDenominator = scaleDenominator;
        return this;
    }

    /**
     * Scale denominator of this tile matrix
     *
     * @return scaleDenominator
     */
    @JsonProperty(JSON_PROPERTY_SCALE_DENOMINATOR)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public BigDecimal getScaleDenominator() {
        return scaleDenominator;
    }

    @JsonProperty(JSON_PROPERTY_SCALE_DENOMINATOR)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setScaleDenominator(BigDecimal scaleDenominator) {
        this.scaleDenominator = scaleDenominator;
    }

    public TileMatrix cellSize(BigDecimal cellSize) {
        this.cellSize = cellSize;
        return this;
    }

    /**
     * Cell size of this tile matrix
     *
     * @return cellSize
     */
    @JsonProperty(JSON_PROPERTY_CELL_SIZE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public BigDecimal getCellSize() {
        return cellSize;
    }

    @JsonProperty(JSON_PROPERTY_CELL_SIZE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setCellSize(BigDecimal cellSize) {
        this.cellSize = cellSize;
    }

    public TileMatrix cornerOfOrigin(CornerOfOriginEnum cornerOfOrigin) {
        this.cornerOfOrigin = cornerOfOrigin;
        return this;
    }

    /**
     * The corner of the tile matrix (_topLeft_ or _bottomLeft_) used as the origin for numbering tile rows and columns.
     * This corner is also a corner of the (0, 0) tile.
     *
     * @return cornerOfOrigin
     */
    @JsonProperty(JSON_PROPERTY_CORNER_OF_ORIGIN)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public CornerOfOriginEnum getCornerOfOrigin() {
        return cornerOfOrigin;
    }

    @JsonProperty(JSON_PROPERTY_CORNER_OF_ORIGIN)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setCornerOfOrigin(CornerOfOriginEnum cornerOfOrigin) {
        this.cornerOfOrigin = cornerOfOrigin;
    }

    public TileMatrix pointOfOrigin(Object pointOfOrigin) {
        this.pointOfOrigin = pointOfOrigin;
        return this;
    }

    /**
     * Get pointOfOrigin
     *
     * @return pointOfOrigin
     */
    @JsonProperty(JSON_PROPERTY_POINT_OF_ORIGIN)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public Object getPointOfOrigin() {
        return pointOfOrigin;
    }

    @JsonProperty(JSON_PROPERTY_POINT_OF_ORIGIN)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setPointOfOrigin(Object pointOfOrigin) {
        this.pointOfOrigin = pointOfOrigin;
    }

    public TileMatrix tileWidth(BigDecimal tileWidth) {
        this.tileWidth = tileWidth;
        return this;
    }

    /**
     * Width of each tile of this tile matrix in pixels minimum: 1
     *
     * @return tileWidth
     */
    @JsonProperty(JSON_PROPERTY_TILE_WIDTH)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public BigDecimal getTileWidth() {
        return tileWidth;
    }

    @JsonProperty(JSON_PROPERTY_TILE_WIDTH)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setTileWidth(BigDecimal tileWidth) {
        this.tileWidth = tileWidth;
    }

    public TileMatrix tileHeight(BigDecimal tileHeight) {
        this.tileHeight = tileHeight;
        return this;
    }

    /**
     * Height of each tile of this tile matrix in pixels minimum: 1
     *
     * @return tileHeight
     */
    @JsonProperty(JSON_PROPERTY_TILE_HEIGHT)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public BigDecimal getTileHeight() {
        return tileHeight;
    }

    @JsonProperty(JSON_PROPERTY_TILE_HEIGHT)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setTileHeight(BigDecimal tileHeight) {
        this.tileHeight = tileHeight;
    }

    public TileMatrix matrixHeight(BigDecimal matrixHeight) {
        this.matrixHeight = matrixHeight;
        return this;
    }

    /**
     * Width of the matrix (number of tiles in width) minimum: 1
     *
     * @return matrixHeight
     */
    @JsonProperty(JSON_PROPERTY_MATRIX_HEIGHT)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public BigDecimal getMatrixHeight() {
        return matrixHeight;
    }

    @JsonProperty(JSON_PROPERTY_MATRIX_HEIGHT)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setMatrixHeight(BigDecimal matrixHeight) {
        this.matrixHeight = matrixHeight;
    }

    public TileMatrix matrixWidth(BigDecimal matrixWidth) {
        this.matrixWidth = matrixWidth;
        return this;
    }

    /**
     * Height of the matrix (number of tiles in height) minimum: 1
     *
     * @return matrixWidth
     */
    @JsonProperty(JSON_PROPERTY_MATRIX_WIDTH)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public BigDecimal getMatrixWidth() {
        return matrixWidth;
    }

    @JsonProperty(JSON_PROPERTY_MATRIX_WIDTH)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setMatrixWidth(BigDecimal matrixWidth) {
        this.matrixWidth = matrixWidth;
    }

    public TileMatrix variableMatrixWidths(List<VariableMatrixWidth> variableMatrixWidths) {
        this.variableMatrixWidths = variableMatrixWidths;
        return this;
    }

    public TileMatrix addVariableMatrixWidthsItem(VariableMatrixWidth variableMatrixWidthsItem) {
        if (this.variableMatrixWidths == null) {
            this.variableMatrixWidths = new ArrayList<>();
        }
        this.variableMatrixWidths.add(variableMatrixWidthsItem);
        return this;
    }

    /**
     * Describes the rows that has variable matrix width
     *
     * @return variableMatrixWidths
     */
    @JsonProperty(JSON_PROPERTY_VARIABLE_MATRIX_WIDTHS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public List<VariableMatrixWidth> getVariableMatrixWidths() {
        return variableMatrixWidths;
    }

    @JsonProperty(JSON_PROPERTY_VARIABLE_MATRIX_WIDTHS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setVariableMatrixWidths(List<VariableMatrixWidth> variableMatrixWidths) {
        this.variableMatrixWidths = variableMatrixWidths;
    }

    /**
     * Return true if this tileMatrix object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TileMatrix other = (TileMatrix) o;
        return Objects.equals(this.title, other.title)
                && Objects.equals(this.description, other.description)
                && Objects.equals(this.keywords, other.keywords)
                && Objects.equals(this.id, other.id)
                && Objects.equals(this.scaleDenominator, other.scaleDenominator)
                && Objects.equals(this.cellSize, other.cellSize)
                && Objects.equals(this.cornerOfOrigin, other.cornerOfOrigin)
                && Objects.equals(this.pointOfOrigin, other.pointOfOrigin)
                && Objects.equals(this.tileWidth, other.tileWidth)
                && Objects.equals(this.tileHeight, other.tileHeight)
                && Objects.equals(this.matrixHeight, other.matrixHeight)
                && Objects.equals(this.matrixWidth, other.matrixWidth)
                && Objects.equals(this.variableMatrixWidths, other.variableMatrixWidths);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, keywords, id, scaleDenominator, cellSize, cornerOfOrigin, pointOfOrigin, tileWidth, tileHeight, matrixHeight, matrixWidth, variableMatrixWidths);
    }

}
