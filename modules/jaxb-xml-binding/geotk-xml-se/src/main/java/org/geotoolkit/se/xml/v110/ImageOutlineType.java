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
package org.geotoolkit.se.xml.v110;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ImageOutlineType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ImageOutlineType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element ref="{http://www.opengis.net/se}LineSymbolizer"/>
 *         &lt;element ref="{http://www.opengis.net/se}PolygonSymbolizer"/>
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
@XmlType(name = "ImageOutlineType", propOrder = {
    "lineSymbolizer",
    "polygonSymbolizer"
})
public class ImageOutlineType {

    @XmlElement(name = "LineSymbolizer")
    protected LineSymbolizerType lineSymbolizer;
    @XmlElement(name = "PolygonSymbolizer")
    protected PolygonSymbolizerType polygonSymbolizer;

    /**
     * Gets the value of the lineSymbolizer property.
     * 
     * @return
     *     possible object is
     *     {@link LineSymbolizerType }
     *     
     */
    public LineSymbolizerType getLineSymbolizer() {
        return lineSymbolizer;
    }

    /**
     * Sets the value of the lineSymbolizer property.
     * 
     * @param value
     *     allowed object is
     *     {@link LineSymbolizerType }
     *     
     */
    public void setLineSymbolizer(final LineSymbolizerType value) {
        this.lineSymbolizer = value;
    }

    /**
     * Gets the value of the polygonSymbolizer property.
     * 
     * @return
     *     possible object is
     *     {@link PolygonSymbolizerType }
     *     
     */
    public PolygonSymbolizerType getPolygonSymbolizer() {
        return polygonSymbolizer;
    }

    /**
     * Sets the value of the polygonSymbolizer property.
     * 
     * @param value
     *     allowed object is
     *     {@link PolygonSymbolizerType }
     *     
     */
    public void setPolygonSymbolizer(final PolygonSymbolizerType value) {
        this.polygonSymbolizer = value;
    }

}
