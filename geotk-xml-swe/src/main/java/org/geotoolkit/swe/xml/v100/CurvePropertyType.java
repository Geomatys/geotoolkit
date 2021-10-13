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
package org.geotoolkit.swe.xml.v100;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.swe.xml.AbstractCurveProperty;


/**
 * Curve is a data-type so usually appears "by value" rather than by reference.
 *
 * <p>Java class for CurvePropertyType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="CurvePropertyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/swe/1.0}Curve"/>
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
@XmlType(name = "CurvePropertyType", propOrder = {
    "curve"
})
public class CurvePropertyType implements AbstractCurveProperty {

    @XmlElement(name = "Curve", required = true)
    private CurveType curve;

    public CurvePropertyType() {

    }

    public CurvePropertyType(final AbstractCurveProperty cp) {
        if (cp != null && cp.getCurve() != null) {
            this.curve = new CurveType(cp.getCurve());
        }
    }

    /**
     * Gets the value of the curve property.
     *
     * @return
     *     possible object is
     *     {@link CurveType }
     *
     */
    public CurveType getCurve() {
        return curve;
    }

    /**
     * Sets the value of the curve property.
     *
     * @param value
     *     allowed object is
     *     {@link CurveType }
     *
     */
    public void setCurve(final CurveType value) {
        this.curve = value;
    }

}
