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
 * <p>Java class for OutputGatewayParametersType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OutputGatewayParametersType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/xls}AbstractGatewayParametersType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/xls}OutputMSIDs"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OutputGatewayParametersType", propOrder = {
    "outputMSIDs"
})
public class OutputGatewayParametersType extends AbstractGatewayParametersType {

    @XmlElement(name = "OutputMSIDs", required = true)
    private OutputMSIDsType outputMSIDs;

    /**
     * Gets the value of the outputMSIDs property.
     * 
     * @return
     *     possible object is
     *     {@link OutputMSIDsType }
     *     
     */
    public OutputMSIDsType getOutputMSIDs() {
        return outputMSIDs;
    }

    /**
     * Sets the value of the outputMSIDs property.
     * 
     * @param value
     *     allowed object is
     *     {@link OutputMSIDsType }
     *     
     */
    public void setOutputMSIDs(OutputMSIDsType value) {
        this.outputMSIDs = value;
    }

}
