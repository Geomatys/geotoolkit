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
 * <p>Java class for AllowedTokensPropertyByValueType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AllowedTokensPropertyByValueType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/swe/2.0}AllowedTokens"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AllowedTokensPropertyByValueType", propOrder = {
    "allowedTokens"
})
public class AllowedTokensPropertyByValueType {

    @XmlElement(name = "AllowedTokens", required = true)
    private AllowedTokensType allowedTokens;

    /**
     * Gets the value of the allowedTokens property.
     * 
     * @return
     *     possible object is
     *     {@link AllowedTokensType }
     *     
     */
    public AllowedTokensType getAllowedTokens() {
        return allowedTokens;
    }

    /**
     * Sets the value of the allowedTokens property.
     * 
     * @param value
     *     allowed object is
     *     {@link AllowedTokensType }
     *     
     */
    public void setAllowedTokens(AllowedTokensType value) {
        this.allowedTokens = value;
    }

}
