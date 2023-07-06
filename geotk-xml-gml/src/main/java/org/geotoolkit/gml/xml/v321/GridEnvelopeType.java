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

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlList;
import jakarta.xml.bind.annotation.XmlType;
import org.opengis.coverage.grid.GridEnvelope;


/**
 * <p>Java class for GridEnvelopeType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="GridEnvelopeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="low" type="{http://www.opengis.net/gml/3.2}integerList"/>
 *         &lt;element name="high" type="{http://www.opengis.net/gml/3.2}integerList"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GridEnvelopeType", propOrder = {
    "low",
    "high"
})
public class GridEnvelopeType {

    @XmlList
    @XmlElement(required = true)
    private List<Long> low;
    @XmlList
    @XmlElement(required = true)
    private List<Long> high;

    public GridEnvelopeType() {

    }

    public GridEnvelopeType(final GridEnvelope gridEnv) {
        if (gridEnv != null) {
            final int dimension = gridEnv.getDimension();
            this.high           = new ArrayList<>();
            this.low            = new ArrayList<>();
            for (int i = 0; i < dimension; i++) {
                this.high.add(gridEnv.getHigh(i));
                this.low.add(gridEnv.getLow(i));
            }
        }
    }
    /**
     * Gets the value of the low property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link Integer }
     *
     */
    public List<Long> getLow() {
        if (low == null) {
            low = new ArrayList<>();
        }
        return this.low;
    }

    /**
     * Gets the value of the high property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link Integer }
     *
     */
    public List<Long> getHigh() {
        if (high == null) {
            high = new ArrayList<>();
        }
        return this.high;
    }

}
