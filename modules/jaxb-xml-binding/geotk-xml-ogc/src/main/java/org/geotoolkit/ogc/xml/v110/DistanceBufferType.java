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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v311.AbstractGeometryType;
import org.opengis.filter.FilterVisitor;
import org.opengis.filter.expression.Expression;
import org.geotoolkit.gml.xml.v311.ObjectFactory;
import org.geotoolkit.gml.xml.v311.PointType;
import org.geotoolkit.gml.xml.v311.PolygonType;


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
    
    /**
     * build a new Distance buffer
     */
    public DistanceBufferType(String propertyName, AbstractGeometryType geometry, double distance, String unit) {
        this.propertyName = new PropertyNameType(propertyName);
        this.distance     = new DistanceType(distance, unit);
        
        //TODO rajouter les autre type possible
        if (geometry instanceof PointType) {
            this.abstractGeometry = factory.createPoint((PointType)geometry);
        } else if (geometry instanceof PolygonType) {
            this.abstractGeometry = factory.createPolygon((PolygonType)geometry);
        } else {
            this.abstractGeometry = factory.createGeometry(geometry);
        }
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

    public boolean evaluate(Object object) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object accept(FilterVisitor visitor, Object extraData) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
