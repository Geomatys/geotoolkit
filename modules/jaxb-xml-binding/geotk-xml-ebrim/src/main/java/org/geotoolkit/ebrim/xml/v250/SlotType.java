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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;


/**
 * <p>Java class for SlotType1 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SlotType1">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5}ValueList"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5}LongName" />
 *       &lt;attribute name="slotType" type="{urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5}LongName" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SlotType1", propOrder = {
    "valueList"
})
public class SlotType {

    @XmlElement(name = "ValueList", required = true)
    private ValueListType valueList;
    @XmlAttribute(required = true)
    private String name;
    @XmlAttribute
    private String slotType;

    /**
     * Gets the value of the valueList property.
     */
    public ValueListType getValueList() {
        return valueList;
    }

    /**
     * Sets the value of the valueList property.
     */
    public void setValueList(final ValueListType value) {
        this.valueList = value;
    }

    /**
     * Gets the value of the name property.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     */
    public void setName(final String value) {
        this.name = value;
    }

    /**
     * Gets the value of the slotType property.
     */
    public String getSlotType() {
        return slotType;
    }

    /**
     * Sets the value of the slotType property.
     */
    public void setSlotType(final String value) {
        this.slotType = value;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[SlotType]\n");
        if (name != null) {
            sb.append("name:").append(name).append('\n');
        }
        if (slotType != null) {
            sb.append("slotType:").append(slotType).append('\n');
        }
        if (valueList != null) {
            sb.append("valueList:").append(valueList).append('\n');
        }
        return sb.toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof SlotType) {
            final SlotType that = (SlotType) obj;
            return Utilities.equals(this.slotType,  that.slotType) &&
                   Utilities.equals(this.name,      that.name) &&
                   Utilities.equals(this.valueList, that.valueList);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + (this.valueList != null ? this.valueList.hashCode() : 0);
        hash = 59 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 59 * hash + (this.slotType != null ? this.slotType.hashCode() : 0);
        return hash;
    }

}
