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
 *     After a datastream has been created and observations have been associated to it, the server may
 *     reject certain updates to the schema (e.g., adding or removing result fields, changing UoM, etc.).
 *     In this case, the `409` response code is used.
 *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part2/openapi/paths/dataStreamSchemas.yaml"> OGC API - Connected Systems - Part 2: Dynamic Data (draft) - Update the observation schema</a></small></i>
 * </blockquote>
 *
 * @author Johann Sorel (Geomatys)
 */
public final class ReplaceDataStreamSchema extends RequestParameters {

    private String dataStreamId;
    private byte[] content;
    private String contentType;

    /**
     * @see #dataStreamId(java.lang.String)
     */
    public String getDataStreamId() {
        return dataStreamId;
    }

    /**
     * @see #dataStreamId(java.lang.String)
     */
    public void setDataStreamId(String dataStreamId) {
        this.dataStreamId = dataStreamId;
    }

    /**
     * <blockquote><q>
     *     Local identifier of a Datastream
     *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part2/openapi/parameters/dataStreamId.yaml"> OGC API - Connected Systems - Part 2 (draft) - dataStreamId parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     *     <li>Non-empty string.</li>
     * </ul>
     */
    public ReplaceDataStreamSchema dataStreamId(String dataStreamId) {
        setDataStreamId(dataStreamId);
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
     * The raw observation schema document that replaces the existing one.
     * <br>
     * <ul>
     *     <li>Required.</li>
     *     <li>Not defined by OGC API - Connected Systems - Part 2 (draft) with a dedicated verbatim
     *     requirement for this field's encoding beyond the request body reference; content is the raw
     *     schema document.</li>
     * </ul>
     */
    public ReplaceDataStreamSchema content(byte[] content) {
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
    public ReplaceDataStreamSchema contentType(String contentType) {
        setContentType(contentType);
        return this;
    }

}
