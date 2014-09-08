/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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

package org.geotoolkit.sml.xml.v200;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.swe.xml.v200.AbstractSWEIdentifiableType;
import org.geotoolkit.swe.xml.v200.DataRecordPropertyType;
import org.geotoolkit.swe.xml.v200.DataStreamPropertyType;


/**
 * <p>Java class for DataInterfaceType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DataInterfaceType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/swe/2.0}AbstractSWEIdentifiableType">
 *       &lt;sequence>
 *         &lt;element name="data" type="{http://www.opengis.net/swe/2.0}DataStreamPropertyType"/>
 *         &lt;element name="interfaceParameters" type="{http://www.opengis.net/swe/2.0}DataRecordPropertyType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DataInterfaceType", propOrder = {
    "data",
    "interfaceParameters"
})
public class DataInterfaceType
    extends AbstractSWEIdentifiableType
{

    @XmlElement(required = true)
    protected DataStreamPropertyType data;
    protected DataRecordPropertyType interfaceParameters;

    /**
     * Gets the value of the data property.
     * 
     * @return
     *     possible object is
     *     {@link DataStreamPropertyType }
     *     
     */
    public DataStreamPropertyType getData() {
        return data;
    }

    /**
     * Sets the value of the data property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataStreamPropertyType }
     *     
     */
    public void setData(DataStreamPropertyType value) {
        this.data = value;
    }

    /**
     * Gets the value of the interfaceParameters property.
     * 
     * @return
     *     possible object is
     *     {@link DataRecordPropertyType }
     *     
     */
    public DataRecordPropertyType getInterfaceParameters() {
        return interfaceParameters;
    }

    /**
     * Sets the value of the interfaceParameters property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataRecordPropertyType }
     *     
     */
    public void setInterfaceParameters(DataRecordPropertyType value) {
        this.interfaceParameters = value;
    }

}
