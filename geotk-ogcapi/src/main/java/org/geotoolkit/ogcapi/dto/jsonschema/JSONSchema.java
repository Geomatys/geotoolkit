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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * JSONSchema made by hand.
 */
@JsonPropertyOrder({
    JSONSchema.JSON_PROPERTY_TYPE,
    JSONSchema.JSON_PROPERTY_REQUIRED,
    JSONSchema.JSON_PROPERTY_PROPERTIES
})
public class JSONSchema extends DataTransferObject {


    public static final String JSON_PROPERTY_TYPE = "type";
    private JSONType type;

    public static final String JSON_PROPERTY_REQUIRED = "required";
    private List<String> required = new ArrayList<>();

    public static final String JSON_PROPERTY_PROPERTIES = "properties";
    private Map<String, JSONSchemaProperty> properties = new HashMap<>();

    public JSONSchema() {
    }

    public JSONSchema type(JSONType type) {
        this.type = type;
        return this;
    }

    /**
     * Get type
     *
     * @return type
     */
    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public JSONType getType() {
        return type;
    }

    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setType(JSONType type) {
        this.type = type;
    }

    public JSONSchema required(List<String> required) {
        this.required = required;
        return this;
    }

    public JSONSchema addRequiredItem(String requiredItem) {
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
    @JsonProperty(JSON_PROPERTY_REQUIRED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public List<String> getRequired() {
        return required;
    }

    @JsonProperty(JSON_PROPERTY_REQUIRED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setRequired(List<String> required) {
        this.required = required;
    }

    public JSONSchema properties(Map<String, JSONSchemaProperty> properties) {
        this.properties = properties;
        return this;
    }

    public JSONSchema putPropertiesItem(String key, JSONSchemaProperty propertiesItem) {
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
    @JsonProperty(JSON_PROPERTY_PROPERTIES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public Map<String, JSONSchemaProperty> getProperties() {
        return properties;
    }

    @JsonProperty(JSON_PROPERTY_PROPERTIES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setProperties(Map<String, JSONSchemaProperty> properties) {
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
        JSONSchema jsonSchema = (JSONSchema) o;
        return Objects.equals(this.type, jsonSchema.type)
                && Objects.equals(this.required, jsonSchema.required)
                && Objects.equals(this.properties, jsonSchema.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, required, properties);
    }

}
