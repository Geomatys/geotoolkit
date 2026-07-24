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
import jakarta.xml.bind.annotation.XmlElement;
import java.util.Objects;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * Common control information shared by every OGC SensorThings API entity
 * (Part 1: Sensing, Part 2: Tasking Core, and the STAplus extension).
 *
 * <p>{@code id} is server-generated control information: a client never sets it when creating a
 * new entity, and only sets it (with every other field left {@code null}) to link an existing
 * entity into a deep-insert body, e.g. {@code new Datastream().id(1)} serializes to
 * {@code {"@iot.id": 1}}.</p>
 *
 * @see https://docs.ogc.org/is/18-088/18-088.html
 * @author Quentin Bialota (Geomatys)
 */
public abstract class SensorThingsEntity extends DataTransferObject {

    public static final String JSON_PROPERTY_ID = "@iot.id";
    public static final String JSON_PROPERTY_SELF_LINK = "@iot.selfLink";

    @XmlElement(name = "id")
    @jakarta.annotation.Nullable
    private Object id;

    @XmlElement(name = "selfLink")
    @jakarta.annotation.Nullable
    private String selfLink;

    /**
     * ponytail: {@code T} is inferred from the call's target type (an assignment, a field/method
     * argument, or a return statement expecting the concrete subclass) — it is NOT inferred from
     * further chained calls. Call this last, e.g. {@code new Datastream().id(1)} passed straight
     * into a nav-property setter, not {@code new Datastream().id(1).name(...)} (won't compile:
     * without a target type, {@code T} resolves to {@code SensorThingsEntity}).
     */
    @SuppressWarnings("unchecked")
    public <T extends SensorThingsEntity> T id(@jakarta.annotation.Nullable Object id) {
        this.id = id;
        return (T) this;
    }

    /**
     * Server-generated control information identifying this entity. {@code null} on entities
     * being created, present on entities read back from the server or used to link to an
     * existing entity. (optional)
     *
     * @return the id
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_ID)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Object getId() {
        return id;
    }

    @JsonProperty(JSON_PROPERTY_ID)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setId(@jakarta.annotation.Nullable Object id) {
        this.id = id;
    }

    /**
     * Server-generated control information: the canonical URL of this entity. Read-only in
     * practice; a value set by the client is never meaningful to the server. (optional)
     *
     * @return the selfLink
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_SELF_LINK)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getSelfLink() {
        return selfLink;
    }

    @JsonProperty(JSON_PROPERTY_SELF_LINK)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setSelfLink(@jakarta.annotation.Nullable String selfLink) {
        this.selfLink = selfLink;
    }

    protected boolean entityEquals(SensorThingsEntity other) {
        return Objects.equals(this.id, other.id) && Objects.equals(this.selfLink, other.selfLink);
    }

    protected int entityHashCode() {
        return Objects.hash(id, selfLink);
    }
}
