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
 * <p>Java class for AllowedTimesPropertyByValueType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AllowedTimesPropertyByValueType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/swe/2.0}AllowedTimes"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AllowedTimesPropertyByValueType", propOrder = {
    "allowedTimes"
})
public class AllowedTimesPropertyByValueType {

    @XmlElement(name = "AllowedTimes", required = true)
    private AllowedTimesType allowedTimes;

    /**
     * Gets the value of the allowedTimes property.
     * 
     * @return
     *     possible object is
     *     {@link AllowedTimesType }
     *     
     */
    public AllowedTimesType getAllowedTimes() {
        return allowedTimes;
    }

    /**
     * Sets the value of the allowedTimes property.
     * 
     * @param value
     *     allowed object is
     *     {@link AllowedTimesType }
     *     
     */
    public void setAllowedTimes(AllowedTimesType value) {
        this.allowedTimes = value;
    }

}
