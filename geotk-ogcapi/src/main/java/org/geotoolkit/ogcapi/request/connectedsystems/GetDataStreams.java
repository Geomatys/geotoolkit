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
 *     List or search all datastreams available from this server endpoint.
 *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part2/openapi/paths/dataStreams.yaml"> OGC API - Connected Systems - Part 2: Dynamic Data (draft) - Datastreams collection (GET)</a></small></i>
 * </blockquote>
 *
 * @author Johann Sorel (Geomatys)
 */
public final class GetDataStreams extends RequestParameters {

    private List<String> id;
    private List<String> q;
    private String phenomenonTime;
    private String resultTime;
    private List<String> system;
    private List<String> foi;
    private List<String> observedProperty;
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
    public GetDataStreams id(List<String> id) {
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
    public GetDataStreams q(List<String> q) {
        setQ(q);
        return this;
    }

    /**
     * @see #phenomenonTime(java.lang.String)
     */
    public String getPhenomenonTime() {
        return phenomenonTime;
    }

    /**
     * @see #phenomenonTime(java.lang.String)
     */
    public void setPhenomenonTime(String phenomenonTime) {
        this.phenomenonTime = phenomenonTime;
    }

    /**
     * <blockquote><q>
     *     Either a date-time or an interval. Date and time expressions adhere to RFC 3339.
     *     Intervals may be bounded or half-bounded (double-dots at start or end).
     *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part2/openapi/parameters/phenomenonTime.yaml"> OGC API - Connected Systems - Part 2 (draft) - phenomenonTime parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>Only resources that have a {@code phenomenonTime} property that intersects the value are selected.</li>
     * </ul>
     */
    public GetDataStreams phenomenonTime(String phenomenonTime) {
        setPhenomenonTime(phenomenonTime);
        return this;
    }

    /**
     * @see #resultTime(java.lang.String)
     */
    public String getResultTime() {
        return resultTime;
    }

    /**
     * @see #resultTime(java.lang.String)
     */
    public void setResultTime(String resultTime) {
        this.resultTime = resultTime;
    }

    /**
     * <blockquote><q>
     *     Either a date-time or an interval. Date and time expressions adhere to RFC 3339.
     *     Intervals may be bounded or half-bounded (double-dots at start or end).
     *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part2/openapi/parameters/resultTime.yaml"> OGC API - Connected Systems - Part 2 (draft) - resultTime parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>Only resources that have a {@code resultTime} property that intersects the value are selected.</li>
     * </ul>
     */
    public GetDataStreams resultTime(String resultTime) {
        setResultTime(resultTime);
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
    public GetDataStreams system(List<String> system) {
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
    public GetDataStreams foi(List<String> foi) {
        setFoi(foi);
        return this;
    }

    /**
     * @see #observedProperty(java.util.List)
     */
    public List<String> getObservedProperty() {
        return observedProperty;
    }

    /**
     * @see #observedProperty(java.util.List)
     */
    public void setObservedProperty(List<String> observedProperty) {
        this.observedProperty = observedProperty;
    }

    /**
     * <blockquote><q>
     *     List of property local IDs or unique IDs (URI).
     *     Only resources that are associated to an observable property that has one of the provided identifiers are selected.
     *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part1/openapi/parameters/obsPropIdList.yaml"> OGC API - Connected Systems - Part 1 (draft) - observedProperty parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     * </ul>
     */
    public GetDataStreams observedProperty(List<String> observedProperty) {
        setObservedProperty(observedProperty);
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
    public GetDataStreams limit(Integer limit) {
        setLimit(limit);
        return this;
    }

}
