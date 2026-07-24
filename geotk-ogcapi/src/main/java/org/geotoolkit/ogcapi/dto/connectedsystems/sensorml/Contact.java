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
 * Contact information of a {@link ResponsibleParty}.
 *
 * @see <a href="https://docs.ogc.org/is/23-000/23-000.html">OGC SensorML 3.0 JSON Encoding</a>
 */
public class Contact extends DataTransferObject {

    public static final String JSON_PROPERTY_PHONE = "phone";
    private Phone phone;

    public static final String JSON_PROPERTY_ADDRESS = "address";
    private Address address;

    public static final String JSON_PROPERTY_WEBSITE = "website";
    private String website;

    public static final String JSON_PROPERTY_HOURS_OF_SERVICE = "hoursOfService";
    private String hoursOfService;

    public static final String JSON_PROPERTY_CONTACT_INSTRUCTIONS = "contactInstructions";
    private String contactInstructions;

    /**
     * Phone numbers. (optional)
     */
    @JsonProperty(JSON_PROPERTY_PHONE)
    public Phone getPhone() {
        return phone;
    }

    @JsonProperty(JSON_PROPERTY_PHONE)
    public void setPhone(Phone phone) {
        this.phone = phone;
    }

    public Contact phone(Phone phone) {
        setPhone(phone);
        return this;
    }

    /**
     * Postal address. (optional)
     */
    @JsonProperty(JSON_PROPERTY_ADDRESS)
    public Address getAddress() {
        return address;
    }

    @JsonProperty(JSON_PROPERTY_ADDRESS)
    public void setAddress(Address address) {
        this.address = address;
    }

    public Contact address(Address address) {
        setAddress(address);
        return this;
    }

    /**
     * Website URL. (optional)
     */
    @JsonProperty(JSON_PROPERTY_WEBSITE)
    public String getWebsite() {
        return website;
    }

    @JsonProperty(JSON_PROPERTY_WEBSITE)
    public void setWebsite(String website) {
        this.website = website;
    }

    public Contact website(String website) {
        setWebsite(website);
        return this;
    }

    /**
     * Hours of service description. (optional)
     */
    @JsonProperty(JSON_PROPERTY_HOURS_OF_SERVICE)
    public String getHoursOfService() {
        return hoursOfService;
    }

    @JsonProperty(JSON_PROPERTY_HOURS_OF_SERVICE)
    public void setHoursOfService(String hoursOfService) {
        this.hoursOfService = hoursOfService;
    }

    public Contact hoursOfService(String hoursOfService) {
        setHoursOfService(hoursOfService);
        return this;
    }

    /**
     * Additional instructions on how/when to contact. (optional)
     */
    @JsonProperty(JSON_PROPERTY_CONTACT_INSTRUCTIONS)
    public String getContactInstructions() {
        return contactInstructions;
    }

    @JsonProperty(JSON_PROPERTY_CONTACT_INSTRUCTIONS)
    public void setContactInstructions(String contactInstructions) {
        this.contactInstructions = contactInstructions;
    }

    public Contact contactInstructions(String contactInstructions) {
        setContactInstructions(contactInstructions);
        return this;
    }

}
