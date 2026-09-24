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
 * The list of tile matrix sets the server offers ({@code /tileMatrixSets}).
 *
 * <blockquote><q>
 *     A tile matrix set is a tiling scheme that defines how a space is divided into individual
 *     tiles. Implementations SHALL support the retrieval of the list of tile matrix sets.
 *     </q><i><small><a href="https://docs.ogc.org/is/20-057/20-057.html#_tilesets"> OGC API - Tiles - Part 1: Core</a></small></i>
 * </blockquote>
 *
 * @author Johann Sorel (Geomatys)
 */
public final class GetTileMatrixSetsList extends RequestParameters {

    private String format;

    /**
     * @see #format(java.lang.String)
     */
    public String getFormat() {
        return format;
    }

    /**
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
     *     <li>Allowed values: {@code json}, {@code html}.</li>
     *     <li>Default: {@code json}.</li>
     * </ul>
     */
    public GetTileMatrixSetsList format(String format) {
        setFormat(format);
        return this;
    }
}
