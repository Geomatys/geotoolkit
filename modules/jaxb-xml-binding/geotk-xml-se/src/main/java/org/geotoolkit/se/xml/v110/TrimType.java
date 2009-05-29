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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.opengis.filter.expression.ExpressionVisitor;


/**
 * <p>Java class for TrimType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TrimType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/se}FunctionType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/se}StringValue"/>
 *       &lt;/sequence>
 *       &lt;attribute name="stripOffPosition" type="{http://www.opengis.net/se}stripOffPositionType" />
 *       &lt;attribute name="stripOffChar" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TrimType", propOrder = {
    "stringValue"
})
public class TrimType
    extends FunctionType
{

    @XmlElement(name = "StringValue", required = true)
    protected ParameterValueType stringValue;
    @XmlAttribute
    protected StripOffPositionType stripOffPosition;
    @XmlAttribute
    protected String stripOffChar;

    /**
     * Gets the value of the stringValue property.
     * 
     * @return
     *     possible object is
     *     {@link ParameterValueType }
     *     
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
     *     
     */
    public void setStringValue(ParameterValueType value) {
        this.stringValue = value;
    }

    /**
     * Gets the value of the stripOffPosition property.
     * 
     * @return
     *     possible object is
     *     {@link StripOffPositionType }
     *     
     */
    public StripOffPositionType getStripOffPosition() {
        return stripOffPosition;
    }

    /**
     * Sets the value of the stripOffPosition property.
     * 
     * @param value
     *     allowed object is
     *     {@link StripOffPositionType }
     *     
     */
    public void setStripOffPosition(StripOffPositionType value) {
        this.stripOffPosition = value;
    }

    /**
     * Gets the value of the stripOffChar property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStripOffChar() {
        return stripOffChar;
    }

    /**
     * Sets the value of the stripOffChar property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStripOffChar(String value) {
        this.stripOffChar = value;
    }

}
