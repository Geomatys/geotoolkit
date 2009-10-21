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
import java.util.Collection;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;


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
 *         &lt;element ref="{http://www.opengis.net/ows/1.1}Value"/>
 *         &lt;element ref="{http://www.opengis.net/ows/1.1}Range"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Guilhem Legal
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "valueOrRange"
})
@XmlRootElement(name = "AllowedValues")
public class AllowedValues {

    @XmlElements({
        @XmlElement(name = "Range", type = RangeType.class),
        @XmlElement(name = "Value", type = ValueType.class)
    })
    private List<Object> valueOrRange;

    /**
     *  empty constructor used by JAXB.
     */
    AllowedValues(){
        
    }
    
    /**
     *  Build an allowed value.
     */
    public AllowedValues(List<Object> valueOrRange){
        
        this.valueOrRange = valueOrRange;
        
    }
    
    /**
     *  Build an allowed value with the specified list of value.
     */
    public AllowedValues(Collection<String> values){
        
        this.valueOrRange = new ArrayList<Object>();
        for (String value: values){
            valueOrRange.add(new ValueType(value));
        }
        
    }
    
    
    /**
     *  Build an allowed value with the specified range
     */
    public AllowedValues(RangeType range){
        
        valueOrRange = new ArrayList<Object>();
        valueOrRange.add(range);
    }
    
    
    /**
     * Gets the value of the valueOrRange property.
     */
    public List<Object> getValueOrRange() {
        if (valueOrRange == null) {
            valueOrRange = new ArrayList<Object>();
        }
        return this.valueOrRange;
    }
    
    /**
     * Verify that this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof AllowedValues) {
            final AllowedValues that = (AllowedValues) object;
            return Utilities.equals(this.valueOrRange,   that.valueOrRange);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.valueOrRange != null ? this.valueOrRange.hashCode() : 0);
        return hash;
    }

}
