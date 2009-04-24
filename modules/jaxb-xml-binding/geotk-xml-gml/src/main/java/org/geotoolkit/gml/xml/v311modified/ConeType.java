/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2009, Geomatys
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


package org.geotoolkit.gml.xml.v311modified;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * A cone is a gridded surface given as a
 *    family of conic sections whose control points vary linearly.
 *    NOTE! A 5-point ellipse with all defining positions identical
 *    is a point. Thus, a truncated elliptical cone can be given as a
 *    2x5 set of control points
 *    ((P1, P1, P1, P1, P1), (P2, P3, P4, P5, P6)). P1 is the apex 
 *    of the cone. P2, P3,P4, P5 and P6 are any five distinct points
 *    around the base ellipse of the cone. If the horizontal curves
 *    are circles as opposed to ellipses, the a circular cone can
 *    be constructed using ((P1, P1, P1),(P2, P3, P4)). The apex most     
 *    not coinside with the other plane.
 * 
 * <p>Java class for ConeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ConeType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml}AbstractGriddedSurfaceType">
 *       &lt;attribute name="horizontalCurveType" type="{http://www.opengis.net/gml}CurveInterpolationType" fixed="circularArc3Points" />
 *       &lt;attribute name="verticalCurveType" type="{http://www.opengis.net/gml}CurveInterpolationType" fixed="linear" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ConeType")
public class ConeType
    extends AbstractGriddedSurfaceType
{

    @XmlAttribute
    private CurveInterpolationType horizontalCurveType;
    @XmlAttribute
    private CurveInterpolationType verticalCurveType;

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
            return CurveInterpolationType.LINEAR;
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
