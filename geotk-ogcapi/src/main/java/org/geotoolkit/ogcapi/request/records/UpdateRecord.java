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
package org.geotoolkit.ogcapi.request.records;

import org.geotoolkit.ogcapi.request.PreferHandling;
import org.geotoolkit.ogcapi.request.RequestParameters;

/**
 * <blockquote><q>
 *     For records, the resource endpoints SHALL be URIs specified by the URI template
 *     {landingPageUri}/collections/{catalogId}/items/{recordId}.
 *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-records/blob/master/extensions/transactions/create-replace-update-delete-harvest/standard/requirements/records/REQ_resource-endpoint.adoc"> OGC API - Records - Part 3: Create, Replace, Update, Delete, Harvest (draft) - Resource endpoint</a></small></i>
 * </blockquote>
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class UpdateRecord extends RequestParameters {

    private String collectionId;
    private String recordId;
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
    public UpdateRecord collectionId(String collectionId) {
        setCollectionId(collectionId);
        return this;
    }

    /**
     * @see #recordId(java.lang.String)
     */
    public String getRecordId() {
        return recordId;
    }

    /**
     * @see #recordId(java.lang.String)
     */
    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    /**
     * <blockquote><q>
     *     The parameter record SHALL be the id property of a record obtained by
     *     previously having queried the Records resource of the collection.
     *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-records/blob/master/extensions/transactions/create-replace-update-delete-harvest/standard/requirements/records/REQ_resource-endpoint.adoc"> OGC API - Records - Part 3: Create, Replace, Update, Delete, Harvest (draft) - Resource endpoint</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     * </ul>
     */
    public UpdateRecord recordId(String recordId) {
        setRecordId(recordId);
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
     *     A merge-patch request SHALL be processed as specified by RFC 7396, "JSON
     *     Merge Patch".
     *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-records/blob/master/extensions/transactions/create-replace-update-delete-harvest/standard/requirements/records/REQ_update-json-merge-patch.adoc"> OGC API - Records - Part 3: Create, Replace, Update, Delete, Harvest (draft) - JSON Merge Patch update</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     *     <li>The raw JSON Merge Patch document (RFC 7396) describing the changes to
     *     apply to the record identified by {@link #recordId(java.lang.String) recordId}.</li>
     * </ul>
     */
    public UpdateRecord content(byte[] content) {
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
     * record is not actually updated, returning a {@code 204} response on success.
     * <br>
     * <ul>
     *     <li>Optional; default {@code false}.</li>
     *     <li>Not defined by OGC API - Records - Part 3 (draft), which has no dry-run
     *     requirement at the time of writing; this is a server-specific extension (no verbatim
     *     spec text available for this field).</li>
     * </ul>
     */
    public UpdateRecord dryRun(Boolean dryRun) {
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
     * The {@code Prefer} header preference (RFC 7240 section 4.4) for record validation.
     * <br>
     * <ul>
     *     <li>Optional; leave {@code null} to omit the {@code Prefer} header entirely.</li>
     *     <li>Not defined by OGC API - Records - Part 3 (draft), which does not mention the
     *     {@code Prefer} header at the time of writing; this is a server-specific extension
     *     (no verbatim spec text available for this field).</li>
     * </ul>
     */
    public UpdateRecord handling(PreferHandling handling) {
        setHandling(handling);
        return this;
    }

}
