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
 * To delete a named temporal property and all its values from a moving feature.
 *
 * <blockquote><q>A delete operation deletes an existing TemporalProperty resource that is
 * specified by {tPropertyName}.</q>
 * <i><small><a href="https://docs.ogc.org/is/22-003r3/22-003r3.html">OGC API - Moving
 * Features - Part 1: Core - 8.9.1. Overview</a></small></i></blockquote>
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class DeleteTemporalProperty extends RequestParameters {

    private String collectionId;
    private String mFeatureId;
    private String tPropertyName;

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
    public DeleteTemporalProperty collectionId(String collectionId) {
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
    public DeleteTemporalProperty mFeatureId(String mFeatureId) {
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
     * <blockquote><q>tPropertyName is a local identifier of the temporal property.</q>
     * <i><small><a href="https://docs.ogc.org/is/22-003r3/22-003r3.html">OGC API - Moving
     * Features - Part 1: Core - 8.9.3.1. Retrieve</a></small></i></blockquote>
     * <br>
     * <ul>
     *     <li>Required. Corresponds to the {@code tPropertyName} path parameter, and identifies
     *     the TemporalProperty (and all its values) to be deleted.</li>
     * </ul>
     *
     * @param tPropertyName the tPropertyName to set
     * @return this
     */
    public DeleteTemporalProperty tPropertyName(String tPropertyName) {
        setTPropertyName(tPropertyName);
        return this;
    }

}
