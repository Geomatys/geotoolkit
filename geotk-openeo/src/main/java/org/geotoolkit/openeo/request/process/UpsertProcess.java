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
package org.geotoolkit.openeo.request.process;

import org.geotoolkit.openeo.dto.process.Process;
import org.geotoolkit.ogcapi.request.RequestParameters;

/**
 * Request parameters for PUT /processes/{processId}.
 *
 * <p>Stores or replaces a user-defined process on the back-end (PUT /processes/{processId}).</p>
 *
 * @author Quentin BIALOTA (Geomatys)
 */
public class UpsertProcess extends RequestParameters {

    private String processId;
    private Process body;

    /**
     * Unique identifier of the process to store or update. (required)
     */
    public String getProcessId() {
        return processId;
    }

    /**
     * @see #getProcessId()
     */
    public UpsertProcess processId(String processId) {
        this.processId = processId;
        return this;
    }

    /**
     * Process definition to store or replace on the back-end. (required)
     */
    public Process getBody() {
        return body;
    }

    /**
     * @see #getBody()
     */
    public UpsertProcess body(Process body) {
        this.body = body;
        return this;
    }
}
