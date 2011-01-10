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

package org.geotoolkit.sampling.xml.v100;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v311.MeasureType;
import org.geotoolkit.gml.xml.v311.SurfacePropertyType;


/**
 * A "SamplingSurface" is an identified 2-D spatial feature. 
 * It may be used for various purposes, in particular for observations of cross sections through features.
 * Specialized names for SamplingSurface include CrossSection, Section, Flitch, Swath, Scene, MapHorizon.
 * 
 * <p>Java class for SamplingSurfaceType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SamplingSurfaceType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/sampling/1.0}SpatiallyExtensiveSamplingFeatureType">
 *       &lt;sequence>
 *         &lt;element name="shape" type="{http://www.opengis.net/gml}SurfacePropertyType"/>
 *         &lt;element name="area" type="{http://www.opengis.net/gml}MeasureType" minOccurs="0"/>
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
@XmlType(name = "SamplingSurfaceType", propOrder = {
    "shape",
    "area"
})
public class SamplingSurfaceType extends SpatiallyExtensiveSamplingFeatureType {

    @XmlElement(required = true)
    private SurfacePropertyType shape;
    private MeasureType area;

    /**
     * Gets the value of the shape property.
     * 
     * @return
     *     possible object is
     *     {@link SurfacePropertyType }
     *     
     */
    public SurfacePropertyType getShape() {
        return shape;
    }

    /**
     * Sets the value of the shape property.
     * 
     * @param value
     *     allowed object is
     *     {@link SurfacePropertyType }
     *     
     */
    public void setShape(final SurfacePropertyType value) {
        this.shape = value;
    }

    /**
     * Gets the value of the area property.
     * 
     * @return
     *     possible object is
     *     {@link MeasureType }
     *     
     */
    public MeasureType getArea() {
        return area;
    }

    /**
     * Sets the value of the area property.
     * 
     * @param value
     *     allowed object is
     *     {@link MeasureType }
     *     
     */
    public void setArea(final MeasureType value) {
        this.area = value;
    }

}
