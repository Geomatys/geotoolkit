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
 * <blockquote><q>The delete operation is defined in the DELETE section of the
 * "Create/Replace/Delete" requirements class of OGC API — Features.</q>
 * <i><small><a href="https://docs.ogc.org/is/22-003r3/22-003r3.html">OGC API - Moving
 * Features - Part 1: Core - 8.10.2.1. Delete</a></small></i></blockquote>
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class DeleteTemporalPrimitiveValue extends RequestParameters {

    private String collectionId;
    private String mFeatureId;
    private String tPropertyName;
    private String tValueId;

    /**
     * @return the collectionId
     * @see #collectionId(java.lang.String)
     */
    public String getCollectionId() {
        return collectionId;
    }

    /**
     * @param collectionId the collectionId to set
     * @see #collectionId(java.lang.String)
     */
    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    /**
     * Local identifier of the collection to which the moving feature belongs.
     *
     * <ul>
     *     <li>Required. Corresponds to the {@code collectionId} path parameter.</li>
     * </ul>
     *
     * @param collectionId the collectionId to set
     * @return this
     */
    public DeleteTemporalPrimitiveValue collectionId(String collectionId) {
        setCollectionId(collectionId);
        return this;
    }

    /**
     * @return the mFeatureId
     * @see #mFeatureId(java.lang.String)
     */
    public String getMFeatureId() {
        return mFeatureId;
    }

    /**
     * @param mFeatureId the mFeatureId to set
     * @see #mFeatureId(java.lang.String)
     */
    public void setMFeatureId(String mFeatureId) {
        this.mFeatureId = mFeatureId;
    }

    /**
     * Local identifier of the moving feature.
     *
     * <ul>
     *     <li>Required. Corresponds to the {@code mFeatureId} path parameter.</li>
     * </ul>
     *
     * @param mFeatureId the mFeatureId to set
     * @return this
     */
    public DeleteTemporalPrimitiveValue mFeatureId(String mFeatureId) {
        setMFeatureId(mFeatureId);
        return this;
    }

    /**
     * @return the tPropertyName
     * @see #tPropertyName(java.lang.String)
     */
    public String getTPropertyName() {
        return tPropertyName;
    }

    /**
     * @param tPropertyName the tPropertyName to set
     * @see #tPropertyName(java.lang.String)
     */
    public void setTPropertyName(String tPropertyName) {
        this.tPropertyName = tPropertyName;
    }

    /**
     * Local identifier of the temporal property that owns the value to delete.
     *
     * <ul>
     *     <li>Required. Corresponds to the {@code tPropertyName} path parameter.</li>
     * </ul>
     *
     * @param tPropertyName the tPropertyName to set
     * @return this
     */
    public DeleteTemporalPrimitiveValue tPropertyName(String tPropertyName) {
        setTPropertyName(tPropertyName);
        return this;
    }

    /**
     * @return the tValueId
     * @see #tValueId(java.lang.String)
     */
    public String getTValueId() {
        return tValueId;
    }

    /**
     * @param tValueId the tValueId to set
     * @see #tValueId(java.lang.String)
     */
    public void setTValueId(String tValueId) {
        this.tValueId = tValueId;
    }

    /**
     * <blockquote><q>The {tValueId} parameter is the unique identifier for a single temporal
     * primitive value object offered by the API. The list of valid values for {tValueId} is
     * provided in the
     * {root}/collections/{collectionId}/items/{mFeatureId}/tproperties/{tPropertyName} GET
     * response.</q><i><small><a href="https://docs.ogc.org/is/22-003r3/22-003r3.html#_operation_9">OGC
     * API - Moving Features - Part 1: Core - 8.10.2.1. Delete</a></small></i></blockquote>
     * <br>
     * <ul>
     *     <li>Required. Corresponds to the {@code tValueId} path parameter.</li>
     * </ul>
     *
     * @param tValueId the tValueId to set
     * @return this
     */
    public DeleteTemporalPrimitiveValue tValueId(String tValueId) {
        setTValueId(tValueId);
        return this;
    }

}
