/*
 *    Constellation - An open SpatialReferenceFrame and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2008, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.sml.v100;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v311modified.EngineeringCRSType;
import org.geotoolkit.util.Utilities;


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
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "engineeringCRS"
})
@XmlRootElement(name = "spatialReferenceFrame")
public class SpatialReferenceFrame {

    @XmlElement(name = "EngineeringCRS", namespace = "http://www.opengis.net/gml", required = true)
    private EngineeringCRSType engineeringCRS;

    public SpatialReferenceFrame() {

    }

    public SpatialReferenceFrame(EngineeringCRSType engineeringCRS) {
        this.engineeringCRS = engineeringCRS;
    }

    /**
     * Gets the value of the engineeringCRS property.
     * 
     * @return
     *     possible object is
     *     {@link EngineeringCRSType }
     *     
     */
    public EngineeringCRSType getEngineeringCRS() {
        return engineeringCRS;
    }

    /**
     * Sets the value of the engineeringCRS property.
     * 
     * @param value
     *     allowed object is
     *     {@link EngineeringCRSType }
     *     
     */
    public void setEngineeringCRS(EngineeringCRSType value) {
        this.engineeringCRS = value;
    }

    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }

        if (object instanceof SpatialReferenceFrame) {
            final SpatialReferenceFrame that = (SpatialReferenceFrame) object;
            return Utilities.equals(this.engineeringCRS, that.engineeringCRS);

        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.engineeringCRS != null ? this.engineeringCRS.hashCode() : 0);
        return hash;
    }



    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[SpatialReferenceFrame]").append("\n");
        if (engineeringCRS != null) {
            sb.append("engineeringCRS: ").append(engineeringCRS).append('\n');
        }
        return sb.toString();
     }
}
