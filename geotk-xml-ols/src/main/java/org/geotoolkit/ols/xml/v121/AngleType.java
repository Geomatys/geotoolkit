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
import javax.xml.bind.annotation.XmlType;


/**
 * This type is used as a unit of measure for ADTs only, it's not used by the GML3 geometry. This will be a point for future work of harmonization.
 *
 * <p>Java class for AngleType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="AngleType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/xls}AbstractMeasureType">
 *       &lt;attribute name="uom" type="{http://www.w3.org/2001/XMLSchema}string" fixed="DecimalDegrees" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AngleType")
public class AngleType extends AbstractMeasureType {

    @XmlAttribute
    private String uom;

    /**
     * Gets the value of the uom property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getUom() {
        if (uom == null) {
            return "DecimalDegrees";
        } else {
            return uom;
        }
    }

    /**
     * Sets the value of the uom property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setUom(String value) {
        this.uom = value;
    }

}
