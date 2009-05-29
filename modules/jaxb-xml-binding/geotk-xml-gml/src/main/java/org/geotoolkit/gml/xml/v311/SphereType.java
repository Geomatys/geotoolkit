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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * A sphere is a gridded surface given as a
 *    family of circles whose positions vary linearly along the
 *    axis of the sphere, and whise radius varies in proportions to
 *    the cosine function of the central angle. The horizontal 
 *    circles resemble lines of constant latitude, and the vertical
 *    arcs resemble lines of constant longitude. 
 *    NOTE! If the control points are sorted in terms of increasing
 *    longitude, and increasing latitude, the upNormal of a sphere
 *    is the outward normal.
 *    EXAMPLE If we take a gridded set of latitudes and longitudes
 *    in degrees,(u,v) such as
 * 
 * 	(-90,-180)  (-90,-90)  (-90,0)  (-90,  90) (-90, 180) 
 * 	(-45,-180)  (-45,-90)  (-45,0)  (-45,  90) (-45, 180) 
 * 	(  0,-180)  (  0,-90)  (  0,0)  (  0,  90) (  0, 180)
 * 	( 45,-180)  ( 45,-90)  ( 45,0)  ( 45, -90) ( 45, 180)
 * 	( 90,-180)  ( 90,-90)  ( 90,0)  ( 90, -90) ( 90, 180)
 *    
 *    And map these points to 3D using the usual equations (where R
 *    is the radius of the required sphere).
 * 
 *     z = R sin u
 *     x = (R cos u)(sin v)
 *     y = (R cos u)(cos v)
 * 
 *    We have a sphere of Radius R, centred at (0,0), as a gridded
 *    surface. Notice that the entire first row and the entire last
 *    row of the control points map to a single point in each 3D
 *    Euclidean space, North and South poles respectively, and that
 *    each horizontal curve closes back on itself forming a 
 *    geometric cycle. This gives us a metrically bounded (of finite
 *    size), topologically unbounded (not having a boundary, a
 *    cycle) surface.
 * 
 * <p>Java class for SphereType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SphereType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml}AbstractGriddedSurfaceType">
 *       &lt;attribute name="horizontalCurveType" type="{http://www.opengis.net/gml}CurveInterpolationType" fixed="circularArc3Points" />
 *       &lt;attribute name="verticalCurveType" type="{http://www.opengis.net/gml}CurveInterpolationType" fixed="circularArc3Points" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SphereType")
public class SphereType
    extends AbstractGriddedSurfaceType
{

    @XmlAttribute
    protected CurveInterpolationType horizontalCurveType;
    @XmlAttribute
    protected CurveInterpolationType verticalCurveType;

    /**
     * Gets the value of the horizontalCurveType property.
     * 
     * @return
     *     possible object is
     *     {@link CurveInterpolationType }
     *     
     */
    public CurveInterpolationType getHorizontalCurveType() {
        if (horizontalCurveType == null) {
            return CurveInterpolationType.CIRCULAR_ARC_3_POINTS;
        } else {
            return horizontalCurveType;
        }
    }

    /**
     * Sets the value of the horizontalCurveType property.
     * 
     * @param value
     *     allowed object is
     *     {@link CurveInterpolationType }
     *     
     */
    public void setHorizontalCurveType(CurveInterpolationType value) {
        this.horizontalCurveType = value;
    }

    /**
     * Gets the value of the verticalCurveType property.
     * 
     * @return
     *     possible object is
     *     {@link CurveInterpolationType }
     *     
     */
    public CurveInterpolationType getVerticalCurveType() {
        if (verticalCurveType == null) {
            return CurveInterpolationType.CIRCULAR_ARC_3_POINTS;
        } else {
            return verticalCurveType;
        }
    }

    /**
     * Sets the value of the verticalCurveType property.
     * 
     * @param value
     *     allowed object is
     *     {@link CurveInterpolationType }
     *     
     */
    public void setVerticalCurveType(CurveInterpolationType value) {
        this.verticalCurveType = value;
    }

}
