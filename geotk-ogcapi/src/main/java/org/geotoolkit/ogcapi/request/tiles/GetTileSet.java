/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
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
import java.util.List;

/**
 * Parameters for {@code GET /collections/{collectionId}/tiles/{tileMatrixSetId}}: the tileset
 * resource, i.e. the metadata needed to request tiles from a geospatial data resource for a
 * given tile matrix set.
 *
 * <blockquote><q>
 *     A tileset consists of a set of tiles obtained by partitioning geospatial data according to
 *     a particular TileMatrixSet. The tileset metadata contains all the information necessary for
 *     a client application to request tiles from the tileset.
 *     </q><i><small><a href="https://docs.ogc.org/is/20-057/20-057.html#_17867384-536f-105e-1ca2-52c55204c66d"> OGC API - Tiles - Part 1: Core - 8.2. Tileset resource</a></small></i>
 * </blockquote>
 *
 * @author Johann Sorel (Geomatys)
 */
public final class GetTileSet extends RequestParameters {

    private TilingType type = TilingType.VECTOR;
    private String collectionId;
    private String tileMatrixSetId;
    private String styleId;
    private String format;
    private List<String> collections;

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
    public GetTileSet type(TilingType type) {
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
     *     For example, an implementation of the OGC API &#x2014; Features &#x2014; Part 1: Core Standard
     *     returns a list of links that include geospatial resource aspects for each geospatial data
     *     resource in the /collections/{collectionId} path.
     *     </q><i><small><a href="https://docs.ogc.org/is/20-057/20-057.html#_e6b4e66c-6d47-27fa-704a-2566b384e8ac"> OGC API - Tiles - Part 1: Core - 11.3. Geospatial data resources (NOTE)</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required if requesting the tileset of a specific collection.</li>
     *     <li>Local identifier of the collection.</li>
     * </ul>
     *
     * @param collectionId the collectionId to set
     */
    public GetTileSet collectionId(String collectionId) {
        setCollectionId(collectionId);
        return this;
    }

    /**
     * @return the TileMatrixSet id
     * @see #tileMatrixSetId(java.lang.String)
     */
    public String getTileMatrixSetId() {
        return tileMatrixSetId;
    }

    /**
     * @param tileMatrixSetId the TileMatrixSet id to set
     * @see #tileMatrixSetId(java.lang.String)
     */
    public void setTileMatrixSetId(String tileMatrixSetId) {
        this.tileMatrixSetId = tileMatrixSetId;
    }

    /**
     * <blockquote><q>
     *     The {@code tileMatrixSetId} variable will be interpreted as one of TileMatrixSet
     *     identifiers supported by the resource. In other words, tileMatrixSetId represents all
     *     the TileMatrixSets supported by the resource.
     *     </q><i><small><a href="https://docs.ogc.org/is/20-057/20-057.html#_7f66635d-a86f-e3ed-427f-494127984475"> OGC API - Tiles - Part 1: Core - 7.1.5. Parameter tileMatrixSetId (optional)</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     * </ul>
     *
     * @param tileMatrixSetId the TileMatrixSet id to set
     */
    public GetTileSet tileMatrixSetId(String tileMatrixSetId) {
        setTileMatrixSetId(tileMatrixSetId);
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
     *     <li>Optional; required if requesting the tileset of a styled map or vector resource.</li>
     *     <li>Not formally defined as a request parameter by OGC API - Tiles - Part 1: Core; the
     *     Standard only shows a "Styled Map tileset list" example path of the form
     *     {@code .../styles/{styleId}/map/tiles} (Table 4/Table 5), with no verbatim parameter
     *     description available.</li>
     * </ul>
     *
     * @param styleId the style id to set
     */
    public GetTileSet styleId(String styleId) {
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
    public GetTileSet format(String format) {
        setFormat(format);
        return this;
    }

    /**
     * @return the collections
     * @see #collections(java.util.List)
     */
    public List<String> getCollections() {
        return collections;
    }

    /**
     * @param collections the collections to set
     * @see #collections(java.util.List)
     */
    public void setCollections(List<String> collections) {
        this.collections = collections;
    }

    /**
     * <blockquote><q>
     *     Only the collections of geospatial data enumerated in the values of the collections
     *     parameter SHALL be used to generate the responses for the resources (tiles and tilesets)
     *     to which they apply.
     *     </q><i><small><a href="https://docs.ogc.org/is/20-057/20-057.html#_feeeca6c-3a49-084f-d02b-377494bc4189"> OGC API - Tiles - Part 1: Core - 12.2. Operation</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional; only applies to a tileset consisting of multiple geospatial data
     *     sub-resources (e.g. a dataset-wide tileset).</li>
     *     <li>Comma-separated list of collection identifiers, or full URLs to local geospatial
     *     resources.</li>
     *     <li>Rendered starting with the first (bottom) and ending with the last (topmost), unless
     *     overridden by styling rules.</li>
     * </ul>
     *
     * @param collections the collections to set
     */
    public GetTileSet collections(List<String> collections) {
        setCollections(collections);
        return this;
    }

}
