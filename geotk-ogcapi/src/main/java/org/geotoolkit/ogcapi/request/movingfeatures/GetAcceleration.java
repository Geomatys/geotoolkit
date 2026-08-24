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
 * <blockquote><q>The Acceleration query returns a time-to-acceleration curve of the
 * TemporalPrimitiveGeometry object as a form of the TemporalProperty.</q>
 * <i><small><a href="https://docs.ogc.org/is/22-003r3/22-003r3.html">OGC API - Moving
 * Features - Part 1: Core - 8.7.5. Acceleration Query</a></small></i></blockquote>
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
     *     {@code /collections/{collectionId}/items/{mFeatureId}/tgsequence/{tGeometryId}/acceleration}.</li>
     * </ul>
     *
     * @param collectionId the collectionId to set
     * @return this
     */
    public GetAcceleration collectionId(String collectionId) {
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
    public GetAcceleration mFeatureId(String mFeatureId) {
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
     * Local identifier of the temporal primitive geometry on which the acceleration curve is
     * computed.
     *
     * <ul>
     *     <li>Required. Corresponds to the {@code tGeometryId} path parameter.</li>
     * </ul>
     *
     * @param tGeometryId the tGeometryId to set
     * @return this
     */
    public GetAcceleration tGeometryId(String tGeometryId) {
        setTGeometryId(tGeometryId);
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
     * A date-time or a (possibly half-bounded) interval, per RFC 3339, restricting the part of
     * the acceleration curve to be returned.
     *
     * <ul>
     *     <li>Required.</li>
     *     <li>No verbatim quotable sentence isolating this parameter alone was found in the
     *     shared query-parameters clause (8.7.2) of the spec; wording follows the RFC 3339
     *     {@code datetime} convention used across the OGC API family.</li>
     * </ul>
     *
     * @param datetime the datetime to set
     * @return this
     */
    public GetAcceleration datetime(String datetime) {
        setDatetime(datetime);
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
     *     interpolate the time-to-acceleration curve.</li>
     *     <li>Must not be combined with {@code subtemporalvalue}.</li>
     * </ul>
     *
     * @param leaf the leaf to set
     * @return this
     */
    public GetAcceleration leaf(List<String> leaf) {
        setLeaf(leaf);
        return this;
    }

    /**
     * @return the subtemporalvalue
     * @see #subtemporalvalue(java.lang.Boolean)
     */
    public Boolean getSubtemporalvalue() {
        return subtemporalvalue;
    }

    /**
     * @param subtemporalvalue the subtemporalvalue to set
     * @see #subtemporalvalue(java.lang.Boolean)
     */
    public void setSubtemporalvalue(Boolean subtemporalvalue) {
        this.subtemporalvalue = subtemporalvalue;
    }

    /**
     * <blockquote><q>The subTemporalValue query parameter is used to select a subset of
     * TemporalProperty for the specified time interval.</q><i><small><a
     * href="https://docs.ogc.org/is/22-003r3/22-003r3.html">OGC API - Moving Features - Part 1:
     * Core - 8.9.2. Query Parameters</a></small></i></blockquote>
     * <br>
     * <ul>
     *     <li>Optional. If {@code true}, {@code datetime} must be a bounded interval and only
     *     the subsequence of the time-to-acceleration curve within that interval is
     *     returned.</li>
     *     <li>Must not be combined with {@code leaf}.</li>
     * </ul>
     *
     * @param subtemporalvalue the subtemporalvalue to set
     * @return this
     */
    public GetAcceleration subtemporalvalue(Boolean subtemporalvalue) {
        setSubtemporalvalue(subtemporalvalue);
        return this;
    }

}
