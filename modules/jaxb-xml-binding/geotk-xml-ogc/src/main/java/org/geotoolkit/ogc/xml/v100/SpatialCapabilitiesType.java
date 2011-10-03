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
package org.geotoolkit.ogc.xml.v100;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Spatial_CapabilitiesType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Spatial_CapabilitiesType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Spatial_Operators" type="{http://www.opengis.net/ogc}Spatial_OperatorsType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Spatial_CapabilitiesType", propOrder = {
    "spatialOperators"
})
public class SpatialCapabilitiesType {

    @XmlElement(name = "Spatial_Operators", required = true)
    protected SpatialOperatorsType spatialOperators;

    /**
     * Gets the value of the spatialOperators property.
     * 
     * @return
     *     possible object is
     *     {@link SpatialOperatorsType }
     *     
     */
    public SpatialOperatorsType getSpatialOperators() {
        return spatialOperators;
    }

    /**
     * Sets the value of the spatialOperators property.
     * 
     * @param value
     *     allowed object is
     *     {@link SpatialOperatorsType }
     *     
     */
    public void setSpatialOperators(SpatialOperatorsType value) {
        this.spatialOperators = value;
    }

}
