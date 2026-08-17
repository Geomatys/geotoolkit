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
 * The {@code tproperties/{tPropertyName}} query:
 * {@code GET /collections/{collectionId}/items/{mFeatureId}/tproperties/{tPropertyName}},
 * the (subsequence of the) data of a single named temporal property.
 *
 * <blockquote><q>
 *     For every temporal property in a moving feature (path
 *     {root}/collections/{collectionId}/items/{mFeatureId}/tproperties), the server SHALL
 *     support the HTTP GET operation at the path
 *     </q><i><small><a href="https://docs.ogc.org/is/22-003r3/22-003r3.html#req_mf-tproperty-op-get"> OGC API - Moving Features, Part 1: Core - Requirement 41 /req/movingfeatures/tproperty-get</a></small></i>
 * </blockquote>
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class GetTemporalProperty extends RequestParameters {

    private String collectionId;
    private String mFeatureId;
    private String tPropertyName;
    private String datetime;
    private List<String> leaf;
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
    public GetTemporalProperty collectionId(String collectionId) {
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
    public GetTemporalProperty mFeatureId(String mFeatureId) {
        setMFeatureId(mFeatureId);
        return this;
    }

    /**
     * @see #tPropertyName(java.lang.String)
     */
    public String getTPropertyName() {
        return tPropertyName;
    }

    /**
     * @see #tPropertyName(java.lang.String)
     */
    public void setTPropertyName(String tPropertyName) {
        this.tPropertyName = tPropertyName;
    }

    /**
     * <blockquote><q>
     *     An identifier for a specific TemporalProperty of a specific MovingFeatures of data
     *     </q><i><small><a href="https://docs.ogc.org/is/22-003r3/22-003r3.html#_6a53f5bb-7088-4fef-b4c3-ab65f8d40189"> OGC API - Moving Features, Part 1: Core - 6.1. General</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     * </ul>
     */
    public GetTemporalProperty tPropertyName(String tPropertyName) {
        setTPropertyName(tPropertyName);
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
     */
    public GetTemporalProperty datetime(String datetime) {
        setDatetime(datetime);
        return this;
    }

    /**
     * @see #leaf(java.util.List)
     */
    public List<String> getLeaf() {
        return leaf;
    }

    /**
     * @see #leaf(java.util.List)
     */
    public void setLeaf(List<String> leaf) {
        this.leaf = leaf;
    }

    /**
     * <blockquote><q>
     *     The leaf parameter SHALL be a sequence of monotonic increasing instants with
     *     date-time strings. The syntax of date-time is specified by RFC 3339, 5.6.
     *     </q><i><small><a href="https://docs.ogc.org/is/22-003r3/22-003r3.html#req_mf_leaf-parameter-definition"> OGC API - Moving Features, Part 1: Core - Requirement 23 /req/movingfeatures/param-leaf-definition</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>At least 1 unique date-time value, monotonically increasing.</li>
     * </ul>
     * <pre>
     * name: leaf
     * in: query
     * required: false
     * schema:
     *   type: array
     *   uniqueItems: true
     *   minItems: 1
     *   items:
     *     type: string
     *     format: date-time
     * style: form
     * explode: false
     * </pre>
     */
    public GetTemporalProperty leaf(List<String> leaf) {
        setLeaf(leaf);
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
    public GetTemporalProperty subtemporalvalue(Boolean subtemporalvalue) {
        setSubtemporalvalue(subtemporalvalue);
        return this;
    }

}
