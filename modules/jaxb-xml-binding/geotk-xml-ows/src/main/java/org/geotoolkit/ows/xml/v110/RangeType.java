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
package org.geotoolkit.ows.xml.v110;

import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * A range of values of a numeric parameter. This range can be continuous or discrete, defined by a fixed spacing between adjacent valid values. If the MinimumValue or MaximumValue is not included, there is no value limit in that direction. Inclusion of the specified minimum and maximum values in the range shall be defined by the rangeClosure. 
 * 
 * <p>Java class for RangeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RangeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/ows/1.1}MinimumValue" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows/1.1}MaximumValue" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows/1.1}Spacing" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute ref="{http://www.opengis.net/ows/1.1}rangeClosure"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 *  @author Guilhem Legal
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RangeType", propOrder = {
    "minimumValue",
    "maximumValue",
    "spacing"
})
public class RangeType {

    @XmlElement(name = "MinimumValue")
    private ValueType minimumValue;
    @XmlElement(name = "MaximumValue")
    private ValueType maximumValue;
    @XmlElement(name = "Spacing")
    private ValueType spacing;
    @XmlAttribute(namespace = "http://www.opengis.net/ows/1.1")
    private List<String> rangeClosure;

    /**
     * Empty constructor used by JAXB.
     */
    RangeType(){
    }
    
    /**
     * Build a new full Range.
     */
    public RangeType(ValueType minimumValue, ValueType maximumValue, ValueType spacing,
            List<String> rangeClosure){
        this.maximumValue = maximumValue;
        this.minimumValue = minimumValue;
        this.rangeClosure = rangeClosure;
        this.spacing      = spacing;
    }
    
    /**
     * Build a new light Range.
     */
    public RangeType(String minimumValue, String maximumValue){
        this.maximumValue = new ValueType(maximumValue);
        this.minimumValue = new ValueType(minimumValue);
    }
    
    /**
     * Gets the value of the minimumValue property.
     */
    public ValueType getMinimumValue() {
        return minimumValue;
    }

    /**
     * Gets the value of the maximumValue property.
     * 
     */
    public ValueType getMaximumValue() {
        return maximumValue;
    }

    /**
     * Shall be included when the allowed values are NOT continuous in this range.
     * Shall not be included when the allowed values are continuous in this range. 
     * 
     */
    public ValueType getSpacing() {
        return spacing;
    }

    /**
     * Shall be included unless the default value applies. 
     * Gets the value of the rangeClosure property.
     * 
     */
    public List<String> getRangeClosure() {
        return Collections.unmodifiableList(rangeClosure);
    }

}
