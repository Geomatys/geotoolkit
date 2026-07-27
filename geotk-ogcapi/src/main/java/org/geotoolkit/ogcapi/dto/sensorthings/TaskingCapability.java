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
 * The set of parameters an Actuator accepts for a Task, describing its tasking capability.
 *
 * <p>OGC SensorThings API Part 2: Tasking Core, section 9.2.2.</p>
 *
 * @see https://docs.ogc.org/is/17-079r1/17-079r1.html#taskingcapability
 * @author Quentin Bialota (Geomatys)
 */
@JsonPropertyOrder({
    SensorThingsEntity.JSON_PROPERTY_ID,
    SensorThingsEntity.JSON_PROPERTY_SELF_LINK,
    TaskingCapability.JSON_PROPERTY_NAME,
    TaskingCapability.JSON_PROPERTY_DESCRIPTION,
    TaskingCapability.JSON_PROPERTY_TASKING_PARAMETERS,
    TaskingCapability.JSON_PROPERTY_PROPERTIES,
    TaskingCapability.JSON_PROPERTY_ACTUATOR
})
public final class TaskingCapability extends SensorThingsEntity {

    public static final String JSON_PROPERTY_NAME = "name";
    private String name;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    private String description;

    public static final String JSON_PROPERTY_TASKING_PARAMETERS = "taskingParameters";
    private Object taskingParameters;

    public static final String JSON_PROPERTY_PROPERTIES = "properties";
    private Map<String, Object> properties = new HashMap<>();

    public static final String JSON_PROPERTY_ACTUATOR = "Actuator";
    private Actuator actuator;

    public TaskingCapability() {
    }

    public TaskingCapability name(String name) {
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

    public TaskingCapability description(String description) {
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

    public TaskingCapability taskingParameters(Object taskingParameters) {
        this.taskingParameters = taskingParameters;
        return this;
    }

    /**
     * A JSON Schema-like object describing the parameters a Task submitted against this
     * capability must provide. (required)
     *
     * @return the taskingParameters
     */
    @JsonProperty(JSON_PROPERTY_TASKING_PARAMETERS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Object getTaskingParameters() {
        return taskingParameters;
    }

    @JsonProperty(JSON_PROPERTY_TASKING_PARAMETERS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setTaskingParameters(Object taskingParameters) {
        this.taskingParameters = taskingParameters;
    }

    public TaskingCapability properties(Map<String, Object> properties) {
        this.properties = properties;
        return this;
    }

    public TaskingCapability putPropertiesItem(String key, Object propertiesItem) {
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

    public TaskingCapability actuator(Actuator actuator) {
        this.actuator = actuator;
        return this;
    }

    /**
     * The Actuator offering this tasking capability, either deep-inserted or linked by
     * {@code @iot.id}. (optional in the DTO — required by the spec on creation)
     *
     * @return the actuator
     */
    @JsonProperty(JSON_PROPERTY_ACTUATOR)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Actuator getActuator() {
        return actuator;
    }

    @JsonProperty(JSON_PROPERTY_ACTUATOR)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setActuator(Actuator actuator) {
        this.actuator = actuator;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TaskingCapability other = (TaskingCapability) o;
        return entityEquals(other)
                && Objects.equals(this.name, other.name)
                && Objects.equals(this.description, other.description)
                && Objects.equals(this.taskingParameters, other.taskingParameters)
                && Objects.equals(this.properties, other.properties)
                && Objects.equals(this.actuator, other.actuator);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityHashCode(), name, description, taskingParameters, properties, actuator);
    }
}
