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
 *     List or search all `System` resources that are subsystems (i.e., components) of a specific
 *     parent system.
 *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part1/openapi/paths/subsystems.yaml"> OGC API - Connected Systems - Part 1 (draft) - List subsystems</a></small></i>
 * </blockquote>
 *
 * @author Johann Sorel (Geomatys)
 */
public final class GetSubsystems extends RequestParameters {

    private String systemId;
    private List<String> id;
    private Envelope bbox;
    private String datetime;
    private String geom;
    private List<String> keyword;
    private List<String> parent;
    private List<String> procedure;
    private List<String> foi;
    private List<String> observedProperty;
    private List<String> controlledProperty;
    private Boolean recursive;
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
     *     <li>Identifies the parent System whose subsystems are listed.</li>
     * </ul>
     */
    public GetSubsystems systemId(String systemId) {
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
     *     Only resources that have one of the provided identifiers are selected.
     *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part1/openapi/parameters/idList.yaml"> OGC API - Connected Systems - Part 1 (draft) - id parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     * </ul>
     * <pre>RES_ID1,RES_ID2,RES_ID63</pre>
     */
    public GetSubsystems id(List<String> id) {
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
     *     <li>The bounding box CRS is derived from this envelope's own coordinate reference system.</li>
     * </ul>
     */
    public GetSubsystems bbox(Envelope bbox) {
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
     *     <li>Only resources that have a {@code validTime} property that intersects the value are selected.</li>
     * </ul>
     */
    public GetSubsystems datetime(String datetime) {
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
     *     WKT geometry and operator to filter resources on their location or geometry.
     *     Only features that have a geometry that intersects the value of `geom` are selected.
     *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part1/openapi/parameters/geom.yaml"> OGC API - Connected Systems - Part 1 (draft) - geom parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     * </ul>
     * <pre>POLYGON((0 0,4 0,4 4,0 4,0 0))</pre>
     */
    public GetSubsystems geom(String geom) {
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
     *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part1/openapi/parameters/keyword.yaml"> OGC API - Connected Systems - Part 1 (draft) - keyword (q) parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>The resource {@code name} and {@code description} properties are always searched.</li>
     * </ul>
     * <pre>gps,imu</pre>
     */
    public GetSubsystems keyword(List<String> keyword) {
        setKeyword(keyword);
        return this;
    }

    /**
     * @see #parent(java.util.List)
     */
    public List<String> getParent() {
        return parent;
    }

    /**
     * @see #parent(java.util.List)
     */
    public void setParent(List<String> parent) {
        this.parent = parent;
    }

    /**
     * <blockquote><q>
     *     List of resource local IDs or unique IDs (URI).
     *     Only resources that have a parent that has one of the provided identifiers are selected.
     *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part1/openapi/parameters/parentIdList.yaml"> OGC API - Connected Systems - Part 1 (draft) - parent parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     * </ul>
     */
    public GetSubsystems parent(List<String> parent) {
        setParent(parent);
        return this;
    }

    /**
     * @see #procedure(java.util.List)
     */
    public List<String> getProcedure() {
        return procedure;
    }

    /**
     * @see #procedure(java.util.List)
     */
    public void setProcedure(List<String> procedure) {
        this.procedure = procedure;
    }

    /**
     * <blockquote><q>
     *     List of procedure local IDs or unique IDs (URI).
     *     Only systems that implement a procedure that has one of the provided identifiers are selected.
     *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part1/openapi/parameters/procedureIdList.yaml"> OGC API - Connected Systems - Part 1 (draft) - procedure parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     * </ul>
     */
    public GetSubsystems procedure(List<String> procedure) {
        setProcedure(procedure);
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
    public GetSubsystems foi(List<String> foi) {
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
    public GetSubsystems observedProperty(List<String> observedProperty) {
        setObservedProperty(observedProperty);
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
    public GetSubsystems controlledProperty(List<String> controlledProperty) {
        setControlledProperty(controlledProperty);
        return this;
    }

    /**
     * @see #recursive(java.lang.Boolean)
     */
    public Boolean getRecursive() {
        return recursive;
    }

    /**
     * @see #recursive(java.lang.Boolean)
     */
    public void setRecursive(Boolean recursive) {
        this.recursive = recursive;
    }

    /**
     * <blockquote><q>
     *     If true, instructs the server to include nested subsystems, recursively.
     *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part1/openapi/paths/subsystems.yaml"> OGC API - Connected Systems - Part 1 (draft) - subsystems recursive parameter override</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional; default {@code false}.</li>
     * </ul>
     */
    public GetSubsystems recursive(Boolean recursive) {
        setRecursive(recursive);
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
    public GetSubsystems limit(Integer limit) {
        setLimit(limit);
        return this;
    }

}
