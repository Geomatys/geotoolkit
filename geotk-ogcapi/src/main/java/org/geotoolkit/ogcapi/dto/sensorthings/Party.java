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
 * The individual or institution responsible for creating/owning a Datastream, MultiDatastream or
 * Thing.
 *
 * <p>STAplus extension.</p>
 *
 * @see https://docs.ogc.org/is/22-022r1/22-022r1.html#party
 * @author Quentin Bialota (Geomatys)
 */
@JsonPropertyOrder({
    SensorThingsEntity.JSON_PROPERTY_ID,
    SensorThingsEntity.JSON_PROPERTY_SELF_LINK,
    Party.JSON_PROPERTY_DISPLAY_NAME,
    Party.JSON_PROPERTY_ROLE,
    Party.JSON_PROPERTY_DESCRIPTION,
    Party.JSON_PROPERTY_AUTH_ID
})
public final class Party extends SensorThingsEntity {

    public static final String JSON_PROPERTY_DISPLAY_NAME = "displayName";
    private String displayName;

    public static final String JSON_PROPERTY_ROLE = "role";
    private String role;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    private String description;

    public static final String JSON_PROPERTY_AUTH_ID = "authId";
    private String authId;

    public Party() {
    }

    public Party displayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    /**
     * The human-readable name of the party. (optional)
     *
     * @return the displayName
     */
    @JsonProperty(JSON_PROPERTY_DISPLAY_NAME)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getDisplayName() {
        return displayName;
    }

    @JsonProperty(JSON_PROPERTY_DISPLAY_NAME)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Party role(String role) {
        this.role = role;
        return this;
    }

    /**
     * The role of the party: {@code "individual"} or {@code "institution"}. (required)
     *
     * @return the role
     */
    @JsonProperty(JSON_PROPERTY_ROLE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getRole() {
        return role;
    }

    @JsonProperty(JSON_PROPERTY_ROLE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setRole(String role) {
        this.role = role;
    }

    public Party description(String description) {
        this.description = description;
        return this;
    }

    /**
     * A free-text description of the party. (optional)
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

    public Party authId(String authId) {
        this.authId = authId;
        return this;
    }

    /**
     * A URI uniquely identifying the party (e.g. an ORCID or a ROR identifier). (optional)
     *
     * @return the authId
     */
    @JsonProperty(JSON_PROPERTY_AUTH_ID)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getAuthId() {
        return authId;
    }

    @JsonProperty(JSON_PROPERTY_AUTH_ID)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setAuthId(String authId) {
        this.authId = authId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Party other = (Party) o;
        return entityEquals(other)
                && Objects.equals(this.displayName, other.displayName)
                && Objects.equals(this.role, other.role)
                && Objects.equals(this.description, other.description)
                && Objects.equals(this.authId, other.authId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityHashCode(), displayName, role, description, authId);
    }
}
