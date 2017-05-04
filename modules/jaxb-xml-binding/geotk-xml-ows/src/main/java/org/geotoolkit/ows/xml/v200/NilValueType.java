/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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

package org.geotoolkit.ows.xml.v200;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * The value used (e.g. -255) to represent a nil value with
 *       optional nilReason and codeSpace attributes.
 *
 * <p>Java class for NilValueType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="NilValueType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.opengis.net/ows/2.0>CodeType">
 *       &lt;attribute name="nilReason" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NilValueType")
public class NilValueType extends CodeType {

    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String nilReason;

    /**
     * Gets the value of the nilReason property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getNilReason() {
        return nilReason;
    }

    /**
     * Sets the value of the nilReason property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setNilReason(String value) {
        this.nilReason = value;
    }

}
