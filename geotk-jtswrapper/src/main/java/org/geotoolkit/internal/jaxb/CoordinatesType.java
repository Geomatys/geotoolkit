/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.internal.jaxb;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlValue;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CoordinatesType", propOrder = {
    "value"
})
public class CoordinatesType {

    @XmlValue
    private String value;
    @XmlAttribute
    private String decimal;
    @XmlAttribute
    private String cs;
    @XmlAttribute
    private String ts;

    /**
     * An empty constructor used by JAXB
     */
    CoordinatesType() {}

    /**
     * build a new coordinate with the specified values.
     *
     * @param value   A list of coordinates coma space separated.
     * @param cs      Symbol used to separate components within a tuple or coordinate string (default="," a comma)
     * @param decimal Symbol used for a decimal point (default="." a stop or period)
     * @param ts      symbol used to separate tuples or coordinate strings (default=" " a space)
     */
    public CoordinatesType(final String value, final String cs, final String decimal, final String ts) {
        this.value   = value;
        this.cs      = cs;
        this.ts      = ts;
        this.decimal = decimal;
    }

    /**
     * build a new coordinate with the specified values.
     *
     * @param value a list of coordinates coma space separated.
     */
    public CoordinatesType(final String value) {
        this.value = value;
    }

    /**
     * Gets the value of the value property.
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     */
    public void setValue(final String value) {
        this.value = value;
    }

    public List<Double> getValues() {
        final String curentTs;
        if (ts == null) {
            curentTs = " ";
        } else {
            curentTs = ts;
        }

        final String curentCs;
        if (cs == null) {
            curentCs = ",";
        } else {
            curentCs = cs;
        }

        final List<Double> values = new ArrayList<Double>();
        if (value != null) {
            final StringTokenizer tokenizer = new StringTokenizer(value, curentTs);
            while (tokenizer.hasMoreTokens()) {
                final String v = tokenizer.nextToken();
                try {
                    int i = v.indexOf(curentCs);
                    if (i != -1) {
                        String first = v.substring(0, i);
                        String second = v.substring(i + 1);
                        values.add(Double.parseDouble(first));
                        values.add(Double.parseDouble(second));
                    }
                } catch (NumberFormatException ex) {
                    Logger.getLogger("org.geotoolkit.internal.jaxb").log(Level.WARNING, "unable to parse coordiante value:{0}", v);
                }
            }
        }
        return values;
    }

    /**
     * Gets the value of the decimal property.
     */
    public String getDecimal() {
        /*if (decimal == null) {
            return ".";
        } else {*/
        return decimal;
    }

    /**
     * Sets the value of the decimal property.
     */
    public void setDecimal(final String value) {
        this.decimal = value;
    }

    /**
     * Gets the value of the cs property.
     */
    public String getCs() {
        /*if (cs == null) {
            return ",";
        } else {*/
        return cs;
    }

    /**
     * Sets the value of the cs property.
     */
    public void setCs(final String value) {
        this.cs = value;
    }

    /**
     * Gets the value of the ts property.
     */
    public String getTs() {
        /*if (ts == null) {
            return " ";
        } else {*/
        return ts;
    }

    /**
     * Sets the value of the ts property.
     */
    public void setTs(final String value) {
        this.ts = value;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + (this.value != null ? this.value.hashCode() : 0);
        hash = 47 * hash + (this.decimal != null ? this.decimal.hashCode() : 0);
        hash = 47 * hash + (this.cs != null ? this.cs.hashCode() : 0);
        hash = 47 * hash + (this.ts != null ? this.ts.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof CoordinatesType) {
            final CoordinatesType that = (CoordinatesType) object;


            return Objects.equals(this.cs,      that.cs) &&
                   Objects.equals(this.ts,      that.ts) &&
                   Objects.equals(this.value,   that.value) &&
                   Objects.equals(this.decimal, that.decimal);
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("CoordinatesType[").append('\n');
        if (value != null) {
            s.append("value : ").append(value.toString()).append('\n');
        }
        if (decimal != null) {
            s.append("decimal : ").append(decimal.toString()).append('\n');
        }
        if (cs != null) {
            s.append("cs : ").append(cs.toString()).append('\n');
        }
        if (ts != null) {
            s.append("ts : ").append(ts.toString()).append('\n');
        }
        s.append("]");
        return s.toString();
    }
}
