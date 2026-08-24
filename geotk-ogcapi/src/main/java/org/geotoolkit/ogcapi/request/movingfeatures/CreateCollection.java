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
 * To register a new moving feature collection: {@code POST /collections}. On success, the URI
 * of the new collection is returned in the response's {@code Location} header.
 *
 * <blockquote><q>
 *     The server SHALL support the HTTP POST operation at the resource endpoint
 *     ({root}/collections).
 *     </q><i><small><a href="https://docs.ogc.org/is/22-003r3/22-003r3.html#req_mfc-collections-op-post"> OGC API - Moving Features, Part 1: Core - Requirement 2 /req/mf-collection/collections-post</a></small></i>
 * </blockquote>
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class CreateCollection extends RequestParameters {

    private byte[] content;

    /**
     * @see #content(byte[])
     */
    public byte[] getContent() {
        return content;
    }

    /**
     * @see #content(byte[])
     */
    public void setContent(byte[] content) {
        this.content = content;
    }

    /**
     * <blockquote><q>
     *     The content of the request body SHALL be based upon the Collection request body
     *     schema.
     *     </q><i><small><a href="https://docs.ogc.org/is/22-003r3/22-003r3.html#req_mfc-collections-op-post"> OGC API - Moving Features, Part 1: Core - Requirement 2 /req/mf-collection/collections-post</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     *     <li>Raw {@code collection-body} JSON document to publish.</li>
     * </ul>
     */
    public CreateCollection content(byte[] content) {
        setContent(content);
        return this;
    }

}
