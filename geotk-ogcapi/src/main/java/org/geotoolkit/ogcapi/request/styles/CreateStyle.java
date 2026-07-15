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
package org.geotoolkit.ogcapi.request.styles;

import org.geotoolkit.ogcapi.request.RequestParameters;

/**
 * To publish a new style. On success, the URI of the new style is returned in the response's
 * {@code Location} header.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class CreateStyle extends RequestParameters {

    private String collectionId;
    private byte[] content;
    private String contentType;
    private Boolean dryRun;
    private PreferHandling handling;

    /**
     * Local identifier of a collection. Set to publish a style for a single collection
     * ({@code /collections/{collectionId}/styles}). Leave {@code null} to publish a style for the
     * whole dataset ({@code /styles}). (optional)
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
    public CreateStyle collectionId(String collectionId) {
        setCollectionId(collectionId);
        return this;
    }

    /**
     * The raw stylesheet document to publish. (required)
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
    public CreateStyle content(byte[] content) {
        setContent(content);
        return this;
    }

    /**
     * The media type of the stylesheet encoding, sent as the {@code Content-Type} header (e.g.
     * {@code application/vnd.mapbox.style+json}, {@code application/vnd.ogc.sld+xml;version=1.0}).
     * (required)
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
    public CreateStyle contentType(String contentType) {
        setContentType(contentType);
        return this;
    }

    /**
     * If {@code true}, the server validates and otherwise processes the request except that no style
     * is actually created, returning a {@code 204} response on success. (optional, default to false)
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
    public CreateStyle dryRun(Boolean dryRun) {
        setDryRun(dryRun);
        return this;
    }

    /**
     * The {@code Prefer} header preference (RFC 7240 section 4.4) for stylesheet validation. Leave
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
    public CreateStyle handling(PreferHandling handling) {
        setHandling(handling);
        return this;
    }

}
