/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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

package org.geotoolkit.ols.xml.v121;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for OverlayType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OverlayType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element ref="{http://www.opengis.net/xls}POI"/>
 *           &lt;element ref="{http://www.opengis.net/xls}RouteGeometry"/>
 *           &lt;element ref="{http://www.opengis.net/xls}Position"/>
 *           &lt;element ref="{http://www.opengis.net/xls}Map"/>
 *         &lt;/choice>
 *         &lt;element name="Style" type="{http://www.opengis.net/xls}StyleType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="zorder" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OverlayType", propOrder = {
    "poi",
    "routeGeometry",
    "position",
    "map",
    "style"
})
public class OverlayType {

    @XmlElement(name = "POI")
    private PointOfInterestType poi;
    @XmlElement(name = "RouteGeometry")
    private RouteGeometryType routeGeometry;
    @XmlElement(name = "Position")
    private PositionType position;
    @XmlElement(name = "Map")
    private MapType map;
    @XmlElement(name = "Style")
    private StyleType style;
    @XmlAttribute
    private Integer zorder;

    /**
     * Gets the value of the poi property.
     * 
     * @return
     *     possible object is
     *     {@link PointOfInterestType }
     *     
     */
    public PointOfInterestType getPOI() {
        return poi;
    }

    /**
     * Sets the value of the poi property.
     * 
     * @param value
     *     allowed object is
     *     {@link PointOfInterestType }
     *     
     */
    public void setPOI(PointOfInterestType value) {
        this.poi = value;
    }

    /**
     * Gets the value of the routeGeometry property.
     * 
     * @return
     *     possible object is
     *     {@link RouteGeometryType }
     *     
     */
    public RouteGeometryType getRouteGeometry() {
        return routeGeometry;
    }

    /**
     * Sets the value of the routeGeometry property.
     * 
     * @param value
     *     allowed object is
     *     {@link RouteGeometryType }
     *     
     */
    public void setRouteGeometry(RouteGeometryType value) {
        this.routeGeometry = value;
    }

    /**
     * Gets the value of the position property.
     * 
     * @return
     *     possible object is
     *     {@link PositionType }
     *     
     */
    public PositionType getPosition() {
        return position;
    }

    /**
     * Sets the value of the position property.
     * 
     * @param value
     *     allowed object is
     *     {@link PositionType }
     *     
     */
    public void setPosition(PositionType value) {
        this.position = value;
    }

    /**
     * Gets the value of the map property.
     * 
     * @return
     *     possible object is
     *     {@link MapType }
     *     
     */
    public MapType getMap() {
        return map;
    }

    /**
     * Sets the value of the map property.
     * 
     * @param value
     *     allowed object is
     *     {@link MapType }
     *     
     */
    public void setMap(MapType value) {
        this.map = value;
    }

    /**
     * Gets the value of the style property.
     * 
     * @return
     *     possible object is
     *     {@link StyleType }
     *     
     */
    public StyleType getStyle() {
        return style;
    }

    /**
     * Sets the value of the style property.
     * 
     * @param value
     *     allowed object is
     *     {@link StyleType }
     *     
     */
    public void setStyle(StyleType value) {
        this.style = value;
    }

    /**
     * Gets the value of the zorder property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getZorder() {
        return zorder;
    }

    /**
     * Sets the value of the zorder property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setZorder(Integer value) {
        this.zorder = value;
    }

}
