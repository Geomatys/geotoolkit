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
package org.geotoolkit.se.xml.v110;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AnchorPointType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="AnchorPointType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/se}AnchorPointX"/>
 *         &lt;element ref="{http://www.opengis.net/se}AnchorPointY"/>
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
@XmlType(name = "AnchorPointType", propOrder = {
    "anchorPointX",
    "anchorPointY"
})
public class AnchorPointType {

    @XmlElement(name = "AnchorPointX", required = true)
    protected ParameterValueType anchorPointX;
    @XmlElement(name = "AnchorPointY", required = true)
    protected ParameterValueType anchorPointY;

    /**
     * Gets the value of the anchorPointX property.
     *
     * @return
     *     possible object is
     *     {@link ParameterValueType }
     *
     */
    public ParameterValueType getAnchorPointX() {
        return anchorPointX;
    }

    /**
     * Sets the value of the anchorPointX property.
     *
     * @param value
     *     allowed object is
     *     {@link ParameterValueType }
     *
     */
    public void setAnchorPointX(final ParameterValueType value) {
        this.anchorPointX = value;
    }

    /**
     * Gets the value of the anchorPointY property.
     *
     * @return
     *     possible object is
     *     {@link ParameterValueType }
     *
     */
    public ParameterValueType getAnchorPointY() {
        return anchorPointY;
    }

    /**
     * Sets the value of the anchorPointY property.
     *
     * @param value
     *     allowed object is
     *     {@link ParameterValueType }
     *
     */
    public void setAnchorPointY(final ParameterValueType value) {
        this.anchorPointY = value;
    }

}
