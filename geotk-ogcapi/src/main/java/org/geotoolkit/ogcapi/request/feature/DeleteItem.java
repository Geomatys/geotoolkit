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
package org.geotoolkit.ogcapi.request.feature;

import org.geotoolkit.ogcapi.request.RequestParameters;

/**
 * To delete an existing feature.
 *
 * <blockquote><q>
 *     For every resource in the collection, the server SHALL support the HTTP DELETE
 *     operation.
 *     </q><i><small><a href="https://docs.ogc.org/DRAFTS/20-002r1.html#delete"> OGC API - Features - Part 4: Create, Replace, Update and Delete (draft) - Removing a resource from a collection (DELETE)</a></small></i>
 * </blockquote>
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class DeleteItem extends RequestParameters {

    private String collectionId;
    private String itemId;

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
     *     <li>Required.</li>
     *     <li>Must match the {@code id} of an existing collection (see {@code GET /collections}).</li>
     * </ul>
     */
    public DeleteItem collectionId(String collectionId) {
        setCollectionId(collectionId);
        return this;
    }

    /**
     * @see #itemId(java.lang.String)
     */
    public String getItemId() {
        return itemId;
    }

    /**
     * @see #itemId(java.lang.String)
     */
    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    /**
     * <blockquote><q>
     *     local identifier of a feature
     *     </q><i><small><a href="https://schemas.opengis.net/ogcapi/features/part1/1.0/openapi/parameters/featureId.yaml"> OGC API - Features - Part 1: Core - featureId parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     *     <li>Local identifier of the feature to delete.</li>
     * </ul>
     */
    public DeleteItem itemId(String itemId) {
        setItemId(itemId);
        return this;
    }

}
