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
 * The real-world entity an Observation's result is ultimately about, when it differs from the
 * Thing hosting the Datastream (e.g. a river reach whose water level is observed by a sensor on a
 * nearby gauge station).
 *
 * <p>OGC SensorThings API Part 1: Sensing, section 8.2.4.</p>
 *
 * @see https://docs.ogc.org/is/18-088/18-088.html#featureofinterest
 * @author Quentin Bialota (Geomatys)
 */
@JsonPropertyOrder({
    SensorThingsEntity.JSON_PROPERTY_ID,
    SensorThingsEntity.JSON_PROPERTY_SELF_LINK,
    FeatureOfInterest.JSON_PROPERTY_NAME,
    FeatureOfInterest.JSON_PROPERTY_DESCRIPTION,
    FeatureOfInterest.JSON_PROPERTY_ENCODING_TYPE,
    FeatureOfInterest.JSON_PROPERTY_FEATURE,
    FeatureOfInterest.JSON_PROPERTY_PROPERTIES
})
@XmlRootElement(name = "FeatureOfInterest")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "FeatureOfInterest")
public final class FeatureOfInterest extends SensorThingsEntity {

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

    public static final String JSON_PROPERTY_FEATURE = "feature";
    @XmlElement(name = "feature")
    @jakarta.annotation.Nonnull
    private Object feature;

    public static final String JSON_PROPERTY_PROPERTIES = "properties";
    @XmlElement(name = "properties")
    @jakarta.annotation.Nullable
    private Map<String, Object> properties = new HashMap<>();

    public FeatureOfInterest() {
    }

    public FeatureOfInterest name(@jakarta.annotation.Nonnull String name) {
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

    public FeatureOfInterest description(@jakarta.annotation.Nonnull String description) {
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

    public FeatureOfInterest encodingType(@jakarta.annotation.Nonnull String encodingType) {
        this.encodingType = encodingType;
        return this;
    }

    /**
     * The encoding type of the {@code feature} property, e.g. {@code "application/geo+json"}.
     * (required)
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

    public FeatureOfInterest feature(@jakarta.annotation.Nonnull Object feature) {
        this.feature = feature;
        return this;
    }

    /**
     * The feature of interest, encoded according to {@code encodingType} (e.g. a GeoJSON
     * geometry object). Modeled as {@code Object} since the JSON shape varies with
     * {@code encodingType}. (required)
     *
     * @return the feature
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_FEATURE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "feature")
    public Object getFeature() {
        return feature;
    }

    @JsonProperty(JSON_PROPERTY_FEATURE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "feature")
    public void setFeature(@jakarta.annotation.Nonnull Object feature) {
        this.feature = feature;
    }

    public FeatureOfInterest properties(@jakarta.annotation.Nullable Map<String, Object> properties) {
        this.properties = properties;
        return this;
    }

    public FeatureOfInterest putPropertiesItem(String key, Object propertiesItem) {
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
        FeatureOfInterest other = (FeatureOfInterest) o;
        return entityEquals(other)
                && Objects.equals(this.name, other.name)
                && Objects.equals(this.description, other.description)
                && Objects.equals(this.encodingType, other.encodingType)
                && Objects.equals(this.feature, other.feature)
                && Objects.equals(this.properties, other.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityHashCode(), name, description, encodingType, feature, properties);
    }
}
