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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.Range;
import org.geotoolkit.util.Utilities;


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
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RangeType", propOrder = {
    "minimumValue",
    "maximumValue",
    "spacing"
})
public class RangeType implements Range {

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
    
    public RangeType(final Range that){
        if (that != null)  {
            if (that.getMaximumValue() != null) {
                this.maximumValue = new ValueType(that.getMaximumValue());
            }
            if (that.getMinimumValue() != null) {
                this.minimumValue = new ValueType(that.getMinimumValue());
            }
            if (that.getRangeClosure() != null) {
                this.rangeClosure = new ArrayList<String>(that.getRangeClosure());
            }
            if (that.getSpacing() != null) {
                this.spacing      = new ValueType(that.getSpacing());
            }
        }
    }
    
    /**
     * Build a new full Range.
     */
    public RangeType(final ValueType minimumValue, final ValueType maximumValue, final ValueType spacing,
            final List<String> rangeClosure){
        this.maximumValue = maximumValue;
        this.minimumValue = minimumValue;
        this.rangeClosure = rangeClosure;
        this.spacing      = spacing;
    }
    
    /**
     * Build a new light Range.
     */
    public RangeType(final String minimumValue, final String maximumValue){
        this.maximumValue = new ValueType(maximumValue);
        this.minimumValue = new ValueType(minimumValue);
    }
    
    /**
     * Gets the value of the minimumValue property.
     */
    @Override
    public ValueType getMinimumValue() {
        return minimumValue;
    }

    /**
     * Gets the value of the maximumValue property.
     * 
     */
    @Override
    public ValueType getMaximumValue() {
        return maximumValue;
    }

    /**
     * Shall be included when the allowed values are NOT continuous in this range.
     * Shall not be included when the allowed values are continuous in this range. 
     * 
     */
    @Override
    public ValueType getSpacing() {
        return spacing;
    }

    /**
     * Shall be included unless the default value applies. 
     * Gets the value of the rangeClosure property.
     * 
     */
    @Override
    public List<String> getRangeClosure() {
        if (rangeClosure != null) {
            return Collections.unmodifiableList(rangeClosure);
        }
        return new ArrayList<String>();
    }
    
    /**
     * Verify that this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof RangeType) {
            final RangeType that = (RangeType) object;
            return Utilities.equals(this.maximumValue,   that.maximumValue) &&
                   Utilities.equals(this.minimumValue,   that.minimumValue) &&
                   Utilities.equals(this.rangeClosure,   that.rangeClosure) &&
                   Utilities.equals(this.spacing,        that.spacing);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (this.minimumValue != null ? this.minimumValue.hashCode() : 0);
        hash = 53 * hash + (this.maximumValue != null ? this.maximumValue.hashCode() : 0);
        hash = 53 * hash + (this.spacing != null ? this.spacing.hashCode() : 0);
        hash = 53 * hash + (this.rangeClosure != null ? this.rangeClosure.hashCode() : 0);
        return hash;
    }

    

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[RangeType]").append("\n");
        if (rangeClosure != null) {
            sb.append("rangeClosure:\n ");
            for (Object obj : rangeClosure) {
                sb.append(obj).append('\n');
            }
        }
        if (minimumValue != null) {
            sb.append("minimumValue:").append(minimumValue).append('\n');
        }
        if (maximumValue != null) {
            sb.append("maximumValue:").append(maximumValue).append('\n');
        }
        if (spacing != null) {
            sb.append("spacing:").append(spacing).append('\n');
        }
        return sb.toString();
    }

}
