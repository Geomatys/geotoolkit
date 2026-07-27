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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.Objects;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

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
public final class Exception extends DataTransferObject {

    public static final String JSON_PROPERTY_TYPE = "type";
    private String type;

    public static final String JSON_PROPERTY_TITLE = "title";
    private String title;

    public static final String JSON_PROPERTY_STATUS = "status";
    private Integer status;

    public static final String JSON_PROPERTY_DETAIL = "detail";
    private String detail;

    public static final String JSON_PROPERTY_INSTANCE = "instance";
    private String instance;

    public Exception() {
    }

    public Exception type(String type) {
        this.type = type;
        return this;
    }

    /**
     * Get type
     *
     * @return type
     */
    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public String getType() {
        return type;
    }

    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setType(String type) {
        this.type = type;
    }

    public Exception title(String title) {
        this.title = title;
        return this;
    }

    /**
     * Get title
     *
     * @return title
     */
    @JsonProperty(JSON_PROPERTY_TITLE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getTitle() {
        return title;
    }

    @JsonProperty(JSON_PROPERTY_TITLE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setTitle(String title) {
        this.title = title;
    }

    public Exception status(Integer status) {
        this.status = status;
        return this;
    }

    /**
     * Get status
     *
     * @return status
     */
    @JsonProperty(JSON_PROPERTY_STATUS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Integer getStatus() {
        return status;
    }

    @JsonProperty(JSON_PROPERTY_STATUS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setStatus(Integer status) {
        this.status = status;
    }

    public Exception detail(String detail) {
        this.detail = detail;
        return this;
    }

    /**
     * Get detail
     *
     * @return detail
     */
    @JsonProperty(JSON_PROPERTY_DETAIL)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getDetail() {
        return detail;
    }

    @JsonProperty(JSON_PROPERTY_DETAIL)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Exception instance(String instance) {
        this.instance = instance;
        return this;
    }

    /**
     * Get instance
     *
     * @return instance
     */
    @JsonProperty(JSON_PROPERTY_INSTANCE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getInstance() {
        return instance;
    }

    @JsonProperty(JSON_PROPERTY_INSTANCE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setInstance(String instance) {
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
