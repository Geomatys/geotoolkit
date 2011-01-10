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
 * A container for an array of geometry elements. The elements are always contained in the array property, 
 * 			referencing geometry elements or arrays of geometry elements is not supported.
 * 
 * <p>Java class for GeometryArrayPropertyType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GeometryArrayPropertyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/gml}AbstractGeometry" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "GeometryArrayPropertyType", propOrder = {
    "abstractGeometry"
})
public class GeometryArrayPropertyType {

    @XmlElementRef(name = "AbstractGeometry", namespace = "http://www.opengis.net/gml", type = JAXBElement.class)
    private List<JAXBElement<? extends AbstractGeometryType>> abstractGeometry;

    /**
     * Gets the value of the abstractGeometry property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link OrientableSurfaceType }{@code >}
     * {@link JAXBElement }{@code <}{@link AbstractSolidType }{@code >}
     * {@link JAXBElement }{@code <}{@link MultiSurfaceType }{@code >}
     * {@link JAXBElement }{@code <}{@link AbstractGeometricAggregateType }{@code >}
     * {@link JAXBElement }{@code <}{@link SolidType }{@code >}
     * {@link JAXBElement }{@code <}{@link TinType }{@code >}
     * {@link JAXBElement }{@code <}{@link MultiGeometryType }{@code >}
     * {@link JAXBElement }{@code <}{@link SurfaceType }{@code >}
     * {@link JAXBElement }{@code <}{@link MultiPointType }{@code >}
     * {@link JAXBElement }{@code <}{@link LineStringType }{@code >}
     * {@link JAXBElement }{@code <}{@link AbstractCurveType }{@code >}
     * {@link JAXBElement }{@code <}{@link AbstractSurfaceType }{@code >}
     * {@link JAXBElement }{@code <}{@link LinearRingType }{@code >}
     * {@link JAXBElement }{@code <}{@link AbstractRingType }{@code >}
     * {@link JAXBElement }{@code <}{@link TriangulatedSurfaceType }{@code >}
     * {@link JAXBElement }{@code <}{@link PolyhedralSurfaceType }{@code >}
     * {@link JAXBElement }{@code <}{@link CurveType }{@code >}
     * {@link JAXBElement }{@code <}{@link MultiLineStringType }{@code >}
     * {@link JAXBElement }{@code <}{@link AbstractGeometryType }{@code >}
     * {@link JAXBElement }{@code <}{@link OrientableCurveType }{@code >}
     * {@link JAXBElement }{@code <}{@link MultiPolygonType }{@code >}
     * {@link JAXBElement }{@code <}{@link PolygonType }{@code >}
     * {@link JAXBElement }{@code <}{@link RingType }{@code >}
     * {@link JAXBElement }{@code <}{@link MultiCurveType }{@code >}
     * {@link JAXBElement }{@code <}{@link AbstractGeometricPrimitiveType }{@code >}
     * {@link JAXBElement }{@code <}{@link PointType }{@code >}
     * {@link JAXBElement }{@code <}{@link MultiSolidType }{@code >}
     * 
     * 
     */
    public List<JAXBElement<? extends AbstractGeometryType>> getJbAbstractGeometry() {
        if (abstractGeometry == null) {
            abstractGeometry = new ArrayList<JAXBElement<? extends AbstractGeometryType>>();
        }
        return this.abstractGeometry;
    }

