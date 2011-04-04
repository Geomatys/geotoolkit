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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;


/**
 * Mapping of the same named interface in ebRIM.
 * 
 * <p>Java class for PersonNameType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PersonNameType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="firstName" type="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}ShortName" />
 *       &lt;attribute name="middleName" type="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}ShortName" />
 *       &lt;attribute name="lastName" type="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}ShortName" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PersonNameType")
public class PersonNameType {

    @XmlAttribute
    private String firstName;
    @XmlAttribute
    private String middleName;
    @XmlAttribute
    private String lastName;

    /**
     * Gets the value of the firstName property.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the value of the firstName property.
     */
    public void setFirstName(final String value) {
        this.firstName = value;
    }

    /**
     * Gets the value of the middleName property.
     */
    public String getMiddleName() {
        return middleName;
    }

    /**
     * Sets the value of the middleName property.
     */
    public void setMiddleName(final String value) {
        this.middleName = value;
    }

    /**
     * Gets the value of the lastName property.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the value of the lastName property.
     */
    public void setLastName(final String value) {
        this.lastName = value;
    }

    @Override
    public String toString() {
        final StringBuilder s = new StringBuilder();
        s.append('[').append(this.getClass().getSimpleName()).append(']').append('\n');
        if (firstName != null) {
            s.append("firstName:").append(firstName).append('\n');
        }
        if (lastName != null) {
            s.append("lastName:").append(lastName).append('\n');
        }
        if (middleName != null) {
            s.append("middleName:").append(middleName).append('\n');
        }
        return s.toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof PersonNameType) {
            final PersonNameType that = (PersonNameType) obj;
            return Utilities.equals(this.firstName,  that.firstName) &&
                   Utilities.equals(this.lastName,   that.lastName) &&
                   Utilities.equals(this.middleName, that.middleName);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.firstName != null ? this.firstName.hashCode() : 0);
        hash = 97 * hash + (this.middleName != null ? this.middleName.hashCode() : 0);
        hash = 97 * hash + (this.lastName != null ? this.lastName.hashCode() : 0);
        return hash;
    }

}
