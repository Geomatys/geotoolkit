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
package org.geotoolkit.ogc.xml.v100;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlValue;


/**
 * <p>Java class for DistanceType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="DistanceType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="units" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DistanceType", propOrder = {
    "content"
})
public class DistanceType {

    @XmlValue
    private String content;
    @XmlAttribute(required = true)
    private String units;

    public DistanceType() {
    }

    public DistanceType(final String content, final String units) {
        this.content = content;
        this.units   = units;
    }

    /**
     * Gets the value of the content property.
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the value of the content property.
     */
    public void setContent(final String value) {
        this.content = value;
    }

    /**
     * Gets the value of the units property.
     */
    public String getUnits() {
        return units;
    }

    /**
     * Sets the value of the units property.
     */
    public void setUnits(final String value) {
        this.units = value;
    }
}
