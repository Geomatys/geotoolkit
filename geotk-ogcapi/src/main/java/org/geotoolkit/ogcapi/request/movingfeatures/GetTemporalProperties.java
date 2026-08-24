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
 * The {@code tproperties} query:
 * {@code GET /collections/{collectionId}/items/{mFeatureId}/tproperties}, the list of
 * temporal properties of a moving feature.
 *
 * <blockquote><q>
 *     For every moving feature identified in the MovingFeatures(Clause 8.3) GET response
 *     (path {root}/collections/{collectionId}/items), the server SHALL support the HTTP
 *     GET operation at the path {root}/collections/{collectionId}/items/{mFeatureId}/tproperties
 *     </q><i><small><a href="https://docs.ogc.org/is/22-003r3/22-003r3.html#req_mf-tproperties-op-get"> OGC API - Moving Features, Part 1: Core - Requirement 36 /req/movingfeatures/tproperties-get</a></small></i>
 * </blockquote>
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
     * @see #collectionId(java.lang.String)
     */
    public String getCollectionId() {
        return collectionId;
    }

    /**
     * @see #collectionId(java.lang.String)
     */
    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    /**
     * <blockquote><q>
     *     An identifier for a specific Collection of data
     *     </q><i><small><a href="https://docs.ogc.org/is/22-003r3/22-003r3.html#_6a53f5bb-7088-4fef-b4c3-ab65f8d40189"> OGC API - Moving Features, Part 1: Core - 6.1. General</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     * </ul>
     */
    public GetTemporalProperties collectionId(String collectionId) {
        setCollectionId(collectionId);
        return this;
    }

    /**
     * @see #mFeatureId(java.lang.String)
     */
    public String getMFeatureId() {
        return mFeatureId;
    }

    /**
     * @see #mFeatureId(java.lang.String)
     */
    public void setMFeatureId(String mFeatureId) {
        this.mFeatureId = mFeatureId;
    }

    /**
     * <blockquote><q>
     *     An identifier for a specific MovingFeature of a specific Collection of data
     *     </q><i><small><a href="https://docs.ogc.org/is/22-003r3/22-003r3.html#_6a53f5bb-7088-4fef-b4c3-ab65f8d40189"> OGC API - Moving Features, Part 1: Core - 6.1. General</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     * </ul>
     */
    public GetTemporalProperties mFeatureId(String mFeatureId) {
        setMFeatureId(mFeatureId);
        return this;
    }

    /**
     * @see #datetime(java.lang.String)
     */
    public String getDatetime() {
        return datetime;
    }

    /**
     * @see #datetime(java.lang.String)
     */
    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    /**
     * <blockquote><q>
     *     Only features that have a temporal primitive geometry (and temporal primitive
     *     value) that intersects the temporal information in the datetime parameter SHALL
     *     be part of the result set, if the parameter is provided.
     *     </q><i><small><a href="https://docs.ogc.org/is/22-003r3/22-003r3.html#req_core_param-datetime"> OGC API - Moving Features, Part 1: Core - Requirement 52 /req/common/param-datetime</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     *     <li>A date-time, or a (possibly half-bounded) interval, per RFC 3339 section 5.6.</li>
     * </ul>
     * <pre>
     * interval-bounded            = date-time "/" date-time
     * interval-half-bounded-start = [".."] "/" date-time
     * interval-half-bounded-end   = date-time "/" [".."]
     * interval                    = interval-bounded / interval-half-bounded-start / interval-half-bounded-end
     * datetime                    = date-time / interval
     * </pre>
     */
    public GetTemporalProperties datetime(String datetime) {
        setDatetime(datetime);
        return this;
    }

    /**
     * @see #limit(java.lang.Integer)
     */
    public Integer getLimit() {
        return limit;
    }

    /**
     * @see #limit(java.lang.Integer)
     */
    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    /**
     * <blockquote><q>
     *     If the limit parameter is provided by the client and supported by the server,
     *     then the response SHALL NOT contain more collections than specified by the
     *     limit parameter.
     *     </q><i><small><a href="https://docs.ogc.org/is/22-003r3/22-003r3.html#req_core_param-limit"> OGC API - Moving Features, Part 1: Core - Requirement 50 /req/common/param-limit</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional; minimum 1, maximum 10000, default 10.</li>
     * </ul>
     */
    public GetTemporalProperties limit(Integer limit) {
        setLimit(limit);
        return this;
    }

    /**
     * @see #subtemporalvalue(java.lang.Boolean)
     */
    public Boolean getSubtemporalvalue() {
        return subtemporalvalue;
    }

    /**
     * @see #subtemporalvalue(java.lang.Boolean)
     */
    public void setSubtemporalvalue(Boolean subtemporalvalue) {
        this.subtemporalvalue = subtemporalvalue;
    }

    /**
     * <blockquote><q>
     *     The subTemporalValue parameter SHALL be used with datetime parameter. If the
     *     subTemporalValue parameter is "true", the datetime parameter SHALL be a bounded
     *     interval, not half-bounded intervals or a date-time.
     *     </q><i><small><a href="https://docs.ogc.org/is/22-003r3/22-003r3.html#req_mf_subtemporalvalue-parameter-definition"> OGC API - Moving Features, Part 1: Core - Requirement 34 /req/movingfeatures/param-subtemporalvalue-definition</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>Requires {@code datetime} to be set to a bounded interval when {@code true}.</li>
     * </ul>
     */
    public GetTemporalProperties subtemporalvalue(Boolean subtemporalvalue) {
        setSubtemporalvalue(subtemporalvalue);
        return this;
    }

}
