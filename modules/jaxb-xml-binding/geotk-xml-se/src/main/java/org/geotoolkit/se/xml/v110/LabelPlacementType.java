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
 * <p>Java class for LabelPlacementType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LabelPlacementType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element ref="{http://www.opengis.net/se}PointPlacement"/>
 *         &lt;element ref="{http://www.opengis.net/se}LinePlacement"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LabelPlacementType", propOrder = {
    "pointPlacement",
    "linePlacement"
})
public class LabelPlacementType {

    @XmlElement(name = "PointPlacement")
    protected PointPlacementType pointPlacement;
    @XmlElement(name = "LinePlacement")
    protected LinePlacementType linePlacement;

    /**
     * Gets the value of the pointPlacement property.
     * 
     * @return
     *     possible object is
     *     {@link PointPlacementType }
     *     
     */
    public PointPlacementType getPointPlacement() {
        return pointPlacement;
    }

    /**
     * Sets the value of the pointPlacement property.
     * 
     * @param value
     *     allowed object is
     *     {@link PointPlacementType }
     *     
     */
    public void setPointPlacement(final PointPlacementType value) {
        this.pointPlacement = value;
    }

    /**
     * Gets the value of the linePlacement property.
     * 
     * @return
     *     possible object is
     *     {@link LinePlacementType }
     *     
     */
    public LinePlacementType getLinePlacement() {
        return linePlacement;
    }

    /**
     * Sets the value of the linePlacement property.
     * 
     * @param value
     *     allowed object is
     *     {@link LinePlacementType }
     *     
     */
    public void setLinePlacement(final LinePlacementType value) {
        this.linePlacement = value;
    }

}
