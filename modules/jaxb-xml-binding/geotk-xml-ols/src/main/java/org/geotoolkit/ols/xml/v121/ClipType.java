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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v311.CircleByCenterPointType;
import org.geotoolkit.gml.xml.v311.PolygonType;


/**
 * <p>Java class for ClipType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ClipType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element ref="{http://www.opengis.net/gml}Polygon"/>
 *         &lt;element ref="{http://www.opengis.net/gml}CircleByCenterPoint"/>
 *         &lt;element name="LineCorridor" type="{http://www.opengis.net/xls}LineCorridorType"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ClipType", propOrder = {
    "polygon",
    "circleByCenterPoint",
    "lineCorridor"
})
public class ClipType {

    @XmlElement(name = "Polygon", namespace = "http://www.opengis.net/gml")
    private PolygonType polygon;
    @XmlElement(name = "CircleByCenterPoint", namespace = "http://www.opengis.net/gml")
    private CircleByCenterPointType circleByCenterPoint;
    @XmlElement(name = "LineCorridor")
    private LineCorridorType lineCorridor;

    /**
     * Clips the portrayed map with a polygon
     * 
     * @return
     *     possible object is
     *     {@link PolygonType }
     *     
     */
    public PolygonType getPolygon() {
        return polygon;
    }

    /**
     * Sets the value of the polygon property.
     * 
     * @param value
     *     allowed object is
     *     {@link PolygonType }
     *     
     */
    public void setPolygon(PolygonType value) {
        this.polygon = value;
    }

    /**
     * Clips the portrayed map with a circle
     * 
     * @return
     *     possible object is
     *     {@link CircleByCenterPointType }
     *     
     */
    public CircleByCenterPointType getCircleByCenterPoint() {
        return circleByCenterPoint;
    }

    /**
     * Sets the value of the circleByCenterPoint property.
     * 
     * @param value
     *     allowed object is
     *     {@link CircleByCenterPointType }
     *     
     */
    public void setCircleByCenterPoint(CircleByCenterPointType value) {
        this.circleByCenterPoint = value;
    }

    /**
     * Gets the value of the lineCorridor property.
     * 
     * @return
     *     possible object is
     *     {@link LineCorridorType }
     *     
     */
    public LineCorridorType getLineCorridor() {
        return lineCorridor;
    }

    /**
     * Sets the value of the lineCorridor property.
     * 
     * @param value
     *     allowed object is
     *     {@link LineCorridorType }
     *     
     */
    public void setLineCorridor(LineCorridorType value) {
        this.lineCorridor = value;
    }

}
