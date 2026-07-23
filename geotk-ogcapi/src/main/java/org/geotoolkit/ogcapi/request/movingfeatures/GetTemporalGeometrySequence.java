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
import org.opengis.geometry.Envelope;

/**
 * The {@code tgsequence} query ({@code /collections/{collectionId}/items/{mFeatureId}/tgsequence}):
 * the temporal geometry sequence of a moving feature.
 *
 * <p>OGC API - Moving Features, Part 1: Core ({@code retrieveTemporalGeometrySequence}).</p>
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class GetTemporalGeometrySequence extends RequestParameters {

    private String collectionId;
    private String mFeatureId;
    private Envelope bbox;
    private String datetime;
    private Integer limit;
    private List<String> leaf;
    private Boolean subtrajectory;

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
    public GetTemporalGeometrySequence collectionId(String collectionId) {
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
    public GetTemporalGeometrySequence mFeatureId(String mFeatureId) {
        setMFeatureId(mFeatureId);
        return this;
    }

    /**
     * Only geometries that intersect the bounding box are selected. (optional)
     *
     * @return the bbox
     */
    public Envelope getBbox() {
        return bbox;
    }

    /**
     * @param bbox the bbox to set
     * @see #getBbox()
     */
    public void setBbox(Envelope bbox) {
        this.bbox = bbox;
    }

    /**
     * @param bbox the bbox to set
     * @see #getBbox()
     */
    public GetTemporalGeometrySequence bbox(Envelope bbox) {
        setBbox(bbox);
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
    public GetTemporalGeometrySequence datetime(String datetime) {
        setDatetime(datetime);
        return this;
    }

    /**
     * Limits the number of items presented in the response. Minimum 1, maximum 10000, default
     * 10. (optional)
     *
     * @return the limit
     */
    public Integer getLimit() {
        return limit;
    }

    /**
     * @param limit the limit to set
     * @see #getLimit()
     */
    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    /**
     * @param limit the limit to set
     * @see #getLimit()
     */
    public GetTemporalGeometrySequence limit(Integer limit) {
        setLimit(limit);
        return this;
    }

    /**
     * A sequence of monotonic increasing date-time instants at which to interpolate the
     * trajectory (the {@code pointAtTime} operation). Must not be combined with
     * {@code subtrajectory}. (optional)
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
    public GetTemporalGeometrySequence leaf(List<String> leaf) {
        setLeaf(leaf);
        return this;
    }

    /**
     * If {@code true}, {@code datetime} must be a bounded interval and only the subsequence of
     * the trajectory within that interval is returned. Must not be combined with {@code leaf}.
     * (optional)
     *
     * @return the subtrajectory
     */
    public Boolean getSubtrajectory() {
        return subtrajectory;
    }

    /**
     * @param subtrajectory the subtrajectory to set
     * @see #getSubtrajectory()
     */
    public void setSubtrajectory(Boolean subtrajectory) {
        this.subtrajectory = subtrajectory;
    }

    /**
     * @param subtrajectory the subtrajectory to set
     * @see #getSubtrajectory()
     */
    public GetTemporalGeometrySequence subtrajectory(Boolean subtrajectory) {
        setSubtrajectory(subtrajectory);
        return this;
    }

}
