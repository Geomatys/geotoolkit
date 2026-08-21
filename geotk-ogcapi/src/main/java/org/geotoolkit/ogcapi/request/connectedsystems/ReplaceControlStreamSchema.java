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
 *     After a control stream has been created and commands have been issued through it, the server may
 *     reject certain updates to the schema (e.g., adding or removing parameters, changing UoM, etc.).
 *     In this case, the `409` response code is used.
 *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part2/openapi/paths/controlStreamSchemas.yaml"> OGC API - Connected Systems - Part 2: Dynamic Data (draft) - Update the command schema</a></small></i>
 * </blockquote>
 *
 * @author Johann Sorel (Geomatys)
 */
public final class ReplaceControlStreamSchema extends RequestParameters {

    private String controlStreamId;
    private byte[] content;
    private String contentType;

    /**
     * @see #controlStreamId(java.lang.String)
     */
    public String getControlStreamId() {
        return controlStreamId;
    }

    /**
     * @see #controlStreamId(java.lang.String)
     */
    public void setControlStreamId(String controlStreamId) {
        this.controlStreamId = controlStreamId;
    }

    /**
     * <blockquote><q>
     *     Local identifier of a Control Stream
     *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part2/openapi/parameters/controlStreamId.yaml"> OGC API - Connected Systems - Part 2 (draft) - controlStreamId parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     *     <li>Non-empty string.</li>
     * </ul>
     */
    public ReplaceControlStreamSchema controlStreamId(String controlStreamId) {
        setControlStreamId(controlStreamId);
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
     * The raw command schema document that replaces the existing one.
     * <br>
     * <ul>
     *     <li>Required.</li>
     *     <li>Not defined by OGC API - Connected Systems - Part 2 (draft) with a dedicated verbatim
     *     requirement for this field's encoding beyond the request body reference; content is the raw
     *     schema document.</li>
     * </ul>
     */
    public ReplaceControlStreamSchema content(byte[] content) {
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
     * The media type of the schema document encoding, sent as the {@code Content-Type} header.
     * <br>
     * <ul>
     *     <li>Required; a {@code 400} error is returned if not provided.</li>
     *     <li>Not defined by OGC API - Connected Systems - Part 2 (draft) with a dedicated verbatim
     *     requirement for this field beyond the request body reference (no verbatim spec text
     *     available for this field).</li>
     * </ul>
     */
    public ReplaceControlStreamSchema contentType(String contentType) {
        setContentType(contentType);
        return this;
    }

}
