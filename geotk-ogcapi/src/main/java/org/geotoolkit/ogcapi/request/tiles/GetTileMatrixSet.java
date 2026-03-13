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

/**
 * To retrive the definition of the specified tiling scheme (tile matrix set).
 *
 * @author Johann Sorel (Geomatys)
 */
public final class GetTileMatrixSet extends RequestParameters {

    private String tileMatrixSetId;
    private String format;

    /**
     * get tileMatrixSetId Identifier for a supported TileMatrixSet (required)
     *
     * @return tileMatrixSetId
     */
    public String getTileMatrixSetId() {
        return tileMatrixSetId;
    }

    /**
     * @param tileMatrixSetId the tileMatrixSetId to set
     * @see #getTileMatrixSetId()
     */
    public void setTileMatrixSetId(String tileMatrixSetId) {
        this.tileMatrixSetId = tileMatrixSetId;
    }

    /**
     * @param tileMatrixSetId the tileMatrixSetId to set
     * @see #getTileMatrixSetId()
     */
    public GetTileMatrixSet tileMatrixSetId(String tileMatrixSetId) {
        setTileMatrixSetId(tileMatrixSetId);
        return this;
    }

    /**
     * Get the format of the response.
     * If no value is provided, the accept header is used to determine the format.
     * Accepted values are &#39;json&#39; or &#39;html&#39;. (optional)
     *
     * @return the format
     */
    public String getFormat() {
        return format;
    }

    /**
     * @param format the format to set
     * @see #getFormat()
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * @param format the format to set
     * @see #getFormat()
     */
    public GetTileMatrixSet format(String format) {
        setFormat(format);
        return this;
    }


}
