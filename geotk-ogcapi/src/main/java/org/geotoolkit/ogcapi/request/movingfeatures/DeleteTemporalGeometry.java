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
 * <blockquote><q>For every temporal geometry in a moving feature (path
 * {root}/collections/{collectionId}/items/{mFeatureId}), the server SHALL support the HTTP
 * DELETE operation at the path
 * {root}/collections/{collectionId}/items/{mFeatureId}/tgsequence/{tGeometryId}</q>
 * <i><small><a href="https://docs.ogc.org/is/22-003r3/22-003r3.html">OGC API - Moving
 * Features - Part 1: Core - Requirement /req/movingfeatures/tpgeometry-delete</a></small></i></blockquote>
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class DeleteTemporalGeometry extends RequestParameters {

    private String collectionId;
    private String mFeatureId;
    private String tGeometryId;

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
    public DeleteTemporalGeometry collectionId(String collectionId) {
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
    public DeleteTemporalGeometry mFeatureId(String mFeatureId) {
        setMFeatureId(mFeatureId);
        return this;
    }

    /**
     * @return the tGeometryId
     * @see #tGeometryId(java.lang.String)
     */
    public String getTGeometryId() {
        return tGeometryId;
    }

    /**
     * @param tGeometryId the tGeometryId to set
     * @see #tGeometryId(java.lang.String)
     */
    public void setTGeometryId(String tGeometryId) {
        this.tGeometryId = tGeometryId;
    }

    /**
     * <blockquote><q>The path parameter tGeometryId is an id property of the temporal
     * geometry.</q><i><small><a href="https://docs.ogc.org/is/22-003r3/22-003r3.html">OGC API
     * - Moving Features - Part 1: Core - Requirement /req/movingfeatures/tpgeometry-delete</a>
     * </small></i></blockquote>
     * <br>
     * <ul>
     *     <li>Required. Corresponds to the {@code tGeometryId} path parameter.</li>
     * </ul>
     *
     * @param tGeometryId the tGeometryId to set
     * @return this
     */
    public DeleteTemporalGeometry tGeometryId(String tGeometryId) {
        setTGeometryId(tGeometryId);
        return this;
    }

}
