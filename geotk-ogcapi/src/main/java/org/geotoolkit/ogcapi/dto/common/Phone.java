/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
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
package org.geotoolkit.ogcapi.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * CollectionDescContactsInnerPhonesInner
 */
@JsonPropertyOrder({
    Phone.JSON_PROPERTY_VALUE,
    Phone.JSON_PROPERTY_ROLES
})
public final class Phone extends DataTransferObject {

    public static final String JSON_PROPERTY_VALUE = "value";
    private String value;

    public static final String JSON_PROPERTY_ROLES = "roles";
    private List<String> roles = new ArrayList<>();

    public Phone() {
    }

    public Phone value(String value) {
        this.value = value;
        return this;
    }

    /**
     * The value is the phone number itself.
     *
     * @return value
     */
    @JsonProperty(JSON_PROPERTY_VALUE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public String getValue() {
        return value;
    }

    @JsonProperty(JSON_PROPERTY_VALUE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setValue(String value) {
        this.value = value;
    }


    public Phone roles(List<String> roles) {
        this.roles = roles;
        return this;
    }

    public Phone addRolesItem(String rolesItem) {
        if (this.roles == null) {
            this.roles = new ArrayList<>();
        }
        this.roles.add(rolesItem);
        return this;
    }

    /**
     * Get roles
     *
     * @return roles
     */
    @JsonProperty(JSON_PROPERTY_ROLES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public Object getRoles() {
        return roles;
    }

    @JsonProperty(JSON_PROPERTY_ROLES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    /**
     * Return true if this collectionDesc_contacts_inner_phones_inner object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Phone collectionDescContactsInnerPhonesInner = (Phone) o;
        return Objects.equals(this.value, collectionDescContactsInnerPhonesInner.value)
                && Objects.equals(this.roles, collectionDescContactsInnerPhonesInner.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, roles);
    }

}
