/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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
package org.geotoolkit.tms.xml.v100;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for OriginType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="OriginType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="x" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       &lt;attribute name="y" type="{http://www.w3.org/2001/XMLSchema}double" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OriginType")
public class OriginType {

    @XmlAttribute(name = "x")
    protected Double x;
    @XmlAttribute(name = "y")
    protected Double y;

    public OriginType() {
    }

    public OriginType(Double x, Double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Gets the value of the x property.
     *
     * @return
     *     possible object is
     *     {@link Double }
     *
     */
    public Double getX() {
        return x;
    }

    /**
     * Sets the value of the x property.
     *
     * @param value
     *     allowed object is
     *     {@link Double }
     *
     */
    public void setX(Double value) {
        this.x = value;
    }

    /**
     * Gets the value of the y property.
     *
     * @return
     *     possible object is
     *     {@link Double }
     *
     */
    public Double getY() {
        return y;
    }

    /**
     * Sets the value of the y property.
     *
     * @param value
     *     allowed object is
     *     {@link Double }
     *
     */
    public void setY(Double value) {
        this.y = value;
    }

}
