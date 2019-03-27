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
import org.geotoolkit.gml.xml.v212.BoxType;


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
 *         &lt;element ref="{http://www.opengis.net/ogc}PropertyName"/>
 *         &lt;choice>
 *           &lt;element ref="{http://www.opengis.net/gml}AbstractGeometry"/>
 *           &lt;element ref="{http://www.opengis.net/gml}Box"/>
 *         &lt;/choice>
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
@XmlType(name = "BinarySpatialOpType", propOrder = {
    "propertyName",
    "geometry",
    "box"
})
public abstract class BinarySpatialOpType extends SpatialOpsType {

    @XmlElement(name = "PropertyName", required = true)
    private PropertyNameType propertyName;
    @XmlElementRef(name = "AbstractGeometry", namespace = "http://www.opengis.net/gml", type = JAXBElement.class)
    private JAXBElement<? extends AbstractGeometryType> geometry;
    @XmlElement(name = "Box", namespace = "http://www.opengis.net/gml")
    private BoxType box;

    public BinarySpatialOpType() {

    }

    public BinarySpatialOpType(final BinarySpatialOpType that) {
        if (that != null) {
            if (that.box != null) {
                this.box = new BoxType(that.box);
            }
            if (that.propertyName != null) {
                this.propertyName = new PropertyNameType(that.propertyName);
            }
            if (that.geometry != null) {
                final AbstractGeometryType geom = that.geometry.getValue().getClone();
                this.geometry = geom.getXmlElement();
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
     * Gets the value of the box property.
     *
     */
    public BoxType getBox() {
        return box;
    }

    /**
     * Sets the value of the box property.
     *
     */
    public void setBox(final BoxType value) {
        this.box = value;
    }

    @Override
    public SpatialOpsType getClone() {
        throw new UnsupportedOperationException("Must be overriden by sub-class");
    }
}
