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
import java.net.URI;
import java.util.Objects;
import org.geotoolkit.ogcapi.model.DataTransferObject;

/**
 * Discrete Global Grid System Reference System (DGGRS) Definition.
 */
@JsonPropertyOrder({
    DggrsDefinition.JSON_PROPERTY_TITLE,
    DggrsDefinition.JSON_PROPERTY_DESCRIPTION,
    DggrsDefinition.JSON_PROPERTY_URI,
    DggrsDefinition.JSON_PROPERTY_DGGH,
    DggrsDefinition.JSON_PROPERTY_ZIRS,
    DggrsDefinition.JSON_PROPERTY_SUB_ZONE_ORDER
})
@XmlRootElement(name = "DggrsDefinition")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "DggrsDefinition")
public final class DggrsDefinition extends DataTransferObject {

    public static final String JSON_PROPERTY_TITLE = "title";
    @XmlElement(name = "title")
    @jakarta.annotation.Nullable
    private String title;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    @XmlElement(name = "description")
    @jakarta.annotation.Nullable
    private String description;

    public static final String JSON_PROPERTY_URI = "uri";
    @XmlElement(name = "uri")
    @jakarta.annotation.Nullable
    private URI uri;

    public static final String JSON_PROPERTY_DGGH = "dggh";
    @XmlElement(name = "dggh")
    @jakarta.annotation.Nonnull
    private Dggs dggh;

    public static final String JSON_PROPERTY_ZIRS = "zirs";
    @XmlElement(name = "zirs")
    @jakarta.annotation.Nonnull
    private Zirs zirs;

    public static final String JSON_PROPERTY_SUB_ZONE_ORDER = "subZoneOrder";
    @XmlElement(name = "subZoneOrder")
    @jakarta.annotation.Nonnull
    private SubZoneOrder subZoneOrder;

    public DggrsDefinition() {
    }

    public DggrsDefinition title(@jakarta.annotation.Nullable String title) {
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

    public DggrsDefinition description(@jakarta.annotation.Nullable String description) {
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

    public DggrsDefinition uri(@jakarta.annotation.Nullable URI uri) {
        this.uri = uri;
        return this;
    }

    /**
     * The authoritative URI associated with this DGGRS definition
     *
     * @return uri
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_URI)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "uri")
    public URI getUri() {
        return uri;
    }

    @JsonProperty(JSON_PROPERTY_URI)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "uri")
    public void setUri(@jakarta.annotation.Nullable URI uri) {
        this.uri = uri;
    }

    public DggrsDefinition dggh(@jakarta.annotation.Nonnull Dggs dggh) {
        this.dggh = dggh;
        return this;
    }

    /**
     * Get dggh
     *
     * @return dggh
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_DGGH)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "dggh")
    public Dggs getDggh() {
        return dggh;
    }

    @JsonProperty(JSON_PROPERTY_DGGH)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "dggh")
    public void setDggh(@jakarta.annotation.Nonnull Dggs dggh) {
        this.dggh = dggh;
    }

    public DggrsDefinition zirs(@jakarta.annotation.Nonnull Zirs zirs) {
        this.zirs = zirs;
        return this;
    }

    /**
     * Get zirs
     *
     * @return zirs
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_ZIRS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "zirs")
    public Zirs getZirs() {
        return zirs;
    }

    @JsonProperty(JSON_PROPERTY_ZIRS)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "zirs")
    public void setZirs(@jakarta.annotation.Nonnull Zirs zirs) {
        this.zirs = zirs;
    }

    public DggrsDefinition subZoneOrder(@jakarta.annotation.Nonnull SubZoneOrder subZoneOrder) {
        this.subZoneOrder = subZoneOrder;
        return this;
    }

    /**
     * Get subZoneOrder
     *
     * @return subZoneOrder
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_SUB_ZONE_ORDER)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "subZoneOrder")
    public SubZoneOrder getSubZoneOrder() {
        return subZoneOrder;
    }

    @JsonProperty(JSON_PROPERTY_SUB_ZONE_ORDER)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "subZoneOrder")
    public void setSubZoneOrder(@jakarta.annotation.Nonnull SubZoneOrder subZoneOrder) {
        this.subZoneOrder = subZoneOrder;
    }

    /**
     * Return true if this dggrs-definition object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DggrsDefinition dggrsDefinition = (DggrsDefinition) o;
        return Objects.equals(this.title, dggrsDefinition.title)
                && Objects.equals(this.description, dggrsDefinition.description)
                && Objects.equals(this.uri, dggrsDefinition.uri)
                && Objects.equals(this.dggh, dggrsDefinition.dggh)
                && Objects.equals(this.zirs, dggrsDefinition.zirs)
                && Objects.equals(this.subZoneOrder, dggrsDefinition.subZoneOrder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, uri, dggh, zirs, subZoneOrder);
    }

}
