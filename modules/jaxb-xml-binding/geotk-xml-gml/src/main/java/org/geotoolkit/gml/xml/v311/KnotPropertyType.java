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
package org.geotoolkit.gml.xml.v311;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Encapsulates a knot to use it in a geometric type.
 * 
 * <p>Java class for KnotPropertyType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="KnotPropertyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Knot" type="{http://www.opengis.net/gml}KnotType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "KnotPropertyType", propOrder = {
    "knot"
})
public class KnotPropertyType {

    @XmlElement(name = "Knot", required = true)
    protected KnotType knot;

    /**
     * Gets the value of the knot property.
     * 
     * @return
     *     possible object is
     *     {@link KnotType }
     *     
     */
    public KnotType getKnot() {
        return knot;
    }

    /**
     * Sets the value of the knot property.
     * 
     * @param value
     *     allowed object is
     *     {@link KnotType }
     *     
     */
    public void setKnot(KnotType value) {
        this.knot = value;
    }

}
