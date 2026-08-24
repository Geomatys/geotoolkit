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
 * Request parameters for retrieving the description of a process:
 * {@code GET /processes/{processId}}.
 *
 * <blockquote><q>
 *     The following section defines the requirements to retrieve metadata about a process.
 *     </q><i><small><a href="https://docs.ogc.org/is/18-062r2/18-062r2.html#sc_process_description"> OGC API - Processes - Part 1: Core - Retrieve a process description</a></small></i>
 * </blockquote>
 *
 * @author Quentin BIALOTA (Geomatys)
 */
public final class GetProcess extends RequestParameters {

    private String processId;

    /**
     * @see #processId(java.lang.String)
     */
    public String getProcessId() {
        return processId;
    }

    /**
     * @see #processId(java.lang.String)
     */
    public void setProcessId(String processId) {
        this.processId = processId;
    }

    /**
     * <blockquote><q>
     *     The server SHALL support the HTTP GET operation at the path /processes/{processID}.
     *     </q><i><small><a href="https://docs.ogc.org/is/18-062r2/18-062r2.html#sc_process_description"> OGC API - Processes - Part 1: Core - Requirement 13 /req/core/process</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     *     <li>Must match the {@code id} of an existing process (see {@code GET /processes}).</li>
     * </ul>
     */
    public GetProcess processId(String processId) {
        setProcessId(processId);
        return this;
    }
}
