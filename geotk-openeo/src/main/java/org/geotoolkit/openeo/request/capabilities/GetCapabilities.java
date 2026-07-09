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
package org.geotoolkit.openeo.request.capabilities;

import org.geotoolkit.ogcapi.request.RequestParameters;

/**
 * Request parameters for GET / (OpenEO capabilities / landing page).
 *
 * <p>Returns information about the back-end: version, available endpoints, billing plans and links.</p>
 *
 * @author Quentin BIALOTA (Geomatys)
 */
public class GetCapabilities extends RequestParameters {

    private String format;

    /**
     * Requested response format ({@code f} query parameter). Defaults to {@code application/json} if omitted. (optional)
     */
    public String getFormat() {
        return format;
    }

    /**
     * @see #getFormat()
     */
    public GetCapabilities format(String format) {
        this.format = format;
        return this;
    }
}
