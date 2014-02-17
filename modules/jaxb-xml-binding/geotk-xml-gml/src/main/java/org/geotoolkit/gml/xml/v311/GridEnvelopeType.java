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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlType;
import org.opengis.coverage.grid.GridCoordinates;
import org.opengis.coverage.grid.GridEnvelope;


/**
 * Provides grid coordinate values for the diametrically opposed corners of an envelope that bounds a section of grid.
 * The value of a single coordinate is the number of offsets from the origin of the grid in the direction of a specific axis.
 * 
 * <p>Java class for GridEnvelopeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GridEnvelopeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="low" type="{http://www.opengis.net/gml}integerList"/>
 *         &lt;element name="high" type="{http://www.opengis.net/gml}integerList"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GridEnvelopeType", propOrder = {
    "low",
    "high"
})
public class GridEnvelopeType implements GridEnvelope {

    @XmlList
    @XmlElement(required = true)
    private int[] low;
    @XmlList
    @XmlElement(required = true)
    private int[] high;

    /**
     * Empty constructor used by JAXB
     */
    GridEnvelopeType(){
        
    }

    /**
     * Build a new Grid envelope
     */
    public GridEnvelopeType(final GridEnvelope env){
        if (env != null) {
            if (env.getHigh() != null) {
                this.high = env.getHigh().getCoordinateValues();
            }
            if (env.getLow() != null) {
                this.low  = env.getLow().getCoordinateValues();
            }
        }
    }

    /**
     * Build a new Grid envelope
     */
    public GridEnvelopeType(final int[] low, final int[] high){
        this.high = high;
        this.low  = low;
    }
    
    /**
     * Gets the value of the low property.
     */
    public GridCoordinates getLow() {
        if (low != null) {
            return new GmlGridCoordinates(low);
        }
        return null;
    }

    /**
     * Gets the value of the high property.
     */
    public GridCoordinates getHigh() {
        if (high != null) {
            return new GmlGridCoordinates(high);
        }
        return null;
    }

    public int getDimension() {
        if (low != null) {
            return low.length;
        }
        return 0;
    }


    public int getLow(final int i) throws IndexOutOfBoundsException {
        if (low != null && i < low.length) {
            return low[i];
        }
        return -1;
    }

    public int getHigh(final int i) throws IndexOutOfBoundsException {
        if (high != null && i < high.length) {
            return high[i];
        }
        return -1;
    }

    public int getSpan(final int i) throws IndexOutOfBoundsException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public class GmlGridCoordinates implements GridCoordinates {

        /**
         * The grid coordinates.
         */
        final int[] coordinates;

        /**
         * Creates a grid coordinates initialized to the specified values.
         *
         * @param coordinates The grid coordinates to copy.
         */
        public GmlGridCoordinates(final int[] coordinates) {
            this.coordinates = coordinates.clone();
        }
    
        public int getDimension() {
            if (coordinates != null) {
                return coordinates.length;
            }
            return -1;
        }

        public int[] getCoordinateValues() {
            if (coordinates != null) {
                return coordinates.clone();
            }
            return null;
        }

        public int getCoordinateValue(final int dimension) throws IndexOutOfBoundsException {
            if (coordinates != null) {
                return coordinates[dimension];
            }
            return -1;
        }

        public void setCoordinateValue(final int dimension, final int value) throws IndexOutOfBoundsException, UnsupportedOperationException {
            if (coordinates != null) {
                coordinates[dimension] = value;
            }

        }
    }
}
