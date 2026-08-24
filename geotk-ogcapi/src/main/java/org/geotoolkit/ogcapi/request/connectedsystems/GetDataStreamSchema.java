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
 *     Get the observation schema for a given format.
 *     The type of observation schema returned depends on the observation encoding format selected using the `obsFormat` parameter.
 *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part2/openapi/paths/dataStreamSchemas.yaml"> OGC API - Connected Systems - Part 2: Dynamic Data (draft) - Get the observation schema</a></small></i>
 * </blockquote>
 *
 * @author Johann Sorel (Geomatys)
 */
public final class GetDataStreamSchema extends RequestParameters {

    private String dataStreamId;
    private String obsFormat;

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
    public GetDataStreamSchema dataStreamId(String dataStreamId) {
        setDataStreamId(dataStreamId);
        return this;
    }

    /**
     * @see #obsFormat(java.lang.String)
     */
    public String getObsFormat() {
        return obsFormat;
    }

    /**
     * @see #obsFormat(java.lang.String)
     */
    public void setObsFormat(String obsFormat) {
        this.obsFormat = obsFormat;
    }

    /**
     * <blockquote><q>
     *     Media type of the desired observation format
     *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part2/openapi/parameters/obsFormat.yaml"> OGC API - Connected Systems - Part 2 (draft) - obsFormat parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     * </ul>
     */
    public GetDataStreamSchema obsFormat(String obsFormat) {
        setObsFormat(obsFormat);
        return this;
    }

}
