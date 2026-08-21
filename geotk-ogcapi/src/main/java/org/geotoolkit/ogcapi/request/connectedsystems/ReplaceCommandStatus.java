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
 *     Update a command status report
 *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part2/openapi/paths/commandStatusById.yaml"> OGC API - Connected Systems - Part 2: Dynamic Data (draft) - Update a command status report</a></small></i>
 * </blockquote>
 *
 * @author Johann Sorel (Geomatys)
 */
public final class ReplaceCommandStatus extends RequestParameters {

    private String cmdId;
    private String statusId;
    private byte[] content;
    private String contentType;

    /**
     * @see #cmdId(java.lang.String)
     */
    public String getCmdId() {
        return cmdId;
    }

    /**
     * @see #cmdId(java.lang.String)
     */
    public void setCmdId(String cmdId) {
        this.cmdId = cmdId;
    }

    /**
     * <blockquote><q>
     *     Local identifier of a Command
     *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part2/openapi/parameters/cmdId.yaml"> OGC API - Connected Systems - Part 2: Dynamic Data (draft) - cmdId parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     * </ul>
     */
    public ReplaceCommandStatus cmdId(String cmdId) {
        setCmdId(cmdId);
        return this;
    }

    /**
     * @see #statusId(java.lang.String)
     */
    public String getStatusId() {
        return statusId;
    }

    /**
     * @see #statusId(java.lang.String)
     */
    public void setStatusId(String statusId) {
        this.statusId = statusId;
    }

    /**
     * <blockquote><q>
     *     Local identifier of a Status Report
     *     </q><i><small><a href="https://github.com/opengeospatial/ogcapi-connected-systems/blob/master/api/part2/openapi/parameters/statusId.yaml"> OGC API - Connected Systems - Part 2: Dynamic Data (draft) - statusId parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     * </ul>
     */
    public ReplaceCommandStatus statusId(String statusId) {
        setStatusId(statusId);
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
     *     <li>The raw CommandStatus document that replaces the existing one.</li>
     * </ul>
     */
    public ReplaceCommandStatus content(byte[] content) {
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
     *     The Implementation SHALL infer the media type of the request body containing
     *     a representation of the new resource content from the Content-Type header.
     *     </q><i><small><a href="https://docs.ogc.org/DRAFTS/20-002r1.html#replace"> OGC API - Features - Part 4: Create, Replace, Update and Delete (draft) - Replacing an existing resource (REPLACE)</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required; a {@code 400} error is returned if not provided.</li>
     * </ul>
     */
    public ReplaceCommandStatus contentType(String contentType) {
        setContentType(contentType);
        return this;
    }

}
