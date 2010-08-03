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

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;


/**
 * Represents a rectangle as a surface with an outer boundary consisting of a linear ring. Note that this is a polygon (subtype) with no inner boundaries. The number of points in the linear ring must be five.
 * 
 * <p>Java class for RectangleType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RectangleType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml}AbstractSurfacePatchType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/gml}exterior"/>
 *       &lt;/sequence>
 *       &lt;attribute name="interpolation" type="{http://www.opengis.net/gml}SurfaceInterpolationType" fixed="planar" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RectangleType", propOrder = {
    "exterior"
})
public class RectangleType extends AbstractSurfacePatchType {

    @XmlElementRef(name = "exterior", namespace = "http://www.opengis.net/gml", type = JAXBElement.class)
    private JAXBElement<AbstractRingPropertyType> exterior;
    @XmlAttribute
    private SurfaceInterpolationType interpolation;

    /**
     * Constraint: The Ring shall be a LinearRing and must form a rectangle; the first and the last position must be co-incident.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link AbstractRingPropertyType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractRingPropertyType }{@code >}
     *     
     */
    public JAXBElement<AbstractRingPropertyType> getJbExterior() {
        return exterior;
    }

    /**
     * Constraint: The Ring shall be a LinearRing and must form a rectangle; the first and the last position must be co-incident.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link AbstractRingPropertyType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractRingPropertyType }{@code >}
     *     
     */
    public void setJbExterior(JAXBElement<AbstractRingPropertyType> value) {
        this.exterior = ((JAXBElement<AbstractRingPropertyType> ) value);
    }

    /**
     * Gets the value of the exterior property.
     *
     * @return
     *     possible object is
     *     {@code <}{@link AbstractRingPropertyType }{@code >}
     *     {@code <}{@link AbstractRingPropertyType }{@code >}
     *
     */
    public AbstractRingPropertyType getExterior() {
        if (exterior != null) {
            return exterior.getValue();
        }
        return null;
    }

    /**
     * Sets the value of the exterior property.
     *
     * @param value
     *     allowed object is
     *     {@code <}{@link AbstractRingPropertyType }{@code >}
     *     {@code <}{@link AbstractRingPropertyType }{@code >}
     *
     */
    public void setExterior(AbstractRingPropertyType value) {
        ObjectFactory factory = new ObjectFactory();
        this.exterior =  factory.createExterior(value);
    }
    
    /**
     * Gets the value of the interpolation property.
     * 
     * @return
     *     possible object is
     *     {@link SurfaceInterpolationType }
     *     
     */
    public SurfaceInterpolationType getInterpolation() {
        if (interpolation == null) {
            return SurfaceInterpolationType.PLANAR;
        } else {
            return interpolation;
        }
    }

    /**
     * Sets the value of the interpolation property.
     * 
     * @param value
     *     allowed object is
     *     {@link SurfaceInterpolationType }
     *     
     */
    public void setInterpolation(SurfaceInterpolationType value) {
        this.interpolation = value;
    }

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof RectangleType) {
            final RectangleType that = (RectangleType) object;

            return Utilities.equals(this.getExterior(),    that.getExterior()) &&
                   Utilities.equals(this.interpolation,    that.interpolation);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + (this.getExterior() != null ? this.getExterior().hashCode() : 0);
        hash = 37 * hash + (this.interpolation != null ? this.interpolation.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[RectangleType]").append("\n");
        if (interpolation != null) {
            sb.append("interpolation:").append(interpolation).append('\n');
        }
        if (exterior != null) {
            sb.append("exterior:").append(exterior.getValue()).append('\n');
        }
        return sb.toString();
    }
}
