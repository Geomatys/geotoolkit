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

package org.geotoolkit.observation.xml.v200;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * ObservationContext is a dataType, without identity, so may only be used
 * 				inline
 * 
 * <p>Java class for ObservationContextPropertyType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ObservationContextPropertyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/om/2.0}ObservationContext"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ObservationContextPropertyType", propOrder = {
    "observationContext"
})
public class ObservationContextPropertyType {

    @XmlElement(name = "ObservationContext", required = true)
    private ObservationContextType observationContext;

    /**
     * Gets the value of the observationContext property.
     * 
     * @return
     *     possible object is
     *     {@link ObservationContextType }
     *     
     */
    public ObservationContextType getObservationContext() {
        return observationContext;
    }

    /**
     * Sets the value of the observationContext property.
     * 
     * @param value
     *     allowed object is
     *     {@link ObservationContextType }
     *     
     */
    public void setObservationContext(ObservationContextType value) {
        this.observationContext = value;
    }

}
