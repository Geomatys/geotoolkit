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
package org.geotoolkit.sld.xml.v100;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


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
 *         &lt;element ref="{http://www.opengis.net/sld}PointPlacement"/>
 *         &lt;element ref="{http://www.opengis.net/sld}LinePlacement"/>
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
    "pointPlacement",
    "linePlacement"
})
@XmlRootElement(name = "LabelPlacement")
public class LabelPlacement {

    @XmlElement(name = "PointPlacement")
    protected PointPlacement pointPlacement;
    @XmlElement(name = "LinePlacement")
    protected LinePlacement linePlacement;

    /**
     * Gets the value of the pointPlacement property.
     * 
     * @return
     *     possible object is
     *     {@link PointPlacement }
     *     
     */
    public PointPlacement getPointPlacement() {
        return pointPlacement;
    }

    /**
     * Sets the value of the pointPlacement property.
     * 
     * @param value
     *     allowed object is
     *     {@link PointPlacement }
     *     
     */
    public void setPointPlacement(PointPlacement value) {
        this.pointPlacement = value;
    }

    /**
     * Gets the value of the linePlacement property.
     * 
     * @return
     *     possible object is
     *     {@link LinePlacement }
     *     
     */
    public LinePlacement getLinePlacement() {
        return linePlacement;
    }

    /**
     * Sets the value of the linePlacement property.
     * 
     * @param value
     *     allowed object is
     *     {@link LinePlacement }
     *     
     */
    public void setLinePlacement(LinePlacement value) {
        this.linePlacement = value;
    }

}
