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
 *     Get an observation by ID
 *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part2/openapi/paths/observationById.yaml"> OGC API - Connected Systems - Part 2: Dynamic Data (draft) - Get an observation</a></small></i>
 * </blockquote>
 *
 * @author Johann Sorel (Geomatys)
 */
public final class GetObservation extends RequestParameters {

    private String obsId;

    /**
     * @see #obsId(java.lang.String)
     */
    public String getObsId() {
        return obsId;
    }

    /**
     * @see #obsId(java.lang.String)
     */
    public void setObsId(String obsId) {
        this.obsId = obsId;
    }

    /**
     * <blockquote><q>
     *     Local identifier of an Observation
     *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part2/openapi/parameters/obsId.yaml"> OGC API - Connected Systems - Part 2: Dynamic Data (draft) - obsId parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     * </ul>
     */
    public GetObservation obsId(String obsId) {
        setObsId(obsId);
        return this;
    }

}
