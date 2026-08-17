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
 * The {@code items} query: {@code GET /collections/{collectionId}/items}, a list of the
 * static data of moving features.
 *
 * <blockquote><q>
 *     For every moving feature collection identified in the moving feature collections
 *     response (path {root}/collections), the server SHALL support the HTTP GET
 *     operation at the path {root}/collections/{collectionId}/items.
 *     </q><i><small><a href="https://docs.ogc.org/is/22-003r3/22-003r3.html#req_mf-features-op-get"> OGC API - Moving Features, Part 1: Core - Requirement 14 /req/movingfeatures/features-get</a></small></i>
 * </blockquote>
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
    public GetMovingFeatures collectionId(String collectionId) {
        setCollectionId(collectionId);
        return this;
    }

    /**
     * @see #bbox(org.opengis.geometry.Envelope)
     */
    public Envelope getBbox() {
        return bbox;
    }

    /**
     * @see #bbox(org.opengis.geometry.Envelope)
     */
    public void setBbox(Envelope bbox) {
        this.bbox = bbox;
    }

    /**
     * <blockquote><q>
     *     The bounding box SHALL be provided as four or six numbers, depending on whether
     *     the coordinate reference system includes a vertical axis (height or depth):
     *     </q><i><small><a href="https://docs.ogc.org/is/22-003r3/22-003r3.html#req_core_param-bbox"> OGC API - Moving Features, Part 1: Core - Requirement 51 /req/common/param-bbox</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>Four numbers (2D) or six numbers (3D): lower-left corner then upper-right corner.</li>
     *     <li>WGS 84 longitude/latitude (CRS84) unless a different CRS is otherwise specified.</li>
     * </ul>
     * <pre>
     * name: bbox
     * in: query
     * required: false
     * schema:
     *   type: array
     *   oneOf:
     *     - minItems: 4
     *       maxItems: 4
     *     - minItems: 6
     *       maxItems: 6
     *   items:
     *     type: number
     * style: form
     * explode: false
     * </pre>
     */
    public GetMovingFeatures bbox(Envelope bbox) {
        setBbox(bbox);
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
    public GetMovingFeatures datetime(String datetime) {
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
    public GetMovingFeatures limit(Integer limit) {
        setLimit(limit);
        return this;
    }

    /**
     * @see #subtrajectory(java.lang.Boolean)
     */
    public Boolean getSubtrajectory() {
        return subtrajectory;
    }

    /**
     * @see #subtrajectory(java.lang.Boolean)
     */
    public void setSubtrajectory(Boolean subtrajectory) {
        this.subtrajectory = subtrajectory;
    }

    /**
     * <blockquote><q>
     *     The subTrajectory parameter SHALL be used with a datetime parameter. If the
     *     subTrajecotry parameter is "true", the datetime parameter SHALL be a bounded
     *     interval, not half-bounded intervals or a date-time.
     *     </q><i><small><a href="https://docs.ogc.org/is/22-003r3/22-003r3.html#req_mf_subtrajectory-parameter-definition"> OGC API - Moving Features, Part 1: Core - Requirement 12 /req/movingfeatures/param-subtrajectory-definition</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>Requires {@code datetime} to be set to a bounded interval when {@code true}.</li>
     * </ul>
     */
    public GetMovingFeatures subtrajectory(Boolean subtrajectory) {
        setSubtrajectory(subtrajectory);
        return this;
    }

}
