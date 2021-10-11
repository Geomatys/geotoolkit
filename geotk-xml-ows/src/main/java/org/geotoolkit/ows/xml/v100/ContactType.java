/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.ows.xml.v100;

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.AbstractContact;


/**
 * For OWS use in the service metadata document, the optional hoursOfService and contactInstructions elements were retained, as possibly being useful in the ServiceProvider section.
 *
 * <p>Java class for ContactType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ContactType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Phone" type="{http://www.opengis.net/ows}TelephoneType" minOccurs="0"/>
 *         &lt;element name="Address" type="{http://www.opengis.net/ows}AddressType" minOccurs="0"/>
 *         &lt;element name="OnlineResource" type="{http://www.opengis.net/ows}OnlineResourceType" minOccurs="0"/>
 *         &lt;element name="HoursOfService" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ContactInstructions" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ContactType", propOrder = {
    "phone",
    "address",
    "onlineResource",
    "hoursOfService",
    "contactInstructions"
})
public class ContactType implements AbstractContact {

    @XmlElement(name = "Phone")
    private TelephoneType phone;
    @XmlElement(name = "Address")
    private AddressType address;
    @XmlElement(name = "OnlineResource")
    private OnlineResourceType onlineResource;
    @XmlElement(name = "HoursOfService")
    private String hoursOfService;
    @XmlElement(name = "ContactInstructions")
    private String contactInstructions;

    /**
     * Empty constructor used by JAXB.
     */
    ContactType() {
    }

    /**
     * Build a new Contact.
     */
    public ContactType(final TelephoneType phone, final AddressType address, final OnlineResourceType onlineResource,
            final String hoursOfService, final String contactInstructions) {
        this.address             = address;
        this.contactInstructions = contactInstructions;
        this.hoursOfService      = hoursOfService;
        this.onlineResource      = onlineResource;
        this.phone               = phone;
    }

    public ContactType(final String phone, final String fax, final String email,
            final String address, final String city, final String state,
            final String zipCode, final String country, final String hoursOfService,
            final String contactInstructions) {
        this.address        = new AddressType(address, city, state, zipCode, country, email);
        this.phone          = new TelephoneType(phone, fax);
        this.hoursOfService = hoursOfService;
        this.contactInstructions = contactInstructions;
    }

    /**
     * Gets the value of the phone property.
     */
    @Override
    public TelephoneType getPhone() {
        return phone;
    }

    /**
     * Gets the value of the address property.
     */
    @Override
    public AddressType getAddress() {
        return address;
    }

   /**
    * Gets the value of the onlineResource property.
    */
    @Override
    public OnlineResourceType getOnlineResource() {
        return onlineResource;
    }

    /**
     * Gets the value of the hoursOfService property.
     */
    @Override
    public String getHoursOfService() {
        return hoursOfService;
    }

    /**
     * Gets the value of the contactInstructions property.
     */
    @Override
    public String getContactInstructions() {
        return contactInstructions;
    }

   /**
     * Verify that this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof ContactType) {
            final ContactType that = (ContactType) object;
            return Objects.equals(this.address,             that.address)             &&
                   Objects.equals(this.contactInstructions, that.contactInstructions) &&
                   Objects.equals(this.hoursOfService,      that.hoursOfService)      &&
                   Objects.equals(this.onlineResource,      that.onlineResource)      &&
                   Objects.equals(this.phone,               that.phone);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + (this.phone != null ? this.phone.hashCode() : 0);
        hash = 23 * hash + (this.address != null ? this.address.hashCode() : 0);
        hash = 23 * hash + (this.onlineResource != null ? this.onlineResource.hashCode() : 0);
        hash = 23 * hash + (this.hoursOfService != null ? this.hoursOfService.hashCode() : 0);
        hash = 23 * hash + (this.contactInstructions != null ? this.contactInstructions.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("class:ContactType").append('\n');
        if (phone != null)
            s.append("phone=").append(phone.toString()).append('\n');
        if (address != null)
            s.append("address=").append(address.toString()).append('\n');
        if (onlineResource != null)
            s.append("onlineResource=").append(onlineResource.toString()).append('\n');
        s.append("hoursOfservice=").append(hoursOfService).append(" contactInstructions=").append(contactInstructions).append('\n');
        return s.toString();
    }


}
