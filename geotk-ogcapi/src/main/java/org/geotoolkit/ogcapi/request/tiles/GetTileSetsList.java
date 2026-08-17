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
 * Parameters for {@code GET /collections/{collectionId}/tiles}: the list of tilesets available
 * for a geospatial data resource (collection).
 *
 * <blockquote><q>
 *     A tilesets list resource links to a list of sets of tiles, each one belonging to a
 *     particular TileMatrixSet.
 *     </q><i><small><a href="https://docs.ogc.org/is/20-057/20-057.html#_15749363-4244-7867-f0da-e614f2d14dfb"> OGC API - Tiles - Part 1: Core - 9.2. Tilesets list</a></small></i>
 * </blockquote>
 *
 * @author Johann Sorel (Geomatys)
 */
public final class GetTileSetsList extends RequestParameters {

    private TilingType type = TilingType.VECTOR;
    private String collectionId;
    private String styleId;
    private String format;

    /**
     * @return the tiling type
     * @see #type(org.geotoolkit.ogcapi.request.tiles.TilingType)
     */
    public TilingType getType() {
        return type;
    }

    /**
     * @param type the type to set
     * @see #type(org.geotoolkit.ogcapi.request.tiles.TilingType)
     */
    public void setType(TilingType type) {
        this.type = type;
    }

    /**
     * <blockquote><q>
     *     Type of data represented in the tileset
     *     </q><i><small><a href="https://docs.ogc.org/is/20-057/20-057.html#_tilesets_list"> OGC API - Tiles - Part 1: Core - 9.2. Tilesets list (tileset schema, property dataType)</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional, default {@code VECTOR}.</li>
     *     <li>Spec-defined values of the underlying {@code dataType} property are {@code map},
     *     {@code vector} and {@code coverage}; not itself a request parameter, it only selects which
     *     tiling subpath ({@link TilingType#subpath}) this request targets.</li>
     * </ul>
     *
     * @param type the type to set
     */
    public GetTileSetsList type(TilingType type) {
        this.type = type;
        return this;
    }

    /**
     * @return the collectionId
     * @see #collectionId(java.lang.String)
     */
    public String getCollectionId() {
        return collectionId;
    }

    /**
     * @param collectionId the collectionId to set
     * @see #collectionId(java.lang.String)
     */
    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    /**
     * <blockquote><q>
     *     The geospatial data resource SHALL have an associated list of at least one tileset
     *     accessible at &#x2026;/tiles supporting an HTTP GET operation.
     *     </q><i><small><a href="https://docs.ogc.org/is/20-057/20-057.html#_be4d76b2-14f5-6e30-d824-e1eb09cefc8d"> OGC API - Tiles - Part 1: Core - 11.4. Geospatial data resources tilesets list</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     *     <li>Local identifier of the collection whose tilesets are listed.</li>
     * </ul>
     *
     * @param collectionId the collectionId to set
     */
    public GetTileSetsList collectionId(String collectionId) {
        setCollectionId(collectionId);
        return this;
    }

    /**
     * @return the style id
     * @see #styleId(java.lang.String)
     */
    public String getStyleId() {
        return styleId;
    }

    /**
     * @param styleId the style id to set
     * @see #styleId(java.lang.String)
     */
    public void setStyleId(String styleId) {
        this.styleId = styleId;
    }

    /**
     * An identifier representing a specific style.
     * <br>
     * <ul>
     *     <li>Optional; required if requesting the tileset list of a styled map or vector resource.</li>
     *     <li>Not formally defined as a request parameter by OGC API - Tiles - Part 1: Core; the
     *     Standard only shows a "Styled Map tileset list" example path of the form
     *     {@code .../styles/{styleId}/map/tiles} (Table 4/Table 5), with no verbatim parameter
     *     description available.</li>
     * </ul>
     *
     * @param styleId the style id to set
     */
    public GetTileSetsList styleId(String styleId) {
        setStyleId(styleId);
        return this;
    }

    /**
     * @return the format
     * @see #format(java.lang.String)
     */
    public String getFormat() {
        return format;
    }

    /**
     * @param format the format to set
     * @see #format(java.lang.String)
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * <blockquote><q>
     *     The optional f parameter indicates the output format that the server
     *     shall provide as part of the response document. The default format is JSON.
     *     </q><i><small><a href="https://docs.ogc.org/is/19-072/19-072.html#json-api-definition"> OGC API - Common - Part 1: Core</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>Accepted values are typically {@code json} or {@code html}.</li>
     *     <li>If no value is provided, the {@code Accept} header is used to determine the format.</li>
     * </ul>
     *
     * @param format the format to set
     */
    public GetTileSetsList format(String format) {
        setFormat(format);
        return this;
    }

}
