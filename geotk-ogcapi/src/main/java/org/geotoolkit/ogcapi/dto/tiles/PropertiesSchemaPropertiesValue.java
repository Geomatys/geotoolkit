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
package org.geotoolkit.ogcapi.dto.tiles;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.math.BigDecimal;
import java.net.URI;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * No property names are defined but any property name they should be described by JSON Schema. So
 * &#39;additionalProperties&#39; implements &#39;name&#39;.
 */
@JsonPropertyOrder({
    PropertiesSchemaPropertiesValue.JSON_PROPERTY_TITLE,
    PropertiesSchemaPropertiesValue.JSON_PROPERTY_DESCRIPTION,
    PropertiesSchemaPropertiesValue.JSON_PROPERTY_TYPE,
    PropertiesSchemaPropertiesValue.JSON_PROPERTY_ENUM,
    PropertiesSchemaPropertiesValue.JSON_PROPERTY_FORMAT,
    PropertiesSchemaPropertiesValue.JSON_PROPERTY_CONTENT_MEDIA_TYPE,
    PropertiesSchemaPropertiesValue.JSON_PROPERTY_MAXIMUM,
    PropertiesSchemaPropertiesValue.JSON_PROPERTY_EXCLUSIVE_MAXIMUM,
    PropertiesSchemaPropertiesValue.JSON_PROPERTY_MINIMUM,
    PropertiesSchemaPropertiesValue.JSON_PROPERTY_EXCLUSIVE_MINIMUM,
    PropertiesSchemaPropertiesValue.JSON_PROPERTY_PATTERN,
    PropertiesSchemaPropertiesValue.JSON_PROPERTY_MAX_ITEMS,
    PropertiesSchemaPropertiesValue.JSON_PROPERTY_MIN_ITEMS,
    PropertiesSchemaPropertiesValue.JSON_PROPERTY_OBSERVED_PROPERTY,
    PropertiesSchemaPropertiesValue.JSON_PROPERTY_OBSERVED_PROPERTY_U_R_I,
    PropertiesSchemaPropertiesValue.JSON_PROPERTY_UOM,
    PropertiesSchemaPropertiesValue.JSON_PROPERTY_UOM_U_R_I
})
public final class PropertiesSchemaPropertiesValue extends DataTransferObject {

    public static final String JSON_PROPERTY_TITLE = "title";
    private String title;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    private String description;

    /**
     * Gets or Sets type
     */
    public enum TypeEnum {
        ARRAY("array"),
        BOOLEAN("boolean"),
        INTEGER("integer"),
        NULL("null"),
        NUMBER("number"),
        OBJECT("object"),
        STRING("string");

        private final String value;

        TypeEnum(String value) {
            this.value = value;
        }

        @JsonValue
        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return value;
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
    private TypeEnum type;

    public static final String JSON_PROPERTY_ENUM = "enum";
    private Set<Object> _enum = new LinkedHashSet<>();

    public static final String JSON_PROPERTY_FORMAT = "format";
    private String format;

    public static final String JSON_PROPERTY_CONTENT_MEDIA_TYPE = "contentMediaType";
    private String contentMediaType;

    public static final String JSON_PROPERTY_MAXIMUM = "maximum";
    private BigDecimal maximum;

    public static final String JSON_PROPERTY_EXCLUSIVE_MAXIMUM = "exclusiveMaximum";
    private BigDecimal exclusiveMaximum;

    public static final String JSON_PROPERTY_MINIMUM = "minimum";
    private BigDecimal minimum;

    public static final String JSON_PROPERTY_EXCLUSIVE_MINIMUM = "exclusiveMinimum";
    private BigDecimal exclusiveMinimum;

    public static final String JSON_PROPERTY_PATTERN = "pattern";
    private String pattern;

    public static final String JSON_PROPERTY_MAX_ITEMS = "maxItems";
    private Integer maxItems;

    public static final String JSON_PROPERTY_MIN_ITEMS = "minItems";
    private Integer minItems = 0;

    public static final String JSON_PROPERTY_OBSERVED_PROPERTY = "observedProperty";
    private String observedProperty;

    public static final String JSON_PROPERTY_OBSERVED_PROPERTY_U_R_I = "observedPropertyURI";
    private URI observedPropertyURI;

    public static final String JSON_PROPERTY_UOM = "uom";
    private String uom;

    public static final String JSON_PROPERTY_UOM_U_R_I = "uomURI";
    private URI uomURI;

    public PropertiesSchemaPropertiesValue() {
    }

    public PropertiesSchemaPropertiesValue title(String title) {
        this.title = title;
        return this;
    }

