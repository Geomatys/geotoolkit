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
package org.geotoolkit.ogcapi.dto.dggs;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.net.URI;
import java.util.Objects;
import org.geotoolkit.ogcapi.dto.jsonschema.JSONSchemaProperty;

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
public final class SchemaProperty extends JSONSchemaProperty {

    public static final String JSON_PROPERTY_X_OGC_DEFINITION = "x-ogc-definition";
    private URI xOgcDefinition;

    public static final String JSON_PROPERTY_X_OGC_UNIT = "x-ogc-unit";
    private String xOgcUnit;

    public static final String JSON_PROPERTY_X_OGC_UNIT_LANG = "x-ogc-unitLang";
    private String xOgcUnitLang;

    public SchemaProperty() {
    }

    public SchemaProperty xOgcDefinition(URI xOgcDefinition) {
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

    public SchemaProperty xOgcUnit(String xOgcUnit) {
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

    public SchemaProperty xOgcUnitLang(String xOgcUnitLang) {
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
        return super.equals(o)
                && Objects.equals(this.xOgcDefinition, dggsJsonSchemaPropertiesValue.xOgcDefinition)
                && Objects.equals(this.xOgcUnit, dggsJsonSchemaPropertiesValue.xOgcUnit)
                && Objects.equals(this.xOgcUnitLang, dggsJsonSchemaPropertiesValue.xOgcUnitLang);
    }

    @Override
    public int hashCode() {
        return super.hashCode() + Objects.hash(xOgcDefinition, xOgcUnit, xOgcUnitLang);
    }

}
