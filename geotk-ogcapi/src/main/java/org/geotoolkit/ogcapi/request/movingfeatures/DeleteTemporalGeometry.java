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
 * To delete a temporal primitive geometry from a moving feature's trajectory.
 *
 * <p>OGC API - Moving Features, Part 1: Core ({@code deleteTemporalPrimitiveGeometry}).</p>
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class DeleteTemporalGeometry extends RequestParameters {

    private String collectionId;
    private String mFeatureId;
    private String tGeometryId;

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
    public DeleteTemporalGeometry collectionId(String collectionId) {
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
    public DeleteTemporalGeometry mFeatureId(String mFeatureId) {
        setMFeatureId(mFeatureId);
        return this;
    }

    /**
     * Local identifier of the temporal primitive geometry. (required)
     *
     * @return the tGeometryId
     */
    public String getTGeometryId() {
        return tGeometryId;
    }

    /**
     * @param tGeometryId the tGeometryId to set
     * @see #getTGeometryId()
     */
    public void setTGeometryId(String tGeometryId) {
        this.tGeometryId = tGeometryId;
    }

    /**
     * @param tGeometryId the tGeometryId to set
     * @see #getTGeometryId()
     */
    public DeleteTemporalGeometry tGeometryId(String tGeometryId) {
        setTGeometryId(tGeometryId);
        return this;
    }

}
