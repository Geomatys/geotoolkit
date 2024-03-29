/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2012, Geomatys
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


package org.geotoolkit.gml.xml.v321;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GridLimitsType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="GridLimitsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="GridEnvelope" type="{http://www.opengis.net/gml/3.2}GridEnvelopeType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GridLimitsType", propOrder = {
    "gridEnvelope"
})
public class GridLimitsType {

    @XmlElement(name = "GridEnvelope", required = true)
    private GridEnvelopeType gridEnvelope;

    public GridLimitsType() {

    }

    public GridLimitsType(GridEnvelopeType gridEnvelope) {
        this.gridEnvelope = gridEnvelope;
    }

    /**
     * Gets the value of the gridEnvelope property.
     *
     * @return
     *     possible object is
     *     {@link GridEnvelopeType }
     *
     */
    public GridEnvelopeType getGridEnvelope() {
        return gridEnvelope;
    }

    /**
     * Sets the value of the gridEnvelope property.
     *
     * @param value
     *     allowed object is
     *     {@link GridEnvelopeType }
     *
     */
    public void setGridEnvelope(GridEnvelopeType value) {
        this.gridEnvelope = value;
    }

}
