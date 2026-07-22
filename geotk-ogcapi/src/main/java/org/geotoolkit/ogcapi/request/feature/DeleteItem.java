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
 * <p>OGC API - Features Part 4: Create, Replace, Update and Delete.</p>
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class DeleteItem extends RequestParameters {

    private String collectionId;
    private String itemId;

    /**
     * Local identifier of the collection. (required)
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
    public DeleteItem collectionId(String collectionId) {
        setCollectionId(collectionId);
        return this;
    }

    /**
     * Local identifier of the feature to delete. (required)
     *
     * @return the itemId
     */
    public String getItemId() {
        return itemId;
    }

    /**
     * @param itemId the itemId to set
     * @see #getItemId()
     */
    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    /**
     * @param itemId the itemId to set
     * @see #getItemId()
     */
    public DeleteItem itemId(String itemId) {
        setItemId(itemId);
        return this;
    }

}
