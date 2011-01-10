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
 * Defines the response type of the WPS, either raw data or XML document
 * 
 * <p>Java class for ResponseFormType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ResponseFormType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="ResponseDocument" type="{http://www.opengis.net/wps/1.0.0}ResponseDocumentType"/>
 *         &lt;element name="RawDataOutput" type="{http://www.opengis.net/wps/1.0.0}OutputDefinitionType"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResponseFormType", propOrder = {
    "responseDocument",
    "rawDataOutput"
})
public class ResponseFormType {

    @XmlElement(name = "ResponseDocument")
    protected ResponseDocumentType responseDocument;
    @XmlElement(name = "RawDataOutput")
    protected OutputDefinitionType rawDataOutput;

    /**
     * Gets the value of the responseDocument property.
     * 
     * @return
     *     possible object is
     *     {@link ResponseDocumentType }
     *     
     */
    public ResponseDocumentType getResponseDocument() {
        return responseDocument;
    }

    /**
     * Sets the value of the responseDocument property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResponseDocumentType }
     *     
     */
    public void setResponseDocument(final ResponseDocumentType value) {
        this.responseDocument = value;
    }

    /**
     * Gets the value of the rawDataOutput property.
     * 
     * @return
     *     possible object is
     *     {@link OutputDefinitionType }
     *     
     */
    public OutputDefinitionType getRawDataOutput() {
        return rawDataOutput;
    }

    /**
     * Sets the value of the rawDataOutput property.
     * 
     * @param value
     *     allowed object is
     *     {@link OutputDefinitionType }
     *     
     */
    public void setRawDataOutput(final OutputDefinitionType value) {
        this.rawDataOutput = value;
    }

}
