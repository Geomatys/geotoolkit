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
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v311.EnvelopeType;


/**
 * Defines the request parameters for route geometry.
 * 
 * <p>Java class for RouteGeometryRequestType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RouteGeometryRequestType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="BoundingBox" type="{http://www.opengis.net/gml}EnvelopeType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="scale" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" default="1" />
 *       &lt;attribute name="provideStartingPortion" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="maxPoints" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" default="100" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RouteGeometryRequestType", propOrder = {
    "boundingBox"
})
public class RouteGeometryRequestType {

    @XmlElement(name = "BoundingBox")
    private EnvelopeType boundingBox;
    @XmlAttribute
    @XmlSchemaType(name = "positiveInteger")
    private Integer scale;
    @XmlAttribute
    private Boolean provideStartingPortion;
    @XmlAttribute
    @XmlSchemaType(name = "positiveInteger")
    private Integer maxPoints;

    /**
     * Gets the value of the boundingBox property.
     * 
     * @return
     *     possible object is
     *     {@link EnvelopeType }
     *     
     */
    public EnvelopeType getBoundingBox() {
        return boundingBox;
    }

    /**
     * Sets the value of the boundingBox property.
     * 
     * @param value
     *     allowed object is
     *     {@link EnvelopeType }
     *     
     */
    public void setBoundingBox(EnvelopeType value) {
        this.boundingBox = value;
    }

    /**
     * Gets the value of the scale property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getScale() {
        if (scale == null) {
            return 1;
        } else {
            return scale;
        }
    }

    /**
     * Sets the value of the scale property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setScale(Integer value) {
        this.scale = value;
    }

    /**
     * Gets the value of the provideStartingPortion property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isProvideStartingPortion() {
        if (provideStartingPortion == null) {
            return false;
        } else {
            return provideStartingPortion;
        }
    }

    /**
     * Sets the value of the provideStartingPortion property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setProvideStartingPortion(Boolean value) {
        this.provideStartingPortion = value;
    }

    /**
     * Gets the value of the maxPoints property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMaxPoints() {
        if (maxPoints == null) {
            return new Integer("100");
        } else {
            return maxPoints;
        }
    }

    /**
     * Sets the value of the maxPoints property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMaxPoints(Integer value) {
        this.maxPoints = value;
    }

}
