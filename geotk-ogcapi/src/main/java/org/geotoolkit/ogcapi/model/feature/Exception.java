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
package org.geotoolkit.ogcapi.model.feature;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.*;
import jakarta.xml.bind.annotation.*;
import org.geotoolkit.ogcapi.model.DataTransferObject;

/**
 * Information about the exception: an error code plus an optional description.
 */
@JsonPropertyOrder({
    Exception.JSON_PROPERTY_CODE,
    Exception.JSON_PROPERTY_DESCRIPTION
})
@XmlRootElement(name = "Exception")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "Exception")
public class Exception extends DataTransferObject {

    public static final String JSON_PROPERTY_CODE = "code";
    @XmlElement(name = "code")
    @jakarta.annotation.Nonnull
    private String code;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    @XmlElement(name = "description")
    @jakarta.annotation.Nullable
    private String description;

    public Exception() {
    }

    public Exception code(@jakarta.annotation.Nonnull String code) {
        this.code = code;
        return this;
    }

    /**
     * Get code
     *
     * @return code
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_CODE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "code")
    public String getCode() {
        return code;
    }

    @JsonProperty(JSON_PROPERTY_CODE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "code")
    public void setCode(@jakarta.annotation.Nonnull String code) {
        this.code = code;
    }

    public Exception description(@jakarta.annotation.Nullable String description) {
        this.description = description;
        return this;
    }

    /**
     * Get description
     *
     * @return description
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "description")
    public String getDescription() {
        return description;
    }

    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "description")
    public void setDescription(@jakarta.annotation.Nullable String description) {
        this.description = description;
    }

    /**
     * Return true if this exception object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Exception exception = (Exception) o;
        return Objects.equals(this.code, exception.code)
                && Objects.equals(this.description, exception.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, description);
    }

}
