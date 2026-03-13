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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.*;
import jakarta.xml.bind.annotation.*;
import java.math.BigDecimal;
import java.net.URI;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import org.geotoolkit.ogcapi.model.DataTransferObject;

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
@XmlRootElement(name = "PropertiesSchemaPropertiesValue")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "PropertiesSchemaPropertiesValue")
public final class PropertiesSchemaPropertiesValue extends DataTransferObject {

    public static final String JSON_PROPERTY_TITLE = "title";
    @XmlElement(name = "title")
    @jakarta.annotation.Nullable
    private String title;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    @XmlElement(name = "description")
    @jakarta.annotation.Nullable
    private String description;

    /**
     * Gets or Sets type
     */
    @XmlType(name = "TypeEnum")
    @XmlEnum(String.class)
    public enum TypeEnum {
        @XmlEnumValue("array")
        ARRAY(String.valueOf("array")),
        @XmlEnumValue("boolean")
        BOOLEAN(String.valueOf("boolean")),
        @XmlEnumValue("integer")
        INTEGER(String.valueOf("integer")),
        @XmlEnumValue("null")
        NULL(String.valueOf("null")),
        @XmlEnumValue("number")
        NUMBER(String.valueOf("number")),
        @XmlEnumValue("object")
        OBJECT(String.valueOf("object")),
        @XmlEnumValue("string")
        STRING(String.valueOf("string"));

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
    @jakarta.annotation.Nullable
    private TypeEnum type;

    public static final String JSON_PROPERTY_ENUM = "enum";
    @XmlElement(name = "enum")
    @jakarta.annotation.Nullable
    private Set<Object> _enum = new LinkedHashSet<>();

    public static final String JSON_PROPERTY_FORMAT = "format";
    @XmlElement(name = "format")
    @jakarta.annotation.Nullable
    private String format;

    public static final String JSON_PROPERTY_CONTENT_MEDIA_TYPE = "contentMediaType";
    @XmlElement(name = "contentMediaType")
    @jakarta.annotation.Nullable
    private String contentMediaType;

    public static final String JSON_PROPERTY_MAXIMUM = "maximum";
    @XmlElement(name = "maximum")
    @jakarta.annotation.Nullable
    private BigDecimal maximum;

    public static final String JSON_PROPERTY_EXCLUSIVE_MAXIMUM = "exclusiveMaximum";
    @XmlElement(name = "exclusiveMaximum")
    @jakarta.annotation.Nullable
    private BigDecimal exclusiveMaximum;

    public static final String JSON_PROPERTY_MINIMUM = "minimum";
    @XmlElement(name = "minimum")
    @jakarta.annotation.Nullable
    private BigDecimal minimum;

    public static final String JSON_PROPERTY_EXCLUSIVE_MINIMUM = "exclusiveMinimum";
    @XmlElement(name = "exclusiveMinimum")
    @jakarta.annotation.Nullable
    private BigDecimal exclusiveMinimum;

    public static final String JSON_PROPERTY_PATTERN = "pattern";
    @XmlElement(name = "pattern")
    @jakarta.annotation.Nullable
    private String pattern;

    public static final String JSON_PROPERTY_MAX_ITEMS = "maxItems";
    @XmlElement(name = "maxItems")
    @jakarta.annotation.Nullable
    private Integer maxItems;

    public static final String JSON_PROPERTY_MIN_ITEMS = "minItems";
    @XmlElement(name = "minItems")
    @jakarta.annotation.Nullable
    private Integer minItems = 0;

    public static final String JSON_PROPERTY_OBSERVED_PROPERTY = "observedProperty";
    @XmlElement(name = "observedProperty")
    @jakarta.annotation.Nullable
    private String observedProperty;

    public static final String JSON_PROPERTY_OBSERVED_PROPERTY_U_R_I = "observedPropertyURI";
    @XmlElement(name = "observedPropertyURI")
    @jakarta.annotation.Nullable
    private URI observedPropertyURI;

    public static final String JSON_PROPERTY_UOM = "uom";
    @XmlElement(name = "uom")
    @jakarta.annotation.Nullable
    private String uom;

    public static final String JSON_PROPERTY_UOM_U_R_I = "uomURI";
    @XmlElement(name = "uomURI")
    @jakarta.annotation.Nullable
    private URI uomURI;

    public PropertiesSchemaPropertiesValue() {
    }

