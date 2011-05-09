/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2011, Geomatys
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


package org.geotoolkit.ogc.xml.v200;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PropertyIsBetweenType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PropertyIsBetweenType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/fes/2.0}ComparisonOpsType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/fes/2.0}expression"/>
 *         &lt;element name="LowerBoundary" type="{http://www.opengis.net/fes/2.0}LowerBoundaryType"/>
 *         &lt;element name="UpperBoundary" type="{http://www.opengis.net/fes/2.0}UpperBoundaryType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PropertyIsBetweenType", propOrder = {
    "expression",
    "lowerBoundary",
    "upperBoundary"
})
public class PropertyIsBetweenType
    extends ComparisonOpsType
{

    @XmlElementRef(name = "expression", namespace = "http://www.opengis.net/fes/2.0", type = JAXBElement.class)
    private JAXBElement<?> expression;
    @XmlElement(name = "LowerBoundary", required = true)
    private LowerBoundaryType lowerBoundary;
    @XmlElement(name = "UpperBoundary", required = true)
    private UpperBoundaryType upperBoundary;

    /**
     * Gets the value of the expression property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link LiteralType }{@code >}
     *     {@link JAXBElement }{@code <}{@link Object }{@code >}
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     {@link JAXBElement }{@code <}{@link FunctionType }{@code >}
     *     
     */
    public JAXBElement<?> getExpression() {
        return expression;
    }

    /**
     * Sets the value of the expression property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link LiteralType }{@code >}
     *     {@link JAXBElement }{@code <}{@link Object }{@code >}
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     {@link JAXBElement }{@code <}{@link FunctionType }{@code >}
     *     
     */
    public void setExpression(JAXBElement<?> value) {
        this.expression = ((JAXBElement<?> ) value);
    }

    /**
     * Gets the value of the lowerBoundary property.
     * 
     * @return
     *     possible object is
     *     {@link LowerBoundaryType }
     *     
     */
    public LowerBoundaryType getLowerBoundary() {
        return lowerBoundary;
    }

    /**
     * Sets the value of the lowerBoundary property.
     * 
     * @param value
     *     allowed object is
     *     {@link LowerBoundaryType }
     *     
     */
    public void setLowerBoundary(LowerBoundaryType value) {
        this.lowerBoundary = value;
    }

    /**
     * Gets the value of the upperBoundary property.
     * 
     * @return
     *     possible object is
     *     {@link UpperBoundaryType }
     *     
     */
    public UpperBoundaryType getUpperBoundary() {
        return upperBoundary;
    }

    /**
     * Sets the value of the upperBoundary property.
     * 
     * @param value
     *     allowed object is
     *     {@link UpperBoundaryType }
     *     
     */
    public void setUpperBoundary(UpperBoundaryType value) {
        this.upperBoundary = value;
    }

}
