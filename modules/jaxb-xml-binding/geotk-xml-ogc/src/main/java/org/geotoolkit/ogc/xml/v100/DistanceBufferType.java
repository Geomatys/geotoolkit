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
public abstract class DistanceBufferType extends SpatialOpsType {

    @XmlElement(name = "PropertyName", required = true)
    private PropertyNameType propertyName;
    @XmlElementRef(name = "AbstractGeometry", namespace = "http://www.opengis.net/gml", type = JAXBElement.class)
    private JAXBElement<? extends AbstractGeometryType> geometry;
    @XmlElement(name = "Distance", required = true)
    private DistanceType distance;

    public DistanceBufferType() {

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

    /**
     * Gets the value of the distance property.
     *
     */
    public DistanceType getDistance() {
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
    public SpatialOpsType getClone() {
        throw new UnsupportedOperationException("Must be overriden by sub-class");
    }
}
