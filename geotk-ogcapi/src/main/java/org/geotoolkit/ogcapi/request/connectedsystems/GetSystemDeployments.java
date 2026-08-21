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
package org.geotoolkit.ogcapi.request.connectedsystems;

import java.util.List;
import org.geotoolkit.ogcapi.request.RequestParameters;
import org.opengis.geometry.Envelope;

/**
 * <blockquote><q>
 *     List deployments of a specific system
 *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part1/openapi/paths/systemDeployments.yaml"> OGC API - Connected Systems - Part 1 (draft) - List deployments of a specific system</a></small></i>
 * </blockquote>
 * <p>
 * No verbatim spec description text is provided for this operation at the time of writing
 * (the specification only gives its one-line summary, quoted above).
 *
 * @author Johann Sorel (Geomatys)
 */
public final class GetSystemDeployments extends RequestParameters {

    private String systemId;
    private List<String> id;
    private Envelope bbox;
    private String datetime;
    private String geom;
    private List<String> keyword;
    private List<String> foi;
    private Integer limit;

    /**
     * @see #systemId(java.lang.String)
     */
    public String getSystemId() {
        return systemId;
    }

    /**
     * @see #systemId(java.lang.String)
     */
    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    /**
     * <blockquote><q>
     *     Local identifier of a System
     *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part1/openapi/parameters/systemId.yaml"> OGC API - Connected Systems - Part 1 (draft) - systemId parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     *     <li>Non-empty string.</li>
     * </ul>
     */
    public GetSystemDeployments systemId(String systemId) {
        setSystemId(systemId);
        return this;
    }

    /**
     * @see #id(java.util.List)
     */
    public List<String> getId() {
        return id;
    }

    /**
     * @see #id(java.util.List)
     */
    public void setId(List<String> id) {
        this.id = id;
    }

    /**
     * <blockquote><q>
     *     List of resource local IDs or unique IDs (URI).
     *     Only resources that have an identifier that matches one of the provided identifiers are selected.
     *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part1/openapi/parameters/idList.yaml"> OGC API - Connected Systems - Part 1 (draft) - id parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     * </ul>
     */
    public GetSystemDeployments id(List<String> id) {
        setId(id);
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
     *     Only features that have a geometry that intersects the bounding box are selected.
     *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part1/openapi/parameters/bbox.yaml"> OGC API - Connected Systems - Part 1 (draft) - bbox parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>The coordinate reference system is derived from this envelope's own CRS.</li>
     * </ul>
     */
    public GetSystemDeployments bbox(Envelope bbox) {
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
     *     Either a date-time or an interval. Date and time expressions adhere to RFC 3339.
     *     Intervals may be bounded or half-bounded (double-dots at start or end).
     *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part1/openapi/parameters/datetime.yaml"> OGC API - Connected Systems - Part 1 (draft) - datetime parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>Only resources that have a temporal property that intersects the value are selected.</li>
     * </ul>
     */
    public GetSystemDeployments datetime(String datetime) {
        setDatetime(datetime);
        return this;
    }

    /**
     * @see #geom(java.lang.String)
     */
    public String getGeom() {
        return geom;
    }

    /**
     * @see #geom(java.lang.String)
     */
    public void setGeom(String geom) {
        this.geom = geom;
    }

    /**
     * <blockquote><q>
     *     Only resources that have a geometry that matches the spatial predicate are selected.
     *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part1/openapi/parameters/geom.yaml"> OGC API - Connected Systems - Part 1 (draft) - geom parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>A WKT geometry.</li>
     * </ul>
     */
    public GetSystemDeployments geom(String geom) {
        setGeom(geom);
        return this;
    }

    /**
     * @see #keyword(java.util.List)
     */
    public List<String> getKeyword() {
        return keyword;
    }

    /**
     * @see #keyword(java.util.List)
     */
    public void setKeyword(List<String> keyword) {
        this.keyword = keyword;
    }

    /**
     * <blockquote><q>
     *     Comma separated list of keywords used for full-text search.
     *     Only resources that have textual fields that contain one of the specified keywords are selected.
     *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part1/openapi/parameters/keyword.yaml"> OGC API - Connected Systems - Part 1 (draft) - keyword parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     * </ul>
     * <pre>gps,imu</pre>
     */
    public GetSystemDeployments keyword(List<String> keyword) {
        setKeyword(keyword);
        return this;
    }

    /**
     * @see #foi(java.util.List)
     */
    public List<String> getFoi() {
        return foi;
    }

    /**
     * @see #foi(java.util.List)
     */
    public void setFoi(List<String> foi) {
        this.foi = foi;
    }

    /**
     * <blockquote><q>
     *     List of feature of interest local IDs or unique IDs (URI).
     *     Only resources associated to a Feature of Interest that has one of the provided identifiers are selected.
     *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part1/openapi/parameters/foiIdList.yaml"> OGC API - Connected Systems - Part 1 (draft) - foi parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     * </ul>
     */
    public GetSystemDeployments foi(List<String> foi) {
        setFoi(foi);
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
     *     This parameter limits the number of items that are presented in the response document.
     *     Only items that are on the first level of the collection in the response document are counted.
     *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part1/openapi/parameters/limit.yaml"> OGC API - Connected Systems - Part 1 (draft) - limit parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>Between 1 and 10000.</li>
     *     <li>Default: 10.</li>
     * </ul>
     */
    public GetSystemDeployments limit(Integer limit) {
        setLimit(limit);
        return this;
    }

}
