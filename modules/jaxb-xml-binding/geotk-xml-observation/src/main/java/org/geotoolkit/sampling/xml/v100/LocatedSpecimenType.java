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

package org.geotoolkit.sampling.xml.v100;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v311.GeometryPropertyType;


/**
 * <p>Java class for LocatedSpecimenType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LocatedSpecimenType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/sampling/1.0}SpecimenType">
 *       &lt;sequence>
 *         &lt;element name="samplingLocation" type="{http://www.opengis.net/gml}GeometryPropertyType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LocatedSpecimenType", propOrder = {
    "samplingLocation"
})
public class LocatedSpecimenType extends SpecimenType {

    @XmlElement(required = true)
    private GeometryPropertyType samplingLocation;

    /**
     * Gets the value of the samplingLocation property.
     * 
     * @return
     *     possible object is
     *     {@link GeometryPropertyType }
     *     
     */
    public GeometryPropertyType getSamplingLocation() {
        return samplingLocation;
    }

    /**
     * Sets the value of the samplingLocation property.
     * 
     * @param value
     *     allowed object is
     *     {@link GeometryPropertyType }
     *     
     */
    public void setSamplingLocation(final GeometryPropertyType value) {
        this.samplingLocation = value;
    }

}
