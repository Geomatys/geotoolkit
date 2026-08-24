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
 *     Get the command schema for a given format.
 *     The type of command schema returned depends on the command encoding format selected using the `commandFormat` parameter.
 *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part2/openapi/paths/controlStreamSchemas.yaml"> OGC API - Connected Systems - Part 2: Dynamic Data (draft) - Get the command schema</a></small></i>
 * </blockquote>
 *
 * @author Johann Sorel (Geomatys)
 */
public final class GetControlStreamSchema extends RequestParameters {

    private String controlStreamId;
    private String cmdFormat;

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
    public GetControlStreamSchema controlStreamId(String controlStreamId) {
        setControlStreamId(controlStreamId);
        return this;
    }

    /**
     * @see #cmdFormat(java.lang.String)
     */
    public String getCmdFormat() {
        return cmdFormat;
    }

    /**
     * @see #cmdFormat(java.lang.String)
     */
    public void setCmdFormat(String cmdFormat) {
        this.cmdFormat = cmdFormat;
    }

    /**
     * <blockquote><q>
     *     Media type of the desired command format
     *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part2/openapi/parameters/cmdFormat.yaml"> OGC API - Connected Systems - Part 2 (draft) - cmdFormat parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     * </ul>
     */
    public GetControlStreamSchema cmdFormat(String cmdFormat) {
        setCmdFormat(cmdFormat);
        return this;
    }

}
