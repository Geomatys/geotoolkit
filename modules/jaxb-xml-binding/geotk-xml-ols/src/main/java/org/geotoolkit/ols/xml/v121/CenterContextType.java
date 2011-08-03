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
import org.geotoolkit.gml.xml.v311.PointType;

/**
 * <p>Java class for CenterContextType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CenterContextType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CenterPoint" type="{http://www.opengis.net/gml}PointType"/>
 *         &lt;choice>
 *           &lt;sequence>
 *             &lt;element name="DisplayScale" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *             &lt;element name="DPI" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *           &lt;/sequence>
 *           &lt;sequence>
 *             &lt;element name="Radius" type="{http://www.opengis.net/xls}RadiusType"/>
 *           &lt;/sequence>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="azimuth" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *       &lt;attribute name="SRS" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CenterContextType", propOrder = {
    "centerPoint",
    "displayScale",
    "dpi",
    "radius"
})
public class CenterContextType {

    @XmlElement(name = "CenterPoint", required = true)
    private PointType centerPoint;
    @XmlElement(name = "DisplayScale")
    private Integer displayScale;
    @XmlElement(name = "DPI")
    private Integer dpi;
    @XmlElement(name = "Radius")
    private RadiusType radius;
    @XmlAttribute
    private Integer azimuth;
    @XmlAttribute(name = "SRS", required = true)
    private String srs;

    /**
     * Gets the value of the centerPoint property.
     * 
     * @return
     *     possible object is
     *     {@link PointType }
     *     
     */
    public PointType getCenterPoint() {
        return centerPoint;
    }

    /**
     * Sets the value of the centerPoint property.
     * 
     * @param value
     *     allowed object is
     *     {@link PointType }
     *     
     */
    public void setCenterPoint(PointType value) {
        this.centerPoint = value;
    }

    /**
     * Gets the value of the displayScale property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getDisplayScale() {
        return displayScale;
    }

    /**
     * Sets the value of the displayScale property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setDisplayScale(Integer value) {
        this.displayScale = value;
    }

    /**
     * Gets the value of the dpi property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getDPI() {
        return dpi;
    }

    /**
     * Sets the value of the dpi property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setDPI(Integer value) {
        this.dpi = value;
    }

    /**
     * Gets the value of the radius property.
     * 
     * @return
     *     possible object is
     *     {@link RadiusType }
     *     
     */
    public RadiusType getRadius() {
        return radius;
    }

    /**
     * Sets the value of the radius property.
     * 
     * @param value
     *     allowed object is
     *     {@link RadiusType }
     *     
     */
    public void setRadius(RadiusType value) {
        this.radius = value;
    }

    /**
     * Gets the value of the azimuth property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getAzimuth() {
        return azimuth;
    }

    /**
     * Sets the value of the azimuth property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setAzimuth(Integer value) {
        this.azimuth = value;
    }

    /**
     * Gets the value of the srs property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSRS() {
        return srs;
    }

    /**
     * Sets the value of the srs property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSRS(String value) {
        this.srs = value;
    }

}