    /**
     * Get title
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

    public PropertiesSchemaPropertiesValue description(String description) {
        this.description = description;
        return this;
    }

    /**
     * Implements &#39;description&#39;
     *
     * @return description
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

    public PropertiesSchemaPropertiesValue type(TypeEnum type) {
        this.type = type;
        return this;
    }

    /**
     * Get type
     *
     * @return type
     */
    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public TypeEnum getType() {
        return type;
    }

    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setType(TypeEnum type) {
        this.type = type;
    }

    public PropertiesSchemaPropertiesValue _enum(Set<Object> _enum) {
        this._enum = _enum;
        return this;
    }

    public PropertiesSchemaPropertiesValue addEnumItem(Object _enumItem) {
        if (this._enum == null) {
            this._enum = new LinkedHashSet<>();
        }
        this._enum.add(_enumItem);
        return this;
    }

    /**
     * Implements &#39;acceptedValues&#39;
     *
     * @return _enum
     */
    @JsonProperty(JSON_PROPERTY_ENUM)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Set<Object> getEnum() {
        return _enum;
    }

    @JsonDeserialize(as = LinkedHashSet.class)
    @JsonProperty(JSON_PROPERTY_ENUM)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setEnum(Set<Object> _enum) {
        this._enum = _enum;
    }

    public PropertiesSchemaPropertiesValue format(String format) {
        this.format = format;
        return this;
    }

