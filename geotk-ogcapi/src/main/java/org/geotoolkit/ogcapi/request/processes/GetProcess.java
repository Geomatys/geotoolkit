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
package org.geotoolkit.ogcapi.request.processes;

import org.geotoolkit.ogcapi.request.RequestParameters;

/**
 *
 * @author Quentin BIALOTA (Geomatys)
 */
public final class GetProcess extends RequestParameters {

    private String processId;

    /**
     * Identifier of the process (required)
     */
    public String getProcessId() {
        return processId;
    }

    /**
     * @see #getProcessId()
     */
    public void setProcessId(String processId) {
        this.processId = processId;
    }

    /**
     * @see #getProcessId()
     */
    public GetProcess processId(String processId) {
        setProcessId(processId);
        return this;
    }
}
