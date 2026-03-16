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
package org.geotoolkit.ogcapi.model.tiles;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.*;
import jakarta.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.geotoolkit.ogcapi.model.DataTransferObject;

/**
 * Attributes of the features or rangetypes of a coverage. Defined by a subset of the JSON Schema for the properties of
 * a feature
 */
@JsonPropertyOrder({
    PropertiesSchema.JSON_PROPERTY_TYPE,
    PropertiesSchema.JSON_PROPERTY_REQUIRED,
    PropertiesSchema.JSON_PROPERTY_PROPERTIES
})
@XmlRootElement(name = "PropertiesSchema")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "PropertiesSchema")
public final class PropertiesSchema extends DataTransferObject {

    public static final String JSON_PROPERTY_TYPE = "type";
    @XmlElement(name = "type")
    @jakarta.annotation.Nonnull
    private String type = "object";

    public static final String JSON_PROPERTY_REQUIRED = "required";
    @XmlElement(name = "required")
    @jakarta.annotation.Nullable
    private List<String> required = new ArrayList<>();

    public static final String JSON_PROPERTY_PROPERTIES = "properties";
    @XmlElement(name = "properties")
    @jakarta.annotation.Nonnull
    private Map<String, PropertiesSchemaPropertiesValue> properties = new HashMap<>();

    public PropertiesSchema() {
    }

    public PropertiesSchema type(@jakarta.annotation.Nonnull String type) {
        this.type = type;
        return this;
    }

    /**
     * Get type
     *
     * @return type
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "type")
    public String getType() {
        return type;
    }

    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "type")
    public void setType(@jakarta.annotation.Nonnull String type) {
        this.type = type;
    }

    public PropertiesSchema required(@jakarta.annotation.Nullable List<String> required) {
        this.required = required;
        return this;
    }

    public PropertiesSchema addRequiredItem(String requiredItem) {
        if (this.required == null) {
            this.required = new ArrayList<>();
        }
        this.required.add(requiredItem);
        return this;
    }

    /**
     * Implements &#39;multiplicity&#39; by citing property &#39;name&#39; defined as &#39;additionalProperties&#39;
     *
     * @return required
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_REQUIRED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "required")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<String> getRequired() {
        return required;
    }

    @JsonProperty(JSON_PROPERTY_REQUIRED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "required")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setRequired(@jakarta.annotation.Nullable List<String> required) {
        this.required = required;
    }

    public PropertiesSchema properties(@jakarta.annotation.Nonnull Map<String, PropertiesSchemaPropertiesValue> properties) {
        this.properties = properties;
        return this;
    }

    public PropertiesSchema putPropertiesItem(String key, PropertiesSchemaPropertiesValue propertiesItem) {
        if (this.properties == null) {
            this.properties = new HashMap<>();
        }
        this.properties.put(key, propertiesItem);
        return this;
    }

    /**
     * Get properties
     *
     * @return properties
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_PROPERTIES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "properties")
    @JacksonXmlElementWrapper(useWrapping = false)
    public Map<String, PropertiesSchemaPropertiesValue> getProperties() {
        return properties;
    }

    @JsonProperty(JSON_PROPERTY_PROPERTIES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "properties")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setProperties(@jakarta.annotation.Nonnull Map<String, PropertiesSchemaPropertiesValue> properties) {
        this.properties = properties;
    }

    /**
     * Return true if this propertiesSchema object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PropertiesSchema other = (PropertiesSchema) o;
        return Objects.equals(this.type, other.type)
                && Objects.equals(this.required, other.required)
                && Objects.equals(this.properties, other.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, required, properties);
    }

}
