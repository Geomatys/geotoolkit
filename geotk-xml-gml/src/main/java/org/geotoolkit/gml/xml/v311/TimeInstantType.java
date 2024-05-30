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

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.time.temporal.Temporal;
import org.apache.sis.util.ComparisonMode;
import org.geotoolkit.gml.xml.GMLInstant;
import org.geotoolkit.gml.xml.AbstractTimePosition;
import org.opengis.filter.Literal;


/**
 * Omit back-pointers begunBy, endedBy.
 *
 * <p>Java class for TimeInstantType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="TimeInstantType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml}AbstractTimeGeometricPrimitiveType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/gml}timePosition"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
    "timePosition"
})
@XmlRootElement(name = "TimeInstant")
public class TimeInstantType extends AbstractTimeGeometricPrimitiveType implements GMLInstant, Serializable, Literal {

    @XmlElement(required = true)
    private TimePositionType timePosition;

    /**
     * Empty constructor used by JAXB.
     */
    public TimeInstantType(){
    }

    public TimeInstantType(final AbstractTimePosition timePosition) {
        this(null, timePosition);
    }
    /**
     * Build a new time instant with the specified timeposition.
     */
    public TimeInstantType(final String id, final AbstractTimePosition timePosition) {
        super(id);
        if (timePosition instanceof TimePositionType) {
            this.timePosition = (TimePositionType) timePosition;
        } else if (timePosition != null && timePosition.getDate() != null) {
            this.timePosition = new TimePositionType(timePosition);
        }
    }

    public TimeInstantType(final Temporal timePosition) {
        this.timePosition = new TimePositionType(timePosition);
    }

    public TimeInstantType(final String id, final Temporal timePosition) {
        super(id);
        this.timePosition = new TimePositionType(timePosition);
    }

    public TimeInstantType(final String timePosition) {
        this.timePosition = new TimePositionType(timePosition);
    }

    public TimeInstantType(final String id, final String timePosition) {
        super(id);
        this.timePosition = new TimePositionType(timePosition);
    }

    /**
     * Gets the value of the timePosition property.
     *
     * @return
     *     possible object is
     *     {@link TimePositionType }
     *
     */
    @Override
    public TimePositionType getTimePosition() {
        return timePosition;
    }

    /**
     * Sets the value of the timePosition property.
     *
     * @param value
     *     allowed object is
     *     {@link TimePositionType }
     *
     */
    public void setTimePosition(final TimePositionType value) {
        this.timePosition = value;
    }

    public void setDate(final Date value) {
//        timePosition = value;
//        if (value instanceof TimePositionType) {
//            this.timePosition = (TimePositionType)value;
        /*} else*/ if (value != null) {
            this.timePosition = new TimePositionType(value.toInstant());
        } else {
            this.timePosition = null;
        }
    }

//    @Override
//    public Collection<Period> getBegunBy() {
//        return null;
//    }
//
//    @Override
//    public Collection<Period> getEndedBy() {
//        return null;
//    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[TimeInstantType]").append('\n');
        if (timePosition != null) {
            sb.append("timePosition:").append(timePosition).append('\n');
        }
        return sb.toString();
    }

    @Override
    public Object getValue() {
        if (timePosition != null) {
            return timePosition;
        }
        return null;
    }

    /**
     * Verify that this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode cm) {
        if (object == this) {
            return true;
        }
        if (object instanceof TimeInstantType that) {
            return  Objects.equals(this.timePosition, that.timePosition);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + (this.timePosition != null ? this.timePosition.hashCode() : 0);
        return hash;
    }
}
