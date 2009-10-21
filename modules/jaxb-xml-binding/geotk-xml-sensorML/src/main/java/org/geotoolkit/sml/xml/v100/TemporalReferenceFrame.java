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
package org.geotoolkit.sml.xml.v100;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v311.TemporalCRSType;
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
 *         &lt;element ref="{http://www.opengis.net/gml}TemporalCRS"/>
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
@XmlType(name = "", propOrder = {
    "temporalCRS"
})
@XmlRootElement(name = "temporalReferenceFrame")
public class TemporalReferenceFrame {

    @XmlElement(name = "TemporalCRS", namespace = "http://www.opengis.net/gml", required = true)
    private TemporalCRSType temporalCRS;

    public TemporalReferenceFrame() {

    }

    public TemporalReferenceFrame(TemporalCRSType temporalCRS) {
        this.temporalCRS = temporalCRS;
    }

    /**
     * Gets the value of the temporalCRS property.
     */
    public TemporalCRSType getTemporalCRS() {
        return temporalCRS;
    }

    /**
     * Sets the value of the temporalCRS property.
     */
    public void setTemporalCRS(TemporalCRSType value) {
        this.temporalCRS = value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[TemporalReferenceFrame]").append("\n");
        if (temporalCRS != null) {
            sb.append("temporalCRS: ").append(temporalCRS).append('\n');
        }
        return sb.toString();
    }

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }

        if (object instanceof TemporalReferenceFrame) {
            final TemporalReferenceFrame that = (TemporalReferenceFrame) object;

            return Utilities.equals(this.temporalCRS,  that.temporalCRS);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + (this.temporalCRS != null ? this.temporalCRS.hashCode() : 0);
        return hash;
    }

}
