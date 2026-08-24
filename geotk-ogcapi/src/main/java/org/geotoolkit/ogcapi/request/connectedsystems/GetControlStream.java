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
 *     Get a control stream by ID
 *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part2/openapi/paths/controlStreamById.yaml"> OGC API - Connected Systems - Part 2: Dynamic Data (draft) - Get a control stream by ID</a></small></i>
 * </blockquote>
 *
 * @author Johann Sorel (Geomatys)
 */
public final class GetControlStream extends RequestParameters {

    private String controlStreamId;

    /**
     * @see #controlStreamId(java.lang.String)
     */
    public String getControlStreamId() {
        return controlStreamId;
    }

    /**
     * @see #controlStreamId(java.lang.String)
     */
    public void setControlStreamId(String controlStreamId) {
        this.controlStreamId = controlStreamId;
    }

    /**
     * <blockquote><q>
     *     Local identifier of a Control Stream
     *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part2/openapi/parameters/controlStreamId.yaml"> OGC API - Connected Systems - Part 2 (draft) - controlStreamId parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     *     <li>Non-empty string.</li>
     * </ul>
     */
    public GetControlStream controlStreamId(String controlStreamId) {
        setControlStreamId(controlStreamId);
        return this;
    }

}
