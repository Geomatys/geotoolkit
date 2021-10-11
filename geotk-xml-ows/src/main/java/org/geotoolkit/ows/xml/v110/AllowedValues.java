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
import java.util.Objects;
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
 *         &lt;element ref="{http://www.opengis.net/ows/1.1}Value"/>
 *         &lt;element ref="{http://www.opengis.net/ows/1.1}Range"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 * @author Guilhem Legal
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "valueOrRange"
})
@XmlRootElement(name = "AllowedValues")
public class AllowedValues implements org.geotoolkit.ows.xml.AllowedValues {

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
     *  Build an allowed value.
     */
    public AllowedValues(final List<Object> valueOrRange){
        this.valueOrRange = valueOrRange;
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

    /**
     *  Build an allowed value with the specified range
     */
    public AllowedValues(final RangeType range){

        valueOrRange = new ArrayList<>();
        valueOrRange.add(range);
    }

    /**
     * Gets the value of the valueOrRange property.
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
