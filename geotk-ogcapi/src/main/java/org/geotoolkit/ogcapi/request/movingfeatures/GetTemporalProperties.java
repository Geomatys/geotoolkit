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
 * The {@code tproperties} query ({@code /collections/{collectionId}/items/{mFeatureId}/tproperties}):
 * the list of temporal properties of a moving feature.
 *
 * <p>OGC API - Moving Features, Part 1: Core ({@code retrieveTemporalProperties}).</p>
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class GetTemporalProperties extends RequestParameters {

    private String collectionId;
    private String mFeatureId;
    private String datetime;
    private Integer limit;
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
    public GetTemporalProperties collectionId(String collectionId) {
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
    public GetTemporalProperties mFeatureId(String mFeatureId) {
        setMFeatureId(mFeatureId);
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
    public GetTemporalProperties datetime(String datetime) {
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
    public GetTemporalProperties limit(Integer limit) {
        setLimit(limit);
        return this;
    }

    /**
     * If {@code true}, {@code datetime} must be a bounded interval and only the subsequence of
     * each temporal property's values within that interval is returned. Must not be combined
     * with {@code leaf}. (optional)
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
    public GetTemporalProperties subtemporalvalue(Boolean subtemporalvalue) {
        setSubtemporalvalue(subtemporalvalue);
        return this;
    }

}
