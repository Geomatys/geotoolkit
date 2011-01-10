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
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;


/**
 * Curve segment defines a homogeneous segment of a curve.
 * 
 * <p>Java class for AbstractCurveSegmentType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AbstractCurveSegmentType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="numDerivativesAtStart" type="{http://www.w3.org/2001/XMLSchema}integer" default="0" />
 *       &lt;attribute name="numDerivativesAtEnd" type="{http://www.w3.org/2001/XMLSchema}integer" default="0" />
 *       &lt;attribute name="numDerivativeInterior" type="{http://www.w3.org/2001/XMLSchema}integer" default="0" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractCurveSegmentType")
@XmlSeeAlso({
    ClothoidType.class,
    BSplineType.class,
    CubicSplineType.class,
    GeodesicStringType.class,
    LineStringSegmentType.class,
    ArcByCenterPointType.class,
    ArcStringType.class,
    OffsetCurveType.class,
    ArcStringByBulgeType.class
})
public abstract class AbstractCurveSegmentType {

    @XmlAttribute
    private Integer numDerivativesAtStart;
    @XmlAttribute
    private Integer numDerivativesAtEnd;
    @XmlAttribute
    private Integer numDerivativeInterior;

    AbstractCurveSegmentType() {

    }

    public AbstractCurveSegmentType(final Integer numDerivativesAtStart, final Integer numDerivativesAtEnd, final Integer numDerivativeInterior) {
        this.numDerivativeInterior = numDerivativeInterior;
        this.numDerivativesAtEnd   = numDerivativesAtEnd;
        this.numDerivativesAtStart = numDerivativesAtStart;
    }
    /**
     * Gets the value of the numDerivativesAtStart property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getNumDerivativesAtStart() {
        /*if (numDerivativesAtStart == null) {
            return 0;
        } else {*/
        return numDerivativesAtStart;
    }

    /**
     * Sets the value of the numDerivativesAtStart property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setNumDerivativesAtStart(final Integer value) {
        this.numDerivativesAtStart = value;
    }

    /**
     * Gets the value of the numDerivativesAtEnd property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getNumDerivativesAtEnd() {
        /*if (numDerivativesAtEnd == null) {
            return 0;
        } else {*/
        return numDerivativesAtEnd;
    }

    /**
     * Sets the value of the numDerivativesAtEnd property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setNumDerivativesAtEnd(final Integer value) {
        this.numDerivativesAtEnd = value;
    }

    /**
     * Gets the value of the numDerivativeInterior property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getNumDerivativeInterior() {
        /*if (numDerivativeInterior == null) {
            return 0;
        } else {*/
        return numDerivativeInterior;
    }

    /**
     * Sets the value of the numDerivativeInterior property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setNumDerivativeInterior(final Integer value) {
        this.numDerivativeInterior = value;
    }

    /**
     * Verify that the point is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof AbstractCurveSegmentType) {
            final AbstractCurveSegmentType that = (AbstractCurveSegmentType) object;
            return  Utilities.equals(this.numDerivativeInterior, that.numDerivativeInterior) &&
                    Utilities.equals(this.numDerivativesAtEnd,   that.numDerivativesAtEnd)   &&
                    Utilities.equals(this.numDerivativesAtStart, that.numDerivativesAtStart);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 31 * hash + (this.numDerivativesAtStart != null ? this.numDerivativesAtStart.hashCode() : 0);
        hash = 31 * hash + (this.numDerivativesAtEnd   != null ? this.numDerivativesAtEnd.hashCode()   : 0);
        hash = 31 * hash + (this.numDerivativeInterior != null ? this.numDerivativeInterior.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[").append(this.getClass().getSimpleName()).append("]\n");
        if (numDerivativesAtStart != null) {
            sb.append("numDerivativesAtStart:").append(numDerivativesAtStart).append('\n');
        }
        if (numDerivativesAtEnd != null) {
            sb.append("numDerivativesAtEnd:").append(numDerivativesAtEnd).append('\n');
        }
        if (numDerivativeInterior != null) {
            sb.append("numDerivativeInterior:").append(numDerivativeInterior).append('\n');
        }
        return sb.toString();
    }
}
