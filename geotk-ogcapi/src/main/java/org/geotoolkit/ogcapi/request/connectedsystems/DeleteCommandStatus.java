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
 * The {@code DELETE} operation on {@code /commands/{cmdId}/status/{statusId}}: deletes a status
 * report of a Command.
 *
 * @see <a href="https://docs.ogc.org/is/23-002/23-002.html">OGC API - Connected Systems - Part 2</a>
 * @author Johann Sorel (Geomatys)
 */
public final class DeleteCommandStatus extends RequestParameters {

    private String cmdId;
    private String statusId;

    /**
     * Local identifier of the command. (required)
     *
     * @return the cmdId
     */
    public String getCmdId() {
        return cmdId;
    }

    /**
     * @param cmdId the cmdId to set
     * @see #getCmdId()
     */
    public void setCmdId(String cmdId) {
        this.cmdId = cmdId;
    }

    /**
     * @param cmdId the cmdId to set
     * @see #getCmdId()
     */
    public DeleteCommandStatus cmdId(String cmdId) {
        setCmdId(cmdId);
        return this;
    }

    /**
     * Local identifier of the command status report. (required)
     *
     * @return the statusId
     */
    public String getStatusId() {
        return statusId;
    }

    /**
     * @param statusId the statusId to set
     * @see #getStatusId()
     */
    public void setStatusId(String statusId) {
        this.statusId = statusId;
    }

    /**
     * @param statusId the statusId to set
     * @see #getStatusId()
     */
    public DeleteCommandStatus statusId(String statusId) {
        setStatusId(statusId);
        return this;
    }

}
