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
 * A typed relationship between an Observation and another Observation, e.g. one derived from
 * another via post-processing.
 *
 * <p>STAplus extension.</p>
 *
 * @see https://docs.ogc.org/is/22-022r1/22-022r1.html#relation
 * @author Quentin Bialota (Geomatys)
 */
@JsonPropertyOrder({
    SensorThingsEntity.JSON_PROPERTY_ID,
    SensorThingsEntity.JSON_PROPERTY_SELF_LINK,
    Relation.JSON_PROPERTY_TYPE,
    Relation.JSON_PROPERTY_DESCRIPTION,
    Relation.JSON_PROPERTY_SUBJECT,
    Relation.JSON_PROPERTY_OBJECT
})
public final class Relation extends SensorThingsEntity {

    public static final String JSON_PROPERTY_TYPE = "type";
    private String type;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    private String description;

    public static final String JSON_PROPERTY_SUBJECT = "Subject";
    private Observation subject;

    public static final String JSON_PROPERTY_OBJECT = "Object";
    private Observation object;

    public Relation() {
    }

    public Relation type(String type) {
        this.type = type;
        return this;
    }

    /**
     * The kind of relationship linking {@code subject} to {@code object}, e.g.
     * {@code "derived-from"}. (required)
     *
     * @return the type
     */
    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getType() {
        return type;
    }

    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setType(String type) {
        this.type = type;
    }

    public Relation description(String description) {
        this.description = description;
        return this;
    }

    /**
     * A free-text description of the relationship. (optional)
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

    public Relation subject(Observation subject) {
        this.subject = subject;
        return this;
    }

    /**
     * The Observation this relation is expressed from, either deep-inserted or linked by
     * {@code @iot.id}. (optional in the DTO — required by the spec on creation)
     *
     * @return the subject
     */
    @JsonProperty(JSON_PROPERTY_SUBJECT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Observation getSubject() {
        return subject;
    }

    @JsonProperty(JSON_PROPERTY_SUBJECT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setSubject(Observation subject) {
        this.subject = subject;
    }

    public Relation object(Observation object) {
        this.object = object;
        return this;
    }

    /**
     * The Observation this relation points to, either deep-inserted or linked by
     * {@code @iot.id}. (optional in the DTO — required by the spec on creation)
     *
     * @return the object
     */
    @JsonProperty(JSON_PROPERTY_OBJECT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Observation getObject() {
        return object;
    }

    @JsonProperty(JSON_PROPERTY_OBJECT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setObject(Observation object) {
        this.object = object;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Relation other = (Relation) o;
        return entityEquals(other)
                && Objects.equals(this.type, other.type)
                && Objects.equals(this.description, other.description)
                && Objects.equals(this.subject, other.subject)
                && Objects.equals(this.object, other.object);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityHashCode(), type, description, subject, object);
    }
}
