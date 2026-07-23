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
package org.geotoolkit.ogcapi.request.movingfeatures;

import org.geotoolkit.ogcapi.request.RequestParameters;

/**
 * To delete a single temporal primitive value from a named temporal property.
 *
 * <p>OGC API - Moving Features, Part 1: Core ({@code deleteTemporalPrimitiveValue}).</p>
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class DeleteTemporalPrimitiveValue extends RequestParameters {

    private String collectionId;
    private String mFeatureId;
    private String tPropertyName;
    private String tValueId;

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
    public DeleteTemporalPrimitiveValue collectionId(String collectionId) {
        setCollectionId(collectionId);
        return this;
    }

    /**
     * Local identifier of the moving feature. (required)
     *
     * @return the mFeatureId
     */
    public String getMFeatureId() {
        return mFeatureId;
    }

    /**
     * @param mFeatureId the mFeatureId to set
     * @see #getMFeatureId()
     */
    public void setMFeatureId(String mFeatureId) {
        this.mFeatureId = mFeatureId;
    }

    /**
     * @param mFeatureId the mFeatureId to set
     * @see #getMFeatureId()
     */
    public DeleteTemporalPrimitiveValue mFeatureId(String mFeatureId) {
        setMFeatureId(mFeatureId);
        return this;
    }

    /**
     * Local identifier of the temporal property. (required)
     *
     * @return the tPropertyName
     */
    public String getTPropertyName() {
        return tPropertyName;
    }

    /**
     * @param tPropertyName the tPropertyName to set
     * @see #getTPropertyName()
     */
    public void setTPropertyName(String tPropertyName) {
        this.tPropertyName = tPropertyName;
    }

    /**
     * @param tPropertyName the tPropertyName to set
     * @see #getTPropertyName()
     */
    public DeleteTemporalPrimitiveValue tPropertyName(String tPropertyName) {
        setTPropertyName(tPropertyName);
        return this;
    }

    /**
     * Local identifier of the temporal primitive value. (required)
     *
     * @return the tValueId
     */
    public String getTValueId() {
        return tValueId;
    }

    /**
     * @param tValueId the tValueId to set
     * @see #getTValueId()
     */
    public void setTValueId(String tValueId) {
        this.tValueId = tValueId;
    }

    /**
     * @param tValueId the tValueId to set
     * @see #getTValueId()
     */
    public DeleteTemporalPrimitiveValue tValueId(String tValueId) {
        setTValueId(tValueId);
        return this;
    }

}
