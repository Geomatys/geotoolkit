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
package org.geotoolkit.swe.xml.v100;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.swe.xml.Coordinate;
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
 *       &lt;group ref="{http://www.opengis.net/swe/1.0}AnyNumerical" minOccurs="0"/>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}token" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "count",
    "quantity",
    "time"
})
public class CoordinateType implements Coordinate {

    @XmlElement(name = "Count")
    private Count count;
    @XmlElement(name = "Quantity")
    private QuantityType quantity;
    @XmlElement(name = "Time")
    private TimeType time;
    @XmlAttribute(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    private String name;

    public CoordinateType() {

    }

    public CoordinateType(Coordinate c) {
        if (c != null) {
            this.name = c.getName();
            if (c.getCount() != null) {
                this.count = new Count(c.getCount());
            }
            if (c.getQuantity() != null) {
                this.quantity = new QuantityType(c.getQuantity());
            }
            if (c.getTime() != null) {
                this.time = new TimeType(c.getTime());
            }
        }
    }
    
    public CoordinateType(String name, QuantityType quantity) {
        this.name     = name;
        this.quantity = quantity;
    }

    public CoordinateType(String name, Count count) {
        this.name  = name;
        this.count = count;
    }

    public CoordinateType(String name, TimeType time) {
        this.name = name;
        this.time = time;
    }

    public CoordinateType(QuantityType quantity) {
        this.quantity = quantity;
    }

    public CoordinateType(Count count) {
        this.count = count;
    }

    public CoordinateType(TimeType time) {
        this.time = time;
    }

    /**
     * Gets the value of the count property.
     */
    @Override
    public Count getCount() {
        return count;
    }

    /**
     * Sets the value of the count property.
     */
    public void setCount(Count value) {
        this.count = value;
    }

    /**
     * Gets the value of the quantity property.
     */
    @Override
    public QuantityType getQuantity() {
        return quantity;
    }

    /**
     * Sets the value of the quantity property.
     */
    public void setQuantity(QuantityType value) {
        this.quantity = value;
    }

    /**
     * Gets the value of the time property.
     */
    @Override
    public TimeType getTime() {
        return time;
    }

    /**
     * Sets the value of the time property.
     *
     */
    public void setTime(TimeType value) {
        this.time = value;
    }

    /**
     * Gets the value of the name property.
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof CoordinateType) {
            final CoordinateType  that = (CoordinateType) object;
            return Utilities.equals(this.count,    that.count)    &&
                   Utilities.equals(this.name,     that.name)     &&
                   Utilities.equals(this.quantity, that.quantity) &&
                   Utilities.equals(this.time,     that.time);

        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + (this.count != null ? this.count.hashCode() : 0);
        hash = 83 * hash + (this.quantity != null ? this.quantity.hashCode() : 0);
        hash = 83 * hash + (this.time != null ? this.time.hashCode() : 0);
        hash = 83 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[CoordinateType]");
        if (count != null) {
            s.append("count:").append(count).append('\n');
        }
        if (name != null) {
            s.append("name:").append(name).append('\n');
        }
        if (quantity != null) {
            s.append("quantity:").append(quantity).append('\n');
        }
        if (time != null) {
            s.append("time:").append(time).append('\n');
        }
        return s.toString();
    }
}


