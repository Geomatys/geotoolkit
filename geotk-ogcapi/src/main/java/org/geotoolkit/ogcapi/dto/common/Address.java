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
 * Contact addresses
 */
@JsonPropertyOrder({
    Address.JSON_PROPERTY_DELIVERY_POINT,
    Address.JSON_PROPERTY_CITY,
    Address.JSON_PROPERTY_ADMINISTRATIVE_AREA,
    Address.JSON_PROPERTY_POSTAL_CODE,
    Address.JSON_PROPERTY_COUNTRY,
    Address.JSON_PROPERTY_ROLES
})
public final class Address extends DataTransferObject {

    public static final String JSON_PROPERTY_DELIVERY_POINT = "deliveryPoint";
    private List<String> deliveryPoint = new ArrayList<>();

    public static final String JSON_PROPERTY_CITY = "city";
    private String city;

    public static final String JSON_PROPERTY_ADMINISTRATIVE_AREA = "administrativeArea";
    private String administrativeArea;

    public static final String JSON_PROPERTY_POSTAL_CODE = "postalCode";
    private String postalCode;

    public static final String JSON_PROPERTY_COUNTRY = "country";
    private String country;

    public static final String JSON_PROPERTY_ROLES = "roles";
    private Object roles = null;

    public Address() {
    }

    public Address deliveryPoint(List<String> deliveryPoint) {
        this.deliveryPoint = deliveryPoint;
        return this;
    }

    public Address addDeliveryPointItem(String deliveryPointItem) {
        if (this.deliveryPoint == null) {
            this.deliveryPoint = new ArrayList<>();
        }
        this.deliveryPoint.add(deliveryPointItem);
        return this;
    }

    /**
     * Address lines for the location.
     *
     * @return deliveryPoint
     */
    @JsonProperty(JSON_PROPERTY_DELIVERY_POINT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public List<String> getDeliveryPoint() {
        return deliveryPoint;
    }

    @JsonProperty(JSON_PROPERTY_DELIVERY_POINT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setDeliveryPoint(List<String> deliveryPoint) {
        this.deliveryPoint = deliveryPoint;
    }

    public Address city(String city) {
        this.city = city;
        return this;
    }

    /**
     * City for the location.
     *
     * @return city
     */
    @JsonProperty(JSON_PROPERTY_CITY)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getCity() {
        return city;
    }

    @JsonProperty(JSON_PROPERTY_CITY)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setCity(String city) {
        this.city = city;
    }

    public Address administrativeArea(String administrativeArea) {
        this.administrativeArea = administrativeArea;
        return this;
    }

    /**
     * State or province of the location.
     *
     * @return administrativeArea
     */
    @JsonProperty(JSON_PROPERTY_ADMINISTRATIVE_AREA)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getAdministrativeArea() {
        return administrativeArea;
    }

    @JsonProperty(JSON_PROPERTY_ADMINISTRATIVE_AREA)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setAdministrativeArea(String administrativeArea) {
        this.administrativeArea = administrativeArea;
    }

    public Address postalCode(String postalCode) {
        this.postalCode = postalCode;
        return this;
    }

    /**
     * ZIP or other postal code.
     *
     * @return postalCode
     */
    @JsonProperty(JSON_PROPERTY_POSTAL_CODE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getPostalCode() {
        return postalCode;
    }

    @JsonProperty(JSON_PROPERTY_POSTAL_CODE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public Address country(String country) {
        this.country = country;
        return this;
    }

    /**
     * Country of the physical address. ISO 3166-1 is recommended.
     *
     * @return country
     */
    @JsonProperty(JSON_PROPERTY_COUNTRY)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getCountry() {
        return country;
    }

    @JsonProperty(JSON_PROPERTY_COUNTRY)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setCountry(String country) {
        this.country = country;
    }

    public Address roles(Object roles) {
        this.roles = roles;
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
    public void setRoles(Object roles) {
        this.roles = roles;
    }

    /**
     * Return true if this collectionDesc_contacts_inner_addresses_inner object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Address collectionDescContactsInnerAddressesInner = (Address) o;
        return Objects.equals(this.deliveryPoint, collectionDescContactsInnerAddressesInner.deliveryPoint)
                && Objects.equals(this.city, collectionDescContactsInnerAddressesInner.city)
                && Objects.equals(this.administrativeArea, collectionDescContactsInnerAddressesInner.administrativeArea)
                && Objects.equals(this.postalCode, collectionDescContactsInnerAddressesInner.postalCode)
                && Objects.equals(this.country, collectionDescContactsInnerAddressesInner.country)
                && Objects.equals(this.roles, collectionDescContactsInnerAddressesInner.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deliveryPoint, city, administrativeArea, postalCode, country, roles);
    }

}
