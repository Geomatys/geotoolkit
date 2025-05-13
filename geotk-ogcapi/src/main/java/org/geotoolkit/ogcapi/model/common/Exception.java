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
package org.geotoolkit.ogcapi.model.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.Objects;
import org.geotoolkit.ogcapi.model.DataTransferObject;

/**
 * JSON schema for exceptions based on RFC 7807
 */
@JsonPropertyOrder({
    Exception.JSON_PROPERTY_TYPE,
    Exception.JSON_PROPERTY_TITLE,
    Exception.JSON_PROPERTY_STATUS,
    Exception.JSON_PROPERTY_DETAIL,
    Exception.JSON_PROPERTY_INSTANCE
})
@XmlRootElement(name = "Exception")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "Exception")
public final class Exception extends DataTransferObject {

    public static final String JSON_PROPERTY_TYPE = "type";
    @XmlElement(name = "type")
    @jakarta.annotation.Nonnull
    private String type;

    public static final String JSON_PROPERTY_TITLE = "title";
    @XmlElement(name = "title")
    @jakarta.annotation.Nullable
    private String title;

    public static final String JSON_PROPERTY_STATUS = "status";
    @XmlElement(name = "status")
    @jakarta.annotation.Nullable
    private Integer status;

    public static final String JSON_PROPERTY_DETAIL = "detail";
    @XmlElement(name = "detail")
    @jakarta.annotation.Nullable
    private String detail;

    public static final String JSON_PROPERTY_INSTANCE = "instance";
    @XmlElement(name = "instance")
    @jakarta.annotation.Nullable
    private String instance;

    public Exception() {
    }

    public Exception type(@jakarta.annotation.Nonnull String type) {
        this.type = type;
        return this;
    }

    /**
     * Get type
     *
     * @return type
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "type")
    public String getType() {
        return type;
    }

    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "type")
    public void setType(@jakarta.annotation.Nonnull String type) {
        this.type = type;
    }

    public Exception title(@jakarta.annotation.Nullable String title) {
        this.title = title;
        return this;
    }

    /**
     * Get title
     *
     * @return title
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_TITLE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "title")
    public String getTitle() {
        return title;
    }

    @JsonProperty(JSON_PROPERTY_TITLE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "title")
    public void setTitle(@jakarta.annotation.Nullable String title) {
        this.title = title;
    }

    public Exception status(@jakarta.annotation.Nullable Integer status) {
        this.status = status;
        return this;
    }

    /**
     * Get status
     *
     * @return status
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_STATUS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "status")
    public Integer getStatus() {
        return status;
    }

    @JsonProperty(JSON_PROPERTY_STATUS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "status")
    public void setStatus(@jakarta.annotation.Nullable Integer status) {
        this.status = status;
    }

    public Exception detail(@jakarta.annotation.Nullable String detail) {
        this.detail = detail;
        return this;
    }

    /**
     * Get detail
     *
     * @return detail
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_DETAIL)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "detail")
    public String getDetail() {
        return detail;
    }

    @JsonProperty(JSON_PROPERTY_DETAIL)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "detail")
    public void setDetail(@jakarta.annotation.Nullable String detail) {
        this.detail = detail;
    }

    public Exception instance(@jakarta.annotation.Nullable String instance) {
        this.instance = instance;
        return this;
    }

    /**
     * Get instance
     *
     * @return instance
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_INSTANCE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "instance")
    public String getInstance() {
        return instance;
    }

    @JsonProperty(JSON_PROPERTY_INSTANCE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "instance")
    public void setInstance(@jakarta.annotation.Nullable String instance) {
        this.instance = instance;
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
        return Objects.equals(this.type, exception.type)
                && Objects.equals(this.title, exception.title)
                && Objects.equals(this.status, exception.status)
                && Objects.equals(this.detail, exception.detail)
                && Objects.equals(this.instance, exception.instance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, title, status, detail, instance);
    }

}
