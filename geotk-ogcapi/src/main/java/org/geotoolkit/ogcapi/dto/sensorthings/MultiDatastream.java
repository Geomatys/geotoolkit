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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A Datastream variant whose Observations each carry a fixed-length array of results, one per
 * ObservedProperty in {@code observedProperties}.
 *
 * <p>OGC SensorThings API Part 1: Sensing, MultiDatastream extension.</p>
 *
 * @see https://docs.ogc.org/is/18-088/18-088.html#multidatastream-extension
 * @author Quentin Bialota (Geomatys)
 */
@JsonPropertyOrder({
    SensorThingsEntity.JSON_PROPERTY_ID,
    SensorThingsEntity.JSON_PROPERTY_SELF_LINK,
    MultiDatastream.JSON_PROPERTY_NAME,
    MultiDatastream.JSON_PROPERTY_DESCRIPTION,
    MultiDatastream.JSON_PROPERTY_UNIT_OF_MEASUREMENTS,
    MultiDatastream.JSON_PROPERTY_OBSERVATION_TYPE,
    MultiDatastream.JSON_PROPERTY_MULTI_OBSERVATION_DATA_TYPES,
    MultiDatastream.JSON_PROPERTY_OBSERVED_AREA,
    MultiDatastream.JSON_PROPERTY_PHENOMENON_TIME,
    MultiDatastream.JSON_PROPERTY_RESULT_TIME,
    MultiDatastream.JSON_PROPERTY_PROPERTIES,
    MultiDatastream.JSON_PROPERTY_THING,
    MultiDatastream.JSON_PROPERTY_SENSOR,
    MultiDatastream.JSON_PROPERTY_OBSERVED_PROPERTIES
})
public final class MultiDatastream extends SensorThingsEntity {

    public static final String JSON_PROPERTY_NAME = "name";
    private String name;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    private String description;

    public static final String JSON_PROPERTY_UNIT_OF_MEASUREMENTS = "unitOfMeasurements";
    private List<UnitOfMeasurement> unitOfMeasurements = new ArrayList<>();

    public static final String JSON_PROPERTY_OBSERVATION_TYPE = "observationType";
    private String observationType;

    public static final String JSON_PROPERTY_MULTI_OBSERVATION_DATA_TYPES = "multiObservationDataTypes";
    private List<String> multiObservationDataTypes = new ArrayList<>();

    public static final String JSON_PROPERTY_OBSERVED_AREA = "observedArea";
    private Object observedArea;

    public static final String JSON_PROPERTY_PHENOMENON_TIME = "phenomenonTime";
    private String phenomenonTime;

    public static final String JSON_PROPERTY_RESULT_TIME = "resultTime";
    private String resultTime;

    public static final String JSON_PROPERTY_PROPERTIES = "properties";
    private Map<String, Object> properties = new HashMap<>();

    public static final String JSON_PROPERTY_THING = "Thing";
    private Thing thing;

    public static final String JSON_PROPERTY_SENSOR = "Sensor";
    private Sensor sensor;

    public static final String JSON_PROPERTY_OBSERVED_PROPERTIES = "ObservedProperties";
    private List<ObservedProperty> observedProperties;

    public MultiDatastream() {
    }

