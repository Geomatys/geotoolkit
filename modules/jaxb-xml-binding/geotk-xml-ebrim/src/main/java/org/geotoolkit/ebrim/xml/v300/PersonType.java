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
package org.geotoolkit.ebrim.xml.v300;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;


/**
 * Mapping of the same named interface in ebRIM.
 * 
 * <p>Java class for PersonType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PersonType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}RegistryObjectType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}Address" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}PersonName" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}TelephoneNumber" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}EmailAddress" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PersonType", propOrder = {
    "address",
    "personName",
    "telephoneNumber",
    "emailAddress"
})
@XmlSeeAlso({
    UserType.class
})
@XmlRootElement(name = "Person")
public class PersonType extends RegistryObjectType {

    @XmlElement(name = "Address")
    private List<PostalAddressType> address;
    @XmlElement(name = "PersonName")
    private PersonNameType personName;
    @XmlElement(name = "TelephoneNumber")
    private List<TelephoneNumberType> telephoneNumber;
    @XmlElement(name = "EmailAddress")
    private List<EmailAddressType> emailAddress;

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
    public void setAddress(final PostalAddressType address) {
        if (this.address == null) {
            this.address = new ArrayList<PostalAddressType>();
        }
        this.address.add(address);
    }
    
    /**
     * Sets the value of the address property.
     */
    public void setAddress(final List<PostalAddressType> address) {
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
    public void setPersonName(final PersonNameType value) {
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
    public void setTelephoneNumber(final TelephoneNumberType number) {
        if (telephoneNumber == null) {
            telephoneNumber = new ArrayList<TelephoneNumberType>();
        }
        this.telephoneNumber.add(number);
    }
    
    /**
     * Sets the value of the telephoneNumber property.
     */
    public void setTelephoneNumber(final List<TelephoneNumberType> numbers) {
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
    public void setEmailAddress(final EmailAddressType email) {
        if (emailAddress == null) {
            emailAddress = new ArrayList<EmailAddressType>();
        }
        this.emailAddress.add(email);
    }
    
    /**
     * Sets the value of the emailAddress property.
     */
    public void setEmailAddress(final List<EmailAddressType> email) {
        this.emailAddress = email;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(super.toString());
        if (personName != null) {
            sb.append("personName:").append(personName).append('\n');
        }
        if (address != null) {
            sb.append("address:\n");
            for (PostalAddressType cl : address) {
                sb.append(cl).append('\n');
            }
        }
        if (emailAddress != null) {
            sb.append("emailAddress:\n");
            for (EmailAddressType cl : emailAddress) {
                sb.append(cl).append('\n');
            }
        }
        if (telephoneNumber != null) {
            sb.append("telephoneNumber:\n");
            for (TelephoneNumberType cl : telephoneNumber) {
                sb.append(cl).append('\n');
            }
        }
        return sb.toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof PersonType && super.equals(obj)) {
            final PersonType that = (PersonType) obj;
            return Utilities.equals(this.address,         that.address) &&
                   Utilities.equals(this.emailAddress,    that.emailAddress) &&
                   Utilities.equals(this.personName,      that.personName) &&
                   Utilities.equals(this.telephoneNumber, that.telephoneNumber);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + super.hashCode();
        hash = 73 * hash + (this.address != null ? this.address.hashCode() : 0);
        hash = 73 * hash + (this.personName != null ? this.personName.hashCode() : 0);
        hash = 73 * hash + (this.telephoneNumber != null ? this.telephoneNumber.hashCode() : 0);
        hash = 73 * hash + (this.emailAddress != null ? this.emailAddress.hashCode() : 0);
        return hash;
    }

}
