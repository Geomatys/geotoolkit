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
package org.geotoolkit.ogcapi.request.styles;

import org.geotoolkit.ogcapi.request.RequestParameters;

/**
 * <blockquote><q>
 *     This operation returns a list of styles that are currently available for the base
 *     resource.
 *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-styles/blob/master/standard/clause_7_core.adoc"> OGC API - Styles - Fetch styles</a></small></i>
 * </blockquote>
 *
 * @author Johann Sorel (Geomatys)
 */
public final class GetStylesList extends RequestParameters {

    private String collectionId;
    private String format;

    /**
     * @see #collectionId(java.lang.String)
     */
    public String getCollectionId() {
        return collectionId;
    }

    /**
     * @see #collectionId(java.lang.String)
     */
    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    /**
     * <blockquote><q>
     *     local identifier of a collection
     *     </q><i><small><a href="https://schemas.opengis.net/ogcapi/features/part1/1.0/openapi/parameters/collectionId.yaml"> OGC API - Features - Part 1: Core - collectionId parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>Set to retrieve the styles of a single collection ({@code /collections/{collectionId}/styles});
     *     leave {@code null} to retrieve the styles of the whole dataset ({@code /styles}).</li>
     * </ul>
     */
    public GetStylesList collectionId(String collectionId) {
        setCollectionId(collectionId);
        return this;
    }

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
     *     The format of the response. If no value is provided, the accept header is used to
     *     determine the format. Accepted values are 'json' or 'html'.
     *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-styles/blob/master/standard/openapi/parameters/unspecified/f-metadata.yaml"> OGC API - Styles - f parameter (styles list format)</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>Allowed values: {@code json}, {@code html}.</li>
     * </ul>
     */
    public GetStylesList format(String format) {
        setFormat(format);
        return this;
    }

}
