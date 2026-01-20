/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
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
package org.geotoolkit.ogcapi.request.common;

import org.geotoolkit.ogcapi.request.RequestParameters;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class GetLandingPage extends RequestParameters {

    private String format;

    /**
     * The optional f parameter indicates the output format that the
     * server shall provide as part of the response document. The default format
     * is JSON. (optional, default to json)
     *
     * @return the format
     */
    public String getFormat() {
        return format;
    }

    /**
     * @param format the format to set
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * @see #getFormat()
     */
    public GetLandingPage format(String format) {
        setFormat(format);
        return this;
    }
}
