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
package org.geotoolkit.swe.xml.v100;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.swe.xml.AbstractCurve;


/**
 * <p>Java class for CurveType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CurveType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/swe/1.0}AbstractDataArrayType">
 *       &lt;sequence>
 *         &lt;element name="elementType" type="{http://www.opengis.net/swe/1.0}SimpleDataRecordPropertyType"/>
 *         &lt;group ref="{http://www.opengis.net/swe/1.0}EncodedValuesGroup" minOccurs="0"/>
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
@XmlType(name = "CurveType", propOrder = {
    "elementType",
    "encoding",
    "values"
})
public class CurveType extends AbstractDataArrayType implements AbstractCurve {

    @XmlElement(required = true)
    private SimpleDataRecordPropertyType elementType;
    private BlockEncodingPropertyType encoding;
    private DataValuePropertyType values;

    public CurveType() {

    }

    public CurveType(final AbstractCurve cu) {
        super(cu);
        if (cu != null) {
            if (cu.getElementType() != null) {
                this.elementType = new SimpleDataRecordPropertyType(cu.getElementType());
            }
            if (cu.getEncoding() != null) {
                this.encoding = new BlockEncodingPropertyType(cu.getEncoding());
            }
            if (cu.getValues() != null) {
                this.values = new DataValuePropertyType(cu.getValues());
            }
        }
    }

    /**
     * Gets the value of the elementType property.
     * 
     * @return
     *     possible object is
     *     {@link SimpleDataRecordPropertyType }
     *     
     */
    public SimpleDataRecordPropertyType getElementType() {
        return elementType;
    }

    /**
     * Sets the value of the elementType property.
     * 
     * @param value
     *     allowed object is
     *     {@link SimpleDataRecordPropertyType }
     *     
     */
    public void setElementType(final SimpleDataRecordPropertyType value) {
        this.elementType = value;
    }

    /**
     * Gets the value of the encoding property.
     * 
     * @return
     *     possible object is
     *     {@link BlockEncodingPropertyType }
     *     
     */
    public BlockEncodingPropertyType getEncoding() {
        return encoding;
    }

    /**
     * Sets the value of the encoding property.
     * 
     * @param value
     *     allowed object is
     *     {@link BlockEncodingPropertyType }
     *     
     */
    public void setEncoding(final BlockEncodingPropertyType value) {
        this.encoding = value;
    }

    /**
     * Gets the value of the values property.
     * 
     * @return
     *     possible object is
     *     {@link DataValuePropertyType }
     *     
     */
    public DataValuePropertyType getValues() {
        return values;
    }

    /**
     * Sets the value of the values property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataValuePropertyType }
     *     
     */
    public void setValues(final DataValuePropertyType value) {
        this.values = value;
    }

}
