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
 *     List or search events about any system.
 *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part2/openapi/paths/systemEventsAll.yaml"> OGC API - Connected Systems - Part 2 (draft) - List all system events</a></small></i>
 * </blockquote>
 *
 * @author Johann Sorel (Geomatys)
 */
public final class GetAllSystemEvents extends RequestParameters {

    private String eventTime;
    private List<String> eventType;
    private List<String> q;
    private List<String> system;
    private Integer limit;

    /**
     * @see #eventTime(java.lang.String)
     */
    public String getEventTime() {
        return eventTime;
    }

    /**
     * @see #eventTime(java.lang.String)
     */
    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    /**
     * <blockquote><q>
     *     Either a date-time or an interval. Date and time expressions adhere to RFC 3339.
     *     Intervals may be bounded or half-bounded (double-dots at start or end).
     *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part2/openapi/parameters/eventTime.yaml"> OGC API - Connected Systems - Part 2 (draft) - eventTime (datetime) parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>Only resources that have a {@code time} property that intersects the value are selected.</li>
     * </ul>
     */
    public GetAllSystemEvents eventTime(String eventTime) {
        setEventTime(eventTime);
        return this;
    }

    /**
     * @see #eventType(java.util.List)
     */
    public List<String> getEventType() {
        return eventType;
    }

    /**
     * @see #eventType(java.util.List)
     */
    public void setEventType(List<String> eventType) {
        this.eventType = eventType;
    }

    /**
     * <blockquote><q>
     *     List of event types.
     *     Only event resources with a type that matches one of the provided types are selected.
     *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part2/openapi/parameters/eventTypeList.yaml"> OGC API - Connected Systems - Part 2 (draft) - eventType parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     * </ul>
     */
    public GetAllSystemEvents eventType(List<String> eventType) {
        setEventType(eventType);
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
    public GetAllSystemEvents q(List<String> q) {
        setQ(q);
        return this;
    }

    /**
     * @see #system(java.util.List)
     */
    public List<String> getSystem() {
        return system;
    }

    /**
     * @see #system(java.util.List)
     */
    public void setSystem(List<String> system) {
        this.system = system;
    }

    /**
     * <blockquote><q>
     *     List of system local IDs or unique IDs (URI).
     *     Only resources that are associated to a System that has one of the provided identifiers are selected.
     *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part1/openapi/parameters/systemIdList.yaml"> OGC API - Connected Systems - Part 1 (draft) - system parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     * </ul>
     */
    public GetAllSystemEvents system(List<String> system) {
        setSystem(system);
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
    public GetAllSystemEvents limit(Integer limit) {
        setLimit(limit);
        return this;
    }

}
