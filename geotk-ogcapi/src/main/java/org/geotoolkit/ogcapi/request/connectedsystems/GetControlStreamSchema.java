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
 * The {@code GET} operation on {@code /controlstreams/{controlStreamId}/schema}: retrieves the
 * command/parameter schema of a ControlStream, encoded in the given command format.
 *
 * @see <a href="https://docs.ogc.org/is/23-002/23-002.html">OGC API - Connected Systems - Part 2</a>
 * @author Johann Sorel (Geomatys)
 */
public final class GetControlStreamSchema extends RequestParameters {

    private String controlStreamId;
    private String cmdFormat;

    /**
     * Local identifier of the control stream. (required)
     *
     * @return the controlStreamId
     */
    public String getControlStreamId() {
        return controlStreamId;
    }

    /**
     * @param controlStreamId the controlStreamId to set
     * @see #getControlStreamId()
     */
    public void setControlStreamId(String controlStreamId) {
        this.controlStreamId = controlStreamId;
    }

    /**
     * @param controlStreamId the controlStreamId to set
     * @see #getControlStreamId()
     */
    public GetControlStreamSchema controlStreamId(String controlStreamId) {
        setControlStreamId(controlStreamId);
        return this;
    }

    /**
     * The command format (media type) for which the schema is requested. (optional)
     *
     * @return the cmdFormat
     */
    public String getCmdFormat() {
        return cmdFormat;
    }

    /**
     * @param cmdFormat the cmdFormat to set
     * @see #getCmdFormat()
     */
    public void setCmdFormat(String cmdFormat) {
        this.cmdFormat = cmdFormat;
    }

    /**
     * @param cmdFormat the cmdFormat to set
     * @see #getCmdFormat()
     */
    public GetControlStreamSchema cmdFormat(String cmdFormat) {
        setCmdFormat(cmdFormat);
        return this;
    }

}