    public MultiDatastream name(String name) {
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

    public MultiDatastream description(String description) {
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

    public MultiDatastream unitOfMeasurements(List<UnitOfMeasurement> unitOfMeasurements) {
        this.unitOfMeasurements = unitOfMeasurements;
        return this;
    }

    public MultiDatastream addUnitOfMeasurementsItem(UnitOfMeasurement unitOfMeasurementsItem) {
        if (this.unitOfMeasurements == null) {
            this.unitOfMeasurements = new ArrayList<>();
        }
        this.unitOfMeasurements.add(unitOfMeasurementsItem);
        return this;
    }

    /**
     * The units of measurement of each observed property, in {@code observedProperties} order.
     * (required)
     *
     * @return the unitOfMeasurements
     */
    @JsonProperty(JSON_PROPERTY_UNIT_OF_MEASUREMENTS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public List<UnitOfMeasurement> getUnitOfMeasurements() {
        return unitOfMeasurements;
    }

    @JsonProperty(JSON_PROPERTY_UNIT_OF_MEASUREMENTS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setUnitOfMeasurements(List<UnitOfMeasurement> unitOfMeasurements) {
        this.unitOfMeasurements = unitOfMeasurements;
    }

    public MultiDatastream observationType(String observationType) {
        this.observationType = observationType;
        return this;
    }

    /**
     * The URI naming the OGC observation type governing the shape of {@code Observation.result}
     * in this multi-datastream (fixed to the "array of observations" complex-observation type by
     * the spec). (required)
     *
     * @return the observationType
     */
    @JsonProperty(JSON_PROPERTY_OBSERVATION_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getObservationType() {
        return observationType;
    }

    @JsonProperty(JSON_PROPERTY_OBSERVATION_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setObservationType(String observationType) {
        this.observationType = observationType;
    }

    public MultiDatastream multiObservationDataTypes(List<String> multiObservationDataTypes) {
        this.multiObservationDataTypes = multiObservationDataTypes;
        return this;
    }

    public MultiDatastream addMultiObservationDataTypesItem(String multiObservationDataTypesItem) {
        if (this.multiObservationDataTypes == null) {
            this.multiObservationDataTypes = new ArrayList<>();
        }
        this.multiObservationDataTypes.add(multiObservationDataTypesItem);
        return this;
    }

    /**
     * The observation data type of each observed property's result component, in
     * {@code observedProperties} order. (required)
     *
     * @return the multiObservationDataTypes
     */
    @JsonProperty(JSON_PROPERTY_MULTI_OBSERVATION_DATA_TYPES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public List<String> getMultiObservationDataTypes() {
        return multiObservationDataTypes;
    }

    @JsonProperty(JSON_PROPERTY_MULTI_OBSERVATION_DATA_TYPES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setMultiObservationDataTypes(List<String> multiObservationDataTypes) {
        this.multiObservationDataTypes = multiObservationDataTypes;
    }

    public MultiDatastream observedArea(Object observedArea) {
        this.observedArea = observedArea;
        return this;
    }

    /**
     * The spatial bounding box of this multi-datastream's Observations, as a GeoJSON geometry.
     * Server-computed; read-only in practice. (optional)
     *
     * @return the observedArea
     */
    @JsonProperty(JSON_PROPERTY_OBSERVED_AREA)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Object getObservedArea() {
        return observedArea;
    }

    @JsonProperty(JSON_PROPERTY_OBSERVED_AREA)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setObservedArea(Object observedArea) {
        this.observedArea = observedArea;
    }

    public MultiDatastream phenomenonTime(String phenomenonTime) {
        this.phenomenonTime = phenomenonTime;
        return this;
    }

    /**
     * The temporal interval spanning this multi-datastream's Observations' phenomenon times,
     * ISO 8601. Server-computed; read-only in practice. (optional)
     *
     * @return the phenomenonTime
     */
    @JsonProperty(JSON_PROPERTY_PHENOMENON_TIME)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getPhenomenonTime() {
        return phenomenonTime;
    }

    @JsonProperty(JSON_PROPERTY_PHENOMENON_TIME)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setPhenomenonTime(String phenomenonTime) {
        this.phenomenonTime = phenomenonTime;
    }

    public MultiDatastream resultTime(String resultTime) {
        this.resultTime = resultTime;
        return this;
    }

    /**
     * The temporal interval spanning this multi-datastream's Observations' result times,
     * ISO 8601. Server-computed; read-only in practice. (optional)
     *
     * @return the resultTime
     */
    @JsonProperty(JSON_PROPERTY_RESULT_TIME)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getResultTime() {
        return resultTime;
    }

    @JsonProperty(JSON_PROPERTY_RESULT_TIME)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setResultTime(String resultTime) {
        this.resultTime = resultTime;
    }

    public MultiDatastream properties(Map<String, Object> properties) {
        this.properties = properties;
        return this;
    }

    public MultiDatastream putPropertiesItem(String key, Object propertiesItem) {
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

    public MultiDatastream thing(Thing thing) {
        this.thing = thing;
        return this;
    }

    /**
     * The Thing hosting this multi-datastream, either deep-inserted or linked by
     * {@code @iot.id}. (optional in the DTO — required by the spec on creation)
     *
     * @return the thing
     */
    @JsonProperty(JSON_PROPERTY_THING)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Thing getThing() {
        return thing;
    }

    @JsonProperty(JSON_PROPERTY_THING)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setThing(Thing thing) {
        this.thing = thing;
    }

    public MultiDatastream sensor(Sensor sensor) {
        this.sensor = sensor;
        return this;
    }

    /**
     * The Sensor producing this multi-datastream's Observations, either deep-inserted or linked
     * by {@code @iot.id}. (optional in the DTO — required by the spec on creation)
     *
     * @return the sensor
     */
    @JsonProperty(JSON_PROPERTY_SENSOR)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Sensor getSensor() {
        return sensor;
    }

    @JsonProperty(JSON_PROPERTY_SENSOR)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }

    public MultiDatastream observedProperties(List<ObservedProperty> observedProperties) {
        this.observedProperties = observedProperties;
        return this;
    }

    public MultiDatastream addObservedPropertiesItem(ObservedProperty observedPropertiesItem) {
        if (this.observedProperties == null) {
            this.observedProperties = new ArrayList<>();
        }
        this.observedProperties.add(observedPropertiesItem);
        return this;
    }

    /**
     * The phenomena this multi-datastream's Observations measure, one per array slot of
     * {@code Observation.result}, either deep-inserted or linked by {@code @iot.id}. (optional in
     * the DTO — required by the spec on creation)
     *
     * @return the observedProperties
     */
    @JsonProperty(JSON_PROPERTY_OBSERVED_PROPERTIES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public List<ObservedProperty> getObservedProperties() {
        return observedProperties;
    }

    @JsonProperty(JSON_PROPERTY_OBSERVED_PROPERTIES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setObservedProperties(List<ObservedProperty> observedProperties) {
        this.observedProperties = observedProperties;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MultiDatastream other = (MultiDatastream) o;
        return entityEquals(other)
                && Objects.equals(this.name, other.name)
                && Objects.equals(this.description, other.description)
                && Objects.equals(this.unitOfMeasurements, other.unitOfMeasurements)
                && Objects.equals(this.observationType, other.observationType)
                && Objects.equals(this.multiObservationDataTypes, other.multiObservationDataTypes)
                && Objects.equals(this.observedArea, other.observedArea)
                && Objects.equals(this.phenomenonTime, other.phenomenonTime)
                && Objects.equals(this.resultTime, other.resultTime)
                && Objects.equals(this.properties, other.properties)
                && Objects.equals(this.thing, other.thing)
                && Objects.equals(this.sensor, other.sensor)
                && Objects.equals(this.observedProperties, other.observedProperties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityHashCode(), name, description, unitOfMeasurements, observationType,
                multiObservationDataTypes, observedArea, phenomenonTime, resultTime, properties, thing,
                sensor, observedProperties);
    }
}
