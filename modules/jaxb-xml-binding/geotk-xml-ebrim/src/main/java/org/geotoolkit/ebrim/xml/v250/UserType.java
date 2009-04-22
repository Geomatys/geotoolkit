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

package org.geotoolkit.ebrim.xml.v250;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * Mapping of the same named interface in ebRIM.
 * 			
 * 
 * <p>Java class for UserType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UserType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5}RegistryObjectType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5}Address" maxOccurs="unbounded"/>
 *         &lt;element ref="{urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5}PersonName"/>
 *         &lt;element ref="{urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5}TelephoneNumber" maxOccurs="unbounded"/>
 *         &lt;element ref="{urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5}EmailAddress" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="url" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UserType", propOrder = {
    "address",
    "personName",
    "telephoneNumber",
    "emailAddress"
})
@XmlRootElement(name = "User")
public class UserType extends RegistryObjectType {

    @XmlElement(name = "Address", required = true)
    private List<PostalAddressType> address;
    @XmlElement(name = "PersonName", required = true)
    private PersonNameType personName;
    @XmlElement(name = "TelephoneNumber", required = true)
    private List<TelephoneNumberType> telephoneNumber;
    @XmlElement(name = "EmailAddress", required = true)
    private List<EmailAddressType> emailAddress;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String url;

    /**
     * Gets the value of the address property.
     */
    public List<PostalAddressType> getAddress() {
        if (address == null) {
            address = new ArrayList<PostalAddressType>();
        }
        return this.address;
    }
    
    /**
     * Sets the value of the address property.
     */
    public void setAddress(PostalAddressType address) {
        if (this.address == null) {
            this.address = new ArrayList<PostalAddressType>();
        }
        this.address.add(address);
    }
    
    /**
     * Sets the value of the address property.
     */
    public void setAddress(List<PostalAddressType> address) {
        this.address = address;
    }

    /**
     * Gets the value of the personName property.
     */
    public PersonNameType getPersonName() {
        return personName;
    }

    /**
     * Sets the value of the personName property.
     */
    public void setPersonName(PersonNameType value) {
        this.personName = value;
    }

    /**
     * Gets the value of the telephoneNumber property.
     */
    public List<TelephoneNumberType> getTelephoneNumber() {
        if (telephoneNumber == null) {
            telephoneNumber = new ArrayList<TelephoneNumberType>();
        }
        return this.telephoneNumber;
    }
    
    /**
     * Sets the value of the telephoneNumber property.
     */
    public void setTelephoneNumber(TelephoneNumberType number) {
        if (telephoneNumber == null) {
            telephoneNumber = new ArrayList<TelephoneNumberType>();
        }
        this.telephoneNumber.add(number);
    }
    
    /**
     * Sets the value of the telephoneNumber property.
     */
    public void setTelephoneNumber(List<TelephoneNumberType> numbers) {
        this.telephoneNumber = numbers;
    }

    /**
     * Gets the value of the emailAddress property.
     */
    public List<EmailAddressType> getEmailAddress() {
        if (emailAddress == null) {
            emailAddress = new ArrayList<EmailAddressType>();
        }
        return this.emailAddress;
    }
    
    /**
     * Sets the value of the emailAddress property.
     */
    public void setEmailAddress(EmailAddressType email) {
        if (emailAddress == null) {
            emailAddress = new ArrayList<EmailAddressType>();
        }
        this.emailAddress.add(email);
    }
    
    /**
     * Sets the value of the emailAddress property.
     */
    public void setEmailAddress(List<EmailAddressType> email) {
        this.emailAddress = email;
    }

    /**
     * Gets the value of the url property.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the value of the url property.
     */
    public void setUrl(String value) {
        this.url = value;
    }

}
