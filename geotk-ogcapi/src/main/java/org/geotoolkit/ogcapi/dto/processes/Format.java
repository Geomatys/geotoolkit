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
package org.geotoolkit.ogcapi.dto.processes;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * Requested output format, used inside {@link OutputSelection}.
 */
@JsonPropertyOrder({
    Format.JSON_PROPERTY_MEDIA_TYPE,
    Format.JSON_PROPERTY_ENCODING,
    Format.JSON_PROPERTY_SCHEMA
})
public final class Format extends DataTransferObject {

    public static final String JSON_PROPERTY_MEDIA_TYPE = "mediaType";
    private String mediaType;

    public static final String JSON_PROPERTY_ENCODING = "encoding";
    private String encoding;

    public static final String JSON_PROPERTY_SCHEMA = "schema";
    private Object schema;

    public Format() {
    }

    public Format mediaType(String mediaType) {
        this.mediaType = mediaType;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_MEDIA_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getMediaType() {
        return mediaType;
    }

    @JsonProperty(JSON_PROPERTY_MEDIA_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public Format encoding(String encoding) {
        this.encoding = encoding;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_ENCODING)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getEncoding() {
        return encoding;
    }

    @JsonProperty(JSON_PROPERTY_ENCODING)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public Format schema(Object schema) {
        this.schema = schema;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_SCHEMA)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Object getSchema() {
        return schema;
    }

    @JsonProperty(JSON_PROPERTY_SCHEMA)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setSchema(Object schema) {
        this.schema = schema;
    }
}
