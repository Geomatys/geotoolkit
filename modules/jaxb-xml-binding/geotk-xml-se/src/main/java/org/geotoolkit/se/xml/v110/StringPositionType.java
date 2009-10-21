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
 * <p>Java class for StringPositionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="StringPositionType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/se}FunctionType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/se}LookupString"/>
 *         &lt;element ref="{http://www.opengis.net/se}StringValue"/>
 *       &lt;/sequence>
 *       &lt;attribute name="searchDirection" type="{http://www.opengis.net/se}searchDirectionType" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StringPositionType", propOrder = {
    "lookupString",
    "stringValue"
})
public class StringPositionType
    extends FunctionType
{

    @XmlElement(name = "LookupString", required = true)
    protected ParameterValueType lookupString;
    @XmlElement(name = "StringValue", required = true)
    protected ParameterValueType stringValue;
    @XmlAttribute
    protected SearchDirectionType searchDirection;

    /**
     * Gets the value of the lookupString property.
     * 
     * @return
     *     possible object is
     *     {@link ParameterValueType }
     *     
     */
    public ParameterValueType getLookupString() {
        return lookupString;
    }

    /**
     * Sets the value of the lookupString property.
     * 
     * @param value
     *     allowed object is
     *     {@link ParameterValueType }
     *     
     */
    public void setLookupString(ParameterValueType value) {
        this.lookupString = value;
    }

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
     * Gets the value of the searchDirection property.
     * 
     * @return
     *     possible object is
     *     {@link SearchDirectionType }
     *     
     */
    public SearchDirectionType getSearchDirection() {
        return searchDirection;
    }

    /**
     * Sets the value of the searchDirection property.
     * 
     * @param value
     *     allowed object is
     *     {@link SearchDirectionType }
     *     
     */
    public void setSearchDirection(SearchDirectionType value) {
        this.searchDirection = value;
    }

}
