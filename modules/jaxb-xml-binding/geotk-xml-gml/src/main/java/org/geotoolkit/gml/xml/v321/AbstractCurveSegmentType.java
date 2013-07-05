/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2012, Geomatys
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


package org.geotoolkit.gml.xml.v321;

import java.util.Objects;
import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.AbstractCurveSegment;


/**
 * <p>Java class for AbstractCurveSegmentType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="AbstractCurveSegmentType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="numDerivativesAtStart" type="{http://www.w3.org/2001/XMLSchema}integer" default="0" />
 *       &lt;attribute name="numDerivativesAtEnd" type="{http://www.w3.org/2001/XMLSchema}integer" default="0" />
 *       &lt;attribute name="numDerivativeInterior" type="{http://www.w3.org/2001/XMLSchema}integer" default="0" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractCurveSegmentType")
@XmlSeeAlso({
    GeodesicStringType.class,
    LineStringSegmentType.class,
    ArcStringType.class,
    BSplineType.class,
    OffsetCurveType.class,
    ClothoidType.class,
    CubicSplineType.class,
    ArcByCenterPointType.class,
    ArcStringByBulgeType.class
})
public abstract class AbstractCurveSegmentType implements AbstractCurveSegment {

    @XmlAttribute
    private BigInteger numDerivativesAtStart;
    @XmlAttribute
    private BigInteger numDerivativesAtEnd;
    @XmlAttribute
    private BigInteger numDerivativeInterior;

    /**
     * Gets the value of the numDerivativesAtStart property.
     *
     * @return
     *     possible object is
     *     {@link BigInteger }
     *
     */
    public BigInteger getNumDerivativesAtStart() {
        if (numDerivativesAtStart == null) {
            return new BigInteger("0");
        } else {
            return numDerivativesAtStart;
        }
    }

    /**
     * Sets the value of the numDerivativesAtStart property.
     *
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *
     */
    public void setNumDerivativesAtStart(BigInteger value) {
        this.numDerivativesAtStart = value;
    }

    /**
     * Gets the value of the numDerivativesAtEnd property.
     *
     * @return
     *     possible object is
     *     {@link BigInteger }
     *
     */
    public BigInteger getNumDerivativesAtEnd() {
        if (numDerivativesAtEnd == null) {
            return new BigInteger("0");
        } else {
            return numDerivativesAtEnd;
        }
    }

    /**
     * Sets the value of the numDerivativesAtEnd property.
     *
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *
     */
    public void setNumDerivativesAtEnd(BigInteger value) {
        this.numDerivativesAtEnd = value;
    }

    /**
     * Gets the value of the numDerivativeInterior property.
     *
     * @return
     *     possible object is
     *     {@link BigInteger }
     *
     */
    public BigInteger getNumDerivativeInterior() {
        if (numDerivativeInterior == null) {
            return new BigInteger("0");
        } else {
            return numDerivativeInterior;
        }
    }

    /**
     * Sets the value of the numDerivativeInterior property.
     *
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *
     */
    public void setNumDerivativeInterior(BigInteger value) {
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
            return  Objects.equals(this.numDerivativeInterior, that.numDerivativeInterior) &&
                    Objects.equals(this.numDerivativesAtEnd,   that.numDerivativesAtEnd)   &&
                    Objects.equals(this.numDerivativesAtStart, that.numDerivativesAtStart);
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
