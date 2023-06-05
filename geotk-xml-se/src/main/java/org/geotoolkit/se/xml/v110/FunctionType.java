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
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;
import org.geotoolkit.ogc.xml.v110.ExpressionType;
import org.opengis.util.ScopedName;


/**
 * <p>Java class for FunctionType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="FunctionType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/ogc}ExpressionType">
 *       &lt;attribute name="fallbackValue" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FunctionType")
@XmlSeeAlso({
    CategorizeType.class,
    TrimType.class,
    InterpolateType.class,
    RecodeType.class,
    StringPositionType.class,
    SubstringType.class,
    ConcatenateType.class,
    StringLengthType.class,
    FormatDateType.class,
    ChangeCaseType.class,
    FormatNumberType.class
})
public abstract class FunctionType
    extends ExpressionType
{

    @XmlAttribute(required = true)
    protected String fallbackValue;

    @Override
    public ScopedName getFunctionName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Gets the value of the fallbackValue property.
     *
     * @return
     *     possible object is
     *     {@link String }
     */
    public String getFallbackValue() {
        return fallbackValue;
    }

    /**
     * Sets the value of the fallbackValue property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     */
    public void setFallbackValue(final String value) {
        this.fallbackValue = value;
    }
}
