/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2008, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
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
import org.geotoolkit.gml.xml.v311.EnvelopeEntry;
import org.geotoolkit.gml.xml.v311.LineStringType;
import org.geotoolkit.gml.xml.v311.PointType;
import org.geotoolkit.gml.xml.v311.PolygonType;
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
    private JAXBElement<EnvelopeEntry> envelope;
    
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
    public BinarySpatialOpType(String propertyName, AbstractGeometryType geometry) {
        
        this.propertyName = ogcFactory.createPropertyName(new PropertyNameType(propertyName));

        if (geometry instanceof PointType) {
            abstractGeometry = gmlFactory.createPoint((PointType)geometry);
        
        } else if (geometry instanceof LineStringType) {
            abstractGeometry = gmlFactory.createLineString((LineStringType)geometry);
        
        } else if (geometry instanceof PolygonType) {
            abstractGeometry = gmlFactory.createPolygon((PolygonType)geometry);
        
        } else {
            abstractGeometry = gmlFactory.createGeometry(geometry);
        }
        
    }
    
    /**
     * Build a new Binary spatial operator
     */
    public BinarySpatialOpType(PropertyNameType propertyName, Object geometry) {
        
        this.propertyName = ogcFactory.createPropertyName(propertyName);
        
        
        if (geometry instanceof PointType) {
            abstractGeometry = gmlFactory.createPoint((PointType)geometry);
            
        } else if (geometry instanceof PolygonType) {
            abstractGeometry = gmlFactory.createPolygon((PolygonType)geometry);
        
        } else if (geometry instanceof LineStringType) {
            abstractGeometry = gmlFactory.createLineString((LineStringType)geometry);
            
        } else if (geometry instanceof EnvelopeEntry) {
            this.envelope = gmlFactory.createEnvelope((EnvelopeEntry)geometry);
        
        } else if (geometry instanceof AbstractGeometryType) {
            abstractGeometry = gmlFactory.createGeometry((AbstractGeometryType)geometry);
        }
        
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
    
    public boolean evaluate(Object object) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object accept(FilterVisitor visitor, Object extraData) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public JAXBElement<? extends AbstractGeometryType> getAbstractGeometry() {
        return abstractGeometry;
    }

    public void setAbstractGeometry(JAXBElement<? extends AbstractGeometryType> abstractGeometry) {
        this.abstractGeometry = abstractGeometry;
    }

    public JAXBElement<PropertyNameType> getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(JAXBElement<PropertyNameType> propertyName) {
        this.propertyName = propertyName;
    }

    public JAXBElement<EnvelopeEntry> getEnvelope() {
        return envelope;
    }

    public void setEnvelope(JAXBElement<EnvelopeEntry> envelope) {
        this.envelope = envelope;
    }

}
