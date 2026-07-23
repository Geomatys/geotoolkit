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
package org.geotoolkit.ogcapi.request.movingfeatures;

import org.geotoolkit.ogcapi.request.RequestParameters;

/**
 * To register a new moving feature collection ({@code collection-body} JSON document). On
 * success, the URI of the new collection is returned in the response's {@code Location} header.
 *
 * <p>OGC API - Moving Features, Part 1: Core ({@code registerMetadata}).</p>
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class CreateCollection extends RequestParameters {

    private byte[] content;

    /**
     * The raw {@code collection-body} JSON document to publish. (required)
     *
     * @return the content
     */
    public byte[] getContent() {
        return content;
    }

    /**
     * @param content the content to set
     * @see #getContent()
     */
    public void setContent(byte[] content) {
        this.content = content;
    }

    /**
     * @param content the content to set
     * @see #getContent()
     */
    public CreateCollection content(byte[] content) {
        setContent(content);
        return this;
    }

}
