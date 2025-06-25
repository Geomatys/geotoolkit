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
 * Link
 */
@JsonPropertyOrder({
    Link.JSON_PROPERTY_HREF,
    Link.JSON_PROPERTY_REL,
    Link.JSON_PROPERTY_TYPE,
    Link.JSON_PROPERTY_HREFLANG,
    Link.JSON_PROPERTY_TITLE,
    Link.JSON_PROPERTY_LENGTH
})
@XmlRootElement(name = "Link")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "Link")
public final class Link extends DataTransferObject {

    public static final String JSON_PROPERTY_HREF = "href";
    @XmlElement(name = "href")
    @jakarta.annotation.Nonnull
    private String href;

    public static final String JSON_PROPERTY_REL = "rel";
    @XmlElement(name = "rel")
    @jakarta.annotation.Nonnull
    private String rel;

    public static final String JSON_PROPERTY_TYPE = "type";
    @XmlElement(name = "type")
    @jakarta.annotation.Nullable
    private String type;

    public static final String JSON_PROPERTY_HREFLANG = "hreflang";
    @XmlElement(name = "hreflang")
    @jakarta.annotation.Nullable
    private String hreflang;

    public static final String JSON_PROPERTY_TITLE = "title";
    @XmlElement(name = "title")
    @jakarta.annotation.Nullable
    private String title;

    public static final String JSON_PROPERTY_LENGTH = "length";
    @XmlElement(name = "length")
    @jakarta.annotation.Nullable
    private Integer length;

    public Link() {
    }

    public Link(String href, String rel, String type, String hreflang, String title, Integer length) {
        this.href = href;
        this.rel = rel;
        this.type = type;
        this.hreflang = hreflang;
        this.title = title;
        this.length = length;
    }

    public Link href(@jakarta.annotation.Nonnull String href) {
        this.href = href;
        return this;
    }

    /**
     * Get href
     *
     * @return href
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_HREF)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "href")
    public String getHref() {
        return href;
    }

    @JsonProperty(JSON_PROPERTY_HREF)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "href")
    public void setHref(@jakarta.annotation.Nonnull String href) {
        this.href = href;
    }

    public Link rel(@jakarta.annotation.Nonnull String rel) {
        this.rel = rel;
        return this;
    }

    /**
     * Get rel
     *
     * @return rel
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_REL)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "rel")
    public String getRel() {
        return rel;
    }

    @JsonProperty(JSON_PROPERTY_REL)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "rel")
    public void setRel(@jakarta.annotation.Nonnull String rel) {
        this.rel = rel;
    }

    public Link type(@jakarta.annotation.Nullable String type) {
        this.type = type;
        return this;
    }

    /**
     * Get type
     *
     * @return type
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "type")
    public String getType() {
        return type;
    }

    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "type")
    public void setType(@jakarta.annotation.Nullable String type) {
        this.type = type;
    }

    public Link hreflang(@jakarta.annotation.Nullable String hreflang) {
        this.hreflang = hreflang;
        return this;
    }

    /**
     * Get hreflang
     *
     * @return hreflang
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_HREFLANG)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "hreflang")
    public String getHreflang() {
        return hreflang;
    }

    @JsonProperty(JSON_PROPERTY_HREFLANG)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "hreflang")
    public void setHreflang(@jakarta.annotation.Nullable String hreflang) {
        this.hreflang = hreflang;
    }

    public Link title(@jakarta.annotation.Nullable String title) {
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

    public Link length(@jakarta.annotation.Nullable Integer length) {
        this.length = length;
        return this;
    }

    /**
     * Get length
     *
     * @return length
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_LENGTH)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "length")
    public Integer getLength() {
        return length;
    }

    @JsonProperty(JSON_PROPERTY_LENGTH)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "length")
    public void setLength(@jakarta.annotation.Nullable Integer length) {
        this.length = length;
    }

    /**
     * Return true if this link object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Link link = (Link) o;
        return Objects.equals(this.href, link.href)
                && Objects.equals(this.rel, link.rel)
                && Objects.equals(this.type, link.type)
                && Objects.equals(this.hreflang, link.hreflang)
                && Objects.equals(this.title, link.title)
                && Objects.equals(this.length, link.length);
    }

    @Override
    public int hashCode() {
        return Objects.hash(href, rel, type, hreflang, title, length);
    }

}
