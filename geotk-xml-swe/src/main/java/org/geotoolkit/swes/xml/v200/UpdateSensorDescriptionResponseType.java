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

package org.geotoolkit.swes.xml.v200;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UpdateSensorDescriptionResponseType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="UpdateSensorDescriptionResponseType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/swes/2.0}ExtensibleResponseType">
 *       &lt;sequence>
 *         &lt;element name="updatedProcedure" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UpdateSensorDescriptionResponseType", propOrder = {
    "updatedProcedure"
})
public class UpdateSensorDescriptionResponseType extends ExtensibleResponseType {

    @XmlElement(required = true)
    @XmlSchemaType(name = "anyURI")
    private String updatedProcedure;

    /**
     * Gets the value of the updatedProcedure property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getUpdatedProcedure() {
        return updatedProcedure;
    }

    /**
     * Sets the value of the updatedProcedure property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setUpdatedProcedure(String value) {
        this.updatedProcedure = value;
    }

}
