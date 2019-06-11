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
package org.geotoolkit.ogc.xml.v100;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v212.AbstractGeometryType;
import org.opengis.filter.FilterVisitor;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.spatial.DistanceBufferOperator;


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
 *         &lt;element ref="{http://www.opengis.net/gml}_Geometry"/>
 *         &lt;element name="Distance" type="{http://www.opengis.net/ogc}DistanceType"/>
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
@XmlType(name = "DistanceBufferType", propOrder = {
    "propertyName",
    "geometry",
    "distance"
})
public abstract class DistanceBufferType extends SpatialOpsType implements DistanceBufferOperator {

    @XmlElement(name = "PropertyName", required = true)
    private PropertyNameType propertyName;
    @XmlElementRef(name = "AbstractGeometry", namespace = "http://www.opengis.net/gml", type = JAXBElement.class)
    private JAXBElement<? extends AbstractGeometryType> geometry;
    @XmlElement(name = "Distance", required = true)
    private DistanceType distance;

    public DistanceBufferType() {

    }


    /**
     * build a new Distance buffer
     */
    public DistanceBufferType(final String propertyName, final Object geometry, final double distance, final String unit) {
        if (propertyName != null) {
            this.propertyName = new PropertyNameType(propertyName);
        }
        this.distance       = new DistanceType(String.valueOf(distance), unit);
        if (geometry instanceof AbstractGeometryType) {
            this.geometry = ((AbstractGeometryType)geometry).getXmlElement();
        }
    }

    public DistanceBufferType(final DistanceBufferType that) {
        if (that != null) {
            if (that.propertyName != null) {
                this.propertyName = new PropertyNameType(that.propertyName);
            }
            if (that.geometry != null) {
                final AbstractGeometryType geom = that.geometry.getValue().getClone();
                this.geometry = geom.getXmlElement();
            }
            if (that.distance != null) {
                this.distance = new DistanceType(that.distance.getContent(), that.distance.getUnits());
            }
        }
    }
    /**
     * Gets the value of the propertyName property.
     *
     */
    public PropertyNameType getPropertyName() {
        return propertyName;
    }

    /**
     * Sets the value of the propertyName property.
     *
     */
    public void setPropertyName(final PropertyNameType value) {
        this.propertyName = value;
    }

    /**
     * Gets the value of the geometry property.
     *
     */
    public JAXBElement<? extends AbstractGeometryType> getGeometry() {
        return geometry;
    }

    /**
     * Sets the value of the geometry property.
     *
     */
    public void setGeometry(final JAXBElement<? extends AbstractGeometryType> value) {
        this.geometry = ((JAXBElement<? extends AbstractGeometryType> ) value);
    }

    @Override
    public double getDistance() {
        if (distance != null && distance.getContent() != null) {
            return Double.parseDouble(distance.getContent());
        }
        return 0.0;
    }

    @Override
    public String getDistanceUnits() {
        if (distance != null) {
            return distance.getUnits();
        }
        return null;
    }

    /**
     * Gets the value of the distance property.
     *
     */
    public DistanceType getDistanceType() {
        return distance;
    }

    /**
     * Sets the value of the distance property.
     *
     */
    public void setDistance(final DistanceType value) {
        this.distance = value;
    }

    @Override
    public Expression getExpression1() {
        return propertyName;
    }

    @Override
    public Expression getExpression2() {
        if (geometry != null) {
            return (Expression) geometry.getValue();
        }
        return null;
    }

    @Override
    public boolean evaluate(final Object object) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object accept(final FilterVisitor visitor, final Object extraData) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public SpatialOpsType getClone() {
        throw new UnsupportedOperationException("Must be overriden by sub-class");
    }
}
