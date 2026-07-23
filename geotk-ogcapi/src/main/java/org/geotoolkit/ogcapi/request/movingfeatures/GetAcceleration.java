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

import java.util.List;
import org.geotoolkit.ogcapi.request.RequestParameters;

/**
 * The {@code acceleration} query: the time-to-acceleration curve of a temporal primitive geometry.
 *
 * <p>OGC API - Moving Features, Part 1: Core ({@code getAccelerationOfTemporalPrimitiveGeometry}).</p>
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class GetAcceleration extends RequestParameters {

    private String collectionId;
    private String mFeatureId;
    private String tGeometryId;
    private String datetime;
    private List<String> leaf;
    private Boolean subtemporalvalue;

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
    public GetAcceleration collectionId(String collectionId) {
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
    public GetAcceleration mFeatureId(String mFeatureId) {
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
    public GetAcceleration tGeometryId(String tGeometryId) {
        setTGeometryId(tGeometryId);
        return this;
    }

    /**
     * A date-time or a (possibly half-bounded) interval, per RFC 3339. (required)
     *
     * @return the datetime
     */
    public String getDatetime() {
        return datetime;
    }

    /**
     * @param datetime the datetime to set
     * @see #getDatetime()
     */
    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    /**
     * @param datetime the datetime to set
     * @see #getDatetime()
     */
    public GetAcceleration datetime(String datetime) {
        setDatetime(datetime);
        return this;
    }

    /**
     * A sequence of monotonic increasing date-time instants at which to interpolate the
     * time-to-acceleration curve (the {@code pointAtTime} operation). Must not be combined with
     * {@code subtemporalvalue}. (optional)
     *
     * @return the leaf
     */
    public List<String> getLeaf() {
        return leaf;
    }

    /**
     * @param leaf the leaf to set
     * @see #getLeaf()
     */
    public void setLeaf(List<String> leaf) {
        this.leaf = leaf;
    }

    /**
     * @param leaf the leaf to set
     * @see #getLeaf()
     */
    public GetAcceleration leaf(List<String> leaf) {
        setLeaf(leaf);
        return this;
    }

    /**
     * If {@code true}, {@code datetime} must be a bounded interval and only the subsequence of
     * the time-to-acceleration curve within that interval is returned. Must not be combined with
     * {@code leaf}. (optional)
     *
     * @return the subtemporalvalue
     */
    public Boolean getSubtemporalvalue() {
        return subtemporalvalue;
    }

    /**
     * @param subtemporalvalue the subtemporalvalue to set
     * @see #getSubtemporalvalue()
     */
    public void setSubtemporalvalue(Boolean subtemporalvalue) {
        this.subtemporalvalue = subtemporalvalue;
    }

    /**
     * @param subtemporalvalue the subtemporalvalue to set
     * @see #getSubtemporalvalue()
     */
    public GetAcceleration subtemporalvalue(Boolean subtemporalvalue) {
        setSubtemporalvalue(subtemporalvalue);
        return this;
    }

}
