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

import org.geotoolkit.ogcapi.request.RequestParameters;

/**
 * <blockquote><q>
 *     This will return the latest description of the system valid before or at the current
 *     time, by default.
 *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part1/openapi/paths/systemById.yaml"> OGC API - Connected Systems - Part 1 (draft) - Get a system by ID</a></small></i>
 * </blockquote>
 *
 * @author Johann Sorel (Geomatys)
 */
public final class GetSystem extends RequestParameters {

    private String systemId;
    private String datetime;

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
    public GetSystem systemId(String systemId) {
        setSystemId(systemId);
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
     *     <li>Optional; if system history is supported, should be a single time instant.</li>
     *     <li>Selects the system description valid at the given time, instead of the latest one.</li>
     * </ul>
     */
    public GetSystem datetime(String datetime) {
        setDatetime(datetime);
        return this;
    }

}
