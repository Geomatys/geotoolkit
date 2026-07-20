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
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * Requested output format, used inside {@link OutputSelection}.
 */
@JsonPropertyOrder({
    Format.JSON_PROPERTY_MEDIA_TYPE,
    Format.JSON_PROPERTY_ENCODING,
    Format.JSON_PROPERTY_SCHEMA
})
@XmlRootElement(name = "Format")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "Format")
public final class Format extends DataTransferObject {

    public static final String JSON_PROPERTY_MEDIA_TYPE = "mediaType";
    @XmlElement(name = "mediaType")
    @jakarta.annotation.Nullable
    private String mediaType;

    public static final String JSON_PROPERTY_ENCODING = "encoding";
    @XmlElement(name = "encoding")
    @jakarta.annotation.Nullable
    private String encoding;

    public static final String JSON_PROPERTY_SCHEMA = "schema";
    @XmlElement(name = "schema")
    @jakarta.annotation.Nullable
    private Object schema;

    public Format() {
    }

    public Format mediaType(@jakarta.annotation.Nullable String mediaType) {
        this.mediaType = mediaType;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_MEDIA_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "mediaType")
    public String getMediaType() {
        return mediaType;
    }

    @JsonProperty(JSON_PROPERTY_MEDIA_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "mediaType")
    public void setMediaType(@jakarta.annotation.Nullable String mediaType) {
        this.mediaType = mediaType;
    }

    public Format encoding(@jakarta.annotation.Nullable String encoding) {
        this.encoding = encoding;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_ENCODING)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "encoding")
    public String getEncoding() {
        return encoding;
    }

    @JsonProperty(JSON_PROPERTY_ENCODING)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "encoding")
    public void setEncoding(@jakarta.annotation.Nullable String encoding) {
        this.encoding = encoding;
    }

    public Format schema(@jakarta.annotation.Nullable Object schema) {
        this.schema = schema;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_SCHEMA)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "schema")
    public Object getSchema() {
        return schema;
    }

    @JsonProperty(JSON_PROPERTY_SCHEMA)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "schema")
    public void setSchema(@jakarta.annotation.Nullable Object schema) {
        this.schema = schema;
    }
}
