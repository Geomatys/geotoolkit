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
package org.geotoolkit.ogcapi.dto.jsonschema;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.net.URI;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * JsonSchemaPropertiesValue
 */
@JsonPropertyOrder({
    JSONSchemaProperty.JSON_PROPERTY_TITLE,
    JSONSchemaProperty.JSON_PROPERTY_DESCRIPTION,
    JSONSchemaProperty.JSON_PROPERTY_TYPE,
    JSONSchemaProperty.JSON_PROPERTY_ENUM,
    JSONSchemaProperty.JSON_PROPERTY_FORMAT,
    JSONSchemaProperty.JSON_PROPERTY_CONTENT_MEDIA_TYPE,
    JSONSchemaProperty.JSON_PROPERTY_MAXIMUM,
    JSONSchemaProperty.JSON_PROPERTY_EXCLUSIVE_MAXIMUM,
    JSONSchemaProperty.JSON_PROPERTY_MINIMUM,
    JSONSchemaProperty.JSON_PROPERTY_EXCLUSIVE_MINIMUM,
    JSONSchemaProperty.JSON_PROPERTY_PATTERN,
    JSONSchemaProperty.JSON_PROPERTY_MAX_ITEMS,
    JSONSchemaProperty.JSON_PROPERTY_MIN_ITEMS,
    JSONSchemaProperty.JSON_PROPERTY_X_OGC_DEFINITION,
    JSONSchemaProperty.JSON_PROPERTY_X_OGC_UNIT,
    JSONSchemaProperty.JSON_PROPERTY_X_OGC_UNIT_LANG,
    JSONSchemaProperty.JSON_PROPERTY_X_OGC_STATISTICS,
    JSONSchemaProperty.JSON_PROPERTY_X_OGC_PROPERTY_SEQ
})
public class JSONSchemaProperty extends DataTransferObject {

    public static final String JSON_PROPERTY_TITLE = "title";
    private String title;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    private String description;

    public static final String JSON_PROPERTY_TYPE = "type";
    private JSONType type;

    public static final String JSON_PROPERTY_ENUM = "enum";
    private Set<Object> _enum = new LinkedHashSet<>();

    public static final String JSON_PROPERTY_FORMAT = "format";
    private String format;

    public static final String JSON_PROPERTY_CONTENT_MEDIA_TYPE = "contentMediaType";
    private String contentMediaType;

    public static final String JSON_PROPERTY_MAXIMUM = "maximum";
    private Double maximum;

    public static final String JSON_PROPERTY_EXCLUSIVE_MAXIMUM = "exclusiveMaximum";
    private Double exclusiveMaximum;

    public static final String JSON_PROPERTY_MINIMUM = "minimum";
    private Double minimum;

    public static final String JSON_PROPERTY_EXCLUSIVE_MINIMUM = "exclusiveMinimum";
    private Double exclusiveMinimum;

    public static final String JSON_PROPERTY_PATTERN = "pattern";
    private String pattern;

    public static final String JSON_PROPERTY_MAX_ITEMS = "maxItems";
    private Integer maxItems;

    public static final String JSON_PROPERTY_MIN_ITEMS = "minItems";
    private Integer minItems = 0;

    public static final String JSON_PROPERTY_X_OGC_DEFINITION = "x-ogc-definition";
    private URI xOgcDefinition;

    public static final String JSON_PROPERTY_X_OGC_UNIT = "x-ogc-unit";
    private String xOgcUnit;

    public static final String JSON_PROPERTY_X_OGC_UNIT_LANG = "x-ogc-unitLang";
    private String xOgcUnitLang;

    public static final String JSON_PROPERTY_X_OGC_STATISTICS = "x-ogc-statistics";
    private RangeStatistics xOgcStatistics;

    public static final String JSON_PROPERTY_X_OGC_PROPERTY_SEQ = "x-ogc-propertySeq";
    private int xOgcPropertySeq;

    public JSONSchemaProperty() {
    }

