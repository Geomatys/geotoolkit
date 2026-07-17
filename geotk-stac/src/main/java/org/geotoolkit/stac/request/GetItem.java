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
package org.geotoolkit.stac.request;

import org.geotoolkit.ogcapi.request.RequestParameters;

/**
 * Request parameters for GET /collections/{collectionId}/items/{featureId}.
 *
 * <p>Retrieves a single STAC Item identified by its collection and feature ID.</p>
 *
 * @author Quentin BIALOTA (Geomatys)
 * Based on: <a href="https://api.stacspec.org/v1.0.0/ogcapi-features/#operation/getFeature">STAC Doc</a>
 */
public class GetItem extends RequestParameters {

    private String collectionId;
    private String featureId;

    /**
     * Unique identifier of the collection the item belongs to. (required)
     */
    public String getCollectionId() {
        return collectionId;
    }

    /**
     * @see #getCollectionId()
     */
    public GetItem collectionId(String collectionId) {
        this.collectionId = collectionId;
        return this;
    }

    /** @see #getCollectionId() */
    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    /**
     * Unique identifier of the item (GeoJSON Feature ID). (required)
     */
    public String getFeatureId() {
        return featureId;
    }

    /**
     * @see #getFeatureId()
     */
    public GetItem featureId(String featureId) {
        this.featureId = featureId;
        return this;
    }

    /** @see #getFeatureId() */
    public void setFeatureId(String featureId) {
        this.featureId = featureId;
    }
}
