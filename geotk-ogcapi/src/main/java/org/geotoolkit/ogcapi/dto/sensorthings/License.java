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
package org.geotoolkit.ogcapi.dto.sensorthings;

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

/**
 * The usage license applying to a Datastream, MultiDatastream or ObservationGroup.
 *
 * <p>STAplus extension.</p>
 *
 * @see https://docs.ogc.org/is/22-022r1/22-022r1.html#license
 * @author Quentin Bialota (Geomatys)
 */
@JsonPropertyOrder({
    SensorThingsEntity.JSON_PROPERTY_ID,
    SensorThingsEntity.JSON_PROPERTY_SELF_LINK,
    License.JSON_PROPERTY_NAME,
    License.JSON_PROPERTY_DEFINITION,
    License.JSON_PROPERTY_DESCRIPTION,
    License.JSON_PROPERTY_LOGO,
    License.JSON_PROPERTY_ATTRIBUTION_TEXT
})
@XmlRootElement(name = "License")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "License")
public final class License extends SensorThingsEntity {

    public static final String JSON_PROPERTY_NAME = "name";
    @XmlElement(name = "name")
    @jakarta.annotation.Nonnull
    private String name;

    public static final String JSON_PROPERTY_DEFINITION = "definition";
    @XmlElement(name = "definition")
    @jakarta.annotation.Nonnull
    private String definition;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    @XmlElement(name = "description")
    @jakarta.annotation.Nullable
    private String description;

    public static final String JSON_PROPERTY_LOGO = "logo";
    @XmlElement(name = "logo")
    @jakarta.annotation.Nullable
    private String logo;

    public static final String JSON_PROPERTY_ATTRIBUTION_TEXT = "attributionText";
    @XmlElement(name = "attributionText")
    @jakarta.annotation.Nullable
    private String attributionText;

    public License() {
    }

    public License name(@jakarta.annotation.Nonnull String name) {
        this.name = name;
        return this;
    }

    /**
     * The name of the license, e.g. {@code "CC-BY-4.0"}. (required)
     *
     * @return the name
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_NAME)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "name")
    public String getName() {
        return name;
    }

    @JsonProperty(JSON_PROPERTY_NAME)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "name")
    public void setName(@jakarta.annotation.Nonnull String name) {
        this.name = name;
    }

    public License definition(@jakarta.annotation.Nonnull String definition) {
        this.definition = definition;
        return this;
    }

    /**
     * A URI identifying the license. (required)
     *
     * @return the definition
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_DEFINITION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "definition")
    public String getDefinition() {
        return definition;
    }

    @JsonProperty(JSON_PROPERTY_DEFINITION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "definition")
    public void setDefinition(@jakarta.annotation.Nonnull String definition) {
        this.definition = definition;
    }

    public License description(@jakarta.annotation.Nullable String description) {
        this.description = description;
        return this;
    }

    /**
     * A free-text description of the license. (optional)
     *
     * @return the description
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

    public License logo(@jakarta.annotation.Nullable String logo) {
        this.logo = logo;
        return this;
    }

    /**
     * A URI pointing to the license's logo image. (optional)
     *
     * @return the logo
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_LOGO)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "logo")
    public String getLogo() {
        return logo;
    }

    @JsonProperty(JSON_PROPERTY_LOGO)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "logo")
    public void setLogo(@jakarta.annotation.Nullable String logo) {
        this.logo = logo;
    }

    public License attributionText(@jakarta.annotation.Nullable String attributionText) {
        this.attributionText = attributionText;
        return this;
    }

    /**
     * The text to use when attributing data released under this license. (optional)
     *
     * @return the attributionText
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_ATTRIBUTION_TEXT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "attributionText")
    public String getAttributionText() {
        return attributionText;
    }

    @JsonProperty(JSON_PROPERTY_ATTRIBUTION_TEXT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "attributionText")
    public void setAttributionText(@jakarta.annotation.Nullable String attributionText) {
        this.attributionText = attributionText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        License other = (License) o;
        return entityEquals(other)
                && Objects.equals(this.name, other.name)
                && Objects.equals(this.definition, other.definition)
                && Objects.equals(this.description, other.description)
                && Objects.equals(this.logo, other.logo)
                && Objects.equals(this.attributionText, other.attributionText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityHashCode(), name, definition, description, logo, attributionText);
    }
}
