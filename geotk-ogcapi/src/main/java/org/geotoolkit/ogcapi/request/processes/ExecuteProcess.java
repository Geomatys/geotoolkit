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

import org.geotoolkit.ogcapi.dto.processes.Execute;
import org.geotoolkit.ogcapi.request.RequestParameters;

/**
 * Request parameters for executing a process:
 * {@code POST /processes/{processId}/execution}.
 *
 * <blockquote><q>
 *     This section describes the requirements for executing a process.
 *     </q><i><small><a href="https://docs.ogc.org/is/18-062r2/18-062r2.html#sc_create_job"> OGC API - Processes - Part 1: Core - Execute a process</a></small></i>
 * </blockquote>
 *
 * @author Quentin BIALOTA (Geomatys)
 */
public final class ExecuteProcess extends RequestParameters {

    private String processId;
    private Execute body;

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
     *     The server SHALL support the HTTP POST operation at the path
     *     /processes/{processID}/execution.
     *     </q><i><small><a href="https://docs.ogc.org/is/18-062r2/18-062r2.html#sc_create_job"> OGC API - Processes - Part 1: Core - Requirement 16 /req/core/process-execute-op</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     *     <li>Must match the {@code id} of an existing process (see {@code GET /processes}).</li>
     * </ul>
     */
    public ExecuteProcess processId(String processId) {
        setProcessId(processId);
        return this;
    }

    /**
     * @see #body(org.geotoolkit.ogcapi.dto.processes.Execute)
     */
    public Execute getBody() {
        return body;
    }

    /**
     * @see #body(org.geotoolkit.ogcapi.dto.processes.Execute)
     */
    public void setBody(Execute body) {
        this.body = body;
    }

    /**
     * <blockquote><q>
     *     The content of a request the request body SHALL be based upon the OpenAPI 3.0
     *     schema execute.yaml.
     *     </q><i><small><a href="https://docs.ogc.org/is/18-062r2/18-062r2.html#sc_create_job"> OGC API - Processes - Part 1: Core - Requirement 17 /req/core/process-execute-request</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     *     <li>Carries the process inputs, requested outputs and response mode for the execution.</li>
     * </ul>
     */
    public ExecuteProcess body(Execute body) {
        setBody(body);
        return this;
    }
}
