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
 * To update the default style of a base resource's style collection, via a
 * {@code application/merge-patch+json} PATCH request (RFC 7396).
 *
 * @author Johann Sorel (Geomatys)
 */
public final class UpdateDefaultStyle extends RequestParameters {

    private String collectionId;
    private String defaultStyleId;

    /**
     * Local identifier of a collection. Set to update the default style of a single collection
     * ({@code /collections/{collectionId}/styles}). Leave {@code null} to update the default style of
     * the whole dataset ({@code /styles}). (optional)
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
    public UpdateDefaultStyle collectionId(String collectionId) {
        setCollectionId(collectionId);
        return this;
    }

    /**
     * The {@code id} of the style to set as the default. Set to {@code null} to clear the default
     * style (there will be no default style in the style collection).
     *
     * @return the defaultStyleId
     */
    public String getDefaultStyleId() {
        return defaultStyleId;
    }

    /**
     * @param defaultStyleId the defaultStyleId to set
     * @see #getDefaultStyleId()
     */
    public void setDefaultStyleId(String defaultStyleId) {
        this.defaultStyleId = defaultStyleId;
    }

    /**
     * @param defaultStyleId the defaultStyleId to set
     * @see #getDefaultStyleId()
     */
    public UpdateDefaultStyle defaultStyleId(String defaultStyleId) {
        setDefaultStyleId(defaultStyleId);
        return this;
    }

}
