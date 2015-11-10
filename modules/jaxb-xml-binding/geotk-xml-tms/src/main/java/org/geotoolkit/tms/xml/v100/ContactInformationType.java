/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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
package org.geotoolkit.tms.xml.v100;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ContactInformationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ContactInformationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ContactPosition" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ContactVoiceTelephone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ContactFacsimileTelephone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ContactElectronicMailAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ContactPersonPrimary" type="{}ContactPersonPrimaryType" minOccurs="0"/>
 *         &lt;element name="ContactAddress" type="{}ContactAddressType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ContactInformationType", propOrder = {
    "contactPosition",
    "contactVoiceTelephone",
    "contactFacsimileTelephone",
    "contactElectronicMailAddress",
    "contactPersonPrimary",
    "contactAddress"
})
public class ContactInformationType {

    @XmlElement(name = "ContactPosition")
    protected String contactPosition;
    @XmlElement(name = "ContactVoiceTelephone")
    protected String contactVoiceTelephone;
    @XmlElement(name = "ContactFacsimileTelephone")
    protected String contactFacsimileTelephone;
    @XmlElement(name = "ContactElectronicMailAddress")
    protected String contactElectronicMailAddress;
    @XmlElement(name = "ContactPersonPrimary")
    protected ContactPersonPrimaryType contactPersonPrimary;
    @XmlElement(name = "ContactAddress")
    protected ContactAddressType contactAddress;

    /**
     * Gets the value of the contactPosition property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContactPosition() {
        return contactPosition;
    }

    /**
     * Sets the value of the contactPosition property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContactPosition(String value) {
        this.contactPosition = value;
    }

    /**
     * Gets the value of the contactVoiceTelephone property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContactVoiceTelephone() {
        return contactVoiceTelephone;
    }

    /**
     * Sets the value of the contactVoiceTelephone property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContactVoiceTelephone(String value) {
        this.contactVoiceTelephone = value;
    }

    /**
     * Gets the value of the contactFacsimileTelephone property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContactFacsimileTelephone() {
        return contactFacsimileTelephone;
    }

    /**
     * Sets the value of the contactFacsimileTelephone property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContactFacsimileTelephone(String value) {
        this.contactFacsimileTelephone = value;
    }

    /**
     * Gets the value of the contactElectronicMailAddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContactElectronicMailAddress() {
        return contactElectronicMailAddress;
    }

    /**
     * Sets the value of the contactElectronicMailAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContactElectronicMailAddress(String value) {
        this.contactElectronicMailAddress = value;
    }

    /**
     * Gets the value of the contactPersonPrimary property.
     * 
     * @return
     *     possible object is
     *     {@link ContactPersonPrimaryType }
     *     
     */
    public ContactPersonPrimaryType getContactPersonPrimary() {
        return contactPersonPrimary;
    }

    /**
     * Sets the value of the contactPersonPrimary property.
     * 
     * @param value
     *     allowed object is
     *     {@link ContactPersonPrimaryType }
     *     
     */
    public void setContactPersonPrimary(ContactPersonPrimaryType value) {
        this.contactPersonPrimary = value;
    }

    /**
     * Gets the value of the contactAddress property.
     * 
     * @return
     *     possible object is
     *     {@link ContactAddressType }
     *     
     */
    public ContactAddressType getContactAddress() {
        return contactAddress;
    }

    /**
     * Sets the value of the contactAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link ContactAddressType }
     *     
     */
    public void setContactAddress(ContactAddressType value) {
        this.contactAddress = value;
    }

}
