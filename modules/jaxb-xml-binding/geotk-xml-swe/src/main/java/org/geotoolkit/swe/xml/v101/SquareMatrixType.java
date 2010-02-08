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
package org.geotoolkit.swe.xml.v101;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.swe.xml.AbstractSquareMatrix;


/**
 * <p>Java class for SquareMatrixType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SquareMatrixType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/swe/1.0.1}AbstractMatrixType">
 *       &lt;sequence>
 *         &lt;element name="elementType" type="{http://www.opengis.net/swe/1.0.1}QuantityPropertyType"/>
 *         &lt;group ref="{http://www.opengis.net/swe/1.0.1}EncodedValuesGroup" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SquareMatrixType", propOrder = {
    "elementType",
    "encoding",
    "values"
})
public class SquareMatrixType extends AbstractMatrixType implements AbstractSquareMatrix {

    @XmlElement(required = true)
    private QuantityPropertyType elementType;
    private BlockEncodingPropertyType encoding;
    private DataValuePropertyType values;

    /**
     * Gets the value of the elementType property.
     */
    public QuantityPropertyType getElementType() {
        return elementType;
    }

    /**
     * Sets the value of the elementType property.
     */
    public void setElementType(QuantityPropertyType value) {
        this.elementType = value;
    }

    /**
     * Gets the value of the encoding property.
     */
    public BlockEncodingPropertyType getEncoding() {
        return encoding;
    }

    /**
     * Sets the value of the encoding property.
     */
    public void setEncoding(BlockEncodingPropertyType value) {
        this.encoding = value;
    }

    /**
     * Gets the value of the values property.
     */
    public DataValuePropertyType getValues() {
        return values;
    }

    /**
     * Sets the value of the values property.
     */
    public void setValues(DataValuePropertyType value) {
        this.values = value;
    }

}
