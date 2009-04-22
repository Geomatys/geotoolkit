/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2008, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.ows.xml.v110;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;


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
 *         &lt;element name="Phone" type="{http://www.opengis.net/ows/1.1}TelephoneType" minOccurs="0"/>
 *         &lt;element name="Address" type="{http://www.opengis.net/ows/1.1}AddressType" minOccurs="0"/>
 *         &lt;element name="OnlineResource" type="{http://www.opengis.net/ows/1.1}OnlineResourceType" minOccurs="0"/>
 *         &lt;element name="HoursOfService" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ContactInstructions" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Guilhem Legal
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ContactType", propOrder = {
    "phone",
    "address",
    "onlineResource",
    "hoursOfService",
    "contactInstructions"
})
public class ContactType {

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
    public ContactType(TelephoneType phone, AddressType address, OnlineResourceType onlineResource,
            String hoursOfService, String contactInstructions) {
        this.address             = address;
        this.contactInstructions = contactInstructions;
        this.hoursOfService      = hoursOfService;
        this.onlineResource      = onlineResource;
        this.phone               = phone;
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

    /**
     * Gets the value of the hoursOfService property.
     */
    public String getHoursOfService() {
        return hoursOfService;
    }

    /**
     * Gets the value of the contactInstructions property.
     */
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

        return Utilities.equals(this.address,             that.address)             &&
               Utilities.equals(this.contactInstructions, that.contactInstructions) &&
               Utilities.equals(this.hoursOfService,      that.hoursOfService)      &&
               Utilities.equals(this.onlineResource,      that.onlineResource)      &&
               Utilities.equals(this.phone,               that.phone);
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
