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
import org.geotoolkit.gml.xml.v311.CurvePropertyType;
import org.geotoolkit.gml.xml.v311.MeasureType;


/**
 * A "SamplingCurve" is an identified 1-D spatial feature. 
 * It may be revisited for various purposes, in particular to retrieve multiple specimens or make repeated or complementary observations.
 * Specialized names for SamplingCurve include Sounding, ObservationWell, FlightLine, Transect.
 * 
 * <p>Java class for SamplingCurveType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SamplingCurveType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/sampling/1.0}SpatiallyExtensiveSamplingFeatureType">
 *       &lt;sequence>
 *         &lt;element name="shape" type="{http://www.opengis.net/gml}CurvePropertyType"/>
 *         &lt;element name="length" type="{http://www.opengis.net/gml}MeasureType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SamplingCurveType", propOrder = {
    "shape",
    "length"
})
public class SamplingCurveType extends SpatiallyExtensiveSamplingFeatureType {

    @XmlElement(required = true)
    private CurvePropertyType shape;
    private MeasureType length;

    /**
     * Gets the value of the shape property.
     * 
     * @return
     *     possible object is
     *     {@link CurvePropertyType }
     *     
     */
    public CurvePropertyType getShape() {
        return shape;
    }

    /**
     * Sets the value of the shape property.
     * 
     * @param value
     *     allowed object is
     *     {@link CurvePropertyType }
     *     
     */
    public void setShape(CurvePropertyType value) {
        this.shape = value;
    }

    /**
     * Gets the value of the length property.
     * 
     * @return
     *     possible object is
     *     {@link MeasureType }
     *     
     */
    public MeasureType getLength() {
        return length;
    }

    /**
     * Sets the value of the length property.
     * 
     * @param value
     *     allowed object is
     *     {@link MeasureType }
     *     
     */
    public void setLength(MeasureType value) {
        this.length = value;
    }

}
