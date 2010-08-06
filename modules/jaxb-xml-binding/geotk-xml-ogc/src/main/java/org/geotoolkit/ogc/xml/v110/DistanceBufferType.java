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
package org.geotoolkit.ogc.xml.v110;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v311.AbstractGeometryType;
import org.geotoolkit.gml.xml.v311.CurveType;
import org.geotoolkit.gml.xml.v311.LineStringType;
import org.geotoolkit.gml.xml.v311.LinearRingType;
import org.geotoolkit.gml.xml.v311.MultiCurveType;
import org.geotoolkit.gml.xml.v311.MultiLineStringType;
import org.geotoolkit.gml.xml.v311.MultiPointType;
import org.geotoolkit.gml.xml.v311.MultiPolygonType;
import org.geotoolkit.gml.xml.v311.MultiSolidType;
import org.geotoolkit.gml.xml.v311.MultiSurfaceType;
import org.opengis.filter.FilterVisitor;
import org.opengis.filter.expression.Expression;
import org.geotoolkit.gml.xml.v311.ObjectFactory;
import org.geotoolkit.gml.xml.v311.OrientableSurfaceType;
import org.geotoolkit.gml.xml.v311.PointType;
import org.geotoolkit.gml.xml.v311.PolyhedralSurfaceType;
import org.geotoolkit.gml.xml.v311.RingType;


/**
 * <p>Java class for DistanceBufferType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DistanceBufferType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/ogc}SpatialOpsType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/ogc}PropertyName"/>
 *         &lt;element ref="{http://www.opengis.net/gml}AbstractGeometry"/>
 *         &lt;element name="Distance" type="{http://www.opengis.net/ogc}DistanceType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DistanceBufferType", propOrder = {
    "propertyName",
    "abstractGeometry",
    "distance"
})
public class DistanceBufferType extends SpatialOpsType {

    @XmlElement(name = "PropertyName", required = true)
    private PropertyNameType propertyName;
    @XmlElementRef(name = "AbstractGeometry", namespace = "http://www.opengis.net/gml", type = JAXBElement.class)
    private JAXBElement<? extends AbstractGeometryType> abstractGeometry;
    @XmlElement(name = "Distance", required = true)
    private DistanceType distance;

    @XmlTransient
    private ObjectFactory factory = new ObjectFactory();
    /**
     * An empty constructor used by JAXB
     */
    public DistanceBufferType() {
        
    }

    public void setAbstractGeometry(JAXBElement<? extends AbstractGeometryType> abstractGeometry) {
        this.abstractGeometry = abstractGeometry;
    }

    public void setDistance(DistanceType distance) {
        this.distance = distance;
    }

    public void setPropertyName(PropertyNameType propertyName) {
        this.propertyName = propertyName;
    }

    /**
     * build a new Distance buffer
     */
    public DistanceBufferType(String propertyName, AbstractGeometryType geometry, double distance, String unit) {
        this.propertyName     = new PropertyNameType(propertyName);
        this.distance         = new DistanceType(distance, unit);
        this.abstractGeometry = getCorrectJaxbElement(geometry);
    }

    private JAXBElement<? extends AbstractGeometryType> getCorrectJaxbElement(Object geometry) {
        if (geometry instanceof PointType) {
            return factory.createPoint((PointType)geometry);
        } else if (geometry instanceof OrientableSurfaceType) {
            return factory.createOrientableSurface((OrientableSurfaceType) geometry);
        } else if (geometry instanceof LinearRingType) {
            return factory.createLinearRing((LinearRingType) geometry);
        } else if (geometry instanceof RingType) {
            return factory.createRing((RingType) geometry);
        } else if (geometry instanceof PolyhedralSurfaceType) {
            return factory.createPolyhedralSurface((PolyhedralSurfaceType) geometry);
        } else if (geometry instanceof CurveType) {
            return factory.createCurve((CurveType) geometry);
        } else if (geometry instanceof PointType) {
            return factory.createPoint((PointType) geometry);
        } else if (geometry instanceof LineStringType) {
            return factory.createLineString((LineStringType) geometry);
        } else if (geometry instanceof PolyhedralSurfaceType) {
            return factory.createPolyhedralSurface((PolyhedralSurfaceType) geometry);
        } else if (geometry instanceof MultiCurveType) {
            return factory.createMultiCurve((MultiCurveType) geometry);
        } else if (geometry instanceof MultiLineStringType) {
            return factory.createMultiLineString((MultiLineStringType) geometry);
        } else if (geometry instanceof MultiPointType) {
            return factory.createMultiPoint((MultiPointType) geometry);
        } else if (geometry instanceof MultiPolygonType) {
            return factory.createMultiPolygon((MultiPolygonType) geometry);
        } else if (geometry instanceof MultiSolidType) {
            return factory.createMultiSolid((MultiSolidType) geometry);
        } else if (geometry instanceof MultiSurfaceType) {
            return factory.createMultiSurface((MultiSurfaceType) geometry);

        } else if (geometry != null){
            throw new IllegalArgumentException("unexpected Geometry type:" + geometry.getClass().getName());
        }
        return null;
    }
    
    /**
     * Gets the value of the propertyName property.
     */
    public PropertyNameType getPropertyName() {
        return propertyName;
    }

    /**
     * Gets the value of the abstractGeometry property.
     */
    public JAXBElement<? extends AbstractGeometryType> getAbstractGeometry() {
        return abstractGeometry;
    }

    
    /**
     * Gets the value of the distance property.
     */
    public DistanceType getDistanceType() {
        return distance;
    }
    
    public double getDistance() {
        if (distance != null)
            return distance.getValue();
        return 0.0;
    }

    public String getDistanceUnits() {
        if (distance != null)
            return distance.getUnits();
        return null;
    }
    
    public Expression getExpression1() {
        return propertyName;
    }

    public Expression getExpression2() {
        if (abstractGeometry != null)
            return abstractGeometry.getValue();
        return null;
    }
    
    
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(super.toString());
        if (propertyName != null)
            s.append("PropertyName=").append(propertyName.getContent()).append('\n');
        if (abstractGeometry != null) {
            s.append("abstract Geometry= ").append(abstractGeometry.getValue().toString()).append('\n');
        } else {
            s.append("abstract Geometry null").append('\n');
        }
        if (distance != null) {
            s.append("distance= ").append(distance.toString()).append('\n');
        } else {
            s.append("distance null").append('\n');
        }
        return s.toString();
    }

    @Override
    public boolean evaluate(Object object) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object accept(FilterVisitor visitor, Object extraData) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
