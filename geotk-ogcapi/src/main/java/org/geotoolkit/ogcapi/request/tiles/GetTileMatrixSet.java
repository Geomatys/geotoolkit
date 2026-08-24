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
 * To retrieve the definition of the specified tiling scheme (tile matrix set).
 *
 * <blockquote><q>
 *     The target IRI points to a resource that describes the TileMatrixSet according to the
 *     2D-TMS standard.
 *     </q><i><small><a href="https://docs.ogc.org/is/20-057/20-057.html#_1f2e99f5-bd84-07ad-69f0-29f8b3d2d258"> OGC API - Tiles - Part 1: Core - 5.2. Link relations (tiling-scheme)</a></small></i>
 * </blockquote>
 *
 * @author Johann Sorel (Geomatys)
 */
public final class GetTileMatrixSet extends RequestParameters {

    private String tileMatrixSetId;
    private String format;

    /**
     * @return tileMatrixSetId
     * @see #tileMatrixSetId(java.lang.String)
     */
    public String getTileMatrixSetId() {
        return tileMatrixSetId;
    }

    /**
     * @param tileMatrixSetId the tileMatrixSetId to set
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
     * @param tileMatrixSetId the tileMatrixSetId to set
     */
    public GetTileMatrixSet tileMatrixSetId(String tileMatrixSetId) {
        setTileMatrixSetId(tileMatrixSetId);
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
    public GetTileMatrixSet format(String format) {
        setFormat(format);
        return this;
    }

}
