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
package org.geotoolkit.se.xml.vext;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.geotoolkit.ogc.xml.v110.ExpressionType;
import org.geotoolkit.se.xml.v110.ParameterValueType;

import org.opengis.filter.expression.ExpressionVisitor;

/**
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ColorItemType", propOrder = {
    "data",
    "value"
})
public class ColorItemType extends ExpressionType {

    @XmlElement(name = "Data")
    protected ParameterValueType data;
    @XmlElement(name = "Value", required = true)
    protected ParameterValueType value;

    /**
     * Gets the value of the data property.
     * 
     */
    public ParameterValueType getData() {
        return data;
    }

    /**
     * Sets the value of the data property.
     * 
     */
    public void setData(final ParameterValueType value) {
        this.data = value;
    }

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link ParameterValueType }
     *     
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
     *     
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

    public Object accept(final ExpressionVisitor visitor, final Object extraData) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
