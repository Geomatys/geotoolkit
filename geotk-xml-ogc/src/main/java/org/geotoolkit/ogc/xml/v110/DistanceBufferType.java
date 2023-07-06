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

import java.util.Arrays;
import java.util.List;
import javax.measure.Quantity;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.XmlType;
import org.apache.sis.measure.Quantities;
import org.apache.sis.measure.Units;
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
import org.opengis.filter.Expression;
import org.geotoolkit.gml.xml.v311.ObjectFactory;
import org.geotoolkit.gml.xml.v311.OrientableSurfaceType;
import org.geotoolkit.gml.xml.v311.PointType;
import org.geotoolkit.gml.xml.v311.PolyhedralSurfaceType;
import org.geotoolkit.gml.xml.v311.RingType;
import org.opengis.filter.DistanceOperator;
import org.opengis.geometry.Geometry;


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
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DistanceBufferType", propOrder = {
    "propertyName",
    "abstractGeometry",
    "distance"
})
public abstract class DistanceBufferType extends SpatialOpsType implements DistanceOperator {

    @XmlElement(name = "PropertyName", required = true)
    private PropertyNameType propertyName;
    @XmlElementRef(name = "AbstractGeometry", namespace = "http://www.opengis.net/gml", type = JAXBElement.class)
    private JAXBElement<? extends AbstractGeometryType> abstractGeometry;
    @XmlElement(name = "Distance", required = true)
    private DistanceType distance;

    @XmlTransient
    private static final ObjectFactory factory = new ObjectFactory();

    /**
     * An empty constructor used by JAXB
     */
    public DistanceBufferType() {
    }

    /**
     * build a new Distance buffer
     */
    public DistanceBufferType(final String propertyName, final AbstractGeometryType geometry, final double distance, final String unit) {
        this.propertyName     = new PropertyNameType(propertyName);
        this.distance         = new DistanceType(distance, unit);
        this.abstractGeometry = getCorrectJaxbElement(geometry);
    }

    public DistanceBufferType(final DistanceBufferType that) {
        if (that != null) {
            if (that.propertyName != null) {
                this.propertyName = new PropertyNameType(that.propertyName);
            }
            if (that.abstractGeometry != null) {
                try {
                    final AbstractGeometryType geom = that.abstractGeometry.getValue().clone();
                } catch (CloneNotSupportedException ex) {
                    throw new IllegalArgumentException("Clone is not supported on type:" + that.abstractGeometry.getValue().getClass().getName());
                }
            }
            if (that.distance != null) {
                this.distance = new DistanceType(that.distance.getValue(), that.distance.getUnits());
            }
        }
    }

    private JAXBElement<? extends AbstractGeometryType> getCorrectJaxbElement(final Object geometry) {
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

    public void setAbstractGeometry(final JAXBElement<? extends AbstractGeometryType> abstractGeometry) {
        this.abstractGeometry = abstractGeometry;
    }

    public void setDistance(final DistanceType distance) {
        this.distance = distance;
    }

    public void setPropertyName(final PropertyNameType propertyName) {
        this.propertyName = propertyName;
    }

    /**
     * Gets the value of the propertyName property.
     */
    public PropertyNameType getPropertyName() {
        return propertyName;
    }

    @Override
    public Geometry getGeometry() {
        throw new UnsupportedOperationException();
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

    @Override
    public Quantity getDistance() {
        if (distance != null) {
            return Quantities.create(distance.getValue(), Units.valueOf(distance.getUnits()));
        }
        return Quantities.create(0, Units.METRE);
    }

    public String getDistanceUnits() {
        if (distance != null) {
            return distance.getUnits();
        }
        return null;
    }

    @Override
    public List getExpressions() {
        return Arrays.asList(getExpression1(), getExpression2());
    }

    public Expression getExpression1() {
        return propertyName;
    }

    public Expression getExpression2() {
        if (abstractGeometry != null) {
            return abstractGeometry.getValue();
        }
        return null;
    }


    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(super.toString());
        if (propertyName != null) {
            s.append("PropertyName=").append(propertyName.getContent()).append('\n');
        }
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
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DistanceBufferType other = (DistanceBufferType) obj;
        if (this.propertyName != other.propertyName && (this.propertyName == null || !this.propertyName.equals(other.propertyName))) {
            return false;
        }
        if (this.abstractGeometry != other.abstractGeometry && (this.abstractGeometry == null || !this.abstractGeometry.equals(other.abstractGeometry))) {
            return false;
        }
        if (this.distance != other.distance && (this.distance == null || !this.distance.equals(other.distance))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + (this.propertyName != null ? this.propertyName.hashCode() : 0);
        hash = 23 * hash + (this.abstractGeometry != null ? this.abstractGeometry.hashCode() : 0);
        hash = 23 * hash + (this.distance != null ? this.distance.hashCode() : 0);
        return hash;
    }
}
