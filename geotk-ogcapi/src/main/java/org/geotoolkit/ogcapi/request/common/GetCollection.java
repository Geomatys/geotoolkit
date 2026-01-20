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
public final class GetCollection extends RequestParameters {

    private String collectionId;
    private String format;

    /**
     * Local identifier of a collection (required)
     */
    public String getCollectionId() {
        return collectionId;
    }

    /**
     * @see #getCollectionId()
     */
    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    /**
     * @see #getCollectionId()
     */
    public GetCollection collectionId(String collectionId) {
        setCollectionId(collectionId);
        return this;
    }

    /**
     * The format of the response. If no value is provided, the accept header is used to determine the format.
     * Accepted values are &#39;json&#39; or &#39;html&#39;. (optional)
     */
    public String getFormat() {
        return format;
    }

    /**
     * @see #getFormat()
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * @see #getFormat()
     */
    public GetCollection format(String format) {
        setFormat(format);
        return this;
    }
}
