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
public final class License extends SensorThingsEntity {

    public static final String JSON_PROPERTY_NAME = "name";
    private String name;

    public static final String JSON_PROPERTY_DEFINITION = "definition";
    private String definition;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    private String description;

    public static final String JSON_PROPERTY_LOGO = "logo";
    private String logo;

    public static final String JSON_PROPERTY_ATTRIBUTION_TEXT = "attributionText";
    private String attributionText;

    public License() {
    }

    public License name(String name) {
        this.name = name;
        return this;
    }

    /**
     * The name of the license, e.g. {@code "CC-BY-4.0"}. (required)
     *
     * @return the name
     */
    @JsonProperty(JSON_PROPERTY_NAME)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getName() {
        return name;
    }

    @JsonProperty(JSON_PROPERTY_NAME)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setName(String name) {
        this.name = name;
    }

    public License definition(String definition) {
        this.definition = definition;
        return this;
    }

    /**
     * A URI identifying the license. (required)
     *
     * @return the definition
     */
    @JsonProperty(JSON_PROPERTY_DEFINITION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getDefinition() {
        return definition;
    }

    @JsonProperty(JSON_PROPERTY_DEFINITION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public License description(String description) {
        this.description = description;
        return this;
    }

    /**
     * A free-text description of the license. (optional)
     *
     * @return the description
     */
    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getDescription() {
        return description;
    }

    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setDescription(String description) {
        this.description = description;
    }

    public License logo(String logo) {
        this.logo = logo;
        return this;
    }

    /**
     * A URI pointing to the license's logo image. (optional)
     *
     * @return the logo
     */
    @JsonProperty(JSON_PROPERTY_LOGO)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getLogo() {
        return logo;
    }

    @JsonProperty(JSON_PROPERTY_LOGO)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setLogo(String logo) {
        this.logo = logo;
    }

    public License attributionText(String attributionText) {
        this.attributionText = attributionText;
        return this;
    }

    /**
     * The text to use when attributing data released under this license. (optional)
     *
     * @return the attributionText
     */
    @JsonProperty(JSON_PROPERTY_ATTRIBUTION_TEXT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getAttributionText() {
        return attributionText;
    }

    @JsonProperty(JSON_PROPERTY_ATTRIBUTION_TEXT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setAttributionText(String attributionText) {
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
