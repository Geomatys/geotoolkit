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
package org.geotoolkit.openeo.request.service;

import org.geotoolkit.ogcapi.request.RequestParameters;

/**
 * Request parameters for GET /services.
 *
 * <p>Lists all secondary web services (WMS, OGC API, etc.) owned by the authenticated user.</p>
 *
 * @author Quentin BIALOTA (Geomatys)
 */
public class GetServices extends RequestParameters {

    private Integer limit;

    /**
     * Maximum number of services to return per page. (optional)
     */
    public Integer getLimit() {
        return limit;
    }

    /**
     * @see #getLimit()
     */
    public GetServices limit(Integer limit) {
        this.limit = limit;
        return this;
    }
}
