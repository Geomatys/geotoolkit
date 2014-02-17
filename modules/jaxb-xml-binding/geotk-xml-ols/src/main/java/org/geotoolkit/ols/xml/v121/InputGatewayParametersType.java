/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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

package org.geotoolkit.ols.xml.v121;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for InputGatewayParametersType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="InputGatewayParametersType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/xls}AbstractGatewayParametersType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/xls}InputMSIDs"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InputGatewayParametersType", propOrder = {
    "inputMSIDs"
})
public class InputGatewayParametersType extends AbstractGatewayParametersType {

    @XmlElement(name = "InputMSIDs", required = true)
    private InputMSIDsType inputMSIDs;

    /**
     * Gets the value of the inputMSIDs property.
     * 
     * @return
     *     possible object is
     *     {@link InputMSIDsType }
     *     
     */
    public InputMSIDsType getInputMSIDs() {
        return inputMSIDs;
    }

    /**
     * Sets the value of the inputMSIDs property.
     * 
     * @param value
     *     allowed object is
     *     {@link InputMSIDsType }
     *     
     */
    public void setInputMSIDs(InputMSIDsType value) {
        this.inputMSIDs = value;
    }

}
