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
 * <blockquote><q>
 *     This will completely replace a specific revision of a system description. The server will
 *     reject the update if the `validTime` property of the provided system description is different
 *     from the original.
 *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part2/openapi/paths/systemHistoryById.yaml"> OGC API - Connected Systems - Part 2 (draft) - Update a system description revision</a></small></i>
 * </blockquote>
 *
 * @author Johann Sorel (Geomatys)
 */
public final class ReplaceSystemHistoryRevision extends RequestParameters {

    private String systemId;
    private String revId;
    private byte[] content;
    private String contentType;

    /**
     * @see #systemId(java.lang.String)
     */
    public String getSystemId() {
        return systemId;
    }

    /**
     * @see #systemId(java.lang.String)
     */
    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    /**
     * <blockquote><q>
     *     Local identifier of a System
     *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part1/openapi/parameters/systemId.yaml"> OGC API - Connected Systems - Part 1 (draft) - systemId parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     *     <li>Non-empty string.</li>
     * </ul>
     */
    public ReplaceSystemHistoryRevision systemId(String systemId) {
        setSystemId(systemId);
        return this;
    }

    /**
     * @see #revId(java.lang.String)
     */
    public String getRevId() {
        return revId;
    }

    /**
     * @see #revId(java.lang.String)
     */
    public void setRevId(String revId) {
        this.revId = revId;
    }

    /**
     * <blockquote><q>
     *     Local identifier of a historical revision
     *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part2/openapi/parameters/revId.yaml"> OGC API - Connected Systems - Part 2 (draft) - revId parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     *     <li>Non-empty string.</li>
     * </ul>
     */
    public ReplaceSystemHistoryRevision revId(String revId) {
        setRevId(revId);
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
     *     The body of the PUT request SHALL contain a representation of the new resource content.
     *     </q><i><small><a href="https://docs.ogc.org/DRAFTS/20-002r1.html#replace"> OGC API - Features - Part 4: Create, Replace, Update and Delete (draft) - Replacing an existing resource (REPLACE)</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     *     <li>The raw system description document that replaces the existing revision; its
     *     {@code validTime} must match the original revision's.</li>
     * </ul>
     */
    public ReplaceSystemHistoryRevision content(byte[] content) {
        setContent(content);
        return this;
    }

    /**
     * @see #contentType(java.lang.String)
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * @see #contentType(java.lang.String)
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * <blockquote><q>
     *     The Implementation SHALL infer the media type of the request body containing a
     *     representation of the new resource content from the Content-Type header.
     *     </q><i><small><a href="https://docs.ogc.org/DRAFTS/20-002r1.html#replace"> OGC API - Features - Part 4: Create, Replace, Update and Delete (draft) - Replacing an existing resource (REPLACE)</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required; a {@code 400} error is returned if not provided.</li>
     * </ul>
     */
    public ReplaceSystemHistoryRevision contentType(String contentType) {
        setContentType(contentType);
        return this;
    }

}
