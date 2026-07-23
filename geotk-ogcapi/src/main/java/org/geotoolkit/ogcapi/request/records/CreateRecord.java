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
 * To create a new record in a catalog. On success, the URI of the new record is returned in the
 * response's {@code Location} header.
 *
 * <p>OGC API - Records Part 3: Create, Replace, Update, Delete, Harvest (draft).</p>
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class CreateRecord extends RequestParameters {

    private String collectionId;
    private byte[] content;
    private String contentType;
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
    public CreateRecord collectionId(String collectionId) {
        setCollectionId(collectionId);
        return this;
    }

    /**
     * The raw record document to publish (e.g. a GeoJSON Feature). (required)
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
    public CreateRecord content(byte[] content) {
        setContent(content);
        return this;
    }

    /**
     * The media type of the record encoding, sent as the {@code Content-Type} header (e.g.
     * {@code application/geo+json}). (required)
     *
     * @return the contentType
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * @param contentType the contentType to set
     * @see #getContentType()
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * @param contentType the contentType to set
     * @see #getContentType()
     */
    public CreateRecord contentType(String contentType) {
        setContentType(contentType);
        return this;
    }

    /**
     * If {@code true}, the server validates and otherwise processes the request except that no
     * record is actually created, returning a {@code 204} response on success. (optional,
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
    public CreateRecord dryRun(Boolean dryRun) {
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
    public CreateRecord handling(PreferHandling handling) {
        setHandling(handling);
        return this;
    }

}
