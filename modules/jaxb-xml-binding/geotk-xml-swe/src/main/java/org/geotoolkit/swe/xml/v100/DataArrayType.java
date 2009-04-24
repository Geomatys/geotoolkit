/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2008, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
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
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DataArrayType", propOrder = {
    "elementType",
    "encoding",
    "values"
})
public class DataArrayType extends AbstractDataArrayType implements DataArray {

    @XmlElement(required = true)
    private DataComponentPropertyType elementType;
    private BlockEncodingPropertyType encoding;
    private DataValuePropertyType values;

    /**
     * Gets the value of the elementType property.
     */
    public DataComponentPropertyType getElementType() {
        return elementType;
    }

    public DataComponentPropertyType getPropertyElementType(){
        return elementType;
    }

    /**
     * Sets the value of the elementType property.
     */
    public void setElementType(DataComponentPropertyType value) {
        this.elementType = value;
    }

    /**
     * Gets the value of the encoding property.
     */
    public AbstractEncodingType getEncoding() {
        if (encoding != null) {
            return encoding.getEncoding();
        }
        return null;
    }

    public BlockEncodingPropertyType getPropertyEncoding(){
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
    public String getValues() {

        //TODO
        return "";
    }

    /**
     * Gets the value of the values property.
     */
    public DataValuePropertyType getDataValues() {
        return values;
    }

    /**
     * Sets the value of the values property.
     */
    public void setValues(DataValuePropertyType value) {
        this.values = value;
    }

}
