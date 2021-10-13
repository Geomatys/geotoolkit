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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * This variant of the arc requires that the points on the arc have to be computed instead of storing the coordinates directly. The control point is the center point of the arc plus the radius and the bearing at start and end. This represenation can be used only in 2D.
 *
 * <p>Java class for ArcByCenterPointType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ArcByCenterPointType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml}AbstractCurveSegmentType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;choice>
 *             &lt;element ref="{http://www.opengis.net/gml}pos"/>
 *             &lt;element ref="{http://www.opengis.net/gml}pointProperty"/>
 *             &lt;element ref="{http://www.opengis.net/gml}pointRep"/>
 *           &lt;/choice>
 *           &lt;element ref="{http://www.opengis.net/gml}posList"/>
 *           &lt;element ref="{http://www.opengis.net/gml}coordinates"/>
 *         &lt;/choice>
 *         &lt;element name="radius" type="{http://www.opengis.net/gml}LengthType"/>
 *         &lt;element name="startAngle" type="{http://www.opengis.net/gml}AngleType" minOccurs="0"/>
 *         &lt;element name="endAngle" type="{http://www.opengis.net/gml}AngleType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="interpolation" type="{http://www.opengis.net/gml}CurveInterpolationType" fixed="circularArcCenterPointWithRadius" />
 *       &lt;attribute name="numArc" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" fixed="1" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArcByCenterPointType", propOrder = {
    "pos",
    "pointProperty",
    "pointRep",
    "posList",
    "coordinates",
    "radius",
    "startAngle",
    "endAngle"
})
@XmlSeeAlso({
    CircleByCenterPointType.class
})
public class ArcByCenterPointType
    extends AbstractCurveSegmentType
{

    protected DirectPositionType pos;
    protected PointPropertyType pointProperty;
    protected PointPropertyType pointRep;
    protected DirectPositionListType posList;
    protected CoordinatesType coordinates;
    @XmlElement(required = true)
    protected LengthType radius;
    protected AngleType startAngle;
    protected AngleType endAngle;
    @XmlAttribute
    protected CurveInterpolationType interpolation;
    @XmlAttribute(required = true)
    protected Integer numArc;

    /**
     * Gets the value of the pos property.
     *
     * @return
     *     possible object is
     *     {@link DirectPositionType }
     *
     */
    public DirectPositionType getPos() {
        return pos;
    }

    /**
     * Sets the value of the pos property.
     *
     * @param value
     *     allowed object is
     *     {@link DirectPositionType }
     *
     */
    public void setPos(final DirectPositionType value) {
        this.pos = value;
    }

    /**
     * Gets the value of the pointProperty property.
     *
     * @return
     *     possible object is
     *     {@link PointPropertyType }
     *
     */
    public PointPropertyType getPointProperty() {
        return pointProperty;
    }

    /**
     * Sets the value of the pointProperty property.
     *
     * @param value
     *     allowed object is
     *     {@link PointPropertyType }
     *
     */
    public void setPointProperty(final PointPropertyType value) {
        this.pointProperty = value;
    }

    /**
     * Deprecated with GML version 3.1.0. Use "pointProperty" instead. Included for backwards compatibility with GML 3.0.0.
     *
     * @return
     *     possible object is
     *     {@link PointPropertyType }
     *
     */
    public PointPropertyType getPointRep() {
        return pointRep;
    }

    /**
     * Deprecated with GML version 3.1.0. Use "pointProperty" instead. Included for backwards compatibility with GML 3.0.0.
     *
     * @param value
     *     allowed object is
     *     {@link PointPropertyType }
     *
     */
    public void setPointRep(final PointPropertyType value) {
        this.pointRep = value;
    }

    /**
     * Gets the value of the posList property.
     *
     * @return
     *     possible object is
     *     {@link DirectPositionListType }
     *
     */
    public DirectPositionListType getPosList() {
        return posList;
    }

    /**
     * Sets the value of the posList property.
     *
     * @param value
     *     allowed object is
     *     {@link DirectPositionListType }
     *
     */
    public void setPosList(final DirectPositionListType value) {
        this.posList = value;
    }

    /**
     * Deprecated with GML version 3.1.0. Use "posList" instead.
     *
     * @return
     *     possible object is
     *     {@link CoordinatesType }
     *
     */
    public CoordinatesType getCoordinates() {
        return coordinates;
    }

    /**
     * Deprecated with GML version 3.1.0. Use "posList" instead.
     *
     * @param value
     *     allowed object is
     *     {@link CoordinatesType }
     *
     */
    public void setCoordinates(final CoordinatesType value) {
        this.coordinates = value;
    }

    /**
     * Gets the value of the radius property.
     *
     * @return
     *     possible object is
     *     {@link LengthType }
     *
     */
    public LengthType getRadius() {
        return radius;
    }

    /**
     * Sets the value of the radius property.
     *
     * @param value
     *     allowed object is
     *     {@link LengthType }
     *
     */
    public void setRadius(final LengthType value) {
        this.radius = value;
    }

    /**
     * Gets the value of the startAngle property.
     *
     * @return
     *     possible object is
     *     {@link AngleType }
     *
     */
    public AngleType getStartAngle() {
        return startAngle;
    }

    /**
     * Sets the value of the startAngle property.
     *
     * @param value
     *     allowed object is
     *     {@link AngleType }
     *
     */
    public void setStartAngle(final AngleType value) {
        this.startAngle = value;
    }

    /**
     * Gets the value of the endAngle property.
     *
     * @return
     *     possible object is
     *     {@link AngleType }
     *
     */
    public AngleType getEndAngle() {
        return endAngle;
    }

    /**
     * Sets the value of the endAngle property.
     *
     * @param value
     *     allowed object is
     *     {@link AngleType }
     *
     */
    public void setEndAngle(final AngleType value) {
        this.endAngle = value;
    }

    /**
     * Gets the value of the interpolation property.
     *
     * @return
     *     possible object is
     *     {@link CurveInterpolationType }
     *
     */
    public CurveInterpolationType getInterpolation() {
        if (interpolation == null) {
            return CurveInterpolationType.CIRCULAR_ARC_CENTER_POINT_WITH_RADIUS;
        } else {
            return interpolation;
        }
    }

    /**
     * Sets the value of the interpolation property.
     *
     * @param value
     *     allowed object is
     *     {@link CurveInterpolationType }
     *
     */
    public void setInterpolation(final CurveInterpolationType value) {
        this.interpolation = value;
    }

    /**
     * Gets the value of the numArc property.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public Integer getNumArc() {
        if (numArc == null) {
            return 1;
        } else {
            return numArc;
        }
    }

    /**
     * Sets the value of the numArc property.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setNumArc(final Integer value) {
        this.numArc = value;
    }

}
