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
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * Formats, encodings, and schemas supported by a process input or output. 
 * 
 * <p>Java class for SupportedComplexDataType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SupportedComplexDataType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Default" type="{http://www.opengis.net/wps/1.0.0}ComplexDataCombinationType"/>
 *         &lt;element name="Supported" type="{http://www.opengis.net/wps/1.0.0}ComplexDataCombinationsType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SupportedComplexDataType", propOrder = {
    "_default",
    "supported"
})
@XmlSeeAlso({
    SupportedComplexDataInputType.class
})
public class SupportedComplexDataType {

    @XmlElement(name = "Default", namespace = "", required = true)
    protected ComplexDataCombinationType _default;
    @XmlElement(name = "Supported", namespace = "", required = true)
    protected ComplexDataCombinationsType supported;

    /**
     * Gets the value of the default property.
     * 
     * @return
     *     possible object is
     *     {@link ComplexDataCombinationType }
     *     
     */
    public ComplexDataCombinationType getDefault() {
        return _default;
    }

    /**
     * Sets the value of the default property.
     * 
     * @param value
     *     allowed object is
     *     {@link ComplexDataCombinationType }
     *     
     */
    public void setDefault(ComplexDataCombinationType value) {
        this._default = value;
    }

    /**
     * Gets the value of the supported property.
     * 
     * @return
     *     possible object is
     *     {@link ComplexDataCombinationsType }
     *     
     */
    public ComplexDataCombinationsType getSupported() {
        return supported;
    }

    /**
     * Sets the value of the supported property.
     * 
     * @param value
     *     allowed object is
     *     {@link ComplexDataCombinationsType }
     *     
     */
    public void setSupported(ComplexDataCombinationsType value) {
        this.supported = value;
    }

}