    public JSONSchemaProperty title(String title) {
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

    public JSONSchemaProperty description(String description) {
        this.description = description;
        return this;
    }

    /**
     * Get description
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

    public JSONSchemaProperty type(JSONType type) {
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
    public JSONType getType() {
        return type;
    }

    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setType(JSONType type) {
        this.type = type;
    }

    public JSONSchemaProperty _enum(Set<Object> _enum) {
        this._enum = _enum;
        return this;
    }

    public JSONSchemaProperty addEnumItem(Object _enumItem) {
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

    public JSONSchemaProperty format(String format) {
        this.format = format;
        return this;
    }

    /**
     * Get format
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

    public JSONSchemaProperty contentMediaType(String contentMediaType) {
        this.contentMediaType = contentMediaType;
        return this;
    }

    /**
     * Get contentMediaType
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

    public JSONSchemaProperty maximum(Double maximum) {
        this.maximum = maximum;
        return this;
    }

    /**
     * Get maximum
     *
     * @return maximum
     */
    @JsonProperty(JSON_PROPERTY_MAXIMUM)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Double getMaximum() {
        return maximum;
    }

    @JsonProperty(JSON_PROPERTY_MAXIMUM)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setMaximum(Double maximum) {
        this.maximum = maximum;
    }

    public JSONSchemaProperty exclusiveMaximum(Double exclusiveMaximum) {
        this.exclusiveMaximum = exclusiveMaximum;
        return this;
    }

    /**
     * Get exclusiveMaximum
     *
     * @return exclusiveMaximum
     */
    @JsonProperty(JSON_PROPERTY_EXCLUSIVE_MAXIMUM)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Double getExclusiveMaximum() {
        return exclusiveMaximum;
    }

    @JsonProperty(JSON_PROPERTY_EXCLUSIVE_MAXIMUM)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setExclusiveMaximum(Double exclusiveMaximum) {
        this.exclusiveMaximum = exclusiveMaximum;
    }

    public JSONSchemaProperty minimum(Double minimum) {
        this.minimum = minimum;
        return this;
    }

    /**
     * Get minimum
     *
     * @return minimum
     */
    @JsonProperty(JSON_PROPERTY_MINIMUM)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Double getMinimum() {
        return minimum;
    }

    @JsonProperty(JSON_PROPERTY_MINIMUM)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setMinimum(Double minimum) {
        this.minimum = minimum;
    }

    public JSONSchemaProperty exclusiveMinimum(Double exclusiveMinimum) {
        this.exclusiveMinimum = exclusiveMinimum;
        return this;
    }

    /**
     * Get exclusiveMinimum
     *
     * @return exclusiveMinimum
     */
    @JsonProperty(JSON_PROPERTY_EXCLUSIVE_MINIMUM)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Double getExclusiveMinimum() {
        return exclusiveMinimum;
    }

    @JsonProperty(JSON_PROPERTY_EXCLUSIVE_MINIMUM)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setExclusiveMinimum(Double exclusiveMinimum) {
        this.exclusiveMinimum = exclusiveMinimum;
    }

    public JSONSchemaProperty pattern(String pattern) {
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

    public JSONSchemaProperty maxItems(Integer maxItems) {
        this.maxItems = maxItems;
        return this;
    }

    /**
     * Get maxItems minimum: 0
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

    public JSONSchemaProperty minItems(Integer minItems) {
        this.minItems = minItems;
        return this;
    }

    /**
     * Get minItems minimum: 0
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

    public JSONSchemaProperty xOgcDefinition(URI xOgcDefinition) {
        this.xOgcDefinition = xOgcDefinition;
        return this;
    }

    /**
     * Get xOgcDefinition
     *
     * @return xOgcDefinition
     */
    @JsonProperty(JSON_PROPERTY_X_OGC_DEFINITION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public URI getxOgcDefinition() {
        return xOgcDefinition;
    }

    @JsonProperty(JSON_PROPERTY_X_OGC_DEFINITION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setxOgcDefinition(URI xOgcDefinition) {
        this.xOgcDefinition = xOgcDefinition;
    }

    public JSONSchemaProperty xOgcUnit(String xOgcUnit) {
        this.xOgcUnit = xOgcUnit;
        return this;
    }

    /**
     * Get xOgcUnit
     *
     * @return xOgcUnit
     */
    @JsonProperty(JSON_PROPERTY_X_OGC_UNIT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getxOgcUnit() {
        return xOgcUnit;
    }

    @JsonProperty(JSON_PROPERTY_X_OGC_UNIT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setxOgcUnit(String xOgcUnit) {
        this.xOgcUnit = xOgcUnit;
    }

    public JSONSchemaProperty xOgcUnitLang(String xOgcUnitLang) {
        this.xOgcUnitLang = xOgcUnitLang;
        return this;
    }

    /**
     * Get xOgcUnitLang
     *
     * @return xOgcUnitLang
     */
    @JsonProperty(JSON_PROPERTY_X_OGC_UNIT_LANG)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getxOgcUnitLang() {
        return xOgcUnitLang;
    }

    @JsonProperty(JSON_PROPERTY_X_OGC_UNIT_LANG)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setxOgcUnitLang(String xOgcUnitLang) {
        this.xOgcUnitLang = xOgcUnitLang;
    }

    public JSONSchemaProperty xOgcStatistics(RangeStatistics xOgcStatistics) {
        this.xOgcStatistics = xOgcStatistics;
        return this;
    }

    /**
     * Get statistics
     *
     * @return statistics
     */
    @JsonProperty(JSON_PROPERTY_X_OGC_STATISTICS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public RangeStatistics getxOgcStatistics() {
        return xOgcStatistics;
    }

    @JsonProperty(JSON_PROPERTY_X_OGC_STATISTICS)
    public void setxOgcStatistics(RangeStatistics xOgcStatistics) {
        this.xOgcStatistics = xOgcStatistics;
    }

    public JSONSchemaProperty xOgcPropertySeq(int xOgcPropertySeq) {
        this.xOgcPropertySeq = xOgcPropertySeq;
        return this;
    }

    /**
     * Get statistics
     *
     * @return statistics
     */
    @JsonProperty(JSON_PROPERTY_X_OGC_PROPERTY_SEQ)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public int getxOgcPropertySeq() {
        return xOgcPropertySeq;
    }

    @JsonProperty(JSON_PROPERTY_X_OGC_PROPERTY_SEQ)
    public void setxOgcPropertySeq(int xOgcPropertySeq) {
        this.xOgcPropertySeq = xOgcPropertySeq;
    }

    /**
     * Return true if this json_schema_properties_value object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JSONSchemaProperty jsonSchemaPropertiesValue = (JSONSchemaProperty) o;
        return xOgcPropertySeq == jsonSchemaPropertiesValue.xOgcPropertySeq
                && Objects.equals(this.title, jsonSchemaPropertiesValue.title)
                && Objects.equals(this.description, jsonSchemaPropertiesValue.description)
                && Objects.equals(this.type, jsonSchemaPropertiesValue.type)
                && Objects.equals(this._enum, jsonSchemaPropertiesValue._enum)
                && Objects.equals(this.format, jsonSchemaPropertiesValue.format)
                && Objects.equals(this.contentMediaType, jsonSchemaPropertiesValue.contentMediaType)
                && Objects.equals(this.maximum, jsonSchemaPropertiesValue.maximum)
                && Objects.equals(this.exclusiveMaximum, jsonSchemaPropertiesValue.exclusiveMaximum)
                && Objects.equals(this.minimum, jsonSchemaPropertiesValue.minimum)
                && Objects.equals(this.exclusiveMinimum, jsonSchemaPropertiesValue.exclusiveMinimum)
                && Objects.equals(this.pattern, jsonSchemaPropertiesValue.pattern)
                && Objects.equals(this.maxItems, jsonSchemaPropertiesValue.maxItems)
                && Objects.equals(this.minItems, jsonSchemaPropertiesValue.minItems)
                && Objects.equals(this.xOgcDefinition, jsonSchemaPropertiesValue.xOgcDefinition)
                && Objects.equals(this.xOgcUnit, jsonSchemaPropertiesValue.xOgcUnit)
                && Objects.equals(this.xOgcUnitLang, jsonSchemaPropertiesValue.xOgcUnitLang)
                && Objects.equals(this.xOgcStatistics, jsonSchemaPropertiesValue.xOgcStatistics);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, type, _enum, format, contentMediaType, maximum, exclusiveMaximum, minimum, exclusiveMinimum, pattern, maxItems, minItems, xOgcDefinition, xOgcUnit, xOgcUnitLang);
    }

}
