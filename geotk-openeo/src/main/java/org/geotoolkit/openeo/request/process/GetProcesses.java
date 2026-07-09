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
 * Request parameters for GET /processes.
 *
 * <p>Lists all pre-defined processes available on the back-end, with optional namespace filter.</p>
 *
 * @author Quentin BIALOTA (Geomatys)
 */
public class GetProcesses extends RequestParameters {

    private Integer limit;
    private String namespace;

    /**
     * Maximum number of processes to return per page. (optional)
     */
    public Integer getLimit() {
        return limit;
    }

    /**
     * @see #getLimit()
     */
    public GetProcesses limit(Integer limit) {
        this.limit = limit;
        return this;
    }

    /**
     * Namespace to filter processes by; omit for the default back-end namespace. (optional)
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * @see #getNamespace()
     */
    public GetProcesses namespace(String namespace) {
        this.namespace = namespace;
        return this;
    }
}