    /**
     * Complements implementation of &#39;type&#39;
     *
     * @return format
     */
    @JsonProperty(JSON_PROPERTY_FORMAT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getFormat() {
        return format;
    }

    @JsonProperty(JSON_PROPERTY_FORMAT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setFormat(String format) {
        this.format = format;
    }

    public PropertiesSchemaPropertiesValue contentMediaType(String contentMediaType) {
        this.contentMediaType = contentMediaType;
        return this;
    }

    /**
     * Implements &#39;mediaType&#39;
     *
     * @return contentMediaType
     */
    @JsonProperty(JSON_PROPERTY_CONTENT_MEDIA_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getContentMediaType() {
        return contentMediaType;
    }

    @JsonProperty(JSON_PROPERTY_CONTENT_MEDIA_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setContentMediaType(String contentMediaType) {
        this.contentMediaType = contentMediaType;
    }

    public PropertiesSchemaPropertiesValue maximum(BigDecimal maximum) {
        this.maximum = maximum;
        return this;
    }

    /**
     * Implements &#39;range&#39;
     *
     * @return maximum
     */
    @JsonProperty(JSON_PROPERTY_MAXIMUM)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public BigDecimal getMaximum() {
        return maximum;
    }

    @JsonProperty(JSON_PROPERTY_MAXIMUM)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setMaximum(BigDecimal maximum) {
        this.maximum = maximum;
    }

    public PropertiesSchemaPropertiesValue exclusiveMaximum(BigDecimal exclusiveMaximum) {
        this.exclusiveMaximum = exclusiveMaximum;
        return this;
    }

    /**
     * Implements &#39;range&#39;
     *
     * @return exclusiveMaximum
     */
    @JsonProperty(JSON_PROPERTY_EXCLUSIVE_MAXIMUM)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public BigDecimal getExclusiveMaximum() {
        return exclusiveMaximum;
    }

    @JsonProperty(JSON_PROPERTY_EXCLUSIVE_MAXIMUM)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setExclusiveMaximum(BigDecimal exclusiveMaximum) {
        this.exclusiveMaximum = exclusiveMaximum;
    }

    public PropertiesSchemaPropertiesValue minimum(BigDecimal minimum) {
        this.minimum = minimum;
        return this;
    }

    /**
     * Implements &#39;range&#39;
     *
     * @return minimum
     */
    @JsonProperty(JSON_PROPERTY_MINIMUM)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public BigDecimal getMinimum() {
        return minimum;
    }

    @JsonProperty(JSON_PROPERTY_MINIMUM)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setMinimum(BigDecimal minimum) {
        this.minimum = minimum;
    }

    public PropertiesSchemaPropertiesValue exclusiveMinimum(BigDecimal exclusiveMinimum) {
        this.exclusiveMinimum = exclusiveMinimum;
        return this;
    }

    /**
     * Implements &#39;range&#39;
     *
     * @return exclusiveMinimum
     */
    @JsonProperty(JSON_PROPERTY_EXCLUSIVE_MINIMUM)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public BigDecimal getExclusiveMinimum() {
        return exclusiveMinimum;
    }

    @JsonProperty(JSON_PROPERTY_EXCLUSIVE_MINIMUM)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setExclusiveMinimum(BigDecimal exclusiveMinimum) {
        this.exclusiveMinimum = exclusiveMinimum;
    }

    public PropertiesSchemaPropertiesValue pattern(String pattern) {
        this.pattern = pattern;
        return this;
    }

    /**
     * Get pattern
     *
     * @return pattern
     */
    @JsonProperty(JSON_PROPERTY_PATTERN)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getPattern() {
        return pattern;
    }

    @JsonProperty(JSON_PROPERTY_PATTERN)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public PropertiesSchemaPropertiesValue maxItems(Integer maxItems) {
        this.maxItems = maxItems;
        return this;
    }

    /**
     * Implements &#39;upperMultiplicity&#39; minimum: 0
     *
     * @return maxItems
     */
    @JsonProperty(JSON_PROPERTY_MAX_ITEMS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Integer getMaxItems() {
        return maxItems;
    }

    @JsonProperty(JSON_PROPERTY_MAX_ITEMS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setMaxItems(Integer maxItems) {
        this.maxItems = maxItems;
    }

    public PropertiesSchemaPropertiesValue minItems(Integer minItems) {
        this.minItems = minItems;
        return this;
    }

    /**
     * Implements &#39;lowerMultiplicity&#39; minimum: 0
     *
     * @return minItems
     */
    @JsonProperty(JSON_PROPERTY_MIN_ITEMS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Integer getMinItems() {
        return minItems;
    }

    @JsonProperty(JSON_PROPERTY_MIN_ITEMS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setMinItems(Integer minItems) {
        this.minItems = minItems;
    }

    public PropertiesSchemaPropertiesValue observedProperty(String observedProperty) {
        this.observedProperty = observedProperty;
        return this;
    }

    /**
     * Get observedProperty
     *
     * @return observedProperty
     */
    @JsonProperty(JSON_PROPERTY_OBSERVED_PROPERTY)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getObservedProperty() {
        return observedProperty;
    }

    @JsonProperty(JSON_PROPERTY_OBSERVED_PROPERTY)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setObservedProperty(String observedProperty) {
        this.observedProperty = observedProperty;
    }

    public PropertiesSchemaPropertiesValue observedPropertyURI(URI observedPropertyURI) {
        this.observedPropertyURI = observedPropertyURI;
        return this;
    }

    /**
     * Get observedPropertyURI
     *
     * @return observedPropertyURI
     */
    @JsonProperty(JSON_PROPERTY_OBSERVED_PROPERTY_U_R_I)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public URI getObservedPropertyURI() {
        return observedPropertyURI;
    }

    @JsonProperty(JSON_PROPERTY_OBSERVED_PROPERTY_U_R_I)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setObservedPropertyURI(URI observedPropertyURI) {
        this.observedPropertyURI = observedPropertyURI;
    }

    public PropertiesSchemaPropertiesValue uom(String uom) {
        this.uom = uom;
        return this;
    }

    /**
     * Get uom
     *
     * @return uom
     */
    @JsonProperty(JSON_PROPERTY_UOM)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getUom() {
        return uom;
    }

    @JsonProperty(JSON_PROPERTY_UOM)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setUom(String uom) {
        this.uom = uom;
    }

    public PropertiesSchemaPropertiesValue uomURI(URI uomURI) {
        this.uomURI = uomURI;
        return this;
    }

    /**
     * Get uomURI
     *
     * @return uomURI
     */
    @JsonProperty(JSON_PROPERTY_UOM_U_R_I)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public URI getUomURI() {
        return uomURI;
    }

    @JsonProperty(JSON_PROPERTY_UOM_U_R_I)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setUomURI(URI uomURI) {
        this.uomURI = uomURI;
    }

    /**
     * Return true if this propertiesSchema_properties_value object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PropertiesSchemaPropertiesValue other = (PropertiesSchemaPropertiesValue) o;
        return Objects.equals(this.title, other.title)
                && Objects.equals(this.description, other.description)
                && Objects.equals(this.type, other.type)
                && Objects.equals(this._enum, other._enum)
                && Objects.equals(this.format, other.format)
                && Objects.equals(this.contentMediaType, other.contentMediaType)
                && Objects.equals(this.maximum, other.maximum)
                && Objects.equals(this.exclusiveMaximum, other.exclusiveMaximum)
                && Objects.equals(this.minimum, other.minimum)
                && Objects.equals(this.exclusiveMinimum, other.exclusiveMinimum)
                && Objects.equals(this.pattern, other.pattern)
                && Objects.equals(this.maxItems, other.maxItems)
                && Objects.equals(this.minItems, other.minItems)
                && Objects.equals(this.observedProperty, other.observedProperty)
                && Objects.equals(this.observedPropertyURI, other.observedPropertyURI)
                && Objects.equals(this.uom, other.uom)
                && Objects.equals(this.uomURI, other.uomURI);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, type, _enum, format, contentMediaType, maximum, exclusiveMaximum, minimum, exclusiveMinimum, pattern, maxItems, minItems, observedProperty, observedPropertyURI, uom, uomURI);
    }

}
