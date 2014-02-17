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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.sos.xml.GetResultResponse;
import org.geotoolkit.swes.xml.v200.ExtensibleResponseType;


/**
 * <p>Java class for GetResultResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetResultResponseType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/swes/2.0}ExtensibleResponseType">
 *       &lt;sequence>
 *         &lt;element name="resultValues" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetResultResponseType", propOrder = {
    "resultValues"
})
@XmlRootElement(name="GetResultResponse")
public class GetResultResponseType extends ExtensibleResponseType implements GetResultResponse {

    @XmlElement(required = true)
    private Object resultValues;

    public GetResultResponseType() {
        
    }
    
    public GetResultResponseType(final Object resultValues) {
        this.resultValues = resultValues;
    }
    
    /**
     * Gets the value of the resultValues property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getResultValues() {
        return resultValues;
    }

    /**
     * Sets the value of the resultValues property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setResultValues(Object value) {
        this.resultValues = value;
    }

}
