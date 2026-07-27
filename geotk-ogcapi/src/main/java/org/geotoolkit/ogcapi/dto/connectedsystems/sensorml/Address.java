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
package org.geotoolkit.ogcapi.dto.connectedsystems.sensorml;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * Postal address of a {@link Contact}.
 *
 * @see <a href="https://docs.ogc.org/is/23-000/23-000.html">OGC SensorML 3.0 JSON Encoding</a>
 */
public class Address extends DataTransferObject {

    public static final String JSON_PROPERTY_DELIVERY_POINT = "deliveryPoint";
    private String deliveryPoint;

    public static final String JSON_PROPERTY_CITY = "city";
    private String city;

    public static final String JSON_PROPERTY_ADMINISTRATIVE_AREA = "administrativeArea";
    private String administrativeArea;

    public static final String JSON_PROPERTY_POSTAL_CODE = "postalCode";
    private String postalCode;

    public static final String JSON_PROPERTY_COUNTRY = "country";
    private String country;

    public static final String JSON_PROPERTY_ELECTRONIC_MAIL_ADDRESS = "electronicMailAddress";
    private String electronicMailAddress;

    /**
     * Street address / delivery point. (optional)
     */
    @JsonProperty(JSON_PROPERTY_DELIVERY_POINT)
    public String getDeliveryPoint() {
        return deliveryPoint;
    }

    @JsonProperty(JSON_PROPERTY_DELIVERY_POINT)
    public void setDeliveryPoint(String deliveryPoint) {
        this.deliveryPoint = deliveryPoint;
    }

    public Address deliveryPoint(String deliveryPoint) {
        setDeliveryPoint(deliveryPoint);
        return this;
    }

    /**
     * City name. (optional)
     */
    @JsonProperty(JSON_PROPERTY_CITY)
    public String getCity() {
        return city;
    }

    @JsonProperty(JSON_PROPERTY_CITY)
    public void setCity(String city) {
        this.city = city;
    }

    public Address city(String city) {
        setCity(city);
        return this;
    }

    /**
     * Administrative area (state, province, region). (optional)
     */
    @JsonProperty(JSON_PROPERTY_ADMINISTRATIVE_AREA)
    public String getAdministrativeArea() {
        return administrativeArea;
    }

    @JsonProperty(JSON_PROPERTY_ADMINISTRATIVE_AREA)
    public void setAdministrativeArea(String administrativeArea) {
        this.administrativeArea = administrativeArea;
    }

    public Address administrativeArea(String administrativeArea) {
        setAdministrativeArea(administrativeArea);
        return this;
    }

    /**
     * Postal code. (optional)
     */
    @JsonProperty(JSON_PROPERTY_POSTAL_CODE)
    public String getPostalCode() {
        return postalCode;
    }

    @JsonProperty(JSON_PROPERTY_POSTAL_CODE)
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public Address postalCode(String postalCode) {
        setPostalCode(postalCode);
        return this;
    }

    /**
     * Country name. (optional)
     */
    @JsonProperty(JSON_PROPERTY_COUNTRY)
    public String getCountry() {
        return country;
    }

    @JsonProperty(JSON_PROPERTY_COUNTRY)
    public void setCountry(String country) {
        this.country = country;
    }

    public Address country(String country) {
        setCountry(country);
        return this;
    }

    /**
     * E-mail address. (optional)
     */
    @JsonProperty(JSON_PROPERTY_ELECTRONIC_MAIL_ADDRESS)
    public String getElectronicMailAddress() {
        return electronicMailAddress;
    }

    @JsonProperty(JSON_PROPERTY_ELECTRONIC_MAIL_ADDRESS)
    public void setElectronicMailAddress(String electronicMailAddress) {
        this.electronicMailAddress = electronicMailAddress;
    }

    public Address electronicMailAddress(String electronicMailAddress) {
        setElectronicMailAddress(electronicMailAddress);
        return this;
    }

}
