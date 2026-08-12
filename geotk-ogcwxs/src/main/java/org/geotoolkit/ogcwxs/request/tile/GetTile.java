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
package org.geotoolkit.ogcwxs.request.tile;

import java.util.Map;
import org.geotoolkit.ogcwxs.request.RequestParameters;

/**
 * @author Quentin Bialota (Geomatys)
 */
public class GetTile extends RequestParameters {

    private String layer;
    private String style;
    private String format = "image/png";
    private String tileMatrixSet;
    private String tileMatrix;
    private Long tileRow;
    private Long tileCol;
    private Map<String, String> dimensions;

    /**
     * @return the layer
     */
    public String getLayer() {
        return layer;
    }

    /**
     * @param layer the layer to set
     */
    public void setLayer(String layer) {
        this.layer = layer;
    }

    /**
     * @param layer the layer to set
     * @return this
     */
    public GetTile layer(String layer) {
        this.layer = layer;
        return this;
    }

    /**
     * @return the style
     */
    public String getStyle() {
        return style;
    }

    /**
     * @param style the style to set
     */
    public void setStyle(String style) {
        this.style = style;
    }

    /**
     * @param style the style to set
     * @return this
     */
    public GetTile style(String style) {
        this.style = style;
        return this;
    }

    /**
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
     * @return this
     */
    public GetTile format(String format) {
        this.format = format;
        return this;
    }

    /**
     * @return the tileMatrixSet
     */
    public String getTileMatrixSet() {
        return tileMatrixSet;
    }

    /**
     * @param tileMatrixSet the tileMatrixSet to set
     */
    public void setTileMatrixSet(String tileMatrixSet) {
        this.tileMatrixSet = tileMatrixSet;
    }

    /**
     * @param tileMatrixSet the tileMatrixSet to set
     * @return this
     */
    public GetTile tileMatrixSet(String tileMatrixSet) {
        this.tileMatrixSet = tileMatrixSet;
        return this;
    }

    /**
     * @return the tileMatrix
     */
    public String getTileMatrix() {
        return tileMatrix;
    }

    /**
     * @param tileMatrix the tileMatrix to set
     */
    public void setTileMatrix(String tileMatrix) {
        this.tileMatrix = tileMatrix;
    }

    /**
     * @param tileMatrix the tileMatrix to set
     * @return this
     */
    public GetTile tileMatrix(String tileMatrix) {
        this.tileMatrix = tileMatrix;
        return this;
    }

    /**
     * @return the tileRow
     */
    public Long getTileRow() {
        return tileRow;
    }

    /**
     * @param tileRow the tileRow to set
     */
    public void setTileRow(Long tileRow) {
        this.tileRow = tileRow;
    }

    /**
     * @param tileRow the tileRow to set
     * @return this
     */
    public GetTile tileRow(Long tileRow) {
        this.tileRow = tileRow;
        return this;
    }

    /**
     * @return the tileCol
     */
    public Long getTileCol() {
        return tileCol;
    }

    /**
     * @param tileCol the tileCol to set
     */
    public void setTileCol(Long tileCol) {
        this.tileCol = tileCol;
    }

    /**
     * @param tileCol the tileCol to set
     * @return this
     */
    public GetTile tileCol(Long tileCol) {
        this.tileCol = tileCol;
        return this;
    }

    /**
     * @return additional dimension name/value pairs (TIME, ELEVATION, ...)
     */
    public Map<String, String> getDimensions() {
        return dimensions;
    }

    /**
     * @param dimensions the dimensions to set
     */
    public void setDimensions(Map<String, String> dimensions) {
        this.dimensions = dimensions;
    }

    /**
     * @param dimensions the dimensions to set
     * @return this
     */
    public GetTile dimensions(Map<String, String> dimensions) {
        this.dimensions = dimensions;
        return this;
    }

}
