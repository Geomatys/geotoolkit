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
package org.geotoolkit.swe.xml.v101;

import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.swe.xml.Coordinate;

/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;group ref="{http://www.opengis.net/swe/1.0.1}AnyNumerical" minOccurs="0"/>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}token" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 * @author Guilhem Legal (Geomatys)
 * @module
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

    public CoordinateType(final Coordinate c) {
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

    public CoordinateType(final String name, final QuantityType quantity) {
        this.name     = name;
        this.quantity = quantity;
    }

    public CoordinateType(final String name, final Count count) {
        this.name  = name;
        this.count = count;
    }

    public CoordinateType(final String name, final TimeType time) {
        this.name = name;
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
    public void setCount(final Count value) {
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
    public void setQuantity(final QuantityType value) {
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
    public void setTime(final TimeType value) {
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
     */
    public void setName(final String value) {
        this.name = value;
    }

    /**
     * Verify that the object is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof CoordinateType) {
            final CoordinateType that = (CoordinateType) object;
            return Objects.equals(this.count, that.count) &&
                   Objects.equals(this.name, that.name) &&
                   Objects.equals(this.quantity, that.quantity) &&
                   Objects.equals(this.time, that.time);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + (this.count != null ? this.count.hashCode() : 0);
        hash = 47 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 47 * hash + (this.quantity != null ? this.quantity.hashCode() : 0);
        hash = 47 * hash + (this.time != null ? this.time.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[CoordinateType]");
        if (count != null) {
            sb.append("count:").append(count).append('\n');
        }
        if (name != null) {
            sb.append("name:").append(name).append('\n');
        }
        if (quantity != null) {
            sb.append("quantity:").append(quantity).append('\n');
        }
        if (time != null) {
            sb.append("time:").append(time).append('\n');
        }
        return sb.toString();
    }
}
