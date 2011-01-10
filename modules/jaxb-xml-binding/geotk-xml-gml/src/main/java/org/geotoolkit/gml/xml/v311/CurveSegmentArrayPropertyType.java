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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;


/**
 * A container for an array of curve segments.
 * 
 * <p>Java class for CurveSegmentArrayPropertyType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CurveSegmentArrayPropertyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/gml}_CurveSegment" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "CurveSegmentArrayPropertyType", propOrder = {
    "abstractCurveSegment"
})
public class CurveSegmentArrayPropertyType {

    @XmlElementRef(name = "AbstractCurveSegment", namespace = "http://www.opengis.net/gml", type = JAXBElement.class)
    private List<JAXBElement<? extends AbstractCurveSegmentType>> abstractCurveSegment;

    public CurveSegmentArrayPropertyType() {}

    public CurveSegmentArrayPropertyType(final List<? extends AbstractCurveSegmentType> segments) {
        if (segments != null) {
            ObjectFactory factory = new ObjectFactory();
            abstractCurveSegment = new ArrayList<JAXBElement<? extends AbstractCurveSegmentType>>();
            for (AbstractCurveSegmentType segment : segments) {
                abstractCurveSegment.add(factory.createLineStringSegment((LineStringSegmentType) segment));
            }
        }
    }

    /**
     * Gets the value of the curveSegment property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link LineStringSegmentType }{@code >}
     * {@link JAXBElement }{@code <}{@link ArcByBulgeType }{@code >}
     * {@link JAXBElement }{@code <}{@link ClothoidType }{@code >}
     * {@link JAXBElement }{@code <}{@link OffsetCurveType }{@code >}
     * {@link JAXBElement }{@code <}{@link BezierType }{@code >}
     * {@link JAXBElement }{@code <}{@link ArcByCenterPointType }{@code >}
     * {@link JAXBElement }{@code <}{@link CubicSplineType }{@code >}
     * {@link JAXBElement }{@code <}{@link CircleType }{@code >}
     * {@link JAXBElement }{@code <}{@link ArcStringByBulgeType }{@code >}
     * {@link JAXBElement }{@code <}{@link ArcType }{@code >}
     * {@link JAXBElement }{@code <}{@link GeodesicType }{@code >}
     * {@link JAXBElement }{@code <}{@link ArcStringType }{@code >}
     * {@link JAXBElement }{@code <}{@link CircleByCenterPointType }{@code >}
     * {@link JAXBElement }{@code <}{@link AbstractCurveSegmentType }{@code >}
     * {@link JAXBElement }{@code <}{@link BSplineType }{@code >}
     * {@link JAXBElement }{@code <}{@link GeodesicStringType }{@code >}
     * 
     * 
     */
    public List<JAXBElement<? extends AbstractCurveSegmentType>> getJbAbstractCurveSegment() {
        if (abstractCurveSegment == null) {
            abstractCurveSegment = new ArrayList<JAXBElement<? extends AbstractCurveSegmentType>>();
        }
        return this.abstractCurveSegment;
    }

    public void setJbAbstractCurveSegment(final List<JAXBElement<? extends AbstractCurveSegmentType>> abstractCurveSegment) {
        this.abstractCurveSegment = abstractCurveSegment;
    }

    public void setAbstractCurveSegment(final AbstractCurveSegmentType abstractCurveSegment) {
        if (this.abstractCurveSegment == null) {
            this.abstractCurveSegment = new ArrayList<JAXBElement<? extends AbstractCurveSegmentType>>();
        }
        ObjectFactory factory = new ObjectFactory();
        if (abstractCurveSegment instanceof LineStringSegmentType) {
            this.abstractCurveSegment.add(factory.createLineStringSegment((LineStringSegmentType) abstractCurveSegment));
        } else if (abstractCurveSegment instanceof ArcByBulgeType) {
            this.abstractCurveSegment.add(factory.createArcByBulge((ArcByBulgeType) abstractCurveSegment));
        } else if (abstractCurveSegment instanceof ClothoidType) {
            this.abstractCurveSegment.add(factory.createClothoid((ClothoidType) abstractCurveSegment));
        } else if (abstractCurveSegment instanceof OffsetCurveType) {
            this.abstractCurveSegment.add(factory.createOffsetCurve((OffsetCurveType) abstractCurveSegment));
        } else if (abstractCurveSegment instanceof BezierType) {
            this.abstractCurveSegment.add(factory.createBezier((BezierType) abstractCurveSegment));
        } else if (abstractCurveSegment instanceof ArcByCenterPointType) {
            this.abstractCurveSegment.add(factory.createArcByCenterPoint((ArcByCenterPointType) abstractCurveSegment));
        } else if (abstractCurveSegment instanceof CubicSplineType) {
            this.abstractCurveSegment.add(factory.createCubicSpline((CubicSplineType) abstractCurveSegment));
        } else if (abstractCurveSegment instanceof CircleType) {
            this.abstractCurveSegment.add(factory.createCircle((CircleType) abstractCurveSegment));
        } else if (abstractCurveSegment instanceof ArcStringByBulgeType) {
            this.abstractCurveSegment.add(factory.createArcStringByBulge((ArcStringByBulgeType) abstractCurveSegment));
        } else if (abstractCurveSegment instanceof ArcType) {
            this.abstractCurveSegment.add(factory.createArc((ArcType) abstractCurveSegment));
        } else if (abstractCurveSegment instanceof GeodesicType) {
            this.abstractCurveSegment.add(factory.createGeodesic((GeodesicType) abstractCurveSegment));
        } else if (abstractCurveSegment instanceof ArcStringType) {
            this.abstractCurveSegment.add(factory.createArcString((ArcStringType) abstractCurveSegment));
        } else if (abstractCurveSegment instanceof CircleByCenterPointType) {
            this.abstractCurveSegment.add(factory.createCircleByCenterPoint((CircleByCenterPointType) abstractCurveSegment));
        } else if (abstractCurveSegment instanceof BSplineType) {
            this.abstractCurveSegment.add(factory.createBSpline((BSplineType) abstractCurveSegment));
        } else if (abstractCurveSegment instanceof GeodesicStringType) {
            this.abstractCurveSegment.add(factory.createGeodesicString((GeodesicStringType) abstractCurveSegment));
        } else {
            throw new IllegalArgumentException("Unexpected CUreType:" + abstractCurveSegment);
        }

    }

    public List<? extends AbstractCurveSegmentType> getAbstractCurveSegment() {
        if (abstractCurveSegment == null) {
            abstractCurveSegment = new ArrayList<JAXBElement<? extends AbstractCurveSegmentType>>();
        }
        List<AbstractCurveSegmentType> response = new ArrayList<AbstractCurveSegmentType>();
        for (JAXBElement<? extends AbstractCurveSegmentType> jb : abstractCurveSegment) {
            response.add(jb.getValue());
        }
        return response;
    }

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof CurveSegmentArrayPropertyType) {
            final CurveSegmentArrayPropertyType that = (CurveSegmentArrayPropertyType) object;

            if (this.abstractCurveSegment != null && that.abstractCurveSegment != null) {
                for (int i = 0; i < abstractCurveSegment.size(); i++) {
                    AbstractCurveSegmentType thisGeom = this.abstractCurveSegment.get(i).getValue();
                    AbstractCurveSegmentType thatGeom = that.abstractCurveSegment.get(i).getValue();

                    if (!Utilities.equals(thisGeom,   thatGeom))
                        return false;
                }
                return true;
            } else if (this.abstractCurveSegment == null && that.abstractCurveSegment == null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.abstractCurveSegment != null ? this.abstractCurveSegment.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[CurveSegmentArrayPropertyType]\n");
        if (abstractCurveSegment != null) {
            sb.append("segments:").append('\n');
            for (JAXBElement<? extends AbstractCurveSegmentType> s : abstractCurveSegment) {
                sb.append(s.getValue()).append('\n');
            }
        }
        return sb.toString();
    }
}
