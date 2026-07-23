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
 * To replace the metadata ({@code collection-body} JSON document) of an existing moving feature
 * collection. Per the specification, the collection's {@code updateFrequency} property is not
 * updated by this operation.
 *
 * <p>OGC API - Moving Features, Part 1: Core ({@code replaceMetadata}).</p>
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class ReplaceCollection extends RequestParameters {

    private String collectionId;
    private byte[] content;

    /**
     * Local identifier of the collection. (required)
     *
     * @return the collectionId
     */
    public String getCollectionId() {
        return collectionId;
    }

    /**
     * @param collectionId the collectionId to set
     * @see #getCollectionId()
     */
    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    /**
     * @param collectionId the collectionId to set
     * @see #getCollectionId()
     */
    public ReplaceCollection collectionId(String collectionId) {
        setCollectionId(collectionId);
        return this;
    }

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
    public ReplaceCollection content(byte[] content) {
        setContent(content);
        return this;
    }

}
