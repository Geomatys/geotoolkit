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
 *     Deleting a style also deletes any sub-resources associated with the style, for example
 *     a map for the style, if one was available.
 *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-styles/blob/master/standard/clause_8_manage_styles.adoc"> OGC API - Styles - Delete a style</a></small></i>
 * </blockquote>
 *
 * @author Johann Sorel (Geomatys)
 */
public final class DeleteStyle extends RequestParameters {

    private String collectionId;
    private String styleId;

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
     *     <li>Set to delete a style of a single collection ({@code /collections/{collectionId}/styles/{styleId}});
     *     leave {@code null} to delete a style of the whole dataset ({@code /styles/{styleId}}).</li>
     * </ul>
     */
    public DeleteStyle collectionId(String collectionId) {
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
     *     <li>Must be the {@code id} property of the style to delete.</li>
     * </ul>
     */
    public DeleteStyle styleId(String styleId) {
        setStyleId(styleId);
        return this;
    }

}
