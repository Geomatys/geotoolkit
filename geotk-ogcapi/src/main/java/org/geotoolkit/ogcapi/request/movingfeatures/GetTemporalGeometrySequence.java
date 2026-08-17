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
 * The {@code tgsequence} query: retrieves the sequence of temporal primitive geometries of a
 * moving feature.
 *
 * <blockquote><q>A retrieve operation returns a sequence of TemporalPrimitiveGeometry object
 * which is included in the MovingFeature that is specified by {mFeatureId}.</q>
 * <i><small><a
 * href="https://docs.ogc.org/is/22-003r3/22-003r3.html#resource-temporalGeometrySequence-section">OGC
 * API - Moving Features - Part 1: Core - 8.5. Resource TemporalGeometrySequence</a></small></i></blockquote>
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
     *     <li>Required. Corresponds to the {@code collectionId} path parameter in
     *     {@code /collections/{collectionId}/items/{mFeatureId}/tgsequence}.</li>
     * </ul>
     *
     * @param collectionId the collectionId to set
     * @return this
     */
    public GetTemporalGeometrySequence collectionId(String collectionId) {
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
     * Local identifier of the moving feature whose temporal geometry sequence is retrieved.
     *
     * <ul>
     *     <li>Required. Corresponds to the {@code mFeatureId} path parameter.</li>
     * </ul>
     *
     * @param mFeatureId the mFeatureId to set
     * @return this
     */
    public GetTemporalGeometrySequence mFeatureId(String mFeatureId) {
        setMFeatureId(mFeatureId);
        return this;
    }

    /**
     * @return the bbox
     * @see #bbox(org.opengis.geometry.Envelope)
     */
    public Envelope getBbox() {
        return bbox;
    }

    /**
     * @param bbox the bbox to set
     * @see #bbox(org.opengis.geometry.Envelope)
     */
    public void setBbox(Envelope bbox) {
        this.bbox = bbox;
    }

    /**
     * <blockquote><q>The sequence of TemporalPrimitiveGeometry object returned to the response
     * can be limited using the query parameters limit, bbox, datetime, and leaf (or
     * subTrajectory).</q><i><small><a
     * href="https://docs.ogc.org/is/22-003r3/22-003r3.html#resource-temporalGeometrySequence-section">OGC
     * API - Moving Features - Part 1: Core - 8.5.1. Overview</a></small></i></blockquote>
     * <br>
     * <ul>
     *     <li>Optional. Only geometries that intersect the bounding box are selected.</li>
     * </ul>
     *
     * @param bbox the bbox to set
     * @return this
     */
    public GetTemporalGeometrySequence bbox(Envelope bbox) {
        setBbox(bbox);
        return this;
    }

    /**
     * @return the datetime
     * @see #datetime(java.lang.String)
     */
    public String getDatetime() {
        return datetime;
    }

    /**
     * @param datetime the datetime to set
     * @see #datetime(java.lang.String)
     */
    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    /**
     * A date-time or a (possibly half-bounded) interval, per RFC 3339, restricting the returned
     * sequence.
     *
     * <ul>
     *     <li>Required.</li>
     *     <li>No verbatim quotable sentence isolating this parameter alone was found in the
     *     spec; wording follows the RFC 3339 {@code datetime} convention used across the OGC
     *     API family (see the shared quote on {@link #bbox(org.opengis.geometry.Envelope)}
     *     for the enclosing clause).</li>
     * </ul>
     *
     * @param datetime the datetime to set
     * @return this
     */
    public GetTemporalGeometrySequence datetime(String datetime) {
        setDatetime(datetime);
        return this;
    }

    /**
     * @return the limit
     * @see #limit(java.lang.Integer)
     */
    public Integer getLimit() {
        return limit;
    }

    /**
     * @param limit the limit to set
     * @see #limit(java.lang.Integer)
     */
    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    /**
     * Limits the number of items presented in the response (see the shared quote on
     * {@link #bbox(org.opengis.geometry.Envelope)} for the clause defining this parameter).
     *
     * <ul>
     *     <li>Optional. Minimum 1, maximum 10000, default 10.</li>
     * </ul>
     *
     * @param limit the limit to set
     * @return this
     */
    public GetTemporalGeometrySequence limit(Integer limit) {
        setLimit(limit);
        return this;
    }

    /**
     * @return the leaf
     * @see #leaf(java.util.List)
     */
    public List<String> getLeaf() {
        return leaf;
    }

    /**
     * @param leaf the leaf to set
     * @see #leaf(java.util.List)
     */
    public void setLeaf(List<String> leaf) {
        this.leaf = leaf;
    }

    /**
     * <blockquote><q>geometry at a particular value of the parameter [source: ISO
     * 19141]</q><i><small><a href="https://docs.ogc.org/is/22-003r3/22-003r3.html">OGC API -
     * Moving Features - Part 1: Core - 4.12. leaf</a></small></i></blockquote>
     * <br>
     * <ul>
     *     <li>Optional. A sequence of monotonic increasing date-time instants at which to
     *     interpolate the trajectory.</li>
     *     <li>Must not be combined with {@code subtrajectory}.</li>
     * </ul>
     *
     * @param leaf the leaf to set
     * @return this
     */
    public GetTemporalGeometrySequence leaf(List<String> leaf) {
        setLeaf(leaf);
        return this;
    }

    /**
     * @return the subtrajectory
     * @see #subtrajectory(java.lang.Boolean)
     */
    public Boolean getSubtrajectory() {
        return subtrajectory;
    }

    /**
     * @param subtrajectory the subtrajectory to set
     * @see #subtrajectory(java.lang.Boolean)
     */
    public void setSubtrajectory(Boolean subtrajectory) {
        this.subtrajectory = subtrajectory;
    }

    /**
     * If {@code true}, {@code datetime} must be a bounded interval and only the subsequence of
     * the trajectory within that interval is returned (see the shared quote on
     * {@link #bbox(org.opengis.geometry.Envelope)} for the clause defining this parameter).
     *
     * <ul>
     *     <li>Optional. Must not be combined with {@code leaf}.</li>
     * </ul>
     *
     * @param subtrajectory the subtrajectory to set
     * @return this
     */
    public GetTemporalGeometrySequence subtrajectory(Boolean subtrajectory) {
        setSubtrajectory(subtrajectory);
        return this;
    }

}
