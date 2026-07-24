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
 * The {@code GET} operation on {@code /systems/{systemId}/deployments}: lists the Deployments
 * associated with a System.
 *
 * @see <a href="https://docs.ogc.org/is/23-001/23-001.html">OGC API - Connected Systems - Part 1</a>
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
     * Local identifier of the system. (required)
     *
     * @return the systemId
     */
    public String getSystemId() {
        return systemId;
    }

    /**
     * @param systemId the systemId to set
     * @see #getSystemId()
     */
    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    /**
     * @param systemId the systemId to set
     * @see #getSystemId()
     */
    public GetSystemDeployments systemId(String systemId) {
        setSystemId(systemId);
        return this;
    }

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
    public GetSystemDeployments id(List<String> id) {
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
    public GetSystemDeployments bbox(Envelope bbox) {
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
    public GetSystemDeployments datetime(String datetime) {
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
    public GetSystemDeployments geom(String geom) {
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
    public GetSystemDeployments keyword(List<String> keyword) {
        setKeyword(keyword);
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
    public GetSystemDeployments foi(List<String> foi) {
        setFoi(foi);
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
    public GetSystemDeployments limit(Integer limit) {
        setLimit(limit);
        return this;
    }

}
