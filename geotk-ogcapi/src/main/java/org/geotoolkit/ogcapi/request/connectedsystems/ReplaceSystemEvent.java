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
 *     Update a system event
 *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part2/openapi/paths/systemEventById.yaml"> OGC API - Connected Systems - Part 2 (draft) - Update a system event</a></small></i>
 * </blockquote>
 * <p>
 * No verbatim spec description text is provided for this operation at the time of writing
 * (the specification only gives its one-line summary, quoted above).
 *
 * @author Johann Sorel (Geomatys)
 */
public final class ReplaceSystemEvent extends RequestParameters {

    private String systemId;
    private String eventId;
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
    public ReplaceSystemEvent systemId(String systemId) {
        setSystemId(systemId);
        return this;
    }

    /**
     * @see #eventId(java.lang.String)
     */
    public String getEventId() {
        return eventId;
    }

    /**
     * @see #eventId(java.lang.String)
     */
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    /**
     * <blockquote><q>
     *     Local identifier of a System Event
     *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part2/openapi/parameters/eventId.yaml"> OGC API - Connected Systems - Part 2 (draft) - eventId parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     *     <li>Non-empty string.</li>
     * </ul>
     */
    public ReplaceSystemEvent eventId(String eventId) {
        setEventId(eventId);
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
     *     <li>The raw SystemEvent document that replaces the existing one.</li>
     * </ul>
     */
    public ReplaceSystemEvent content(byte[] content) {
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
    public ReplaceSystemEvent contentType(String contentType) {
        setContentType(contentType);
        return this;
    }

}
