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
 * To delete a style. Deleting a style also deletes any sub-resources associated with the style.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class DeleteStyle extends RequestParameters {

    private String collectionId;
    private String styleId;

    /**
     * Local identifier of a collection. Set to delete a style of a single collection
     * ({@code /collections/{collectionId}/styles/{styleId}}). Leave {@code null} to delete a style of
     * the whole dataset ({@code /styles/{styleId}}). (optional)
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
    public DeleteStyle collectionId(String collectionId) {
        setCollectionId(collectionId);
        return this;
    }

    /**
     * The {@code id} property of the style to delete. (required)
     *
     * @return the styleId
     */
    public String getStyleId() {
        return styleId;
    }

    /**
     * @param styleId the styleId to set
     * @see #getStyleId()
     */
    public void setStyleId(String styleId) {
        this.styleId = styleId;
    }

    /**
     * @param styleId the styleId to set
     * @see #getStyleId()
     */
    public DeleteStyle styleId(String styleId) {
        setStyleId(styleId);
        return this;
    }

}
