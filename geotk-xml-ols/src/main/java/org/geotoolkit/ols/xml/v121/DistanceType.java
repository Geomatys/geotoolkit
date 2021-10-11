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
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DistanceType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="DistanceType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/xls}AbstractMeasureType">
 *       &lt;attribute name="uom" type="{http://www.opengis.net/xls}DistanceUnitType" default="M" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DistanceType")
@XmlSeeAlso({
    AltitudeType.class
})
public class DistanceType extends AbstractMeasureType {

    @XmlAttribute
    private DistanceUnitType uom;

    /**
     * Gets the value of the uom property.
     *
     * @return
     *     possible object is
     *     {@link DistanceUnitType }
     *
     */
    public DistanceUnitType getUom() {
        if (uom == null) {
            return DistanceUnitType.M;
        } else {
            return uom;
        }
    }

    /**
     * Sets the value of the uom property.
     *
     * @param value
     *     allowed object is
     *     {@link DistanceUnitType }
     *
     */
    public void setUom(DistanceUnitType value) {
        this.uom = value;
    }

}
