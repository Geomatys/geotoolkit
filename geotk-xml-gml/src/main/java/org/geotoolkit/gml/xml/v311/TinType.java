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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * A tin is a triangulated surface that uses
 *    the Delauny algorithm or a similar algorithm complemented with
 *    consideration of breaklines, stoplines, and maximum length of
 *    triangle sides. These networks satisfy the Delauny's criterion
 *    away from the modifications: Fore each triangle in the
 *    network, the circle passing through its vertices does not
 *    contain, in its interior, the vertex of any other triangle.
 *
 * <p>Java class for TinType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="TinType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml}TriangulatedSurfaceType">
 *       &lt;sequence>
 *         &lt;element name="stopLines" type="{http://www.opengis.net/gml}LineStringSegmentArrayPropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="breakLines" type="{http://www.opengis.net/gml}LineStringSegmentArrayPropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="maxLength" type="{http://www.opengis.net/gml}LengthType"/>
 *         &lt;element name="controlPoint">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;choice>
 *                   &lt;element ref="{http://www.opengis.net/gml}posList"/>
 *                   &lt;group ref="{http://www.opengis.net/gml}geometricPositionGroup" maxOccurs="unbounded" minOccurs="3"/>
 *                 &lt;/choice>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
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
@XmlType(name = "TinType", propOrder = {
    "stopLines",
    "breakLines",
    "maxLength",
    "controlPoint"
})
@XmlRootElement(name = "Tin")
public class TinType extends TriangulatedSurfaceType {

    protected List<LineStringSegmentArrayPropertyType> stopLines;
    protected List<LineStringSegmentArrayPropertyType> breakLines;
    @XmlElement(required = true)
    protected LengthType maxLength;
    @XmlElement(required = true)
    protected TinType.ControlPoint controlPoint;

    /**
     * Gets the value of the stopLines property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link LineStringSegmentArrayPropertyType }
     *
     *
     */
    public List<LineStringSegmentArrayPropertyType> getStopLines() {
        if (stopLines == null) {
            stopLines = new ArrayList<>();
        }
        return this.stopLines;
    }

    /**
     * Gets the value of the breakLines property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link LineStringSegmentArrayPropertyType }
     *
     *
     */
    public List<LineStringSegmentArrayPropertyType> getBreakLines() {
        if (breakLines == null) {
            breakLines = new ArrayList<>();
        }
        return this.breakLines;
    }

    /**
     * Gets the value of the maxLength property.
     *
     * @return
     *     possible object is
     *     {@link LengthType }
     *
     */
    public LengthType getMaxLength() {
        return maxLength;
    }

    /**
     * Sets the value of the maxLength property.
     *
     * @param value
     *     allowed object is
     *     {@link LengthType }
     *
     */
    public void setMaxLength(final LengthType value) {
        this.maxLength = value;
    }

    /**
     * Gets the value of the controlPoint property.
     *
     * @return
     *     possible object is
     *     {@link TinType.ControlPoint }
     *
     */
    public TinType.ControlPoint getControlPoint() {
        return controlPoint;
    }

    /**
     * Sets the value of the controlPoint property.
     *
     * @param value
     *     allowed object is
     *     {@link TinType.ControlPoint }
     *
     */
    public void setControlPoint(final TinType.ControlPoint value) {
        this.controlPoint = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     *
     * <p>The following schema fragment specifies the expected content contained within this class.
     *
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;choice>
     *         &lt;element ref="{http://www.opengis.net/gml}posList"/>
     *         &lt;group ref="{http://www.opengis.net/gml}geometricPositionGroup" maxOccurs="unbounded" minOccurs="3"/>
     *       &lt;/choice>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "posList",
        "geometricPositionGroup"
    })
    public static class ControlPoint {

        protected DirectPositionListType posList;
        @XmlElements({
            @XmlElement(name = "pos", type = DirectPositionType.class),
            @XmlElement(name = "pointProperty", type = PointPropertyType.class)
        })
        protected List<Object> geometricPositionGroup;

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
         * Gets the value of the geometricPositionGroup property.
         *
         * Objects of the following type(s) are allowed in the list
         * {@link DirectPositionType }
         * {@link PointPropertyType }
         *
         *
         */
        public List<Object> getGeometricPositionGroup() {
            if (geometricPositionGroup == null) {
                geometricPositionGroup = new ArrayList<>();
            }
            return this.geometricPositionGroup;
        }

    }

}
