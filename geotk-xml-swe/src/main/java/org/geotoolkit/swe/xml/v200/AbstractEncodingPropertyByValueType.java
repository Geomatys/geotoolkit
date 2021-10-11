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
 * <p>Java class for AbstractEncodingPropertyByValueType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="AbstractEncodingPropertyByValueType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/swe/2.0}AbstractEncoding"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractEncodingPropertyByValueType", propOrder = {
    "abstractEncoding"
})
public abstract class AbstractEncodingPropertyByValueType {

    @XmlElementRef(name = "AbstractEncoding", namespace = "http://www.opengis.net/swe/2.0", type = JAXBElement.class)
    private JAXBElement<? extends AbstractEncodingType> abstractEncoding;

    /**
     * Gets the value of the abstractEncoding property.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link TextEncodingType }{@code >}
     *     {@link JAXBElement }{@code <}{@link XMLEncodingType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinaryEncodingType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractEncodingType }{@code >}
     *
     */
    public JAXBElement<? extends AbstractEncodingType> getAbstractEncoding() {
        return abstractEncoding;
    }

    /**
     * Sets the value of the abstractEncoding property.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link TextEncodingType }{@code >}
     *     {@link JAXBElement }{@code <}{@link XMLEncodingType }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinaryEncodingType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractEncodingType }{@code >}
     *
     */
    public void setAbstractEncoding(JAXBElement<? extends AbstractEncodingType> value) {
        this.abstractEncoding = ((JAXBElement<? extends AbstractEncodingType> ) value);
    }

}
