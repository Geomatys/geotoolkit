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

import org.geotoolkit.ogcapi.request.RequestParameters;

/**
 * Request parameters for POST /result.
 *
 * <p>Executes a process graph synchronously and streams back the result file.
 * Suited for short computations; use batch jobs for long-running processes.</p>
 *
 * @author Quentin BIALOTA (Geomatys)
 */
public class ExecuteSync extends RequestParameters {

    private Object body;

    /**
     * Process graph to execute. (required)
     */
    public Object getBody() {
        return body;
    }

    /**
     * @see #getBody()
     */
    public ExecuteSync body(Object body) {
        this.body = body;
        return this;
    }
}
