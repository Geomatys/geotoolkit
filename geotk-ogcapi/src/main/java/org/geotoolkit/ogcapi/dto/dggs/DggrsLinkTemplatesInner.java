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
package org.geotoolkit.ogcapi.dto.dggs;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.Objects;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

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
public final class DggrsLinkTemplatesInner extends DataTransferObject {

    public static final String JSON_PROPERTY_URI_TEMPLATE = "uriTemplate";
    private String uriTemplate;

    public static final String JSON_PROPERTY_REL = "rel";
    private String rel;

    public static final String JSON_PROPERTY_TYPE = "type";
    private String type;

    public static final String JSON_PROPERTY_VAR_BASE = "varBase";
    private String varBase;

    public static final String JSON_PROPERTY_HREFLANG = "hreflang";
    private String hreflang;

    public static final String JSON_PROPERTY_TITLE = "title";
    private String title;

    public static final String JSON_PROPERTY_LENGTH = "length";
    private Integer length;

    public DggrsLinkTemplatesInner() {
    }

    public DggrsLinkTemplatesInner uriTemplate(String uriTemplate) {
        this.uriTemplate = uriTemplate;
        return this;
    }

    /**
     * Supplies the URL template to a remote resource (or resource fragment), with template variables surrounded by
     * curly brackets (&#x60;{&#x60; &#x60;}&#x60;).
     *
     * @return uriTemplate
     */
    @JsonProperty(JSON_PROPERTY_URI_TEMPLATE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public String getUriTemplate() {
        return uriTemplate;
    }

    @JsonProperty(JSON_PROPERTY_URI_TEMPLATE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setUriTemplate(String uriTemplate) {
        this.uriTemplate = uriTemplate;
    }

    public DggrsLinkTemplatesInner rel(String rel) {
        this.rel = rel;
        return this;
    }

    /**
     * The type or semantics of the relation.
     *
     * @return rel
     */
    @JsonProperty(JSON_PROPERTY_REL)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public String getRel() {
        return rel;
    }

    @JsonProperty(JSON_PROPERTY_REL)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setRel(String rel) {
        this.rel = rel;
    }

    public DggrsLinkTemplatesInner type(String type) {
        this.type = type;
        return this;
    }

    /**
     * A hint indicating what the media type of the result of dereferencing the link templates should be.
     *
     * @return type
     */
    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getType() {
        return type;
    }

    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setType(String type) {
        this.type = type;
    }

    public DggrsLinkTemplatesInner varBase(String varBase) {
        this.varBase = varBase;
        return this;
    }

    /**
     * A base path to retrieve semantic information about the variables used in URL template.
     *
     * @return varBase
     */
    @JsonProperty(JSON_PROPERTY_VAR_BASE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getVarBase() {
        return varBase;
    }

    @JsonProperty(JSON_PROPERTY_VAR_BASE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setVarBase(String varBase) {
        this.varBase = varBase;
    }

    public DggrsLinkTemplatesInner hreflang(String hreflang) {
        this.hreflang = hreflang;
        return this;
    }

    /**
     * A hint indicating what the language of the result of dereferencing the link should be.
     *
     * @return hreflang
     */
    @JsonProperty(JSON_PROPERTY_HREFLANG)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getHreflang() {
        return hreflang;
    }

    @JsonProperty(JSON_PROPERTY_HREFLANG)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setHreflang(String hreflang) {
        this.hreflang = hreflang;
    }

    public DggrsLinkTemplatesInner title(String title) {
        this.title = title;
        return this;
    }

    /**
     * Used to label the destination of a link template such that it can be used as a human-readable identifier.
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

    public DggrsLinkTemplatesInner length(Integer length) {
        this.length = length;
        return this;
    }

    /**
     * Get length
     *
     * @return length
     */
    @JsonProperty(JSON_PROPERTY_LENGTH)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Integer getLength() {
        return length;
    }

    @JsonProperty(JSON_PROPERTY_LENGTH)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setLength(Integer length) {
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
