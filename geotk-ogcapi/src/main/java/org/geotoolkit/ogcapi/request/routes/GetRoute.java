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
 * <blockquote><q>
 *     This operation returns the route with id {@code routeId}. The route content is
 *     described by the "Route Exchange Model".
 *     </q><i><small><a href="https://docs.ogc.org/DRAFTS/21-000.html#get_route"> OGC API - Routes - Part 1: Core (draft) - Fetch a route</a></small></i>
 * </blockquote>
 *
 * @author Quentin BIALOTA (Geomatys)
 */
public final class GetRoute extends RequestParameters {

    private String routeId;

    /**
     * <blockquote><q>
     *     The server SHALL support the HTTP GET operation at the path {@code /routes/{routeId}}
     *     for each route referenced from the Routes resource at {@code /routes}.
     *     </q><i><small><a href="https://docs.ogc.org/DRAFTS/21-000.html#req_manage-routes_get-route-op"> OGC API - Routes - Part 1: Core (draft) - /req/manage-routes/get-route-op</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     *     <li>Identifies the route to fetch (or its definition) or delete, depending on the
     *     operation this request is used for.</li>
     * </ul>
     */
    public GetRoute routeId(String routeId) {
        this.routeId = routeId;
        return this;
    }

    /**
     * @see #routeId(java.lang.String)
     */
    public String getRouteId() {
        return routeId;
    }

    /**
     * @see #routeId(java.lang.String)
     */
    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }
}
