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
import org.opengis.geometry.Envelope;

/**
 * The {@code items} query ({@code /collections/{collectionId}/items}): a list of static data of
 * moving features.
 *
 * <p>OGC API - Moving Features, Part 1: Core ({@code retrieveMovingFeatures}).</p>
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class GetMovingFeatures extends RequestParameters {

    private String collectionId;
    private Envelope bbox;
    private String datetime;
    private Integer limit;
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
    public GetMovingFeatures collectionId(String collectionId) {
        setCollectionId(collectionId);
        return this;
    }

    /**
     * Only features that have a geometry that intersects the bounding box are selected.
     * (optional)
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
    public GetMovingFeatures bbox(Envelope bbox) {
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
    public GetMovingFeatures datetime(String datetime) {
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
    public GetMovingFeatures limit(Integer limit) {
        setLimit(limit);
        return this;
    }

    /**
     * If {@code true}, {@code datetime} must be a bounded interval and only a subsequence of each
     * matching feature's trajectory within that interval is returned. Must not be combined with
     * {@code leaf}. (optional)
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
    public GetMovingFeatures subtrajectory(Boolean subtrajectory) {
        setSubtrajectory(subtrajectory);
        return this;
    }

}
