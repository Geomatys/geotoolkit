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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.OrientableCurve;

/**
 * OrientableCurve consists of a curve and an orientation. If the orientation is "+", then the OrientableCurve is identical to the baseCurve. If the orientation is "-", then the OrientableCurve is related to another _Curve with a parameterization that reverses the sense of the curve traversal.
 *
 * <p>Java class for OrientableCurveType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="OrientableCurveType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml}AbstractCurveType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/gml}baseCurve"/>
 *       &lt;/sequence>
 *       &lt;attribute name="orientation" type="{http://www.opengis.net/gml}SignType" default="+" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OrientableCurveType", propOrder = {
    "baseCurve"
})
@XmlRootElement(name="OrientableCurve")
public class OrientableCurveType extends AbstractCurveType implements OrientableCurve{

    @XmlElement(required = true)
    protected CurvePropertyType baseCurve;
    @XmlAttribute
    protected String orientation;

    /**
     * References or contains the base curve (positive orientation).
     * NOTE: This definition allows for a nested structure, i.e. an OrientableCurve may use another OrientableCurve as its base curve.
     *
     * @return
     *     possible object is
     *     {@link CurvePropertyType }
     *
     */
    public CurvePropertyType getBaseCurve() {
        return baseCurve;
    }

    /**
     * References or contains the base curve (positive orientation).
     * NOTE: This definition allows for a nested structure, i.e. an OrientableCurve may use another OrientableCurve as its base curve.
     *
     * @param value
     *     allowed object is
     *     {@link CurvePropertyType }
     *
     */
    public void setBaseCurve(final CurvePropertyType value) {
        this.baseCurve = value;
    }

    /**
     * Gets the value of the orientation property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getOrientation() {
        if (orientation == null) {
            return "+";
        } else {
            return orientation;
        }
    }

    /**
     * Sets the value of the orientation property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setOrientation(final String value) {
        this.orientation = value;
    }

}
