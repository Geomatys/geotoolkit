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
package org.geotoolkit.ebrim.xml.v250;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * Mapping of the same named interface in ebRIM.
 * 			
 * 
 * <p>Java class for PostalAddressType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PostalAddressType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *         &lt;element ref="{urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5}Slot"/>
 *       &lt;/sequence>
 *       &lt;attribute name="city" type="{urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5}ShortName" />
 *       &lt;attribute name="country" type="{urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5}ShortName" />
 *       &lt;attribute name="postalCode" type="{urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5}ShortName" />
 *       &lt;attribute name="stateOrProvince" type="{urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5}ShortName" />
 *       &lt;attribute name="street" type="{urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5}ShortName" />
 *       &lt;attribute name="streetNumber" type="{urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5}String32" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PostalAddressType", propOrder = {
    "slot"
})
public class PostalAddressType {

    @XmlElement(name = "Slot")
    private List<SlotType> slot;
    @XmlAttribute
    private String city;
    @XmlAttribute
    private String country;
    @XmlAttribute
    private String postalCode;
    @XmlAttribute
    private String stateOrProvince;
    @XmlAttribute
    private String street;
    @XmlAttribute
    private String streetNumber;

    /**
     * Gets the value of the slot property.
     * 
     */
    public List<SlotType> getSlot() {
        if (slot == null) {
            slot = new ArrayList<SlotType>();
        }
        return this.slot;
    }

    /**
     * Sets the value of the slot property.
     */
    public void setSlot(SlotType slot) {
        if (this.slot == null) {
            this.slot = new ArrayList<SlotType>();
        }
        this.slot.add(slot);
    }
    
     /**
     * Sets the value of the slot property.
     */
    public void setSlot(List<SlotType> slot) {
        this.slot = slot;
    }

    
    /**
     * Gets the value of the city property.
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets the value of the city property.
     */
    public void setCity(String value) {
        this.city = value;
    }

    /**
     * Gets the value of the country property.
     */
    public String getCountry() {
        return country;
    }

    /**
     * Sets the value of the country property.
     */
    public void setCountry(String value) {
        this.country = value;
    }

    /**
     * Gets the value of the postalCode property.
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Sets the value of the postalCode property.
     */
    public void setPostalCode(String value) {
        this.postalCode = value;
    }

    /**
     * Gets the value of the stateOrProvince property.
     */
    public String getStateOrProvince() {
        return stateOrProvince;
    }

    /**
     * Sets the value of the stateOrProvince property.
     */
    public void setStateOrProvince(String value) {
        this.stateOrProvince = value;
    }

    /**
     * Gets the value of the street property.
     */
    public String getStreet() {
        return street;
    }

    /**
     * Sets the value of the street property.
     */
    public void setStreet(String value) {
        this.street = value;
    }

    /**
     * Gets the value of the streetNumber property.
     */
    public String getStreetNumber() {
        return streetNumber;
    }

    /**
     * Sets the value of the streetNumber property.
     */
    public void setStreetNumber(String value) {
        this.streetNumber = value;
    }

}
