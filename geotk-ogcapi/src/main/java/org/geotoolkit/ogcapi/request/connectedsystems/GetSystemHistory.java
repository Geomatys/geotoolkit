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

/**
 * <blockquote><q>
 *     List or search historized descriptions of the parent system.
 *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part2/openapi/paths/systemHistory.yaml"> OGC API - Connected Systems - Part 2 (draft) - List system description history</a></small></i>
 * </blockquote>
 *
 * @author Johann Sorel (Geomatys)
 */
public final class GetSystemHistory extends RequestParameters {

    private String systemId;
    private String validTime;
    private List<String> q;
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
    public GetSystemHistory systemId(String systemId) {
        setSystemId(systemId);
        return this;
    }

    /**
     * @see #validTime(java.lang.String)
     */
    public String getValidTime() {
        return validTime;
    }

    /**
     * @see #validTime(java.lang.String)
     */
    public void setValidTime(String validTime) {
        this.validTime = validTime;
    }

    /**
     * <blockquote><q>
     *     Either a date-time or an interval. Date and time expressions adhere to RFC 3339.
     *     Intervals may be bounded or half-bounded (double-dots at start or end).
     *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part2/openapi/parameters/validTime.yaml"> OGC API - Connected Systems - Part 2 (draft) - validTime parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>Only resources that have a {@code validTime} property that intersects the value are selected.</li>
     * </ul>
     */
    public GetSystemHistory validTime(String validTime) {
        setValidTime(validTime);
        return this;
    }

    /**
     * @see #q(java.util.List)
     */
    public List<String> getQ() {
        return q;
    }

    /**
     * @see #q(java.util.List)
     */
    public void setQ(List<String> q) {
        this.q = q;
    }

    /**
     * <blockquote><q>
     *     Comma separated list of keywords used for full-text search.
     *     Only resources that have textual fields that contain one of the specified keywords are selected.
     *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part1/openapi/parameters/keyword.yaml"> OGC API - Connected Systems - Part 1 (draft) - keyword (q) parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     * </ul>
     * <pre>gps,imu</pre>
     */
    public GetSystemHistory q(List<String> q) {
        setQ(q);
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
    public GetSystemHistory limit(Integer limit) {
        setLimit(limit);
        return this;
    }

}
