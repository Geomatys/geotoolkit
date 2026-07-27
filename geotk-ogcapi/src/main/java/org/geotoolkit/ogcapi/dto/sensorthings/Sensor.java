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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * An instrument that observes a property or phenomenon.
 *
 * <p>OGC SensorThings API Part 1: Sensing, section 8.2.5.</p>
 *
 * @see https://docs.ogc.org/is/18-088/18-088.html#sensor
 * @author Quentin Bialota (Geomatys)
 */
@JsonPropertyOrder({
    SensorThingsEntity.JSON_PROPERTY_ID,
    SensorThingsEntity.JSON_PROPERTY_SELF_LINK,
    Sensor.JSON_PROPERTY_NAME,
    Sensor.JSON_PROPERTY_DESCRIPTION,
    Sensor.JSON_PROPERTY_ENCODING_TYPE,
    Sensor.JSON_PROPERTY_METADATA,
    Sensor.JSON_PROPERTY_PROPERTIES
})
public final class Sensor extends SensorThingsEntity {

    public static final String JSON_PROPERTY_NAME = "name";
    private String name;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    private String description;

    public static final String JSON_PROPERTY_ENCODING_TYPE = "encodingType";
    private String encodingType;

    public static final String JSON_PROPERTY_METADATA = "metadata";
    private Object metadata;

    public static final String JSON_PROPERTY_PROPERTIES = "properties";
    private Map<String, Object> properties = new HashMap<>();

    public Sensor() {
    }

    public Sensor name(String name) {
        this.name = name;
        return this;
    }

    /**
     * A property provided by the user. (required)
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

    public Sensor description(String description) {
        this.description = description;
        return this;
    }

    /**
     * A property provided by the user. (required)
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

    public Sensor encodingType(String encodingType) {
        this.encodingType = encodingType;
        return this;
    }

    /**
     * The encoding type of the {@code metadata} property. (required)
     *
     * @return the encodingType
     */
    @JsonProperty(JSON_PROPERTY_ENCODING_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getEncodingType() {
        return encodingType;
    }

    @JsonProperty(JSON_PROPERTY_ENCODING_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setEncodingType(String encodingType) {
        this.encodingType = encodingType;
    }

    public Sensor metadata(Object metadata) {
        this.metadata = metadata;
        return this;
    }

    /**
     * The detailed description of the sensor, shaped according to {@code encodingType} (a plain
     * string, or a nested object for a structured encoding). (required)
     *
     * @return the metadata
     */
    @JsonProperty(JSON_PROPERTY_METADATA)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Object getMetadata() {
        return metadata;
    }

    @JsonProperty(JSON_PROPERTY_METADATA)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setMetadata(Object metadata) {
        this.metadata = metadata;
    }

    public Sensor properties(Map<String, Object> properties) {
        this.properties = properties;
        return this;
    }

    public Sensor putPropertiesItem(String key, Object propertiesItem) {
        if (this.properties == null) {
            this.properties = new HashMap<>();
        }
        this.properties.put(key, propertiesItem);
        return this;
    }

    /**
     * A JSON object containing user-annotated properties. (optional)
     *
     * @return the properties
     */
    @JsonProperty(JSON_PROPERTY_PROPERTIES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Map<String, Object> getProperties() {
        return properties;
    }

    @JsonProperty(JSON_PROPERTY_PROPERTIES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Sensor other = (Sensor) o;
        return entityEquals(other)
                && Objects.equals(this.name, other.name)
                && Objects.equals(this.description, other.description)
                && Objects.equals(this.encodingType, other.encodingType)
                && Objects.equals(this.metadata, other.metadata)
                && Objects.equals(this.properties, other.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityHashCode(), name, description, encodingType, metadata, properties);
    }
}
