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
 * Additional metadata attached to a process, an input or an output.
 * The spec also allows a {@code Link}-shaped variant (a link carrying a
 * {@code role}) instead of this inline {role,title,lang,value} shape;
 * fields of that variant that don't match one of ours are preserved via
 * {@link #otherFields()} rather than lost.
 */
@JsonPropertyOrder({
    Metadata.JSON_PROPERTY_ROLE,
    Metadata.JSON_PROPERTY_TITLE,
    Metadata.JSON_PROPERTY_LANG,
    Metadata.JSON_PROPERTY_VALUE
})
public final class Metadata extends DataTransferObject {

    public static final String JSON_PROPERTY_ROLE = "role";
    private String role;

    public static final String JSON_PROPERTY_TITLE = "title";
    private String title;

    public static final String JSON_PROPERTY_LANG = "lang";
    private String lang;

    public static final String JSON_PROPERTY_VALUE = "value";
    private Object value;

    public Metadata() {
    }

    public Metadata role(String role) {
        this.role = role;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_ROLE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getRole() {
        return role;
    }

    @JsonProperty(JSON_PROPERTY_ROLE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setRole(String role) {
        this.role = role;
    }

    public Metadata title(String title) {
        this.title = title;
        return this;
    }

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

    public Metadata lang(String lang) {
        this.lang = lang;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_LANG)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getLang() {
        return lang;
    }

    @JsonProperty(JSON_PROPERTY_LANG)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setLang(String lang) {
        this.lang = lang;
    }

    public Metadata value(Object value) {
        this.value = value;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_VALUE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Object getValue() {
        return value;
    }

    @JsonProperty(JSON_PROPERTY_VALUE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setValue(Object value) {
        this.value = value;
    }
}
