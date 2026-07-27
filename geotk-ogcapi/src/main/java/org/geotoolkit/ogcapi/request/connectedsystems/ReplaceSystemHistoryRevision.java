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
package org.geotoolkit.ogcapi.request.connectedsystems;

import org.geotoolkit.ogcapi.request.RequestParameters;

/**
 * The {@code PUT} operation on {@code /systems/{systemId}/history/{revId}}: replaces an existing
 * historical revision of a System.
 *
 * @see <a href="https://docs.ogc.org/is/23-002/23-002.html">OGC API - Connected Systems - Part 2</a>
 * @author Johann Sorel (Geomatys)
 */
public final class ReplaceSystemHistoryRevision extends RequestParameters {

    private String systemId;
    private String revId;
    private byte[] content;
    private String contentType;

    /**
     * Local identifier of the system. (required)
     *
     * @return the systemId
     */
    public String getSystemId() {
        return systemId;
    }

    /**
     * @param systemId the systemId to set
     * @see #getSystemId()
     */
    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    /**
     * @param systemId the systemId to set
     * @see #getSystemId()
     */
    public ReplaceSystemHistoryRevision systemId(String systemId) {
        setSystemId(systemId);
        return this;
    }

    /**
     * Identifier of the historical revision. (required)
     *
     * @return the revId
     */
    public String getRevId() {
        return revId;
    }

    /**
     * @param revId the revId to set
     * @see #getRevId()
     */
    public void setRevId(String revId) {
        this.revId = revId;
    }

    /**
     * @param revId the revId to set
     * @see #getRevId()
     */
    public ReplaceSystemHistoryRevision revId(String revId) {
        setRevId(revId);
        return this;
    }

    /**
     * The raw document that replaces the existing one. (required)
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
    public ReplaceSystemHistoryRevision content(byte[] content) {
        setContent(content);
        return this;
    }

    /**
     * The media type of the document encoding, sent as the {@code Content-Type} header. (required)
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
    public ReplaceSystemHistoryRevision contentType(String contentType) {
        setContentType(contentType);
        return this;
    }

}
