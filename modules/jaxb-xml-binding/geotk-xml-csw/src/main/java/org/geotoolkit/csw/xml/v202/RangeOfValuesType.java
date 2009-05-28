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
package org.geotoolkit.csw.xml.v202;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;


/**
 * <p>Java class for RangeOfValuesType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RangeOfValuesType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="MinValue" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *         &lt;element name="MaxValue" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RangeOfValuesType", propOrder = {
    "minValue",
    "maxValue"
})
public class RangeOfValuesType {

    @XmlElement(name = "MinValue", required = true)
    private Object minValue;
    @XmlElement(name = "MaxValue", required = true)
    private Object maxValue;

    /**
     * Gets the value of the minValue property.
     */
    public Object getMinValue() {
        return minValue;
    }

    /**
     * Gets the value of the maxValue property.
     */
    public Object getMaxValue() {
        return maxValue;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[RangeOfValuesType]").append('\n');
        if (minValue != null) {
            sb.append("minValue:").append(minValue).append('\n');
        }
        if (maxValue != null) {
            sb.append("maxValue:").append(maxValue).append('\n');
        }
        return sb.toString();
    }

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof RangeOfValuesType) {
            final RangeOfValuesType that = (RangeOfValuesType) object;

            return  Utilities.equals(this.minValue, that.minValue) &&
                    Utilities.equals(this.maxValue, that.maxValue);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (this.minValue != null ? this.minValue.hashCode() : 0);
        hash = 53 * hash + (this.maxValue != null ? this.maxValue.hashCode() : 0);
        return hash;
    }
    
}
