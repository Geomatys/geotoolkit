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
 * <p>Java class for InputMSIDsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="InputMSIDsType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/xls}AbstractMSIDsType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/xls}InputMSInformation"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InputMSIDsType", propOrder = {
    "inputMSInformation"
})
public class InputMSIDsType extends AbstractMSIDsType {

    @XmlElement(name = "InputMSInformation", required = true)
    private InputMSInformationType inputMSInformation;

    /**
     * Gets the value of the inputMSInformation property.
     * 
     * @return
     *     possible object is
     *     {@link InputMSInformationType }
     *     
     */
    public InputMSInformationType getInputMSInformation() {
        return inputMSInformation;
    }

    /**
     * Sets the value of the inputMSInformation property.
     * 
     * @param value
     *     allowed object is
     *     {@link InputMSInformationType }
     *     
     */
    public void setInputMSInformation(InputMSInformationType value) {
        this.inputMSInformation = value;
    }

}
