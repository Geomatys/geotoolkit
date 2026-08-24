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
package org.geotoolkit.ogcapi.request.maps;

import org.geotoolkit.ogcapi.request.RequestParameters;

/**
 * To retrieve the list of custom CRS projection operation methods and datums supported for map
 * retrieval operations.
 *
 * <blockquote><q>
 *     The resource at /projectionsAndDatums supports a GET operation, with JSON as an available
 *     representation, returning a list of the supported projection operation methods and their
 *     associated parameters, as well as a list of available datums.
 *     </q><i><small><a href="https://docs.ogc.org/is/20-058/20-058.html#_b8f30122-e54f-4bd7-9916-788100c8c88d"> OGC API - Maps - Part 1: Core - 18.3.1. Available projections operation</a></small></i>
 * </blockquote>
 *
 * @author Johann Sorel (Geomatys)
 */
public final class GetProjections extends RequestParameters {

    private String format;

    /**
     * @return the format
     * @see #format(java.lang.String)
     */
    public String getFormat() {
        return format;
    }

    /**
     * @param format the format to set
     * @see #format(java.lang.String)
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * <ul>
     *     <li>Optional.</li>
     *     <li>If no value is provided, the accept header is used to determine the format.</li>
     *     <li>A GET operation at {@code /projectionsAndDatums} providing at minimum a JSON
     *     representation is required by the spec (Requirement 44.A); an {@code html} alternate
     *     representation, if offered, is not itself defined by a verbatim spec sentence.</li>
     * </ul>
     *
     * @param format the format to set
     */
    public GetProjections format(String format) {
        setFormat(format);
        return this;
    }

}
