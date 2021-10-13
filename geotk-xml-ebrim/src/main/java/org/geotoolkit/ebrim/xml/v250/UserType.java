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
import java.util.Objects;
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
 * @module
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

    /**
     * Gets the value of the url property.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the value of the url property.
     */
    public void setUrl(final String value) {
        this.url = value;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(super.toString());
        if (url != null) {
            sb.append("url:").append(url).append('\n');
        }
        if (personName != null) {
            sb.append("personName:").append(personName).append('\n');
        }
        if (address != null) {
            sb.append("address:\n");
            for (PostalAddressType p : address) {
                sb.append(p).append('\n');
            }
        }
        if (emailAddress != null) {
            sb.append("emailAddress:\n");
            for (EmailAddressType p : emailAddress) {
                sb.append(p).append('\n');
            }
        }
        if (telephoneNumber != null) {
            sb.append("telephoneNumber:\n");
            for (TelephoneNumberType p : telephoneNumber) {
                sb.append(p).append('\n');
            }
        }
        return sb.toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof UserType && super.equals(obj)) {
            final UserType that = (UserType) obj;
            return Objects.equals(this.address,         that.address) &&
                   Objects.equals(this.emailAddress,    that.emailAddress) &&
                   Objects.equals(this.personName,      that.personName) &&
                   Objects.equals(this.url,             that.url) &&
                   Objects.equals(this.telephoneNumber, that.telephoneNumber);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + (this.address != null ? this.address.hashCode() : 0);
        hash = 67 * hash + (this.personName != null ? this.personName.hashCode() : 0);
        hash = 67 * hash + (this.telephoneNumber != null ? this.telephoneNumber.hashCode() : 0);
        hash = 67 * hash + (this.emailAddress != null ? this.emailAddress.hashCode() : 0);
        hash = 67 * hash + (this.url != null ? this.url.hashCode() : 0);
        return hash;
    }
}
