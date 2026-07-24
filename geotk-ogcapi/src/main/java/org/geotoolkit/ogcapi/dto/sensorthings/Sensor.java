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
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
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
@XmlRootElement(name = "Sensor")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "Sensor")
public final class Sensor extends SensorThingsEntity {

    public static final String JSON_PROPERTY_NAME = "name";
    @XmlElement(name = "name")
    @jakarta.annotation.Nonnull
    private String name;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    @XmlElement(name = "description")
    @jakarta.annotation.Nonnull
    private String description;

    public static final String JSON_PROPERTY_ENCODING_TYPE = "encodingType";
    @XmlElement(name = "encodingType")
    @jakarta.annotation.Nonnull
    private String encodingType;

    public static final String JSON_PROPERTY_METADATA = "metadata";
    @XmlElement(name = "metadata")
    @jakarta.annotation.Nonnull
    private Object metadata;

    public static final String JSON_PROPERTY_PROPERTIES = "properties";
    @XmlElement(name = "properties")
    @jakarta.annotation.Nullable
    private Map<String, Object> properties = new HashMap<>();

    public Sensor() {
    }

    public Sensor name(@jakarta.annotation.Nonnull String name) {
        this.name = name;
        return this;
    }

    /**
     * A property provided by the user. (required)
     *
     * @return the name
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_NAME)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "name")
    public String getName() {
        return name;
    }

    @JsonProperty(JSON_PROPERTY_NAME)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "name")
    public void setName(@jakarta.annotation.Nonnull String name) {
        this.name = name;
    }

    public Sensor description(@jakarta.annotation.Nonnull String description) {
        this.description = description;
        return this;
    }

    /**
     * A property provided by the user. (required)
     *
     * @return the description
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "description")
    public String getDescription() {
        return description;
    }

    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "description")
    public void setDescription(@jakarta.annotation.Nonnull String description) {
        this.description = description;
    }

    public Sensor encodingType(@jakarta.annotation.Nonnull String encodingType) {
        this.encodingType = encodingType;
        return this;
    }

    /**
     * The encoding type of the {@code metadata} property. (required)
     *
     * @return the encodingType
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_ENCODING_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "encodingType")
    public String getEncodingType() {
        return encodingType;
    }

    @JsonProperty(JSON_PROPERTY_ENCODING_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "encodingType")
    public void setEncodingType(@jakarta.annotation.Nonnull String encodingType) {
        this.encodingType = encodingType;
    }

    public Sensor metadata(@jakarta.annotation.Nonnull Object metadata) {
        this.metadata = metadata;
        return this;
    }

    /**
     * The detailed description of the sensor, shaped according to {@code encodingType} (a plain
     * string, or a nested object for a structured encoding). (required)
     *
     * @return the metadata
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_METADATA)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "metadata")
    public Object getMetadata() {
        return metadata;
    }

    @JsonProperty(JSON_PROPERTY_METADATA)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "metadata")
    public void setMetadata(@jakarta.annotation.Nonnull Object metadata) {
        this.metadata = metadata;
    }

    public Sensor properties(@jakarta.annotation.Nullable Map<String, Object> properties) {
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
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_PROPERTIES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "properties")
    public Map<String, Object> getProperties() {
        return properties;
    }

    @JsonProperty(JSON_PROPERTY_PROPERTIES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "properties")
    public void setProperties(@jakarta.annotation.Nullable Map<String, Object> properties) {
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
