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
package org.geotoolkit.se.xml.v110;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PointPlacementType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PointPlacementType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/se}AnchorPoint" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/se}Displacement" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/se}Rotation" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PointPlacementType", propOrder = {
    "anchorPoint",
    "displacement",
    "rotation"
})
public class PointPlacementType {

    @XmlElement(name = "AnchorPoint")
    protected AnchorPointType anchorPoint;
    @XmlElement(name = "Displacement")
    protected DisplacementType displacement;
    @XmlElement(name = "Rotation")
    protected ParameterValueType rotation;

    /**
     * Gets the value of the anchorPoint property.
     * 
     * @return
     *     possible object is
     *     {@link AnchorPointType }
     *     
     */
    public AnchorPointType getAnchorPoint() {
        return anchorPoint;
    }

    /**
     * Sets the value of the anchorPoint property.
     * 
     * @param value
     *     allowed object is
     *     {@link AnchorPointType }
     *     
     */
    public void setAnchorPoint(final AnchorPointType value) {
        this.anchorPoint = value;
    }

    /**
     * Gets the value of the displacement property.
     * 
     * @return
     *     possible object is
     *     {@link DisplacementType }
     *     
     */
    public DisplacementType getDisplacement() {
        return displacement;
    }

    /**
     * Sets the value of the displacement property.
     * 
     * @param value
     *     allowed object is
     *     {@link DisplacementType }
     *     
     */
    public void setDisplacement(final DisplacementType value) {
        this.displacement = value;
    }

    /**
     * Gets the value of the rotation property.
     * 
     * @return
     *     possible object is
     *     {@link ParameterValueType }
     *     
     */
    public ParameterValueType getRotation() {
        return rotation;
    }

    /**
     * Sets the value of the rotation property.
     * 
     * @param value
     *     allowed object is
     *     {@link ParameterValueType }
     *     
     */
    public void setRotation(final ParameterValueType value) {
        this.rotation = value;
    }

}
