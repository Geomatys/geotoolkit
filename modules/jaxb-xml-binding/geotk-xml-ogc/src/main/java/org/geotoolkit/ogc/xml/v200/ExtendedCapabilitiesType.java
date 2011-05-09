/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2011, Geomatys
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


package org.geotoolkit.ogc.xml.v200;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Extended_CapabilitiesType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Extended_CapabilitiesType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="AdditionalOperators" type="{http://www.opengis.net/fes/2.0}AdditionalOperatorsType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Extended_CapabilitiesType", propOrder = {
    "additionalOperators"
})
public class ExtendedCapabilitiesType {

    @XmlElement(name = "AdditionalOperators")
    private AdditionalOperatorsType additionalOperators;

    /**
     * Gets the value of the additionalOperators property.
     * 
     * @return
     *     possible object is
     *     {@link AdditionalOperatorsType }
     *     
     */
    public AdditionalOperatorsType getAdditionalOperators() {
        return additionalOperators;
    }

    /**
     * Sets the value of the additionalOperators property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdditionalOperatorsType }
     *     
     */
    public void setAdditionalOperators(AdditionalOperatorsType value) {
        this.additionalOperators = value;
    }

}
