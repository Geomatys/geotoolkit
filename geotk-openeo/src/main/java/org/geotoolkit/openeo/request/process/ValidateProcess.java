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
 * Request parameters for POST /validation.
 *
 * <p>Validates a process graph without executing it. Returns a list of errors, or an empty list if valid.</p>
 *
 * @author Quentin BIALOTA (Geomatys)
 */
public class ValidateProcess extends RequestParameters {

    private Object body;

    /**
     * Process graph definition to validate. (required)
     */
    public Object getBody() {
        return body;
    }

    /**
     * @see #getBody()
     */
    public ValidateProcess body(Object body) {
        this.body = body;
        return this;
    }
}
