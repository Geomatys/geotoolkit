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
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ContactType", propOrder = {
    "phone",
    "address",
    "onlineResource"
})
public class ContactType {

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
     public ContactType(TelephoneType phone, AddressType address, OnlineResourceType onlineResource) {
         this.address        = address;
         this.phone          = phone;
         this.onlineResource = onlineResource;
     }
    
    /**
     * Gets the value of the phone property.
     */
    public TelephoneType getPhone() {
        return phone;
    }

    /**
     * Gets the value of the address property.
     */
    public AddressType getAddress() {
        return address;
    }

    /**
     * Gets the value of the onlineResource property.
     */
    public OnlineResourceType getOnlineResource() {
        return onlineResource;
    }
}
