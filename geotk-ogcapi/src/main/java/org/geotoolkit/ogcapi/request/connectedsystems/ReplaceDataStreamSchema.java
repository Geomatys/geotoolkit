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
 * The {@code PUT} operation on {@code /datastreams/{dataStreamId}/schema}: replaces the
 * result/parameter schema of a DataStream.
 *
 * @see <a href="https://docs.ogc.org/is/23-002/23-002.html">OGC API - Connected Systems - Part 2</a>
 * @author Johann Sorel (Geomatys)
 */
public final class ReplaceDataStreamSchema extends RequestParameters {

    private String dataStreamId;
    private byte[] content;
    private String contentType;

    /**
     * Local identifier of the data stream. (required)
     *
     * @return the dataStreamId
     */
    public String getDataStreamId() {
        return dataStreamId;
    }

    /**
     * @param dataStreamId the dataStreamId to set
     * @see #getDataStreamId()
     */
    public void setDataStreamId(String dataStreamId) {
        this.dataStreamId = dataStreamId;
    }

    /**
     * @param dataStreamId the dataStreamId to set
     * @see #getDataStreamId()
     */
    public ReplaceDataStreamSchema dataStreamId(String dataStreamId) {
        setDataStreamId(dataStreamId);
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
    public ReplaceDataStreamSchema content(byte[] content) {
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
    public ReplaceDataStreamSchema contentType(String contentType) {
        setContentType(contentType);
        return this;
    }

}
