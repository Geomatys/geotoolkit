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
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v311.AbstractGeometryType;
import org.geotoolkit.gml.xml.v311.CurveType;
import org.geotoolkit.gml.xml.v311.EnvelopeType;
import org.geotoolkit.gml.xml.v311.LineStringType;
import org.geotoolkit.gml.xml.v311.LinearRingType;
import org.geotoolkit.gml.xml.v311.MultiCurveType;
import org.geotoolkit.gml.xml.v311.MultiLineStringType;
import org.geotoolkit.gml.xml.v311.MultiPointType;
import org.geotoolkit.gml.xml.v311.MultiPolygonType;
import org.geotoolkit.gml.xml.v311.MultiSolidType;
import org.geotoolkit.gml.xml.v311.MultiSurfaceType;
import org.geotoolkit.gml.xml.v311.OrientableSurfaceType;
import org.geotoolkit.gml.xml.v311.PointType;
import org.geotoolkit.gml.xml.v311.PolyhedralSurfaceType;
import org.geotoolkit.gml.xml.v311.RingType;
import org.geotoolkit.util.Utilities;
import org.opengis.filter.FilterVisitor;
import org.opengis.filter.expression.Expression;


/**
 * <p>Java class for BinarySpatialOpType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BinarySpatialOpType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/ogc}SpatialOpsType">
 *       &lt;sequence>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element ref="{http://www.opengis.net/gml}AbstractGeometry"/>
 *           &lt;element ref="{http://www.opengis.net/gml}AbstractGeometricPrimitive"/>
 *           &lt;element ref="{http://www.opengis.net/gml}Point"/>
 *           &lt;element ref="{http://www.opengis.net/gml}AbstractImplicitGeometry"/>
 *           &lt;element ref="{http://www.opengis.net/gml}Envelope"/>
 *           &lt;element ref="{http://www.opengis.net/gml}EnvelopeWithTimePeriod"/>
 *           &lt;element ref="{http://www.opengis.net/ogc}PropertyName"/>
 *         &lt;/choice>
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
@XmlType(name = "BinarySpatialOpType", propOrder = {
    "propertyName", 
    "envelope",
    "abstractGeometry"
})
@XmlSeeAlso({PropertyNameType.class})
public class BinarySpatialOpType extends SpatialOpsType {

    @XmlElementRef(name = "AbstractGeometry", namespace = "http://www.opengis.net/gml", type = JAXBElement.class)
    private JAXBElement<? extends AbstractGeometryType> abstractGeometry;
    
    @XmlElementRef(name = "PropertyName",     namespace = "http://www.opengis.net/ogc", type = JAXBElement.class)
    private JAXBElement<PropertyNameType> propertyName;

    @XmlElementRef(name = "Envelope",         namespace = "http://www.opengis.net/gml", type = JAXBElement.class)
    private JAXBElement<EnvelopeType> envelope;
    
    @XmlTransient
    private ObjectFactory ogcFactory = new ObjectFactory();
    
    @XmlTransient
    private org.geotoolkit.gml.xml.v311.ObjectFactory gmlFactory = new org.geotoolkit.gml.xml.v311.ObjectFactory();
    
    /**
     * An empty constructor used by JAXB
     */
    public BinarySpatialOpType() {
        
    }
    
    /**
     * Build a new Binary spatial operator
     */
    public BinarySpatialOpType(final String propertyName, final AbstractGeometryType geometry) {
        this.propertyName     = ogcFactory.createPropertyName(new PropertyNameType(propertyName));
        this.abstractGeometry = getCorrectJaxbElement(geometry);
        
    }
    
    /**
     * Build a new Binary spatial operator
     */
    public BinarySpatialOpType(final PropertyNameType propertyName, final Object geometry) {
        this.propertyName     = ogcFactory.createPropertyName(propertyName);
        if (geometry instanceof EnvelopeType) {
            this.envelope = gmlFactory.createEnvelope((EnvelopeType)geometry);
        } else {
            this.abstractGeometry = getCorrectJaxbElement(geometry);
        }
        
    }

    private JAXBElement<? extends AbstractGeometryType> getCorrectJaxbElement(final Object geometry) {
        if (geometry instanceof PointType) {
            return gmlFactory.createPoint((PointType)geometry);
        } else if (geometry instanceof OrientableSurfaceType) {
            return gmlFactory.createOrientableSurface((OrientableSurfaceType) geometry);
        } else if (geometry instanceof LinearRingType) {
            return gmlFactory.createLinearRing((LinearRingType) geometry);
        } else if (geometry instanceof RingType) {
            return gmlFactory.createRing((RingType) geometry);
        } else if (geometry instanceof PolyhedralSurfaceType) {
            return gmlFactory.createPolyhedralSurface((PolyhedralSurfaceType) geometry);
        } else if (geometry instanceof CurveType) {
            return gmlFactory.createCurve((CurveType) geometry);
        } else if (geometry instanceof PointType) {
            return gmlFactory.createPoint((PointType) geometry);
        } else if (geometry instanceof LineStringType) {
            return gmlFactory.createLineString((LineStringType) geometry);
        } else if (geometry instanceof PolyhedralSurfaceType) {
            return gmlFactory.createPolyhedralSurface((PolyhedralSurfaceType) geometry);
        } else if (geometry instanceof MultiCurveType) {
            return gmlFactory.createMultiCurve((MultiCurveType) geometry);
        } else if (geometry instanceof MultiLineStringType) {
            return gmlFactory.createMultiLineString((MultiLineStringType) geometry);
        } else if (geometry instanceof MultiPointType) {
            return gmlFactory.createMultiPoint((MultiPointType) geometry);
        } else if (geometry instanceof MultiPolygonType) {
            return gmlFactory.createMultiPolygon((MultiPolygonType) geometry);
        } else if (geometry instanceof MultiSolidType) {
            return gmlFactory.createMultiSolid((MultiSolidType) geometry);
        } else if (geometry instanceof MultiSurfaceType) {
            return gmlFactory.createMultiSurface((MultiSurfaceType) geometry);

        } else if (geometry != null){
            throw new IllegalArgumentException("unexpected Geometry type:" + geometry.getClass().getName());
        }
        return null;
    }
    /**
     * Verify that this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof BinarySpatialOpType) {
            final BinarySpatialOpType that = (BinarySpatialOpType) object;
            
            boolean pname = false;
            if (this.propertyName != null && that.propertyName != null) {
                pname = Utilities.equals(this.propertyName.getValue(), that.propertyName.getValue());
            } else if (this.propertyName == null && that.propertyName == null)
                pname = true;
            
            boolean env = false;
            if (this.envelope != null && that.envelope != null) {
                env = Utilities.equals(this.envelope.getValue(), that.envelope.getValue());
            } else if (this.envelope == null && that.envelope == null)
                env = true;
            
            boolean abgeo = false;
            if (this.abstractGeometry != null && that.abstractGeometry != null) {
                abgeo = Utilities.equals(this.abstractGeometry.getValue(), that.abstractGeometry.getValue());
            } else if (this.abstractGeometry == null && that.abstractGeometry == null) {
                abgeo = true;
            }

            return  pname && env && abgeo;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + (this.abstractGeometry != null ? this.abstractGeometry.hashCode() : 0);
        hash = 47 * hash + (this.propertyName != null ? this.propertyName.hashCode() : 0);
        hash = 47 * hash + (this.envelope != null ? this.envelope.hashCode() : 0);
        return hash;
    }

   
   
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(super.toString());
        if (propertyName != null && propertyName.getValue() != null)
            s.append("PropertyName: ").append(propertyName.getValue().getPropertyName()).append('\n');
        
        if (envelope != null && envelope.getValue() != null)
            s.append("envelope: ").append(envelope.getValue().toString()).append('\n');
        
        if (abstractGeometry != null && abstractGeometry.getValue() != null)
            s.append("abstract Geometry: ").append(abstractGeometry.getValue().toString()).append('\n');
        
        return s.toString();
    }

    public Expression getExpression1() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Expression getExpression2() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public boolean evaluate(final Object object) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object accept(final FilterVisitor visitor, final Object extraData) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public JAXBElement<? extends AbstractGeometryType> getAbstractGeometry() {
        return abstractGeometry;
    }

    public void setAbstractGeometry(final JAXBElement<? extends AbstractGeometryType> abstractGeometry) {
        this.abstractGeometry = abstractGeometry;
    }

    public JAXBElement<PropertyNameType> getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(final JAXBElement<PropertyNameType> propertyName) {
        this.propertyName = propertyName;
    }

    public JAXBElement<EnvelopeType> getEnvelope() {
        return envelope;
    }

    public void setEnvelope(final JAXBElement<EnvelopeType> envelope) {
        this.envelope = envelope;
    }

}
