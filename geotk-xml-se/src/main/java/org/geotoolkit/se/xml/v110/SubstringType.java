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

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SubstringType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="SubstringType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/se}FunctionType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/se}StringValue"/>
 *         &lt;element ref="{http://www.opengis.net/se}Position" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/se}Length" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SubstringType", propOrder = {
    "stringValue",
    "position",
    "length"
})
public class SubstringType
    extends FunctionType
{

    @XmlElement(name = "StringValue", required = true)
    protected ParameterValueType stringValue;
    @XmlElement(name = "Position")
    protected ParameterValueType position;
    @XmlElement(name = "Length")
    protected ParameterValueType length;

    /**
     * Gets the value of the stringValue property.
     *
     * @return
     *     possible object is
     *     {@link ParameterValueType }
     */
    public ParameterValueType getStringValue() {
        return stringValue;
    }

    /**
     * Sets the value of the stringValue property.
     *
     * @param value
     *     allowed object is
     *     {@link ParameterValueType }
     */
    public void setStringValue(final ParameterValueType value) {
        this.stringValue = value;
    }

    /**
     * Gets the value of the position property.
     *
     * @return
     *     possible object is
     *     {@link ParameterValueType }
     */
    public ParameterValueType getPosition() {
        return position;
    }

    /**
     * Sets the value of the position property.
     *
     * @param value
     *     allowed object is
     *     {@link ParameterValueType }
     */
    public void setPosition(final ParameterValueType value) {
        this.position = value;
    }

    /**
     * Gets the value of the length property.
     *
     * @return
     *     possible object is
     *     {@link ParameterValueType }
     */
    public ParameterValueType getLength() {
        return length;
    }

    /**
     * Sets the value of the length property.
     *
     * @param value
     *     allowed object is
     *     {@link ParameterValueType }
     */
    public void setLength(final ParameterValueType value) {
        this.length = value;
    }
}
