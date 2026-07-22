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
package org.geotoolkit.ogcapi.request;

/**
 * The {@code Prefer} header preferences defined by RFC 7240 section 4.4 for validation on
 * create/replace/update operations.
 *
 * @author Johann Sorel (Geomatys)
 */
public enum PreferHandling {
    /**
     * The server validates the submitted document for conformance with its encoding, returning
     * a {@code 400} response if an error is identified.
     */
    STRICT("handling=strict"),
    /**
     * The server does not validate the submitted document for conformance with its encoding.
     */
    LENIENT("handling=lenient");

    public final String headerValue;

    private PreferHandling(String headerValue) {
        this.headerValue = headerValue;
    }
}
