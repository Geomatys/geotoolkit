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
 * <p>Java class for EmailAddressType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EmailAddressType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *         &lt;element ref="{urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5}Slot"/>
 *       &lt;/sequence>
 *       &lt;attribute name="address" use="required" type="{urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5}ShortName" />
 *       &lt;attribute name="type" type="{urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5}String32" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EmailAddressType", propOrder = {
    "slot"
})
public class EmailAddressType {

    @XmlElement(name = "Slot")
    private List<SlotType> slot;
    @XmlAttribute(required = true)
    private String address;
    @XmlAttribute
    private String type;

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
     * Gets the value of the address property.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the value of the address property.
     */
    public void setAddress(final String value) {
        this.address = value;
    }

    /**
     * Gets the value of the type property.
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
    */
    public void setType(final String value) {
        this.type = value;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[EmailAddressType]\n");
        if (address != null) {
            sb.append("address:").append(address).append('\n');
        }
        if (type != null) {
            sb.append("type:").append(type).append('\n');
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
        if (obj instanceof EmailAddressType) {
            final EmailAddressType that = (EmailAddressType) obj;
            return Utilities.equals(this.address, that.address) &&
                   Utilities.equals(this.type,    that.type) &&
                   Utilities.equals(this.slot,    that.slot);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (this.slot != null ? this.slot.hashCode() : 0);
        hash = 97 * hash + (this.address != null ? this.address.hashCode() : 0);
        hash = 97 * hash + (this.type != null ? this.type.hashCode() : 0);
        return hash;
    }

}
