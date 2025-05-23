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

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * An offset curve is a curve at a constant
 *       distance from the basis curve. They can be useful as a cheap
 *       and simple alternative to constructing curves that are offsets
 *       by definition.
 *
 * <p>Java class for OffsetCurveType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="OffsetCurveType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml}AbstractCurveSegmentType">
 *       &lt;sequence>
 *         &lt;element name="offsetBase" type="{http://www.opengis.net/gml}CurvePropertyType"/>
 *         &lt;element name="distance" type="{http://www.opengis.net/gml}LengthType"/>
 *         &lt;element name="refDirection" type="{http://www.opengis.net/gml}VectorType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OffsetCurveType", propOrder = {
    "offsetBase",
    "distance",
    "refDirection"
})
public class OffsetCurveType
    extends AbstractCurveSegmentType
{

    @XmlElement(required = true)
    protected CurvePropertyType offsetBase;
    @XmlElement(required = true)
    protected LengthType distance;
    protected VectorType refDirection;

    /**
     * Gets the value of the offsetBase property.
     *
     * @return
     *     possible object is
     *     {@link CurvePropertyType }
     *
     */
    public CurvePropertyType getOffsetBase() {
        return offsetBase;
    }

    /**
     * Sets the value of the offsetBase property.
     *
     * @param value
     *     allowed object is
     *     {@link CurvePropertyType }
     *
     */
    public void setOffsetBase(final CurvePropertyType value) {
        this.offsetBase = value;
    }

    /**
     * Gets the value of the distance property.
     *
     * @return
     *     possible object is
     *     {@link LengthType }
     *
     */
    public LengthType getDistance() {
        return distance;
    }

    /**
     * Sets the value of the distance property.
     *
     * @param value
     *     allowed object is
     *     {@link LengthType }
     *
     */
    public void setDistance(final LengthType value) {
        this.distance = value;
    }

    /**
     * Gets the value of the refDirection property.
     *
     * @return
     *     possible object is
     *     {@link VectorType }
     *
     */
    public VectorType getRefDirection() {
        return refDirection;
    }

    /**
     * Sets the value of the refDirection property.
     *
     * @param value
     *     allowed object is
     *     {@link VectorType }
     *
     */
    public void setRefDirection(final VectorType value) {
        this.refDirection = value;
    }

}
