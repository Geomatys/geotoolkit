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
package org.geotoolkit.swe.xml.v100;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.swe.xml.AbstractQuantityProperty;


/**
 * Quantity is a data-type so usually appears "by value" rather than by reference.
 *
 * <p>Java class for QuantityPropertyType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="QuantityPropertyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/swe/1.0}Quantity"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QuantityPropertyType", propOrder = {
    "quantity"
})
public class QuantityPropertyType implements AbstractQuantityProperty {

    @XmlElement(name = "Quantity", required = true)
    private QuantityType quantity;

    public QuantityPropertyType() {

    }

    public QuantityPropertyType(final AbstractQuantityProperty qp) {
        if (qp != null && qp.getQuantity() != null) {
            this.quantity = new QuantityType(qp.getQuantity());
        }
    }

    /**
     * Gets the value of the quantity property.
     *
     * @return
     *     possible object is
     *     {@link Quantity }
     *
     */
    public QuantityType getQuantity() {
        return quantity;
    }

    /**
     * Sets the value of the quantity property.
     *
     * @param value
     *     allowed object is
     *     {@link Quantity }
     *
     */
    public void setQuantity(final QuantityType value) {
        this.quantity = value;
    }

}
