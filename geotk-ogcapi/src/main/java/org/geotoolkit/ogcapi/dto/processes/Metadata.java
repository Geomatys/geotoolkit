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
@XmlRootElement(name = "Metadata")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "Metadata")
public final class Metadata extends DataTransferObject {

    public static final String JSON_PROPERTY_ROLE = "role";
    @XmlElement(name = "role")
    @jakarta.annotation.Nullable
    private String role;

    public static final String JSON_PROPERTY_TITLE = "title";
    @XmlElement(name = "title")
    @jakarta.annotation.Nullable
    private String title;

    public static final String JSON_PROPERTY_LANG = "lang";
    @XmlElement(name = "lang")
    @jakarta.annotation.Nullable
    private String lang;

    public static final String JSON_PROPERTY_VALUE = "value";
    @XmlElement(name = "value")
    @jakarta.annotation.Nullable
    private Object value;

    public Metadata() {
    }

    public Metadata role(@jakarta.annotation.Nullable String role) {
        this.role = role;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_ROLE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "role")
    public String getRole() {
        return role;
    }

    @JsonProperty(JSON_PROPERTY_ROLE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "role")
    public void setRole(@jakarta.annotation.Nullable String role) {
        this.role = role;
    }

    public Metadata title(@jakarta.annotation.Nullable String title) {
        this.title = title;
        return this;
    }

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

    public Metadata lang(@jakarta.annotation.Nullable String lang) {
        this.lang = lang;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_LANG)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "lang")
    public String getLang() {
        return lang;
    }

    @JsonProperty(JSON_PROPERTY_LANG)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "lang")
    public void setLang(@jakarta.annotation.Nullable String lang) {
        this.lang = lang;
    }

    public Metadata value(@jakarta.annotation.Nullable Object value) {
        this.value = value;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_VALUE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "value")
    public Object getValue() {
        return value;
    }

    @JsonProperty(JSON_PROPERTY_VALUE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "value")
    public void setValue(@jakarta.annotation.Nullable Object value) {
        this.value = value;
    }
}
