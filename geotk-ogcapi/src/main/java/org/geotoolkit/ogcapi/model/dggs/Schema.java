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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.geotoolkit.ogcapi.model.DataTransferObject;

/**
 * Attributes of the features or fields of a coverage range. Defined by a subset of the JSON Schema for the properties
 * of a feature
 */
@JsonPropertyOrder({
    Schema.JSON_PROPERTY_TYPE,
    Schema.JSON_PROPERTY_REQUIRED,
    Schema.JSON_PROPERTY_PROPERTIES
})
@XmlRootElement(name = "DggsJsonSchema")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "DggsJsonSchema")
public final class Schema extends DataTransferObject {

    /**
     * Gets or Sets type
     */
    @XmlType(name = "TypeEnum")
    @XmlEnum(String.class)
    public enum TypeEnum {
        @XmlEnumValue("object")
        OBJECT(String.valueOf("object"));

        private String value;

        TypeEnum(String value) {
            this.value = value;
        }

        @JsonValue
        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static TypeEnum fromValue(String value) {
            for (TypeEnum b : TypeEnum.values()) {
                if (b.value.equals(value)) {
                    return b;
                }
            }
            throw new IllegalArgumentException("Unexpected value '" + value + "'");
        }
    }

    public static final String JSON_PROPERTY_TYPE = "type";
    @XmlElement(name = "type")
    @jakarta.annotation.Nonnull
    private TypeEnum type;

    public static final String JSON_PROPERTY_REQUIRED = "required";
    @XmlElement(name = "required")
    @jakarta.annotation.Nullable
    private List<String> required = new ArrayList<>();

    public static final String JSON_PROPERTY_PROPERTIES = "properties";
    @XmlElement(name = "properties")
    @jakarta.annotation.Nonnull
    private Map<String, SchemaProperty> properties = new HashMap<>();

    public Schema() {
    }

    public Schema type(@jakarta.annotation.Nonnull TypeEnum type) {
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
    public TypeEnum getType() {
        return type;
    }

    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "type")
    public void setType(@jakarta.annotation.Nonnull TypeEnum type) {
        this.type = type;
    }

    public Schema required(@jakarta.annotation.Nullable List<String> required) {
        this.required = required;
        return this;
    }

    public Schema addRequiredItem(String requiredItem) {
        if (this.required == null) {
            this.required = new ArrayList<>();
        }
        this.required.add(requiredItem);
        return this;
    }

    /**
     * Get required
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

    public Schema properties(@jakarta.annotation.Nonnull Map<String, SchemaProperty> properties) {
        this.properties = properties;
        return this;
    }

    public Schema putPropertiesItem(String key, SchemaProperty propertiesItem) {
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
    public Map<String, SchemaProperty> getProperties() {
        return properties;
    }

    @JsonProperty(JSON_PROPERTY_PROPERTIES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "properties")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setProperties(@jakarta.annotation.Nonnull Map<String, SchemaProperty> properties) {
        this.properties = properties;
    }

    /**
     * Return true if this dggs_json_schema object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Schema dggsJsonSchema = (Schema) o;
        return Objects.equals(this.type, dggsJsonSchema.type)
                && Objects.equals(this.required, dggsJsonSchema.required)
                && Objects.equals(this.properties, dggsJsonSchema.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, required, properties);
    }

}
