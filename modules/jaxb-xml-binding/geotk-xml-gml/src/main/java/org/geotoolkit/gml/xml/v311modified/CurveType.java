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
package org.geotoolkit.gml.xml.v311modified;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Curve is a 1-dimensional primitive. Curves are continuous, connected, and have a measurable length in terms of the coordinate system. 
 * 				A curve is composed of one or more curve segments. Each curve segment within a curve may be defined using a different interpolation method. The curve segments are connected to one another, with the end point of each segment except the last being the start point of the next segment in the segment list.
 * 				The orientation of the curve is positive.
 * 
 * <p>Java class for CurveType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CurveType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml}AbstractCurveType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/gml}segments"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CurveType", propOrder = {
    "segments"
})
public class CurveType extends AbstractCurveType {

    @XmlElement(required = true)
    private CurveSegmentArrayPropertyType segments;

    /**
     * This element encapsulates the segments of the curve.
     * 
     * @return
     *     possible object is
     *     {@link CurveSegmentArrayPropertyType }
     *     
     */
    public CurveSegmentArrayPropertyType getSegments() {
        return segments;
    }

    /**
     * This element encapsulates the segments of the curve.
     * 
     * @param value
     *     allowed object is
     *     {@link CurveSegmentArrayPropertyType }
     *     
     */
    public void setSegments(CurveSegmentArrayPropertyType value) {
        this.segments = value;
    }

}
