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
 *     Get a status report by ID
 *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part2/openapi/paths/commandStatusById.yaml"> OGC API - Connected Systems - Part 2: Dynamic Data (draft) - Get a status report</a></small></i>
 * </blockquote>
 *
 * @author Johann Sorel (Geomatys)
 */
public final class GetCommandStatus extends RequestParameters {

    private String cmdId;
    private String statusId;

    /**
     * @see #cmdId(java.lang.String)
     */
    public String getCmdId() {
        return cmdId;
    }

    /**
     * @see #cmdId(java.lang.String)
     */
    public void setCmdId(String cmdId) {
        this.cmdId = cmdId;
    }

    /**
     * <blockquote><q>
     *     Local identifier of a Command
     *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part2/openapi/parameters/cmdId.yaml"> OGC API - Connected Systems - Part 2: Dynamic Data (draft) - cmdId parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     * </ul>
     */
    public GetCommandStatus cmdId(String cmdId) {
        setCmdId(cmdId);
        return this;
    }

    /**
     * @see #statusId(java.lang.String)
     */
    public String getStatusId() {
        return statusId;
    }

    /**
     * @see #statusId(java.lang.String)
     */
    public void setStatusId(String statusId) {
        this.statusId = statusId;
    }

    /**
     * <blockquote><q>
     *     Local identifier of a Status Report
     *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part2/openapi/parameters/statusId.yaml"> OGC API - Connected Systems - Part 2: Dynamic Data (draft) - statusId parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     * </ul>
     */
    public GetCommandStatus statusId(String statusId) {
        setStatusId(statusId);
        return this;
    }

}
