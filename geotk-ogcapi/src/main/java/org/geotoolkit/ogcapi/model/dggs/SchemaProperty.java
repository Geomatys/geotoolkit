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
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
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
import java.net.URI;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import org.geotoolkit.ogcapi.model.DataTransferObject;

/**
 * DggsJsonSchemaPropertiesValue
 */
@JsonPropertyOrder({
    SchemaProperty.JSON_PROPERTY_TITLE,
    SchemaProperty.JSON_PROPERTY_DESCRIPTION,
    SchemaProperty.JSON_PROPERTY_TYPE,
    SchemaProperty.JSON_PROPERTY_ENUM,
    SchemaProperty.JSON_PROPERTY_FORMAT,
    SchemaProperty.JSON_PROPERTY_CONTENT_MEDIA_TYPE,
    SchemaProperty.JSON_PROPERTY_MAXIMUM,
    SchemaProperty.JSON_PROPERTY_EXCLUSIVE_MAXIMUM,
    SchemaProperty.JSON_PROPERTY_MINIMUM,
    SchemaProperty.JSON_PROPERTY_EXCLUSIVE_MINIMUM,
    SchemaProperty.JSON_PROPERTY_PATTERN,
    SchemaProperty.JSON_PROPERTY_MAX_ITEMS,
    SchemaProperty.JSON_PROPERTY_MIN_ITEMS,
    SchemaProperty.JSON_PROPERTY_X_OGC_DEFINITION,
    SchemaProperty.JSON_PROPERTY_X_OGC_UNIT,
    SchemaProperty.JSON_PROPERTY_X_OGC_UNIT_LANG
})
@XmlRootElement(name = "DggsJsonSchemaPropertiesValue")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "DggsJsonSchemaPropertiesValue")
public final class SchemaProperty extends DataTransferObject {

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
    private Double maximum;

    public static final String JSON_PROPERTY_EXCLUSIVE_MAXIMUM = "exclusiveMaximum";
    @XmlElement(name = "exclusiveMaximum")
    @jakarta.annotation.Nullable
    private Double exclusiveMaximum;

    public static final String JSON_PROPERTY_MINIMUM = "minimum";
    @XmlElement(name = "minimum")
    @jakarta.annotation.Nullable
    private Double minimum;

    public static final String JSON_PROPERTY_EXCLUSIVE_MINIMUM = "exclusiveMinimum";
    @XmlElement(name = "exclusiveMinimum")
    @jakarta.annotation.Nullable
    private Double exclusiveMinimum;

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

    public static final String JSON_PROPERTY_X_OGC_DEFINITION = "x-ogc-definition";
    @XmlElement(name = "x-ogc-definition")
    @jakarta.annotation.Nullable
    private URI xOgcDefinition;

    public static final String JSON_PROPERTY_X_OGC_UNIT = "x-ogc-unit";
    @XmlElement(name = "x-ogc-unit")
    @jakarta.annotation.Nullable
    private String xOgcUnit;

    public static final String JSON_PROPERTY_X_OGC_UNIT_LANG = "x-ogc-unitLang";
    @XmlElement(name = "x-ogc-unitLang")
    @jakarta.annotation.Nullable
    private String xOgcUnitLang;

    public SchemaProperty() {
    }

