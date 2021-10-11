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
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * Common base type for all types that have unique identity.
 * If id is provided and is not in proper URN syntax then it is used for linkage within document and is ignored by the registry.
 * In this case the registry generates a UUID URN for id attribute.
 * id must not be null when object is retrieved from the registry.
 *
 *
 * <p>Java class for IdentifiableType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="IdentifiableType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}Slot" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="home" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IdentifiableType", propOrder = {
    "slot"
})
@XmlSeeAlso({
    ObjectRefType.class,
    RegistryObjectType.class
})
public class IdentifiableType {

    @XmlElement(name = "Slot")
    private List<SlotType> slot;
    @XmlAttribute(required = true)
    @XmlSchemaType(name = "anyURI")
    private String id;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String home;

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
     * Gets the value of the id property.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     */
    public void setId(final String value) {
        this.id = value;
    }

    /**
     * Gets the value of the home property.
     */
    public String getHome() {
        return home;
    }

    /**
     * Sets the value of the home property.
     */
    public void setHome(final String value) {
        this.home = value;
    }

    @Override
    public String toString() {
        final StringBuilder s = new StringBuilder();
        s.append('[').append(this.getClass().getSimpleName()).append(']').append('\n');
        s.append("id:").append(id).append('\n');
        if (home != null) {
            s.append("home:").append(home).append('\n');
        }
        if (slot != null) {
            int i = 0;
            for (SlotType sl: slot) {
                s.append("Slot ").append(i).append(sl.toString()).append('\n');
                i++;
            }
        }
        return s.toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof IdentifiableType) {
            final IdentifiableType that = (IdentifiableType) obj;
            return Objects.equals(this.home, that.home) &&
                   Objects.equals(this.id,   that.id)   &&
                   Objects.equals(this.slot, that.slot);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.slot != null ? this.slot.hashCode() : 0);
        hash = 37 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 37 * hash + (this.home != null ? this.home.hashCode() : 0);
        return hash;
    }

}
