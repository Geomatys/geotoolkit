/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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

package org.geotoolkit.wcs.xml.v200;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DimensionSliceType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="DimensionSliceType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/wcs/2.0}DimensionSubsetType">
 *       &lt;sequence>
 *         &lt;element name="SlicePoint" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DimensionSliceType", propOrder = {
    "slicePoint"
})
public class DimensionSliceType extends DimensionSubsetType {

    @XmlElement(name = "SlicePoint", required = true)
    private String slicePoint;

    /**
     * Gets the value of the slicePoint property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSlicePoint() {
        return slicePoint;
    }

    /**
     * Sets the value of the slicePoint property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSlicePoint(String value) {
        this.slicePoint = value;
    }

}
