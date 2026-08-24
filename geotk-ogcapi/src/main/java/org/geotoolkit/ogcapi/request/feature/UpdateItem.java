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
package org.geotoolkit.ogcapi.request.feature;

import org.geotoolkit.ogcapi.request.PreferHandling;
import org.geotoolkit.ogcapi.request.RequestParameters;

/**
 * To update an existing feature with a partial document (RFC 7396 JSON Merge Patch). Only the
 * properties present in the patch document are changed.
 *
 * <blockquote><q>
 *     For every resource in a collection, the server SHALL support the HTTP PATCH
 *     operation.
 *     </q><i><small><a href="https://docs.ogc.org/DRAFTS/20-002r1.html#update"> OGC API - Features - Part 4: Create, Replace, Update and Delete (draft) - Modifying an existing resource (UPDATE)</a></small></i>
 * </blockquote>
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class UpdateItem extends RequestParameters {

    private String collectionId;
    private String itemId;
    private byte[] content;
    private Boolean dryRun;
    private PreferHandling handling;

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
     *     local identifier of a collection
     *     </q><i><small><a href="https://schemas.opengis.net/ogcapi/features/part1/1.0/openapi/parameters/collectionId.yaml"> OGC API - Features - Part 1: Core - collectionId parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     *     <li>Must match the {@code id} of an existing collection (see {@code GET /collections}).</li>
     * </ul>
     */
    public UpdateItem collectionId(String collectionId) {
        setCollectionId(collectionId);
        return this;
    }

    /**
     * @see #itemId(java.lang.String)
     */
    public String getItemId() {
        return itemId;
    }

    /**
     * @see #itemId(java.lang.String)
     */
    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    /**
     * <blockquote><q>
     *     local identifier of a feature
     *     </q><i><small><a href="https://schemas.opengis.net/ogcapi/features/part1/1.0/openapi/parameters/featureId.yaml"> OGC API - Features - Part 1: Core - featureId parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     *     <li>Local identifier of the feature to update.</li>
     * </ul>
     */
    public UpdateItem itemId(String itemId) {
        setItemId(itemId);
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
     *     The request body SHALL contain a document describing the specific parts of
     *     the target resource that should be modified by the operation.
     *     </q><i><small><a href="https://docs.ogc.org/DRAFTS/20-002r1.html#update"> OGC API - Features - Part 4: Create, Replace, Update and Delete (draft) - Modifying an existing resource (UPDATE)</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     *     <li>The RFC 7396 JSON Merge Patch document to apply, sent as
     *     {@code application/merge-patch+json}.</li>
     *     <li>Only the properties present in the patch document are changed.</li>
     *     <li>A resource identifier present in the body is ignored by the server.</li>
     * </ul>
     */
    public UpdateItem content(byte[] content) {
        setContent(content);
        return this;
    }

    /**
     * @see #dryRun(java.lang.Boolean)
     */
    public Boolean getDryRun() {
        return dryRun;
    }

    /**
     * @see #dryRun(java.lang.Boolean)
     */
    public void setDryRun(Boolean dryRun) {
        this.dryRun = dryRun;
    }

    /**
     * If {@code true}, the server validates and otherwise processes the request except that the
     * feature is not actually updated, returning a {@code 204} response on success.
     * <br>
     * <ul>
     *     <li>Optional; default {@code false}.</li>
     *     <li>Not defined by OGC API - Features - Part 4 (draft), which has no dry-run
     *     requirement at the time of writing; this is a server-specific extension (no verbatim
     *     spec text available for this field).</li>
     * </ul>
     */
    public UpdateItem dryRun(Boolean dryRun) {
        setDryRun(dryRun);
        return this;
    }

    /**
     * @see #handling(org.geotoolkit.ogcapi.request.PreferHandling)
     */
    public PreferHandling getHandling() {
        return handling;
    }

    /**
     * @see #handling(org.geotoolkit.ogcapi.request.PreferHandling)
     */
    public void setHandling(PreferHandling handling) {
        this.handling = handling;
    }

    /**
     * The {@code Prefer} header preference (RFC 7240 section 4.4) for feature validation.
     * <br>
     * <ul>
     *     <li>Optional; leave {@code null} to omit the {@code Prefer} header entirely.</li>
     *     <li>Not defined by OGC API - Features - Part 4 (draft), which does not mention the
     *     {@code Prefer} header at the time of writing; this is a server-specific extension
     *     (no verbatim spec text available for this field).</li>
     * </ul>
     */
    public UpdateItem handling(PreferHandling handling) {
        setHandling(handling);
        return this;
    }

}
