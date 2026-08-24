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
 * To replace the metadata of an existing moving feature collection:
 * {@code PUT /collections/{collectionId}}.
 *
 * <blockquote><q>
 *     For every resource in the collection, the server SHALL support the HTTP PUT
 *     operation.
 *     </q><i><small><a href="https://docs.ogc.org/is/22-003r3/22-003r3.html#req_mfc-collection-op-put"> OGC API - Moving Features, Part 1: Core - Requirement 7 /req/mf-collection/collection-put</a></small></i>
 * </blockquote>
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class ReplaceCollection extends RequestParameters {

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
     *     <li>Must match the {@code id} of an existing collection.</li>
     * </ul>
     */
    public ReplaceCollection collectionId(String collectionId) {
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
     *     The content of the request body SHALL be based upon the Collection request body
     *     schema, except updateFrequency. If the updateFrequency is included in the request
     *     body, the server SHALL ignore it.
     *     </q><i><small><a href="https://docs.ogc.org/is/22-003r3/22-003r3.html#req_mfc-collection-op-put"> OGC API - Moving Features, Part 1: Core - Requirement 7 /req/mf-collection/collection-put</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     *     <li>Raw {@code collection-body} JSON document to publish; the {@code updateFrequency}
     *     property, if present, is ignored by the server.</li>
     * </ul>
     */
    public ReplaceCollection content(byte[] content) {
        setContent(content);
        return this;
    }

}
