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
 * The {@code GET} operation on {@code /systems/{systemId}/history}: lists the historical revisions
 * of a System.
 *
 * @see <a href="https://docs.ogc.org/is/23-002/23-002.html">OGC API - Connected Systems - Part 2</a>
 * @author Johann Sorel (Geomatys)
 */
public final class GetSystemHistory extends RequestParameters {

    private String systemId;
    private String validTime;
    private List<String> q;
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
    public GetSystemHistory systemId(String systemId) {
        setSystemId(systemId);
        return this;
    }

    /**
     * A date-time or a period, per RFC 3339, filtering on the validity time. (optional)
     *
     * @return the validTime
     */
    public String getValidTime() {
        return validTime;
    }

    /**
     * @param validTime the validTime to set
     * @see #getValidTime()
     */
    public void setValidTime(String validTime) {
        this.validTime = validTime;
    }

    /**
     * @param validTime the validTime to set
     * @see #getValidTime()
     */
    public GetSystemHistory validTime(String validTime) {
        setValidTime(validTime);
        return this;
    }

    /**
     * Free-text search on the resource's textual properties. (optional)
     *
     * @return the q
     */
    public List<String> getQ() {
        return q;
    }

    /**
     * @param q the q to set
     * @see #getQ()
     */
    public void setQ(List<String> q) {
        this.q = q;
    }

    /**
     * @param q the q to set
     * @see #getQ()
     */
    public GetSystemHistory q(List<String> q) {
        setQ(q);
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
    public GetSystemHistory limit(Integer limit) {
        setLimit(limit);
        return this;
    }

}
