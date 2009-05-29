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
package org.geotoolkit.gml.xml.v311modified;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlType;


/**
 * Provides grid coordinate values for the diametrically opposed corners of an envelope that bounds a section of grid. The value of a single coordinate is the number of offsets from the origin of the grid in the direction of a specific axis.
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
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GridEnvelopeType", propOrder = {
    "low",
    "high"
})
public class GridEnvelopeType {

    @XmlList
    @XmlElement(required = true)
    private List<BigInteger> low;
    @XmlList
    @XmlElement(required = true)
    private List<BigInteger> high;

    /**
     * Empty constructor used by JAXB
     */
    GridEnvelopeType(){
        
    }
    
    /**
     * Build a new Grid envelope
     */
    public GridEnvelopeType(List<BigInteger> low, List<BigInteger> high){
        this.high = high;
        this.low  = low;
    }
    
    /**
     * Gets the value of the low property.
     */
    public List<BigInteger> getLow() {
        if (low == null){
            low = new ArrayList<BigInteger>();
        }
        return Collections.unmodifiableList(low);
    }

    /**
     * Gets the value of the high property.
     */
    public List<BigInteger> getHigh() {
        if (high == null){
            high = new ArrayList<BigInteger>();
        }
        return Collections.unmodifiableList(high);
    }

}
