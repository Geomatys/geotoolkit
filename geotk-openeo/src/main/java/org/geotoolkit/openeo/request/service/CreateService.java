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
import org.geotoolkit.openeo.dto.service.CreateServiceBody;

/**
 * Request parameters for POST /services.
 *
 * <p>Creates a new secondary web service backed by a process graph.</p>
 *
 * @author Quentin BIALOTA (Geomatys)
 */
public class CreateService extends RequestParameters {

    private CreateServiceBody body;

    /**
     * Service definition including type, process graph and optional configuration. (required)
     */
    public CreateServiceBody getBody() {
        return body;
    }

    /**
     * @see #getBody()
     */
    public CreateService body(CreateServiceBody body) {
        this.body = body;
        return this;
    }
}
