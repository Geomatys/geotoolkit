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
 *     The PATCH operation on {baseResource}/styles updates the default style of the style
 *     collection.
 *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-styles/blob/master/standard/clause_8_manage_styles.adoc"> OGC API - Styles - Update the default style</a></small></i>
 * </blockquote>
 *
 * @author Johann Sorel (Geomatys)
 */
public final class UpdateDefaultStyle extends RequestParameters {

    private String collectionId;
    private String defaultStyleId;

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
     *     <li>Set to update the default style of a single collection ({@code /collections/{collectionId}/styles});
     *     leave {@code null} to update the default style of the whole dataset ({@code /styles}).</li>
     * </ul>
     */
    public UpdateDefaultStyle collectionId(String collectionId) {
        setCollectionId(collectionId);
        return this;
    }

    /**
     * @see #defaultStyleId(java.lang.String)
     */
    public String getDefaultStyleId() {
        return defaultStyleId;
    }

    /**
     * @see #defaultStyleId(java.lang.String)
     */
    public void setDefaultStyleId(String defaultStyleId) {
        this.defaultStyleId = defaultStyleId;
    }

    /**
     * <blockquote><q>
     *     A value null will remove the default member, i.e., there is no default style in
     *     the style collection. A value that is a valid style identifier in the collection
     *     will set that style as the default style.
     *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-styles/blob/master/standard/clause_8_manage_styles.adoc"> OGC API - Styles - Update the default style</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional; set to {@code null} to clear the default style (no default style in the collection).</li>
     *     <li>Otherwise must be the {@code id} of an existing style in the collection.</li>
     * </ul>
     */
    public UpdateDefaultStyle defaultStyleId(String defaultStyleId) {
        setDefaultStyleId(defaultStyleId);
        return this;
    }

}
