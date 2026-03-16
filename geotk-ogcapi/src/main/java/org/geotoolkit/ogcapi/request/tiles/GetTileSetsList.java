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
 *
 * @author Johann Sorel (Geomatys)
 */
public final class GetTileSetsList extends RequestParameters {

    private TilingType type = TilingType.VECTOR;
    private String collectionId;
    private String styleId;
    private String format;

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
    public GetTileSetsList type(TilingType type) {
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
    public GetTileSetsList collectionId(String collectionId) {
        setCollectionId(collectionId);
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
    public GetTileSetsList styleId(String styleId) {
        setStyleId(styleId);
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
     */
    public GetTileSetsList format(String format) {
        setFormat(format);
        return this;
    }

}
