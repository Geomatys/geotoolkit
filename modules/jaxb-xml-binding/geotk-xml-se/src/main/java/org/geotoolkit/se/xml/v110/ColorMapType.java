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
 * <p>Java class for ColorMapType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ColorMapType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element ref="{http://www.opengis.net/se}Categorize"/>
 *         &lt;element ref="{http://www.opengis.net/se}Interpolate"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ColorMapType", propOrder = {
    "categorize",
    "interpolate"
})
public class ColorMapType {

    @XmlElement(name = "Categorize")
    protected CategorizeType categorize;
    @XmlElement(name = "Interpolate")
    protected InterpolateType interpolate;

    /**
     * Gets the value of the categorize property.
     * 
     * @return
     *     possible object is
     *     {@link CategorizeType }
     *     
     */
    public CategorizeType getCategorize() {
        return categorize;
    }

    /**
     * Sets the value of the categorize property.
     * 
     * @param value
     *     allowed object is
     *     {@link CategorizeType }
     *     
     */
    public void setCategorize(CategorizeType value) {
        this.categorize = value;
    }

    /**
     * Gets the value of the interpolate property.
     * 
     * @return
     *     possible object is
     *     {@link InterpolateType }
     *     
     */
    public InterpolateType getInterpolate() {
        return interpolate;
    }

    /**
     * Sets the value of the interpolate property.
     * 
     * @param value
     *     allowed object is
     *     {@link InterpolateType }
     *     
     */
    public void setInterpolate(InterpolateType value) {
        this.interpolate = value;
    }

}
