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
 * Request parameters for GET /process_graphs.
 *
 * <p>Lists all user-defined process graphs stored on the back-end.</p>
 *
 * @author Quentin BIALOTA (Geomatys)
 */
public class GetProcessGraphs extends RequestParameters {

    private Integer limit;

    /**
     * Maximum number of process graphs to return per page. (optional)
     */
    public Integer getLimit() {
        return limit;
    }

    /**
     * @see #getLimit()
     */
    public GetProcessGraphs limit(Integer limit) {
        this.limit = limit;
        return this;
    }
}
