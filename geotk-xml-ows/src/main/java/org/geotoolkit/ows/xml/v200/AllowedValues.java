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
import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


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
public class AllowedValues implements org.geotoolkit.ows.xml.AllowedValues {

    @XmlElements({
        @XmlElement(name = "Value", type = ValueType.class),
        @XmlElement(name = "Range", type = RangeType.class)
    })
    private List<Object> valueOrRange;

    public AllowedValues() {

    }

    public AllowedValues(final Collection valueOrRange){
        this.valueOrRange = new ArrayList<>();
        for (Object value: valueOrRange){
            if (value instanceof String) {
                this.valueOrRange.add(new ValueType((String)value));
            } else if (value instanceof ValueType || value instanceof RangeType) {
                this.valueOrRange.add(value);
            }
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

    public List<RangeType> getRangeValues() {
        final List<RangeType> values = new ArrayList<>();
        if (valueOrRange != null) {
            for (Object o : valueOrRange) {
                if (o instanceof RangeType) {
                    values.add((RangeType)o);
                }
            }
        }
        return values;
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
            return Objects.equals(this.valueOrRange,   that.valueOrRange);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.valueOrRange != null ? this.valueOrRange.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[AllowedValues]").append("\n");
        if (valueOrRange != null) {
            sb.append("valueOrRange:\n ");
            for (Object obj : valueOrRange) {
                sb.append(obj).append('\n');
            }
        }
        return sb.toString();
    }
}
