/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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

package org.geotoolkit.swe.xml.v200;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DataArrayPropertyByValueType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="DataArrayPropertyByValueType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/swe/2.0}DataArray"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DataArrayPropertyByValueType", propOrder = {
    "dataArray"
})
public class DataArrayPropertyByValueType {

    @XmlElementRef(name = "DataArray", namespace = "http://www.opengis.net/swe/2.0", type = JAXBElement.class)
    private JAXBElement<? extends DataArrayType> dataArray;

    /**
     * Gets the value of the dataArray property.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link DataArrayType }{@code >}
     *     {@link JAXBElement }{@code <}{@link MatrixType }{@code >}
     *
     */
    public JAXBElement<? extends DataArrayType> getDataArray() {
        return dataArray;
    }

    /**
     * Sets the value of the dataArray property.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link DataArrayType }{@code >}
     *     {@link JAXBElement }{@code <}{@link MatrixType }{@code >}
     *
     */
    public void setDataArray(JAXBElement<? extends DataArrayType> value) {
        this.dataArray = ((JAXBElement<? extends DataArrayType> ) value);
    }

}
