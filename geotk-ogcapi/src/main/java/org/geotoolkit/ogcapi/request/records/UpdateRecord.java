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
 * To update an existing record with a partial document (RFC 7396 JSON Merge Patch). Only the
 * properties present in the patch document are changed.
 *
 * <p>OGC API - Records Part 3: Create, Replace, Update, Delete, Harvest (draft).</p>
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
     * Local identifier of the catalog. (required)
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
    public UpdateRecord collectionId(String collectionId) {
        setCollectionId(collectionId);
        return this;
    }

    /**
     * Local identifier of the record to update. (required)
     *
     * @return the recordId
     */
    public String getRecordId() {
        return recordId;
    }

    /**
     * @param recordId the recordId to set
     * @see #getRecordId()
     */
    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    /**
     * @param recordId the recordId to set
     * @see #getRecordId()
     */
    public UpdateRecord recordId(String recordId) {
        setRecordId(recordId);
        return this;
    }

    /**
     * The RFC 7396 JSON Merge Patch document to apply. (required)
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
    public UpdateRecord content(byte[] content) {
        setContent(content);
        return this;
    }

    /**
     * If {@code true}, the server validates and otherwise processes the request except that the
     * record is not actually updated, returning a {@code 204} response on success. (optional,
     * default to false)
     *
     * @return the dryRun
     */
    public Boolean getDryRun() {
        return dryRun;
    }

    /**
     * @param dryRun the dryRun to set
     * @see #getDryRun()
     */
    public void setDryRun(Boolean dryRun) {
        this.dryRun = dryRun;
    }

    /**
     * @param dryRun the dryRun to set
     * @see #getDryRun()
     */
    public UpdateRecord dryRun(Boolean dryRun) {
        setDryRun(dryRun);
        return this;
    }

    /**
     * The {@code Prefer} header preference (RFC 7240 section 4.4) for record validation. Leave
     * {@code null} to omit the {@code Prefer} header entirely. (optional)
     *
     * @return the handling
     */
    public PreferHandling getHandling() {
        return handling;
    }

    /**
     * @param handling the handling to set
     * @see #getHandling()
     */
    public void setHandling(PreferHandling handling) {
        this.handling = handling;
    }

    /**
     * @param handling the handling to set
     * @see #getHandling()
     */
    public UpdateRecord handling(PreferHandling handling) {
        setHandling(handling);
        return this;
    }

}
