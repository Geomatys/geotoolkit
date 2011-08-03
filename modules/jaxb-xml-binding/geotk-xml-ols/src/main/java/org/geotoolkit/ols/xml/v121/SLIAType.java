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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SLIAType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SLIAType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/xls}AbstractResponseParametersType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/xls}OutputGatewayParameters"/>
 *       &lt;/sequence>
 *       &lt;attribute name="requestID" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SLIAType", propOrder = {
    "outputGatewayParameters"
})
public class SLIAType extends AbstractResponseParametersType {

    @XmlElement(name = "OutputGatewayParameters", required = true)
    private OutputGatewayParametersType outputGatewayParameters;
    @XmlAttribute
    private String requestID;

    /**
     * Gets the value of the outputGatewayParameters property.
     * 
     * @return
     *     possible object is
     *     {@link OutputGatewayParametersType }
     *     
     */
    public OutputGatewayParametersType getOutputGatewayParameters() {
        return outputGatewayParameters;
    }

    /**
     * Sets the value of the outputGatewayParameters property.
     * 
     * @param value
     *     allowed object is
     *     {@link OutputGatewayParametersType }
     *     
     */
    public void setOutputGatewayParameters(OutputGatewayParametersType value) {
        this.outputGatewayParameters = value;
    }

    /**
     * Gets the value of the requestID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequestID() {
        return requestID;
    }

    /**
     * Sets the value of the requestID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequestID(String value) {
        this.requestID = value;
    }

}