    /**
     * Gets the value of the abstractGeometry property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link OrientableSurfaceType }{@code >}
     * {@link JAXBElement }{@code <}{@link AbstractSolidType }{@code >}
     * {@link JAXBElement }{@code <}{@link MultiSurfaceType }{@code >}
     * {@link JAXBElement }{@code <}{@link AbstractGeometricAggregateType }{@code >}
     * {@link JAXBElement }{@code <}{@link SolidType }{@code >}
     * {@link JAXBElement }{@code <}{@link TinType }{@code >}
     * {@link JAXBElement }{@code <}{@link MultiGeometryType }{@code >}
     * {@link JAXBElement }{@code <}{@link SurfaceType }{@code >}
     * {@link JAXBElement }{@code <}{@link MultiPointType }{@code >}
     * {@link JAXBElement }{@code <}{@link LineStringType }{@code >}
     * {@link JAXBElement }{@code <}{@link AbstractCurveType }{@code >}
     * {@link JAXBElement }{@code <}{@link AbstractSurfaceType }{@code >}
     * {@link JAXBElement }{@code <}{@link LinearRingType }{@code >}
     * {@link JAXBElement }{@code <}{@link AbstractRingType }{@code >}
     * {@link JAXBElement }{@code <}{@link TriangulatedSurfaceType }{@code >}
     * {@link JAXBElement }{@code <}{@link PolyhedralSurfaceType }{@code >}
     * {@link JAXBElement }{@code <}{@link CurveType }{@code >}
     * {@link JAXBElement }{@code <}{@link MultiLineStringType }{@code >}
     * {@link JAXBElement }{@code <}{@link AbstractGeometryType }{@code >}
     * {@link JAXBElement }{@code <}{@link OrientableCurveType }{@code >}
     * {@link JAXBElement }{@code <}{@link MultiPolygonType }{@code >}
     * {@link JAXBElement }{@code <}{@link PolygonType }{@code >}
     * {@link JAXBElement }{@code <}{@link RingType }{@code >}
     * {@link JAXBElement }{@code <}{@link MultiCurveType }{@code >}
     * {@link JAXBElement }{@code <}{@link AbstractGeometricPrimitiveType }{@code >}
     * {@link JAXBElement }{@code <}{@link PointType }{@code >}
     * {@link JAXBElement }{@code <}{@link MultiSolidType }{@code >}
     *
     *
     */
    public void setJbAbstractGeometry(final List<JAXBElement<? extends AbstractGeometryType>> abstractGeometry) {
        this.abstractGeometry = abstractGeometry;
    }

    /**
     * Gets the value of the abstractGeometry property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@code <}{@link OrientableSurfaceType }{@code >}
     * {@code <}{@link AbstractSolidType }{@code >}
     * {@code <}{@link MultiSurfaceType }{@code >}
     * {@code <}{@link AbstractGeometricAggregateType }{@code >}
     * {@code <}{@link SolidType }{@code >}
     * {@code <}{@link TinType }{@code >}
     * {@code <}{@link MultiGeometryType }{@code >}
     * {@code <}{@link SurfaceType }{@code >}
     * {@code <}{@link MultiPointType }{@code >}
     * {@code <}{@link LineStringType }{@code >}
     * {@code <}{@link AbstractCurveType }{@code >}
     * {@code <}{@link AbstractSurfaceType }{@code >}
     * {@code <}{@link LinearRingType }{@code >}
     * {@code <}{@link AbstractRingType }{@code >}
     * {@code <}{@link TriangulatedSurfaceType }{@code >}
     * {@code <}{@link PolyhedralSurfaceType }{@code >}
     * {@code <}{@link CurveType }{@code >}
     * {@code <}{@link MultiLineStringType }{@code >}
     * {@code <}{@link AbstractGeometryType }{@code >}
     * {@code <}{@link OrientableCurveType }{@code >}
     * {@code <}{@link MultiPolygonType }{@code >}
     * {@code <}{@link PolygonType }{@code >}
     * {@code <}{@link RingType }{@code >}
     * {@code <}{@link MultiCurveType }{@code >}
     * {@code <}{@link AbstractGeometricPrimitiveType }{@code >}
     * {@code <}{@link PointType }{@code >}
     * {@code <}{@link MultiSolidType }{@code >}
     *
     *
     */
    public List<? extends AbstractGeometryType> getAbstractGeometry() {
        if (abstractGeometry == null) {
            abstractGeometry = new ArrayList<JAXBElement<? extends AbstractGeometryType>>();
        }
        final List<AbstractGeometryType> result = new ArrayList<AbstractGeometryType>();
        for (JAXBElement<? extends AbstractGeometryType> jb : abstractGeometry) {
            result.add(jb.getValue());
        }
        return result;
    }

