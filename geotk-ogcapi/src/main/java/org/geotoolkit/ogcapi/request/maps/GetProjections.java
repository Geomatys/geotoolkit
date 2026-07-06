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
 * @author Johann Sorel (Geomatys)
 */
public final class GetProjections extends RequestParameters {

    private String format;

    /**
     * The format of the response. If no value is provided, the accept header is used to determine the
     * format. Accepted values are 'json' or 'html'. (optional)
     *
     * @return the format
     */
    public String getFormat() {
        return format;
    }

    /**
     * @param format the format to set
     * @see #getFormat()
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * @param format the format to set
     * @see #getFormat()
     */
    public GetProjections format(String format) {
        setFormat(format);
        return this;
    }

}
