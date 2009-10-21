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
 * <p>Java class for FormatNumberType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FormatNumberType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/se}FunctionType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/se}NumericValue"/>
 *         &lt;element ref="{http://www.opengis.net/se}Pattern"/>
 *         &lt;element ref="{http://www.opengis.net/se}NegativePattern" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="decimalPoint" type="{http://www.w3.org/2001/XMLSchema}string" default="." />
 *       &lt;attribute name="groupingSeparator" type="{http://www.w3.org/2001/XMLSchema}string" default="," />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FormatNumberType", propOrder = {
    "numericValue",
    "pattern",
    "negativePattern"
})
public class FormatNumberType
    extends FunctionType
{

    @XmlElement(name = "NumericValue", required = true)
    protected ParameterValueType numericValue;
    @XmlElement(name = "Pattern", required = true)
    protected String pattern;
    @XmlElement(name = "NegativePattern")
    protected String negativePattern;
    @XmlAttribute
    protected String decimalPoint;
    @XmlAttribute
    protected String groupingSeparator;

    /**
     * Gets the value of the numericValue property.
     * 
     * @return
     *     possible object is
     *     {@link ParameterValueType }
     *     
     */
    public ParameterValueType getNumericValue() {
        return numericValue;
    }

    /**
     * Sets the value of the numericValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link ParameterValueType }
     *     
     */
    public void setNumericValue(ParameterValueType value) {
        this.numericValue = value;
    }

    /**
     * Gets the value of the pattern property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPattern() {
        return pattern;
    }

    /**
     * Sets the value of the pattern property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPattern(String value) {
        this.pattern = value;
    }

    /**
     * Gets the value of the negativePattern property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNegativePattern() {
        return negativePattern;
    }

    /**
     * Sets the value of the negativePattern property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNegativePattern(String value) {
        this.negativePattern = value;
    }

    /**
     * Gets the value of the decimalPoint property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDecimalPoint() {
        if (decimalPoint == null) {
            return ".";
        } else {
            return decimalPoint;
        }
    }

    /**
     * Sets the value of the decimalPoint property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDecimalPoint(String value) {
        this.decimalPoint = value;
    }

    /**
     * Gets the value of the groupingSeparator property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGroupingSeparator() {
        if (groupingSeparator == null) {
            return ",";
        } else {
            return groupingSeparator;
        }
    }

    /**
     * Sets the value of the groupingSeparator property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGroupingSeparator(String value) {
        this.groupingSeparator = value;
    }

}
