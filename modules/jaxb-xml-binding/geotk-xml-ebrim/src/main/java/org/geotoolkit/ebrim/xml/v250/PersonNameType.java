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
import org.geotoolkit.util.Utilities;


/**
 * 
 * Mapping of the same named interface in ebRIM.
 * 			
 * 
 * <p>Java class for PersonNameType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PersonNameType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *         &lt;element ref="{urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5}Slot"/>
 *       &lt;/sequence>
 *       &lt;attribute name="firstName" type="{urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5}ShortName" />
 *       &lt;attribute name="middleName" type="{urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5}ShortName" />
 *       &lt;attribute name="lastName" type="{urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5}ShortName" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PersonNameType", propOrder = {
    "slot"
})
public class PersonNameType {

    @XmlElement(name = "Slot")
    private List<SlotType> slot;
    @XmlAttribute
    private String firstName;
    @XmlAttribute
    private String middleName;
    @XmlAttribute
    private String lastName;

    /**
     * Gets the value of the slot property.
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
    public void setSlot(final SlotType slot) {
        if (this.slot == null) {
            this.slot = new ArrayList<SlotType>();
        }
        this.slot.add(slot);
    }
    
     /**
     * Sets the value of the slot property.
     */
    public void setSlot(final List<SlotType> slot) {
        this.slot = slot;
    }


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
        final StringBuilder sb = new StringBuilder("[PersonNameType]\n");
        if (firstName != null) {
            sb.append("firstName:").append(firstName).append('\n');
        }
        if (lastName != null) {
            sb.append("lastName:").append(lastName).append('\n');
        }
        if (middleName != null) {
            sb.append("middleName:").append(middleName).append('\n');
        }
        if (slot != null) {
            sb.append("slot:\n");
            for (SlotType p : slot) {
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
        if (obj instanceof PersonNameType) {
            final PersonNameType that = (PersonNameType) obj;
            return Utilities.equals(this.firstName,  that.firstName) &&
                   Utilities.equals(this.lastName,   that.lastName) &&
                   Utilities.equals(this.middleName, that.middleName) &&
                   Utilities.equals(this.slot,       that.slot);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + (this.slot != null ? this.slot.hashCode() : 0);
        hash = 89 * hash + (this.firstName != null ? this.firstName.hashCode() : 0);
        hash = 89 * hash + (this.middleName != null ? this.middleName.hashCode() : 0);
        hash = 89 * hash + (this.lastName != null ? this.lastName.hashCode() : 0);
        return hash;
    }
}
