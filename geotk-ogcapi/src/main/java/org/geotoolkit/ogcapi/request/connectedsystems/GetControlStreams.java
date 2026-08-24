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
 *     List or search all control streams available from this server endpoint.
 *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part2/openapi/paths/controlStreams.yaml"> OGC API - Connected Systems - Part 2: Dynamic Data (draft) - Control streams collection (GET)</a></small></i>
 * </blockquote>
 *
 * @author Johann Sorel (Geomatys)
 */
public final class GetControlStreams extends RequestParameters {

    private List<String> id;
    private List<String> q;
    private String issueTime;
    private String executionTime;
    private List<String> statusCode;
    private List<String> sender;
    private List<String> system;
    private List<String> foi;
    private List<String> controlledProperty;
    private Integer limit;

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
     *     Only resources that have one of the provided identifiers are selected.
     *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part1/openapi/parameters/idList.yaml"> OGC API - Connected Systems - Part 1 (draft) - id parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     * </ul>
     * <pre>RES_ID1,RES_ID2,RES_ID63</pre>
     */
    public GetControlStreams id(List<String> id) {
        setId(id);
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
     *     <li>The resource {@code name} and {@code description} properties are always searched.</li>
     * </ul>
     * <pre>gps,imu</pre>
     */
    public GetControlStreams q(List<String> q) {
        setQ(q);
        return this;
    }

    /**
     * @see #issueTime(java.lang.String)
     */
    public String getIssueTime() {
        return issueTime;
    }

    /**
     * @see #issueTime(java.lang.String)
     */
    public void setIssueTime(String issueTime) {
        this.issueTime = issueTime;
    }

    /**
     * <blockquote><q>
     *     Either a date-time or an interval. Date and time expressions adhere to RFC 3339.
     *     Intervals may be bounded or half-bounded (double-dots at start or end).
     *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part2/openapi/parameters/issueTime.yaml"> OGC API - Connected Systems - Part 2 (draft) - issueTime parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>Only resources that have an {@code issueTime} property that intersects the value are selected.</li>
     * </ul>
     */
    public GetControlStreams issueTime(String issueTime) {
        setIssueTime(issueTime);
        return this;
    }

    /**
     * @see #executionTime(java.lang.String)
     */
    public String getExecutionTime() {
        return executionTime;
    }

    /**
     * @see #executionTime(java.lang.String)
     */
    public void setExecutionTime(String executionTime) {
        this.executionTime = executionTime;
    }

    /**
     * <blockquote><q>
     *     Either a date-time or an interval. Date and time expressions adhere to RFC 3339.
     *     Intervals may be bounded or half-bounded (double-dots at start or end).
     *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part2/openapi/parameters/executionTime.yaml"> OGC API - Connected Systems - Part 2 (draft) - executionTime parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>Only resources that have an {@code executionTime} property that intersects the value are selected.</li>
     * </ul>
     */
    public GetControlStreams executionTime(String executionTime) {
        setExecutionTime(executionTime);
        return this;
    }

    /**
     * @see #statusCode(java.util.List)
     */
    public List<String> getStatusCode() {
        return statusCode;
    }

    /**
     * @see #statusCode(java.util.List)
     */
    public void setStatusCode(List<String> statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * <blockquote><q>
     *     List of command status codes.
     *     Only command resources whose current status matches one of the provided status codes are selected.
     *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part2/openapi/parameters/statusCodeList.yaml"> OGC API - Connected Systems - Part 2 (draft) - statusCode parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>One of {@code PENDING}, {@code ACCEPTED}, {@code REJECTED}, {@code SCHEDULED},
     *     {@code UPDATED}, {@code CANCELED}, {@code EXECUTING}, {@code FAILED}, {@code COMPLETED}.</li>
     * </ul>
     */
    public GetControlStreams statusCode(List<String> statusCode) {
        setStatusCode(statusCode);
        return this;
    }

    /**
     * @see #sender(java.util.List)
     */
    public List<String> getSender() {
        return sender;
    }

    /**
     * @see #sender(java.util.List)
     */
    public void setSender(List<String> sender) {
        this.sender = sender;
    }

    /**
     * <blockquote><q>
     *     List of sender IDs.
     *     Only command resources issued by one of the specified senders are selected.
     *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part2/openapi/parameters/senderList.yaml"> OGC API - Connected Systems - Part 2 (draft) - sender parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     * </ul>
     */
    public GetControlStreams sender(List<String> sender) {
        setSender(sender);
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
    public GetControlStreams system(List<String> system) {
        setSystem(system);
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
     *     List of feature local IDs or unique IDs (URI).
     *     Only resources that are associated to a feature of interest that has one of the provided identifiers are selected.
     *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part1/openapi/parameters/foiIdList.yaml"> OGC API - Connected Systems - Part 1 (draft) - foi parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     * </ul>
     */
    public GetControlStreams foi(List<String> foi) {
        setFoi(foi);
        return this;
    }

    /**
     * @see #controlledProperty(java.util.List)
     */
    public List<String> getControlledProperty() {
        return controlledProperty;
    }

    /**
     * @see #controlledProperty(java.util.List)
     */
    public void setControlledProperty(List<String> controlledProperty) {
        this.controlledProperty = controlledProperty;
    }

    /**
     * <blockquote><q>
     *     List of property local IDs or unique IDs (URI).
     *     Only resources that are associated to a controllable property that has one of the provided identifiers are selected.
     *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part1/openapi/parameters/controlPropIdList.yaml"> OGC API - Connected Systems - Part 1 (draft) - controlledProperty parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     * </ul>
     */
    public GetControlStreams controlledProperty(List<String> controlledProperty) {
        setControlledProperty(controlledProperty);
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
    public GetControlStreams limit(Integer limit) {
        setLimit(limit);
        return this;
    }

}