    /**
     * Gets the value of the abstractGeometry property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@code <}{@link OrientableSurfaceType }{@code >}
     * {@code <}{@link AbstractSolidType }{@code >}
     * {@code <}{@link MultiSurfaceType }{@code >}
     * {@code <}{@link AbstractGeometricAggregateType }{@code >}
     * {@code <}{@link SolidType }{@code >}
     * {@code <}{@link TinType }{@code >}
     * {@code <}{@link MultiGeometryType }{@code >}
     * {@code <}{@link SurfaceType }{@code >}
     * {@code <}{@link MultiPointType }{@code >}
     * {@code <}{@link LineStringType }{@code >}
     * {@code <}{@link AbstractCurveType }{@code >}
     * {@code <}{@link AbstractSurfaceType }{@code >}
     * {@code <}{@link LinearRingType }{@code >}
     * {@code <}{@link AbstractRingType }{@code >}
     * {@code <}{@link TriangulatedSurfaceType }{@code >}
     * {@code <}{@link PolyhedralSurfaceType }{@code >}
     * {@code <}{@link CurveType }{@code >}
     * {@code <}{@link MultiLineStringType }{@code >}
     * {@code <}{@link AbstractGeometryType }{@code >}
     * {@code <}{@link OrientableCurveType }{@code >}
     * {@code <}{@link MultiPolygonType }{@code >}
     * {@code <}{@link PolygonType }{@code >}
     * {@code <}{@link RingType }{@code >}
     * {@code <}{@link MultiCurveType }{@code >}
     * {@code <}{@link AbstractGeometricPrimitiveType }{@code >}
     * {@code <}{@link PointType }{@code >}
     * {@code <}{@link MultiSolidType }{@code >}
     *
     *
     */
    public void setAbstractGeometry(final List<? extends AbstractGeometryType> abstractGeometry) {
        if (this.abstractGeometry == null) {
            this.abstractGeometry = new ArrayList<JAXBElement<? extends AbstractGeometryType>>();
        }
        final ObjectFactory factory = new ObjectFactory();
        for (AbstractGeometryType value : abstractGeometry) {
            if (value instanceof PolygonType) {
                this.abstractGeometry.add(factory.createPolygon((PolygonType) value));
            } else if (value instanceof OrientableSurfaceType) {
                this.abstractGeometry.add(factory.createOrientableSurface((OrientableSurfaceType) value));
            } else if (value instanceof LinearRingType) {
                this.abstractGeometry.add(factory.createLinearRing((LinearRingType) value));
            } else if (value instanceof RingType) {
                this.abstractGeometry.add(factory.createRing((RingType) value));
            } else if (value instanceof PolyhedralSurfaceType) {
                this.abstractGeometry.add(factory.createPolyhedralSurface((PolyhedralSurfaceType) value));
            } else if (value instanceof CurveType) {
                this.abstractGeometry.add(factory.createCurve((CurveType) value));
            } else if (value instanceof PointType) {
                this.abstractGeometry.add(factory.createPoint((PointType) value));
            } else if (value instanceof LineStringType) {
                this.abstractGeometry.add(factory.createLineString((LineStringType) value));
            } else if (value instanceof PolyhedralSurfaceType) {
                this.abstractGeometry.add(factory.createPolyhedralSurface((PolyhedralSurfaceType) value));
            } else if (value instanceof MultiCurveType) {
                this.abstractGeometry.add(factory.createMultiCurve((MultiCurveType) value));
            } else if (value instanceof MultiLineStringType) {
                this.abstractGeometry.add(factory.createMultiLineString((MultiLineStringType) value));
            } else if (value instanceof MultiPointType) {
                this.abstractGeometry.add(factory.createMultiPoint((MultiPointType) value));
            } else if (value instanceof MultiPolygonType) {
                this.abstractGeometry.add(factory.createMultiPolygon((MultiPolygonType) value));
            } else if (value instanceof MultiSolidType) {
                this.abstractGeometry.add(factory.createMultiSolid((MultiSolidType) value));
            } else if (value instanceof MultiSurfaceType) {
                this.abstractGeometry.add(factory.createMultiSurface((MultiSurfaceType) value));
            } else {
                throw new IllegalArgumentException("unexpected geometry type:" + value);
            }
        }

    }

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof GeometryArrayPropertyType) {
            final GeometryArrayPropertyType that = (GeometryArrayPropertyType) object;

            if (this.abstractGeometry != null && that.abstractGeometry != null) {
                for (int i = 0; i < abstractGeometry.size(); i++) {
                    AbstractGeometryType thisGeom = this.abstractGeometry.get(i).getValue();
                    AbstractGeometryType thatGeom = that.abstractGeometry.get(i).getValue();

                    if (!Utilities.equals(thisGeom,   thatGeom))
                        return false;
                }
                return true;
            } else if (this.abstractGeometry == null && that.abstractGeometry == null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.abstractGeometry != null ? this.abstractGeometry.hashCode() : 0);
        return hash;
    }
}
