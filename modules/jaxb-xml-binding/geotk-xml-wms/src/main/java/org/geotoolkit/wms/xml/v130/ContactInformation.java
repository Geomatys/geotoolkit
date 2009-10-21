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
package org.geotoolkit.wms.xml.v130;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/wms}ContactPersonPrimary" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/wms}ContactPosition" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/wms}ContactAddress" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/wms}ContactVoiceTelephone" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/wms}ContactFacsimileTelephone" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/wms}ContactElectronicMailAddress" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Guilhem Legal 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "contactPersonPrimary",
    "contactPosition",
    "contactAddress",
    "contactVoiceTelephone",
    "contactFacsimileTelephone",
    "contactElectronicMailAddress"
})
@XmlRootElement(name = "ContactInformation")
public class ContactInformation {

    @XmlElement(name = "ContactPersonPrimary")
    private ContactPersonPrimary contactPersonPrimary;
    @XmlElement(name = "ContactPosition")
    private String contactPosition;
    @XmlElement(name = "ContactAddress")
    private ContactAddress contactAddress;
    @XmlElement(name = "ContactVoiceTelephone")
    private String contactVoiceTelephone;
    @XmlElement(name = "ContactFacsimileTelephone")
    private String contactFacsimileTelephone;
    @XmlElement(name = "ContactElectronicMailAddress")
    private String contactElectronicMailAddress;

    /**
     * An empty constructor used by JAXB.
     */
     ContactInformation() {
     }

    /**
     * Build a new Contact information object.
     */
    public ContactInformation(final ContactPersonPrimary contactPersonPrimary, final String contactPosition,
            final ContactAddress contactAddress, final String contactVoiceTelephone, final String contactFacsimileTelephone,
            final String contactElectronicMailAddress) {
        
        this.contactAddress               = contactAddress;
        this.contactElectronicMailAddress = contactElectronicMailAddress;
        this.contactFacsimileTelephone    = contactFacsimileTelephone;
        this.contactPersonPrimary         = contactPersonPrimary;
        this.contactPosition              = contactPosition;
        this.contactVoiceTelephone        = contactVoiceTelephone; 
    }
    /**
     * Gets the value of the contactPersonPrimary property.
     * 
     */
    public ContactPersonPrimary getContactPersonPrimary() {
        return contactPersonPrimary;
    }

    /**
     * Gets the value of the contactPosition property.
     */
    public String getContactPosition() {
        return contactPosition;
    }

    /**
     * Gets the value of the contactAddress property.
     */
    public ContactAddress getContactAddress() {
        return contactAddress;
    }

    /**
     * Gets the value of the contactVoiceTelephone property.
     * 
     */
    public String getContactVoiceTelephone() {
        return contactVoiceTelephone;
    }

    /**
     * Gets the value of the contactFacsimileTelephone property.
     */
    public String getContactFacsimileTelephone() {
        return contactFacsimileTelephone;
    }

    /**
     * Gets the value of the contactElectronicMailAddress property.
     * 
     */
    public String getContactElectronicMailAddress() {
        return contactElectronicMailAddress;
    }
}
