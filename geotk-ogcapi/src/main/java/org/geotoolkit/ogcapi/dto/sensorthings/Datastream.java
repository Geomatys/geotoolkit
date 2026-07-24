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
 * A collection of Observations sharing the same Thing, Sensor and ObservedProperty.
 *
 * <p>OGC SensorThings API Part 1: Sensing, section 8.2.1. The {@code party} and {@code license}
 * navigation fields are the STAplus extension.</p>
 *
 * @see https://docs.ogc.org/is/18-088/18-088.html#datastream
 * @author Quentin Bialota (Geomatys)
 */
@JsonPropertyOrder({
    SensorThingsEntity.JSON_PROPERTY_ID,
    SensorThingsEntity.JSON_PROPERTY_SELF_LINK,
    Datastream.JSON_PROPERTY_NAME,
    Datastream.JSON_PROPERTY_DESCRIPTION,
    Datastream.JSON_PROPERTY_UNIT_OF_MEASUREMENT,
    Datastream.JSON_PROPERTY_OBSERVATION_TYPE,
    Datastream.JSON_PROPERTY_OBSERVED_AREA,
    Datastream.JSON_PROPERTY_PHENOMENON_TIME,
    Datastream.JSON_PROPERTY_RESULT_TIME,
    Datastream.JSON_PROPERTY_PROPERTIES,
    Datastream.JSON_PROPERTY_THING,
    Datastream.JSON_PROPERTY_SENSOR,
    Datastream.JSON_PROPERTY_OBSERVED_PROPERTY,
    Datastream.JSON_PROPERTY_PARTY,
    Datastream.JSON_PROPERTY_LICENSE
})
@XmlRootElement(name = "Datastream")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "Datastream")
public final class Datastream extends SensorThingsEntity {

    public static final String JSON_PROPERTY_NAME = "name";
    @XmlElement(name = "name")
    @jakarta.annotation.Nonnull
    private String name;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    @XmlElement(name = "description")
    @jakarta.annotation.Nonnull
    private String description;

    public static final String JSON_PROPERTY_UNIT_OF_MEASUREMENT = "unitOfMeasurement";
    @XmlElement(name = "unitOfMeasurement")
    @jakarta.annotation.Nonnull
    private UnitOfMeasurement unitOfMeasurement;

    public static final String JSON_PROPERTY_OBSERVATION_TYPE = "observationType";
    @XmlElement(name = "observationType")
    @jakarta.annotation.Nonnull
    private String observationType;

    public static final String JSON_PROPERTY_OBSERVED_AREA = "observedArea";
    @XmlElement(name = "observedArea")
    @jakarta.annotation.Nullable
    private Object observedArea;

    public static final String JSON_PROPERTY_PHENOMENON_TIME = "phenomenonTime";
    @XmlElement(name = "phenomenonTime")
    @jakarta.annotation.Nullable
    private String phenomenonTime;

    public static final String JSON_PROPERTY_RESULT_TIME = "resultTime";
    @XmlElement(name = "resultTime")
    @jakarta.annotation.Nullable
    private String resultTime;

    public static final String JSON_PROPERTY_PROPERTIES = "properties";
    @XmlElement(name = "properties")
    @jakarta.annotation.Nullable
    private Map<String, Object> properties = new HashMap<>();

    public static final String JSON_PROPERTY_THING = "Thing";
    @XmlElement(name = "thing")
    @jakarta.annotation.Nullable
    private Thing thing;

    public static final String JSON_PROPERTY_SENSOR = "Sensor";
    @XmlElement(name = "sensor")
    @jakarta.annotation.Nullable
    private Sensor sensor;

    public static final String JSON_PROPERTY_OBSERVED_PROPERTY = "ObservedProperty";
    @XmlElement(name = "observedProperty")
    @jakarta.annotation.Nullable
    private ObservedProperty observedProperty;

    public static final String JSON_PROPERTY_PARTY = "Party";
    @XmlElement(name = "party")
    @jakarta.annotation.Nullable
    private Party party;

