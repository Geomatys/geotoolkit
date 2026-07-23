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
 * @author Quentin BIALOTA (Geomatys)
 */
public final class CreateRoute extends RequestParameters {

    private RouteDefinition body;

    public CreateRoute body(RouteDefinition body) {
        this.body = body;
        return this;
    }

    public RouteDefinition getBody() {
        return body;
    }

    public void setBody(RouteDefinition body) {
        this.body = body;
    }
}
