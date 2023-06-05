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
package org.geotoolkit.gml.xml.v311;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import org.opengis.coverage.grid.GridEnvelope;


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
 *         &lt;element name="GridEnvelope" type="{http://www.opengis.net/gml}GridEnvelopeType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GridLimitsType", propOrder = {
    "gridEnvelope"
})
public class GridLimitsType {

    @XmlElement(name = "GridEnvelope", required = true)
    private GridEnvelopeType gridEnvelope;

    /**
     * An empty constructor used by JAXB.
     */
    GridLimitsType(){
    }

    /**
     * Build a new Grid limits
     */
    public GridLimitsType(final GridEnvelope gridEnvelope){
        if (gridEnvelope instanceof GridEnvelopeType) {
            this.gridEnvelope = (GridEnvelopeType) gridEnvelope;
        } else {
            this.gridEnvelope = new GridEnvelopeType(gridEnvelope);
        }
    }

    /**
     * Build a new Grid limits
     */
    public GridLimitsType(final long[] low, final long[] high){
        this.gridEnvelope = new GridEnvelopeType(low, high);
    }

    /**
     * Gets the value of the gridEnvelope property.
     */
    public GridEnvelopeType getGridEnvelope() {
        return gridEnvelope;
    }
}
