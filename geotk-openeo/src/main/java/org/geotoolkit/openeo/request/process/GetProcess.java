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
 * Request parameters for GET /processes/{processId}.
 *
 * <p>Retrieves the definition of a single pre-defined process by its identifier.</p>
 *
 * @author Quentin BIALOTA (Geomatys)
 */
public class GetProcess extends RequestParameters {

    private String processId;
    private String namespace;

    /**
     * Unique identifier of the process. (required)
     */
    public String getProcessId() {
        return processId;
    }

    /**
     * @see #getProcessId()
     */
    public GetProcess processId(String processId) {
        this.processId = processId;
        return this;
    }

    /**
     * Namespace of the process; omit for the default back-end namespace. (optional)
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * @see #getNamespace()
     */
    public GetProcess namespace(String namespace) {
        this.namespace = namespace;
        return this;
    }
}
