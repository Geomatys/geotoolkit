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
 * The {@code DELETE} operation on {@code /controlstreams/{controlStreamId}}: deletes a
 * ControlStream resource.
 *
 * @see <a href="https://docs.ogc.org/is/23-002/23-002.html">OGC API - Connected Systems - Part 2</a>
 * @author Johann Sorel (Geomatys)
 */
public final class DeleteControlStream extends RequestParameters {

    private String controlStreamId;
    private Boolean cascade;

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
    public DeleteControlStream controlStreamId(String controlStreamId) {
        setControlStreamId(controlStreamId);
        return this;
    }

    /**
     * If {@code true}, related resources are deleted as well. (optional)
     *
     * @return the cascade
     */
    public Boolean getCascade() {
        return cascade;
    }

    /**
     * @param cascade the cascade to set
     * @see #getCascade()
     */
    public void setCascade(Boolean cascade) {
        this.cascade = cascade;
    }

    /**
     * @param cascade the cascade to set
     * @see #getCascade()
     */
    public DeleteControlStream cascade(Boolean cascade) {
        setCascade(cascade);
        return this;
    }

}
