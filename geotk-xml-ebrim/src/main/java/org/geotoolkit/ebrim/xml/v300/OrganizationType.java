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
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Mapping of the same named interface in ebRIM.
 *
 * <p>Java class for OrganizationType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="OrganizationType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}RegistryObjectType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}Address" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}TelephoneNumber" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}EmailAddress" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="parent" type="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}referenceURI" />
 *       &lt;attribute name="primaryContact" type="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}referenceURI" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OrganizationType", propOrder = {
    "address",
    "telephoneNumber",
    "emailAddress"
})
@XmlRootElement(name = "Organization")
public class OrganizationType extends RegistryObjectType {

    @XmlElement(name = "Address")
    private List<PostalAddressType> address;
    @XmlElement(name = "TelephoneNumber")
    private List<TelephoneNumberType> telephoneNumber;
    @XmlElement(name = "EmailAddress")
    private List<EmailAddressType> emailAddress;
    @XmlAttribute
    private String parent;
    @XmlAttribute
    private String primaryContact;

    /**
     * Gets the value of the address property.
     *
     */
    public List<PostalAddressType> getAddress() {
        if (address == null) {
            address = new ArrayList<PostalAddressType>();
        }
        return this.address;
    }

    /**
     * Sets the value of the address property.
     *
     */
    public void setAddress(final PostalAddressType address) {
        if (this.address == null) {
            this.address = new ArrayList<PostalAddressType>();
        }
        this.address.add(address);
    }

    /**
     * Sets the value of the address property.
     *
     */
    public void setAddress(final List<PostalAddressType> address) {
        this.address = address;
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
    public void setTelephoneNumber(final List<TelephoneNumberType> number) {
        this.telephoneNumber = number;
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
    public void setEmailAddress(final List<EmailAddressType> emails) {
        this.emailAddress = emails;
    }

    /**
     * Gets the value of the parent property.
     */
    public String getParent() {
        return parent;
    }

    /**
     * Sets the value of the parent property.
     */
    public void setParent(final String value) {
        this.parent = value;
    }

    /**
     * Gets the value of the primaryContact property.
     */
    public String getPrimaryContact() {
        return primaryContact;
    }

    /**
     * Sets the value of the primaryContact property.
     */
    public void setPrimaryContact(final String value) {
        this.primaryContact = value;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(super.toString());
        if (parent != null) {
            sb.append("parent:").append(parent).append('\n');
        }
        if (primaryContact != null) {
            sb.append("primaryContact:").append(primaryContact).append('\n');
        }
        if (telephoneNumber != null) {
            sb.append("telephoneNumber:\n");
            for (TelephoneNumberType t : telephoneNumber) {
                sb.append(t).append('\n');
            }
        }
        if (emailAddress != null) {
            sb.append("emailAddress:\n");
            for (EmailAddressType t : emailAddress) {
                sb.append(t).append('\n');
            }
        }
        if (address != null) {
            sb.append("address:\n");
            for (PostalAddressType t : address) {
                sb.append(t).append('\n');
            }
        }
        return sb.toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof OrganizationType && super.equals(obj)) {
            final OrganizationType that = (OrganizationType) obj;
            return Objects.equals(this.address,         that.address) &&
                   Objects.equals(this.emailAddress,    that.emailAddress) &&
                   Objects.equals(this.primaryContact,  that.primaryContact) &&
                   Objects.equals(this.telephoneNumber, that.telephoneNumber) &&
                   Objects.equals(this.parent,          that.parent);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.address != null ? this.address.hashCode() : 0);
        hash = 97 * hash + (this.telephoneNumber != null ? this.telephoneNumber.hashCode() : 0);
        hash = 97 * hash + (this.emailAddress != null ? this.emailAddress.hashCode() : 0);
        hash = 97 * hash + (this.parent != null ? this.parent.hashCode() : 0);
        hash = 97 * hash + (this.primaryContact != null ? this.primaryContact.hashCode() : 0);
        return hash;
    }
}