    public static final String JSON_PROPERTY_LICENSE = "License";
    @XmlElement(name = "license")
    @jakarta.annotation.Nullable
    private License license;

    public Datastream() {
    }

    public Datastream name(@jakarta.annotation.Nonnull String name) {
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

    public Datastream description(@jakarta.annotation.Nonnull String description) {
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

    public Datastream unitOfMeasurement(@jakarta.annotation.Nonnull UnitOfMeasurement unitOfMeasurement) {
        this.unitOfMeasurement = unitOfMeasurement;
        return this;
    }

    /**
     * The unit of measurement of the datastream's observation results. (required)
     *
     * @return the unitOfMeasurement
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_UNIT_OF_MEASUREMENT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "unitOfMeasurement")
    public UnitOfMeasurement getUnitOfMeasurement() {
        return unitOfMeasurement;
    }

    @JsonProperty(JSON_PROPERTY_UNIT_OF_MEASUREMENT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "unitOfMeasurement")
    public void setUnitOfMeasurement(@jakarta.annotation.Nonnull UnitOfMeasurement unitOfMeasurement) {
        this.unitOfMeasurement = unitOfMeasurement;
    }

    public Datastream observationType(@jakarta.annotation.Nonnull String observationType) {
        this.observationType = observationType;
        return this;
    }

    /**
     * The URI naming the OGC observation type governing the shape of {@code Observation.result}
     * in this datastream. (required)
     *
     * @return the observationType
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_OBSERVATION_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "observationType")
    public String getObservationType() {
        return observationType;
    }

    @JsonProperty(JSON_PROPERTY_OBSERVATION_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "observationType")
    public void setObservationType(@jakarta.annotation.Nonnull String observationType) {
        this.observationType = observationType;
    }

    public Datastream observedArea(@jakarta.annotation.Nullable Object observedArea) {
        this.observedArea = observedArea;
        return this;
    }

    /**
     * The spatial bounding box of this datastream's Observations, as a GeoJSON geometry.
     * Server-computed; read-only in practice. (optional)
     *
     * @return the observedArea
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_OBSERVED_AREA)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "observedArea")
    public Object getObservedArea() {
        return observedArea;
    }

    @JsonProperty(JSON_PROPERTY_OBSERVED_AREA)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "observedArea")
    public void setObservedArea(@jakarta.annotation.Nullable Object observedArea) {
        this.observedArea = observedArea;
    }

    public Datastream phenomenonTime(@jakarta.annotation.Nullable String phenomenonTime) {
        this.phenomenonTime = phenomenonTime;
        return this;
    }

    /**
     * The temporal interval spanning this datastream's Observations' phenomenon times, ISO 8601.
     * Server-computed; read-only in practice. (optional)
     *
     * @return the phenomenonTime
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_PHENOMENON_TIME)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "phenomenonTime")
    public String getPhenomenonTime() {
        return phenomenonTime;
    }

    @JsonProperty(JSON_PROPERTY_PHENOMENON_TIME)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "phenomenonTime")
    public void setPhenomenonTime(@jakarta.annotation.Nullable String phenomenonTime) {
        this.phenomenonTime = phenomenonTime;
    }

    public Datastream resultTime(@jakarta.annotation.Nullable String resultTime) {
        this.resultTime = resultTime;
        return this;
    }

    /**
     * The temporal interval spanning this datastream's Observations' result times, ISO 8601.
     * Server-computed; read-only in practice. (optional)
     *
     * @return the resultTime
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_RESULT_TIME)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "resultTime")
    public String getResultTime() {
        return resultTime;
    }

    @JsonProperty(JSON_PROPERTY_RESULT_TIME)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "resultTime")
    public void setResultTime(@jakarta.annotation.Nullable String resultTime) {
        this.resultTime = resultTime;
    }

    public Datastream properties(@jakarta.annotation.Nullable Map<String, Object> properties) {
        this.properties = properties;
        return this;
    }

    public Datastream putPropertiesItem(String key, Object propertiesItem) {
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

    public Datastream thing(@jakarta.annotation.Nullable Thing thing) {
        this.thing = thing;
        return this;
    }

    /**
     * The Thing hosting this datastream, either deep-inserted or linked by {@code @iot.id}.
     * (optional in the DTO — required by the spec on creation, but omitted on normal reads where
     * it is exposed only as a navigation link)
     *
     * @return the thing
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_THING)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "Thing")
    public Thing getThing() {
        return thing;
    }

    @JsonProperty(JSON_PROPERTY_THING)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "Thing")
    public void setThing(@jakarta.annotation.Nullable Thing thing) {
        this.thing = thing;
    }

    public Datastream sensor(@jakarta.annotation.Nullable Sensor sensor) {
        this.sensor = sensor;
        return this;
    }

    /**
     * The Sensor producing this datastream's Observations, either deep-inserted or linked by
     * {@code @iot.id}. (optional in the DTO — required by the spec on creation)
     *
     * @return the sensor
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_SENSOR)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "Sensor")
    public Sensor getSensor() {
        return sensor;
    }

    @JsonProperty(JSON_PROPERTY_SENSOR)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "Sensor")
    public void setSensor(@jakarta.annotation.Nullable Sensor sensor) {
        this.sensor = sensor;
    }

    public Datastream observedProperty(@jakarta.annotation.Nullable ObservedProperty observedProperty) {
        this.observedProperty = observedProperty;
        return this;
    }

    /**
     * The phenomenon this datastream's Observations measure, either deep-inserted or linked by
     * {@code @iot.id}. (optional in the DTO — required by the spec on creation)
     *
     * @return the observedProperty
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_OBSERVED_PROPERTY)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "ObservedProperty")
    public ObservedProperty getObservedProperty() {
        return observedProperty;
    }

    @JsonProperty(JSON_PROPERTY_OBSERVED_PROPERTY)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "ObservedProperty")
    public void setObservedProperty(@jakarta.annotation.Nullable ObservedProperty observedProperty) {
        this.observedProperty = observedProperty;
    }

    public Datastream party(@jakarta.annotation.Nullable Party party) {
        this.party = party;
        return this;
    }

    /**
     * The party owning this datastream, either deep-inserted or linked by {@code @iot.id}.
     * STAplus extension. (optional)
     *
     * @return the party
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_PARTY)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "Party")
    public Party getParty() {
        return party;
    }

    @JsonProperty(JSON_PROPERTY_PARTY)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "Party")
    public void setParty(@jakarta.annotation.Nullable Party party) {
        this.party = party;
    }

    public Datastream license(@jakarta.annotation.Nullable License license) {
        this.license = license;
        return this;
    }

    /**
     * The usage license of this datastream, either deep-inserted or linked by {@code @iot.id}.
     * STAplus extension. (optional)
     *
     * @return the license
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_LICENSE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "License")
    public License getLicense() {
        return license;
    }

    @JsonProperty(JSON_PROPERTY_LICENSE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "License")
    public void setLicense(@jakarta.annotation.Nullable License license) {
        this.license = license;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Datastream other = (Datastream) o;
        return entityEquals(other)
                && Objects.equals(this.name, other.name)
                && Objects.equals(this.description, other.description)
                && Objects.equals(this.unitOfMeasurement, other.unitOfMeasurement)
                && Objects.equals(this.observationType, other.observationType)
                && Objects.equals(this.observedArea, other.observedArea)
                && Objects.equals(this.phenomenonTime, other.phenomenonTime)
                && Objects.equals(this.resultTime, other.resultTime)
                && Objects.equals(this.properties, other.properties)
                && Objects.equals(this.thing, other.thing)
                && Objects.equals(this.sensor, other.sensor)
                && Objects.equals(this.observedProperty, other.observedProperty)
                && Objects.equals(this.party, other.party)
                && Objects.equals(this.license, other.license);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityHashCode(), name, description, unitOfMeasurement, observationType,
                observedArea, phenomenonTime, resultTime, properties, thing, sensor, observedProperty,
                party, license);
    }
}
