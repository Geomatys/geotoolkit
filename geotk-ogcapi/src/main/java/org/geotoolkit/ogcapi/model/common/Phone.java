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
package org.geotoolkit.ogcapi.model.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.geotoolkit.ogcapi.model.DataTransferObject;

/**
 * CollectionDescContactsInnerPhonesInner
 */
@JsonPropertyOrder({
    Phone.JSON_PROPERTY_VALUE,
    Phone.JSON_PROPERTY_ROLES
})
@XmlRootElement(name = "CollectionDescContactsInnerPhonesInner")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "CollectionDescContactsInnerPhonesInner")
public final class Phone extends DataTransferObject {

    public static final String JSON_PROPERTY_VALUE = "value";
    @XmlElement(name = "value")
    @jakarta.annotation.Nonnull
    private String value;

    public static final String JSON_PROPERTY_ROLES = "roles";
    @XmlElement(name = "roles")
    @jakarta.annotation.Nullable
    private List<String> roles = new ArrayList<>();

    public Phone() {
    }

    public Phone value(@jakarta.annotation.Nonnull String value) {
        this.value = value;
        return this;
    }

    /**
     * The value is the phone number itself.
     *
     * @return value
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_VALUE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "value")
    public String getValue() {
        return value;
    }

    @JsonProperty(JSON_PROPERTY_VALUE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "value")
    public void setValue(@jakarta.annotation.Nonnull String value) {
        this.value = value;
    }


    public Phone roles(@jakarta.annotation.Nullable List<String> roles) {
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
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_ROLES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "roles")
    public Object getRoles() {
        return roles;
    }

    @JsonProperty(JSON_PROPERTY_ROLES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "roles")
    public void setRoles(@jakarta.annotation.Nullable List<String> roles) {
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