    public SchemaProperty title(@jakarta.annotation.Nullable String title) {
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

    public SchemaProperty description(@jakarta.annotation.Nullable String description) {
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

    public SchemaProperty type(@jakarta.annotation.Nullable TypeEnum type) {
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

    public SchemaProperty _enum(@jakarta.annotation.Nullable Set<Object> _enum) {
        this._enum = _enum;
        return this;
    }

    public SchemaProperty addEnumItem(Object _enumItem) {
        if (this._enum == null) {
            this._enum = new LinkedHashSet<>();
        }
        this._enum.add(_enumItem);
        return this;
    }

    /**
     * Get _enum
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

    public SchemaProperty format(@jakarta.annotation.Nullable String format) {
        this.format = format;
        return this;
    }

    /**
     * Get format
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

    public SchemaProperty contentMediaType(@jakarta.annotation.Nullable String contentMediaType) {
        this.contentMediaType = contentMediaType;
        return this;
    }

    /**
     * Get contentMediaType
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

    public SchemaProperty maximum(@jakarta.annotation.Nullable Double maximum) {
        this.maximum = maximum;
        return this;
    }

    /**
     * Get maximum
     *
     * @return maximum
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_MAXIMUM)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "maximum")
    public Double getMaximum() {
        return maximum;
    }

    @JsonProperty(JSON_PROPERTY_MAXIMUM)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "maximum")
    public void setMaximum(@jakarta.annotation.Nullable Double maximum) {
        this.maximum = maximum;
    }

    public SchemaProperty exclusiveMaximum(@jakarta.annotation.Nullable Double exclusiveMaximum) {
        this.exclusiveMaximum = exclusiveMaximum;
        return this;
    }

    /**
     * Get exclusiveMaximum
     *
     * @return exclusiveMaximum
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_EXCLUSIVE_MAXIMUM)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "exclusiveMaximum")
    public Double getExclusiveMaximum() {
        return exclusiveMaximum;
    }

    @JsonProperty(JSON_PROPERTY_EXCLUSIVE_MAXIMUM)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "exclusiveMaximum")
    public void setExclusiveMaximum(@jakarta.annotation.Nullable Double exclusiveMaximum) {
        this.exclusiveMaximum = exclusiveMaximum;
    }

    public SchemaProperty minimum(@jakarta.annotation.Nullable Double minimum) {
        this.minimum = minimum;
        return this;
    }

    /**
     * Get minimum
     *
     * @return minimum
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_MINIMUM)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "minimum")
    public Double getMinimum() {
        return minimum;
    }

    @JsonProperty(JSON_PROPERTY_MINIMUM)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "minimum")
    public void setMinimum(@jakarta.annotation.Nullable Double minimum) {
        this.minimum = minimum;
    }

    public SchemaProperty exclusiveMinimum(@jakarta.annotation.Nullable Double exclusiveMinimum) {
        this.exclusiveMinimum = exclusiveMinimum;
        return this;
    }

    /**
     * Get exclusiveMinimum
     *
     * @return exclusiveMinimum
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_EXCLUSIVE_MINIMUM)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "exclusiveMinimum")
    public Double getExclusiveMinimum() {
        return exclusiveMinimum;
    }

    @JsonProperty(JSON_PROPERTY_EXCLUSIVE_MINIMUM)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "exclusiveMinimum")
    public void setExclusiveMinimum(@jakarta.annotation.Nullable Double exclusiveMinimum) {
        this.exclusiveMinimum = exclusiveMinimum;
    }

    public SchemaProperty pattern(@jakarta.annotation.Nullable String pattern) {
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

    public SchemaProperty maxItems(@jakarta.annotation.Nullable Integer maxItems) {
        this.maxItems = maxItems;
        return this;
    }

    /**
     * Get maxItems minimum: 0
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

    public SchemaProperty minItems(@jakarta.annotation.Nullable Integer minItems) {
        this.minItems = minItems;
        return this;
    }

    /**
     * Get minItems minimum: 0
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

    public SchemaProperty xOgcDefinition(@jakarta.annotation.Nullable URI xOgcDefinition) {
        this.xOgcDefinition = xOgcDefinition;
        return this;
    }

    /**
     * Get xOgcDefinition
     *
     * @return xOgcDefinition
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_X_OGC_DEFINITION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "x-ogc-definition")
    public URI getxOgcDefinition() {
        return xOgcDefinition;
    }

    @JsonProperty(JSON_PROPERTY_X_OGC_DEFINITION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "x-ogc-definition")
    public void setxOgcDefinition(@jakarta.annotation.Nullable URI xOgcDefinition) {
        this.xOgcDefinition = xOgcDefinition;
    }

    public SchemaProperty xOgcUnit(@jakarta.annotation.Nullable String xOgcUnit) {
        this.xOgcUnit = xOgcUnit;
        return this;
    }

    /**
     * Get xOgcUnit
     *
     * @return xOgcUnit
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_X_OGC_UNIT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "x-ogc-unit")
    public String getxOgcUnit() {
        return xOgcUnit;
    }

    @JsonProperty(JSON_PROPERTY_X_OGC_UNIT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "x-ogc-unit")
    public void setxOgcUnit(@jakarta.annotation.Nullable String xOgcUnit) {
        this.xOgcUnit = xOgcUnit;
    }

    public SchemaProperty xOgcUnitLang(@jakarta.annotation.Nullable String xOgcUnitLang) {
        this.xOgcUnitLang = xOgcUnitLang;
        return this;
    }

    /**
     * Get xOgcUnitLang
     *
     * @return xOgcUnitLang
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_X_OGC_UNIT_LANG)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "x-ogc-unitLang")
    public String getxOgcUnitLang() {
        return xOgcUnitLang;
    }

    @JsonProperty(JSON_PROPERTY_X_OGC_UNIT_LANG)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "x-ogc-unitLang")
    public void setxOgcUnitLang(@jakarta.annotation.Nullable String xOgcUnitLang) {
        this.xOgcUnitLang = xOgcUnitLang;
    }

    /**
     * Return true if this dggs_json_schema_properties_value object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SchemaProperty dggsJsonSchemaPropertiesValue = (SchemaProperty) o;
        return Objects.equals(this.title, dggsJsonSchemaPropertiesValue.title)
                && Objects.equals(this.description, dggsJsonSchemaPropertiesValue.description)
                && Objects.equals(this.type, dggsJsonSchemaPropertiesValue.type)
                && Objects.equals(this._enum, dggsJsonSchemaPropertiesValue._enum)
                && Objects.equals(this.format, dggsJsonSchemaPropertiesValue.format)
                && Objects.equals(this.contentMediaType, dggsJsonSchemaPropertiesValue.contentMediaType)
                && Objects.equals(this.maximum, dggsJsonSchemaPropertiesValue.maximum)
                && Objects.equals(this.exclusiveMaximum, dggsJsonSchemaPropertiesValue.exclusiveMaximum)
                && Objects.equals(this.minimum, dggsJsonSchemaPropertiesValue.minimum)
                && Objects.equals(this.exclusiveMinimum, dggsJsonSchemaPropertiesValue.exclusiveMinimum)
                && Objects.equals(this.pattern, dggsJsonSchemaPropertiesValue.pattern)
                && Objects.equals(this.maxItems, dggsJsonSchemaPropertiesValue.maxItems)
                && Objects.equals(this.minItems, dggsJsonSchemaPropertiesValue.minItems)
                && Objects.equals(this.xOgcDefinition, dggsJsonSchemaPropertiesValue.xOgcDefinition)
                && Objects.equals(this.xOgcUnit, dggsJsonSchemaPropertiesValue.xOgcUnit)
                && Objects.equals(this.xOgcUnitLang, dggsJsonSchemaPropertiesValue.xOgcUnitLang);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, type, _enum, format, contentMediaType, maximum, exclusiveMaximum, minimum, exclusiveMinimum, pattern, maxItems, minItems, xOgcDefinition, xOgcUnit, xOgcUnitLang);
    }

}
