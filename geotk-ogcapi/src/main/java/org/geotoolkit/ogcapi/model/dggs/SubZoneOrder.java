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
 * The ordering used for this Discrete Global Grid System Reference System when encoding the values associated with
 * sub-zones at any given depth relative to a parent zone.
 */
@JsonPropertyOrder({
    SubZoneOrder.JSON_PROPERTY_DESCRIPTION,
    SubZoneOrder.JSON_PROPERTY_TYPE
})
@XmlRootElement(name = "DggrsDefinitionSubZoneOrder")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "DggrsDefinitionSubZoneOrder")
public final class SubZoneOrder extends DataTransferObject {

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    @XmlElement(name = "description")
    @jakarta.annotation.Nonnull
    private String description;

    /**
     * May be one of, but not limited to :
     * "scanline", "spiralFromCenter", "mortonCurve", "hilbertCurve"
     */
    public static final String JSON_PROPERTY_TYPE = "type";
    @XmlElement(name = "type")
    @jakarta.annotation.Nullable
    private String type;

    public SubZoneOrder() {
    }

    public SubZoneOrder description(@jakarta.annotation.Nonnull String description) {
        this.description = description;
        return this;
    }

    /**
     * Get description
     *
     * @return description
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "description")
    public String getDescription() {
        return description;
    }

    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "description")
    public void setDescription(@jakarta.annotation.Nonnull String description) {
        this.description = description;
    }

    public SubZoneOrder type(@jakarta.annotation.Nullable String type) {
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

    /**
     * Return true if this dggrs_definition_subZoneOrder object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SubZoneOrder dggrsDefinitionSubZoneOrder = (SubZoneOrder) o;
        return Objects.equals(this.description, dggrsDefinitionSubZoneOrder.description)
                && Objects.equals(this.type, dggrsDefinitionSubZoneOrder.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, type);
    }

}
