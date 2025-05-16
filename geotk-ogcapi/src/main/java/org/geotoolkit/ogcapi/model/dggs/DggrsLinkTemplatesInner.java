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
package org.geotoolkit.ogcapi.model.dggs;

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
 * DggrsLinkTemplatesInner
 */
@JsonPropertyOrder({
    DggrsLinkTemplatesInner.JSON_PROPERTY_URI_TEMPLATE,
    DggrsLinkTemplatesInner.JSON_PROPERTY_REL,
    DggrsLinkTemplatesInner.JSON_PROPERTY_TYPE,
    DggrsLinkTemplatesInner.JSON_PROPERTY_VAR_BASE,
    DggrsLinkTemplatesInner.JSON_PROPERTY_HREFLANG,
    DggrsLinkTemplatesInner.JSON_PROPERTY_TITLE,
    DggrsLinkTemplatesInner.JSON_PROPERTY_LENGTH
})
@XmlRootElement(name = "DggrsLinkTemplatesInner")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "DggrsLinkTemplatesInner")
public final class DggrsLinkTemplatesInner extends DataTransferObject {

    public static final String JSON_PROPERTY_URI_TEMPLATE = "uriTemplate";
    @XmlElement(name = "uriTemplate")
    @jakarta.annotation.Nonnull
    private String uriTemplate;

    public static final String JSON_PROPERTY_REL = "rel";
    @XmlElement(name = "rel")
    @jakarta.annotation.Nonnull
    private String rel;

    public static final String JSON_PROPERTY_TYPE = "type";
    @XmlElement(name = "type")
    @jakarta.annotation.Nullable
    private String type;

    public static final String JSON_PROPERTY_VAR_BASE = "varBase";
    @XmlElement(name = "varBase")
    @jakarta.annotation.Nullable
    private String varBase;

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

    public DggrsLinkTemplatesInner() {
    }

    public DggrsLinkTemplatesInner uriTemplate(@jakarta.annotation.Nonnull String uriTemplate) {
        this.uriTemplate = uriTemplate;
        return this;
    }

    /**
     * Supplies the URL template to a remote resource (or resource fragment), with template variables surrounded by
     * curly brackets (&#x60;{&#x60; &#x60;}&#x60;).
     *
     * @return uriTemplate
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_URI_TEMPLATE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "uriTemplate")
    public String getUriTemplate() {
        return uriTemplate;
    }

    @JsonProperty(JSON_PROPERTY_URI_TEMPLATE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "uriTemplate")
    public void setUriTemplate(@jakarta.annotation.Nonnull String uriTemplate) {
        this.uriTemplate = uriTemplate;
    }

    public DggrsLinkTemplatesInner rel(@jakarta.annotation.Nonnull String rel) {
        this.rel = rel;
        return this;
    }

    /**
     * The type or semantics of the relation.
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

    public DggrsLinkTemplatesInner type(@jakarta.annotation.Nullable String type) {
        this.type = type;
        return this;
    }

    /**
     * A hint indicating what the media type of the result of dereferencing the link templates should be.
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

    public DggrsLinkTemplatesInner varBase(@jakarta.annotation.Nullable String varBase) {
        this.varBase = varBase;
        return this;
    }

    /**
     * A base path to retrieve semantic information about the variables used in URL template.
     *
     * @return varBase
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_VAR_BASE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "varBase")
    public String getVarBase() {
        return varBase;
    }

    @JsonProperty(JSON_PROPERTY_VAR_BASE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "varBase")
    public void setVarBase(@jakarta.annotation.Nullable String varBase) {
        this.varBase = varBase;
    }

    public DggrsLinkTemplatesInner hreflang(@jakarta.annotation.Nullable String hreflang) {
        this.hreflang = hreflang;
        return this;
    }

    /**
     * A hint indicating what the language of the result of dereferencing the link should be.
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

    public DggrsLinkTemplatesInner title(@jakarta.annotation.Nullable String title) {
        this.title = title;
        return this;
    }

    /**
     * Used to label the destination of a link template such that it can be used as a human-readable identifier.
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

    public DggrsLinkTemplatesInner length(@jakarta.annotation.Nullable Integer length) {
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
     * Return true if this dggrs_linkTemplates_inner object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DggrsLinkTemplatesInner dggrsLinkTemplatesInner = (DggrsLinkTemplatesInner) o;
        return Objects.equals(this.uriTemplate, dggrsLinkTemplatesInner.uriTemplate)
                && Objects.equals(this.rel, dggrsLinkTemplatesInner.rel)
                && Objects.equals(this.type, dggrsLinkTemplatesInner.type)
                && Objects.equals(this.varBase, dggrsLinkTemplatesInner.varBase)
                && Objects.equals(this.hreflang, dggrsLinkTemplatesInner.hreflang)
                && Objects.equals(this.title, dggrsLinkTemplatesInner.title)
                && Objects.equals(this.length, dggrsLinkTemplatesInner.length);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uriTemplate, rel, type, varBase, hreflang, title, length);
    }

}