    public PropertiesSchemaPropertiesValue title(@jakarta.annotation.Nullable String title) {
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

    public PropertiesSchemaPropertiesValue description(@jakarta.annotation.Nullable String description) {
        this.description = description;
        return this;
    }

    /**
     * Implements &#39;description&#39;
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

    public PropertiesSchemaPropertiesValue type(@jakarta.annotation.Nullable TypeEnum type) {
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
    public TypeEnum getType() {
        return type;
    }

    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "type")
    public void setType(@jakarta.annotation.Nullable TypeEnum type) {
        this.type = type;
    }

    public PropertiesSchemaPropertiesValue _enum(@jakarta.annotation.Nullable Set<Object> _enum) {
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
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_ENUM)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "enum")
    @JacksonXmlElementWrapper(useWrapping = false)
    public Set<Object> getEnum() {
        return _enum;
    }

    @JsonDeserialize(as = LinkedHashSet.class)
    @JsonProperty(JSON_PROPERTY_ENUM)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "enum")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setEnum(@jakarta.annotation.Nullable Set<Object> _enum) {
        this._enum = _enum;
    }

    public PropertiesSchemaPropertiesValue format(@jakarta.annotation.Nullable String format) {
        this.format = format;
        return this;
    }

    /**
     * Complements implementation of &#39;type&#39;
     *
     * @return format
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_FORMAT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "format")
    public String getFormat() {
        return format;
    }

    @JsonProperty(JSON_PROPERTY_FORMAT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "format")
    public void setFormat(@jakarta.annotation.Nullable String format) {
        this.format = format;
    }

    public PropertiesSchemaPropertiesValue contentMediaType(@jakarta.annotation.Nullable String contentMediaType) {
        this.contentMediaType = contentMediaType;
        return this;
    }

    /**
     * Implements &#39;mediaType&#39;
     *
     * @return contentMediaType
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_CONTENT_MEDIA_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "contentMediaType")
    public String getContentMediaType() {
        return contentMediaType;
    }

    @JsonProperty(JSON_PROPERTY_CONTENT_MEDIA_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "contentMediaType")
    public void setContentMediaType(@jakarta.annotation.Nullable String contentMediaType) {
        this.contentMediaType = contentMediaType;
    }

    public PropertiesSchemaPropertiesValue maximum(@jakarta.annotation.Nullable BigDecimal maximum) {
        this.maximum = maximum;
        return this;
    }

    /**
     * Implements &#39;range&#39;
     *
     * @return maximum
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_MAXIMUM)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "maximum")
    public BigDecimal getMaximum() {
        return maximum;
    }

    @JsonProperty(JSON_PROPERTY_MAXIMUM)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "maximum")
    public void setMaximum(@jakarta.annotation.Nullable BigDecimal maximum) {
        this.maximum = maximum;
    }

    public PropertiesSchemaPropertiesValue exclusiveMaximum(@jakarta.annotation.Nullable BigDecimal exclusiveMaximum) {
        this.exclusiveMaximum = exclusiveMaximum;
        return this;
    }

    /**
     * Implements &#39;range&#39;
     *
     * @return exclusiveMaximum
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_EXCLUSIVE_MAXIMUM)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "exclusiveMaximum")
    public BigDecimal getExclusiveMaximum() {
        return exclusiveMaximum;
    }

    @JsonProperty(JSON_PROPERTY_EXCLUSIVE_MAXIMUM)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "exclusiveMaximum")
    public void setExclusiveMaximum(@jakarta.annotation.Nullable BigDecimal exclusiveMaximum) {
        this.exclusiveMaximum = exclusiveMaximum;
    }

    public PropertiesSchemaPropertiesValue minimum(@jakarta.annotation.Nullable BigDecimal minimum) {
        this.minimum = minimum;
        return this;
    }

    /**
     * Implements &#39;range&#39;
     *
     * @return minimum
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_MINIMUM)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "minimum")
    public BigDecimal getMinimum() {
        return minimum;
    }

    @JsonProperty(JSON_PROPERTY_MINIMUM)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "minimum")
    public void setMinimum(@jakarta.annotation.Nullable BigDecimal minimum) {
        this.minimum = minimum;
    }

    public PropertiesSchemaPropertiesValue exclusiveMinimum(@jakarta.annotation.Nullable BigDecimal exclusiveMinimum) {
        this.exclusiveMinimum = exclusiveMinimum;
        return this;
    }

    /**
     * Implements &#39;range&#39;
     *
     * @return exclusiveMinimum
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_EXCLUSIVE_MINIMUM)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "exclusiveMinimum")
    public BigDecimal getExclusiveMinimum() {
        return exclusiveMinimum;
    }

    @JsonProperty(JSON_PROPERTY_EXCLUSIVE_MINIMUM)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "exclusiveMinimum")
    public void setExclusiveMinimum(@jakarta.annotation.Nullable BigDecimal exclusiveMinimum) {
        this.exclusiveMinimum = exclusiveMinimum;
    }

    public PropertiesSchemaPropertiesValue pattern(@jakarta.annotation.Nullable String pattern) {
        this.pattern = pattern;
        return this;
    }

    /**
     * Get pattern
     *
     * @return pattern
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_PATTERN)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "pattern")
    public String getPattern() {
        return pattern;
    }

    @JsonProperty(JSON_PROPERTY_PATTERN)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "pattern")
    public void setPattern(@jakarta.annotation.Nullable String pattern) {
        this.pattern = pattern;
    }

    public PropertiesSchemaPropertiesValue maxItems(@jakarta.annotation.Nullable Integer maxItems) {
        this.maxItems = maxItems;
        return this;
    }

    /**
     * Implements &#39;upperMultiplicity&#39; minimum: 0
     *
     * @return maxItems
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_MAX_ITEMS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "maxItems")
    public Integer getMaxItems() {
        return maxItems;
    }

    @JsonProperty(JSON_PROPERTY_MAX_ITEMS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "maxItems")
    public void setMaxItems(@jakarta.annotation.Nullable Integer maxItems) {
        this.maxItems = maxItems;
    }

    public PropertiesSchemaPropertiesValue minItems(@jakarta.annotation.Nullable Integer minItems) {
        this.minItems = minItems;
        return this;
    }

    /**
     * Implements &#39;lowerMultiplicity&#39; minimum: 0
     *
     * @return minItems
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_MIN_ITEMS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "minItems")
    public Integer getMinItems() {
        return minItems;
    }

    @JsonProperty(JSON_PROPERTY_MIN_ITEMS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "minItems")
    public void setMinItems(@jakarta.annotation.Nullable Integer minItems) {
        this.minItems = minItems;
    }

    public PropertiesSchemaPropertiesValue observedProperty(@jakarta.annotation.Nullable String observedProperty) {
        this.observedProperty = observedProperty;
        return this;
    }

    /**
     * Get observedProperty
     *
     * @return observedProperty
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_OBSERVED_PROPERTY)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "observedProperty")
    public String getObservedProperty() {
        return observedProperty;
    }

    @JsonProperty(JSON_PROPERTY_OBSERVED_PROPERTY)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "observedProperty")
    public void setObservedProperty(@jakarta.annotation.Nullable String observedProperty) {
        this.observedProperty = observedProperty;
    }

    public PropertiesSchemaPropertiesValue observedPropertyURI(@jakarta.annotation.Nullable URI observedPropertyURI) {
        this.observedPropertyURI = observedPropertyURI;
        return this;
    }

    /**
     * Get observedPropertyURI
     *
     * @return observedPropertyURI
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_OBSERVED_PROPERTY_U_R_I)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "observedPropertyURI")
    public URI getObservedPropertyURI() {
        return observedPropertyURI;
    }

    @JsonProperty(JSON_PROPERTY_OBSERVED_PROPERTY_U_R_I)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "observedPropertyURI")
    public void setObservedPropertyURI(@jakarta.annotation.Nullable URI observedPropertyURI) {
        this.observedPropertyURI = observedPropertyURI;
    }

    public PropertiesSchemaPropertiesValue uom(@jakarta.annotation.Nullable String uom) {
        this.uom = uom;
        return this;
    }

    /**
     * Get uom
     *
     * @return uom
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_UOM)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "uom")
    public String getUom() {
        return uom;
    }

    @JsonProperty(JSON_PROPERTY_UOM)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "uom")
    public void setUom(@jakarta.annotation.Nullable String uom) {
        this.uom = uom;
    }

    public PropertiesSchemaPropertiesValue uomURI(@jakarta.annotation.Nullable URI uomURI) {
        this.uomURI = uomURI;
        return this;
    }

    /**
     * Get uomURI
     *
     * @return uomURI
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_UOM_U_R_I)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "uomURI")
    public URI getUomURI() {
        return uomURI;
    }

    @JsonProperty(JSON_PROPERTY_UOM_U_R_I)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "uomURI")
    public void setUomURI(@jakarta.annotation.Nullable URI uomURI) {
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
