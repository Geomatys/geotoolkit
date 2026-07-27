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
 * The last known location of a Thing, encoded as a GeoJSON geometry.
 *
 * <p>OGC SensorThings API Part 1: Sensing, section 8.2.2.</p>
 *
 * @see https://docs.ogc.org/is/18-088/18-088.html#location
 * @author Quentin Bialota (Geomatys)
 */
@JsonPropertyOrder({
    SensorThingsEntity.JSON_PROPERTY_ID,
    SensorThingsEntity.JSON_PROPERTY_SELF_LINK,
    Location.JSON_PROPERTY_NAME,
    Location.JSON_PROPERTY_DESCRIPTION,
    Location.JSON_PROPERTY_ENCODING_TYPE,
    Location.JSON_PROPERTY_LOCATION,
    Location.JSON_PROPERTY_PROPERTIES
})
public final class Location extends SensorThingsEntity {

    public static final String JSON_PROPERTY_NAME = "name";
    private String name;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    private String description;

    public static final String JSON_PROPERTY_ENCODING_TYPE = "encodingType";
    private String encodingType;

    public static final String JSON_PROPERTY_LOCATION = "location";
    private Object location;

    public static final String JSON_PROPERTY_PROPERTIES = "properties";
    private Map<String, Object> properties = new HashMap<>();

    public Location() {
    }

    public Location name(String name) {
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

    public Location description(String description) {
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

    public Location encodingType(String encodingType) {
        this.encodingType = encodingType;
        return this;
    }

    /**
     * The encoding type of the {@code location} property, e.g. {@code "application/geo+json"}.
     * (required)
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

    public Location location(Object location) {
        this.location = location;
        return this;
    }

    /**
     * The location, encoded according to {@code encodingType} (e.g. a GeoJSON geometry object).
     * Modeled as {@code Object} since the JSON shape varies with {@code encodingType}. (required)
     *
     * @return the location
     */
    @JsonProperty(JSON_PROPERTY_LOCATION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Object getLocation() {
        return location;
    }

    @JsonProperty(JSON_PROPERTY_LOCATION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setLocation(Object location) {
        this.location = location;
    }

    public Location properties(Map<String, Object> properties) {
        this.properties = properties;
        return this;
    }

    public Location putPropertiesItem(String key, Object propertiesItem) {
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
        Location other = (Location) o;
        return entityEquals(other)
                && Objects.equals(this.name, other.name)
                && Objects.equals(this.description, other.description)
                && Objects.equals(this.encodingType, other.encodingType)
                && Objects.equals(this.location, other.location)
                && Objects.equals(this.properties, other.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityHashCode(), name, description, encodingType, location, properties);
    }
}
