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

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.swe.xml.DataArray;


/**
 * <p>Java class for DataArrayType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="DataArrayType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/swe/1.0}AbstractDataArrayType">
 *       &lt;sequence>
 *         &lt;element name="elementType" type="{http://www.opengis.net/swe/1.0}DataComponentPropertyType"/>
 *         &lt;group ref="{http://www.opengis.net/swe/1.0}EncodedValuesGroup" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DataArrayType", propOrder = {
    "elementType",
    "encoding",
    "dataValues",
    "values"
})
public class DataArrayType extends AbstractDataArrayType implements DataArray {

    @XmlElement(required = true)
    private DataComponentPropertyType elementType;
    private BlockEncodingPropertyType encoding;
    private DataValuePropertyType dataValues;
    private String values;

    public DataArrayType() {

    }

    public DataArrayType(final DataArray ar) {
        super(ar);
        if (ar != null) {
            if (ar.getPropertyEncoding() != null) {
                this.encoding = new BlockEncodingPropertyType(ar.getPropertyEncoding());
            }
            if (ar.getPropertyElementType() != null) {
                this.elementType = new DataComponentPropertyType(ar.getPropertyElementType());
            }
            if (ar.getDataValues() != null) {
                this.dataValues = new DataValuePropertyType(ar.getDataValues());
            }
            this.values = ar.getValues();
        }
    }

    public DataArrayType(final String id, final Integer count, final String elementName, final AbstractDataRecordType elementType,
            final AbstractEncodingType encoding, final String values, final List<Object> dataValues) {
        super(id, count);
        if (elementType != null) {
            this.elementType = new DataComponentPropertyType(elementType, elementName);
        }
        this.encoding    = new BlockEncodingPropertyType(encoding);
        this.values      = values;
        if (dataValues != null) {
            this.dataValues = new DataValuePropertyType(dataValues);
        }
    }

    /**
     * Gets the value of the elementType property.
     */
    public DataComponentPropertyType getElementType() {
        return elementType;
    }

    @Override
    public DataComponentPropertyType getPropertyElementType(){
        return elementType;
    }

    /**
     * Sets the value of the elementType property.
     */
    public void setElementType(final DataComponentPropertyType value) {
        this.elementType = value;
    }

    /**
     * Gets the value of the encoding property.
     */
    @Override
    public AbstractEncodingType getEncoding() {
        if (encoding != null) {
            return encoding.getEncoding();
        }
        return null;
    }

    @Override
    public BlockEncodingPropertyType getPropertyEncoding(){
        return encoding;
    }

    /**
     * Sets the value of the encoding property.
     */
    public void setEncoding(final BlockEncodingPropertyType value) {
        this.encoding = value;
    }

    /**
     * Gets the value of the values property.
     */
    @Override
    public String getValues() {
        return values;
    }

    /**
     * Gets the value of the values property.
     */
    @Override
    public DataValuePropertyType getDataValues() {
        return dataValues;
    }

    /**
     * Sets the value of the values property.
     */
    public void setDataValues(final DataValuePropertyType value) {
        this.dataValues = value;
    }

    @Override
    public void setValues(final String values) {
        this.values = values;
    }

}
