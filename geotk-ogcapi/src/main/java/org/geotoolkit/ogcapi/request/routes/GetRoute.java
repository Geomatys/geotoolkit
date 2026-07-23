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
package org.geotoolkit.ogcapi.request.routes;

import org.geotoolkit.ogcapi.request.RequestParameters;

/**
 * Request parameters shared by {@code GET /routes/{routeId}},
 * {@code GET /routes/{routeId}/definition} and {@code DELETE /routes/{routeId}}.
 *
 * @author Quentin BIALOTA (Geomatys)
 */
public final class GetRoute extends RequestParameters {

    private String routeId;

    public GetRoute routeId(String routeId) {
        this.routeId = routeId;
        return this;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }
}
