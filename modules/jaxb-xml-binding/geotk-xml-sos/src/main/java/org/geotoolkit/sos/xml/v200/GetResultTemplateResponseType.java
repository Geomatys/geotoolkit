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

package org.geotoolkit.sos.xml.v200;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.sos.xml.GetResultTemplateResponse;
import org.geotoolkit.swe.xml.v200.AbstractDataComponentType;
import org.geotoolkit.swe.xml.v200.AbstractEncodingType;
import org.geotoolkit.swes.xml.v200.ExtensibleResponseType;


/**
 * <p>Java class for GetResultTemplateResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetResultTemplateResponseType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/swes/2.0}ExtensibleResponseType">
 *       &lt;sequence>
 *         &lt;element name="resultStructure">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.opengis.net/swe/2.0}AbstractDataComponent"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="resultEncoding">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.opengis.net/swe/2.0}AbstractEncoding"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetResultTemplateResponseType", propOrder = {
    "resultStructure",
    "resultEncoding"
})
public class GetResultTemplateResponseType extends ExtensibleResponseType implements GetResultTemplateResponse {

    @XmlElement(required = true)
    private ResultStructure resultStructure;
    @XmlElement(required = true)
    private ResultEncoding resultEncoding;

    public GetResultTemplateResponseType() {
        
    }
    
    public GetResultTemplateResponseType(final AbstractDataComponentType resultStructure, final AbstractEncodingType encoding) {
        this.resultEncoding = new ResultEncoding(encoding);
        this.resultStructure = new ResultStructure(resultStructure);
    }
    
    /**
     * Gets the value of the resultStructure property.
     * 
     * @return
     *     possible object is
     *     {@link GetResultTemplateResponseType.ResultStructure }
     *     
     */
    public AbstractDataComponentType getResultStructure() {
        if (resultStructure != null) {
            return resultStructure.getAbstractDataComponent();
        }
        return null;
    }

    /**
     * Sets the value of the resultStructure property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetResultTemplateResponseType.ResultStructure }
     *     
     */
    public void setResultStructure(final AbstractDataComponentType value) {
        if (value != null) {
            this.resultStructure = new ResultStructure(value);
        } else {
            this.resultStructure = null;
        }
    }

    /**
     * Gets the value of the resultEncoding property.
     * 
     * @return
     *     possible object is
     *     {@link GetResultTemplateResponseType.ResultEncoding }
     *     
     */
    public AbstractEncodingType getResultEncoding() {
        if (resultEncoding != null) {
            return resultEncoding.getAbstractEncoding();
        }
        return null;
    }

    /**
     * Sets the value of the resultEncoding property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetResultTemplateResponseType.ResultEncoding }
     *     
     */
    public void setResultEncoding(ResultEncoding value) {
        this.resultEncoding = value;
    }
}
