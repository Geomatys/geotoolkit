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
package org.geotoolkit.sml.xml.v101;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v311.EngineeringCRSType;
import org.geotoolkit.sml.xml.AbstractSpatialReferenceFrame;


/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/gml}EngineeringCRS"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "engineeringCRS"
})
@XmlRootElement(name = "SpatialReferenceFrame")
public class SpatialReferenceFrame extends SensorObject implements AbstractSpatialReferenceFrame {

    @XmlElement(name = "EngineeringCRS", namespace = "http://www.opengis.net/gml", required = true)
    protected EngineeringCRSType engineeringCRS;

    public SpatialReferenceFrame() {

    }

    public SpatialReferenceFrame(final AbstractSpatialReferenceFrame sr) {
        if (sr != null) {
            this.engineeringCRS = sr.getEngineeringCRS();
        }
    }

    public SpatialReferenceFrame(final EngineeringCRSType engineeringCRS) {
        this.engineeringCRS = engineeringCRS;
    }

    /**
     * Gets the value of the engineeringCRS property.
     *
     */
    public EngineeringCRSType getEngineeringCRS() {
        return engineeringCRS;
    }

    /**
     * Sets the value of the engineeringCRS property.
     *
     */
    public void setEngineeringCRS(final EngineeringCRSType value) {
        this.engineeringCRS = value;
    }

}
