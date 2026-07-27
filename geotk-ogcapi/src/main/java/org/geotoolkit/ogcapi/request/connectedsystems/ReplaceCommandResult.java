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
 * The {@code PUT} operation on {@code /commands/{cmdId}/result/{resultId}}: replaces an existing
 * result of a Command.
 *
 * @see <a href="https://docs.ogc.org/is/23-002/23-002.html">OGC API - Connected Systems - Part 2</a>
 * @author Johann Sorel (Geomatys)
 */
public final class ReplaceCommandResult extends RequestParameters {

    private String cmdId;
    private String resultId;
    private byte[] content;
    private String contentType;

    /**
     * Local identifier of the command. (required)
     *
     * @return the cmdId
     */
    public String getCmdId() {
        return cmdId;
    }

    /**
     * @param cmdId the cmdId to set
     * @see #getCmdId()
     */
    public void setCmdId(String cmdId) {
        this.cmdId = cmdId;
    }

    /**
     * @param cmdId the cmdId to set
     * @see #getCmdId()
     */
    public ReplaceCommandResult cmdId(String cmdId) {
        setCmdId(cmdId);
        return this;
    }

    /**
     * Local identifier of the command result. (required)
     *
     * @return the resultId
     */
    public String getResultId() {
        return resultId;
    }

    /**
     * @param resultId the resultId to set
     * @see #getResultId()
     */
    public void setResultId(String resultId) {
        this.resultId = resultId;
    }

    /**
     * @param resultId the resultId to set
     * @see #getResultId()
     */
    public ReplaceCommandResult resultId(String resultId) {
        setResultId(resultId);
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
    public ReplaceCommandResult content(byte[] content) {
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
    public ReplaceCommandResult contentType(String contentType) {
        setContentType(contentType);
        return this;
    }

}
