/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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

package org.geotoolkit.ows.xml.v200;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded">
 *         &lt;element ref="{http://www.opengis.net/ows/2.0}Value"/>
 *         &lt;element ref="{http://www.opengis.net/ows/2.0}Range"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "valueOrRange"
})
@XmlRootElement(name = "AllowedValues")
public class AllowedValues {

    @XmlElements({
        @XmlElement(name = "Value", type = ValueType.class),
        @XmlElement(name = "Range", type = RangeType.class)
    })
    private List<Object> valueOrRange;

    public AllowedValues() {
        
    }
    /**
     *  Build an allowed value with the specified list of value.
     */
    public AllowedValues(final Collection<String> values){
        
        this.valueOrRange = new ArrayList<>();
        for (String value: values){
            valueOrRange.add(new ValueType(value));
        }
    }
    
    public AllowedValues(final AllowedValues that){
        if (that != null && that.valueOrRange != null) {
            this.valueOrRange = new ArrayList<>();
            for (Object obj : that.valueOrRange) {
                if (obj instanceof RangeType) {
                    this.valueOrRange.add(new RangeType((RangeType)obj));
                } else if (obj instanceof ValueType) {
                    this.valueOrRange.add(new ValueType((ValueType)obj));
                } else {
                    // should not happen
                    throw new IllegalArgumentException("only accept value or range object");
                }
            }
        }
    }
    
    /**
     *  Build an allowed value with the specified range
     */
    public AllowedValues(final RangeType range){
        
        valueOrRange = new ArrayList<>();
        valueOrRange.add(range);
    }
    
    /**
     * Gets the value of the valueOrRange property.
     * 
     */
    public List<Object> getValueOrRange() {
        if (valueOrRange == null) {
            valueOrRange = new ArrayList<>();
        }
        return this.valueOrRange;
    }

    public List<String> getStringValues() {
        final List<String> values = new ArrayList<>();
        if (valueOrRange != null) {
            for (Object o : valueOrRange) {
                if (o instanceof ValueType) {
                    values.add(((ValueType)o).getValue());
                }
            }
        }
        return values;
    }
}
