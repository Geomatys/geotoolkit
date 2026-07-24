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
@XmlRootElement(name = "Task")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "Task")
public final class Task extends SensorThingsEntity {

    public static final String JSON_PROPERTY_CREATION_TIME = "creationTime";
    @XmlElement(name = "creationTime")
    @jakarta.annotation.Nullable
    private String creationTime;

    public static final String JSON_PROPERTY_TASKING_PARAMETERS = "taskingParameters";
    @XmlElement(name = "taskingParameters")
    @jakarta.annotation.Nonnull
    private Object taskingParameters;

    public static final String JSON_PROPERTY_TASKING_CAPABILITY = "TaskingCapability";
    @XmlElement(name = "taskingCapability")
    @jakarta.annotation.Nullable
    private TaskingCapability taskingCapability;

    public Task() {
    }

    public Task creationTime(@jakarta.annotation.Nullable String creationTime) {
        this.creationTime = creationTime;
        return this;
    }

    /**
     * The ISO 8601 date-time this task was created. Server-generated; read-only in practice.
     * (optional)
     *
     * @return the creationTime
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_CREATION_TIME)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "creationTime")
    public String getCreationTime() {
        return creationTime;
    }

    @JsonProperty(JSON_PROPERTY_CREATION_TIME)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "creationTime")
    public void setCreationTime(@jakarta.annotation.Nullable String creationTime) {
        this.creationTime = creationTime;
    }

    public Task taskingParameters(@jakarta.annotation.Nonnull Object taskingParameters) {
        this.taskingParameters = taskingParameters;
        return this;
    }

    /**
     * The parameter values for this task, matching the shape declared by the owning
     * TaskingCapability's {@code taskingParameters}. (required)
     *
     * @return the taskingParameters
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_TASKING_PARAMETERS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "taskingParameters")
    public Object getTaskingParameters() {
        return taskingParameters;
    }

    @JsonProperty(JSON_PROPERTY_TASKING_PARAMETERS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "taskingParameters")
    public void setTaskingParameters(@jakarta.annotation.Nonnull Object taskingParameters) {
        this.taskingParameters = taskingParameters;
    }

    public Task taskingCapability(@jakarta.annotation.Nullable TaskingCapability taskingCapability) {
        this.taskingCapability = taskingCapability;
        return this;
    }

    /**
     * The TaskingCapability this task is submitted against, either deep-inserted or linked by
     * {@code @iot.id}. (optional in the DTO — required by the spec on creation)
     *
     * @return the taskingCapability
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_TASKING_CAPABILITY)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "TaskingCapability")
    public TaskingCapability getTaskingCapability() {
        return taskingCapability;
    }

    @JsonProperty(JSON_PROPERTY_TASKING_CAPABILITY)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "TaskingCapability")
    public void setTaskingCapability(@jakarta.annotation.Nullable TaskingCapability taskingCapability) {
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
