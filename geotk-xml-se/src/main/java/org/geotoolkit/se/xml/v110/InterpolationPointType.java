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
import org.geotoolkit.ogc.xml.v110.ExpressionType;
import org.opengis.util.ScopedName;


/**
 * <p>Java class for InterpolationPointType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="InterpolationPointType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/ogc}ExpressionType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/se}Data"/>
 *         &lt;element ref="{http://www.opengis.net/se}Value"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InterpolationPointType", propOrder = {
    "data",
    "value"
})
public class InterpolationPointType
    extends ExpressionType
{

    @XmlElement(name = "Data")
    protected double data;
    @XmlElement(name = "Value", required = true)
    protected ParameterValueType value;

    @Override
    public ScopedName getFunctionName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Gets the value of the data property.
     */
    public double getData() {
        return data;
    }

    /**
     * Sets the value of the data property.
     */
    public void setData(final double value) {
        this.data = value;
    }

    /**
     * Gets the value of the value property.
     *
     * @return
     *     possible object is
     *     {@link ParameterValueType }
     */
    public ParameterValueType getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     *
     * @param value
     *     allowed object is
     *     {@link ParameterValueType }
     */
    public void setValue(final ParameterValueType value) {
        this.value = value;
    }

    public Object evaluate(final Object object) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public <T> T evaluate(final Object object, final Class<T> context) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
