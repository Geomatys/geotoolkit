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
import java.util.Objects;

/**
 * A single request submitted against a TaskingCapability, instructing its Actuator to act.
 *
 * <p>OGC SensorThings API Part 2: Tasking Core, section 9.2.3.</p>
 *
 * @see https://docs.ogc.org/is/17-079r1/17-079r1.html#task
 * @author Quentin Bialota (Geomatys)
 */
@JsonPropertyOrder({
    SensorThingsEntity.JSON_PROPERTY_ID,
    SensorThingsEntity.JSON_PROPERTY_SELF_LINK,
    Task.JSON_PROPERTY_CREATION_TIME,
    Task.JSON_PROPERTY_TASKING_PARAMETERS,
    Task.JSON_PROPERTY_TASKING_CAPABILITY
})
public final class Task extends SensorThingsEntity {

    public static final String JSON_PROPERTY_CREATION_TIME = "creationTime";
    private String creationTime;

    public static final String JSON_PROPERTY_TASKING_PARAMETERS = "taskingParameters";
    private Object taskingParameters;

    public static final String JSON_PROPERTY_TASKING_CAPABILITY = "TaskingCapability";
    private TaskingCapability taskingCapability;

    public Task() {
    }

    public Task creationTime(String creationTime) {
        this.creationTime = creationTime;
        return this;
    }

    /**
     * The ISO 8601 date-time this task was created. Server-generated; read-only in practice.
     * (optional)
     *
     * @return the creationTime
     */
    @JsonProperty(JSON_PROPERTY_CREATION_TIME)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getCreationTime() {
        return creationTime;
    }

    @JsonProperty(JSON_PROPERTY_CREATION_TIME)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public Task taskingParameters(Object taskingParameters) {
        this.taskingParameters = taskingParameters;
        return this;
    }

    /**
     * The parameter values for this task, matching the shape declared by the owning
     * TaskingCapability's {@code taskingParameters}. (required)
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

    public Task taskingCapability(TaskingCapability taskingCapability) {
        this.taskingCapability = taskingCapability;
        return this;
    }

    /**
     * The TaskingCapability this task is submitted against, either deep-inserted or linked by
     * {@code @iot.id}. (optional in the DTO — required by the spec on creation)
     *
     * @return the taskingCapability
     */
    @JsonProperty(JSON_PROPERTY_TASKING_CAPABILITY)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public TaskingCapability getTaskingCapability() {
        return taskingCapability;
    }

    @JsonProperty(JSON_PROPERTY_TASKING_CAPABILITY)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setTaskingCapability(TaskingCapability taskingCapability) {
        this.taskingCapability = taskingCapability;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Task other = (Task) o;
        return entityEquals(other)
                && Objects.equals(this.creationTime, other.creationTime)
                && Objects.equals(this.taskingParameters, other.taskingParameters)
                && Objects.equals(this.taskingCapability, other.taskingCapability);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityHashCode(), creationTime, taskingParameters, taskingCapability);
    }
}
