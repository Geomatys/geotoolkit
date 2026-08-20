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

import org.geotoolkit.ogcapi.dto.routes.RouteDefinition;
import org.geotoolkit.ogcapi.request.RequestParameters;

/**
 * Request parameters for {@code POST /routes}.
 *
 * <blockquote><q>
 *     This operation creates a new route. The payload of the request specifies the definition
 *     of the new route.
 *     </q><i><small><a href="https://docs.ogc.org/DRAFTS/21-000.html#compute_route"> OGC API - Routes - Part 1: Core (draft) - Compute a new route</a></small></i>
 * </blockquote>
 *
 * @author Quentin BIALOTA (Geomatys)
 */
public final class CreateRoute extends RequestParameters {

    private RouteDefinition body;

    /**
     * <blockquote><q>
     *     The server SHALL accept a route definition in the content of the request based upon
     *     the following OpenAPI 3.0 schema:
     *     </q><i><small><a href="https://docs.ogc.org/DRAFTS/21-000.html#req_core_compute-route-op"> OGC API - Routes - Part 1: Core (draft) - /req/core/compute-route-op</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     *     <li>{@code inputs.waypoints} (start and end point, as a two-point {@code MultiPoint})
     *     is required; {@code inputs.name} and {@code inputs.mode} are optional.</li>
     *     <li>Additional members in the route definition can be ignored by the server.</li>
     * </ul>
     */
    public CreateRoute body(RouteDefinition body) {
        this.body = body;
        return this;
    }

    /**
     * @see #body(org.geotoolkit.ogcapi.dto.routes.RouteDefinition)
     */
    public RouteDefinition getBody() {
        return body;
    }

    /**
     * @see #body(org.geotoolkit.ogcapi.dto.routes.RouteDefinition)
     */
    public void setBody(RouteDefinition body) {
        this.body = body;
    }
}
