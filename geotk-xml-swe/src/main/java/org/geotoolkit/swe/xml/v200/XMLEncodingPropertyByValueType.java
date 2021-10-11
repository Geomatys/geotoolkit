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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for XMLEncodingPropertyByValueType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="XMLEncodingPropertyByValueType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/swe/2.0}XMLEncoding"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "XMLEncodingPropertyByValueType", propOrder = {
    "xmlEncoding"
})
public class XMLEncodingPropertyByValueType {

    @XmlElement(name = "XMLEncoding", required = true)
    private XMLEncodingType xmlEncoding;

    /**
     * Gets the value of the xmlEncoding property.
     *
     * @return
     *     possible object is
     *     {@link XMLEncodingType }
     *
     */
    public XMLEncodingType getXMLEncoding() {
        return xmlEncoding;
    }

    /**
     * Sets the value of the xmlEncoding property.
     *
     * @param value
     *     allowed object is
     *     {@link XMLEncodingType }
     *
     */
    public void setXMLEncoding(XMLEncodingType value) {
        this.xmlEncoding = value;
    }

}
