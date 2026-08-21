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
 *     Get a datastream by ID
 *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part2/openapi/paths/dataStreamById.yaml"> OGC API - Connected Systems - Part 2: Dynamic Data (draft) - Get a datastream by ID</a></small></i>
 * </blockquote>
 *
 * @author Johann Sorel (Geomatys)
 */
public final class GetDataStream extends RequestParameters {

    private String dataStreamId;

    /**
     * @see #dataStreamId(java.lang.String)
     */
    public String getDataStreamId() {
        return dataStreamId;
    }

    /**
     * @see #dataStreamId(java.lang.String)
     */
    public void setDataStreamId(String dataStreamId) {
        this.dataStreamId = dataStreamId;
    }

    /**
     * <blockquote><q>
     *     Local identifier of a Datastream
     *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part2/openapi/parameters/dataStreamId.yaml"> OGC API - Connected Systems - Part 2 (draft) - dataStreamId parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     *     <li>Non-empty string.</li>
     * </ul>
     */
    public GetDataStream dataStreamId(String dataStreamId) {
        setDataStreamId(dataStreamId);
        return this;
    }

}
