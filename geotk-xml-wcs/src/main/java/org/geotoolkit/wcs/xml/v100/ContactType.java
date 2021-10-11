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
package org.geotoolkit.wcs.xml.v100;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.AbstractContact;


/**
 * Information required to enable contact with the responsible person and/or organization.
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
 *         &lt;element name="phone" type="{http://www.opengis.net/wcs}TelephoneType" minOccurs="0"/>
 *         &lt;element name="address" type="{http://www.opengis.net/wcs}AddressType" minOccurs="0"/>
 *         &lt;element name="onlineResource" type="{http://www.opengis.net/wcs}OnlineResourceType" minOccurs="0"/>
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
    "onlineResource"
})
public class ContactType implements AbstractContact {

    private TelephoneType phone;
    private AddressType address;
    private OnlineResourceType onlineResource;

    /**
     * Empty constructor used by JAXB
     */
     ContactType() {

     }

     /**
      * Build a new Contact
      */
     public ContactType(final TelephoneType phone, final AddressType address, final OnlineResourceType onlineResource) {
         this.address        = address;
         this.phone          = phone;
         this.onlineResource = onlineResource;
     }

     public ContactType(final String phone, final String fax, final String email,
            final String address, final String city, final String state,
            final String zipCode, final String country) {
        this.address             = new AddressType(address, city, state, zipCode, country, email);
        this.phone               = new TelephoneType(phone, fax);
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

    @Override
    public String getHoursOfService() {
        return null;
    }

    @Override
    public String getContactInstructions() {
        return null;
    }
}
