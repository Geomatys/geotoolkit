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
 * To insert one or more moving features into a collection:
 * {@code POST /collections/{collectionId}/items}. On success, the URI of the new
 * resource(s) is returned in the response's {@code Location} header.
 *
 * <blockquote><q>
 *     The server SHALL support the HTTP POST operation at the resource
 *     endpoint({root}/collections/{collectionId}/items).
 *     </q><i><small><a href="https://docs.ogc.org/is/22-003r3/22-003r3.html#req_mf-features-op-post"> OGC API - Moving Features, Part 1: Core - Requirement 15 /req/movingfeatures/features-post</a></small></i>
 * </blockquote>
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class InsertMovingFeatures extends RequestParameters {

    private String collectionId;
    private byte[] content;

    /**
     * @see #collectionId(java.lang.String)
     */
    public String getCollectionId() {
        return collectionId;
    }

    /**
     * @see #collectionId(java.lang.String)
     */
    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    /**
     * <blockquote><q>
     *     An identifier for a specific Collection of data
     *     </q><i><small><a href="https://docs.ogc.org/is/22-003r3/22-003r3.html#_6a53f5bb-7088-4fef-b4c3-ab65f8d40189"> OGC API - Moving Features, Part 1: Core - 6.1. General</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     * </ul>
     */
    public InsertMovingFeatures collectionId(String collectionId) {
        setCollectionId(collectionId);
        return this;
    }

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
     *     The content of the request body SHALL be based upon the MovingFeature object and
     *     MovingFeatureCollection object in OGC Moving Features JSON Encoding Standard.
     *     </q><i><small><a href="https://docs.ogc.org/is/22-003r3/22-003r3.html#req_mf-features-op-post"> OGC API - Moving Features, Part 1: Core - Requirement 15 /req/movingfeatures/features-post</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     *     <li>A single {@code movingFeature-mfjson} document, or a {@code movingFeatureCollection}
     *     document to insert several moving features at once.</li>
     * </ul>
     */
    public InsertMovingFeatures content(byte[] content) {
        setContent(content);
        return this;
    }

}
