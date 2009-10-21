/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.wps.xml.v100;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Description of a process Output. 
 * 
 * In this use, the DescriptionType shall describe this process output. 
 * 
 * <p>Java class for OutputDescriptionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OutputDescriptionType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/wps/1.0.0}DescriptionType">
 *       &lt;group ref="{http://www.opengis.net/wps/1.0.0}OutputFormChoice"/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OutputDescriptionType", propOrder = {
    "complexOutput",
    "literalOutput",
    "boundingBoxOutput"
})
public class OutputDescriptionType
    extends DescriptionType
{

    @XmlElement(name = "ComplexOutput", namespace = "")
    protected SupportedComplexDataType complexOutput;
    @XmlElement(name = "LiteralOutput", namespace = "")
    protected LiteralOutputType literalOutput;
    @XmlElement(name = "BoundingBoxOutput", namespace = "")
    protected SupportedCRSsType boundingBoxOutput;

    /**
     * Gets the value of the complexOutput property.
     * 
     * @return
     *     possible object is
     *     {@link SupportedComplexDataType }
     *     
     */
    public SupportedComplexDataType getComplexOutput() {
        return complexOutput;
    }

    /**
     * Sets the value of the complexOutput property.
     * 
     * @param value
     *     allowed object is
     *     {@link SupportedComplexDataType }
     *     
     */
    public void setComplexOutput(SupportedComplexDataType value) {
        this.complexOutput = value;
    }

    /**
     * Gets the value of the literalOutput property.
     * 
     * @return
     *     possible object is
     *     {@link LiteralOutputType }
     *     
     */
    public LiteralOutputType getLiteralOutput() {
        return literalOutput;
    }

    /**
     * Sets the value of the literalOutput property.
     * 
     * @param value
     *     allowed object is
     *     {@link LiteralOutputType }
     *     
     */
    public void setLiteralOutput(LiteralOutputType value) {
        this.literalOutput = value;
    }

    /**
     * Gets the value of the boundingBoxOutput property.
     * 
     * @return
     *     possible object is
     *     {@link SupportedCRSsType }
     *     
     */
    public SupportedCRSsType getBoundingBoxOutput() {
        return boundingBoxOutput;
    }

    /**
     * Sets the value of the boundingBoxOutput property.
     * 
     * @param value
     *     allowed object is
     *     {@link SupportedCRSsType }
     *     
     */
    public void setBoundingBoxOutput(SupportedCRSsType value) {
        this.boundingBoxOutput = value;
    }

}
