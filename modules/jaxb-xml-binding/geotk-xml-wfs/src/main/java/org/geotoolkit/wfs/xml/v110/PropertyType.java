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
package org.geotoolkit.wfs.xml.v110;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import org.apache.xerces.dom.ElementNSImpl;
import org.geotoolkit.util.Utilities;
import org.w3c.dom.Node;

/**
 * <p>Java class for PropertyType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PropertyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}QName"/>
 *         &lt;element name="Value" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PropertyType", propOrder = {
    "name",
    "value"
})
public class PropertyType {

    @XmlElement(name = "Name", required = true)
    private QName name;

    @XmlElement(name="Value")
    private ValueType value;

    public PropertyType() {

    }

    public PropertyType(final QName name, final ValueType value) {
        this.name  = name;
        this.value = value;
    }

    /**
     * Gets the value of the name property.
     */
    public QName getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     */
    public void setName(final QName value) {
        this.name = value;
    }

    /**
     * Gets the value of the value property.
     */
    public Object getValue() {
        if (value != null) {
            return value.getValue();
        }
        return value;
    }

    /**
     * Sets the value of the value property.
     */
    public void setValue(final ValueType value) {
        this.value = value;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[").append(this.getClass().getSimpleName()).append(']');
        if (name != null) {
            sb.append("name=").append(name).append('\n');
        }
        if (value != null) {
            sb.append("value=").append(value).append('\n');
        }
        return sb.toString();
    }

    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof PropertyType) {
            final PropertyType that = (PropertyType) object;
            return  Utilities.equals(this.name, that.name) &&
                    Utilities.equals(this.value, that.value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 97 * hash + (this.value != null ? this.value.hashCode() : 0);
        return hash;
    }
}
