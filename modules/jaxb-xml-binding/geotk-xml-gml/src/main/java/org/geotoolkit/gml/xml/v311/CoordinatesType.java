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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import org.geotoolkit.util.Utilities;


/**
 * Tables or arrays of tuples.  
 * May be used for text-encoding of values from a table.  
 * Actually just a string, but allows the user to indicate which characters are used as separators.  
 * The value of the 'cs' attribute is the separator for coordinate values, 
 * and the value of the 'ts' attribute gives the tuple separator (a single space by default); 
 * the default values may be changed to reflect local usage.
 * Defaults to CSV within a tuple, space between tuples.  
 * However, any string content will be schema-valid.  
 * 
 * <p>Java class for CoordinatesType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CoordinatesType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *       &lt;attribute name="decimal" type="{http://www.w3.org/2001/XMLSchema}string" default="." />
 *       &lt;attribute name="cs" type="{http://www.w3.org/2001/XMLSchema}string" default="," />
 *       &lt;attribute name="ts" type="{http://www.w3.org/2001/XMLSchema}string" default=" " />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
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
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValue(final String value) {
        this.value = value;
    }

    /**
     * Gets the value of the decimal property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDecimal() {
        /*if (decimal == null) {
            return ".";
        } else {*/
        return decimal;
    }

    /**
     * Sets the value of the decimal property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDecimal(final String value) {
        this.decimal = value;
    }

    /**
     * Gets the value of the cs property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCs() {
        /*if (cs == null) {
            return ",";
        } else {*/
        return cs;
    }

    /**
     * Sets the value of the cs property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCs(final String value) {
        this.cs = value;
    }

    /**
     * Gets the value of the ts property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTs() {
        /*if (ts == null) {
            return " ";
        } else {*/
        return ts;
    }

    /**
     * Sets the value of the ts property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
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


            return Utilities.equals(this.cs,      that.cs) &&
                   Utilities.equals(this.ts,      that.ts) &&
                   Utilities.equals(this.value,   that.value) &&
                   Utilities.equals(this.decimal, that.decimal);
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
