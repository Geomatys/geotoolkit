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
 *     This operation returns the stylesheet of a style.
 *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-styles/blob/master/standard/clause_7_core.adoc"> OGC API - Styles - Fetch style</a></small></i>
 * </blockquote>
 *
 * @author Johann Sorel (Geomatys)
 */
public final class GetStyle extends RequestParameters {

    private String collectionId;
    private String styleId;
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
     *     <li>Set to retrieve a style of a single collection ({@code /collections/{collectionId}/styles/{styleId}});
     *     leave {@code null} to retrieve a style of the whole dataset ({@code /styles/{styleId}}).</li>
     * </ul>
     */
    public GetStyle collectionId(String collectionId) {
        setCollectionId(collectionId);
        return this;
    }

    /**
     * @see #styleId(java.lang.String)
     */
    public String getStyleId() {
        return styleId;
    }

    /**
     * @see #styleId(java.lang.String)
     */
    public void setStyleId(String styleId) {
        this.styleId = styleId;
    }

    /**
     * <blockquote><q>
     *     The parameter styleId SHALL be the id property of a style obtained by previously
     *     having queried the Styles resource (i.e., responses to GET requests to {baseResource}/styles).
     *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-styles/blob/master/standard/clause_8_manage_styles.adoc"> OGC API - Styles - Style resource endpoints</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     * </ul>
     */
    public GetStyle styleId(String styleId) {
        setStyleId(styleId);
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
     *     The format of the style response (e.g. CartoSym-CSS). Accepted values are 'sld',
     *     'mbgl', 'cscss' or 'csjson'
     *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-styles/blob/master/standard/openapi/parameters/unspecified/f-style.yaml"> OGC API - Styles - f parameter (style encoding)</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional; if not provided, the accept header is used to determine the encoding.</li>
     *     <li>Allowed values: {@code sld}, {@code mbgl}, {@code cscss}, {@code csjson}.</li>
     * </ul>
     */
    public GetStyle format(String format) {
        setFormat(format);
        return this;
    }

}
