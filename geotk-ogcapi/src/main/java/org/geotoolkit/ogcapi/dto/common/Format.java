/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
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
package org.geotoolkit.ogcapi.dto.common;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * Format
 */
@JsonPropertyOrder({
    Format.JSON_PROPERTY_NAME,
    Format.JSON_PROPERTY_MEDIA_TYPE
})
public final class Format extends DataTransferObject {

    public static final String JSON_PROPERTY_NAME = "name";
    private String name;

    public static final String JSON_PROPERTY_MEDIA_TYPE = "mediaType";
    private String mediaType;

    public Format() {
    }

    public Format name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Get name
     *
     * @return name
     */
    @JsonProperty(JSON_PROPERTY_NAME)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getName() {
        return name;
    }

    @JsonProperty(JSON_PROPERTY_NAME)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setName(String name) {
        this.name = name;
    }

    public Format mediaType(String mediaType) {
        this.mediaType = mediaType;
        return this;
    }

    /**
     * Get mediaType
     *
     * @return mediaType
     */
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

    /**
     * Return true if this format object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Format format = (Format) o;
        return Objects.equals(this.name, format.name)
                && Objects.equals(this.mediaType, format.mediaType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, mediaType);
    }

}
