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

import java.util.Objects;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;


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
 *         &lt;element ref="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}ValueList"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}LongName" />
 *       &lt;attribute name="slotType" type="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}referenceURI" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SlotType1", propOrder = {
    "valueList"
})
public class SlotType {

    @XmlElementRef(name = "ValueList", namespace = "urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0", type = JAXBElement.class)
    private JAXBElement<? extends ValueListType> valueList;
    @XmlAttribute(required = true)
    private String name;
    @XmlAttribute
    private String slotType;

    @XmlTransient
    private ObjectFactory factory = new ObjectFactory();

    /**
     * Gets the value of the valueList property.
     */
    public JAXBElement<? extends ValueListType> getValueList() {
        return valueList;
    }

    /**
     * Gets the value of the valueList property.
     */
    public void setValueList(final JAXBElement<? extends ValueListType> valueList) {
        this.valueList = valueList;
    }

    /**
     * Sets the value of the valueList property.
     */
    public void setValueList(final ValueListType value) {
        this.valueList = factory.createValueList(value);
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
        StringBuilder s = new StringBuilder();
        s.append('[').append(this.getClass().getSimpleName()).append(']').append('\n');
        s.append("name: ").append(name).append('\n');
        if (slotType != null) {
            s.append("slotType: ").append(slotType).append('\n');
        }
        if (valueList != null && valueList.getValue() != null) {
            s.append("Value list: ").append(valueList.getValue().toString()).append('\n');
        }
        return s.toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof SlotType) {
            final SlotType that = (SlotType) obj;

            boolean vl = false;
            if (this.valueList == null && that.valueList == null) {
                vl = true;
            } else if (this.valueList != null && that.valueList != null) {
                vl = Objects.equals(this.valueList.getValue(), that.valueList.getValue());
            }
            return Objects.equals(this.name, that.name) &&
                   Objects.equals(this.slotType,   that.slotType)   &&
                   vl;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + (this.valueList != null ? this.valueList.hashCode() : 0);
        hash = 89 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 89 * hash + (this.slotType != null ? this.slotType.hashCode() : 0);
        return hash;
    }

}
