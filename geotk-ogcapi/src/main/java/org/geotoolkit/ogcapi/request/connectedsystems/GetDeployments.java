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
 * The {@code GET} operation on {@code /deployments}: lists Deployment resources.
 *
 * @see <a href="https://docs.ogc.org/is/23-001/23-001.html">OGC API - Connected Systems - Part 1</a>
 * @author Johann Sorel (Geomatys)
 */
public final class GetDeployments extends RequestParameters {

    private List<String> id;
    private Envelope bbox;
    private String datetime;
    private String geom;
    private List<String> keyword;
    private List<String> parent;
    private List<String> system;
    private List<String> foi;
    private List<String> observedProperty;
    private List<String> controlledProperty;
    private Integer limit;

    /**
     * Only resources with one of the given identifiers are selected. (optional)
     *
     * @return the id
     */
    public List<String> getId() {
        return id;
    }

    /**
     * @param id the id to set
     * @see #getId()
     */
    public void setId(List<String> id) {
        this.id = id;
    }

    /**
     * @param id the id to set
     * @see #getId()
     */
    public GetDeployments id(List<String> id) {
        setId(id);
        return this;
    }

    /**
     * Only resources that have a geometry that intersects the bounding box are selected. The bounding
     * box coordinate reference system is derived from this envelope's own coordinate reference system.
     * (optional)
     *
     * @return the bbox
     */
    public Envelope getBbox() {
        return bbox;
    }

    /**
     * @param bbox the bbox to set
     * @see #getBbox()
     */
    public void setBbox(Envelope bbox) {
        this.bbox = bbox;
    }

    /**
     * @param bbox the bbox to set
     * @see #getBbox()
     */
    public GetDeployments bbox(Envelope bbox) {
        setBbox(bbox);
        return this;
    }

    /**
     * A date-time or a period, per RFC 3339. (optional)
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
    public GetDeployments datetime(String datetime) {
        setDatetime(datetime);
        return this;
    }

    /**
     * A WKT geometry used to spatially filter the selected resources. (optional)
     *
     * @return the geom
     */
    public String getGeom() {
        return geom;
    }

    /**
     * @param geom the geom to set
     * @see #getGeom()
     */
    public void setGeom(String geom) {
        this.geom = geom;
    }

    /**
     * @param geom the geom to set
     * @see #getGeom()
     */
    public GetDeployments geom(String geom) {
        setGeom(geom);
        return this;
    }

    /**
     * Only resources that match one of the given keywords are selected. (optional)
     *
     * @return the keyword
     */
    public List<String> getKeyword() {
        return keyword;
    }

    /**
     * @param keyword the keyword to set
     * @see #getKeyword()
     */
    public void setKeyword(List<String> keyword) {
        this.keyword = keyword;
    }

    /**
     * @param keyword the keyword to set
     * @see #getKeyword()
     */
    public GetDeployments keyword(List<String> keyword) {
        setKeyword(keyword);
        return this;
    }

    /**
     * Only resources that are children of one of the given parent identifiers are selected. (optional)
     *
     * @return the parent
     */
    public List<String> getParent() {
        return parent;
    }

    /**
     * @param parent the parent to set
     * @see #getParent()
     */
    public void setParent(List<String> parent) {
        this.parent = parent;
    }

    /**
     * @param parent the parent to set
     * @see #getParent()
     */
    public GetDeployments parent(List<String> parent) {
        setParent(parent);
        return this;
    }

    /**
     * Only resources associated with one of the given system identifiers are selected. (optional)
     *
     * @return the system
     */
    public List<String> getSystem() {
        return system;
    }

    /**
     * @param system the system to set
     * @see #getSystem()
     */
    public void setSystem(List<String> system) {
        this.system = system;
    }

    /**
     * @param system the system to set
     * @see #getSystem()
     */
    public GetDeployments system(List<String> system) {
        setSystem(system);
        return this;
    }

    /**
     * Only resources associated with one of the given features of interest are selected. (optional)
     *
     * @return the foi
     */
    public List<String> getFoi() {
        return foi;
    }

    /**
     * @param foi the foi to set
     * @see #getFoi()
     */
    public void setFoi(List<String> foi) {
        this.foi = foi;
    }

    /**
     * @param foi the foi to set
     * @see #getFoi()
     */
    public GetDeployments foi(List<String> foi) {
        setFoi(foi);
        return this;
    }

    /**
     * Only resources that observe one of the given properties are selected. (optional)
     *
     * @return the observedProperty
     */
    public List<String> getObservedProperty() {
        return observedProperty;
    }

    /**
     * @param observedProperty the observedProperty to set
     * @see #getObservedProperty()
     */
    public void setObservedProperty(List<String> observedProperty) {
        this.observedProperty = observedProperty;
    }

    /**
     * @param observedProperty the observedProperty to set
     * @see #getObservedProperty()
     */
    public GetDeployments observedProperty(List<String> observedProperty) {
        setObservedProperty(observedProperty);
        return this;
    }

    /**
     * Only resources that control one of the given properties are selected. (optional)
     *
     * @return the controlledProperty
     */
    public List<String> getControlledProperty() {
        return controlledProperty;
    }

    /**
     * @param controlledProperty the controlledProperty to set
     * @see #getControlledProperty()
     */
    public void setControlledProperty(List<String> controlledProperty) {
        this.controlledProperty = controlledProperty;
    }

    /**
     * @param controlledProperty the controlledProperty to set
     * @see #getControlledProperty()
     */
    public GetDeployments controlledProperty(List<String> controlledProperty) {
        setControlledProperty(controlledProperty);
        return this;
    }

    /**
     * The optional limit parameter limits the number of items presented in the response. (optional)
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
    public GetDeployments limit(Integer limit) {
        setLimit(limit);
        return this;
    }

}
