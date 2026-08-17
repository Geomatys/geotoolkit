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
 * To append a temporal primitive geometry to a moving feature's trajectory. On success, the URI
 * of the new resource is returned in the response's {@code Location} header.
 *
 * <blockquote><q>A create operation posts a new TemporalPrimitiveGeometry resource to the
 * MovingFeature that is specified by {mFeatureId}.</q>
 * <i><small><a
 * href="https://docs.ogc.org/is/22-003r3/22-003r3.html#resource-temporalGeometrySequence-section">OGC
 * API - Moving Features - Part 1: Core - 8.5.1. Overview</a></small></i></blockquote>
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class InsertTemporalGeometry extends RequestParameters {

    private String collectionId;
    private String mFeatureId;
    private byte[] content;

    /**
     * @return the collectionId
     * @see #collectionId(java.lang.String)
     */
    public String getCollectionId() {
        return collectionId;
    }

    /**
     * @param collectionId the collectionId to set
     * @see #collectionId(java.lang.String)
     */
    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    /**
     * Local identifier of the collection to which the moving feature belongs.
     *
     * <ul>
     *     <li>Required. Corresponds to the {@code collectionId} path parameter.</li>
     * </ul>
     *
     * @param collectionId the collectionId to set
     * @return this
     */
    public InsertTemporalGeometry collectionId(String collectionId) {
        setCollectionId(collectionId);
        return this;
    }

    /**
     * @return the mFeatureId
     * @see #mFeatureId(java.lang.String)
     */
    public String getMFeatureId() {
        return mFeatureId;
    }

    /**
     * @param mFeatureId the mFeatureId to set
     * @see #mFeatureId(java.lang.String)
     */
    public void setMFeatureId(String mFeatureId) {
        this.mFeatureId = mFeatureId;
    }

    /**
     * Local identifier of the moving feature that the new temporal primitive geometry is
     * appended to (the {@code {mFeatureId}} referenced by the class-level quote).
     *
     * <ul>
     *     <li>Required. Corresponds to the {@code mFeatureId} path parameter.</li>
     * </ul>
     *
     * @param mFeatureId the mFeatureId to set
     * @return this
     */
    public InsertTemporalGeometry mFeatureId(String mFeatureId) {
        setMFeatureId(mFeatureId);
        return this;
    }

    /**
     * @return the content
     * @see #content(byte[])
     */
    public byte[] getContent() {
        return content;
    }

    /**
     * @param content the content to set
     * @see #content(byte[])
     */
    public void setContent(byte[] content) {
        this.content = content;
    }

    /**
     * The raw {@code temporalPrimitiveGeometry} JSON document to publish.
     *
     * <ul>
     *     <li>Required. This is the HTTP POST request body; no isolated verbatim spec sentence
     *     describes the body's bytes independently of the {@code TemporalPrimitiveGeometry}
     *     schema referenced by the class-level quote.</li>
     * </ul>
     *
     * @param content the content to set
     * @return this
     */
    public InsertTemporalGeometry content(byte[] content) {
        setContent(content);
        return this;
    }

}
